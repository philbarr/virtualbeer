/*==============================================================================
 Copyright (c) 2012-2013 Qualcomm Connected Experiences, Inc.
 All Rights Reserved.
 ==============================================================================*/

package com.simplyapped.libgdx.ext.vuforia;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Interpolator.Result;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.HINT;
import com.qualcomm.vuforia.ImageTargetBuilder;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.PIXEL_FORMAT;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vec2F;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Vuforia.UpdateCallbackInterface;

public class AndroidVuforiaSession implements VuforiaSession, UpdateCallbackInterface
{

  private static final String LOGTAG = AndroidVuforiaSession.class.toString();

  // Reference to the current activity
  private Activity activity;

  // Flags
  private boolean isStarted = false;

  // An object used for synchronizing Vuforia initialization, dataset loading
  // and the Android onDestroy() life cycle event. If the application is
  // destroyed while a data set is still being loaded, then we wait for the
  // loading operation to finish before shutting down Vuforia:
  private Object lock = new Object();

  // Holds the camera configuration to use upon resuming
  private int camera = CameraDevice.CAMERA.CAMERA_DEFAULT;

  // Stores the projection matrix to use for rendering purposes
  private Matrix44F projectionMatrix;

  // Store the DataSet in the session so that it's management can be correctly
  // synchronized
  private DataSet dataSet;

  private VuforiaListener listener;

  private boolean extendedTracking;

  private boolean hasAutoFocus;
  private boolean hasFlash;

  private int screenWidth;
  private int screenHeight;

  public AndroidVuforiaSession(Activity activity)
  {
    this.activity = activity;
  }

  public void initAsync()
  {
    new AsyncTask<Void, Integer, Void>()
    {

      @Override
      protected Void doInBackground(Void... params)
      {
        init();
        return null;
      }
    }.execute();
  }
  
  private int progressValue = 0;

  private int width;

  private int height;
  // Initializes Vuforia and sets up preferences.
  public void init()
  {
    synchronized (lock)
    {
      Vuforia.setInitParameters(activity, Vuforia.GL_20);
      do
      {
        progressValue = Vuforia.init();
      }
      while (getProgressValue() >= 0 && getProgressValue() < 100);

      ImageTracker tracker = (ImageTracker) TrackerManager.getInstance().initTracker(ImageTracker.getClassType());
      if (tracker == null)
      {
        tracker = (ImageTracker) TrackerManager.getInstance().getTracker(ImageTracker.getClassType());
      }
      else
      {
        dataSet = tracker.createDataSet();
        tracker.activateDataSet(dataSet); // returns false if data set failed to load
      }
      Vuforia.registerCallback(this); // calls QCAR_onUpdate(State state) after
                                      // end of each tracking phase

      // start ImageTargetBuilder
      ImageTargetBuilder targetBuilder = tracker.getImageTargetBuilder();
      targetBuilder.startScan();
      
      try
      {
        startCamera(); 
      }
      catch (VuforiaException e)
      {
        e.printStackTrace();
      }
      
      if (listener != null)
      {
        listener.onInitDone();
      }
    }
  }

