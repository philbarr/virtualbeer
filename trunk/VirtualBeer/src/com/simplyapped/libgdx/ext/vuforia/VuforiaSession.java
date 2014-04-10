package com.simplyapped.libgdx.ext.vuforia;

import com.badlogic.gdx.math.Matrix4;

public interface VuforiaSession {
  // Rendering
	VuforiaState beginRendering();
	boolean drawVideoBackground();
	void endRendering();
	
	// starting and stopping
	boolean isInited();
	boolean isRunning();
	void init();
	void deinit() throws VuforiaException;
	void startCamera() throws VuforiaException;
	void stopCamera();
	void startTrackers();
	void stopTrackers();

	VuforiaImageTargetBuilder getTargetBuilder();
	void setListener(VuforiaListener listener);
	
	/**
	 * Create a new VuforiaTrackableSource and overwrite the current DataSet with it
	 * @param source - the new TrackableSource
	 */
	void createTrackable(VuforiaTrackableSource source);
	void clearAllTrackables();
	void setExtendedTracking(boolean extendedTracking);
	boolean isExtendedTracking();
	
	boolean setFlash(boolean on);
	boolean setAutoFocus(boolean on);
	Matrix4 getProjectionMatrix();
	double getFieldOfView();
	boolean setNumTrackablesHint(int numTrackables);
	boolean hasAutoFocus();
	boolean hasFlash();
  int getProgressValue();
  int getWidth();
  int getHeight();
  void doFocusCamera();
}
