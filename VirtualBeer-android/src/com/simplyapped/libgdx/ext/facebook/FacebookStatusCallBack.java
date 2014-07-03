package com.simplyapped.libgdx.ext.facebook;

import android.os.Bundle;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.widget.FacebookDialog.Callback;
import com.facebook.widget.FacebookDialog.PendingCall;

public class FacebookStatusCallBack implements StatusCallback, Callback {

  
  
  // to handle the result of calling the Share dialog
  @Override
  public void call(Session session, SessionState state, Exception exception)
  {
  }

  
  // Invoked when Facebook Share dialog closes 
  @Override
  public void onComplete(PendingCall pendingCall, Bundle data)
  {
    // TODO Auto-generated method stub
    
  }

  // Invoked when Facebook Share dialog closes 
  @Override
  public void onError(PendingCall pendingCall, Exception error, Bundle data)
  {
    // TODO Auto-generated method stub
    
  }
}