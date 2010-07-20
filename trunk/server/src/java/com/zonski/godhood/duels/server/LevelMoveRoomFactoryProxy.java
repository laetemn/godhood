package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.action.SimpleAction;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 28, 2004
 * Time: 9:04:43 AM
 * To change this template use Options | File Templates.
 * <p>
 * This room factory adds in monsters moving from a previous level to monsters coming from a new level
 */
public class LevelMoveRoomFactoryProxy extends AbstractRoomFactory
{
    private RoomFactory proxied;

    private Room from;
    private String exitName;
    private String entranceName;

    public LevelMoveRoomFactoryProxy(RoomFactory proxied)
    {
        this.proxied = proxied;
    }

    public Room getFrom()
    {
        return this.from;
    }

    public void setFrom(Room from)
    {
        this.from = from;
    }

    /**
     * Obtains the name of the exit in the "from" room that the monsters will be moved from
     * @return the exit name
     */
    public String getExitName()
    {
        return this.exitName;
    }

    public void setExitName(String exitName)
    {
        this.exitName = exitName;
    }

    /**
     * Obtains the name of the entrance in the generated room that the monsters will be moved to
     * @return the entrance name
     */
    public String getEntranceName()
    {
        return this.entranceName;
    }

    public void setEntranceName(String entranceName)
    {
        this.entranceName = entranceName;
    }

    public Room getRoom(String name) throws RoomCreationException
    {
        return getRoom(name, this.from, this.exitName, this.entranceName);
    }

    public final Room getRoom(String name, Room from, String exitName, String entranceName)
        throws RoomCreationException
    {
        Room room = this.proxied.getRoom(name);
        // find the exit
        Vector monsters;
        if(exitName != null)
        {
            monsters = from.getExiting(exitName);

            if(monsters != null)
            {

                boolean foundExit = false;
                outer: for(int x=0; x<room.width; x++)
                {
                    for(int y=0; y<room.height; y++)
                    {
                        Vector found = room.getEntitiesAt(x, y);
                        for(int i=0; i<found.size(); i++)
                        {
                            Entity exit = (Entity)found.elementAt(i);
                            if(exit.entityType == Entity.INHERIT_TYPE)
                            {
                                // we inherit actions from here!
                                Vector actions = exit.actions;
                                if(actions != null)
                                {
                                    for(int j=0; j<actions.size(); j++)
                                    {
                                        Action action = (Action)actions.elementAt(j);
                                        if(action instanceof SimpleAction)
                                        {
                                            SimpleAction simpleAction = (SimpleAction)action;
                                            if(simpleAction.getMode() == SimpleAction.EXIT)
                                            {
                                                if(simpleAction.exitName.equals(entranceName))
                                                {
                                                    // we've found it
                                                    // add in the entities that moved out of the previous room
                                                    for(int k=0; k<monsters.size(); k++)
                                                    {
                                                        Entity monster = (Entity)monsters.elementAt(k);
                                                        monster.x = (byte)x;
                                                        monster.y = (byte)y;
                                                        room.addContainedEntity(monster);
                                                    }
                                                    foundExit = true;
                                                    break outer;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(!foundExit)
                {
                    // TODO : add the monsters in randomly
                    System.out.println("Unable to find exit named "+entranceName+" from "+exitName+" in room "+name);
                }
                // clear out the monsters, we've moved now
                monsters.removeAllElements();
            }
        }else{
            monsters = null;
        }
        return room;
    }
}
