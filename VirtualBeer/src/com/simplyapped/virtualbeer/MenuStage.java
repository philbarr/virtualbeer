package com.simplyapped.virtualbeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.simplyapped.virtualbeer.MenuStage.MenuButtonListener.Action;

public class MenuStage extends Stage {
	static class MenuButtonListener extends ClickListener {
		private Action action;
		enum Action{
			FLASH,
			CAMERA,
			MENU,
			DIRECTIONS
		}
		public MenuButtonListener(Action action) {
			this.action = action;
		}
		@Override
		public void clicked(InputEvent event, float x, float y) {
			switch(action){
				case FLASH:
					break;
				case CAMERA:
					break;
				case MENU:
					break;
				case DIRECTIONS:
					break;
			}
		}
	}

	private Skin skin = new Skin(Gdx.files.internal("data/menu/beermenu.json"));

	private Table tableCameraSmall;
	private Table tableCameraLarge;

	private boolean hasFlash;

	public MenuStage(float width, float height, boolean keepAspect) {
		super(width, height, keepAspect);
		
		float panelHeight = Gdx.graphics.getHeight()/6;
		float panelWidth = Gdx.graphics.getWidth();
		float panelPadding = panelHeight / 25;
		float buttonMargin = panelWidth / 25;
		float buttonWidth = (panelWidth - buttonMargin * 4 - panelPadding * 2) / 3 ;
		float buttonHeight = panelHeight / 1 - buttonMargin * 2 - panelPadding * 2;
		
		tableCameraSmall = createTable(panelWidth, panelHeight);
		tableCameraSmall.addActor(createButton(Action.FLASH, "flash"
				, panelPadding + buttonMargin * 1 + buttonWidth * 0, panelPadding + buttonMargin, buttonWidth, buttonHeight));
		tableCameraSmall.addActor(createButton(Action.CAMERA, "camera_small"
				, panelPadding + buttonMargin * 2 + buttonWidth * 1, panelPadding + buttonMargin, buttonWidth, buttonHeight));
		tableCameraSmall.addActor(createButton(Action.MENU, "menu"
				, panelPadding + buttonMargin * 3 + buttonWidth * 2, panelPadding + buttonMargin, buttonWidth, buttonHeight));
		
		tableCameraLarge = createTable(panelWidth, panelHeight);
		tableCameraLarge.addActor(createButton(Action.CAMERA, "camera_large"
				, panelPadding + buttonMargin * 1 + buttonWidth * 0, panelPadding + buttonMargin, buttonWidth * 2 + buttonMargin, buttonHeight));
		tableCameraLarge.addActor(createButton(Action.MENU, "menu"
				, panelPadding + buttonMargin * 3 + buttonWidth * 2, panelPadding + buttonMargin, buttonWidth, buttonHeight));
		
		this.addActor(tableCameraSmall);
		this.addActor(tableCameraLarge);
	}

	private Table createTable(float width, float height) {
		Table table = new Table();
		table.setPosition(0, 0);
		table.setBackground(skin.getDrawable("buttonpanel"));
		table.setSize(width, height);
		return table;
	}

	private Button createButton(Action action, String style, float x, float y, float width, float height) {
		Button button = new Button(skin, style);
		button.addListener(new MenuButtonListener(action));
		button.setPosition(x, y);
		button.setSize(width, height);
		return button;
	}

	public void setHasFlash(boolean hasFlash) {
		this.hasFlash = hasFlash;
	}
	
	@Override
	public void draw() {
		this.tableCameraSmall.setVisible(hasFlash);
		this.tableCameraLarge.setVisible(!hasFlash);
		super.draw();
	}
}
