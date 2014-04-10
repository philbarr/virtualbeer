package com.simplyapped.virtualbeer;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.simplyapped.libgdx.ext.actions.ExecuteAction;

public class MenuStage extends Stage
{

  private static final float DURATION = 0.5f;
  public static final String LIGHTSTRIPOFF = "lightstripoff";
  public static final String LIGHTSTRIPRED = "lightstripred";
  public static final String LIGHTSTRIPAMBER = "lightstripamber";
  public static final String LIGHTSTRIPGREEN = "lightstripgreen";

  private static final Interpolation INTERPOLATION = Interpolation.swing;
  private static final String PREFERENCE_STORE_VIRTUAL_BEER = "VirtualBeer";
  private static final String PREFERENCE_SHOW_INSTRUCTIONS = "showInstructions";

  private static final String BUTTONPANEL = "buttonpanel";
  private static final String BUTTON_MENU = "menu";
  private static final String BUTTON_FLASH = "flash";
  private static final String BUTTON_INSTRUCTIONS = "instructions";
  private static final String BUTTON_EXIT = "exit";
  private static final String BUTTON_CAMERA_LARGE = "camera_large";
  private static final String BUTTON_CAMERA_SMALL = "camera_small";

  private final static String INSTRUCTIONS_1 = "instructionsone";
  private final static String INSTRUCTIONS_2 = "instructionstwo";
  private final static String INSTRUCTIONS_3_FLASH = "instructionsthreeflash";
  private final static String INSTRUCTIONS_3_NO_FLASH = "instructionsthreenoflash";
  private final static String INSTRUCTIONS_4 = "instructionsfour";
  private final static String[] INSTRUCTIONS_NO_FLASH_LIST = new String[] { INSTRUCTIONS_1, INSTRUCTIONS_2, INSTRUCTIONS_3_NO_FLASH, INSTRUCTIONS_4 };
  private final static String[] INSTRUCTIONS_FLASH_LIST = new String[] { INSTRUCTIONS_1, INSTRUCTIONS_2, INSTRUCTIONS_3_FLASH, INSTRUCTIONS_4 };

  enum Action
  {
    FLASH, CAMERA, MENU, SHOW_INSTRUCTIONS, EXIT
  }

  class MenuButtonListener extends ClickListener
  {
    private Action action;

    public MenuButtonListener(Action action)
    {
      this.action = action;
    }

    @Override
    public void clicked(InputEvent event, float x, float y)
    {
      switch (action)
      {
        case FLASH:
          boolean flashButtonClicked = listener.flashButtonClicked();
          ((Button) event.getListenerActor()).setChecked(flashButtonClicked);
          break;
        case CAMERA:
          showFullMenu(false);
          listener.cameraButtonClicked();
          break;
        case MENU:
          showHideFullMenu();
          break;
        case SHOW_INSTRUCTIONS:
          showHideFullMenu();
          startShowingInstructions();
          break;
        case EXIT:
          if (listener != null)
          {
            listener.quit();
          }
          break;
      }
      event.handle();
    }
  }

  private boolean isFullMenuShown;
  private Skin skin = new Skin(Gdx.files.internal("data/menu/beermenu.json"));

  private Table table;
  private Group lightGroup;
  private Group instructionsGroup;

  private MenuStageListener listener;
  private int instructionToDisplay = -1; // -1 means don't show instructions
  private String[] instructionsList = INSTRUCTIONS_FLASH_LIST;
  private float panelHeight;
  private boolean hasFlash;
  private Image spinner;

