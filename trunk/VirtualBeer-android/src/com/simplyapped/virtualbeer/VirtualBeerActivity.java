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
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.simplyapped.libgdx.ext.ui.AndroidOSDialog;
import com.simplyapped.libgdx.ext.vuforia.AndroidVuforiaSession;

public class VirtualBeerActivity extends AndroidApplication
{
  private static final String LOGTAG = VirtualBeerActivity.class.toString();
  private static final String GOOLGE_AD_UNIT_ID = "ca-app-pub-7782303924153821/4318057002";
  private AndroidVuforiaSession vuforia;
  private View gameview;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // ADS view
    RelativeLayout layout = new RelativeLayout(this);
    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams
        (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

    AdView adView = new AdView(this);
    adView.setAdSize(AdSize.BANNER);
    adView.setAdUnitId(GOOLGE_AD_UNIT_ID);

    AdRequest adRequest = new AdRequest.Builder()
                          .addTestDevice("2339EA1D6F4179678B076F1547710A22")
                          .build();
    adView.loadAd(adRequest);

    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = true;

    VirtualBeerGame game = new VirtualBeerGame();
    vuforia = new AndroidVuforiaSession(this);
    vuforia.setHasAutoFocus(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS));
    vuforia.setHasFlash(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH));
    game.setVuforia(vuforia);
    game.setDialog(new AndroidOSDialog(this));
    vuforia.initAsync();
    gameview = initializeForView(game, cfg);

    try
    {
      requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    catch (Exception ex)
    {
      log("AndroidApplication", "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
    }

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    layout.addView(gameview, adParams);
    layout.addView(adView);
    setContentView(layout, createLayoutParams());
  }

  protected FrameLayout.LayoutParams createLayoutParams()
  {
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