package com.simplyapped.virtualbeer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.simplyapped.libgdx.ext.ui.OSDialog;
import com.simplyapped.libgdx.ext.vuforia.VuforiaImageTargetBuilder;
import com.simplyapped.libgdx.ext.vuforia.VuforiaListener;
import com.simplyapped.libgdx.ext.vuforia.VuforiaSession;
import com.simplyapped.libgdx.ext.vuforia.VuforiaState;
import com.simplyapped.libgdx.ext.vuforia.VuforiaTrackableResult;
import com.simplyapped.libgdx.ext.vuforia.VuforiaTrackableSource;

public class VirtualBeerGame implements ApplicationListener, VuforiaListener {
	private static final String DATA = "data/cube.g3db";
	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;
	public AssetManager assets;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public Environment lights;
	private VuforiaSession vuforia;
	private Model model;
	private ModelInstance instance;
	private Environment environment;
	private Stage stage;
	private Label boxPoseMatrix;
	private Label projectionMatrix;
	private VuforiaImageTargetBuilder builder;
	
	private boolean isScanning;
	private boolean isBuilding;
	private OSDialog dialog;
	private boolean flashon;
	private boolean focuson;
	private int idx;
	private boolean stopall;
	private DirectionalLight light;
	private int fieldOfView = 90;
	