  public MenuStage(float width, float height, boolean keepAspect, MenuStageListener listener, boolean hasFlash)
  {
    super(width, height, keepAspect);
    float gutterWidth = (width-Gdx.graphics.getWidth())/2;
    this.listener = listener;
    this.hasFlash = hasFlash;

    panelHeight = (height - this.getGutterHeight() * 2) / 6;
    float panelWidth = width - this.getGutterWidth() * 2;
    float panelPadding = panelHeight / 25;
    float buttonMargin = panelWidth / 25;
    float buttonWidthTopRow = (panelWidth - buttonMargin * 4 - panelPadding * 2) / 3;
    float buttonWidthBottomRow = (panelWidth - buttonMargin * 3 - panelPadding * 2) / 2;
    float buttonHeight = panelHeight / 1 - buttonMargin * 2 - panelPadding * 2;

    // instructions group
    instructionsGroup = new Group();
    for (String instruction : INSTRUCTIONS_FLASH_LIST)
    {
      createInstructionImage(width, height, instruction);
    }
    createInstructionImage(width, height, INSTRUCTIONS_3_NO_FLASH);

    // light group
    float lightGroupWidth = buttonWidthTopRow * 3 + buttonMargin * 2 - gutterWidth * 2;
    lightGroup = new Group();
    lightGroup.setPosition(panelPadding + buttonMargin + gutterWidth, panelHeight);
    float lightGroupHeight = createLight(LIGHTSTRIPOFF, lightGroupWidth);
    createLight(LIGHTSTRIPRED, lightGroupWidth);
    createLight(LIGHTSTRIPAMBER, lightGroupWidth);
    createLight(LIGHTSTRIPGREEN, lightGroupWidth);
    lightGroup.setSize(panelWidth, lightGroupHeight);

    instructionsList = hasFlash ? INSTRUCTIONS_FLASH_LIST : INSTRUCTIONS_NO_FLASH_LIST;
    if (hasFlash)
    {
      // table with a small camera button
      table = createTable(panelWidth, panelHeight * 3);
      table.addActor(createButton(Action.FLASH, BUTTON_FLASH, panelPadding + buttonMargin * 1 + buttonWidthTopRow * 0, panelPadding + buttonMargin
          + panelHeight*2, buttonWidthTopRow, buttonHeight));
      table.addActor(createButton(Action.CAMERA, BUTTON_CAMERA_SMALL, panelPadding + buttonMargin * 2 + buttonWidthTopRow * 1, panelPadding + buttonMargin
          + panelHeight*2, buttonWidthTopRow, buttonHeight));
    }
    else
    {
      // table with a large camera button
      table = createTable(panelWidth, panelHeight * 3);
      table.addActor(createButton(Action.CAMERA, BUTTON_CAMERA_LARGE, panelPadding + buttonMargin * 1 + buttonWidthTopRow * 0, panelPadding + buttonMargin
          + panelHeight * 2, buttonWidthTopRow * 2 + buttonMargin, buttonHeight));
    }
    table.addActor(createButton(Action.MENU, BUTTON_MENU, panelPadding + buttonMargin * 3 + buttonWidthTopRow * 2, panelPadding + buttonMargin 
        + panelHeight * 2,  buttonWidthTopRow, buttonHeight));
    table.addActor(createButton(Action.SHOW_INSTRUCTIONS, BUTTON_INSTRUCTIONS, panelPadding + buttonMargin * 1 + buttonWidthBottomRow * 0, panelPadding
        + buttonMargin+ panelHeight, buttonWidthBottomRow, buttonHeight));
    table.addActor(createButton(Action.EXIT, BUTTON_EXIT, panelPadding + buttonMargin * 2 + buttonWidthBottomRow * 1, panelPadding + buttonMargin+ panelHeight,
        buttonWidthBottomRow, buttonHeight));

    spinner = new Image(skin.getDrawable("spinner"));
    spinner.setSize(width * 0.7f, width*0.7f);//make it sqaure
    spinner.setPosition(Gdx.graphics.getWidth()/2f-spinner.getWidth()/2f, Gdx.graphics.getHeight() /2f - spinner.getHeight()/2f);
    spinner.setOrigin(spinner.getHeight()/2f, spinner.getWidth()/2f);
    spinner.addAction(repeat(RepeatAction.FOREVER, sequence( delay(0.1f), ( rotateBy(-30)))));
    
    this.addActor(lightGroup);
    this.addActor(table);
    this.addActor(instructionsGroup);
    this.addActor(spinner);
    setLight(LIGHTSTRIPOFF);
    
    this.addListener(new ClickListener() {@Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
    {
      if (!event.isHandled())
      {
        MenuStage.this.listener.focus();
      }
      return true;
    }});

    Preferences preferences = Gdx.app.getPreferences(PREFERENCE_STORE_VIRTUAL_BEER);
    if (preferences.getBoolean(PREFERENCE_SHOW_INSTRUCTIONS, true))
    {
      startShowingInstructions();
    }
    preferences.putBoolean(PREFERENCE_SHOW_INSTRUCTIONS, false);
    preferences.flush();
  }

