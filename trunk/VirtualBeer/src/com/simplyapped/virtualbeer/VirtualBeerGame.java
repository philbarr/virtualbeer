package com.simplyapped.virtualbeer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.simplyapped.libgdx.ext.ui.OSDialog;
import com.simplyapped.libgdx.ext.vuforia.VuforiaException;
import com.simplyapped.libgdx.ext.vuforia.VuforiaImageTargetBuilder;
import com.simplyapped.libgdx.ext.vuforia.VuforiaListener;
import com.simplyapped.libgdx.ext.vuforia.VuforiaSession;
import com.simplyapped.libgdx.ext.vuforia.VuforiaState;
import com.simplyapped.libgdx.ext.vuforia.VuforiaTrackableResult;
import com.simplyapped.libgdx.ext.vuforia.VuforiaTrackableSource;

public class VirtualBeerGame implements ApplicationListener, VuforiaListener, AnimationListener, MenuStageListener
{
  private static final String DATA = "data/beer.g3db";
  public PerspectiveCamera cam;
  public ModelBatch modelBatch;
  public AssetManager assets;
  public Array<ModelInstance> instances = new Array<ModelInstance>();
  public Environment lights;
  private VuforiaSession vuforia;
  private ModelInstance instance;
  private Environment environment;
  private MenuStage stage;
  AnimationController controller = null;
  private VuforiaImageTargetBuilder builder;

  private boolean isScanning;
  private boolean isBuilding;
  private OSDialog dialog;
  private boolean flashstate;
  private int idx;
  private boolean isTrackingTarget;
  private DirectionalLight light;
  private int fieldOfView = 90;
  private Model beerModel;
  private boolean animComplete;
  
  @Override
  public void create()
  {

    assets = new AssetManager();
    assets.load(DATA, Model.class);

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    
    modelBatch = new ModelBatch();
    stage = new MenuStage(width, height, true, this, vuforia != null && vuforia.hasFlash());
    cam = new PerspectiveCamera(fieldOfView, vuforia.getWidth(), vuforia.getHeight());

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
    light = new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f);
    environment.add(light);

    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchMenuKey(true);

