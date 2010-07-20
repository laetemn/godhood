package com.zonski.godhood.duels.server;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 11:01:18 AM
 * To change this template use Options | File Templates.
 */
public class RoomCreationException extends Exception
{
    public RoomCreationException(String roomName)
    {
        this(roomName, null, null);
    }

    public RoomCreationException(String roomName, String description)
    {
        this(roomName, description, null);
    }

    public RoomCreationException(String roomName, String description, Throwable cause)
    {
        super("Couldn't obtain room "+roomName+(description==null?"":" because "+description), cause);
    }
}
