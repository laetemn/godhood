package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 28, 2004
 * Time: 10:52:43 AM
 * To change this template use Options | File Templates.
 */
public interface RoomStore extends RoomFactory
{
    /**
     * Stores the specified room into the specified name, the name can be used to
     * then retrieve the room
     * @param name the unique name of the room
     * @param store the room to store
     */
    void storeRoom(String name, Room store) throws IOException;

    /**
     * indicates whether a room with the specified name exists within the store
     * @param name the unique name of the room
     * @return whether this room exists
     */
    boolean isStored(String name);
}
