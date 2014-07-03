package com.simplyapped.libgdx.ext.vuforia;

import java.nio.ByteBuffer;

public class AndroidVuforiaImage implements VuforiaImage
{
  private ByteBuffer bytes;
  private int width;
  private int height;
  private int stride;

  @Override
  public ByteBuffer getBytes()
  {
    return bytes;
  }

  @Override
  public void setBytes(ByteBuffer bytes)
  {
    this.bytes = bytes;
  }

  @Override
  public int getWidth()
  {
    return width;
  }

  @Override
  public void setWidth(int width)
  {
    this.width = width;
  }

  @Override
  public int getHeight()
  {
    return height;
  }

  @Override
  public void setHeight(int height)
  {
    this.height = height;
  }

  @Override
  public int getStride()
  {
    return stride;
  }

  @Override
  public void setStride(int stride)
  {
    this.stride = stride;
  }

}
