package com.simplyapped.libgdx.ext.vuforia;

import com.badlogic.gdx.math.Matrix4;

public class DesktopVuforiaSession implements VuforiaSession {

	VuforiaListener listener;
	private boolean extendedTracking;
	private boolean flash;
	private boolean focus;
	private boolean hasAutoFocus;
	private boolean hasFlash;
	
	@Override
	public VuforiaState beginRendering() {
		return new DesktopVuforiaState();
	}

	@Override
	public boolean drawVideoBackground() {
		return true;
	}

	@Override
	public void endRendering() {
	}

	@Override
	public boolean isInited() {
		return true;
	}

	@Override
	public void init() {
		if (listener != null){
			listener.onInitDone();
		}
	}


	@Override
	public VuforiaImageTargetBuilder getTargetBuilder() {
		return new DesktopVuforiaImageTargetBuilder();
	}

	@Override
	public void setListener(VuforiaListener listener) {
		this.listener = listener;
	}

	@Override
	public void createTrackable(VuforiaTrackableSource source) {
	}

	@Override
	public void setExtendedTracking(boolean extendedTracking) {
		this.extendedTracking = extendedTracking;
	}

	@Override
	public boolean isExtendedTracking() {
		return extendedTracking;
	}

	@Override
	public boolean setFlash(boolean on) {
		this.flash = on;
		return flash;
	}

	@Override
	public boolean setAutoFocus(boolean on) {
		this.focus = on;
		return focus;
	}

	@Override
	public void startTrackers() {
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		return new Matrix4(new float[]{0.0f, -1.69032f, 0.0f, 0.0f, -2.25376f, 0.0f, 0.0f, 0.0f, 0.0f, -0.003125f, 1.002002f, 1.0f, 0.0f, 0.0f, -20.02002f, 0.0f});
	}

	@Override
	public double getFieldOfView() {
		return 67;
	}

	@Override
	public boolean setNumTrackablesHint(int numTrackables) {
		return true;
	}

	@Override
	public boolean hasAutoFocus() {
		return hasAutoFocus;
	}

	@Override
	public boolean hasFlash() {
		return hasFlash;
	}

	public void setHasAutoFocus(boolean hasAutoFocus) {
		this.hasAutoFocus = hasAutoFocus;
	}

	public void setHasFlash(boolean hasFlash) {
		this.hasFlash = hasFlash;
	}


  @Override
  public void clearAllTrackables()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean isRunning()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void deinit() throws VuforiaException
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void startCamera() throws VuforiaException
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stopCamera()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stopTrackers()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public int getProgressValue()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getWidth()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getHeight()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void doFocusCamera()
  {
    // TODO Auto-generated method stub
    
  }

}
