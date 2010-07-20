package com.zonski.godhood.duels.game;

import com.zonski.godhood.duels.game.action.*;

import java.util.Vector;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Nov 4, 2003
 * Time: 10:09:17 AM
 * To change this template use Options | File Templates.
 */
public class Entity
{
    /**
     * Adjusts the value passed in to account for differences between a parent and a child when
     * inheriting values from contained entities
     * @param value the value, typically speed, attack, defense, or frequency
     * @param sourceCategory category of the object that we are inheriting from
     * @param targetCategory category of the object that is inheriting the value
     * @param divide indicates whether the value should be adjusted up or down
     * @return the value adjusted for category differences
     */
    public static final int getAdjustedValue(int value, byte sourceCategory, byte targetCategory, boolean divide)
    {
        /*
        int mult = 1;
        int div = 1;
        for(int i=0; i<32; i++)
        {
            if((sourceCategory & 1) > 0)
            {
                int target = targetCategory;
                for(int j=0; j<32; j++)
                {
                    if((target & 1) > 0)
                    {
                        int bigMult;
                        int smallMult;
                        if(i < j)
                        {
                            int big = mults[i];
                            int small = mults[j];
                            bigMult = (big >> j) & 0x01;
                            smallMult = (small >> i) & 0x01;
                        }else{
                            int big = mults[j];
                            int small = mults[i];
                            bigMult = (big >> i) & 0x01;
                            smallMult = (small >> j) & 0x01;
                        }
                        int found_mult;
                        int found_div;
                        if(bigMult == 0 && smallMult == 0)
                        {
                            found_mult = 1;
                            found_div = 1;
                        }else if(bigMult == 0 && smallMult != 0){
                            found_mult = 1;
                            found_div = 2;
                        }else if(bigMult != 0 && smallMult == 0){
                            found_mult = 3;
                            found_div = 2;
                        }else{
                            found_mult = 2;
                            found_div = 1;
                        }

                        mult *= found_mult;
                        div *= found_div;
                    }
                    target >>= 1;
                }
            }
            sourceCategory >>= 1;
        }
        if(divide)
        {
            value = ((value*div)/mult);
        }else{
            value = ((value*mult)/div);
        }
        */
        return value;
    }

    public static final byte COLLISION_EFFECT_NONE       = 0;
    public static final byte COLLISION_EFFECT_MONSTER    = 1;
    public static final byte COLLISION_EFFECT_BOUNCE     = 2;

    public static final byte COLLISION_EFFECT_MASK       = 3;
    public static final byte DEAD_FLAG                   = 4;
    public static final byte DONE_FLAG                   = 8;
    public static final byte BOSS_FLAG                   = 16;
    /**
     * The entity will not attack you unless you attack it, at which point it
     * will hunt you down and kill you
     */
    public static final byte PACIFIST_FLAG               = 32;

    public static final byte CATEGORY_NONE       = 0;
    public static final byte CATEGORY_NOT        = 1;
    public static final byte CATEGORY_ELEMENTAL  = 2;
    public static final byte CATEGORY_ANIMAL     = 4;
    public static final byte CATEGORY_HUMANOID   = 8;
    public static final byte CATEGORY_INTELLIGENT= 16;
    public static final byte CATEGORY_UNDEAD     = 32;
    public static final byte CATEGORY_FLYING     = 64;
    public static final byte CATEGORY_MAGIC      = (byte)(CATEGORY_FLYING << 1);
    public static final byte CATEGORY_ALL        = (byte)(CATEGORY_ELEMENTAL | CATEGORY_UNDEAD |
            CATEGORY_ANIMAL | CATEGORY_HUMANOID | CATEGORY_INTELLIGENT | CATEGORY_FLYING |
            CATEGORY_MAGIC);

    public static final boolean meets(byte category, byte filter)
    {
        boolean meets;
        if((filter & CATEGORY_NOT) > 0)
        {
            // it's a not filter, as long as the category isn't one of the specified values we're sweet
            meets = (category & filter) == 0;
        }else{
            // it's a normal filter, as long as the category meets all of the filter values, we're sweet
            meets = (category & filter) == filter;
        }
        return meets;
    }

    public static final Random RANDOM = new Random();

