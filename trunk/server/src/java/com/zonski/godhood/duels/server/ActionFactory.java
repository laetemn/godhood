package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.action.SimpleAction;
import com.zonski.godhood.duels.game.action.AttackAction;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Action;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 10/03/2004
 * Time: 14:01:35
 * To change this template use Options | File Templates.
 * <p>
 * Factory to create simple actions
 */
public class ActionFactory
{
    public static final String WAIT_NAME = "Wait";
    public static final String PICKUP_NAME = "Pick up";
    public static final String PUTDOWN_NAME = "Put down";
    public static final String ENDTURN_NAME = "End turn";
    public static final String DIE_NAME = "dies";
    //public static final String SELF_DESTRUCT_NAME = null;
    public static final String DEFEND_NAME = "Defend";
    public static final String DAZED_NAME = "Dazed";
    public static final String EXIT_NAME = "Leave room";
    public static final String EXIT_UP_NAME = "Ascend";
    public static final String EXIT_DOWN_NAME = "Descend";
    public static final String CONVERT_NAME = "Converted";
    public static final String TELEPORT_NAME = "Teleported";
    public static final String POLYMORPH_NAME = "Polymorphed";
    public static final String STONE_NAME = "Turned to Stone";
    //public static final String ROOM_WIN_NAME = null;
    public static final String HEAL_NAME= "Healed";
    public static final String HURT_NAME = "Wounded";
    public static final String BLEEDING_NAME = "Bleeding";
    //public static final String TALK_NAME = null;
    public static final String PROVOKE_NAME = "Provokes";
    //public static final String FRIENDLY_NAME = null;
    //public static final String UNFRIENDLY_NAME = null;

    private static final String[] NAMES = {WAIT_NAME, PICKUP_NAME, PUTDOWN_NAME, ENDTURN_NAME,
                                           DIE_NAME, null, DEFEND_NAME, DAZED_NAME, EXIT_NAME,
                                           CONVERT_NAME, TELEPORT_NAME, POLYMORPH_NAME, STONE_NAME,
                                           null, HEAL_NAME, HURT_NAME, BLEEDING_NAME,
                                           null, PROVOKE_NAME, null, null
    };

    private static final String[] ACTIONS = {
        "The {0} waits",
        "The {0} picks up the {2}",
        "The {0} puts down the {2}",
        null,
        "The {0} {1}",
        null,
        "The {0} defends",
        "The {0} is {1}",
        "The {0} leaves the room",
        "The {0} is {1}",
        "The {0} is {1}",
        "The {0} is {1}",
        "The {0} is {1}",
        null,
        "The {0} is {1}, regaining {2} points of health",
        "The {0} is {1}, losing {2} points of health",
        "The {0} suddenly looks angry",
        "The {0} has no reaction",
        null,
        "The {0} suddenly looks friendly",
        "The {0} suddenly looks angry",
    };

    public static final String getName(int mode, String[] names)
    {
        if(mode >= 0)
        {
            return names[mode];
        }else{
            return null;
        }
    }

    public static final ActionFactory INSTANCE = new ActionFactory();

    private ArrayList existingActions;

    public ActionFactory()
    {
        this.existingActions = new ArrayList();
    }

    public Action create(Entity owner, int mode, int cost, byte type, boolean silent)
    {
        SimpleAction result = reallyCreate(owner, mode, cost, type, silent);
        return checkResult(result);
    }

