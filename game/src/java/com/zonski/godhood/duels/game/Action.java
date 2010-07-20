package com.zonski.godhood.duels.game;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Nov 5, 2003
 * Time: 10:58:23 AM
 * To change this template use Options | File Templates.
 * <p>
 * Describes what the entity is doing and the result of performing an action
 */
public abstract class Action
{
    public static final String CANNOT_SPAN = "there is not enough time";

    public static final String format(String format, String s1, String s2, String s3)
    {
        // format the message if needs be
        String message = format;
        if(format != null)
        {
            for(int i=0; i<3; i++)
            {
                int index = message.indexOf("{"+i+"}");
                if(index >= 0)
                {
                    String s;
                    switch(i)
                    {
                        case 0:
                            s = s1;
                            break;
                        case 1:
                            s = s2;
                            break;
                        case 2:
                            s = s3;
                            break;
                        default:
                            s = "?";
                    }
                    message = message.substring(0, index) + s + message.substring(index+3);
                }
            }
        }
        return message;
    }

    /**
     * Indicates whether the action is inherent to the monster or
     * has been added by performing some action
     */
    //public boolean inherent = true;

    /**
     * The entity associated with this action
     */
    public Entity entity;

    /**
     * Indicates whether the action finishes the turn for the entity or not
     * @return whether it ends the turn for the entity
     */
    public boolean endsTurn()
    {
        return false;
    }

    /**
     * updates the action by one time step
     * @return the number of units consumed by this update
     */
    public abstract int update();

    /**
     * Obtains the total number of steps that this action result will take up
     * @return the total number of steps that the action goes through, or -1 for ongoing
     */
    public abstract int getTotalSteps();

    /**
     * Obtains the name of the action
     * @return the name of the action
     */
    public abstract String getName();

    /**
     * Indictes wether the action has completed
     * @return true if it's done, false otherwise
     */
    public abstract boolean isCompleted();

    /**
     * creates a copy of this action
     * @param entity the owning entity for the copy
     * @return a copy of this action
     */
    public abstract Action copy(Entity entity);

    /**
     * Indicates whether the action can be applied to the specified entity
     * @return whether the action is allowed given the entities state, null if it is, a reason if
     * it isn't
     */
    public String getAvailability(Entity entity)
    {
        String availability;
        if(this.getTotalSteps() > entity.unitsRemaining)
        {
            availability = CANNOT_SPAN;
        }else{
            availability = null;
        }
        return availability;
    }

    /**
     * The action is the current doing action for the entity
     */
    public void started()
    {
    }

    /**
     * The action is no longer the current doing action for the entity
     */
    public void stopped()
    {

    }

    /**
     * indicates whether this action is available to the user
     * @return
     */
    public boolean isVisible()
    {
        return true;
    }

    public final Entity getParent()
    {
        return this.entity;
    }

    public final String toString()
    {
        return this.getName();
    }
}
