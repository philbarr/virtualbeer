package com.simplyapped.virtualbeer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.Array;
import com.simplyapped.libgdx.ext.vuforia.Vuforia;

public class VirtualBeerGame implements ApplicationListener {

	public Camera cam;
	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;
	public AssetManager assets;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public Environment lights;
	private Vuforia vuforia;
	private Model model;
	private ModelInstance instance;
	private Environment environment;

	@Override
	public void create() {
		modelBatch = new ModelBatch();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		cam = new PerspectiveCamera(67, width, height);
//		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 0f, 0f);
		cam.lookAt(0, 0, 10);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();
		
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, 10f));

		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createBox(5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		
		instance = new ModelInstance(model, 0f,0f,50f);
		vuforia.onCreate();
		vuforia.onResize(width, height);
	}

	@Override
	public void render() {
		if (vuforia.isInited()) {

			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glDisable(GL20.GL_CULL_FACE);
			
			
			vuforia.beginRendering();
			vuforia.drawVideoBackground();
			vuforia.endRendering();

			instance.transform.rotate(1f, 2f, 1f, 0.5f);
			
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			modelBatch.begin(cam);
			modelBatch.render(instance, environment);
			modelBatch.end();

		}
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		model.dispose();
	}

	public void resume() {
	}

	public void resize(int width, int height) {
		vuforia.onResize(width, height);
	}

	public void pause() {
	}

	public Vuforia getVuforia() {
		return vuforia;
	}

	public void setVuforia(Vuforia vuforia) {
		this.vuforia = vuforia;
	}
}