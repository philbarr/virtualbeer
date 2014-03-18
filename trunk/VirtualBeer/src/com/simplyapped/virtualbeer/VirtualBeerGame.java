package com.simplyapped.virtualbeer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
import com.simplyapped.virtualbeer.shader.BeerShaderProvider;

public class VirtualBeerGame implements ApplicationListener, VuforiaListener, AnimationListener {
	private static final String DATA = "data/beer3.g3db";
//	private static final String DATA = "data/cube.g3db";
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
	AnimationController controller = null;
	private VuforiaImageTargetBuilder builder;
	
	private boolean isScanning;
	private boolean isBuilding;
	private OSDialog dialog;
	private boolean flashstate;
	private boolean focuson;
	private int idx;
	private boolean stopall;
	private DirectionalLight light;
	private int fieldOfView = 90;
	private Model building;
	private boolean desktopInited;
	private BeerShaderProvider shaderProvider;
	private boolean animComplete;
	
	@Override
	public void create() {
		shaderProvider = new BeerShaderProvider();
		modelBatch = new ModelBatch(shaderProvider);
		spriteBatch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, spriteBatch);
		stage.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (vuforia!=null)
				isBuilding = builder.build("beer" + idx++, Gdx.graphics.getWidth()/2);
			}
		});
		stage.addListener(new ClickListener()
		{
			@Override
			public boolean keyDown(InputEvent event, int keycode)
			{
				if (keycode == Keys.MENU || keycode == Keys.BACKSPACE)
				{
					flashstate = vuforia.setFlash(!flashstate) ? !flashstate : flashstate;
					return true;
				}
				return false;
			}
		});
		Skin skin = new Skin(Gdx.files.internal("data/modeltrial.json"));
		
		assets = new AssetManager(); 
        assets.load(DATA, Model.class);
        assets.finishLoading();
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		cam = new PerspectiveCamera(fieldOfView, width, height);
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, 10f);
		environment.add(light);
		
		//createAxisBoxes();
		building = assets.get(DATA, Model.class);
        instance = new ModelInstance(building);
