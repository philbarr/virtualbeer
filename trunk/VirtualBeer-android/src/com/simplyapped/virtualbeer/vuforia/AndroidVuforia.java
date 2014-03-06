package com.simplyapped.virtualbeer.vuforia;

import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.simplyapped.libgdx.ext.vuforia.Vuforia;

public class AndroidVuforia implements Vuforia {

	private State state;
	private boolean inited;
	private float fovDegrees;

	@Override
	public void beginRendering() {
		state = Renderer.getInstance().begin();
	}

	@Override
	public void endRendering() {
		Renderer.getInstance().end();		
	}

	@Override
	public boolean drawVideoBackground() {
		return Renderer.getInstance().drawVideoBackground();
	}

	@Override
	public boolean isInited() {
		return inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	@Override
	public void setFieldOfView(float fovDegrees) {
		this.fovDegrees = fovDegrees;
	}

	@Override
	public float getFieldOfView() {
		return fovDegrees;
	}

	@Override
	public void onCreate() {
		com.qualcomm.vuforia.Vuforia.onSurfaceCreated();
	}

	@Override
	public void onResize(int width, int height) {
		com.qualcomm.vuforia.Vuforia.onSurfaceChanged(width, height);
	}

}
