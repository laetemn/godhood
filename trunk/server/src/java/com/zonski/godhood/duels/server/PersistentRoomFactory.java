package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 28, 2004
 * Time: 1:26:47 PM
 * To change this template use Options | File Templates.
 */
public class PersistentRoomFactory extends AbstractRoomFactory
{
    private RoomStore persistentSource;
    private RoomFactory newRoomSource;

    public PersistentRoomFactory(RoomStore persistentSource, RoomFactory newRoomSource)
    {
        this.persistentSource = persistentSource;
        this.newRoomSource = newRoomSource;
    }

    public Room getRoom(String name)
        throws RoomCreationException
    {
        Room room;
        if(this.persistentSource.isStored(name))
        {
            room = this.persistentSource.getRoom(name);
        }else{
            room = this.newRoomSource.getRoom(name);
        }
        return room;
    }
}
