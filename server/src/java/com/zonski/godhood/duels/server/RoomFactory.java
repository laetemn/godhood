package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 10:45:22 AM
 * To change this template use Options | File Templates.
 * <p>
 * Interface for obtaining rooms
 */
public interface RoomFactory
{
    /**
     * Obtains a room with the specified name
     * @param name an identifier of the room, can be just about anything
     * @return a room that suits the specified name
     */
    Room getRoom(String name) throws RoomCreationException;

    /**
     * Writes the room with the specified name to the specified output stream
     * @param name the name of the room
     * @param outs the output stream to write to
     * @throws RoomCreationException if anything goes wrong
     */
    void writeRoom(String name, OutputStream outs) throws RoomCreationException;
}