  public void showLoadingSpinner(boolean show)
  {
    spinner.setVisible(show);
  }

  private void showFullMenu(boolean show)
  {
    if (isFullMenuShown != show)
    {
      showHideFullMenu();
    }
  }

  private void showHideFullMenu()
  {
    float amountY = table.getHeight() / 3;
    if (isFullMenuShown)
    {
      table.addAction(moveBy(0, -amountY, DURATION, INTERPOLATION));
    }
    else
    {
      removeCurrentInstruction();
      instructionToDisplay = -1;
      table.addAction(moveBy(0, amountY, DURATION, INTERPOLATION));
    }
    isFullMenuShown = !isFullMenuShown;
  }

  private void createInstructionImage(float width, float height, String instruction)
  {
    Image image = new Image(skin.getDrawable(instruction));
    image.setName(instruction);
    image.setSize((float) (width * 0.8), (float) (width * 0.8));
    image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
    image.setPosition(-width + width / 2 - image.getWidth() / 2, height / 2 - image.getHeight() / 2);
    image.addListener(new ClickListener()
    {
      @Override
      public void clicked(InputEvent event, float x, float y)
      {
        nextInstruction();
      }
    });
    image.setVisible(false);
    instructionsGroup.addActor(image);
  }

  private void startShowingInstructions()
  {
    instructionToDisplay = 0;
    lightGroup.addAction(fadeOut(DURATION));
    nextInstruction();
  }

  public boolean isShowingInstructions()
  {
    return instructionToDisplay >= 0;
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

  private Table createTable(float width, float height)
  {
    Table table = new Table();
    table.setPosition(0, -height * 2 / 3);
    table.setBackground(skin.getDrawable(BUTTONPANEL));
    table.setSize(width, height);
    return table;
  }

  private Button createButton(Action action, String style, float x, float y, float width, float height)
  {
    Button button = new Button(skin, style);
    button.addListener(new MenuButtonListener(action));
    button.setPosition(x, y);
    button.setSize(width, height);
    button.setName(style);
    return button;
  }

  public void setIsTrackingTarget(boolean isTracking)
  {
    Button btn;
    if (hasFlash)
    {
      btn = (Button) table.findActor(BUTTON_CAMERA_SMALL);
    }
    else
    {
      btn = (Button) table.findActor(BUTTON_CAMERA_LARGE);
    }
    if (btn != null)
    {
      btn.setChecked(isTracking);
    }
    lightGroup.setVisible(!isTracking);
  }

  public void nextInstruction()
  {
    // catch edge case where tap event is fired but no instructions are to be
    // shown
    if (!isShowingInstructions())
    {
      return;
    }

    // catch edge case where double tap attempts to start lots of animations at
    // once
    for (Actor actor : instructionsGroup.getChildren())
    {
      if (actor.getActions() != null && actor.getActions().size > 0)
      {
        return;
      }
    }

    instructionsGroup.setVisible(true);

    // handle the previous instruction
    removeCurrentInstruction();

    // once all instructions are drawn turn them off
    if (instructionToDisplay >= instructionsList.length)
    {
      lightGroup.addAction(fadeIn(DURATION));
      instructionToDisplay = -1;
    }
    else
    {
      // handle the latest instruction if there is one
      Actor current = instructionsGroup.findActor(instructionsList[instructionToDisplay++]);
      current.setVisible(true);
      current.addAction(sequence(delay(DURATION), moveTo(Gdx.graphics.getWidth() / 2 - current.getWidth() / 2, current.getY(), 1, INTERPOLATION)));
    }
  }

  private void removeCurrentInstruction()
  {
    if (instructionToDisplay > 0)
    {
      Actor prev = instructionsGroup.findActor(instructionsList[instructionToDisplay - 1]);
      prev.addAction(sequence(moveTo(-Gdx.graphics.getWidth() + (Gdx.graphics.getWidth() - prev.getWidth()) / 2, prev.getY(), 1, INTERPOLATION),
          new ExecuteAction(new ExecuteAction.Delegate()
          {
            @Override
            public boolean execute(Actor actor, float delta)
            {
              actor.setVisible(false);
              return true;
            }
          })));
    }
  }
}
