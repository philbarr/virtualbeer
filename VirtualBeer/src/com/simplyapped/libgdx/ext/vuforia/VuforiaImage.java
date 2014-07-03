package com.simplyapped.libgdx.ext.vuforia;

import java.nio.ByteBuffer;

public interface VuforiaImage
{
  ByteBuffer getBytes();
  void setBytes(ByteBuffer bytes);
  public abstract void setStride(int stride);
  public abstract int getStride();
  public abstract void setHeight(int height);
  public abstract int getHeight();
  public abstract void setWidth(int width);
  public abstract int getWidth();
}
