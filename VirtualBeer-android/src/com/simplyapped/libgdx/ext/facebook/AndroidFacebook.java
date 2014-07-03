package com.simplyapped.libgdx.ext.facebook;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.OpenGraphAction;
import com.facebook.widget.FacebookDialog;

public class AndroidFacebook implements Facebook
{

  private Activity activity;
  private UiLifecycleHelper uiHelper;
  private FacebookStatusCallBack callback;

  public AndroidFacebook(Activity activity, FacebookStatusCallBack callback, UiLifecycleHelper uiHelper)
  {
    this.activity = activity;
    this.callback = callback;
    this.uiHelper = uiHelper;
  }
  
  @Override
  public void uploadPhoto(byte[] image, String albumName, OnPublishListener listener)
  {
    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
    OpenGraphAction action = OpenGraphAction.Factory.createForPost("virtualbeer:drink");
    action.setProperty("meal", "https://example.com/cooking-app/meal/Lamb-Vindaloo.html");

    List<Bitmap> images = new ArrayList<Bitmap>();
    images.add(bitmap);

    FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(activity, action, "beer")
            .setImageAttachmentsForAction(images, true)
            .build();
    uiHelper.trackPendingDialogCall(shareDialog.present());
  }

}
