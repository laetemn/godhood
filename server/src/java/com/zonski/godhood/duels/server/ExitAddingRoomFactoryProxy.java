package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.action.SimpleAction;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 26, 2004
 * Time: 9:57:54 AM
 * To change this template use Options | File Templates.
 */
public class ExitAddingRoomFactoryProxy implements RoomFactory
{
    private RoomFactory proxied;

    private HashMap exits;

    private static final Random RANDOM = new Random();

    public ExitAddingRoomFactoryProxy(RoomFactory proxied)
    {
        this.proxied = proxied;
        this.exits = new HashMap();
    }

    public void addExit(String roomName, String exitName, byte type, String actionName)
    {
        ArrayList descriptions = (ArrayList)this.exits.get(roomName);
        if(descriptions == null)
        {
            descriptions = new ArrayList();
            this.exits.put(roomName, descriptions);
        }
        descriptions.add(new ExitDescription(exitName, type, actionName));
    }

    public Room getRoom(String name) throws RoomCreationException
    {
        Room room = this.proxied.getRoom(name);

        // add in the exits at random points
        ArrayList descriptions = (ArrayList)this.exits.get(name);
        if(descriptions != null)
        {
            for(int i=0; i<descriptions.size(); i++)
            {
                ExitDescription description = (ExitDescription)descriptions.get(i);
                // find an unoccupied spot in the room
                int startX = Math.abs(RANDOM.nextInt()%room.width);
                int startY = Math.abs(RANDOM.nextInt()%room.height);

                int x = startX;
                int y = startY;
                boolean done = false;
                while(!done)
                {
                    Vector entities = room.getEntitiesAt(x, y);
                    boolean ok = room.getTile(x, y) != 0 && room.getObstacle(x, y) == 0;

                    if(entities != null)
                    {
                        // check to see if there is an immovable object on the exit
                        for(int j=0; j<entities.size() && ok; j++)
                        {
                            Entity entity = (Entity)entities.get(j);
                            if((entity.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE)
                            {
                                ok = false;
                            }else if(entity.entityType == Entity.MONSTER_TYPE ||
                                    entity.entityType == Entity.ITEM_TYPE ||
                                    entity.entityType == Entity.INHERIT_TYPE){
                                // don't know what it is, don't put anything here
                                ok = false;
                            }
                        }
                    }
                    if(ok)
                    {
                        // add in the exit
                        Entity exit = EntityFactory.create(description.getType());
                        if(exit != null)
                        {
                            System.out.println("creating exit at "+x+","+y);
                            exit.x = (byte)x;
                            exit.y = (byte)y;
                            exit.actions = new Vector(1);
                            SimpleAction exitAction = new SimpleAction(exit, SimpleAction.EXIT, (byte)3, Entity.NONE_TYPE, false);
                            exitAction.name = description.getActionName();
                            exitAction.exitName = description.getName();
                            exit.actions.addElement(exitAction);
                            room.addContainedEntity(exit);
                        }else{
                            System.err.println("WARNING : no such entity type "+description.getType());
                        }
                        done = true;
                    }else{
                        x = x+1;
                        if(x >= room.width)
                        {
                            x = 0;
                            y = (y+1)%room.height;
                        }
                        if(x == startX && y == startY)
                        {
                            // there's no free spaces
                            System.err.println("WARNING : no free spaces for exits in room!");
                            done = true;
                        }
                    }
                }
            }
        }

        return room;
    }

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

    private final class ExitDescription
    {
        private String name;
        private byte type;
        private String actionName;

        public ExitDescription(String name, byte type, String actionName)
        {
            this.name = name;
            this.type = type;
            this.actionName = actionName;
        }

        public String getName()
        {
            return this.name;
        }

        public byte getType()
        {
            return this.type;
        }

        public String getActionName()
        {
            return this.actionName;
        }
    }
}
