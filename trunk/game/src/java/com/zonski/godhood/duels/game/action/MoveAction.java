package com.zonski.godhood.duels.game.action;

import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.Room;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Nov 5, 2003
 * Time: 11:02:16 AM
 * To change this template use Options | File Templates.
 * <p>
 * Describes moving from one spot to another
 */
public final class MoveAction extends Action
{
    public static final String NAME = "Move";

    public static final byte MOVE_RESULT_FLAG   = 1;
    public static final byte SWAPPED_FLAG       = 2;
    public static final byte CHANGED_LOCATION_FLAG = 4;

    /**
     * X coordinate that the object moved to
     */
    public byte toX;

    /**
     * Y coodinate that the object moved to
     */
    public byte toY;

    private byte totalCost;

    /**
     * Indicates whether the move resulted in a swap with another entity
     */
    public boolean swapped;

    public byte flags;

    public MoveAction(byte totalCost, Entity entity)
    {
        this.totalCost = totalCost;
        this.entity = entity;
    }

    public int getTotalSteps()
    {
        return this.totalCost;
    }

    public String getName()
    {
        return NAME;
    }

    public void setMove(byte fromX, byte fromY, byte toX, byte toY)
    {
        this.toX = toX;
        this.toY = toY;
    }

    public final int update()
    {
        // we've had a go at it, that's all it means
        this.flags |= MOVE_RESULT_FLAG;

        byte fromX = this.entity.x;
        byte fromY = this.entity.y;
        int result = this.getTotalSteps();
        boolean move = true;
        // move the entity
        // check that the spot that the entity is moving to isn't occupied

        Room room = (Room)entity.container;
        int obstacle = room.getObstacle(this.toX, this.toY);
        if(obstacle == 0)
        {
            Vector entities = room.getEntitiesAt(this.toX, this.toY);
            for(int i=0; i<entities.size(); i++)
            {
                Entity found = (Entity)entities.elementAt(i);
                if((found.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_BOUNCE)
                {
                    move = false;
                    result = 0;
                    room.addMessage("Bonk! The "+entity.name+" runs into the "+found.name);
                    break;
                }else if((found.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_MONSTER &&
                        (this.entity.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE){
                    if(room.isFriendly(this.entity.side, found.side))
                    {
                        // swap places if it has enough moves
                        Action moveAction = found.getAction(MoveAction.NAME);
                        if(moveAction != null && moveAction.getTotalSteps() <= found.unitsRemaining)
                        {
                            found.unitsRemaining -= moveAction.getTotalSteps();
                            move = true;
                            room.addMessage("The "+entity.name+" swapped with the "+found.name);
                            entities.removeElementAt(i);
                            Vector mySpot = room.getEntitiesAt(fromX, fromY);
                            found.x = fromX;
                            found.y = fromY;
                            mySpot.addElement(found);
                            this.flags |= SWAPPED_FLAG | CHANGED_LOCATION_FLAG;
                        }else{
                            if(moveAction == null)
                            {
                                room.addMessage("The "+found.name+" is fixed in place");
                            }else{
                                room.addMessage("The "+found.name+" doesn't have enough turn units");
                            }
                            move = false;
                            result = 0;
                        }
                    }else{
                        move = false;
                        AttackAction attackAction = MonsterAction.getBestAttack(this.entity, null, true, 1, true);
                        String attackAvailability = null;
                        String attackActionName;
                        if(attackAction != null)
                        {

                            attackAction = (AttackAction)attackAction.copy(this.entity);
                            attackAction.setPosition(this.toX, this.toY);
                            attackAvailability = attackAction.getAvailability(this.entity);
                            if(attackAvailability == null)
                            {
                                this.entity.doing = attackAction;
                                result = attackAction.update();
                                /* not sure about this...
                                if(attackAction.isCompleted())
                                {
                                    this.flags |= MOVE_RESULT_FLAG;
                                }else{
                                    this.flags = (byte)(this.flags & ~MOVE_RESULT_FLAG);
                                }
                                */
                                attackActionName = null;
                            }else{
                                attackActionName = attackAction.getName();
                            }
                        }else{
                            attackAvailability = Action.CANNOT_SPAN;
                            attackActionName = "attack";
                            result = 0;
                        }
                        if(attackAvailability != null)
                        {
                            room.addMessage("The "+entity.name+" could not "+attackActionName+" the "+
                                    found.name+" because "+attackAvailability);
                            result = 0;
                        }
                    }
                    break;
                }
            }
        }else{
            move = false;
            result = 0;
            room.addMessage("That way is blocked");
        }
        if(move)
        {
            // check for flying creatures
            int tile = room.getTile(this.toX, this.toY);
            if(tile == 0 && (this.entity.entityCategory & Entity.CATEGORY_FLYING) == 0)
            {
                result = 0;
                room.addMessage("There's nothing there to walk on");
            }else if(this.toX < 0 || this.toX >= room.width || this.toY < 0 || this.toY >= room.height){
                result = 0;
                room.addMessage("A mysterious force prevents you from going there");
            }else{
                entity.x = this.toX;
                entity.y = this.toY;
                this.flags |= CHANGED_LOCATION_FLAG;
            }
        }
        return result;
    }

    public Action copy(Entity entity)
    {
        MoveAction action = new MoveAction(this.totalCost, entity);
        //action.inherent = this.inherent;
        return action;
    }

    public final boolean isCompleted()
    {
        return (this.flags & MOVE_RESULT_FLAG) > 0;
    }
}
