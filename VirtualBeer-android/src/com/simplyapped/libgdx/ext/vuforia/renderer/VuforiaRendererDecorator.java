package com.simplyapped.libgdx.ext.vuforia.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView.Renderer;
import com.badlogic.gdx.Gdx;
import com.qualcomm.vuforia.Vuforia;

public class VuforiaRendererDecorator implements Renderer
{

  private Renderer renderer;

  public VuforiaRendererDecorator(Renderer renderer)
  {
    this.renderer = renderer;
  }
  
  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config)
  {
    Gdx.app.log(VuforiaRendererDecorator.class.toString(), "ON_SURFACE_CREATED");
    Vuforia.onSurfaceCreated();
    renderer.onSurfaceCreated(gl, config);
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height)
  {
    Gdx.app.log(VuforiaRendererDecorator.class.toString(), "ON_SURFACE_CHANGED: " + width + ", " + height);
    Vuforia.onSurfaceChanged(width, height);
    renderer.onSurfaceChanged(gl, width, height);
  }

  @Override
  public void onDrawFrame(GL10 gl)
  {
    renderer.onDrawFrame(gl);
  }

}
