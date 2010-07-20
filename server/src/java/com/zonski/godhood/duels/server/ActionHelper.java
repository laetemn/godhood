package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.action.SimpleAction;
import com.zonski.godhood.duels.game.action.MoveAction;
import com.zonski.godhood.duels.game.action.AttackAction;

import java.util.Vector;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 17/03/2004
 * Time: 09:11:38
 * To change this template use Options | File Templates.
 */
public class ActionHelper
{
    public static final boolean equals(Action a1, Action a2)
    {
        boolean equal;
        if(a1 == a2)
        {
            equal = true;
        }
        else if(a1 != null && a2 != null && a1.getClass().equals(a2.getClass()))
        {
            if(a1 instanceof SimpleAction)
            {
                SimpleAction s1 = (SimpleAction)a1;
                SimpleAction s2 = (SimpleAction)a2;
                equal = (s1.getMode() == s2.getMode() &&
                        equals(s1.getName(), s2.getName()) &&
                        equals(s1.action, s2.action) &&
                        s1.item == s2.item &&
                        s1.convertSide == s2.convertSide &&
                        s1.filter == s2.filter &&
                        s1.type == s2.type &&
                        equals(s1.exitName, s2.exitName) &&
                        s1.isVisible() == s2.isVisible() &&
                        s1.flags == s2.flags);
            }else if(a1 instanceof MoveAction){
                MoveAction m1 = (MoveAction)a1;
                MoveAction m2 = (MoveAction)a2;
                equal = m1.getTotalSteps() == m2.getTotalSteps();
            }else if(a1 instanceof AttackAction){
                AttackAction x1 = (AttackAction)a1;
                AttackAction x2 = (AttackAction)a2;
                equal = (equals(x1.getName(), x2.getName()) &&
                        x1.diceNumber == x2.diceNumber &&
                        x1.diceSides == x2.diceSides &&
                        x1.diceBonus == x2.diceBonus &&
                        x1.category == x2.category &&
                        equals(x1.confers, x2.confers) &&
                        x1.conferPercent == x2.conferPercent &&
                        x1.flags == x2.flags &&
                        x1.creates == x2.creates &&
                        equals(x1.itemName, x2.itemName) &&
                        x1.prerequsiteCategory == x2.prerequsiteCategory &&
                        x1.radius == x2.radius &&
                        x1.range == x2.range &&
                        x1.type == x2.type);
            }else{
                // don't know what it is => not equal
                equal = false;
            }
        }else{
            equal = false;
        }
        return equal;
    }

    public static final boolean equals(String s1, String s2)
    {
        boolean equal;
        if(s1 == null && s2 == null)
        {
            equal = true;
        }else if(s1 != null && s2 != null){
            equal = s1.equals(s2);
        }else{
            equal = false;
        }
        return equal;
    }

    /**
     * ensures that identical actions share the same memory
     * @param room the room to pack
     */
    public static final void packActions(Room room)
    {
        Vector entities = room.getContainedEntities();
        ArrayList packed = new ArrayList(entities.size());
        /*
        for(int i=0; i<room.prototypes.length; i++)
        {
            Entity entity = room.prototypes[i];
            if(entity != null)
            {
                packEntityActions(packed, entity);
            }
        }
        */
        for(int i=0; i<entities.size(); i++)
        {
            Entity entity = (Entity)entities.get(i);
            packEntityActions(packed, entity);
        }
    }

    private static final void packEntityActions(ArrayList packed, Entity entity)
    {
        Vector actions = entity.actions;
        if(actions != null)
        {
            for(int j=0; j<actions.size(); j++)
            {
                Action action = (Action)actions.get(j);
                Action pack = packAction(packed, action);
                if(pack != null)
                {
                    // replace
                    actions.set(j, pack);
                }
            }
        }
    }

    private static final Action packAction(ArrayList packed, Action action)
    {
        Action result = null;
        for(int i=0; i<packed.size(); i++)
        {
            Action pack = (Action)packed.get(i);
            if(equals(pack, action))
            {
                result = pack;
                break;
            }
        }
        if(result == null)
        {
            // pack any children
            packed.add(action);
            if(action instanceof AttackAction)
            {
                AttackAction attack = (AttackAction)action;
                if(attack.confers != null)
                {
                    Action pack = packAction(packed, attack.confers);
                    if(pack != null)
                    {
                        attack.confers = pack;
                    }
                }
                if(attack.creates != null)
                {
                    packEntityActions(packed, attack.creates);
                }
            }else if(action instanceof SimpleAction){
                SimpleAction simple = (SimpleAction)action;
                if(simple.item instanceof Entity)
                {
                    packEntityActions(packed, (Entity)simple.item);
                }else if(simple.item instanceof Action){
                    simple.item = packAction(packed, (Action)simple.item);
                }
            }
        }else{
            System.out.println("packed "+action.getName()+" into "+result.getName());
        }
        return result;
    }
}
