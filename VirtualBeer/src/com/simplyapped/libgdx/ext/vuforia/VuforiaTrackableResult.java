package com.simplyapped.libgdx.ext.vuforia;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;

public interface VuforiaTrackableResult {

	Matrix4 getPose();

	void setCameraPositionAndDirection(Camera cam);

}
