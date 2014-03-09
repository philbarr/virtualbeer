package com.simplyapped.libgdx.ext.vuforia;

import com.qualcomm.vuforia.Trackable;

public class AndroidVuforiaTrackable implements VuforiaTrackable {

	private Trackable trackable;

	public AndroidVuforiaTrackable(Trackable trackable) {
		this.trackable = trackable;
	}

}
