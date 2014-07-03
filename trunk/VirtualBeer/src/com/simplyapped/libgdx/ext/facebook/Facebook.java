package com.simplyapped.libgdx.ext.facebook;

public interface Facebook
{
  void uploadPhoto(byte[] image, String albumName, OnPublishListener listener);
}
