package com.simplyapped.virtualbeer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.simplyapped.libgdx.ext.ui.DesktopOSDialog;

public class DesktopVirtualBeer {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "VirtualBeer";
		cfg.useGL20 = true;
		cfg.width = 480;
		cfg.height = 320;
		
		VirtualBeerGame game = new VirtualBeerGame();
		game.setDialog(new DesktopOSDialog());
		LwjglApplication app = new LwjglApplication(game, cfg);
		
	}
}