//		instance.transform.setToTranslation(0, 0, 50);
//		instance.transform.scl(250f);
		
		instances.add(instance);
		
		if (vuforia != null){
			vuforia.setListener(this);
			vuforia.setExtendedTracking(true);
		}
		
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchMenuKey(true);
	}

	private void createAxisBoxes() {
		ModelBuilder modelBuilder = new ModelBuilder();
		float lineWidth = 10f;
		float lineLength = 100f;
		Model xBox = modelBuilder.createBox(lineLength, lineWidth, lineWidth,
						new Material(ColorAttribute.createDiffuse(Color.RED)),
						Usage.Position | Usage.Normal);
		xBox.nodes.get(0).globalTransform.translate(lineLength/2, 0, 0);
		xBox.calculateTransforms();
		Model yBox = modelBuilder.createBox(lineWidth, lineLength, lineWidth,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		yBox.nodes.get(0).globalTransform.translate(0, lineLength/2, 0);
		yBox.calculateTransforms();
		Model zBox = modelBuilder.createBox(lineWidth, lineWidth, lineLength,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)),
				Usage.Position | Usage.Normal);
		zBox.nodes.get(0).localTransform.translate(0, 0, lineLength/2);
		zBox.calculateTransforms();
		
		modelBuilder.begin();
		modelBuilder.node("xBox", xBox).translation.add(lineLength/2, 0, 0);
		modelBuilder.node("yBox", yBox).translation.add(0, lineLength/2, 0);
		modelBuilder.node("zBox", zBox).translation.add(0, 0, lineLength/2);
		Model total = modelBuilder.end();
		
		instances.add(new ModelInstance(total));
		
		
	}

	@Override
	public void render() {
		int renderables = 0;
		if ((vuforia!=null) && vuforia.isInited()) {
			
			
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
			
			if (controller == null && building.animations != null && building.animations.size>0){
				controller = new AnimationController(instance);
				controller.animate(building.animations.get(0).id,1, this, 0);
			}
			
			for (int i = 0; i < renderables ; i++) {
	            VuforiaTrackableResult trackableResult = state.getTrackableResult(i);
	            for (ModelInstance inst : instances) {
	            	inst.transform.set(trackableResult.getPose());		
				}
			}
			if (instances != null && instances.size>0 && renderables>0)
			{
				if (controller != null){
					if (!animComplete)
					{
						controller.update(Gdx.graphics.getDeltaTime());				
					}
					if (animComplete)
					{
						for(Node node : instance.nodes)
						{
							node.localTransform.setTranslation(new Vector3(0,0,0));
							node.calculateLocalTransform();
							node.calculateTransforms(true);
						}
					}
				}
				modelBatch.begin(cam);
				modelBatch.render(instances, environment);
				modelBatch.end();
			}

			vuforia.endRendering();
			stage.act();
			stage.draw();
		}
		else
		{
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			
			if (!desktopInited)
			{
				for (Node node : building.nodes) 
				{
					if (node.id.startsWith("Point"))
					{
						PointLight light = new PointLight();
						light.position.set(node.globalTransform.val[12],node.globalTransform.val[13],node.globalTransform.val[14]);
						environment.add(light);
					}
				}
				cam.projection.set(new float[]{0.0f, -1.69032f, 0.0f, 0.0f, -2.25376f, 0.0f, 0.0f, 0.0f, 0.0f, -0.003125f, 1.002002f, 1.0f, 0.0f, 0.0f, -20.02002f, 0.0f});
				cam.combined.set(new float[]{0.0f, 		-1.69032f, 	0.0f, 		0.0f, 
											-2.25376f, 	0.0f, 		0.0f, 		0.0f, 
											0.0f, 		-0.003125f, 1.002002f, 	1.0f, 
											0.0f, 		0.0f, 		-20.02002f, 0.0f});
				cam.direction.set(new float[]{0,0,-1});
				cam.position.set(new float[]{0,0,0});
				instance.transform.set(new Matrix4(new float[]{	0.65500414f, -0.58503735f, -0.47822696f, 0.0f, 
																-0.44855505f, -0.8103584f, 0.3769852f, 0.0f, 
																-0.6080855f, -0.03241571f, -0.79320955f, 0.0f, 
																19.917683f, 18.692945f, 545.3725f, 1.0f}));

				if (controller == null && building.animations != null && building.animations.size>0)
				{
					controller = new AnimationController(instance);
					controller.animate(building.animations.get(0).id,1, this, 0);
				}
				desktopInited = true;
			}
			
			
			if (controller != null){
				if (!animComplete)
				{
					controller.update(Gdx.graphics.getDeltaTime());				
				}
				if (animComplete)
				{
					for(Node node : instance.nodes)
					{
						node.localTransform.setTranslation(new Vector3(0,0,0));
						node.calculateLocalTransform();
						node.calculateTransforms(true);
					}
				}
			}
			modelBatch.begin(cam);
			modelBatch.render(instance, environment);
			modelBatch.end();
		}
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
		if (vuforia!=null)
		vuforia.stop();
		try {
			assets.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resume() {
		if (vuforia!=null)
		vuforia.onResume();
	}

	public void resize(int width, int height) {
//		float aspectRatio = (float) width / (float) height;
//        cam = new PerspectiveCamera(67, 2f * aspectRatio, 2f);
		if (vuforia!=null)
		vuforia.onResize(width, height);
	}

	public void pause() {
		if (vuforia!=null)
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
		if ((vuforia!=null) && vuforia.isInited() && !stopall)
		{
			if (builder == null)
			{
				builder = vuforia.getTargetBuilder();
				focuson = vuforia.setAutoFocus(true);
				
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

	@Override
	public void onInitDone(VuforiaException exception) {
		if (vuforia!=null)
		vuforia.setNumTrackablesHint(5);
	}

	@Override
	public void onEnd(AnimationDesc animation) {
		animComplete = true;
	}

	@Override
	public void onLoop(AnimationDesc animation) {
		animComplete = true;
	}
}