    /**
     * multipliers, categories interact as follows
     * category mentioned on both lines = *2
     * category mentioned on leading line = *1.5
     * category mentioned on trailing line = *0.5
     * category not mentioned on either = *1
     */
    /*
    private static final int[] mults = {
        // not
        CATEGORY_NONE,
        // earth
        CATEGORY_EARTH,
        // fire
        CATEGORY_FIRE | CATEGORY_WIND | CATEGORY_FLYING,
        // water
        CATEGORY_FIRE | CATEGORY_EARTH | CATEGORY_WATER | CATEGORY_ELECTRIC | CATEGORY_HOLY,
        // wind
        CATEGORY_EARTH | CATEGORY_WIND,
        // void
        CATEGORY_EARTH | CATEGORY_FIRE | CATEGORY_WATER | CATEGORY_WIND | CATEGORY_VOID | CATEGORY_UNDEAD,
        // electric
        CATEGORY_EARTH | CATEGORY_VOID | CATEGORY_ELECTRIC | CATEGORY_HOLY,
        // holy
        CATEGORY_VOID | CATEGORY_HOLY | CATEGORY_GOOD,
        // undead
        CATEGORY_FIRE | CATEGORY_HOLY | CATEGORY_UNDEAD | CATEGORY_EVIL | CATEGORY_MAGIC,
        // good
        CATEGORY_VOID | CATEGORY_HOLY | CATEGORY_GOOD,
        // evil
        CATEGORY_HOLY | CATEGORY_EVIL,
        // animal
        CATEGORY_NONE,
        // human
        CATEGORY_NONE,
        // intelligent
        CATEGORY_VOID | CATEGORY_UNDEAD,
        // flying
        CATEGORY_NONE,
        // magic
        CATEGORY_VOID | CATEGORY_MAGIC,
    };
    */

    // information bits (helps differentiate between types, used primarily in AI)
    public static final int IMPASSABLE_TYPE = 1;
    public static final int ITEM_TYPE       = 2;
    public static final int INHERIT_TYPE     = 3;
    public static final int MONSTER_TYPE    = 4;
    public static final int PHENOMINA_TYPE  = 6;

    public static final byte MIN_TILE           = 1;
    public static final byte MAX_TILE           = 12;
    public static final byte MIN_OBSTACLE       = MAX_TILE + 1;
    public static final byte MAX_OBSTACLE       = 24;
    public static final byte MIN_MONSTER        = MAX_OBSTACLE + 1;
    public static final byte MAX_MONSTER        = 100;

    public static final byte NUM_TILES          = MAX_TILE - MIN_TILE;
    public static final byte NUM_OBSTACLES      = MAX_OBSTACLE - MAX_TILE;
    public static final byte NUM_MONSTERS       = MAX_MONSTER - MAX_OBSTACLE;

    public static final byte NONE_TYPE           = 0;

    public static final byte LEFT_ARCH_TYPE      = Entity.MIN_MONSTER + 59;
    public static final byte RIGHT_ARCH_TYPE     = Entity.MIN_MONSTER + 60;
    public static final byte LEFT_DOOR_TYPE      = Entity.MIN_MONSTER + 65;
    public static final byte RIGHT_DOOR_TYPE     = Entity.MIN_MONSTER + 66;

    /**
     * Indicates the estimated strength of the entity, doesn't go up or down usually and
     * effects nothing if it does
     */
    public byte level;

    /**
     * Obtains the speed of the entity
     */
    public byte speed;

    /**
     * Time units for this entity
     */
    public byte unitsRemaining;

    /**
     * Obtains the frequency of updates for the entity
     */
    public byte frequency;

    /**
     * Information on whether the entity is dead, done, and its collision effect
     */
    public byte flags;

    protected Vector containedEntities;

    public Entity container;
    /**
     * The entity that created this entity
     */
    public Entity parent;

    public byte x;
    public byte y;

    /**
     * identifier of whose side the entity is on
     */
    public byte side;

    /**
     * Object representing decisions that the entity has made, can be anything
     */
    public Object thoughts;

    /**
     * The identifier of the type of the entity
     */
    public byte entityTypeId;

    /**
     * The basic type of the entity
     */
    public byte entityType;

    /**
     * The action that the entity is performing, if it's null then the entity is doing nothing
     */
    public Action doing;

    /**
     * What the entity is thinking, if it's null then the entity isn't thinking anything, thinking modifies
     * doing based on the available activities
     */
    public Action thinking;

