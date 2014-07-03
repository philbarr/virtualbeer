package com.simplyapped.libgdx.ext.vuforia;

import java.nio.ByteBuffer;
import com.qualcomm.vuforia.Frame;
import com.qualcomm.vuforia.Image;
import com.qualcomm.vuforia.PIXEL_FORMAT;

public class AndroidVuforiaFrame implements VuforiaFrame
{
  private Frame frame;

  public AndroidVuforiaFrame(Frame frame)
  {
    this.frame = frame;
  }
  
  @Override
  public VuforiaImage getImage()
  {
    Image imageRGB565 = null;
          
    for (int i = 0; i < frame.getNumImages(); ++i) {
        Image image = frame.getImage(i);
        if (image.getFormat() == PIXEL_FORMAT.RGB565) {
            imageRGB565 = image;
            break;
        }
    }
          
    if (imageRGB565 != null) {
        VuforiaImage i = new AndroidVuforiaImage();
        i.setBytes(imageRGB565.getPixels());
        int imageWidth = imageRGB565.getWidth();
        int imageHeight = imageRGB565.getHeight();
        int stride = imageRGB565.getStride();
        i.setWidth(imageRGB565.getWidth());
        i.setHeight(imageRGB565.getHeight());
        i.setStride(imageRGB565.getStride());
        return i;
    }
    return null;
  }

}
