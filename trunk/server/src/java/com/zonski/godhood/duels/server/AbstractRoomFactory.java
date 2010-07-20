package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 28, 2004
 * Time: 1:29:27 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractRoomFactory implements RoomFactory
{
    public void writeRoom(String name, OutputStream outs) throws RoomCreationException
    {
        try
        {
            DataOutputStream dos = new DataOutputStream(outs);
            Room.store(getRoom(name), dos);
        }catch(IOException ex){
            throw new RoomCreationException(name, "couldn't write room", ex);
        }
    }
}
