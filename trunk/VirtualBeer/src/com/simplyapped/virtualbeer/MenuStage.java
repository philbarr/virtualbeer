package com.simplyapped.virtualbeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MenuStage extends Stage {

	private Skin skin = new Skin(Gdx.files.internal("data/beermenu.json"));
	
	public MenuStage() {

	}
}