    /**
     * The actions that this kind of entity can perform
     */
    public Vector actions;

    /**
     * Base attack for this monster
     */
    public byte baseAttack;

    /**
     * Base defense for this monster
     */
    public byte baseArmourClass;

    /**
     * Health for this monster
     */
    public byte health;

    /**
     * Maximum health for this monster
     */
    public byte maxHealth;

    /**
     * Category bits for this monster
     */
    public byte entityCategory;

    /**
     * human readable name of the entity
     */
    public String name;

    /**
     * The room listener indicating the current reactions for this entity,
     * can be null
     */
    public RoomListener reaction;

    public Entity()
    {
    }

    public final Vector getContainedEntities()
    {
        return this.containedEntities;
    }

    public final SimpleAction getSimpleAction(int type)
    {
        SimpleAction action = null;
        if(this.actions != null)
        {
            for(int i=0; i<this.actions.size(); i++)
            {
                Action found = (Action)this.actions.elementAt(i);
                if(found instanceof SimpleAction)
                {
                    SimpleAction simpleFound = (SimpleAction)found;
                    if(simpleFound.getMode() == type)
                    {
                        action = simpleFound;
                        break;
                    }
                }
            }
        }
        return action;
    }

    public final Action getAction(String name)
    {
        Action action = null;
        if(this.actions != null)
        {
            for(int i=0; i<this.actions.size(); i++)
            {
                Action found = (Action)this.actions.elementAt(i);
                if(found.getName().equals(name))
                {
                    action = found;
                    break;
                }
            }
        }
        if(action == null && this.containedEntities != null)
        {
            for(int i=0; i<this.containedEntities.size(); i++)
            {
                Entity found = (Entity)this.containedEntities.elementAt(i);
                action = found.getAction(name);
                if(action != null)
                {
                    break;
                }
            }
        }

        return action;
    }

    public void removeAction(AttackAction action)
    {
        /*
        Entity owner;
        if(action.itemName != null && !action.itemName.equals(this.name))
        {
            //System.out.println("got an action with the item name "+action.itemName);
            owner = null;
            Vector items = getContainedEntities();
            if(items != null)
            {
                outer: for(int i=0; i<items.size(); i++)
                {
                    Entity item = (Entity)items.elementAt(i);
                    if(item.name.equals(action.itemName))
                    {
                        Action found = getAction(action.getName());
                        if(found != null)
                        {
                            // we've got our name
                            owner = item;
                            break outer;
                        }
                    }
                }
            }
        }else{
            owner = this;
        }
        if(owner != null)
        {
        */
            Entity owner = this;
            Vector actions = owner.actions;
            if(actions != null)
            {
                for(int i=actions.size(); i>0; )
                {
                    i--;
                    Action foundAction = (Action)actions.elementAt(i);
                    if(foundAction.getName().equals(action.getName()))
                    {
                        actions.removeElementAt(i);
                        break;
                    }
                }
            }
        //}
    }

    public void addContainedEntity(Entity entity)
    {
        if(this.containedEntities == null)
        {
            this.containedEntities = new Vector(3, 3);
        }
        this.containedEntities.addElement(entity);
        entity.container = this;
    }

    public void removeContainedEntity(Entity entity)
    {
        if(this.containedEntities != null)
        {
            this.containedEntities.removeElement(entity);
        }
    }

    public final int getAttack()
    {
        int attack = this.baseAttack;
        if(this.containedEntities != null)
        {
            for(int i=0; i<this.containedEntities.size(); i++)
            {
                Entity contained = (Entity)this.containedEntities.elementAt(i);
                if(contained.baseAttack > 0)
                {
                    attack += getAdjustedValue(contained.getAttack(), contained.entityCategory, this.entityCategory, false);
                }
            }
        }
        return attack;
    }

