package com.zonski.godhood.duels.game.action;

import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.RoomListener;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 12, 2003
 * Time: 6:50:50 PM
 * To change this template use Options | File Templates.
 * <p>
 * Action that encompasses the functionality of pickup, putdown, endturn, die, self destruct, and defend
 */
public final class SimpleAction extends Action implements RoomListener
{
    public static final byte WAIT           = 0;
    public static final byte PICKUP         = 1;
    public static final byte PUTDOWN        = 2;
    public static final byte ENDTURN        = 3;
    public static final byte DIE            = 4;
    public static final byte SELF_DESTRUCT  = 5;
    public static final byte DEFEND         = 6;
    public static final byte DAZED          = 7;
    public static final byte EXIT           = 8;
    public static final byte CONVERT        = 9;
    public static final byte TELEPORT       = 10;
    public static final byte POLYMORPH      = 11;
    public static final byte STONE          = 12;
    public static final byte ROOM_WIN       = 13;
    public static final byte HEAL           = 14;
    public static final byte HURT           = 15;
    public static final byte BLEEDING       = 16;
    public static final byte TALK           = 17;
    public static final byte PROVOKE        = 18;
    public static final byte FRIENDLY       = 19;
    public static final byte UNFRIENDLY     = 20;
    public static final byte SWALLOW        = 21;
    public static final byte UNLOCK         = 22;
    public static final byte LEARN          = 23;
    public static final byte RECRUIT        = 24;
    public static final byte PRAY           = 25;
    public static final byte TURN_UNDEAD    = 26;
    public static final byte PICKPOCKET     = 27;
    public static final byte ROOM_LOSE      = 28;
    public static final byte TRANSMUTATION  = 29;

    public static final byte VISIBLE_FLAG = 0x01;
    public static final byte SILENT_FLAG  = 0x02;
    private static final byte IS_RUN_FLAG = 0x08;

    public byte flags;
    public byte filter = Entity.CATEGORY_NONE;
    private byte mode;
    private byte cost;
    public Object item;
    /**
     * The type of object that will be created, or picked up by this action
     */
    public byte type;
    public String name;
    /**
     * String describing what happens when this action is performed, can be
     * null, in which case an action is pieced together from the name
     */
    public String action;
    /**
     * Used when the mode is CONVERT, the side to convert the entity to
     * this variable is also used when the mode is HEAL to indicate the amount to heal by
     */
    public byte convertSide;
    /**
     * Used when the mode is EXIT, the name of the exit to use
     */
    public String exitName;

    public SimpleAction(Entity entity, byte mode, byte cost, byte type, boolean silent)
    {
        this.entity = entity;
        this.mode = mode;
        this.cost = cost;
        this.type = type;
        if(silent)
        {
            this.flags |= SILENT_FLAG;
        }
        if(mode != DIE && mode != DAZED && mode != TALK)
        {
            this.flags |= VISIBLE_FLAG;
        }
    }

    public final boolean endsTurn()
    {
        return mode == DIE || mode == DAZED || mode == ENDTURN || mode == POLYMORPH || mode == DEFEND ||
                mode == TALK || mode == PROVOKE || mode == EXIT || mode == STONE || mode == ROOM_WIN ||
                mode == TURN_UNDEAD || mode==RECRUIT || mode == PRAY;
    }

