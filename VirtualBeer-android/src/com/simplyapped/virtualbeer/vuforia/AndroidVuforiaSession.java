/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.simplyapped.virtualbeer.vuforia;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.PIXEL_FORMAT;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Vuforia.UpdateCallbackInterface;
import com.simplyapped.libgdx.ext.vuforia.VuforiaException;
import com.simplyapped.libgdx.ext.vuforia.VuforiaSession;


public class AndroidVuforiaSession implements VuforiaSession, UpdateCallbackInterface
{
    
    private static final String LOGTAG = "Vuforia_Sample_Applications";
    
    // Reference to the current activity
    private Activity m_activity;
    
    // Flags
    private boolean m_started = false;

    
    // The async tasks to initialize the Vuforia SDK:
    private InitVuforiaTask mInitVuforiaTask;
    private LoadTrackerTask mLoadTrackerTask;
    
    // An object used for synchronizing Vuforia initialization, dataset loading
    // and the Android onDestroy() life cycle event. If the application is
    // destroyed while a data set is still being loaded, then we wait for the
    // loading operation to finish before shutting down Vuforia:
    private Object mShutdownLock = new Object();
    
    // Vuforia initialization flags:
    private int mVuforiaFlags = 0;
    
    // Holds the camera configuration to use upon resuming
    private int mCamera = CameraDevice.CAMERA.CAMERA_DEFAULT;
    
    // Stores the projection matrix to use for rendering purposes
    private Matrix44F mProjectionMatrix;
    
    // Stores orientation
    private boolean mIsPortrait = false;

	private DataSet dataSetUserDef;

    public AndroidVuforiaSession(Activity activity)
    {
        this.m_activity = activity;
    }
    
    
    // Initializes Vuforia and sets up preferences.
    public void initAsync()
    {
        VuforiaException vuforiaException = null;

        // As long as this window is visible to the user, keep the device's
        // screen turned on and bright:
        m_activity.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mVuforiaFlags = Vuforia.GL_20;
        
        // Initialize Vuforia SDK asynchronously to avoid blocking the
        // main (UI) thread.
        //
        // NOTE: This task instance must be created and invoked on the
        // UI thread and it can be executed only once!
        if (mInitVuforiaTask != null)
        {
            String logMessage = "Cannot initialize SDK twice";
            vuforiaException = new VuforiaException(
                VuforiaException.VUFORIA_ALREADY_INITIALIZATED,
                logMessage);
            Log.e(LOGTAG, logMessage);
        }
        
        if (vuforiaException == null)
        {
            try
            {
                mInitVuforiaTask = new InitVuforiaTask();
                mInitVuforiaTask.execute();
            } catch (Exception e)
            {
                String logMessage = "Initializing Vuforia SDK failed";
                vuforiaException = new VuforiaException(
                    VuforiaException.INITIALIZATION_FAILURE,
                    logMessage);
                Log.e(LOGTAG, logMessage);
            }
        }
    }
    
    
    // Starts Vuforia, initialize and starts the camera and start the trackers
    public void startAR(int camera) throws VuforiaException
    {
        String error;
        mCamera = camera;
        if (!CameraDevice.getInstance().init(camera))
        {
            error = "Unable to open camera device: " + camera;
            Log.e(LOGTAG, error);
            throw new VuforiaException(
                VuforiaException.CAMERA_INITIALIZATION_FAILURE, error);
        }
        
        configureVideoBackground();
        
        if (!CameraDevice.getInstance().selectVideoMode(
            CameraDevice.MODE.MODE_DEFAULT))
        {
            error = "Unable to set video mode";
            Log.e(LOGTAG, error);
            throw new VuforiaException(
                VuforiaException.CAMERA_INITIALIZATION_FAILURE, error);
        }
        
        if (!CameraDevice.getInstance().start())
        {
            error = "Unable to start camera device: " + camera;
            Log.e(LOGTAG, error);
            throw new VuforiaException(
                VuforiaException.CAMERA_INITIALIZATION_FAILURE, error);
        }
        
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        
        setProjectionMatrix();
        
        // start trackers
        Tracker imageTracker = TrackerManager.getInstance().getTracker(ImageTracker.getClassType());
        if (imageTracker != null)
        {
    		imageTracker.start();
        }
            
        
        try
        {
            setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
        } catch (VuforiaException exceptionTriggerAuto)
        {
            setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
        }
    }
    
    
    // Stops any ongoing initialization, stops Vuforia
    public void stopAR() throws VuforiaException
    {
        // Cancel potentially running tasks
        if (mInitVuforiaTask != null
            && mInitVuforiaTask.getStatus() != InitVuforiaTask.Status.FINISHED)
        {
            mInitVuforiaTask.cancel(true);
            mInitVuforiaTask = null;
        }
        
        if (mLoadTrackerTask != null
            && mLoadTrackerTask.getStatus() != LoadTrackerTask.Status.FINISHED)
        {
            mLoadTrackerTask.cancel(true);
            mLoadTrackerTask = null;
        }
        
        mInitVuforiaTask = null;
        mLoadTrackerTask = null;
        
        m_started = false;
        
        stopCamera();
        
        // Ensure that all asynchronous operations to initialize Vuforia
        // and loading the tracker datasets do not overlap:
        synchronized (mShutdownLock)
        {
            
            boolean unloadTrackersResult;
            boolean deinitTrackersResult;
            
            // Destroy the tracking data set:
            unloadTrackersResult = doUnloadTrackersData();
            
            // Deinitialize the trackers:
            deinitTrackersResult = doDeinitTrackers();
            
            // Deinitialize Vuforia SDK:
            Vuforia.deinit();
            
            if (!unloadTrackersResult)
                throw new VuforiaException(
                    VuforiaException.UNLOADING_TRACKERS_FAILURE,
                    "Failed to unload trackers\' data");
            
            if (!deinitTrackersResult)
                throw new VuforiaException(
                    VuforiaException.TRACKERS_DEINITIALIZATION_FAILURE,
                    "Failed to deinitialize trackers");
            
        }
    }
    private boolean doUnloadTrackersData()
    {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;
        
        // Get the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        if (imageTracker == null)
        {
            result = false;
            Log.d(
                LOGTAG,
                "Failed to destroy the tracking data set because the ImageTracker has not been initialized.");
        }
        
        if (dataSetUserDef != null)
        {
            if (imageTracker.getActiveDataSet() != null
                && !imageTracker.deactivateDataSet(dataSetUserDef))
            {
                Log.d(
                    LOGTAG,
                    "Failed to destroy the tracking data set because the data set could not be deactivated.");
                result = false;
            }
            
            if (!imageTracker.destroyDataSet(dataSetUserDef))
            {
                Log.d(LOGTAG, "Failed to destroy the tracking data set.");
                result = false;
            }
            
            Log.d(LOGTAG, "Successfully destroyed the data set.");
            dataSetUserDef = null;
        }
        
        return result;
    }
    public boolean doInitTrackers()
    {
        // Indicate if the trackers were initialized correctly
        boolean result = true;
        
        // Initialize the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker tracker = trackerManager.initTracker(ImageTracker
            .getClassType());
        if (tracker == null)
        {
            Log.d(LOGTAG, "Failed to initialize ImageTracker.");
            result = false;
        } else
        {
            Log.d(LOGTAG, "Successfully initialized ImageTracker.");
        }
        