    public final int getArmourClass(byte categoryAgainst)
    {
        int adjustedAc = 10 - this.baseArmourClass;
        int defense = getAdjustedValue(adjustedAc, this.entityCategory, categoryAgainst, false);
        if(this.containedEntities != null)
        {
            for(int i=0; i<this.containedEntities.size(); i++)
            {
                Entity contained = (Entity)this.containedEntities.elementAt(i);
                if(contained.baseArmourClass > 0 && contained.entityType == Entity.ITEM_TYPE)
                {
                    defense += getAdjustedValue(contained.getArmourClass(categoryAgainst), contained.entityCategory, this.entityCategory, false);
                }
            }
        }
        if(this.doing instanceof SimpleAction)
        {
            SimpleAction simple = ((SimpleAction)this.doing);
            if(simple.getMode() == SimpleAction.DEFEND)
            {
                // add in the attack value of the current attack
                AttackAction best = MonsterAction.getBestAttack(this, null, true, 0, false);
                if(best != null)
                {
                    defense += getAdjustedValue((best.diceNumber * best.diceSides)/2 + best.diceBonus,
                            this.entityCategory, categoryAgainst, false);
                }
            }else if(simple.getMode() == SimpleAction.DAZED){
                defense = defense/2;
            }
        }
        // convert defense into ac values again
        defense = 10 - defense;
        return defense;
    }

    public final void setHealth(byte health, Action source)
    {
        // objects with max-health of 0 are indestructable
        boolean lowered = this.health > health;
        int damage = this.health - health;
        this.health = health;
        if(this.maxHealth != 0)
        {
            if(health < 0)
            {
                SimpleAction dieAction = this.getSimpleAction(SimpleAction.DIE);

                // die now
                if(dieAction == null)
                {
                    // make up a new die action
                    dieAction = new SimpleAction(this, SimpleAction.DIE, (byte)0, Entity.NONE_TYPE, false);
                }else{
                    dieAction = (SimpleAction)dieAction.copy(this);
                }
                if(source != null)
                {
                    dieAction.convertSide = source.getParent().side;
                }
                Room room = (Room)this.container;
                room.dequeueEntity(this);
                this.doing = dieAction;
                room.queueEntityImmediately(this);
            }else if(lowered){

                SimpleAction hitAction = new SimpleAction(this, SimpleAction.BLEEDING, (byte)0, Entity.NONE_TYPE, true);
                hitAction.action = "The {0} gets mad";
                hitAction.type = (byte)damage;
                if(source != null)
                {
                    hitAction.convertSide = source.getParent().side;
                }
                this.doing = hitAction;
            }
        }
    }

    public void setLastStrike(Action attack)
    {
        // we'll remember this!
        if(this.isFlag(BOSS_FLAG | PACIFIST_FLAG))
        {
            this.thoughts = attack;
        }
    }

    /**
     * Creates a copy of this entity
     * @return a copy of this entity
     */
    public Entity copy()
    {
        Entity entity = new Entity();
        copyInto(entity);
        return entity;
    }

    protected void copyInto(Entity copied)
    {
        copied.entityTypeId = this.entityTypeId;
        copied.entityType = this.entityType;
        if(this.thinking != null)
        {
            copied.thinking = this.thinking.copy(copied);
        }
        if(this.doing != null)
        {
            copied.doing = this.doing.copy(copied);
        }
        copied.speed = this.speed;
        copied.frequency = this.frequency;
        if(this.containedEntities != null)
        {
            Vector copiedContainedEntities = new Vector(this.containedEntities.size(), 3);
            for(int i=0; i<this.containedEntities.size(); i++)
            {
                Entity contained = (Entity)this.containedEntities.elementAt(i);
                copiedContainedEntities.addElement(contained.copy());
            }
            copied.containedEntities = copiedContainedEntities;
        }
        copied.entityCategory = this.entityCategory;
        copied.flags = this.flags;
        copied.thoughts = this.thoughts;
        copied.baseAttack = this.baseAttack;
        copied.baseArmourClass = this.baseArmourClass;
        copied.health = this.health;
        copied.maxHealth = this.maxHealth;
        copied.name = this.name;
        copied.side = this.side;
        copied.level = this.level;
        if(this.actions != null)
        {
            Vector copiedActions = new Vector(this.actions.size(), 2);
            for(int i=0; i<this.actions.size(); i++)
            {
                copiedActions.addElement(this.actions.elementAt(i));
            }
            copied.actions = copiedActions;
        }
    }

    public final boolean isFlag(int mask)
    {
        return (this.flags & mask) > 0;
    }

    public final void setFlag(byte mask, boolean on)
    {
        if(on)
        {
            this.flags |= mask;
        }else{
            this.flags = (byte)(this.flags & ~mask);
        }
    }

}
