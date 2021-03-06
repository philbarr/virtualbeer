package com.simplyapped.libgdx.ext.vuforia;

import com.qualcomm.vuforia.Frame;
import com.qualcomm.vuforia.State;

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



  @Override
  public VuforiaFrame getFrame()
  {
    return new AndroidVuforiaFrame(state.getFrame());
  }



  @Override
  public int getNumImages()
  {
    // TODO Auto-generated method stub
    return 0;
  }
}
