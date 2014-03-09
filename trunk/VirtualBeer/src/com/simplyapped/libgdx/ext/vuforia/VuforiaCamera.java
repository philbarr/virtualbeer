package com.simplyapped.libgdx.ext.vuforia;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class VuforiaCamera extends PerspectiveCamera {
	final Vector3 tmp = new Vector3();
	public VuforiaCamera(int i, int width, int height) {
		super(i,width,height);
	}
	public void update (boolean updateFrustum, Matrix4 projection) {
		this.projection.set(projection);
		view.setToLookAt(position, tmp.set(position).add(direction), up);
		combined.set(projection);
		Matrix4.mul(combined.val, view.val);

		if (updateFrustum) {
			invProjectionView.set(combined);
			Matrix4.inv(invProjectionView.val);
			frustum.update(invProjectionView);
		}
	}
}
