package com.zonski.godhood.duels.wireless;

import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 11, 2003
 * Time: 3:30:48 PM
 * To change this template use Options | File Templates.
 */
public final class PlayerMind extends Action
{
    private DuelsCanvas canvas;

    public PlayerMind(DuelsCanvas canvas, Entity entity)
    {
        this.canvas = canvas;
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return this.entity;
    }

    public int update()
    {
        try
        {
            synchronized(this)
            {
                canvas.showActions(this, this.entity);
                this.wait();
            }
        }catch(Exception ex){
            // do nothing
            ex.printStackTrace();
        }
        return 0;
    }

    public int getTotalSteps()
    {
        return -1;
    }

    public String getName()
    {
        return null;
    }

    public boolean isCompleted()
    {
        return false;
    }

    public final Action copy(Entity entity)
    {
        return new PlayerMind(this.canvas, entity);
    }

    public String getAvailability(Entity entity)
    {
        return null;
    }

    public void setAction(Action action)
    {
        this.entity.doing = action;
        synchronized(this)
        {
            this.notify();
        }
    }
}