	@Override
	public void create() {
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, spriteBatch);
		stage.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				isBuilding = builder.build("beer" + idx++, Gdx.graphics.getWidth()/2);
			}
		});
		Skin skin = new Skin(Gdx.files.internal("data/modeltrial.json"));
		
		assets = new AssetManager(); 
        assets.load(DATA, Model.class);
        assets.finishLoading();
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		cam = new PerspectiveCamera(fieldOfView, width, height);
		cam.position.set(0f, 0f, 0f);
		cam.lookAt(0, 0, 10000);
		cam.near = 10f;
		cam.far = 1000f;
		cam.update();
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, 10f);
		environment.add(light);

		ModelBuilder modelBuilder = new ModelBuilder();
		
		float lineWidth = 10f;
		float lineLength = 100f;
		instances.add(new ModelInstance(modelBuilder.createBox(lineLength, lineWidth, lineWidth,
						new Material(ColorAttribute.createDiffuse(Color.RED)),
						Usage.Position | Usage.Normal), lineLength/2,0f,00f));
		instances.add(new ModelInstance(modelBuilder.createBox(lineWidth, lineLength, lineWidth,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal), 0f,lineLength/2,0f));
		instances.add(new ModelInstance(modelBuilder.createBox(lineWidth, lineWidth, lineLength,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)),
				Usage.Position | Usage.Normal), 0f,0f,lineLength/2));
		
		boxPoseMatrix = new Label("Box Pose...",skin,"progress");
		boxPoseMatrix.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
		boxPoseMatrix.setPosition(0, Gdx.graphics.getHeight() /2);
		
		projectionMatrix = new Label("Projection...",skin,"progress");
		projectionMatrix.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2);
		projectionMatrix.setPosition(0,0);
		
		stage.addActor(boxPoseMatrix);
		stage.addActor(projectionMatrix);
		
		Model building = assets.get(DATA, Model.class);
        instance = new ModelInstance(building);
		instance.transform.setToTranslation(0, 0, 50);
		instance.transform.scl(50f);
		vuforia.onResize(width, height);
		vuforia.setListener(this);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render() {
		int renderables = 0;
		if (vuforia.isInited()) {
			
			
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			
			VuforiaState state = vuforia.beginRendering();
			vuforia.drawVideoBackground();
	//		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //TODO STILL NEEDED?!! Renderer was adjusting glViewport
			renderables = state.getNumTrackableResults();
			for (int i = 0; i < renderables ; i++) {
				// Get the trackable:
	            VuforiaTrackableResult trackableResult = state.getTrackableResult(i);
	            cam.projection.set(vuforia.getProjectionMatrix());
	            cam.view.set(trackableResult.getPose());
	            cam.combined.set(cam.projection);
	    		Matrix4.mul(cam.combined.val, cam.view.val);
	    		
	            modelBatch.begin(cam);
//	            for (ModelInstance instance : instances) {
//	            	instance.transform=trackableResult.getPose();
//	            	instance.transform.val[14] = 300;
//	            	instance.calculateTransforms();
//				}
	            
	            modelBatch.render(instances, environment);
	            modelBatch.render(instance, environment);
	            modelBatch.end();
//	            modelInstance.transform.scale(0, 0, -90f);
//	            instance.transform.translate(0, 0, 3f);
//	            instance.transform.scale(13f, 13f, 13f);
//	            boxPoseMatrix.setText(mToS(instance.transform));
//	            cam.update(false, vuforia.getProjectionMatrix());
//	            cam.combined.set(vuforia.getProjectionMatrix());
	            
//	            projectionMatrix.setText(mToS(vuforia.getProjectionMatrix()) + "fov: " + vuforia.getFieldOfView());
//	            cam.fieldOfView = (float) vuforia.getFieldOfView();
//	            trackableResult.setCameraPositionAndDirection(cam);
//	            cam.update();
//	            light.direction.set(cam.direction);
	            
			}

			vuforia.endRendering();
			stage.act();
			stage.draw();
		}
//		if (builder != null)
//		boxPoseMatrix.setText(isScanning + ":" + isBuilding + ":" + builder.frameQuality() + ":" + renderables);
//		boxPoseMatrix.setPosition(0, 15 );
	}

	private String mToS(Matrix4 transform) {
		return  
				String.format("%.2g",transform.val[Matrix4.M00]) + "|" + String.format("%.2g",transform.val[Matrix4.M01]) + "|" + String.format("%.2g",transform.val[Matrix4.M02]) + "|" + String.format("%.2g",transform.val[Matrix4.M03]) + "])\n" + 
				String.format("%.2g",transform.val[Matrix4.M10]) + "|" + String.format("%.2g",transform.val[Matrix4.M11]) + "|"	+ String.format("%.2g",transform.val[Matrix4.M12]) + "|" + String.format("%.2g",transform.val[Matrix4.M13]) + "])\n" + 
				String.format("%.2g",transform.val[Matrix4.M20]) + "|" + String.format("%.2g",transform.val[Matrix4.M21]) + "|" + String.format("%.2g",transform.val[Matrix4.M22]) + "|" + String.format("%.2g",transform.val[Matrix4.M23]) + "])\n" + 
				String.format("%.2g",transform.val[Matrix4.M30]) + "|" + String.format("%.2g",transform.val[Matrix4.M31]) + "|" + String.format("%.2g",transform.val[Matrix4.M32]) + "|" + String.format("%.2g",transform.val[Matrix4.M33]) + "])\n";
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		
		vuforia.stop();
		try {
			assets.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resume() {
		vuforia.onResume();
	}

	public void resize(int width, int height) {
//		float aspectRatio = (float) width / (float) height;
//        cam = new PerspectiveCamera(67, 2f * aspectRatio, 2f);

		vuforia.onResize(width, height);
	}

	public void pause() {
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
		if (vuforia.isInited() && !stopall)
		{
			if (builder == null)
			{
				builder = vuforia.getTargetBuilder();
				focuson = vuforia.setAutoFocus(true);
				flashon = vuforia.setFlash(true);
			}
			if (!isScanning && !isBuilding)
			{
				isScanning =  builder.startScan();
			}
			else if (isScanning && isBuilding)
			{
				
				VuforiaTrackableSource trackableSource = builder.getTrackableSource();
				if (trackableSource != null)
				{
					vuforia.createTrackable(trackableSource);
					dialog.showShortToast("worked");
					stopall = true;
					vuforia.startTrackers();
					isBuilding = false;
					isScanning = false;
				}
				else
				{
					dialog.showShortToast("didn't work!");
				}
			}
		}
	}

	public void setDialog(OSDialog dialog) {
		this.dialog = dialog;
	}
}
