package com.simplyapped.libgdx.ext.vuforia;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;

public class DesktopVuforiaTrackableResult implements VuforiaTrackableResult {

	@Override
	public Matrix4 getPose() {
		return new Matrix4(new float[]{	0.65500414f, -0.58503735f, -0.47822696f, 0.0f, 
				-0.44855505f, -0.8103584f, 0.3769852f, 0.0f, 
				-0.6080855f, -0.03241571f, -0.79320955f, 0.0f, 
				19.917683f, 18.692945f, 545.3725f, 1.0f});
	}
}
