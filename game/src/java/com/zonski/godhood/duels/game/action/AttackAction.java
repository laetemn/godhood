package com.zonski.godhood.duels.game.action;

import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.RoomListener;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Nov 11, 2003
 * Time: 5:50:48 PM
 * To change this template use Options | File Templates.
 */
public class AttackAction extends Action implements RoomListener
{
    public static final byte HAND_TO_HAND = 0;
    public static final byte ARROW = 1;
    public static final byte MAGIC = 2;
    public static final byte THROWN_OBJECT = 3;
    public static final byte IMPLICIT = 4;

    public static final byte EFFECT_GROUP_BIT   = 1;
    public static final byte CONSUME_BIT        = 2;
    public static final byte ALWAYS_HITS_BIT    = 4;
    public static final byte SPAN_TURN_BIT      = 8;
    public static final byte KILL_PARENT_BIT    = 16;
    public static final byte END_TURN_BIT       = 32;
    public static final byte GOOD_BIT           = 64;

    private static final byte DEFAULT_FLAGS = END_TURN_BIT;

    private static final byte POSITION_SET_FLAG = 1;
    private static final byte UPDATED_ONCE_FLAG = 2;

    /**
     * The range of the attack in game squares
     */
    public byte range;

    /**
     * The category of the attack, effects the power depending on the monster being attacked
     */
    public byte category;

    /**
     * The number of attack dice
     */
    public byte diceNumber;

    /**
     * The number of sides on the dice
     */
    public byte diceSides;

    /**
     * The bonus applied to the end result
     */
    public byte diceBonus;

    /**
     * the type of the attack, hand-to-hand, arrow, or magic
     */
    public byte type;

    public byte attackX;
    public byte attackY;
    public byte prerequsiteCategory;
    public String itemName;

    /**
     * If non-null an object of this type will be created on the square being attacked
     */
    public Entity creates;

    /**
     * The radius of objects to create as a result of this call (0 creates one)
     */
    public byte radius;

    /**
     * Action that the attack confers onto the target
     */
    public Action confers;
    /**
     * The percent chance of the attack confering the effect or creating the "creates"
     * object
     */
    public byte conferPercent;

    public String action;

    private String name;
    private byte cost;
    public byte completed;

    public byte flags = DEFAULT_FLAGS;

    private byte internalFlags = 0;

    public static final void counterAttack(Entity counterAttacker, Entity entity, Action action)
    {
        // see if it's moved, and if so if it's moved next to the owner of this
        // action, if so, beat it up - free shot
        if(action instanceof MoveAction &&
                //counterAttacker.container instanceof Room &&
                !((Room)counterAttacker.container).isFriendly(counterAttacker.side, entity.side) &&
                (!counterAttacker.isFlag(Entity.PACIFIST_FLAG)) &&
                !(counterAttacker.doing instanceof SimpleAction &&
                ((SimpleAction)counterAttacker.doing).getMode() == SimpleAction.DEFEND))
        {
            MoveAction move = (MoveAction)action;
            if((move.flags & MoveAction.SWAPPED_FLAG) == 0 && (move.flags & MoveAction.CHANGED_LOCATION_FLAG)>0)
            {
                // nail em'
                AttackAction attack = MonsterAction.getBestAttack(counterAttacker, entity, true, 1, true);
                if(attack != null)
                {
                    if(action.isCompleted() && action.getTotalSteps() > 0 &&
                            AttackAction.inRange(
                                    counterAttacker.x, counterAttacker.y, entity.x, entity.y, attack.range)
                    )
                    {
                        if(attack.getAvailability(counterAttacker) == null)
                        {
                            // get them
                            attack = (AttackAction)attack.copy(counterAttacker);
                            attack.setPosition(entity.x, entity.y);
                            // have to check again because it will look for additional information now
                            // we've set the position
                            if(attack.getAvailability(counterAttacker) == null)
                            {
                                counterAttacker.doing = attack;
                                Room room = (Room)counterAttacker.container;
                                room.queueEntityImmediately(counterAttacker);
                            }
                        }
                    }
                }
            }
        }
    }

