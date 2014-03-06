package com.simplyapped.virtualbeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

class PhilCameraInputController extends CameraInputController
{
	protected static class PhilCameraGestureListener extends CameraGestureListener
	{
		private float previousZoom; 
		@Override
		public boolean touchDown (float x, float y, int pointer, int button) {
			previousZoom = 0;
			return false;
		}
		
		@Override
		public boolean zoom (float initialDistance, float distance) {
			float newZoom = distance - initialDistance;
			float amount = newZoom - previousZoom;
			previousZoom = newZoom;
			float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
			return controller.zoom((amount / ((w > h) ? h : w)) * 2000);
		}
	}

	protected PhilCameraInputController(Camera camera)
	{
		super(new PhilCameraGestureListener(), camera);
	}
	
}