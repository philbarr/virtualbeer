package com.simplyapped.virtualbeer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
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

public class VirtualBeerGame implements ApplicationListener, VuforiaListener,
		AnimationListener, MenuStageListener {
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
	private boolean stopall;
	private DirectionalLight light;
	private int fieldOfView = 90;
	private Model beerModel;
	private boolean animComplete;
	private boolean isAutoFocusing;

	@Override
	public void create() {
		modelBatch = new ModelBatch();
		stage = new MenuStage(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true, this);

		assets = new AssetManager();
		assets.load(DATA, Model.class);
		assets.finishLoading();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		cam = new PerspectiveCamera(fieldOfView, width, height);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f,
				1f, 1f));
		light = new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f);
		environment.add(light);

		beerModel = assets.get(DATA, Model.class);
		instance = new ModelInstance(beerModel);

		instances.add(instance);

		if (vuforia != null) {
			vuforia.setListener(this);
			vuforia.setExtendedTracking(true);
		}

		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchMenuKey(true);
	}

	@Override
	public void render() {
		int renderables = 0;
		if ((vuforia != null) && vuforia.isInited()) {
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight());
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

			if (controller == null && beerModel.animations != null
					&& beerModel.animations.size > 0) {
				controller = new AnimationController(instance);
				controller.animate(beerModel.animations.get(0).id, 1, this, 0);
			}

			for (int i = 0; i < renderables; i++) {
				VuforiaTrackableResult trackableResult = state
						.getTrackableResult(i);
				for (ModelInstance inst : instances) {
					inst.transform.set(trackableResult.getPose());
				}
			}
			if (instances != null && instances.size > 0 && renderables > 0) {
				if (controller != null) {
					if (!animComplete) {
						controller.update(Gdx.graphics.getDeltaTime());
					}
					if (animComplete) {
						for (Node node : instance.nodes) {
							node.localTransform.setTranslation(new Vector3(0,
									0, 0));

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
			if (stage != null) {
				stage.setHasFlash(vuforia.hasFlash());
				stage.act();
				stage.draw();
			}
		}
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		if (vuforia != null)
			vuforia.stop();
		try {
			assets.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resume() {
		if (vuforia != null)
			vuforia.onResume();
	}

	public void resize(int width, int height) {
		if (vuforia != null)
			vuforia.onResize(width, height);
	}

	public void pause() {
		if (vuforia != null)
			vuforia.onPause();
	}

	public VuforiaSession getVuforia() {
		return vuforia;
	}

	public void setVuforia(VuforiaSession vuforia) {
		this.vuforia = vuforia;
	}

	@Override
	public void onUpdate(VuforiaState state) {
		if ((vuforia != null) && vuforia.isInited() && !stopall) {
			if (builder == null) {
				builder = vuforia.getTargetBuilder();
			}
			if (!isScanning && !isBuilding) {
				isScanning = builder.startScan();
			} else if (isScanning && isBuilding) {

				VuforiaTrackableSource trackableSource = builder
						.getTrackableSource();
				if (trackableSource != null) {
					vuforia.createTrackable(trackableSource);
					dialog.showShortToast("worked");
					stopall = true;
					vuforia.startTrackers();
					isBuilding = false;
					isScanning = false;
				} else {
					dialog.showShortToast("didn't work!");
				}
			}
		}
	}

	public void setDialog(OSDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public void onInitDone(VuforiaException exception) {
		if (vuforia != null) {
			vuforia.setNumTrackablesHint(5);
			if (vuforia.hasAutoFocus()) {
				isAutoFocusing = vuforia.setAutoFocus(true);
			}
		}
	}

	@Override
	public void onEnd(AnimationDesc animation) {
		animComplete = true;
	}

	@Override
	public void onLoop(AnimationDesc animation) {
		animComplete = true;
	}

	@Override
	public boolean flashButtonClicked() {
		return flashstate = vuforia.setFlash(!flashstate) ? !flashstate
				: flashstate;
	}

	@Override
	public boolean cameraButtonClicked() {
		if (vuforia != null) {
			isBuilding = builder.build("beer" + idx++,
					Gdx.graphics.getWidth() / 2);
		}
		return isBuilding;
	}

	@Override
	public void doFocus() {
		if (!isAutoFocusing) {
			if (!vuforia.doFocus()) {
				dialog.showShortToast("Failed to focus");
			}
		}
	}
}