    private static final String format(String action, Entity attacker, String attackeeName, AttackAction attack)
    {
        String result;
        if(action.indexOf('{')>=0)
        {
            // the action contains special formatting information
            result = Action.format(action, attacker.name, attackeeName, attack.itemName);
        }else{
            // it's pretty run of the mill
            result = "The "+attacker.name+" "+action+" the "+attackeeName;
        }
        return result;
    }

    /**
     * Creates an attack action
     * @param entity the attacking entity
     * @param name the name of the attack (eg. Slash)
     * @param action the name of the attacking action (eg. slashes)
     * @param cost the cost of the action
     * @param range the range of the action
     */
    public AttackAction(Entity entity, String name, String action, byte cost, byte range,
                        byte diceNum, byte diceSides, byte bonus, byte category)
    {
        this.range = range;
        this.diceNumber = diceNum;
        this.diceSides = diceSides;
        this.diceBonus = bonus;
        this.category = category;
        this.entity = entity;
        this.action = action;
        this.name = name;
        this.cost = cost;
    }

    public void setPosition(byte attackX, byte attackY)
    {
        this.attackX = attackX;
        this.attackY = attackY;
        this.internalFlags |= POSITION_SET_FLAG;
    }

    public final boolean endsTurn()
    {
        return (this.flags & END_TURN_BIT) > 0;
    }

