package com.simplyapped.libgdx.ext.vuforia;

import com.qualcomm.vuforia.TrackableSource;

public class AndroidVuforiaTrackableSource implements VuforiaTrackableSource {

	private TrackableSource trackableSource;

	public AndroidVuforiaTrackableSource(TrackableSource trackableSource) {
		this.trackableSource = trackableSource;
	}

	public TrackableSource getTrackableSource() {
		return trackableSource;
	}

}
