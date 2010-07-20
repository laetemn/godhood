package com.zonski.godhood.duels.game;

import com.zonski.godhood.duels.game.action.AttackAction;
import com.zonski.godhood.duels.game.action.SimpleAction;
import com.zonski.godhood.duels.game.action.MoveAction;
import com.zonski.godhood.duels.game.action.MonsterAction;

import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Nov 4, 2003
 * Time: 11:13:11 AM
 * To change this template use Options | File Templates.
 * <p>
 * A room that contains a whole lot of entities, each at a distinct position
 */
public final class Room extends Entity
{
    /**
     * constants used in loading an entity in a room
     */
    public static final byte NONE     = 0;
    public static final byte ITEM     = 2;
    public static final byte ATTACK   = 4;
    public static final byte MOVE     = 8;
    public static final byte SIMPLE   = 16;
    public static final byte PACKED   = 32;

    public static final byte PLAYER_SIDE = 1;

    /**
     * Indicates that the room has been won, as defined by
     * some external condition (if this flag is set the
     * room has been won)
     */
    public static final byte WIN_EXTERNAL        = -1;
    /**
     * The game does not finish in this room
     */
    public static final byte MODE_CONTINUOUS     = 0;
    /**
     * The game finishes when there is only one boss alive in this room
     */
    public static final byte MODE_LAST_BOSS      = 1;
    /**
     * The game finishes when there are only monsters from one side alive in this room
     */
    public static final byte MODE_LAST_SIDE   = 2;
    /**
     * The game finishes when a win item is picked up in this room
     */
    public static final byte MODE_WIN_ITEM = 3;

    private static final Vector EMPTY_VECTOR = new Vector(0);

    private Vector[][] matrix;

    /**
     * The time in the room
     */
    public int roomTime;

    /**
     * the entities in order of updates
     */
    public Stack updates;

    /**
     * The listeners upon this room
     */
    public Vector listeners;

    /**
     * Identifiers of the tiles on the floor of this room
     */
    public byte[][] tiles;

    /**
     * Identifiers of the obstacles in this room
     */
    public byte[][] obstacles;

    public byte width;
    public byte height;

    /**
     * The mode that this room should be played in
     */
    public byte mode;

    private Vector messages;

    /**
     * Message displayed upon entering this room
     */
    public String welcomeMessage;

    /**
     * Message displayed upon completing this rooms objective
     */
    public String winMessage;

    /**
     * Message displayed on failure to complete the rooms objective (someone else does it)
     */
    public String loseMessage;

    /**
     * A hashtable of the names of exits from this room and the objects
     * exiting via those exits
     */
    public Hashtable exits;

    /**
     * The entity that has won in this room, or null
     */
    public Entity winner;

    /**
     * Filter for non-aggressive sides, if a side is non-aggressive then it doesn't attack other
     * sides (unless it is attacked)
     */
    public int friendlySideFilter;

    /**
     * array of bytes used by monster ai for searches
     */
    public byte[][] map;

    public Room(Action playerMind, byte width, byte height)
    {
        this.updates = new Stack();
        this.exits = new Hashtable(3);
        this.width = width;
        this.height = height;
        this.tiles = new byte[height][width];
        this.obstacles = new byte[height][width];
        this.matrix = new Vector[height][width];
        this.map = new byte[height][width];
        this.messages = new Vector(3, 1);
        this.thinking = playerMind;
    }

    public final boolean isFriendly(int side, int withSide)
    {
        //boolean friendly = (0x01 & (this.friendlySideFilter >> side)) != 0 || side == withSide;
        //all sides hate eachother, some sides hate the player (side 1) too
        boolean friendly;
        if(side == withSide)
        {
            friendly = true;
        }else{
            if(side == PLAYER_SIDE)
            {
                friendly = (0x01 & (this.friendlySideFilter >> withSide)) != 0;
            }else if(withSide == PLAYER_SIDE){
                friendly = (0x01 & (this.friendlySideFilter >> side)) != 0;
            }else{
                friendly = false;
            }
        }
        return friendly;
    }

    public final byte getTile(int x, int y)
    {
        return getIntAt(this.tiles, x, y);
    }

    public final byte getObstacle(int x, int y)
    {
        return getIntAt(this.obstacles, x, y);
    }

    private final byte getIntAt(byte[][] ints, int x, int y)
    {
        byte i;
        if(y >=0 && y<ints.length)
        {
            byte[] xints = ints[y];
            if(x >= 0 && x < xints.length)
            {
                i = xints[x];
            }else{
                i = 0;
            }
        }else{
            i = 0;
        }
        return i;
    }

    public final void addContainedEntity(Entity entity)
    {
        // make it think
        addBrain(entity);
        super.addContainedEntity(entity);

        Vector at = getOrCreateEntitiesAt(entity.x, entity.y);
        at.addElement(entity);
    }

    public final void removeContainedEntity(Entity entity)
    {
        super.removeContainedEntity(entity);
        getEntitiesAt(entity.x, entity.y).removeElement(entity);
    }

    private final Vector getOrCreateEntitiesAt(int x, int y)
    {
        Vector entitiesAt;
        if(this.matrix[y][x] == null)
        {
            this.matrix[y][x] = new Vector(2, 1);
        }
        entitiesAt = this.matrix[y][x];
        return entitiesAt;
    }

    public Vector getEntitiesAt(int x, int y)
    {
        Vector entitiesAt;
        if(x >= 0 && x<width && y>=0 && y<height)
        {
            entitiesAt = this.matrix[y][x];
            if(entitiesAt == null)
            {
                entitiesAt = EMPTY_VECTOR;
            }
        }else{
            entitiesAt = EMPTY_VECTOR;
        }

        return entitiesAt;
    }

    public void addMessage(String message)
    {
        this.messages.addElement(message);
    }

    // room engine

