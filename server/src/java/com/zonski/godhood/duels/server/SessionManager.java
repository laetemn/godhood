package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 11:13:41 AM
 * To change this template use Options | File Templates.
 */
public interface SessionManager
{
    /**
     * Starts a session in this manager
     * @param name the visible name of the person starting the session, it does not have to be unique
     * @return a session for the user
     */
    Session startSession(String name);
}