    public final int update()
    {
        int timeRemaining = this.entity.unitsRemaining;
        int cost;
        if(this.entity == null || Entity.meets(this.entity.entityCategory, this.filter) || this.mode == TRANSMUTATION)
        {
            cost = this.cost;
            switch(this.mode)
            {
                default:
                case WAIT:
                    // do nothing
                    break;
                case PICKUP:
                    if(pickup() == 0)
                    {
                        cost = 0;
                    }
                    break;
                case PUTDOWN:
                    putdown();
                    break;
                case ENDTURN:
                    endturn();
                    break;
                case DIE:
                    if(this.entity.entityType == Entity.MONSTER_TYPE)
                    {
                        // wake everyone up
                        unfriendly("The {0} screams loudly", this.convertSide);
                    }
                    die();
                    break;
                case SELF_DESTRUCT:
                    selfdestruct();
                    break;
                case DEFEND:
                    defend();
                    // do nothing
                    break;
                case DAZED:
                    Room room = (Room)this.entity.container;
                    if(cost >= timeRemaining)
                    {
                        this.entity.setFlag(Entity.DONE_FLAG, true);
                        this.cost -= timeRemaining;
                        cost = timeRemaining;
                        room.addMessage(format(this.action, this.entity.name, this.name, null));
                    }else{
                        room.addMessage("The "+this.entity.name+" can move again");
                    }
                    break;
                case EXIT:
                    exit();
                    break;
                case CONVERT:
                    convert();
                    break;
                case TELEPORT:
                    teleport();
                    break;
                case POLYMORPH:
                    polymorph();
                    break;
                case STONE:
                    //stone();
                    break;
                case ROOM_WIN:
                    win();
                    break;
                case HEAL:
                    heal();
                    break;
                case HURT:
                    hurt();
                    break;
                case PROVOKE:
                    provoke();
                    break;
                case TALK:
                    talk();
                    break;
                //case FRIENDLY:
                    //friendly(this.action, this.convertSide);
                    //break;
                case UNFRIENDLY:
                case BLEEDING:
                    unfriendly(this.action, this.convertSide);
                    break;
                case SWALLOW:
                    swallow();
                    break;
                case UNLOCK:
                    unlock();
                    break;
                case LEARN:
                    learn();
                    break;
                case RECRUIT:
                    recruit();
                    break;
                case PRAY:
                    pray();
                    break;
                case TURN_UNDEAD:
                    turnUndead();
                    break;
                case PICKPOCKET:
                    pickPocket();
                    break;
                case TRANSMUTATION:
                    transmutation();
                    break;
            }
        }else{
            cost = 0;
            // can't do this to the entity
            Room room = (Room)this.entity.container;
            room.addMessage("The "+this.entity.name+" is immune to being "+this.name);
            // TODO : this may cause a bug, we're assuming that this effect has been instantaneously conferred
            // from an attack here
            if(this.cost == 0)
            {
                this.entity.setFlag(Entity.DONE_FLAG, true);
            }
        }
        this.flags |= IS_RUN_FLAG;
        return cost;
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
        return (this.flags & IS_RUN_FLAG) > 0 && this.mode != SELF_DESTRUCT;
    }

    public final String getAvailability(Entity entity)
    {
        String availability;
        if(this.mode == DAZED)
        {
            availability = null;
        }else{
            availability = super.getAvailability(entity);
        }
        return availability;
    }

    public final Action copy(Entity entity)
    {
        SimpleAction action = new SimpleAction(entity, this.mode, this.cost, this.type, (this.flags & SILENT_FLAG) > 0);
        //action.inherent = this.inherent;
        action.item = this.item;
        action.name = this.name;
        action.exitName = this.exitName;
        action.convertSide = this.convertSide;
        action.filter = this.filter;
        action.action = this.action;
        return action;
    }

    public int getMode()
    {
        return this.mode;
    }

    /**
     * Makes the side for this monster unfriendly
     */
    private final void unfriendly(String action, int side)
    {
        Room room = (Room)this.entity.container;
        boolean friendly = room.isFriendly(this.entity.side, side);
        if(friendly && side!=this.entity.side)
        {
            room.addMessage(format(action, this.entity.name, this.getName(), null));
            room.friendlySideFilter &= ~(0x01 << this.entity.side);
        }
    }

    /**
     * Makes the side for this monster friendly
     */
    /*
    private final void friendly(String action, int side)
    {
        Room room = (Room)this.entity.container;
        boolean friendly = room.isFriendly(this.entity.side, side);
        if(!friendly)
        {
            room.addMessage(format(action, this.entity.name, this.getName(), null));
            room.friendlySideFilter |= (0x01 << this.entity.side);
        }
    }
    */

    private final void talk()
    {
        // presents the action message
        if(this.action != null)
        {
            Room room = (Room)this.entity.container;
            room.addMessage(format(this.action, this.entity.name, this.getName(), null));
        }
        this.entity.setFlag(Entity.DONE_FLAG, true);
    }

    private final void provoke()
    {
        // causes the entity to perform a simple action that it has internally
        // can be used for a variety of things, eg
        // one monster talking to another (provokes a talk)
        // one monster causing the death of another (provokes death)
        // one monster causing another to drop an item (provokes put down)
        Action found = this.entity.getSimpleAction(this.convertSide);
        if(found != null)
        {
            this.entity.doing = found.copy(this.entity);
        }else{
            // he's done
            this.entity.setFlag(Entity.DONE_FLAG, true);
        }
    }