  // Starts Vuforia, initialize and starts the camera and start the trackers
  public void startCamera() throws VuforiaException
  {
    if (!isStarted)
    {
      String error;
      if (!CameraDevice.getInstance().init(camera))
      {
        error = "Unable to open camera device: " + camera;
        Log.e(LOGTAG, error);
        throw new VuforiaException(VuforiaException.CAMERA_INITIALIZATION_FAILURE, error);
      }
      // Camera.g
      configureVideoBackground();

      if (!CameraDevice.getInstance().selectVideoMode(CameraDevice.MODE.MODE_DEFAULT))
      {
        error = "Unable to set video mode";
        Log.e(LOGTAG, error);
        throw new VuforiaException(VuforiaException.CAMERA_INITIALIZATION_FAILURE, error);
      }

      if (!CameraDevice.getInstance().start())
      {
        error = "Unable to start camera device: " + camera;
        Log.e(LOGTAG, error);
        throw new VuforiaException(VuforiaException.CAMERA_INITIALIZATION_FAILURE, error);
      }

      Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);

      setProjectionMatrix();

      startTrackers();
      isStarted = true;
      try
      {
        if (hasAutoFocus)
        {
          CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
        }
        else
        {
          CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
        }
      }
      catch (Exception e)
      {
        // ok let's just leave it as it is
      }
    }
  }

  public void doFocusCamera()
  {
    CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
  }

  @Override
  public boolean setNumTrackablesHint(int numTrackables)
  {
    boolean set = Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, numTrackables);
    return set;
  }

  // Stops any ongoing initialization, stops Vuforia
  public void deinit() throws VuforiaException
  {
    stopCamera();

    // Ensure that all asynchronous operations to initialize Vuforia
    // and loading the tracker datasets do not overlap:
    synchronized (lock)
    {
      TrackerManager trackerManager = TrackerManager.getInstance();
      ImageTracker imageTracker = (ImageTracker) trackerManager.getTracker(ImageTracker.getClassType());
      if (imageTracker != null) // if null then imageTracker is not inited
      {
        // can get current dataSet with imageTracker.getActiveDataSet();
        imageTracker.deactivateDataSet(dataSet); // returns false if failed
        imageTracker.destroyDataSet(dataSet); // returns false if failed
        ImageTargetBuilder builder = imageTracker.getImageTargetBuilder();
        if (builder != null && (builder.getFrameQuality() != ImageTargetBuilder.FRAME_QUALITY.FRAME_QUALITY_NONE))
        {
          builder.stopScan();
        }
        trackerManager.deinitTracker(ImageTracker.getClassType());
        Vuforia.deinit();
      }
    }

    Gdx.app.log(LOGTAG, "Vuforia unloaded");
  }

  // Gets the projection matrix to be used for rendering
  @Override
  public Matrix4 getProjectionMatrix()
  {
    if (projectionMatrix == null)
    {
      setProjectionMatrix();
    }
    return new Matrix4(projectionMatrix.getData());
  }

  // Callback called every cycle
  @Override
  public void QCAR_onUpdate(State state)
  {
    if (listener != null && state != null)
    {
      listener.onUpdate(new AndroidVuforiaState(state));
    }
  }

  // Manages the configuration changes
  public void onConfigurationChanged()
  {
    if (isRunning())
    {
      // configure video background
      configureVideoBackground();
      setProjectionMatrix();
    }
  }

  // Method for setting / updating the projection matrix for AR content
  // rendering
  private void setProjectionMatrix()
  {
//    synchronized (lock)
    {
      Log.i(LOGTAG, "setProjectionMatrix");
      CameraCalibration camCal = CameraDevice.getInstance().getCameraCalibration();
      projectionMatrix = Tool.getProjectionGL(camCal, 10f, 10000f);

    }
  }

  @Override
  public void stopCamera()
  {
    Gdx.app.log(LOGTAG, "stopCamera");
    isStarted = false;
    stopTrackers();
    CameraDevice.getInstance().stop();
    CameraDevice.getInstance().deinit();

  }

  // Configures the video mode and sets offsets for the camera's image
  private void configureVideoBackground()
  {
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    screenWidth = metrics.widthPixels;
    screenHeight = metrics.heightPixels;

    Configuration activityConfig = activity.getResources().getConfiguration();
    boolean isPortrait = true;
    switch (activityConfig.orientation)
    {
      case Configuration.ORIENTATION_PORTRAIT:
        isPortrait = true;
        break;
      case Configuration.ORIENTATION_LANDSCAPE:
        isPortrait = false;
        break;
      case Configuration.ORIENTATION_UNDEFINED:
      default:
        break;
    }

    CameraDevice cameraDevice = CameraDevice.getInstance();
    VideoMode vm = cameraDevice.getVideoMode(CameraDevice.MODE.MODE_DEFAULT);
    VideoBackgroundConfig config = new VideoBackgroundConfig();
    config.setEnabled(true);
    config.setSynchronous(true);
    config.setPosition(new Vec2I(0, 0));

    setWidth(0);
    setHeight(0);
    if (isPortrait)
    {
      setWidth((int) (vm.getHeight() * (screenHeight / (float) vm.getWidth())));
      setHeight(screenHeight);

      if (getWidth() < screenWidth)
      {
        setWidth(screenWidth);
        setHeight((int) (screenWidth * (vm.getWidth() / (float) vm.getHeight())));
      }
    }
    else
    {
      setWidth(screenWidth);
      setHeight((int) (vm.getHeight() * (screenWidth / (float) vm.getWidth())));

      if (getHeight() < screenHeight)
      {
        setWidth((int) (screenHeight * (vm.getWidth() / (float) vm.getHeight())));
        setHeight(screenHeight);
      }
    }
    config.setSize(new Vec2I(getWidth(), getHeight()));
    Renderer.getInstance().setVideoBackgroundConfig(config);
  }

  @Override
  public VuforiaState beginRendering()
  {
    State state = Renderer.getInstance().begin();

    return new AndroidVuforiaState(state);
  }

  @Override
  public boolean drawVideoBackground()
  {
    return Renderer.getInstance().drawVideoBackground();
  }

  @Override
  public void endRendering()
  {
    Renderer.getInstance().end();
  }

  @Override
  public boolean isInited()
  {
    return Vuforia.isInitialized();
  }

  @Override
  public VuforiaImageTargetBuilder getTargetBuilder()
  {
    return new AndroidVuforiaImageTargetBuilder();
  }

  @Override
  public void setListener(VuforiaListener listener)
  {
    this.listener = listener;

  }

  @Override
  public void createTrackable(VuforiaTrackableSource source)
  {
    Gdx.app.log(LOGTAG, "createTrackable");
    TrackerManager trackerManager = TrackerManager.getInstance();
    ImageTracker imageTracker = (ImageTracker) (trackerManager.getTracker(ImageTracker.getClassType()));
    if (imageTracker != null)
    {
      imageTracker.deactivateDataSet(dataSet);

      // Clear the oldest target if the dataset is full or the dataset
      // already contains five user-defined targets.
      if (dataSet.hasReachedTrackableLimit() || dataSet.getNumTrackables() >= 5)
      {
        dataSet.destroy(dataSet.getTrackable(0));
      }

      if (isExtendedTracking() && dataSet.getNumTrackables() > 0)
      {
        // We need to stop the extended tracking for the previous target
        // so we can enable it for the new one
        int previousCreatedTrackableIndex = dataSet.getNumTrackables() - 1;

        dataSet.getTrackable(previousCreatedTrackableIndex).stopExtendedTracking();
      }

      Trackable trackable = dataSet.createTrackable(((AndroidVuforiaTrackableSource) source).getTrackableSource());

      // Reactivate current dataset
      imageTracker.activateDataSet(dataSet);

      if (isExtendedTracking())
      {
        trackable.startExtendedTracking();
      }
    }
  }

  @Override
  public double getFieldOfView()
  {
    CameraCalibration cameraCalibration = CameraDevice.getInstance().getCameraCalibration();

    Vec2F size = cameraCalibration.getSize();
    Vec2F focalLength = cameraCalibration.getFocalLength();

    double fovRadians = 2 * Math.atan(0.5f * size.getData()[1] / focalLength.getData()[1]);
    double fovDegrees = fovRadians * 180.0f / Math.PI;
    return fovDegrees;
  }

  @Override
  public boolean isExtendedTracking()
  {
    return extendedTracking;
  }

  @Override
  public void setExtendedTracking(boolean extendedTracking)
  {
    this.extendedTracking = extendedTracking;
  }

  @Override
  public boolean setFlash(boolean on)
  {
    return CameraDevice.getInstance().setFlashTorchMode(on);
  }

  @Override
  public boolean setAutoFocus(boolean on)
  {
    if (on)
    {
      return CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
    }
    else
    {
      return CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
    }
  }

  @Override
  public boolean hasAutoFocus()
  {
    return hasAutoFocus;
  }

  public void setHasAutoFocus(boolean hasAutoFocus)
  {
    this.hasAutoFocus = hasAutoFocus;
  }

  @Override
  public boolean hasFlash()
  {
    return hasFlash;
  }

  public void setHasFlash(boolean hasFlash)
  {
    this.hasFlash = hasFlash;
  }

  @Override
  public void clearAllTrackables()
  {
    TrackerManager trackerManager = TrackerManager.getInstance();
    ImageTracker imageTracker = (ImageTracker) (trackerManager.getTracker(ImageTracker.getClassType()));
    if (imageTracker != null)
    {
      imageTracker.deactivateDataSet(dataSet);
      for (int i = 0; i < dataSet.getNumTrackables(); i++)
      {
        dataSet.destroy(dataSet.getTrackable(i));
      }
      imageTracker.activateDataSet(dataSet);
    }
  }

  @Override
  public boolean isRunning()
  {
    return isStarted;
  }

  @Override
  public void startTrackers()
  {
    Tracker imageTracker = TrackerManager.getInstance().getTracker(ImageTracker.getClassType());
    if (imageTracker != null)
    {
      imageTracker.start();
    }
  }

  @Override
  public void stopTrackers()
  {
    Tracker imageTracker = TrackerManager.getInstance().getTracker(ImageTracker.getClassType());
    if (imageTracker != null)
    {
      imageTracker.stop();
    }

  }

  public int getProgressValue()
  {
    return progressValue;
  }

  public int getHeight()
  {
    return height;
  }

  private void setHeight(int height)
  {
    this.height = height;
  }

  public int getWidth()
  {
    return width;
  }

  private void setWidth(int width)
  {
    this.width = width;
  }
}