    public final int update()
    {
        this.internalFlags |= UPDATED_ONCE_FLAG;
        int completed = Math.min(this.cost - this.completed, this.entity.unitsRemaining);
        if((this.completed+completed) >= this.cost)
        {
            // the position must be set for the attack to finish, if it
            // isn't it should be set and the update method called again
            if((this.internalFlags & POSITION_SET_FLAG)>0)
            {
                // check the range
                Room room = (Room)this.entity.container;

                // the availability info should have filtered out the range checks here ??
                //if(!this.internalInRange(this.attackX, this.attackY))
                //{
                    //room.addMessage("out of range");
                    //this.internalFlags = (byte)(this.internalFlags & ~POSITION_SET_FLAG);
                    //completed = 0;
                    // can try again
                //}else{

                    boolean found = false;
                    boolean hit = (this.diceSides == 0);
                    //Entity blocker = getBlocker(this.attackX, this.attackY);
                    //if(blocker == null)
                    {
                        Vector entities = room.getEntitiesAt(this.attackX, this.attackY);
                        if(entities != null)
                        {
                            boolean good = (this.flags & GOOD_BIT) > 0;
                            Vector attackees = new Vector(2, 2);
                            for(int i=0; i<entities.size(); i++)
                            {
                                Entity entity = (Entity)entities.elementAt(i);
                                if(this.creates != null)
                                {
                                    Entity prototype = this.creates;
                                    if((prototype.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE &&
                                       (entity.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE)
                                    {
                                        room.addMessage("The "+entity.name+" is in the way");
                                        completed = 0;
                                        this.internalFlags = (byte)(this.internalFlags & ~POSITION_SET_FLAG);
                                        attackees.removeAllElements();
                                        break;
                                    }
                                }
                                // it's the only thing there, of course we're going to attack it!
                                if(entities.size() == 1 ||
                                        // if it's bad, we'll attack it, or if it's good we'll help it
                                        entity.side != 0 && (room.isFriendly(entity.side, this.entity.side) ^ !good) ||
                                        // if it's indescriminate, we're all in trouble
                                        ((this.flags & EFFECT_GROUP_BIT) != 0 && (entity != this.entity || good)))
                                {
                                    attackees.addElement(entity);
                                    found = true;
                                }
                            }
                            if(attackees.size() > 0)
                            {
                                // attack one or all of the entities, depending on the attack type
                                // if the effect group flag is set then it hits everything here
                                if((this.flags & EFFECT_GROUP_BIT) == 0)
                                {
                                    if(attackees.size() > 1)
                                    {
                                        while(attackees.size() > 1)
                                        {
                                            // remove a random entity
                                            int index = Math.abs(Entity.RANDOM.nextInt())%attackees.size();
                                            attackees.removeElementAt(index);
                                        }
                                    }
                                }
                                for(int i=0; i<attackees.size(); i++)
                                {
                                    Entity entity = (Entity)attackees.elementAt(i);
                                    // lets get him
                                    String attackResult;
                                    if(this.diceSides > 0)
                                    {
                                        // see if we hit
                                        int toHit = this.entity.level + this.entity.baseAttack;
                                        int defense = entity.getArmourClass(this.category);
                                        int roll = Math.abs(Entity.RANDOM.nextInt() % 20);
                                        if((roll + toHit) > (20 - defense) || roll == 19 ||
                                                (this.flags & ALWAYS_HITS_BIT)>0)
                                        {
                                            hit = true;
                                            int attack = this.entity.getAttack();
                                            attack = Entity.getAdjustedValue(attack, this.entity.entityCategory,  this.category,  false);
                                            for(int d=0; d<this.diceNumber; d++)
                                            {
                                                attack += Math.abs(Entity.RANDOM.nextInt() % this.diceSides)+1;
                                            }
                                            attack += this.diceBonus;
                                            int hurt = Entity.getAdjustedValue(attack, this.category, entity.entityCategory, false);
                                            if(hurt >= 0)
                                            {
                                                entity.setHealth((byte)(entity.health - hurt), this);
                                                entity.setLastStrike(this);
                                                attackResult = format(this.action, this.entity, entity.name, this);
                                                //attackResult = "The "+this.entity.name + " "+this.action+" the " + entity.name;
                                                if(this.confers != null && entity.health > 0)
                                                {
                                                    // we may confer an effect onto the target as long as it is alive
                                                    confer(entity);
                                                }
                                            }else{
                                                attackResult = "The "+this.entity.name + " fails to hurt the "+entity.name;
                                            }
                                        }else{
                                            attackResult = "The "+this.entity.name + " misses the "+entity.name;
                                        }
                                    }else if(this.diceSides < 0){
                                        // heal the monster
                                        int attack = 0;
                                        for(int d=0; d<this.diceNumber; d++)
                                        {
                                            attack += Math.abs(Entity.RANDOM.nextInt()) % -this.diceSides + 1;
                                        }
                                        entity.setHealth((byte)Math.min(entity.health + attack, (int)entity.maxHealth), this);
                                        //int restored = 0;
                                        attackResult = "The "+this.entity.name + " "+this.action+" the "+entity.name;
                                        entity.setLastStrike(this);
                                        hit = true;
                                        if(this.confers != null)
                                        {
                                            // we may confer an effect onto the target
                                            confer(entity);
                                        }
                                    }else{
                                        attackResult = format(this.action, this.entity, entity.name, this);
                                        entity.setLastStrike(this);
                                        hit = true;
                                        if(this.confers != null)
                                        {
                                            // we may confer an effect onto the target
                                            attackResult += confer(entity);
                                        }
                                    }
                                    room.addMessage(attackResult);
                                }
                            }
                        }
                    //}else{
                    //    this.internalFlags = (byte)(this.internalFlags & ~POSITION_SET_FLAG);
                    //    room.addMessage("The "+blocker.name+" is in the way");
                    //    completed = 0;
                    //    hit = false;
                    //}

                    if((this.internalFlags & POSITION_SET_FLAG)> 0)
                    {
                        if(!found && this.range > 0)
                        {
                            Entity prototype = this.creates;
                            if(prototype == null || (prototype.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_NONE)
                            {
                                //room.addMessage("The "+this.entity.name+" "+this.action+" thin air");
                                room.addMessage(format(this.action, this.entity, "thin air", this));
                            }else{
                                // assumes that collidable monsters do not have targets
                                room.addMessage("The "+this.entity.name+" "+this.action);
                            }
                        }
                        if(this.creates != null && hit)
                        {
                            // create in the specified radius
                            for(byte r=0; r<=this.radius; r++)
                            {
                                byte minx = (byte)(this.attackX - r);
                                byte miny = (byte)(this.attackY - r);
                                byte maxx = (byte)(this.attackX + r + 1);
                                byte maxy = (byte)(this.attackY + r + 1);

                                for(byte x = maxx; x > this.attackX; )
                                {
                                    x--;
                                    for(byte y = this.attackY; y < maxy; y++)
                                    {
                                        createRadiusEntity(x, y, r, room);
                                    }
                                }
                                for(byte x = this.attackX; x > minx; )
                                {
                                    x--;
                                    for(byte y = maxy; y > this.attackY; )
                                    {
                                        y--;
                                        createRadiusEntity(x, y, r, room);
                                    }
                                }

                                for(byte x = minx; x < this.attackX; x++)
                                {
                                    for(byte y = this.attackY; y > miny; )
                                    {
                                        y--;
                                        createRadiusEntity(x, y, r, room);
                                    }
                                }
                                for(byte x = this.attackX; x < maxx; x++)
                                {
                                    for(byte y = miny; y < this.attackY; y++)
                                    {
                                        createRadiusEntity(x, y, r, room);
                                    }
                                }
                            }
                        }
                        if((this.flags & CONSUME_BIT) > 0)
                        {
                            if(this.itemName != null)
                            {
                                // destroy the item as part of using this effect
                                Vector items = entity.getContainedEntities();
                                if(items != null)
                                {
                                    for(int i=0; i<items.size(); i++)
                                    {
                                        Entity item = (Entity)items.elementAt(i);
                                        if(item.name.equals(this.itemName))
                                        {
                                            // bye!
                                            items.removeElementAt(i);
                                            break;
                                        }
                                    }
                                }
                            }else{
                                this.entity.removeAction(this);
                            }
                        }
                        if((this.flags & KILL_PARENT_BIT) > 0)
                        {
                            //this.entity.setFlag(Entity.DEAD_FLAG, true);
                            this.entity.doing = this.entity.getSimpleAction(SimpleAction.DIE).copy(this.entity);
                        }else{
                            if(this.endsTurn())
                            {
                                this.entity.setFlag(Entity.DONE_FLAG, true);
                            }
                            this.entity.reaction = this;
                            room.addRoomListener(this);
                        }
                    }
                }
            }
        }
        this.completed += completed;
        return completed;
    }

    private final void createRadiusEntity(byte x, byte y, int r, Room room)
    {
        // check bounds conditions and immovable objects
        if(x >= 0 && x < room.width && y >= 0 && y < room.height)
        {
            Vector entitiesAt = room.getEntitiesAt(x, y);
            boolean blocked = room.getObstacle(x, y) != Entity.NONE_TYPE ||
                    (room.getTile(x, y) == Entity.NONE_TYPE && this.creates.entityType == Entity.MONSTER_TYPE);
            for(int i=0; i<entitiesAt.size() && !blocked; i++)
            {
                Entity entity = (Entity)entitiesAt.elementAt(i);
                if((entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_BOUNCE)
                {
                    blocked = true;
                }
            }
            if(!blocked)
            {

                if(r==0 || (inRange(x, y, this.attackX, this.attackY, r) &&
                         !inRange(x, y, this.attackX, this.attackY, r-1)))
                {
                    Entity created = room.copyEntity(this.creates, this.entity.side);
                    created.x = x;
                    created.y = y;
                    room.addContainedEntity(created);
                    if(created.entityType == Entity.MONSTER_TYPE)
                    {
                        room.queueEntity(created);
                    }else{
                        created.parent = this.entity;
                        // if it's an effect queue it immediately
                        room.queueEntityImmediately(created);
                    }
                }
            }
        }

    }

    private String confer(Entity entity)
    {
        String attackResult;
        int adjustedConferPercent;
        if(this.conferPercent < 100)
        {
            int diff = entity.level - this.entity.level;
            if(diff > 0)
            {
                adjustedConferPercent = this.conferPercent/2;
                for(int i=0; i<diff; i++)
                {
                    adjustedConferPercent = (adjustedConferPercent * 9) / 10;
                }
                adjustedConferPercent += this.conferPercent/2+1;
            }else{
                int adjustedRemainder = (100 - this.conferPercent)/2;
                for(int i=0; i<-diff; i++)
                {
                    adjustedRemainder = (adjustedRemainder * 9) / 10;
                }
                adjustedConferPercent = this.conferPercent + (100 - this.conferPercent)/2 - adjustedRemainder;
            }
            // adjust for the entity types
            adjustedConferPercent = Entity.getAdjustedValue(adjustedConferPercent,
                    this.entity.entityCategory, entity.entityCategory, false);
        }else{
            adjustedConferPercent = 100;
        }
        if((entity.entityType == Entity.MONSTER_TYPE ||
                ((this.confers instanceof SimpleAction) &&
                ((SimpleAction)this.confers).getMode() == SimpleAction.UNLOCK)) &&
                Math.abs(Entity.RANDOM.nextInt())%100 < adjustedConferPercent)
        {
            //attackResult = ", the "+entity.name+" is "+this.confers.getName();
            attackResult = "";
            Action doing = this.confers.copy(entity);
            if(doing instanceof SimpleAction)
            {
                SimpleAction simple = (SimpleAction)doing;
                if((simple.getMode() == SimpleAction.CONVERT || simple.getMode() == SimpleAction.STONE ||
                        simple.getMode() == SimpleAction.TURN_UNDEAD || simple.getMode() == SimpleAction.RECRUIT)
                    && simple.convertSide <= 0)
                {
                    simple.convertSide = this.entity.side;
                }
                if(simple.getMode() == SimpleAction.SWALLOW ||
                        simple.getMode() == SimpleAction.PICKPOCKET ||
                        simple.getMode() == SimpleAction.RECRUIT ||
                        simple.getMode() == SimpleAction.TURN_UNDEAD){
                    // yum!
                    simple.item = this.entity;
                }
            }
            if(doing.getTotalSteps() == 0)
            {
                Room room = (Room)entity.container;
                entity.doing = doing;
                room.queueEntityImmediately(entity);
            }else{
                // do it later
                entity.doing = doing;
            }
        }else if(this.diceSides > 0){
            attackResult = "";
        }else{
            attackResult = " with no effect";
        }
        return attackResult;
    }

    public String getAvailability(Entity entity)
    {
        String availability;
        if(Entity.meets(entity.entityCategory, this.prerequsiteCategory))
        {
            availability = super.getAvailability(entity);
            if((this.internalFlags & POSITION_SET_FLAG)>0)
            {
                if(availability == null)
                {
                    if(!this.inRange(this.attackX, this.attackY, entity.x, entity.y, this.range))
                    {
                        availability = "the target is out of range";
                    }else{
                        Room room = (Room)entity.container;
                        Entity blocker = getBlocker(this.attackX, this.attackY, entity.x, entity.y, room);
                        if(blocker != null)
                        {
                            availability = "the "+blocker.name+" is in the way";
                        }else{
                            // check whether we can actually create something here
                            if(this.creates != null)
                            {
                                Entity prototype = this.creates;
                                if((prototype.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE)
                                {
                                    Vector entities = room.getEntitiesAt(this.attackX, this.attackY);
                                    for(int i=0; i<entities.size(); i++)
                                    {
                                        Entity e = (Entity)entities.elementAt(i);
                                        if((e.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE)
                                        {
                                            availability = "the "+entity.name+" is in the way";
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            // TODO : list what's missing
            availability = "it can't use the "+this.itemName;
        }
        return availability;
    }

    public Action copy(Entity entity)
    {
        AttackAction copy = new AttackAction(entity, this.getName(), this.action, this.cost, this.range,
                this.diceNumber, this.diceSides, this.diceBonus, this.category);
        copy.creates = this.creates;
        copy.prerequsiteCategory = this.prerequsiteCategory;
        copy.itemName = this.itemName;
        //copy.consume = this.consume;
        //copy.span = this.span;
        copy.type = this.type;
        copy.confers = this.confers;
        copy.conferPercent = this.conferPercent;
        copy.radius = this.radius;
        //copy.killentity = this.killentity;
        //copy.effectGroup = this.effectGroup;
        //copy.endTurn = this.endTurn;
        //copy.alwaysHits = this.alwaysHits;
        copy.flags = this.flags;
        //copy.inherent = this.inherent;
        return copy;
    }

    public static final boolean inRange(int x, int y, int px, int py, int range)
    {
        int dx = px - x;
        int dy = py - y;

        int radiussq = dx*dx + dy * dy;
        return inRange(radiussq, range);
    }

    public static final boolean inRange(int radiussq, int range)
    {
        /*
        System.out.println("(x,y) = ("+x+","+y+")");
        System.out.println("(px, py) = ("+px+","+py+")");
        System.out.println("radiussq = "+radiussq);
        System.out.println("rangesq = "+((range * range) + (range+1)*(range+1))/2);
        */
        /*
        int rangesq = range * range;
        return radiussq <= rangesq ||
                radiussq > rangesq && radiussq < (range+1) * (range+1);
                */
        return radiussq <= ((range * range) + (range+1)*(range+1))/2;
    }

    public static final byte[][] getPoints(byte toX, byte toY, byte fromX, byte fromY)
    {
        int dx = toX - fromX;
        int dy = toY - fromY;

        byte[][] points;
        if(dx == 0 && dy == 0)
        {
            points = new byte[0][];
        }else if(dx == 0){
            byte miny;
            if(toY > fromY)
            {
                miny = fromY;
            }else{
                miny = toY;
            }
            points = new byte[Math.abs(dy)-1][];
            // simply walk along dy
            for(byte i=1; i<Math.abs(dy); i++)
            {
                points[i-1] = new byte[]{fromX, (byte)(miny+i)};
            }
        }else if(dy == 0){
            byte minx;
            if(toX > fromX)
            {
                minx = fromX;
            }else{
                minx = toX;
            }
            points = new byte[Math.abs(dx)-1][];
            // simply walk along dx
            for(byte i=1; i<Math.abs(dx); i++)
            {
                points[i-1] = new byte[]{(byte)(minx+i), fromY};
            }
        }else{
            if(Math.abs(dx) > Math.abs(dy))
            {
                points = new byte[Math.abs(dx)-1][];
                byte inc;
                if(dx > 0)
                {
                    inc = 1;
                }else{
                    inc = -1;
                }
                for(byte i=inc; i!=dx; i+=inc)
                {
                    byte x = (byte)(fromX + i);
                    int idy = i*dy;
                    int mod = idy % dx;
                    int adj = 0;
                    adj = (mod*2)/dx;
                    byte y = (byte)(fromY + idy/dx + adj);
                    points[Math.abs(i)-1] = new byte[]{x, y};
                }
            }else{
                points = new byte[Math.abs(dy)-1][];
                byte inc;
                if(dy > 0)
                {
                    inc = 1;
                }else{
                    inc = -1;
                }
                for(byte i=inc; i!=dy; i+=inc)
                {
                    byte y = (byte)(fromY + i);
                    int idx = i*dx;
                    int mod = idx % dy;
                    int adj = 0;
                    adj = (mod*2)/dy;
                    byte x = (byte)(fromX + idx/dy + adj);
                    points[Math.abs(i)-1] = new byte[]{x, y};
                }
            }
        }
        return points;
    }

    public static final Entity getBlocker(byte toX, byte toY, byte fromX, byte fromY, Room room)
    {
        Entity blocker = null;
        if(toX != fromX || toY != fromY)
        {
            byte[][] points = getPoints(toX, toY, fromX, fromY);
            for(int i=0; i<points.length && blocker == null; i++)
            {
                byte x = points[i][0];
                byte y = points[i][1];
                int obstacle = room.getObstacle(x, y);
                if(obstacle == 0)
                {
                    blocker = getBlocker(room.getEntitiesAt(x, y));
                }else{
                    // create a ficticious entity to represent the blocker
                    Entity madeup = new Entity();
                    madeup.flags |= Entity.COLLISION_EFFECT_BOUNCE;
                    //madeup.side = 0;
                    madeup.x = x;
                    madeup.y = y;
                    // TODO : obstacle names? tree, brick wall, etc...
                    madeup.name = "wall";
                    madeup.entityType = Entity.IMPASSABLE_TYPE;
                    madeup.entityTypeId = (byte)obstacle;
                    blocker = madeup;
                }
            }
        }
        return blocker;
    }

    private static final Entity getBlocker(Vector v)
    {
        Entity blocker = null;
        if(v != null)
        {
            for(int i=0; i<v.size(); i++)
            {
                Entity e = (Entity)v.elementAt(i);
                if(isBlocker(e))
                {
                    blocker = e;
                    break;
                }
            }
        }
        return blocker;
    }

    private static final boolean isBlocker(Entity entity)
    {
        return (entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_BOUNCE ||
                (entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_MONSTER;
    }

    public int getTotalSteps()
    {
        return this.cost;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isCompleted()
    {
        return this.cost <= this.completed && (this.internalFlags & UPDATED_ONCE_FLAG)>0;
    }

    public void entityUpdated(Room room, Entity entity, Action action, String[] messages)
    {
        if(!this.entity.isFlag(Entity.DEAD_FLAG))
        {
            counterAttack(this.entity, entity, action);
        }else{
            // looks like we died!
            room.removeRoomListener(this);
        }
    }
}