    private void hurt()
    {
        hurt(this.convertSide, this.action);
    }

    private void hurt(int damage, String action)
    {
        this.entity.setHealth((byte)(this.entity.health - damage), null);
        Room room = (Room)this.entity.container;
        if(action != null)
        {
            room.addMessage(format(action, this.entity.name, this.name, Integer.toString(damage)));
        }
    }

    private void heal()
    {
        Room room = (Room)this.entity.container;
        // heal the entity by "convert side" health points
        byte recover = (byte)Math.min(this.entity.health + this.convertSide, this.entity.maxHealth);
        int recovered = recover - this.entity.health;
        if(recovered != 0)
        {
            this.entity.setHealth(recover, null);
        }
        if(this.action != null)
        {
            room.addMessage(format(this.action, this.entity.name, this.name, Integer.toString(recovered)));
        }
        // TODO : present a message indicating that the entity is already at maximum health
    }

    private void win()
    {
        Room room = (Room)this.entity.container;
        room.winner = this.entity;
        room.mode = Room.WIN_EXTERNAL;
    }

    private void exit()
    {
        // get out of here
        ((Room)this.entity.container).exit(this.exitName, this.entity);
    }

    private void convert()
    {
        convert(this.action);
    }

    private void convert(String action)
    {
        if(this.entity.side != this.convertSide)
        {
            this.entity.side = this.convertSide;
            if(action != null)
            {
                Room room = (Room)this.entity.container;
                // TODO : give the sides names
                room.addMessage(format(action, this.entity.name, this.name, Integer.toString(this.convertSide)));
            }
        }
        // we're done now, next turn we work for someone else
        this.entity.setFlag(Entity.DONE_FLAG, true);
    }

    /*
    private void stone()
    {
        Room room = (Room)this.entity.container;
        Entity statue = room.copyEntity((Entity)this.item, this.convertSide);
        // add the target to the statue
        statue.addContainedEntity(this.entity);
        // remove the contained entity from the room
        room.removeContainedEntity(this.entity);
        room.dequeueEntity(this.entity);
        statue.x = this.entity.x;
        statue.y = this.entity.y;
        room.addContainedEntity(statue);
        this.entity.setFlag(Entity.DONE_FLAG, true);
        this.entity.setFlag(Entity.DEAD_FLAG, true);
        room.queueEntity(statue);
        if(this.action != null)
        {
            room.addMessage(format(this.action, this.entity.name, this.name, null));
        }
    }
    */

    private void polymorph()
    {
        // turns the entity into something else
        Room room = (Room)this.entity.container;
        Entity to;
        if(this.item == null)
        {
            // obtain similar a type from the entities present in the room
            to = null;
            byte fromType = this.entity.entityType;
            Vector contained = room.getContainedEntities();
            int startIndex = Math.abs(Entity.RANDOM.nextInt())%contained.size();
            int index = startIndex;
            do
            {
                Entity found = (Entity)contained.elementAt(index);
                if(found.entityType == fromType && found.entityTypeId != this.entity.entityTypeId && !found.isFlag(Entity.BOSS_FLAG))
                {
                    to = found;
                    break;
                }
                index = (index+1)%contained.size();
            }while(index != startIndex);
        }else{
            to = ((Entity)this.item);
        }
        if(to != null)
        {
            Entity polymorphTo = to;
            String a;
            char first = Character.toLowerCase(polymorphTo.name.charAt(0));
            if((first == 'a') || (first == 'e') || (first == 'i') || (first == 'o') || (first == 'u'))
            {
                a = "an";
            }else{
                a = "a";
            }
            if(this.action != null)
            {
                room.addMessage(format(this.action, this.entity.name, this.name, a+" "+polymorphTo.name));
            }
            // do polymorph
            this.entity.entityTypeId = polymorphTo.entityTypeId;
            this.entity.setFlag(Entity.DONE_FLAG, true);

            this.entity.baseAttack = polymorphTo.baseAttack;
            this.entity.baseArmourClass = polymorphTo.baseArmourClass;
            this.entity.flags = (byte)((this.entity.flags & ~Entity.COLLISION_EFFECT_MASK) | (polymorphTo.flags & Entity.COLLISION_EFFECT_MASK));
            this.entity.entityCategory = polymorphTo.entityCategory;
            this.entity.frequency = polymorphTo.frequency;
            //this.entity.health = (byte)((polymorphTo.maxHealth * this.entity.health)/this.entity.maxHealth);
            this.entity.maxHealth = polymorphTo.maxHealth;
            this.entity.health = polymorphTo.maxHealth;
            this.entity.name = polymorphTo.name;
            this.entity.speed = polymorphTo.speed;
            this.entity.level = polymorphTo.level;

            // remove the inherent actions
            if(this.entity.actions == null)
            {
                this.entity.actions = new Vector(3, 3);
            }else{
                this.entity.actions.removeAllElements();
            }
            /*
            for(int i=this.entity.actions.size(); i>0; )
            {
                i--;
                Action action = (Action)this.entity.actions.elementAt(i);
                //if(action.inherent)
                //{
                this.entity.actions.removeElementAt(i);
                //}
            }
            */
            // add the new actions
            if(polymorphTo.actions != null)
            {
                for(int i=0; i<polymorphTo.actions.size(); i++)
                {
                    Action action = (Action)polymorphTo.actions.elementAt(i);
                    //if(action.inherent)
                    //{
                    //Action copy = action.copy(this.entity);
                    this.entity.actions.addElement(action);
                    //}
                }
            }
        }else{
            room.addMessage("The "+this.entity.name+" feels the same");
        }
    }

