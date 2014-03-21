package com.simplyapped.virtualbeer.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector2;

public class BeerShaderProvider extends DefaultShaderProvider {

	
	@Override
	protected Shader createShader(Renderable renderable) {
		BeerShader shader = new BeerShader(renderable, 
				new DefaultShader.Config(
						Gdx.files.internal("data/default.libgdx.vert").readString(),
						Gdx.files.internal("data/default.libgdx.frag").readString()));
		return shader;
	}
}
