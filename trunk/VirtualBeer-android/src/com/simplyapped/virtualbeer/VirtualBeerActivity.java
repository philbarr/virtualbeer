package com.simplyapped.virtualbeer;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.VuforiaAndroidApplication;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.qualcomm.vuforia.CameraDevice;
import com.simplyapped.libgdx.ext.ui.AndroidOSDialog;
import com.simplyapped.libgdx.ext.vuforia.AndroidVuforiaSession;

public class VirtualBeerActivity extends VuforiaAndroidApplication
{
  private static final String LOGTAG = VirtualBeerActivity.class.toString();
  private static final String GOOLGE_AD_UNIT_ID = "ca-app-pub-7782303924153821/5391574604";
  private static AndroidVuforiaSession vuforia;
  private View gameview;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    try
    {
      Camera.open(CameraDevice.CAMERA.CAMERA_DEFAULT).setErrorCallback(new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera)
        {
          Toast.makeText(getApplicationContext(), "Camera died. Attempting to restart", Toast.LENGTH_LONG).show();
          createVuforiaInstance();
          vuforia.initAsync();
        }});
      log(LOGTAG, "Error callback registered successfully");
    }
    catch (Exception e)
    {
      Gdx.app.log(LOGTAG, "Failed to register camera error callback: ", e);
    }

    // ADS view
    RelativeLayout layout = new RelativeLayout(this);
    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams
        (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

    AdView adView = new AdView(this);
    adView.setAdSize(AdSize.SMART_BANNER);
    adView.setAdUnitId(GOOLGE_AD_UNIT_ID);

    AdRequest adRequest = new AdRequest.Builder()
                          .addTestDevice("2339EA1D6F4179678B076F1547710A22")
                          .build();
    adView.loadAd(adRequest);

    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = true;

    VirtualBeerGame game = new VirtualBeerGame();
    createVuforiaInstance();
    game.setVuforia(vuforia);
    game.setDialog(new AndroidOSDialog(this));
    
    
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
    layout.addView(gameview, createLayoutParams());
    layout.addView(adView, adParams);
    layout.setPadding(0, 1, 0, 0);
    setContentView(layout);
    
  }

  private void createVuforiaInstance()
  {
    if (vuforia == null)
    {
      vuforia = new AndroidVuforiaSession(this);
      vuforia.setHasAutoFocus(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS));
      vuforia.setHasFlash(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH));
      vuforia.initAsync();
    }
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