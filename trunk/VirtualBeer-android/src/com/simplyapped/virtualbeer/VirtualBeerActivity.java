package com.simplyapped.virtualbeer;

import android.app.Activity;
import android.content.Intent;
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
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.VuforiaAndroidApplication;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.simplyapped.libgdx.ext.facebook.AndroidFacebook;
import com.simplyapped.libgdx.ext.facebook.Facebook;
import com.simplyapped.libgdx.ext.facebook.FacebookStatusCallBack;
import com.simplyapped.libgdx.ext.ui.AndroidOSDialog;
import com.simplyapped.libgdx.ext.vuforia.AndroidVuforiaSession;

public class VirtualBeerActivity extends VuforiaAndroidApplication
{
  private static final String LOGTAG = VirtualBeerActivity.class.toString();
  private static final String GOOGLE_AD_UNIT_ID = "ca-app-pub-7782303924153821/5391574604";
  private AndroidVuforiaSession vuforia;
  private View gameview;
  private AdLayout amazonAdView;
  private RelativeLayout layout;
  private UiLifecycleHelper facebookHelper;
  private FacebookStatusCallBack facebookCallback;

  private class AdSwitcher extends AmazonAdListenerAdapter{
    
    private Activity activity;

    public AdSwitcher(Activity activity){
      this.activity = activity;}
    
    @Override
    public void onAdFailedToLoad(AdLayout layout, AdError error) {
      // ADMOB ADS VIEW AND REQUESTS
        // Look up the AdView as a resource and load a request.
        AdView adView = new AdView(VirtualBeerActivity.this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(GOOGLE_AD_UNIT_ID);
    
        AdRequest adRequest = new AdRequest.Builder()
                              .addTestDevice("2339EA1D6F4179678B076F1547710A22")
                              .build();
        adView.loadAd(adRequest);
        if (VirtualBeerActivity.this.amazonAdView != null)
        {
          VirtualBeerActivity.this.amazonAdView.destroy();
        }
        VirtualBeerActivity.this.layout.addView(adView);
        Log.d(VirtualBeerActivity.class.toString(), "Amazon ADs failed to load using Admob ads: " + error.getMessage());
    }
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    //FacebookDialog.OpenGraphActionDialogBuilder d;d.canPresent()
//    WebDialog.FeedDialogBuilder f;f.
    facebookCallback = new FacebookStatusCallBack();
    facebookHelper = new UiLifecycleHelper(this, facebookCallback);
    facebookHelper.onCreate(savedInstanceState);
    
    Facebook fb = new AndroidFacebook(this, facebookCallback, facebookHelper);
    
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().setFlags( 
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    try
    {
      requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    catch (Exception ex)
    {
      log("AndroidApplication", "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
    }
    
    layout = new RelativeLayout(this);
    
    /** AMAZON ADS VIEW AND REQUESTS */
//    AdRegistration.enableTesting(true);
//    AdRegistration.enableLogging(true);
    AdRegistration.setAppKey("cbaeb9717771460a892433442cbabb95");
    this.amazonAdView = new AdLayout(this);
    this.amazonAdView.setListener(new AdSwitcher(this));
    this.amazonAdView.setLayoutParams(createAdParams());
    this.amazonAdView.loadAd(new AdTargetingOptions()); // This AsyncTask retrieves an ad
    
    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = true;

    VirtualBeerGame game = new VirtualBeerGame();
    createVuforiaInstance();
    game.setVuforia(vuforia);
    game.setDialog(new AndroidOSDialog(this));
    
    gameview = initializeForView(game, cfg);
    
    layout.addView(gameview, createLayoutParams());
    layout.addView(amazonAdView);
    layout.setPadding(0, 1, 0, 0);
    setContentView(layout);
  }

  private RelativeLayout.LayoutParams createAdParams()
  {
    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams
        (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//    adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    return adParams;
  }

  private void createVuforiaInstance()
  {
    if (vuforia == null)
    {
      vuforia = new AndroidVuforiaSession(this);
      vuforia.setHasAutoFocus(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS));
      vuforia.setHasFlash(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH));
      vuforia.init();
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
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    facebookHelper.onActivityResult(requestCode, resultCode, data, facebookCallback);
  }
  
  @Override
  protected void onResume() {
      super.onResume();
      facebookHelper.onResume();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      facebookHelper.onSaveInstanceState(outState);
  }

  @Override
  public void onPause() {
      super.onPause();
      facebookHelper.onPause();
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    facebookHelper.onDestroy();
    
  }
}