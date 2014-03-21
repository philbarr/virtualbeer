package com.simplyapped.libgdx.ext.vuforia;

public class DesktopVuforiaImageTargetBuilder implements
		VuforiaImageTargetBuilder {

	private boolean isRunning;

	@Override
	public boolean startScan() {
		isRunning = true;
		return true;
	}

	@Override
	public void stopScan() {
		isRunning = false;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public int frameQuality() {
		return VuforiaImageTargetBuilder.FRAME_QUALITY_HIGH;
	}

	@Override
	public boolean build(String name, float sceneSizeWidth) {
		return true;
	}

	@Override
	public VuforiaTrackableSource getTrackableSource() {
		return new DesktopVuforiaTrackableSource();
	}

}
