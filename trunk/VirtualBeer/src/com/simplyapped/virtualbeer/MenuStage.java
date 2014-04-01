package com.simplyapped.virtualbeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;

public class MenuStage extends Stage {
	private static final String BUTTONPANEL = "buttonpanel";
  public static final String LIGHTSTRIPOFF = "lightstripoff";
	public static final String LIGHTSTRIPRED = "lightstripred";
	public static final String LIGHTSTRIPAMBER = "lightstripamber";
	public static final String LIGHTSTRIPGREEN = "lightstripgreen";
  private static final String BUTTON_CAMERA_LARGE = "camera_large";
  private static final String BUTTON_CAMERA_SMALL = "camera_small";

  enum Action{
		FLASH,
		CAMERA,
		MENU,
		DIRECTIONS
	}
  
	class MenuButtonListener extends ClickListener {
		private Action action;
		public MenuButtonListener(Action action) {
			this.action = action;
		}
		@Override
		public void clicked(InputEvent event, float x, float y) {
			switch(action){
				case FLASH:
					((Button)event.getListenerActor()).setChecked(listener.flashButtonClicked());
					break;
				case CAMERA:
					listener.cameraButtonClicked();
					break;
				case MENU:
//					((Button)event.getListenerActor()).
					break;
				case DIRECTIONS:
					break;
			}
		}
	}

	private Skin skin = new Skin(Gdx.files.internal("data/menu/beermenu.json"));

	private Table tableCameraSmall;
	private Table tableCameraLarge;
	private Group lightGroup;
	
	private boolean hasFlash;
	private MenuStageListener listener;

	public MenuStage(float width, float height, boolean keepAspect, MenuStageListener listener) {
		super(width, height, keepAspect);
		this.listener = listener;
		
		float panelHeight = (height - this.getGutterHeight() * 2)/6;
		float panelWidth = width - this.getGutterWidth() * 2;
		float panelPadding = panelHeight / 25;
		float buttonMargin = panelWidth / 25;
		float buttonWidth = (panelWidth - buttonMargin * 4 - panelPadding * 2) / 3 ;
		float buttonHeight = panelHeight / 1 - buttonMargin * 2 - panelPadding * 2;

		// light group
		float lightGroupWidth = buttonWidth * 3 + buttonMargin * 2;
		lightGroup = new Group();
		lightGroup.setPosition(panelPadding + buttonMargin , panelHeight);
		float lightGroupHeight = createLight(LIGHTSTRIPOFF, lightGroupWidth);
		createLight(LIGHTSTRIPRED, lightGroupWidth);
		createLight(LIGHTSTRIPAMBER, lightGroupWidth);
		createLight(LIGHTSTRIPGREEN, lightGroupWidth);
		lightGroup.setSize(panelWidth, lightGroupHeight);
		
		// table with a small camera button
		tableCameraSmall = createTable(panelWidth, panelHeight);
		tableCameraSmall.addActor(createButton(Action.FLASH, "flash"
				, panelPadding + buttonMargin * 1 + buttonWidth * 0, panelPadding + buttonMargin, buttonWidth, buttonHeight));
		tableCameraSmall.addActor(createButton(Action.CAMERA, BUTTON_CAMERA_SMALL
				, panelPadding + buttonMargin * 2 + buttonWidth * 1, panelPadding + buttonMargin, buttonWidth, buttonHeight));
		tableCameraSmall.addActor(createButton(Action.MENU, "menu"
				, panelPadding + buttonMargin * 3 + buttonWidth * 2, panelPadding + buttonMargin, buttonWidth, buttonHeight));

    // table with a large camera button
		tableCameraLarge = createTable(panelWidth, panelHeight);
		tableCameraLarge.addActor(createButton(Action.CAMERA, BUTTON_CAMERA_LARGE
				, panelPadding + buttonMargin * 1 + buttonWidth * 0, panelPadding + buttonMargin, buttonWidth * 2 + buttonMargin, buttonHeight));
		tableCameraLarge.addActor(createButton(Action.MENU, "menu"
				, panelPadding + buttonMargin * 3 + buttonWidth * 2, panelPadding + buttonMargin, buttonWidth, buttonHeight));
    
		this.addActor(tableCameraSmall);
		this.addActor(tableCameraLarge);
		this.addActor(lightGroup);

		setLight(LIGHTSTRIPOFF);
	}

  public void setLight(String lightName)
  {
    SnapshotArray<Actor> children = lightGroup.getChildren();
    Actor[] actors = children.begin();
    for (int i = 0; i < children.size; i++)
    {
      if (actors[i].getName().startsWith("light"))
      {
        if (actors[i].getName().equals(lightName))
        {
          actors[i].setVisible(true);
        }
        else
        {
          actors[i].setVisible(false);
        }
      }
    }
    children.end();
  }

  private float createLight(String lightname, float width)
  {
    Image light = new Image(skin.getDrawable(lightname));
		light.setName(lightname);
		light.setVisible(false);
		light.setWidth(width);
		lightGroup.addActor(light);
		return light.getHeight();
  }

	private Table createTable(float width, float height) {
		Table table = new Table();
		table.setPosition(0, 0);
		table.setBackground(skin.getDrawable(BUTTONPANEL));
		table.setSize(width, height);
		return table;
	}

	private Button createButton(Action action, String style, float x, float y, float width, float height) {
		Button button = new Button(skin, style);
		button.addListener(new MenuButtonListener(action));
		button.setPosition(x, y);
		button.setSize(width, height);
		button.setName(style);
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
	
	public void setIsTrackingTarget(boolean isTracking){
	  ((Button)tableCameraSmall.findActor(BUTTON_CAMERA_SMALL)).setChecked(isTracking);
	  ((Button)tableCameraLarge.findActor(BUTTON_CAMERA_LARGE)).setChecked(isTracking);
	}
}
