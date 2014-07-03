package com.simplyapped.libgdx.ext.vuforia;

public class DesktopVuforiaState implements VuforiaState {

	@Override
	public VuforiaTrackableResult getTrackableResult(int index) {
		return new DesktopVuforiaTrackableResult();
	}

	@Override
	public VuforiaTrackable getTrackable(int index) {
		return new DesktopVuforiaTrackable();
	}

	@Override
	public int getNumTrackables() {
		return 1;
	}

	@Override
	public int getNumTrackableResults() {
		return 1;
	}

  @Override
  public VuforiaFrame getFrame()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getNumImages()
  {
    // TODO Auto-generated method stub
    return 0;
  }

}