    public Action createDie(Entity owner)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.DIE, 0, Entity.NONE_TYPE, false);
        return result;
    }

    public Action createDie(Entity owner, String name, String action, byte creates, boolean silent)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.DIE, 0, creates, silent);
        result.action = action;
        result.name = name;
        return checkResult(result);
    }

    public Action createEndturn(Entity owner)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.ENDTURN, 0, Entity.NONE_TYPE, false);
        return checkResult(result);
    }

    public Action createPickup(Entity owner)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.PICKUP, 2, Entity.NONE_TYPE, false);
        return checkResult(result);
    }

    public Action createPutdown(Entity owner, byte itemType)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.PUTDOWN, 2, Entity.NONE_TYPE, false);
        result.type = itemType;
        return checkResult(result);
    }

    public Action createDefend(Entity owner)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.DEFEND, 3, Entity.NONE_TYPE, false);
        return checkResult(result);
    }

    public Action createDazed(Entity owner, int cost)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.DAZED, cost, Entity.NONE_TYPE, false);
        return checkResult(result);
    }

    public Action createDazed(Entity owner, int cost, String action, byte filter)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.DAZED, cost, Entity.NONE_TYPE, false);
        result.action = action;
        result.filter = filter;
        return checkResult(result);
    }

    public Action createExit(Entity owner, String name, String action, String exitName)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.EXIT, 4, Entity.NONE_TYPE, false);
        result.name = name;
        result.action = action;
        result.exitName = exitName;
        return checkResult(result);
    }

    public Action createConvert(Entity owner, String action, byte side, byte filter)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.CONVERT, 0, Entity.NONE_TYPE, false);
        result.convertSide = side;
        result.action = action;
        result.filter = filter;
        return checkResult(result);
    }

    public Action createTeleport(Entity owner, int cost)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.TELEPORT, cost, Entity.NONE_TYPE, false);
        return checkResult(result);
    }

    public Action createPolymorph(Entity owner, int cost, byte targetType)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.POLYMORPH, cost, targetType, false);
        return checkResult(result);
    }

    public Action createStone(Entity owner)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.STONE, (byte)0, Entity.NONE_TYPE, false);
        return checkResult(result);
    }

    public Action createTalk(Entity owner)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.TALK, (byte)0, Entity.NONE_TYPE, false);
        if((result.flags & SimpleAction.VISIBLE_FLAG) > 0)
        {
            result.flags ^= SimpleAction.VISIBLE_FLAG;
        }
        return checkResult(result);
    }

    public Action createTalk(Entity owner, String action)
    {
        SimpleAction result = reallyCreate(owner, SimpleAction.TALK, (byte)0, Entity.NONE_TYPE, false);
        result.action = action;
        if((result.flags & SimpleAction.VISIBLE_FLAG) > 0)
        {
            result.flags ^= SimpleAction.VISIBLE_FLAG;
        }
        return checkResult(result);
    }

    public Action createChat(Entity owner)
    {
        AttackAction result = new AttackAction(null, "Chat", "chats to",
                (byte)0, (byte)1, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_NONE);
        result.conferPercent = 100;
        result.confers = createProvoke(owner, SimpleAction.TALK);
        result.flags &= ~AttackAction.END_TURN_BIT;
        return checkResult(result);
    }

    public Action createProvoke(Entity owner, byte provokes)
    {
        SimpleAction result = new SimpleAction(null, SimpleAction.PROVOKE, (byte)0, Entity.NONE_TYPE, false);
        result.convertSide = provokes;
        return checkResult(result);
    }

    public Action createAttack(Entity owner, String name, String action, byte cost, byte range, byte diceNum,
                               byte diceSides, byte diceBonus, byte category, byte type)
    {
        AttackAction result = new AttackAction(owner, name, action, cost, range, diceNum, diceSides, diceBonus, category);
        result.type = type;
        return checkResult(result);
    }

    public Action createAttack(Entity owner, String name, String action, byte cost, byte range, byte diceNum,
                               byte diceSides, byte diceBonus, byte category, byte type, boolean alwaysHits,
                               boolean effectGroup, boolean consume, boolean killParent)
    {
        AttackAction result = new AttackAction(owner, name, action, cost, range, diceNum, diceSides, diceBonus, category);
        result.type = type;
        if(effectGroup)
        {
            result.flags |= AttackAction.EFFECT_GROUP_BIT;
        }
        if(alwaysHits)
        {
            result.flags |= AttackAction.ALWAYS_HITS_BIT;
        }
        if(killParent)
        {
            result.flags |= AttackAction.KILL_PARENT_BIT;
        }
        if(consume)
        {
            result.flags |= AttackAction.CONSUME_BIT;
        }
        return checkResult(result);
    }

    public Action createAttack(Entity owner, String name, String action, byte cost, byte range, byte diceNum,
                               byte diceSides, byte diceBonus, byte category, byte type, Action confers, byte conferPercent)
    {
        AttackAction result = new AttackAction(owner, name, action, cost, range, diceNum, diceSides, diceBonus, category);
        result.type = type;
        result.conferPercent = conferPercent;
        result.confers = confers;
        return checkResult(result);
    }

    public Action createAttack(Entity owner, String name, String action, byte cost, byte range, byte diceNum,
                               byte diceSides, byte diceBonus, byte category, byte type, Entity creates)
    {
        AttackAction result = new AttackAction(owner, name, action, cost, range, diceNum, diceSides, diceBonus, category);
        result.type = type;
        result.creates = creates;
        result.conferPercent = 100;
        return checkResult(result);
    }

    public Action createAttack(Entity owner, String name, String action, byte cost, byte range, byte diceNum,
                               byte diceSides, byte diceBonus, byte category, byte type, Entity creates, byte createPercent,
                               byte radius)
    {
        AttackAction result = new AttackAction(owner, name, action, cost, range, diceNum, diceSides, diceBonus, category);
        result.type = type;
        result.creates = creates;
        result.conferPercent = 100;
        result.radius = radius;
        return checkResult(result);
    }

    private Action checkResult(Action result)
    {
        boolean found = false;
        for(int i=0; i<this.existingActions.size(); i++)
        {
            Action action = (Action)this.existingActions.get(i);
            if(ActionHelper.equals(action, result))
            {
                result = action;
                found = true;
                break;
            }
        }
        if(!found)
        {
            this.existingActions.add(result);
        }
        return result;
    }

    private SimpleAction reallyCreate(Entity owner, int mode, int cost, byte type, boolean silent)
    {
        SimpleAction action = new SimpleAction(owner, (byte)mode, (byte)cost, type, silent);
        action.name = getName(mode, NAMES);
        action.action = getName(mode, ACTIONS);
        return action;
    }
}
