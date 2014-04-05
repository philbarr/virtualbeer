package com.simplyapped.virtualbeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.SnapshotArray;

public class MenuStage extends Stage {
	private static final String PREFERENCE_STORE_VIRTUAL_BEER = "VirtualBeer";
  private static final String PREFERENCE_SHOW_INSTRUCTIONS = "showInstructions";
  private static final String BUTTONPANEL = "buttonpanel";
  public static final String LIGHTSTRIPOFF = "lightstripoff";
	public static final String LIGHTSTRIPRED = "lightstripred";
	public static final String LIGHTSTRIPAMBER = "lightstripamber";
	public static final String LIGHTSTRIPGREEN = "lightstripgreen";
  private static final String BUTTON_CAMERA_LARGE = "camera_large";
  private static final String BUTTON_CAMERA_SMALL = "camera_small";
  private final static String INSTRUCTIONS_1 = "instructions_1";
  private final static String INSTRUCTIONS_2 = "instructions_2";
  private final static String INSTRUCTIONS_3_FLASH = "instructions_3_flash";
  private final static String INSTRUCTIONS_3_NO_FLASH = "instructions_3_no_flash";
  private final static String INSTRUCTIONS_4 = "instructions_4";
  private final static String[] INSTRUCTIONS_NO_FLASH_LIST = new String[]
      {
        INSTRUCTIONS_1,
        INSTRUCTIONS_2,
        INSTRUCTIONS_3_NO_FLASH,
        INSTRUCTIONS_4
      };
  private final static String[] INSTRUCTIONS_FLASH_LIST = new String[]
      {
        INSTRUCTIONS_1,
        INSTRUCTIONS_2,
        INSTRUCTIONS_3_FLASH,
        INSTRUCTIONS_4
      };
  
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
	private Group instructionsGroup;
	
	private boolean hasFlash;
	private MenuStageListener listener;
	private int instructionToDisplay = -1; // -1 means don't show instructions
	private String[] instructionsList = INSTRUCTIONS_FLASH_LIST;

	public MenuStage(float width, float height, boolean keepAspect, MenuStageListener listener) {
		super(width, height, keepAspect);
		
		Preferences preferences = Gdx.app.getPreferences(PREFERENCE_STORE_VIRTUAL_BEER);
    if (preferences.getBoolean(PREFERENCE_SHOW_INSTRUCTIONS, true))
		{
		  startShowingInstructions();
		}
		preferences.putBoolean(PREFERENCE_SHOW_INSTRUCTIONS, false);
		this.listener = listener;
		
		float panelHeight = (height - this.getGutterHeight() * 2)/6;
		float panelWidth = width - this.getGutterWidth() * 2;
		float panelPadding = panelHeight / 25;
		float buttonMargin = panelWidth / 25;
		float buttonWidth = (panelWidth - buttonMargin * 4 - panelPadding * 2) / 3 ;
		float buttonHeight = panelHeight / 1 - buttonMargin * 2 - panelPadding * 2;

		// instructions group
		instructionsGroup = new Group();
		for (String instruction : INSTRUCTIONS_FLASH_LIST)
    {
      createInstructionImage(width, height, instruction);
    }
		createInstructionImage(width, height, INSTRUCTIONS_3_NO_FLASH);
		
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

  private void createInstructionImage(float width, float height, String instruction)
  {
    Image image = new Image(skin.getDrawable(instruction));
    image.setName(instruction);
    image.setOrigin(image.getWidth()/2, image.getHeight()/2);
    image.setPosition(width/2, height/2);
    instructionsGroup.addActor(image);
  }

  private void startShowingInstructions()
  {
    instructionToDisplay = 0;
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
		instructionsList = hasFlash ? INSTRUCTIONS_FLASH_LIST : INSTRUCTIONS_NO_FLASH_LIST;
	}
	
	@Override
	public void draw() {
		this.tableCameraSmall.setVisible(hasFlash);
		this.tableCameraLarge.setVisible(!hasFlash);
		
		// handle drawing the instructions
		lightGroup.setVisible(false);
		if (instructionToDisplay >= 0)
		{
		  lightGroup.setVisible(true);
		  for (Actor actor : lightGroup.getChildren())
      {
        actor.setVisible(false);
      }
		  lightGroup.findActor(instructionsList[instructionToDisplay++]).setVisible(true);
		  
		  // once all instructions are drawn turn them off
		  if (instructionToDisplay > instructionsList.length)
		  {
		    instructionToDisplay=-1;
		  }
		}
		super.draw();
	}
	
	public void setIsTrackingTarget(boolean isTracking){
	  ((Button)tableCameraSmall.findActor(BUTTON_CAMERA_SMALL)).setChecked(isTracking);
	  ((Button)tableCameraLarge.findActor(BUTTON_CAMERA_LARGE)).setChecked(isTracking);
	  lightGroup.setVisible(!isTracking);
	}
}