    /**
     * Queues the specified entity immediately
     * @param entity the entity to queue next
     */
    public final void queueEntityImmediately(Entity entity)
    {
        // add the entity to the end, will be looked at next
        Object[] pair = new Object[]{entity, null, new Integer(this.roomTime)};
        this.updates.addElement(pair);
    }

    public final void queueEntity(Entity entity)
    {
        int frequency = entity.frequency;
        if(frequency > 0)
        {
            int nextUpdate = frequency + this.roomTime;
            int index = this.getIndex(nextUpdate);
            // if the entity isn't already queued before this point
            boolean found = false;
            for(int i=index; i<this.updates.size(); i++)
            {
                Object[] at = (Object[])this.updates.elementAt(i);
                if(at[0] == entity)
                {
                    found = true;
                    break;
                }
            }
            if(!found)
            {
                this.updates.insertElementAt(
                        new Object[]{entity, new Byte(entity.speed), new Integer(nextUpdate)}, index);
            }
        }
    }

    public final void dequeueEntity(Entity entity)
    {
        for(int i=this.updates.size(); i>0; )
        {
            i--;
            Object[] pair = (Object[])this.updates.elementAt(i);
            Entity found = (Entity)pair[0];
            if(found == entity)
            {
                this.updates.removeElementAt(i);
            }
        }
    }

    public final void queueEntities(Vector entities)
    {
        this.updates.ensureCapacity(entities.size()+this.updates.size());
        for(int i=entities.size(); i>0; )
        {
            i--;
            Entity entity = (Entity)entities.elementAt(i);
            queueEntity(entity);
        }
    }

    public final void dequeueEntities(Vector entities)
    {
        for(int i=entities.size(); i>0; )
        {
            i--;
            Entity entity = (Entity)entities.elementAt(i);
            dequeueEntity(entity);
        }
    }

    public final void updateNext()
    {
        if(this.updates.size() > 0)
        {
            Object[] pair = (Object[])this.updates.peek();
            Entity entity = (Entity)pair[0];
            if(!entity.isFlag(Entity.DEAD_FLAG))
            {
                Byte speed = (Byte)pair[1];
                Integer nextUpdate = (Integer)pair[2];
                if(speed != null)
                {
                    entity.unitsRemaining = speed.byteValue();
                    pair[1] = null;
                }
                this.roomTime = nextUpdate.intValue();
                this.update(entity);
                checkWinConditions(entity);
                // popping it should dequeue the entity and be a lot more efficient
                // than an actual call to dequeueEntity
                //this.updates.pop();
                // TODO : sometimes it gets mixed up about what is on top for some reason
                dequeueEntity(entity);
                if(!entity.isFlag(Entity.DEAD_FLAG))
                {
                    queueEntity(entity);
                }else{
                    // trim the queue, it's just gotten smaller
                    this.updates.trimToSize();
                }
            }
        }
    }

    /**
     * checks whether the game has been won yet
     */
    private final void checkWinConditions(Entity on)
    {
        Entity winner;
        switch(this.mode)
        {
            default:
                winner = null;
                break;
            case MODE_LAST_BOSS:
            case MODE_LAST_SIDE:
                {
                    // check if there is only one boss
                    winner = null;
                    Vector contained = this.getContainedEntities();
                    int bossCount = 0;
                    int lastSide = 0;
                    Enumeration keys = this.exits.keys();
                    outer : do
                    {
                        for(int i=contained.size(); i>0 && bossCount <= 1;)
                        {
                            i--;
                            Entity entity = (Entity)contained.elementAt(i);
                            if(this.mode == MODE_LAST_BOSS)
                            {
                                if(entity.isFlag(Entity.BOSS_FLAG))
                                {
                                    winner = entity;
                                    bossCount++;
                                }
                            }else{
                                if(entity.side != 0)
                                {
                                    if(lastSide == 0)
                                    {
                                        lastSide = entity.side;
                                        if(winner == null || entity.isFlag(Entity.BOSS_FLAG))
                                        {
                                            winner = entity;
                                        }
                                    }else if(lastSide != entity.side){
                                        winner = null;
                                        break outer;
                                    }
                                }
                            }
                        }
                        if(keys.hasMoreElements())
                        {
                            contained = (Vector)this.exits.get(keys.nextElement());
                        }else{
                            contained = null;
                        }
                    }while(contained != null);
                    if(bossCount > 1 && this.mode == MODE_LAST_BOSS)
                    {
                        winner = null;
                    }
                }
                break;
            case MODE_WIN_ITEM:
                {
                    // check to see if anyone has picked up the win item
                    winner = null;
                    Vector contained = this.getContainedEntities();
                    outer: for(int i=contained.size(); i>0;)
                    {
                        i--;
                        Entity entity = (Entity)contained.elementAt(i);
                        Vector items = entity.getContainedEntities();
                        if(items != null)
                        {
                            for(int j=items.size(); j>0; )
                            {
                                j--;
                                Entity item = (Entity)items.elementAt(j);
                                if(item.isFlag(Entity.BOSS_FLAG))
                                {
                                    // assume that boss items are "win" items
                                    winner = entity;
                                    break outer;
                                }
                            }
                        }
                    }
                }
                break;
            case WIN_EXTERNAL:
                // we've won
                // winner should already be set
                winner = this.winner;
                break;
        }
        if(winner != null)
        {
            // fire a special room event
            this.fireRoomEvent(winner, new SimpleAction(this, SimpleAction.ROOM_WIN, (byte)0, Entity.NONE_TYPE, false));
        }
        // check to see if the player boss has been killed
        if(on.doing instanceof SimpleAction)
        {
            SimpleAction simple = (SimpleAction)on.doing;
            if(on.isFlag(Entity.BOSS_FLAG))
            {
                if(on.side == PLAYER_SIDE)
                {
                    // there's only a few ways to die, DIE, STONE, SWALLOW, or POLYMORPH (into an inanimate object)
                    if(simple.getMode() == SimpleAction.DIE || simple.getMode() == SimpleAction.STONE ||
                            simple.getMode() == SimpleAction.SWALLOW ||
                            (simple.getMode() == SimpleAction.POLYMORPH && simple.item != null && ((Entity)simple.item).frequency <= 0))
                    {
                        // he's dead or as good as
                        SimpleAction lose = new SimpleAction(this, SimpleAction.ROOM_LOSE, (byte)0, Entity.NONE_TYPE, false);
                        lose.convertSide = on.side;
                        this.fireRoomEvent(on, lose);
                    }
                }else if(simple.getMode() == SimpleAction.CONVERT){
                    // is there a boss on the player side left?
                    Room room = (Room)on.container;
                    Vector contained = room.getContainedEntities();
                    boolean found = false;
                    for(int i=0; i<contained.size(); i++)
                    {
                        Entity e = (Entity)contained.elementAt(i);
                        if(e.side == PLAYER_SIDE && e.isFlag(Entity.BOSS_FLAG))
                        {
                            found = true;
                            break;
                        }
                    }
                    if(!found)
                    {
                        // he's been brain washed!
                        SimpleAction lose = new SimpleAction(this, SimpleAction.ROOM_LOSE, (byte)0, Entity.NONE_TYPE, false);
                        lose.convertSide = PLAYER_SIDE;
                        this.fireRoomEvent(on, lose);
                    }
                }
            }
        }
    }

