package com.simplyapped.libgdx.ext.vuforia;

import com.badlogic.gdx.math.Matrix4;

public interface VuforiaSession {
	VuforiaState beginRendering();
	boolean drawVideoBackground();
	void endRendering();
	boolean isInited();
	void initAsync();
	void onResize(int width, int height);
	void onPause();
	void onResume();
	void stop();
	VuforiaImageTargetBuilder getTargetBuilder();
	void setListener(VuforiaListener listener);
	
	/**
	 * Create a new VuforiaTrackableSource and overwrite the current DataSet with it
	 * @param source - the new TrackableSource
	 */
	void createTrackable(VuforiaTrackableSource source);
	public abstract void setExtendedTracking(boolean extendedTracking);
	public abstract boolean isExtendedTracking();
	
	boolean setFlash(boolean on);
	boolean setAutoFocus(boolean on);
	void startTrackers();
	Matrix4 getProjectionMatrix();
	public abstract double getFieldOfView();
	boolean setNumTrackablesHint(int numTrackables);
	boolean hasAutoFocus();
	boolean hasFlash();
	boolean doFocus();
	
}
