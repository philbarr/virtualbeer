package com.simplyapped.libgdx.ext.vuforia;

public interface VuforiaSession {
	void beginRendering();
	boolean drawVideoBackground();
	void endRendering();
	boolean isInited();
	void initAsync();
	void onResize(int width, int height);
	void onPause();
	void onResume();
	void stop();
	TargetBuilder getTargetBuilder();
}