    public final void update(Entity entity)
    {
        if(entity.reaction != null)
        {
            this.removeRoomListener(entity.reaction);
            entity.reaction = null;
        }
        entity.setFlag(Entity.DONE_FLAG, false);
        // call attention to the entity, even though it might not do anything
        this.fireRoomEvent(entity, null);
        while(!entity.isFlag(Entity.DONE_FLAG))
        {
            Action action = entity.doing;
            if(action == null || action.isCompleted())
            {
                if(entity.thinking != null)
                {
                    Action oldAction = entity.doing;
                    entity.thinking.update();
                    action = entity.doing;
                    String availability = action.getAvailability(entity);
                    if(availability != null)
                    {
                        action = null;
                        entity.doing = null;
                    }else{
                        if(oldAction != action)
                        {
                            if(action != null && availability == null)
                            {
                                // we're doing something that we're thinking about doing
                                entity.setLastStrike(action);
                                action.started();
                            }
                            if(oldAction != null)
                            {
                                oldAction.stopped();
                            }
                        }
                    }
                }else{
                    entity.setFlag(Entity.DONE_FLAG, true);
                }
            }

            if(action != null && !action.isCompleted())
            {
                //System.out.println("the "+entity.name+" is "+action+"-ing ");
                int oldX = entity.x;
                int oldY = entity.y;
                int cost = action.update();
                if(entity.isFlag(Entity.DEAD_FLAG))
                {
                    // uh, oh, looks like we just killed ourselves
                    Vector old = getEntitiesAt(oldX, oldY);
                    old.removeElement(entity);
                    // clear up the memory, it can get ugly otherwise
                    old.trimToSize();
                }else if(entity.x != oldX || entity.y != oldY){
                    Vector old = getEntitiesAt(oldX, oldY);
                    old.removeElement(entity);
                    // clear up the memory, it can get ugly otherwise
                    old.trimToSize();
                    Vector entitiesAt = getOrCreateEntitiesAt(entity.x, entity.y);
                    entitiesAt.addElement(entity);
                }
                entity.unitsRemaining -= cost;
                this.fireRoomEvent(entity, action);
                // if we're done or dead well the next guy is going to be having
                // a turn pretty soon anyway
                if(this.updates.size() > 0 && !entity.isFlag(Entity.DONE_FLAG) && !entity.isFlag(Entity.DEAD_FLAG))
                {
                    Object[] pair = (Object[])this.updates.peek();
                    if(pair[0] != entity)
                    {
                        // someone's put something on the front of the queue
                        updateNext();
                        // get the focus back here
                        this.fireRoomEvent(entity, null);
                    }
                }
            }
        }
        this.addBrain(entity);
        if(entity.side == PLAYER_SIDE)
        {
            if(entity.thinking != null && entity.thinking instanceof MonsterAction)
            {
                // give it a player mind
                entity.thinking = this.thinking.copy(entity);
            }
        }else{
            if(entity.thinking != null && entity.thinking.getClass().equals(this.thinking.getClass()))
            {
                // give it a monster mind
                entity.thinking = new MonsterAction(entity);
            }
        }
        // TODO : this is purely for presentation purposes, hence doesn't belong here
        if(entity.doing instanceof AttackAction)
        {
            entity.doing = null;
        }
    }


    /**
     * Obtains the index of the entity in the queue
     * @return the index of the entity
     */
    private final int getIndex(int time)
    {
        // the maximum index for the entity should be returned
        // eg. inserting 3 into the sequence 5,3,3,3,2,1,1 should be inserted in the second
        // position
        int min = 0;
        int max = this.updates.size();
        while(min < max)
        {
            int mid = (min+max)/2;
            Object[] pair = (Object[])this.updates.elementAt(mid);
            Integer midUpdate = (Integer)pair[2];
            if(midUpdate.intValue() > time)
            {
                min = mid+1;
            }else{
                max = mid;
            }
        }
        while(min > 0)
        {
            Object[] pair = (Object[])this.updates.elementAt(min-1);
            Integer minUpdate = (Integer)pair[2];
            if(minUpdate.intValue() != time)
            {
                break;
            }
            min--;
        }
        return min;
    }

    public final void addRoomListener(RoomListener listener)
    {
        if(this.listeners == null)
        {
            this.listeners = new Vector(4, 4);
        }
        this.listeners.addElement(listener);
    }

