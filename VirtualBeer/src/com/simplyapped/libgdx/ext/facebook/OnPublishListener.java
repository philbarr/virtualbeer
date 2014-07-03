package com.simplyapped.libgdx.ext.facebook;

public interface OnPublishListener
{
  void onCall(Exception exception);
  void onComplete();
  void onError();
}
