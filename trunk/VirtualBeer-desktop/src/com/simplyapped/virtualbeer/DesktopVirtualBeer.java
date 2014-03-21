package com.simplyapped.virtualbeer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.simplyapped.libgdx.ext.ui.DesktopOSDialog;
import com.simplyapped.libgdx.ext.vuforia.DesktopVuforiaSession;

public class DesktopVirtualBeer {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "VirtualBeer";
		cfg.useGL20 = true;
		cfg.width = 450;
		cfg.height = 675;
		
		//Vuforia
		DesktopVuforiaSession vuforia = new DesktopVuforiaSession();
		vuforia.setHasAutoFocus(true);
		vuforia.setHasFlash(true);

		//Dialogs
		DesktopOSDialog dialogs = new DesktopOSDialog();

		VirtualBeerGame game = new VirtualBeerGame();
		game.setDialog(dialogs);
		game.setVuforia(vuforia);
		
		LwjglApplication app = new LwjglApplication(game, cfg);
		
		vuforia.initAsync();
	}
}