    public final void removeRoomListener(RoomListener listener)
    {
        if(this.listeners != null)
        {
            this.listeners.removeElement(listener);
        }
    }

    public final Vector getExiting(String exitName)
    {
        return (Vector)this.exits.get(exitName);
    }

    public final void exit(String exitName, Entity exiter)
    {
        Vector exiting = (Vector)this.exits.get(exitName);
        if(exiting == null)
        {
            exiting = new Vector(4, 4);
            this.exits.put(exitName, exiting);
        }
        exiting.addElement(exiter);
        this.removeContainedEntity(exiter);
        // NOTE : don't need to dequeue the entity (probably) it's being moved
        // as part of its turn and it's listed as dead anyway
        //this.dequeueEntity(exiter);
        exiter.setFlag(Entity.DONE_FLAG, true);
        // NOTE : they're not really dead, but as far as this room is concerned they're
        // not coming back (for a while anyway)
        exiter.setFlag(Entity.DEAD_FLAG, true);
    }

    /**
     * fires the engine event, may block the thread
     * @param entity the entity that the event relates to
     * @param action the action that the entity is performing
     */
    protected final void fireRoomEvent(Entity entity, Action action)
    {
        if(this.listeners.size() > 0)
        {
            String[] msgs = new String[this.messages.size()];
            this.messages.copyInto(msgs);
            this.messages.removeAllElements();
            for(int i=this.listeners.size(); i>0;)
            {
                i--;
                RoomListener listener = (RoomListener)this.listeners.elementAt(i);
                listener.entityUpdated(this, entity, action, msgs);
            }
        }
    }

    // entity factory
    public final Entity copyEntity(Entity prototype, byte side)
    {
        Entity result;
        Entity found = prototype;
        result = found.copy();
        result.unitsRemaining = result.speed;
        result.side = side;
        addBrain(result);
        return result;
    }

    public final void setPlayerMind(Action playerMind)
    {
        this.thinking = playerMind;
        Vector contained = getContainedEntities();
        for(int i=0; i<contained.size(); i++)
        {
            Entity c = (Entity)contained.elementAt(i);
            addBrain(c);
        }
    }

