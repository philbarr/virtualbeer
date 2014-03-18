package com.simplyapped.virtualbeer.shader;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.math.Vector2;

public class BeerShader extends DefaultShader{

	public BeerShader(Renderable renderable, Config config) {
		super(renderable, config);
	}

	@Override
	public void render(Renderable renderable) {
		//custom stuff here
		super.render(renderable);
	}
	
	@Override
	public void render(Renderable renderable, Attributes combinedAttributes) {
		//custom stuff here
		super.render(renderable, combinedAttributes);
	}
}
