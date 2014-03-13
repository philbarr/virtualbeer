package com.simplyapped.virtualbeer;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.simplyapped.libgdx.ext.ui.AndroidOSDialog;
import com.simplyapped.libgdx.ext.vuforia.AndroidVuforiaSession;

public class VirtualBeerActivity extends AndroidApplication {
    private static final String LOGTAG = VirtualBeerActivity.class.toString();
	private AndroidVuforiaSession vuforia;
	private View view;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       	AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;

        VirtualBeerGame game = new VirtualBeerGame();
        vuforia = new AndroidVuforiaSession(this);
        vuforia.setHasAutoFocus(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS));
        vuforia.setHasFlash(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH));
        
        
		game.setVuforia(vuforia);
		game.setDialog(new AndroidOSDialog(this));
		vuforia.initAsync();
		view = initializeForView(game, cfg);
		
        try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception ex) {
			log("AndroidApplication", "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
		}
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(view, createLayoutParams());
    }
	
	protected FrameLayout.LayoutParams createLayoutParams () {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
			android.view.ViewGroup.LayoutParams.FILL_PARENT);
		layoutParams.gravity = Gravity.CENTER;
		return layoutParams;
	}
	
    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config)
    {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        
        vuforia.onConfigurationChanged();
        
    }
}