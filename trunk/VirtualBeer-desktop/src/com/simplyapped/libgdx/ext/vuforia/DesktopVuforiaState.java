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

}
