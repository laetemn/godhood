package com.zonski.godhood.duels.game;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Nov 10, 2003
 * Time: 12:19:09 PM
 * To change this template use Options | File Templates.
 * <p>
 * Listener that should be implemented by objects interested in updates
 * from the engine
 */
public interface RoomListener
{
    /**
     * Indicates that the engine has updated the specified entity doing the specified action
     * @param room the room being updated
     * @param entity the entity that was updated
     * @param action the action that was performed
     * @param messages messages for the room at this time
     */
    void entityUpdated(Room room, Entity entity, Action action, String[] messages);
}
