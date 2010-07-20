package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 11:23:29 AM
 * To change this template use Options | File Templates.
 */
public interface Session
{
    public static final int SESSION_ERROR   = -2;
    public static final int SESSION_TIMEOUT = -1;
    public static final int UNKNOWN         = 0;
    public static final int SUICIDE         = 1;
    public static final int ESCAPED         = 2;
    public static final int DIED            = 3;
    public static final int QUIT            = 4;

    /**
     * obtains the room factory
     */
    RoomFactory getRoomFactory();

    /**
     * Indicates that the user has left the specified room, potentially we will save the state of
     * this room for when they return
     * @param room the room that was left in the state in which it was left
     */
    void leftRoom(Room room);

    /**
     * Indicates that the session should be closed
     * @param room the room in which the session was terminated in the state it was terminated in
     * @param reason the reason the session is closed (see list above)
     * @param description a description of the reason for the session to be closed
     * @param score the score accumulated during this session
     */
    void close(Room room, int reason, String description, int score);
}
