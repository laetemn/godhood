package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Feb 10, 2004
 * Time: 12:34:53 PM
 * To change this template use Options | File Templates.
 * <p>
 * Populates the room with monsters
 */
public class  MonsterRoomFactoryProxy extends AbstractRoomFactory
{
    /**
     * The room is empty of monsters
     */
    public static final int INFESTATION_NONE        = -3;

    /**
     * The room has one monster
     */
    public static final int INFESTATION_SOLITARY    = -2;

    /**
     * The room has a handful of monsters
     */
    public static final int INFESTATION_MINIMAL     = -1;

    /**
     * The room has a sustainable number of monsters (a normal amount)
     */
    public static final int INFESTATION_SUSTAINABLE = 0;

    /**
     * The room has an unsustainably large number of monsters (monsters
     * have to forage to find food outside of this area)
     */
    public static final int INFESTATION_UNSUSTAINABLE = 1;

    /**
     * The room is a meeting place or town for monsters
     * (heaps of monsters)
     */
    public static final int INFESTATION_CIVILISATION = 2;

    /**
     * The room is cram-packed with monsters
     */
    public static final int INFESTATION_MAXIMUM     = 3;

    private RoomFactory proxied;

    private HashMap roomDescriptions;

    private static final Random RANDOM = new Random();

    public MonsterRoomFactoryProxy(RoomFactory proxied)
    {
        this.proxied = proxied;
        this.roomDescriptions = new HashMap();
    }

    public void setRoomInfestation(String roomName, int infestationLevel)
    {
        RoomDescription description = getRoomDescription(roomName);
        description.setInfestationLevel(infestationLevel);
    }

    public void addMonsterDescription(String roomName, Entity monster, int probability)
    {
        RoomDescription description = getRoomDescription(roomName);
        description.addMonsterDescription(new MonsterDescription(monster, probability));
    }

    private RoomDescription getRoomDescription(String roomName)
    {
        RoomDescription description = (RoomDescription)this.roomDescriptions.get(roomName);
        if(description == null)
        {
            description = new RoomDescription();
            this.roomDescriptions.put(roomName, description);
        }
        return description;
    }

    public Room getRoom(String name)
        throws RoomCreationException
    {
        Room room = this.proxied.getRoom(name);

        if(this.roomDescriptions.containsKey(name))
        {
            RoomDescription roomDescription = getRoomDescription(name);
            if(roomDescription.getInfestationLevel() == INFESTATION_NONE)
            {
                // do nothing
            }else if(roomDescription.getInfestationLevel() == INFESTATION_SOLITARY){
                // one creature
                Entity monster = roomDescription.createMonster();
                boolean found = false;
                while(!found)
                {
                    int x = Math.abs(RANDOM.nextInt()%room.width);
                    int y = Math.abs(RANDOM.nextInt()%room.height);
                    if(room.getTile(x, y) != 0)
                    {
                        Vector entities = room.getEntitiesAt(x, y);
                        if(entities == null || entities.size() == 0)
                        {
                            found = true;
                            monster.x = (byte)x;
                            monster.y = (byte)y;
                            room.addContainedEntity(monster);
                        }
                    }
                }
            }else{
                int pmonster;
                switch(roomDescription.getInfestationLevel())
                {
                    case INFESTATION_MINIMAL:
                        pmonster = 3;
                        break;
                    case INFESTATION_SUSTAINABLE:
                        pmonster = 7;
                        break;
                    case INFESTATION_UNSUSTAINABLE:
                        pmonster =  15;
                        break;
                    case INFESTATION_CIVILISATION:
                        pmonster = 35;
                        break;
                    case INFESTATION_MAXIMUM:
                        pmonster = 100;
                        break;
                    default:
                        pmonster = 0;
                        break;
                }
                for(int x = 0; x<room.width; x++)
                {
                    for(int y = 0; y<room.height; y++)
                    {
                        Vector entities = room.getEntitiesAt(x, y);
                        boolean foundBlocker = false;
                        if(room.getTile(x, y) == 0)
                        {
                            foundBlocker = true;
                        }else if(entities != null){
                            for(int i=0; i<entities.size() && !foundBlocker; i++)
                            {
                                Entity entity = (Entity)entities.elementAt(i);
                                if((entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_BOUNCE ||
                                        (entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_MONSTER)
                                {
                                    foundBlocker = true;
                                }
                            }
                        }
                        if(!foundBlocker)
                        {
                            // can put down a monster
                            if(Math.abs(RANDOM.nextInt()%100) < pmonster)
                            {
                                Entity monster = roomDescription.createMonster();
                                monster.x = (byte)x;
                                monster.y = (byte)y;
                                room.addContainedEntity(monster);
                            }
                        }
                    }
                }
            }
        }

        return room;
    }

    private class RoomDescription
    {
        private int infestationLevel;
        private List monsterDescriptions;
        private int maxProb;

        public RoomDescription()
        {
            this.monsterDescriptions = new ArrayList();
        }

        public int getInfestationLevel()
        {
            return this.infestationLevel;
        }

        public void setInfestationLevel(int infestationLevel)
        {
            this.infestationLevel = infestationLevel;
        }

        public List getMonsterDescriptions()
        {
            return this.monsterDescriptions;
        }

        public void addMonsterDescription(MonsterDescription description)
        {
            this.monsterDescriptions.add(description);
            this.maxProb += description.getProbability();
        }

        public Entity createMonster()
        {
            int monsterIndex = Math.abs(RANDOM.nextInt()) % this.maxProb;
            for(int i=0; i<this.monsterDescriptions.size(); i++)
            {
                MonsterDescription description = (MonsterDescription)this.monsterDescriptions.get(i);
                monsterIndex -= description.probability;
                if(monsterIndex < 0)
                {
                    return description.getMonster().copy();
                }
            }
            return null;
        }
    }

    private class MonsterDescription
    {
        private int probability;
        private Entity monster;

        public MonsterDescription(Entity monster, int probability)
        {
            this.monster = monster;
            this.probability = probability;
        }

        public int getProbability()
        {
            return this.probability;
        }

        public Entity getMonster()
        {
            return this.monster;
        }
    }
}