    private void teleport()
    {
        // find an unoccupied spot in the room
        Room room = (Room)this.entity.container;
        int goes = 0;
        int x = -1;
        int y = -1;
        while(goes < 10 && x < 0 && y < 0)
        {
            int tx = Math.abs(Entity.RANDOM.nextInt()) % room.width;
            int ty = Math.abs(Entity.RANDOM.nextInt()) % room.height;
            if(room.getTile(tx, ty) > 0 && room.getObstacle(tx, ty) == 0)
            {
                Vector entities = room.getEntitiesAt(tx, ty);
                if(entities != null)
                {
                    boolean ok = true;
                    for(int i=0; i<entities.size(); i++)
                    {
                        Entity entity = (Entity)entities.elementAt(i);
                        if((entity.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE)
                        {
                            ok = false;
                            break;
                        }
                    }
                    if(ok)
                    {
                        x = tx;
                        y = ty;
                    }
                }else{
                    x = tx;
                    y = ty;
                }
            }
            goes ++;
        }
        if(x >= 0 && y >= 0)
        {
            this.entity.x = (byte)x;
            this.entity.y = (byte)y;
            this.entity.setFlag(Entity.DONE_FLAG, true);
            if(this.action != null)
            {
                room.addMessage(format(action, this.entity.name, this.name, null));
            }
        }else{
            room.addMessage("The "+this.entity.name+" failed to "+this.name);
        }
    }

    private int pickup()
    {
        Entity entity = this.entity;
        Room room = (Room)entity.container;
        Vector items = room.getEntitiesAt(entity.x, entity.y);
        int pickedUp = 0;
        if(this.item == null)
        {
            // get everything
            for(int i=items.size(); i>0; )
            {
                i--;
                Entity item = (Entity)items.elementAt(i);
                if(item.entityType  == Entity.ITEM_TYPE)
                {
                    pickedUp ++;
                    // pick it up
                    pickup(this.entity, item);
                }
            }
        }else{
            for(int i=items.size(); i>0; )
            {
                i--;
                Entity item = (Entity)items.elementAt(i);
                if(item.entityType == Entity.ITEM_TYPE &&
                        item == this.item)
                {
                    pickedUp ++;
                    // pick it up
                    pickup(this.entity, item);
                    break;
                }
            }
        }
        if(pickedUp == 0)
        {
            room.addMessage("There's nothing here");
        }
        return pickedUp;
    }

    private final void pickup(Entity entity, Entity item)
    {
        Room room = (Room)this.entity.container;
        item.container.removeContainedEntity(item);
        entity.addContainedEntity(item);
        if(this.action != null)
        {
            room.addMessage(format(this.action, entity.name, this.name, item.name));
        }
    }

    private void putdown()
    {
        Room room = (Room)this.entity.container;
        Vector entities = this.entity.getContainedEntities();
        Entity found = null;
        if(entities != null)
        {
            for(int i=0; i<entities.size(); i++)
            {
                Entity entity = (Entity)entities.elementAt(i);
                if(entity.entityTypeId == this.type || this.type == Entity.NONE_TYPE)
                {
                    found = entity;
                    break;
                }
            }
        }
        if(found != null)
        {
            Entity held = found;
            held.x = this.entity.x;
            held.y = this.entity.y;
            this.entity.removeContainedEntity(held);
            if(this.action != null)
            {
                room.addMessage(format(this.action, this.entity.name, this.name,  held.name));
            }
            if(room.getTile(this.entity.x, this.entity.y) == 0)
            {
                room.addMessage("The "+held.name+" falls down a chasm");
            }else{
                room.addContainedEntity(held);
            }
        }
    }

    private void endturn()
    {
        this.entity.setFlag(Entity.DONE_FLAG, true);
        this.entity.doing = null;
        this.entity.reaction = this;
        ((Room)this.entity.container).addRoomListener(this);
    }

    private void die()
    {
        die(this.action);
    }

    private void die(String action)
    {
        Vector contained = this.entity.getContainedEntities();
        Room room = (Room)this.entity.container;
        if(contained != null && contained.size() > 0)
        {
            boolean fell = false;
            for(int i=0; i<contained.size(); i++)
            {
                Entity child = (Entity)contained.elementAt(i);
                child.x = this.entity.x;
                child.y = this.entity.y;
                // some things die when they enter possession of another object, revive them
                if(room.getTile(this.entity.x, this.entity.y) != 0 ||
                        (child.entityCategory & Entity.CATEGORY_FLYING) != 0)
                {
                    child.setFlag(Entity.DEAD_FLAG, false);
                    this.entity.container.addContainedEntity(child);
                    room.queueEntity(child);
                }else{
                    fell = true;
                }
            }
            if(fell)
            {
                room.addMessage("Some stuff falls down a chasm");
            }
        }
        this.entity.container.removeContainedEntity(this.entity);
        this.entity.setFlag(Entity.DEAD_FLAG, true);
        this.entity.setFlag(Entity.DONE_FLAG, true);
        if((this.flags & SILENT_FLAG) == 0)
        {
            if(action != null)
            {
                room.addMessage(format(action, this.entity.name, this.name, null));
            }
        }

        if(this.item != null)
        {
            // assumes that the object created is on the same side
            Entity created = room.copyEntity((Entity)this.item, this.entity.side);
            created.x = this.entity.x;
            created.y = this.entity.y;
            room.addContainedEntity(created);
            room.queueEntityImmediately(created);
        }
    }

    private void swallow()
    {
        if(this.item != null)
        {
            // we eat him
            this.entity.setFlag(Entity.DONE_FLAG, true);
            this.entity.setFlag(Entity.DEAD_FLAG, true);
            pickup((Entity)this.item, this.entity);
        }
    }

    private void unlock()
    {
        // convert the entity into an unlocked version of itself
        boolean door;
        if(this.entity.entityTypeId == Entity.LEFT_DOOR_TYPE)
        {
            door = true;
            this.entity.entityTypeId = Entity.LEFT_ARCH_TYPE;
        }else if(entity.entityTypeId == Entity.RIGHT_DOOR_TYPE){
            door = true;
            this.entity.entityTypeId = Entity.RIGHT_ARCH_TYPE;
        }else{
            door = false;
        }
        Room room = (Room)this.entity.container;
        if(door)
        {
            this.entity.flags = (byte)((this.entity.flags & ~Entity.COLLISION_EFFECT_MASK) | Entity.COLLISION_EFFECT_NONE);
            this.entity.entityType = Entity.INHERIT_TYPE;
            room.addMessage("The "+this.entity.name+" is unlocked");
        }else{
            // TODO : if a key is used here then we'll actually lose the key, even though it hasn't been used
            room.addMessage("The "+this.entity.name+" cannot be unlocked");
        }
    }

    private void learn()
    {
        // teach the learner the action
        Room room = (Room)this.entity.container;
        if(!this.entity.actions.contains(this.item))
        {
            this.entity.actions.addElement(this.item);
            if(this.action != null)
            {
                room.addMessage(format(this.action, this.entity.name, this.name, null));
            }
        }else{
            room.addMessage("The "+this.entity.name+" already knows that spell");
        }
    }

    private void selfdestruct()
    {
        Action action;
        Room room = (Room)this.entity.container;
        if(this.entity.actions.size() > 0 && room.getEntitiesAt(this.entity.x, this.entity.y).size() > 1)
        {
            action = (Action)this.entity.actions.elementAt(0);
            this.entity.actions.removeElementAt(0);
            action = action.copy(this.entity);
            if(action instanceof AttackAction)
            {
                AttackAction attack = (AttackAction)action;
                attack.setPosition(this.entity.x, this.entity.y);
                // don't want to end the turn, want to die!
                attack.flags &= ~AttackAction.END_TURN_BIT;
            }
            this.entity.doing = action;
        }else{
            SimpleAction die = new SimpleAction(this.entity, SimpleAction.DIE, (byte)0, Entity.NONE_TYPE, true);
            this.entity.doing = die;
        }
    }

    private void recruit()
    {
        Room room = (Room)this.entity.container;
        if(room.isFriendly(this.entity.side, this.convertSide) &&
                this.item instanceof Entity &&
                ((Entity)this.item).level/2 > this.entity.level)
        {
            // can only recruit entities that are of a much lower level than the recruiter
            convert();
        }else{
            room.addMessage("The "+this.entity.name+" is unimpressed");
        }
        this.entity.setFlag(Entity.DONE_FLAG, true);
    }

    private void pray()
    {
        Room room = (Room)this.entity.container;
        if(room.actions != null && room.actions.size() > 0)
        {
            // the effect of praying is dependent on the room
            Action prayer = (Action)room.actions.elementAt(0);
            room.actions.removeElementAt(0);
            prayer = prayer.copy(this.entity);
            prayer.update();
        }else{
            room.addMessage("you feel you are being ignored");
        }
        this.entity.setFlag(Entity.DONE_FLAG, true);
    }

    private void turnUndead()
    {
        // may kill the undead, convert them, or injure them
        // TODO : scare them
        int result = Math.abs(Entity.RANDOM.nextInt()%Math.max(this.entity.level, 1));
        switch(result)
        {
            case 0:
                // kill
                Action die = this.entity.getSimpleAction(DIE);
                if(die == null)
                {
                    die("The {0} dies");
                }else{
                    die = die.copy(this.entity);
                    die.update();
                }
                break;
            case 1:
                // convert
                convert("The {0} kneels before you");
                break;
            default:
                // injure
                int damage = this.entity.health / result;
                hurt(damage, "The {0} develops several wounds");
                break;
        }
        this.entity.setFlag(Entity.DONE_FLAG, true);
    }

    private void pickPocket()
    {
        // gives a random item (if it has any) to the attacker
        Room room = (Room)this.entity.container;
        Entity stolen;
        if(this.item != null)
        {
            Entity thief = (Entity)this.item;
            // we steal from him
            Vector contained = this.entity.getContainedEntities();
            stolen = null;
            if(contained != null && contained.size() > 0)
            {
                for(int i=0; i<contained.size(); i++)
                {
                    Entity item = (Entity)contained.elementAt(i);
                    if(item.entityType == Entity.ITEM_TYPE)
                    {
                        contained.removeElementAt(i);
                        thief.addContainedEntity(item);
                        if(this.action != null)
                        {
                            room.addMessage(format(action, this.entity.name, thief.name, item.name));
                        }
                        stolen = item;
                        break;
                    }
                }
            }else{
                stolen = null;
            }
            if(stolen == null)
            {
                room.addMessage("The "+thief.name+" finds nothing");
            }
        }
    }

    private final void transmutation()
    {
        // boost the inherent armour class for the entity
        this.entity.baseArmourClass -= this.convertSide;
        // modify the underlying structure of the entitys makeup
        this.entity.entityCategory |= this.filter;
        // eg. stone-skin -> earth, undeath -> undead, learning -> magic, intelligence -> brains
        if(this.action != null)
        {
            Room room = (Room)this.entity.container;
            room.addMessage(format(this.action, this.entity.name, this.name, null));
        }
    }

    private final void defend()
    {
        this.entity.setFlag(Entity.DONE_FLAG, true);
    }

    public final boolean isVisible()
    {
        return (this.flags & VISIBLE_FLAG) > 0;
    }

    public void entityUpdated(Room room, Entity entity, Action action, String[] messages)
    {
        AttackAction.counterAttack(this.entity, entity, action);
    }
}
