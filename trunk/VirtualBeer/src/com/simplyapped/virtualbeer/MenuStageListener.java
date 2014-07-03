package com.simplyapped.virtualbeer;

public interface MenuStageListener {
	/**
	 * @return the new state of the flash (true for on, false for off)
	 */
	boolean flashButtonClicked();
	
	/**
	 * @return the new state of the camera (true for running, false for not running)
	 */
	void cameraButtonClicked();

  void quit();

  void focus();
	
  void takeSnapshot();
}