    public final void addBrain(Entity entity)
    {

        int maskedType = entity.entityType;
        if(maskedType == MONSTER_TYPE)
        {
            // it's a monster!
            if(entity.side == PLAYER_SIDE)
            {
                if(this.thinking != null)
                {
                    entity.thinking = this.thinking.copy(entity);
                }
            }else{
                entity.thinking = new MonsterAction(entity);
            }
        }else if(maskedType == PHENOMINA_TYPE){
            // it's a phenomina
            entity.thinking = new SimpleAction(entity, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        }else{
            //result.thinking = null;
        }

    }

    // room factory

    public static final Room create(Action playerMind, DataInputStream ins)
        throws Exception
    {
        // get the width and the height
        String name = readString(ins);
        byte width = ins.readByte();
        byte height = ins.readByte();

        Room room = new Room(playerMind, width, height);
        room.welcomeMessage = readString(ins);
        room.winMessage = readString(ins);
        room.loseMessage = readString(ins);
        boolean save = ins.readBoolean();
        if(!save)
        {
            room.flags = 1;
        }
        room.name = name;
        room.mode = ins.readByte();
        room.friendlySideFilter = ins.readInt();
        // fill in the tiles
        for(int y = 0; y<height; y++)
        {
            for(int x = 0; x<width; x++)
            {
                room.tiles[y][x] = ins.readByte();
            }
        }
        // fill in the obstacles
        for(int y = 0; y<height; y++)
        {
            for(int x = 0; x<width; x++)
            {
                room.obstacles[y][x] = ins.readByte();
            }
        }
        // read in the word map
        int numWords = ins.readByte();
        String[] words = new String[numWords];
        for(int i=0; i<numWords; i++)
        {
            int index = ins.readByte();
            String word = readString(ins);
            words[index] = word;
        }

        // read in the packed actions
        int numPackedActions = ins.readByte();
        Hashtable prototypes = new Hashtable(numPackedActions);
        Action[] packedActions = new Action[numPackedActions];
        for(int i=0; i<numPackedActions; i++)
        {
            //boolean inherent = ins.readBoolean();
            byte type = ins.readByte();
            packedActions[i] = readAction(type, null, ins, words, true, packedActions, prototypes);
        }
        // read in the actions for this room (prayers)
        readActionsAndItems(room, ins, words, true, packedActions, prototypes);
        // read in the prototypes for the entities in this room
        int numPrototypes = ins.readByte();
        int numEntities = ins.readByte();
        int numRecords = numPrototypes + numEntities;
        room.containedEntities = new Vector(numEntities, 5);

        // create the entities
        for(int i=0; i<numRecords; i++)
        {
            boolean isPrototype = ins.readBoolean();
            Entity entity = readCompleteEntity(room, words, packedActions, ins, isPrototype, prototypes);
            if(isPrototype)
            {
                prototypes.put(new Byte(entity.entityTypeId), entity);
            }
        }
        return room;
    }

    private static final Entity readCompleteEntity(Entity container, String[] words,
                                                   Action[] packedActions, DataInputStream ins,
                                                   boolean isPrototype, Hashtable prototypes)
        throws Exception
    {
        byte typeId = ins.readByte();
        String entityName = readString(ins, words);
        Entity entity = null;
        // is it a prototype or a derivation
        // prototype
        if(isPrototype)
        {
            // it may have already been created for a placeholder in the actions
            entity = (Entity)prototypes.get(new Byte(typeId));
        }
        if(entity == null)
        {
            entity = new Entity();
        }
        entity.name = entityName;
        entity.entityTypeId = typeId;
        byte type = ins.readByte();
        entity.entityType = type;
        // load in vital statistics
        // collision effect, level, speed, base attack, base armour class, health, category
        entity.level = ins.readByte();
        entity.speed = ins.readByte();
        entity.frequency = ins.readByte();
        entity.flags = ins.readByte();
        entity.baseAttack = ins.readByte();
        entity.baseArmourClass = ins.readByte();
        entity.maxHealth = ins.readByte();
        entity.entityCategory = ins.readByte();

        readActionsAndItems(entity, ins, words, true, packedActions, prototypes);
        //System.out.println(entity.name +" : "+typeId);
        if(!isPrototype)
        {
            String exitName = null;
            entity.container = container;

            entity.side = ins.readByte();
            // is it the side boss?
            entity.setFlag(Entity.BOSS_FLAG, ins.readBoolean());
            // get the entities health
            entity.health = ins.readByte();

            boolean inExit = ins.readBoolean();
            if(!inExit)
            {
                entity.x = ins.readByte();
                entity.y = ins.readByte();
            }else{
                // this entity is exiting, hence doesn't have a position, just an exit name
                exitName = readString(ins, words);
            }

            readActionsAndItems(entity, ins, words, false, packedActions, prototypes);

            if(exitName == null)
            {
                container.addContainedEntity(entity);
            }else{
                ((Room)container).exit(exitName, entity);
            }
        }else{
            entity.health = entity.maxHealth;
        }
        return entity;
    }

    private static final void readActionsAndItems(Entity entity, DataInputStream ins, String[] words, boolean inherent, Action[] packed, Hashtable prototypes)
        throws Exception
    {
        // read in additional actions and items owned by this entity
        int numActions = ins.readByte();
        int numItems = ins.readByte();
        int total = numActions + numItems;
        if(numActions > 0)
        {
            if(entity.actions != null)
            {
                entity.actions.ensureCapacity(entity.actions.size()+numActions);
            }else{
                entity.actions = new Vector(numActions, 2);
            }
        }
        if(numItems > 0)
        {
            if(entity.containedEntities != null)
            {
                entity.containedEntities.ensureCapacity(entity.containedEntities.size() + numItems);
            }else{
                entity.containedEntities = new Vector(numItems, 2);
            }
        }
        for(int i=0; i<total; i++)
        {
            byte containedType = ins.readByte();
            switch(containedType)
            {
                case ITEM:
                    // read in the extraneous prototype bit
                    ins.readBoolean();
                    readCompleteEntity(entity, words, packed, ins, false, prototypes);
                    // it's already been added to the container
                    break;
                default:
                    Action action;
                    action = readAction(containedType, entity, ins, words, inherent, packed, prototypes);
                    entity.actions.addElement(action);
                    break;
            }
        }
    }

    private static final Action readAction(byte type, Entity entity, DataInputStream ins,
                                           String[] words, boolean inherent, Action[] packed,
                                           Hashtable prototypes)
        throws Exception
    {
        //System.out.println("action ("+type+")");
        Action result;
        switch(type)
        {
            case ATTACK:
            {
                String name = readString(ins, words);
                String action = readString(ins, words);
                String itemName = readString(ins, words);
                AttackAction attack = new AttackAction(
                        null, name, action, ins.readByte(),
                        ins.readByte(), ins.readByte(), ins.readByte(),
                        ins.readByte(), ins.readByte());
                attack.type = ins.readByte();
                attack.itemName = itemName;
                attack.prerequsiteCategory = ins.readByte();
                attack.flags = ins.readByte();
                byte creates = ins.readByte();
                if(creates != Entity.NONE_TYPE)
                {
                    Byte key = new Byte(creates);
                    Entity created = (Entity)prototypes.get(key);
                    if(created != null)
                    {
                        attack.creates = created;
                    }else{
                        // put in a placeholder it will be populated later
                        created = new Entity();
                        attack.creates = created;
                        prototypes.put(key, created);
                    }
                }
                attack.conferPercent = ins.readByte();
                attack.radius = ins.readByte();
                byte confersType = ins.readByte();
                if(confersType != NONE)
                {
                    // read in another action
                    attack.confers = readAction(confersType, entity, ins, words, inherent, packed, prototypes);
                }
                result = attack;
            }
            break;
            case MOVE:
            {
                byte cost = ins.readByte();
                MoveAction moveAction;
                moveAction = new MoveAction(cost, null);
                result = moveAction;
            }
            break;
            case SIMPLE:
            {
                byte mode;
                byte cost;
                byte filter;
                // read in the type and the cost
                mode = ins.readByte();
                cost = ins.readByte();
                // prerequisites
                filter = ins.readByte();
                // they all have a name
                String name = readString(ins, words);
                // ...and a description
                String actionDescription = readString(ins, words);
                SimpleAction action = new SimpleAction(null, mode, cost, Entity.NONE_TYPE, false);
                action.name = name;
                action.action = actionDescription;
                action.filter = filter;

                // read in action-specific data
                switch(mode)
                {
                    case SimpleAction.EXIT:
                        String exitName = readString(ins, words);
                        action.exitName = exitName;
                        break;
                    case SimpleAction.HEAL:
                    case SimpleAction.HURT:
                    case SimpleAction.CONVERT:
                    case SimpleAction.PROVOKE:
                    case SimpleAction.TRANSMUTATION:
                        action.convertSide = ins.readByte();
                        break;
                    case SimpleAction.STONE:
                    case SimpleAction.POLYMORPH:
                        action.convertSide = ins.readByte();
                    case SimpleAction.DIE:
                        byte itemType = ins.readByte();
                        if(itemType != Entity.NONE_TYPE)
                        {
                            Byte key = new Byte(itemType);
                            Entity created = (Entity)prototypes.get(key);
                            if(created != null)
                            {
                                action.item = created;
                            }else{
                                // put in a placeholder it will be populated later
                                created = new Entity();
                                action.item = created;
                                prototypes.put(key, created);
                            }
                        }
                        break;
                    case SimpleAction.PUTDOWN:
                        itemType = ins.readByte();
                        action.type = itemType;
                        break;
                    case SimpleAction.LEARN:
                        byte learnType = ins.readByte();
                        action.item = readAction(learnType, null, ins, words, false, packed, prototypes);
                        break;
                }
                result = action;
            }
            break;
            case PACKED:
            {
                byte index = ins.readByte();
                result = packed[index];
                //System.out.println("unpacking "+result.getName()+" = "+index);
            }
            break;
            default:
                throw new Exception("?"+type);
        }
        //result.inherent = inherent;
        return result;
    }

    private static final Hashtable getTextMap(Room room)
    {
        int count = 0;
        Vector contained = room.getContainedEntities();
        Hashtable textmap = new Hashtable();
        // go through the actions
        if(room.actions != null)
        {
            count = insertActionWords(textmap, room.actions, count);
        }
        // go through the entities
        if(contained != null)
        {
            for(int i=0; i<contained.size(); i++)
            {
                Entity entity = (Entity)contained.elementAt(i);
                count = insertActionWords(textmap, entity, count);
            }
        }
        // go through the exits
        Hashtable exits = room.exits;
        Enumeration keys = exits.keys();
        while(keys.hasMoreElements())
        {
            Vector leaving = (Vector)exits.get(keys.nextElement());
            for(int i=0; i<leaving.size(); i++)
            {
                Entity entity = (Entity)leaving.elementAt(i);
                count = insertActionWords(textmap, entity, count);
            }
        }
        return textmap;
    }

    private static final int insertActionWords(Hashtable words, Entity entity, int count)
    {
        if(!words.containsKey(entity))
        {
            // ensure that the entity is only ever passed once
            words.put(entity, entity);
            count = insertWord(words, entity.name, count);
            if(entity.actions != null)
            {
                count = insertActionWords(words, entity.actions, count);
            }
            Vector contained = entity.getContainedEntities();
            if(contained != null)
            {
                for(int i=0; i<contained.size(); i++)
                {
                    Entity item = (Entity)contained.elementAt(i);
                    count = insertActionWords(words, item, count);
                }
            }
        }
        return count;
    }

    private static final int insertActionWords(Hashtable words, Vector actions, int count)
    {
        int result = count;
        for(int i=0; i<actions.size(); i++)
        {
            Action action = (Action)actions.elementAt(i);
            result = insertActionWords(words, action, result);
        }
        return result;
    }

    private static final int insertActionWords(Hashtable words, Action action, int count)
    {
        int result;
        insertAction(words, action);
        if(action instanceof AttackAction)
        {
            AttackAction attack = (AttackAction)action;
            result = insertWord(words, attack.action, count);
            result = insertWord(words, attack.getName(), result);
            result = insertWord(words, attack.itemName, result);
            if(attack.creates != null)
            {
                result = insertActionWords(words, attack.creates, result);
            }
            if(attack.confers != null)
            {
                result = insertActionWords(words, attack.confers,  result);
            }
        }else if(action instanceof MoveAction){
            // do nothing moving is moving
            result = count;
        }else if(action instanceof SimpleAction){
            SimpleAction simple = (SimpleAction)action;
            result = insertWord(words, simple.name, count);
            result = insertWord(words, simple.action, result);
            result = insertWord(words, simple.exitName, result);
            if(simple.item instanceof Entity)
            {
                result = insertActionWords(words, (Entity)simple.item, result);
            }else if(simple.item instanceof Action){
                result = insertActionWords(words, (Action)simple.item, result);
            }
        }else{
            throw new RuntimeException("?"+action.getName());
        }
        return result;
    }

    private static final int insertWord(Hashtable words, String word, int count)
    {
        int result;
        if(word == null || words.containsKey(word))
        {
            result = count;
        }else{
            words.put(word, new Integer(count));
            result = count+1;
        }
        return result;
    }

    private static final void insertAction(Hashtable words, Action action)
    {
        Object val = words.get(action);
        if(val != null)
        {
            if(val != action)
            {
                words.put(action, action);
            }
        }else{
            words.put(action, words);
        }
    }

    private static final boolean CHECK_BOUNDS = true;

    public static final void store(Room room, DataOutputStream outs)
        throws IOException
    {
        Hashtable textMap = getTextMap(room);

        writeString(outs, room.name);
        //writeLine(outs, ""+room.width+","+room.height+","+room.mode+","+room.friendlySideFilter);
        outs.writeByte(room.width);
        outs.writeByte(room.height);
        writeString(outs, room.welcomeMessage);
        writeString(outs, room.winMessage);
        writeString(outs, room.loseMessage);
        outs.writeBoolean(room.flags == 0);
        outs.writeByte(room.mode);
        outs.writeInt(room.friendlySideFilter);
        // write out the tiles
        for(int y=0; y<room.height; y++)
        {
            for(int x = 0; x<room.width; x++)
            {
                outs.writeByte(room.getTile(x, y));
            }
        }
        // write out the obstacles
        for(int y=0; y<room.height; y++)
        {
            for(int x = 0; x<room.width; x++)
            {
                outs.writeByte(room.getObstacle(x, y));
            }
        }

        // write out the words used in this room
        int wordCount = 0;
        Enumeration textKeys = textMap.keys();
        while(textKeys.hasMoreElements())
        {
            Object key = textKeys.nextElement();
            if(key instanceof String)
            {
                wordCount++;
            }
        }
        if(CHECK_BOUNDS)
        {
            if(wordCount > Byte.MAX_VALUE)
            {
                throw new RuntimeException("too many strings");
            }
        }
        outs.writeByte(wordCount);
        textKeys = textMap.keys();
        Vector packed = new Vector();
        Vector prototypes = new Vector();
        while(textKeys.hasMoreElements())
        {
            Object key = textKeys.nextElement();
            if(key instanceof String)
            {
                String word = (String)key;
                Integer index = (Integer)textMap.get(key);
                outs.writeByte((byte)index.intValue());
                writeString(outs, word);
            }else if(key instanceof Action){
                // add any entities referenced by these actions to the prototypes list
                Action action = (Action)key;
                Entity prototype;
                //System.out.println("action "+action.getName());
                if(action instanceof AttackAction)
                {
                    AttackAction attack = (AttackAction)action;
                    prototype = attack.creates;
                }else if(action instanceof SimpleAction){
                    SimpleAction simple = (SimpleAction)action;
                    if(simple.item instanceof Entity)
                    {
                        prototype = (Entity)simple.item;
                    }else{
                        prototype = null;
                    }
                }else{
                    prototype = null;
                }
                if(prototype != null)
                {
                    // make sure it isn't already there
                    boolean found = false;
                    for(int i=0; i<prototypes.size(); i++)
                    {
                        Entity p = (Entity)prototypes.elementAt(i);
                        if(p.entityTypeId == prototype.entityTypeId)
                        {
                            found = true;
                            break;
                        }
                    }
                    if(!found)
                    {
                        prototypes.addElement(prototype);
                    }
                }

                if(textMap.get(key) == key)
                {
                    packed.addElement(key);
                }
            }
        }

        // write out the packed actions
        outs.writeByte(packed.size());
        for(int i=0; i<packed.size(); i++)
        {
            Action pack = (Action)packed.elementAt(i);
            //outs.writeBoolean(pack.inherent);
            storeAction(pack, outs, textMap, null);
        }

        // write out room actions
        storeItemsAndActions(room, outs, textMap, true, packed);

        // write out the number of prototypes
        int numPrototypes = prototypes.size();
        //System.out.println("# prototypes = "+numPrototypes);
        outs.writeByte(numPrototypes);

        // write out the number of entities
        Vector entities = room.getContainedEntities();
        if(entities != null)
        {
            if(CHECK_BOUNDS)
            {
                if(entities.size() > Byte.MAX_VALUE)
                {
                    throw new RuntimeException("too many entities ("+entities.size()+")");
                }
            }
            outs.writeByte(entities.size());
        }else{
            outs.writeByte(0);
        }
        // write out prototypes

        for(int i=0; i<prototypes.size(); i++)
        {
            Entity entity = (Entity)prototypes.elementAt(i);
            if(entity != null)
            {
                writePrototype(entity, true, outs, textMap, packed);
            }
        }
        // write out entities
        if(entities != null)
        {
            for(int i=0; i<entities.size(); i++)
            {
                Entity entity = (Entity)entities.elementAt(i);
                writeEntity(entity, null, outs, textMap, packed);
            }
        }
        // store the information about the monsters exiting this room
        Enumeration exitNames = room.exits.keys();
        while(exitNames.hasMoreElements())
        {
            String exitName = (String)exitNames.nextElement();
            Vector exiting = (Vector)room.exits.get(exitName);
            for(int i=0; i<exiting.size(); i++)
            {
                Entity entity = (Entity)exiting.elementAt(i);
                writeEntity(entity, exitName, outs, textMap, packed);
            }
        }
    }

    private static final void writeEntity(Entity entity, String exitName, DataOutputStream outs, Hashtable textMap, Vector packed)
        throws IOException
    {
        writePrototype(entity, false, outs, textMap, packed);
        //writeString(outs, textMap, entity.name);
        //outs.writeByte(entity.entityTypeId);
        //outs.writeBoolean(false);
        outs.writeByte(entity.side);
        outs.writeBoolean(entity.isFlag(Entity.BOSS_FLAG));
        outs.writeByte(entity.health);
        // not in the exit
        outs.writeBoolean(exitName != null);
        if(exitName == null)
        {
            outs.writeByte(entity.x);
            outs.writeByte(entity.y);
        }else{
            writeString(outs, exitName);
        }
        storeItemsAndActions(entity, outs, textMap, false, packed);

    }

    private static final void writePrototype(Entity entity, boolean prototype, DataOutputStream outs, Hashtable textMap, Vector packed)
        throws IOException
    {
        outs.writeBoolean(prototype);
        outs.writeByte(entity.entityTypeId);
        writeString(outs, textMap, entity.name);
        outs.writeByte(entity.entityType);
        outs.writeByte(entity.level);
        outs.writeByte(entity.speed);
        outs.writeByte(entity.frequency);
        outs.writeByte(entity.flags);
        outs.writeByte(entity.baseAttack);
        outs.writeByte(entity.baseArmourClass);
        outs.writeByte(entity.maxHealth);
        outs.writeByte(entity.entityCategory);
        storeItemsAndActions(entity, outs, textMap, true, packed);
    }

    public static final void storeItemsAndActions(Entity entity, DataOutputStream outs, Hashtable words,
                                                  boolean inherent, Vector packed)
        throws IOException
    {

        Vector actions = entity.actions;
        Vector items = entity.getContainedEntities();

        if(actions != null)
        {
            int actionCount;
            if(inherent)
            {
                actionCount = actions.size();
            }else{
                actionCount = 0;
            }
            outs.writeByte(actionCount);
        }else{
            outs.writeByte(0);
        }
        if(items != null)
        {
            int itemCount;
            // TODO : assumes that prototypes never have items
            if(inherent)
            {
                itemCount = 0;
            }else{
                itemCount = items.size();
            }
            outs.writeByte(itemCount);
        }else{
            outs.writeByte(0);
        }

        if(actions != null)
        {
            for(int i=0; i<actions.size(); i++)
            {
                Action action = (Action)actions.elementAt(i);
                if(inherent)
                {
                    //System.out.println("storing action "+action.getName()+" as inherent = "+inherent);
                    storeAction(action, outs, words, packed);
                }
            }
        }
        // TODO : assumes that prototypes never have items
        if(items != null && !inherent)
        {
            for(int i=0; i<items.size(); i++)
            {
                Entity item = (Entity)items.elementAt(i);
                outs.writeByte(ITEM);
                writeEntity(item, null, outs, words, packed);
                //storeItemsAndActions(item, outs, words, inherent, packed);
            }
        }
    }

    private static final void storeAction(Action action, DataOutputStream outs, Hashtable words,
                                          Vector packedActions)
        throws IOException
    {
        int index;
        if(packedActions != null)
        {
            index = packedActions.indexOf(action);
        }else{
            index = -1;
        }
        if(index >= 0)
        {
            outs.writeByte(PACKED);
            outs.writeByte(index);
            //System.out.println("packing "+index+" = "+action.getName());
        }
        else
        {
            if(action instanceof AttackAction)
            {
                // write the action
                AttackAction attack = (AttackAction)action;
                outs.writeByte(ATTACK);
                writeString(outs, words, attack.getName());
                writeString(outs, words, attack.action);
                writeString(outs, words, attack.itemName);
                outs.writeByte(attack.getTotalSteps());
                outs.writeByte(attack.range);
                outs.writeByte(attack.diceNumber);
                outs.writeByte(attack.diceSides);
                outs.writeByte(attack.diceBonus);
                outs.writeByte(attack.category);
                outs.writeByte(attack.type);
                outs.writeByte(attack.prerequsiteCategory);
                outs.writeByte(attack.flags);
                byte creates;
                if(attack.creates != null)
                {
                    creates = attack.creates.entityTypeId;
                }else{
                    creates = Entity.NONE_TYPE;
                }
                outs.writeByte(creates);
                outs.writeByte(attack.conferPercent);
                outs.writeByte(attack.radius);
                // write out the conferred action
                if(attack.confers != null)
                {
                    storeAction(attack.confers, outs, words, packedActions);
                }else{
                    outs.writeByte(NONE);
                }
            }else if(action instanceof SimpleAction){
                outs.writeByte(SIMPLE);
                SimpleAction simpleAction = (SimpleAction)action;
                outs.writeByte(simpleAction.getMode());
                outs.writeByte(simpleAction.getTotalSteps());
                outs.writeByte(simpleAction.filter);
                writeString(outs, words, simpleAction.getName());
                writeString(outs, words, simpleAction.action);
                switch(simpleAction.getMode())
                {
                    case SimpleAction.EXIT:
                        writeString(outs, words, simpleAction.exitName);
                        break;
                    case SimpleAction.HEAL:
                    case SimpleAction.HURT:
                    case SimpleAction.CONVERT:
                    case SimpleAction.PROVOKE:
                    case SimpleAction.TRANSMUTATION:
                        outs.writeByte(simpleAction.convertSide);
                        break;
                    case SimpleAction.STONE:
                    case SimpleAction.POLYMORPH:
                        outs.writeByte(simpleAction.convertSide);
                    case SimpleAction.DIE:
                        byte entityTypeId;
                        if(simpleAction.item != null)
                        {
                            entityTypeId = ((Entity)simpleAction.item).entityTypeId;
                        }else{
                            entityTypeId = Entity.NONE_TYPE;
                        }
                        outs.writeByte(entityTypeId);
                        break;
                    case SimpleAction.PUTDOWN:
                        outs.writeByte(simpleAction.type);
                        break;
                    case SimpleAction.LEARN:
                        storeAction((Action)simpleAction.item, outs, words, packedActions);
                        break;
                }
            }else if(action instanceof MoveAction){
                outs.writeByte(MOVE);
                outs.writeByte(action.getTotalSteps());
            }else{
                throw new RuntimeException("?"+action.getName());
            }
        }
    }

    public static final String readString(DataInputStream ins)
        throws IOException
    {
        String result;
        byte size = ins.readByte();
        if(size >= 0)
        {
            byte[] string = new byte[size];
            ins.readFully(string);
            //char[] string = new char[size];
            //for(int i=0; i<size; i++)
            //{
            //    string[i] = ins.readChar();
            //}
            result = new String(string);
            //System.out.println("read :"+result);
        }else{
            result = null;
        }
        return result;
    }

    private static final String readString(DataInputStream ins, String[] words)
        throws IOException
    {
        int index = ins.readByte();
        if(index >= 0)
        {
            //System.out.print("index "+index+" = ");
            //System.out.flush();
            String word = words[index];
            //System.out.println(word);
            return word;
        }else{
            return null;
        }
    }

    public static final void writeString(DataOutputStream outs, String string)
        throws IOException
    {
        if(string != null)
        {
            byte[] bytes = string.getBytes();
            if(CHECK_BOUNDS)
            {
                if(bytes.length > Byte.MAX_VALUE)
                {
                    throw new RuntimeException("The string "+string+" is too long ("+bytes.length+")");
                }
            }
            outs.writeByte(bytes.length);
            outs.write(bytes);
            //outs.writeInt(string.length());
            //for(int i=0; i<string.length(); i++)
            //{
            //    outs.writeChar(string.charAt(i));
            //}
        }else{
            outs.writeByte(-1);
        }
    }

    private static final void writeString(DataOutputStream outs, Hashtable words, String string)
        throws IOException
    {
        if(string != null)
        {
            Integer index = (Integer)words.get(string);
            if(index == null)
            {
                throw new RuntimeException("?"+string);
            }
            if(CHECK_BOUNDS)
            {
                if(index.intValue() > Byte.MAX_VALUE)
                {
                    throw new RuntimeException("The string "+string+" has an index that is too large ("+index+")");
                }
            }
            outs.writeByte((byte)index.intValue());
        }else{
            outs.writeByte(-1);
        }
    }
}
