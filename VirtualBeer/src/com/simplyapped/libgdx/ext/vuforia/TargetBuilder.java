package com.simplyapped.libgdx.ext.vuforia;

public interface TargetBuilder {
	int FRAME_QUALITY_NONE = 0;
	int FRAME_QUALITY_LOW = 1;
	int FRAME_QUALITY_MEDIUM = 2;
	int FRAME_QUALITY_HIGH = 3;
	
	boolean startScan();
	void stopScan();
	boolean isRunning();
	int frameQuality();
	boolean build(String name, float sceneSizeWidth);
}
