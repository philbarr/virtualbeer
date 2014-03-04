package com.simplyapped.virtualbeer;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.qualcomm.vuforia.State;
import com.simplyapped.virtualbeer.vuforia.SampleApplicationControl;
import com.simplyapped.virtualbeer.vuforia.SampleApplicationException;
import com.simplyapped.virtualbeer.vuforia.SampleApplicationSession;

public class VirtualBeerActivity extends AndroidApplication implements SampleApplicationControl {
    private SampleApplicationSession vuforiaAppSession;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        initialize(new VirtualBeerGame(), cfg);
        
        vuforiaAppSession = new SampleApplicationSession(this);
        
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

	@Override
	public boolean doInitTrackers() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doLoadTrackersData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doStartTrackers() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doStopTrackers() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doUnloadTrackersData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doDeinitTrackers() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onInitARDone(SampleApplicationException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onQCARUpdate(State state) {
		// TODO Auto-generated method stub
		
	}
}