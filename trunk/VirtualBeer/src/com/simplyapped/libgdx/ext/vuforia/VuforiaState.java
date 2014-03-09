package com.simplyapped.libgdx.ext.vuforia;

public interface VuforiaState {

	public abstract VuforiaTrackableResult getTrackableResult(int index);

	public abstract VuforiaTrackable getTrackable(int index);

	public abstract int getNumTrackables();

	public abstract int getNumTrackableResults();

}