    stage.addListener(new ClickListener()
    {
      @Override
      public boolean keyDown(InputEvent event, int keycode)
      {
        if (keycode == Keys.BACK || keycode == Keys.BACKSPACE)
        {
          quit();
          return true;
        }
        return false;
      }
    });
    if (vuforia != null)
    {
      vuforia.setExtendedTracking(true);
      vuforia.setListener(this);
    }
  }

  @Override
  public void render()
  {
    int renderables = 0;
    boolean assetsLoaded = assets.update();
    int progressValue = vuforia.getProgressValue();
    if ((vuforia != null) && vuforia.isRunning() && assetsLoaded && progressValue == 100)
    {
      if (beerModel == null)
      {
        beerModel = assets.get(DATA, Model.class);
        instance = new ModelInstance(beerModel);
        instances.add(instance);
      }
      
      stage.showLoadingSpinner(false);
      Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

      Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
      Gdx.gl.glDisable(GL20.GL_CULL_FACE);

      VuforiaState state = vuforia.beginRendering();
      vuforia.drawVideoBackground();
      renderables = state.getNumTrackableResults();

      Matrix4 vuforiaProjection = vuforia.getProjectionMatrix();
      cam.projection.set(vuforiaProjection);
      cam.combined.set(cam.projection);
      Matrix4.mul(cam.combined.val, cam.view.val);

      if (controller == null && beerModel.animations != null && beerModel.animations.size > 0)
      {
        controller = new AnimationController(instance);
        controller.animate(beerModel.animations.get(0).id, 1, this, 0);
      }

      for (int i = 0; i < renderables; i++)
      {
        VuforiaTrackableResult trackableResult = state.getTrackableResult(i);
        for (ModelInstance inst : instances)
        {
          inst.transform.set(trackableResult.getPose());
        }
      }
      if (instances != null && instances.size > 0 && renderables > 0)
      {
        if (controller != null)
        {
          if (!animComplete)
          {
            controller.update(Gdx.graphics.getDeltaTime());
          }
          if (animComplete)
          {
            for (Node node : instance.nodes)
            {
              node.localTransform.setTranslation(new Vector3(0, 0, 0));

              node.calculateLocalTransform();
              node.calculateTransforms(true);
            }
          }
        }

        light.direction.set(cam.direction);
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
      }
      vuforia.endRendering();
    }
    if (stage != null)
    {
      try
      {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.act();
        stage.draw();
      }
      catch (Exception e)
      {
        Gdx.app.error(VirtualBeerGame.class.toString(), "Failed to draw MenuStage correctly",e);
      }
    }
  }

  @Override
  public void dispose()
  {
    modelBatch.dispose();
    try
    {
      assets.dispose();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void resume()
  {
    Gdx.app.log(VirtualBeerGame.class.toString(), "VBG RESUME");
    if (vuforia != null && vuforia.isInited())
    {
      try
      {
        vuforia.startCamera();
        vuforia.getTargetBuilder().startScan();
      }
      catch (VuforiaException e)
      {
        Gdx.app.log(VirtualBeerGame.class.toString(), "Failed to start the camera");
      }
    }
  }

  public void resize(int width, int height)
  {
  }

  public void pause()
  {
    isScanning = false;
    if (vuforia != null && vuforia.isRunning()) 
    {
      vuforia.getTargetBuilder().stopScan();
      vuforia.stopCamera();
    }
  }

  public VuforiaSession getVuforia()
  {
    return vuforia;
  }

  public void setVuforia(VuforiaSession vuforia)
  {
    this.vuforia = vuforia;
  }

  @Override
  public void onUpdate(VuforiaState state)
  {
    if ((vuforia != null) && vuforia.isInited() && vuforia.isRunning() && !isTrackingTarget)
    {
      builder = vuforia.getTargetBuilder();
      if (!isScanning && !isBuilding)
      {
        isScanning = builder.startScan();
      }
      else if (isScanning && isBuilding)
      {

        VuforiaTrackableSource trackableSource = builder.getTrackableSource();
        if (trackableSource != null)
        {
          vuforia.createTrackable(trackableSource);
          obtainedTarget();
          setIsTrackingTarget(true);
          vuforia.startTrackers();
          isBuilding = false;
        }
      }
      try
      {
        switch (builder.frameQuality())
        {
          case VuforiaImageTargetBuilder.FRAME_QUALITY_LOW:
            stage.setLight(MenuStage.LIGHTSTRIPRED);
            break;
          case VuforiaImageTargetBuilder.FRAME_QUALITY_MEDIUM:
            stage.setLight(MenuStage.LIGHTSTRIPAMBER);
            break;
          case VuforiaImageTargetBuilder.FRAME_QUALITY_HIGH:
            stage.setLight(MenuStage.LIGHTSTRIPGREEN);
            break;
          default:
            stage.setLight(MenuStage.LIGHTSTRIPOFF);
            if (builder!=null) builder.startScan();
        }
      }
      catch (Exception e)
      {
        Gdx.app.log(VirtualBeerGame.class.toString(), "Setting frame quality error", e);
      }
    }
  }

  private void obtainedTarget()
  {
    dialog.showShortToast("Got it! Step back!");
  }

  public void setDialog(OSDialog dialog)
  {
    this.dialog = dialog;
  }

  @Override
  public void onInitDone()
  {
    if (vuforia != null)
    {
      vuforia.setNumTrackablesHint(5);
     
    }
  }

  @Override
  public void onEnd(AnimationDesc animation)
  {
    animComplete = true;
  }

  @Override
  public void onLoop(AnimationDesc animation)
  {
    animComplete = true;
  }

  @Override
  public boolean flashButtonClicked()
  {
    return flashstate = vuforia.setFlash(!flashstate) ? !flashstate : flashstate;
  }

  @Override
  public void cameraButtonClicked()
  {
    if (vuforia != null)
    {
      if (isTrackingTarget)
      {
        vuforia.clearAllTrackables();
        setIsTrackingTarget(false);
      }
      else
      {
        builder = vuforia.getTargetBuilder();
        isBuilding = builder.build("beer" + idx++, Gdx.graphics.getWidth() / 2);
      }
    }
  }

  private void setIsTrackingTarget(boolean isTracking)
  {
    isTrackingTarget = isTracking;
    stage.setIsTrackingTarget(isTracking);
  }

  @Override
  public void quit()
  {
    try
    {
      vuforia.deinit();
    }
    catch (VuforiaException e)
    {
      Gdx.app.log(VirtualBeerGame.class.toString(), "Failed to deinit Vuforia", e);
    }
    Gdx.app.exit();
  }

  @Override
  public void focus()
  {
    if (vuforia != null)
    {
      vuforia.doFocusCamera();
    }
    
  }
}
