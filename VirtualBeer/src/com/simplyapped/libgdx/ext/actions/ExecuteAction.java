package com.simplyapped.libgdx.ext.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ExecuteAction extends Action
{
  private Delegate method;

  public interface Delegate{
    boolean execute(Actor actor, float delta);
  }

  public ExecuteAction(Delegate method)
  {
    this.method = method;
  }

  @Override
  public boolean act(float delta)
  {
    if (method != null)
    {
      return method.execute(this.getActor(), delta);
    }
    return false;
  }

}