        return result;
    }
    public boolean doDeinitTrackers()
    {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ImageTracker.getClassType());
        
        return result;
    }
    
    public boolean doStopTrackers()
    {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        Tracker imageTracker = TrackerManager.getInstance().getTracker(
            ImageTracker.getClassType());
        if (imageTracker != null)
            imageTracker.stop();
        
        return result;
    }
    
    // Resumes Vuforia, restarts the trackers and the camera
    public void resumeAR() throws VuforiaException
    {
        // Vuforia-specific resume operation
        Vuforia.onResume();
        
        if (m_started)
            startAR(mCamera);
    }
    
    
    // Pauses Vuforia and stops the camera
    public void pauseAR() throws VuforiaException
    {
        if (m_started)
            stopCamera();
        
        Vuforia.onPause();
    }
    
    
    // Gets the projection matrix to be used for rendering
    public Matrix44F getProjectionMatrix()
    {
        return mProjectionMatrix;
    }
    
    
    // Callback called every cycle
    @Override
    public void QCAR_onUpdate(State s)
    {
        
    }
    
    
    // Manages the configuration changes
    public void onConfigurationChanged()
    {
        updateActivityOrientation();
        
        if (isARRunning())
        {
            // configure video background
            configureVideoBackground();
            
            // Update projection matrix:
            setProjectionMatrix();
        }
        
    }
    
    
    // Methods to be called to handle lifecycle
    public void onResume()
    {
        // Vuforia-specific resume operation
        Vuforia.onResume();
        
        if (m_started)
			try {
				startAR(mCamera);
			} catch (VuforiaException e) {
				e.printStackTrace();
			}
    }
    
    
    public void onPause()
    {
        if (m_started)
            stopCamera();
        
        Vuforia.onPause();
    }
    
    
    public void onSurfaceChanged(int width, int height)
    {
        Vuforia.onSurfaceChanged(width, height);
    }
    
    
    public void onSurfaceCreated()
    {
        Vuforia.onSurfaceCreated();
    }
    
    private void onInitARDone(VuforiaException vuforiaException) {
		try
        {
            startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
        } catch (VuforiaException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        boolean result = CameraDevice.getInstance().setFocusMode(
            CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
	}
    
    // An async task to initialize Vuforia asynchronously.
    private class InitVuforiaTask extends AsyncTask<Void, Integer, Boolean>
    {
        // Initialize with invalid value:
        private int mProgressValue = -1;
        
        
        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap with initialization:
            synchronized (mShutdownLock)
            {
                Vuforia.setInitParameters(m_activity, mVuforiaFlags);
                
                do
                {
                    // Vuforia.init() blocks until an initialization step is
                    // complete, then it proceeds to the next step and reports
                    // progress in percents (0 ... 100%).
                    // If Vuforia.init() returns -1, it indicates an error.
                    // Initialization is done when progress has reached 100%.
                    mProgressValue = Vuforia.init();
                    
                    // Publish the progress value:
                    publishProgress(mProgressValue);
                    
                    // We check whether the task has been canceled in the
                    // meantime (by calling AsyncTask.cancel(true)).
                    // and bail out if it has, thus stopping this thread.
                    // This is necessary as the AsyncTask will run to completion
                    // regardless of the status of the component that
                    // started is.
                } while (!isCancelled() && mProgressValue >= 0
                    && mProgressValue < 100);
                
                return (mProgressValue > 0);
            }
        }
        
        
        protected void onProgressUpdate(Integer... values)
        {
            // Do something with the progress value "values[0]", e.g. update
            // splash screen, progress bar, etc.
        }
        
        
        protected void onPostExecute(Boolean result)
        {
            // Done initializing Vuforia, proceed to next application
            // initialization status:
            
            VuforiaException vuforiaException = null;
            
            if (result)
            {
                Log.d(LOGTAG, "InitVuforiaTask.onPostExecute: Vuforia "
                    + "initialization successful");
                
                boolean initTrackersResult;
                initTrackersResult = doInitTrackers();
                
                if (initTrackersResult)
                {
                    try
                    {
                        mLoadTrackerTask = new LoadTrackerTask();
                        mLoadTrackerTask.execute();
                    } catch (Exception e)
                    {
                        String logMessage = "Loading tracking data set failed";
                        vuforiaException = new VuforiaException(
                            VuforiaException.LOADING_TRACKERS_FAILURE,
                            logMessage);
                        Log.e(LOGTAG, logMessage);
                        onInitARDone(vuforiaException);
                    }
                    
                } else
                {
                    vuforiaException = new VuforiaException(
                        VuforiaException.TRACKERS_INITIALIZATION_FAILURE,
                        "Failed to initialize trackers");
                    onInitARDone(vuforiaException);
                }
            } else
            {
                String logMessage;
                
                // NOTE: Check if initialization failed because the device is
                // not supported. At this point the user should be informed
                // with a message.
                if (mProgressValue == Vuforia.INIT_DEVICE_NOT_SUPPORTED)
                {
                    logMessage = "Failed to initialize Vuforia because this "
                        + "device is not supported.";
                } else
                {
                    logMessage = "Failed to initialize Vuforia.";
                }
                
                // Log error:
                Log.e(LOGTAG, "InitVuforiaTask.onPostExecute: " + logMessage
                    + " Exiting.");
                
                // Send Vuforia Exception to the application and call initDone
                // to stop initialization process
                vuforiaException = new VuforiaException(
                    VuforiaException.INITIALIZATION_FAILURE,
                    logMessage);
                onInitARDone(vuforiaException);
            }
        }


		
    }
    
    public boolean doLoadTrackersData()
    {
        // Get the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        if (imageTracker == null)
        {
            Log.d(
                LOGTAG,
                "Failed to load tracking data set because the ImageTracker has not been initialized.");
            return false;
        }
        
        // Create the data set:
        dataSetUserDef = imageTracker.createDataSet();
        if (dataSetUserDef == null)
        {
            Log.d(LOGTAG, "Failed to create a new tracking data.");
            return false;
        }
        
        if (!imageTracker.activateDataSet(dataSetUserDef))
        {
            Log.d(LOGTAG, "Failed to activate data set.");
            return false;
        }
        
        Log.d(LOGTAG, "Successfully loaded and activated data set.");
        return true;
    }
    
    // An async task to load the tracker data asynchronously.
    private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap:
            synchronized (mShutdownLock)
            {
                // Load the tracker data set:
                return doLoadTrackersData();
            }
        }
        
        
        protected void onPostExecute(Boolean result)
        {
            
            VuforiaException vuforiaException = null;
            
            Log.d(LOGTAG, "LoadTrackerTask.onPostExecute: execution "
                + (result ? "successful" : "failed"));
            
            if (!result)
            {
                String logMessage = "Failed to load tracker data.";
                // Error loading dataset
                Log.e(LOGTAG, logMessage);
                vuforiaException = new VuforiaException(
                    VuforiaException.LOADING_TRACKERS_FAILURE,
                    logMessage);
            } else
            {
                // Hint to the virtual machine that it would be a good time to
                // run the garbage collector:
                //
                // NOTE: This is only a hint. There is no guarantee that the
                // garbage collector will actually be run.
                System.gc();
                
                Vuforia.registerCallback(AndroidVuforiaSession.this);
                
                m_started = true;
            }
            
            // Done loading the tracker, update application status, send the
            // exception to check errors
            onInitARDone(vuforiaException);
        }
    }
    
    // Stores the orientation depending on the current resources configuration
    private void updateActivityOrientation()
    {
        Configuration config = m_activity.getResources().getConfiguration();
        
        switch (config.orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                mIsPortrait = true;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                mIsPortrait = false;
                break;
            case Configuration.ORIENTATION_UNDEFINED:
            default:
                break;
        }
        
        Log.i(LOGTAG, "Activity is in "
            + (mIsPortrait ? "PORTRAIT" : "LANDSCAPE"));
    }
    
    
    // Method for setting / updating the projection matrix for AR content
    // rendering
    private void setProjectionMatrix()
    {
        CameraCalibration camCal = CameraDevice.getInstance()
            .getCameraCalibration();
        mProjectionMatrix = Tool.getProjectionGL(camCal, 10.0f, 5000.0f);
    }
    
    
    private void stopCamera()
    {
        doStopTrackers();
        CameraDevice.getInstance().stop();
        CameraDevice.getInstance().deinit();
    }
    
    
    // Applies auto focus if supported by the current device
    private boolean setFocusMode(int mode) throws VuforiaException
    {
        boolean result = CameraDevice.getInstance().setFocusMode(mode);
        
        if (!result)
            throw new VuforiaException(
                VuforiaException.SET_FOCUS_MODE_FAILURE,
                "Failed to set focus mode: " + mode);
        
        return result;
    }
    
    
    // Configures the video mode and sets offsets for the camera's image
    private void configureVideoBackground()
    {
        CameraDevice cameraDevice = CameraDevice.getInstance();
        VideoMode vm = cameraDevice.getVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        
        VideoBackgroundConfig config = new VideoBackgroundConfig();
        config.setEnabled(true);
        config.setSynchronous(true);
        config.setPosition(new Vec2I(0, 0));
        
        int xSize = 0, ySize = 0;
        int mScreenHeight = Gdx.graphics.getHeight();
        int mScreenWidth = Gdx.graphics.getWidth();
        
		if (mIsPortrait)
        {
            xSize = (int) (vm.getHeight() * (mScreenHeight / (float) vm
                .getWidth()));
            ySize = mScreenHeight;
            
            if (xSize < mScreenWidth)
            {
                xSize = mScreenWidth;
                ySize = (int) (mScreenWidth * (vm.getWidth() / (float) vm
                    .getHeight()));
            }
        } else
        {
            xSize = mScreenWidth;
            ySize = (int) (vm.getHeight() * (mScreenWidth / (float) vm
                .getWidth()));
            
            if (ySize < mScreenHeight)
            {
                xSize = (int) (mScreenHeight * (vm.getWidth() / (float) vm
                    .getHeight()));
                ySize = mScreenHeight;
            }
        }
        
        config.setSize(new Vec2I(xSize, ySize));
        
        Log.i(LOGTAG, "Configure Video Background : Video (" + vm.getWidth()
            + " , " + vm.getHeight() + "), Screen (" + mScreenWidth + " , "
            + mScreenHeight + "), mSize (" + xSize + " , " + ySize + ")");
        
        Renderer.getInstance().setVideoBackgroundConfig(config);
        
    }
    
    
    // Returns true if Vuforia is initialized, the trackers started and the
    // tracker data loaded
    private boolean isARRunning()
    {
        return m_started;
    }


	@Override
	public void beginRendering() {
		State currentState = Renderer.getInstance().begin();
		
	}


	@Override
	public boolean drawVideoBackground() {
		return Renderer.getInstance().drawVideoBackground();
	}


	@Override
	public void endRendering() {
		Renderer.getInstance().end();
	}


	@Override
	public boolean isInited() {
		return Vuforia.isInitialized();
	}

	@Override
	public void onResize(int width, int height) {
		Vuforia.onSurfaceChanged(width, height);
	}

	@Override
	public void stop() {
        try
        {
            stopAR();
        } catch (VuforiaException e)
        {
            Log.e(LOGTAG, e.getString());
        }
	}
    
}
