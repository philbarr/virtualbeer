package com.simplyapped.libgdx.ext.vuforia;

import com.qualcomm.vuforia.HINT;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.Vuforia;

public class AndroidVuforiaState implements VuforiaState {

	private State state;

	public AndroidVuforiaState(State state) {
		this.state = state;
	}


	
	@Override
	public int getNumTrackableResults()
	{
		return state.getNumTrackableResults();
	}
	
	@Override
	public int getNumTrackables()
	{
		return state.getNumTrackables();
	}
	
	@Override
	public VuforiaTrackable getTrackable(int index)
	{
		return new AndroidVuforiaTrackable(state.getTrackable(index));
	}
	
	@Override
	public VuforiaTrackableResult getTrackableResult(int index)
	{
		return new AndroidVuforiaTrackableResult(state.getTrackableResult(index));
	}
}
