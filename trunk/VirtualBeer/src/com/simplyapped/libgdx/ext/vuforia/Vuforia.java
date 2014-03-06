package com.simplyapped.libgdx.ext.vuforia;

public interface Vuforia {
	void beginRendering();
	boolean drawVideoBackground();
	void endRendering();
	boolean isInited();
	
	/**
	 * Field of view in degrees
	 * @param fovDegrees
	 */
	void setFieldOfView(float fovDegrees);
	float getFieldOfView();
	
	void onCreate();
	void onResize(int width, int height);
}
