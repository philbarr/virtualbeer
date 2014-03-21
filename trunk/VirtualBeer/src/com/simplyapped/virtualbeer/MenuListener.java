package com.simplyapped.virtualbeer;

public interface MenuListener {
	/**
	 * @return the new state of the flash (true for on, false for off)
	 */
	boolean flashClicked();
	
	/**
	 * @return the new state of the camera (true for running, false for not running)
	 */
	boolean cameraClicked();
}
