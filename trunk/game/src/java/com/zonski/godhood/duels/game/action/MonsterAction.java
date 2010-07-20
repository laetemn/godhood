package com.zonski.godhood.duels.game.action;


import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Room;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Nov 10, 2003
 * Time: 5:28:30 PM
 * To change this template use Options | File Templates.
 * <p>
 * action mind that should be used for animals
 */
public final class MonsterAction extends Action
{
    private static final byte EMPTY_UNASSIGNED = -1;
    private static final byte MONSTER = -2;
    private static final byte OBSTACLE = -3;

    private static final byte BOSS_BONUS = 10;

    private Entity seeking;
    private Vector path;

    /**
     * level of excitement for this entity, excited monsters will tend to flock together
     */
    public byte excitement;

    public MonsterAction(Entity entity)
    {
        this.entity = entity;
    }

    public final int update()
    {
        Action result;
        Room room = (Room)this.entity.container;
        int rsq = room.width * room.width + room.height * room.height;
        int maxside = 0;
        if(this.path == null)
        {
            this.path = new Vector(5, 5);
        }
        if((this.seeking == null || this.seeking.isFlag(Entity.DEAD_FLAG) || path.size() == 0))
        {
            // only recreate the map if we absolutely have to
            // find something of interest to the monster
            int interest = Integer.MIN_VALUE;
            Entity best = null;

            int closestTargetDistance = Integer.MAX_VALUE;
            Entity closestTarget = null;

            Vector actions = this.entity.actions;
            boolean angry;
            if(this.entity.isFlag(Entity.PACIFIST_FLAG))
            {
                if(this.entity.thoughts instanceof AttackAction)
                {
                    AttackAction attack = (AttackAction)this.entity.thoughts;
                    Entity angryAt = attack.getParent();
                    if(!angryAt.isFlag(Entity.DEAD_FLAG) && (attack.flags & AttackAction.GOOD_BIT) == 0)
                    {
                        // try and get revenge on the attacker
                        best = angryAt;
                        interest = Integer.MAX_VALUE;
                        angry = true;
                        // we're no longer a pacifist
                        this.entity.setFlag(Entity.PACIFIST_FLAG, false);
                        //System.out.println("the "+this.entity.name+" is angry");
                    }else{
                        this.entity.thoughts = null;
                        angry = false;
                    }
                }else{
                    angry = false;
                }
            }else{
                angry = true;
            }
            if(angry)
            {
                int lx;
                int ly;
                int lw;
                int lh;
                int maxrange = 0;
                if(actions != null)
                {
                    for(int i=0; i<actions.size(); i++)
                    {
                        Action action = (Action)actions.elementAt(i);
                        if(action instanceof AttackAction)
                        {
                            AttackAction attack = (AttackAction)action;
                            maxrange = Math.max(attack.range, maxrange);
                        }
                    }
                }
                if(this.entity.getAction(MoveAction.NAME) == null)
                {
                    // use the maximum range of available attacks for this entity
                    // note that this will still allow the corners to be searched, when
                    // we cannot actually reach them
                    lx = Math.max(0, this.entity.x - maxrange);
                    ly = Math.max(0, this.entity.y - maxrange);
                    lw = Math.min(1 + 2*maxrange, room.width-lx);
                    lh = Math.min(1 + 2*maxrange, room.height-ly);
                }else{
                    lx = 0;
                    ly = 0;
                    lw = room.width;
                    lh = room.height;
                }

                for(int x=lx; x<lx+lw; x++)
                {
                    for(int y=ly; y<ly+lh; y++)
                    {
                        room.map[y][x] = EMPTY_UNASSIGNED;
                        int tile = room.getTile(x, y);
                        int obstacle = room.getObstacle(x, y);
                        if((tile == 0 && (this.entity.entityCategory & Entity.CATEGORY_FLYING) == 0) || obstacle > 0)
                        {
                            room.map[y][x] = OBSTACLE;
                        }else{
                            Vector entities = room.getEntitiesAt(x, y);
                            if(entities != null)
                            {
                                for(int i=0; i<entities.size(); i++)
                                {
                                    Entity e = (Entity)entities.elementAt(i);
                                    int eType = e.entityType;
                                    // not even looking at objects that are on the same side or are
                                    // obstacles
                                    if(eType == Entity.MONSTER_TYPE)
                                    {
                                        // you can walk though some monsters, although if two monsters are collision effect none, we'll go around
                                        //boolean ceffectnone1 = (e.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_NONE;
                                        //boolean ceffectnone2 = (this.entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_NONE;
                                        if((e.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_NONE ^
                                           (this.entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_NONE)
                                        {
                                            // don't assign it again, there could be another, more solid, monster there
                                            //room.map[y][x] = EMPTY_UNASSIGNED;
                                        }else{
                                            // don't want swapping to be done by the AI
                                            room.map[y][x] = MONSTER;
                                            /*
                                            // he's no friend of mine
                                            if(!room.isFriendly(entity.side, e.side))
                                            {
                                                room.map[y][x] = MONSTER;
                                                break;
                                                //System.out.println("The "+e.name+" has an interest of "+eInterest+" to the "+this.entity.name);
                                            }else{
                                                Action moveAction = e.getAction(MoveAction.NAME);
                                                // if the monsters are on the same side you can swap with eachother
                                                if(moveAction != null && moveAction.getTotalSteps()<=e.unitsRemaining)
                                                {
                                                    // we can swap with him
                                                    room.map[y][x] = EMPTY_UNASSIGNED;
                                                }else{
                                                    room.map[y][x] = MONSTER;
                                                }
                                            }
                                            */
                                        }

                                    }else if((e.flags & Entity.COLLISION_EFFECT_MASK) != Entity.COLLISION_EFFECT_NONE){
                                        room.map[y][x] = OBSTACLE;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                // populate the map, we now know what we can and cannot reach
                room.map[this.entity.y][this.entity.x] = EMPTY_UNASSIGNED;
                pop(0, this.entity.x, this.entity.y, room.map);

                Vector entities = room.getContainedEntities();
                if(entities != null)
                {
                    for(int i=0; i<entities.size(); i++)
                    {
                        int eInterest;
                        Entity e = (Entity)entities.elementAt(i);
                        int x = e.x;
                        int y = e.y;
                        int dx = x - this.entity.x;
                        int dy = y - this.entity.y;
                        int hsq = (dx*dx)+(dy*dy);
                        if(hsq < closestTargetDistance &&
                                AttackAction.getBlocker(this.entity.x, this.entity.y, e.x, e.y, room) == null &&
                                !room.isFriendly(this.entity.side, e.side))
                        {
                            closestTargetDistance = hsq;
                            closestTarget = e;
                        }
                        if(canReach(x, y, room.map))
                        {
                            if(hsq == 0)
                            {
                                // what are you going to do, it's right on top of it!
                                hsq = 1;
                            }
                            int eType = e.entityType;
                            // not even looking at objects that are on the same side or are
                            // obstacles
                            if(eType == Entity.MONSTER_TYPE)
                            {
                                int eStrength = getStrengthVs(e, this.entity);
                                if(e.side > maxside)
                                {
                                    maxside = e.side;
                                }
                                // he's no friend of mine
                                if(!room.isFriendly(entity.side, e.side))
                                {
                                    int entityStrength = getStrengthVs(this.entity, e);
                                    // weaker monsters are the most attractive, stronger
                                    // monsters are scary
                                    eInterest = entityStrength - eStrength;
                                    // make random adjustment, just to spice things up
                                    if(eInterest/4 != 0)
                                    {
                                        eInterest += Entity.RANDOM.nextInt()%Math.abs(eInterest/4);
                                    }

                                    // bosses are obvious targets for agression for minions
                                    if(e.isFlag(Entity.BOSS_FLAG) && !this.entity.isFlag(Entity.BOSS_FLAG))
                                    {
                                        eInterest += BOSS_BONUS;
                                    }
                                    // closer monsters are more attractive
                                    if(eInterest == 0)
                                    {
                                        eInterest = 1;
                                    }
                                    eInterest = (eInterest * eInterest * rsq) / hsq;
                                    //System.out.println("The "+e.name+" has an interest of "+eInterest+" to the "+this.entity.name);
                                }else{
                                    if(e != this.entity)
                                    {
                                        if(e.thinking instanceof MonsterAction)
                                        {
                                            eInterest = ((MonsterAction)e.thinking).excitement/2;
                                        }else{
                                            eInterest = Integer.MIN_VALUE;
                                        }
                                    }else{
                                        eInterest = Integer.MIN_VALUE;
                                    }
                                }
                            }else if(eType == Entity.ITEM_TYPE){
                                // items compatible with the entitys type are the most attractive
                                // can we pick it up
                                if(this.entity.getSimpleAction(SimpleAction.PICKUP) != null)
                                {
                                    eInterest = Entity.getAdjustedValue(10, this.entity.entityCategory, e.entityCategory, false);
                                    eInterest = (eInterest * eInterest * rsq) / hsq;
                                    if(e.isFlag(Entity.BOSS_FLAG))
                                    {
                                        eInterest = Integer.MAX_VALUE;
                                    }
                                }else{
                                    eInterest = Integer.MIN_VALUE;
                                }
                            }else if(eType == Entity.INHERIT_TYPE){
                                // it's benign, might be interesting, we should go there if there isn't anything else to do
                                // and we haven't been there last, make sure it's the only thing on the square
                                if(this.entity.thoughts == e || room.isFriendly(1, this.entity.side))
                                {
                                    eInterest = Integer.MIN_VALUE;
                                }else{
                                    eInterest = -hsq;
                                }
                            }else{
                                eInterest = Integer.MIN_VALUE;
                            }
                            if(eInterest > interest)
                            {
                                best = e;
                                interest = eInterest;
                            }
                        }
                    }
                }
                this.excitement = (byte)Math.min(Math.max(Byte.MIN_VALUE, interest), Byte.MAX_VALUE);
            }

            if(this.entity.health < this.entity.maxHealth/2)
            {
                // we're feeling sick, perhaps there's something in our bag of tricks that can fix us up?
                int index = 0;
                int itemCount;
                Vector items = this.entity.getContainedEntities();
                if(items != null)
                {
                    itemCount = items.size();
                }else{
                    itemCount = 0;
                }
                result = null;
                while(true)
                {
                    if(actions != null)
                    {
                        for(int i=0; i<actions.size(); i++)
                        {
                            Action action = (Action)actions.elementAt(i);
                            if(action.getTotalSteps() <= this.entity.unitsRemaining)
                            {
                                if(action instanceof SimpleAction)
                                {
                                    SimpleAction simple = (SimpleAction)action;
                                    if(isGood(simple))
                                    {
                                        result = simple.copy(this.entity);
                                        break;
                                    }
                                }else if(action instanceof AttackAction){
                                    AttackAction attack = (AttackAction)action;
                                    if(attack.diceSides < 0 ||
                                            (attack.confers instanceof SimpleAction &&
                                            isGood((SimpleAction)attack.confers)))
                                    {
                                        attack = (AttackAction)attack.copy(this.entity);
                                        // it's good, lets use it
                                        attack.setPosition(this.entity.x, this.entity.y);
                                        result = attack;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if(index < itemCount)
                    {
                        actions = ((Entity)items.elementAt(index)).actions;
                        index ++;
                    }else{
                        break;
                    }
                }
            }else{
                result = null;
            }
            if(result == null && closestTarget != null)
            {
                // shoot the closest targetable entity if there is one
                /*
                result = MonsterAction.getBestAttack(this.entity, closestTarget,
                        this.entity.health < this.entity.maxHealth/2, closestTargetDistance);
                        */
                result = getAction(closestTarget, room.map);
            }
            if(result == null)
            {
                if(best != null)
                {
                    result = seek(best, room.map);
                }
            }
        }else{
            result = seek(this.seeking, room.map);
        }
        if(result == null || result.getAvailability(this.entity) != null)
        {
            result = this.entity.getSimpleAction(SimpleAction.ENDTURN).copy(this.entity);
        }
        if(result != null)
        {
            if(result.endsTurn())
            {
                // the turn has ended, we want to clear out the path and re-evaluate our options next turn
                if(this.path != null)
                {
                    this.path.removeAllElements();
                }
                this.seeking = null;
            }
        }
        entity.doing = result;
        return 0;
    }

    private final boolean isGood(SimpleAction simple)
    {
        return (simple.getMode() == SimpleAction.POLYMORPH || simple.getMode() == SimpleAction.STONE) &&
                                            (simple.item == null ||
                                            ((Entity)simple.item).entityTypeId != this.entity.entityTypeId &&
                                             ((Entity)simple.item).entityType == Entity.MONSTER_TYPE) ||
                                       simple.getMode() == SimpleAction.HEAL;
    }

    private static final int getStrengthVs(Entity entity1, Entity entity2)
    {
        byte entity2category;
        if(entity2 == null)
        {
            entity2category = 0;
        }else{
            entity2category = entity2.entityCategory;
        }

        int entity1Strength =
                entity1.getAttack() +
                entity1.getArmourClass(entity2category) +
                entity1.health +
                entity1.level;
        return entity1Strength;
    }

    private final Action getAction(Entity toSeek, byte[][] map)
    {
        Action result;
        boolean monster = toSeek.entityType == Entity.MONSTER_TYPE;
        // sure there's something on it, we'll just have to take that into account later
        // check we can't attack or pick up the entity
        if(monster)
        {
            if(toSeek.side != this.entity.side)
            {
                int r_dx = toSeek.x - this.entity.x;
                int r_dy = toSeek.y - this.entity.y;
                int div;
                if(this.entity.maxHealth <= 0)
                {
                    div = 1;
                }else{
                    div = this.entity.maxHealth;
                }
                AttackAction bestAttackAction = getBestAttack(this.entity, toSeek,
                        toSeek.level < (this.entity.level*this.entity.health)/div,
                        r_dx*r_dx + r_dy*r_dy, true);
                if(bestAttackAction != null)
                {
                    bestAttackAction = (AttackAction)bestAttackAction.copy(this.entity);
                    Room room = (Room)this.entity.container;
                    Entity target = toSeek;
                    Entity blocker = AttackAction.getBlocker(toSeek.x, toSeek.y, this.entity.x, this.entity.y, room);
                    // if there are any other enemies in the way, target them first
                    while(blocker != null)
                    {
                        target = blocker;
                        blocker = AttackAction.getBlocker(target.x, target.y, this.entity.x, this.entity.y, room);
                    }
                    if(target.side != 0 && target.side != this.entity.side)
                    {
                        boolean targetOnMonster;

                        if(bestAttackAction.creates != null)
                        {
                            Entity prototype = bestAttackAction.creates;
                            targetOnMonster = ((prototype.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_NONE);
                        }else{
                            targetOnMonster = true;
                        }
                        if(targetOnMonster)
                        {
                            bestAttackAction.setPosition(target.x, target.y);
                        }else{

                            // place it between the attacker and the attackee
                            int xat;
                            int yat;
                            int xfrom;
                            int yfrom;
                            if(AttackAction.inRange(this.entity.x, this.entity.y, target.x, target.y, bestAttackAction.range))
                            {
                                xat = target.x;
                                yat = target.y;
                                xfrom = this.entity.x;
                                yfrom = this.entity.y;
                            }else{
                                xat = this.entity.x;
                                yat = this.entity.y;
                                xfrom = target.x;
                                yfrom = target.y;
                            }
                            // anywhere free will do
                            byte bestdx = 0;
                            byte bestdy = 0;
                            int mindsq = Integer.MAX_VALUE;
                            for(byte dx = -1; dx <= 1; dx++)
                            {
                                for(byte dy = -1; dy <= 1; dy++)
                                {
                                    byte x = (byte)(xat+dx);
                                    byte y = (byte)(yat+dy);
                                    if(y>=0 && y<map.length && x>=0 && x<map[y].length)
                                    {
                                        if(map[y][x] != OBSTACLE && map[y][x] != MONSTER &&
                                                (x != this.entity.x || y != this.entity.y))
                                        {
                                            //bestAttackAction.setPosition(x, y);
                                            int dsq = (x-xfrom)*(x-xfrom)+(y-yfrom)*(y-yfrom);
                                            if(dsq<mindsq)
                                            {
                                                mindsq = dsq;
                                                bestdx = dx;
                                                bestdy = dy;
                                            }
                                        }
                                    }
                                }
                            }
                            bestAttackAction.setPosition((byte)(xat+bestdx), (byte)(yat+bestdy));
                        }
                        String reason = bestAttackAction.getAvailability(this.entity);
                        if(reason == null)
                        {
                            result = bestAttackAction;
                        }else{
                            //System.out.println("cannot "+bestAttackAction+" because "+reason);
                            //System.out.println("the "+this.entity.name+" attack is not available because "+reason);
                            // it's in range and there's nothing in the way,
                            // we need to wait until next turn so we can get this shot off obviously
                            //result = this.entity.getSimpleAction(SimpleAction.ENDTURN).copy(this.entity);
                            result = null;
                        }
                    }else{
                        // he's one of ours
                        result = null;
                    }
                }else{
                    // no attacks available
                    result = null;
                }

            }else{
                if(Math.abs(toSeek.x - this.entity.x) <= 1 && Math.abs(toSeek.y - this.entity.y) <= 1)
                {
                    // TODO : heal them if needed
                    // if we're already there we'll just wait
                    result = this.entity.getSimpleAction(SimpleAction.ENDTURN).copy(this.entity);
                }else{
                    // find them
                    result = null;
                }
            }
        }else{
            if(toSeek.x == this.entity.x && toSeek.y == this.entity.y)
            {
                // if it's an item
                int seekType = toSeek.entityType;
                if(seekType == Entity.ITEM_TYPE)
                {
                    result = this.entity.getSimpleAction(SimpleAction.PICKUP).copy(this.entity);
                }else{
                    // see if we can inherit one or more actions from it, might be something good
                    Vector seekActions = toSeek.actions;
                    if(seekActions != null && seekActions.size() > 0)
                    {
                        result = ((Action)seekActions.elementAt(0)).copy(this.entity);
                        // whatever happened we wont do that again for a while
                        this.entity.thoughts = toSeek;
                    }else{
                        result = null;
                    }
                }
            }else{
                result = null;
            }
        }
        return result;
    }

    private final Action seek(Entity toSeek, byte[][] map)
    {
        //System.out.println("The "+this.entity.name+" is seeking the "+toSeek.name);
        this.seeking = toSeek;
        Action result = getAction(toSeek, map);

        // check we can't move towards the entity
        if(result == null)
        {
            Action move = this.entity.getAction(MoveAction.NAME);
            if(move != null && move.getAvailability(this.entity) == null)
            {
                // find the shortest path and move along it
                if(this.path.size() == 0)
                {
                    // write out the map
                    /*
                    for(int y=0; y<map.length; y++)
                    {
                        for(int x=0; x<map[y].length; x++)
                        {
                            String s = Integer.toString(map[y][x], 16);
                            while(s.length() < 2)
                            {
                                s = " "+s;
                            }
                            System.out.print(s);
                        }
                        System.out.println();
                    }
                    */

                    // work backwards from the target
                    int currentValue = Integer.MAX_VALUE;
                    // the current value is the minimum of the surrounding squares plus one
                    // we do this rather than set the target square to UNASSIGNED,
                    // because we don't want to corrupt our matrix for future searches
                    byte x = toSeek.x;
                    byte y = toSeek.y;
                    for(int dx = -1; dx <= 1; dx++)
                    {
                        for(int dy = -1; dy <= 1; dy++)
                        {
                            if(y+dy>=0 && y+dy < map.length && x+dx >= 0 && x+dx < map[y+dy].length)
                            {
                                int value = map[y+dy][x+dx];
                                if(value >= 0 && currentValue > value+1)
                                {
                                    currentValue = value+1;
                                }
                            }
                        }
                    }
                    if(currentValue > 0 && currentValue != Integer.MAX_VALUE)
                    {

                        //System.out.println(this.entity.name + " is at ("+this.entity.x+","+this.entity.y+")");
                        //System.out.println("seeking : "+toSeek.name);
                        while(x != this.entity.x || y != this.entity.y)
                        {
                            //System.out.println("("+x+","+y+")");
                            this.path.insertElementAt(new byte[]{x, y}, 0);

                            // find the value for x and y that is one less than the current value
                            outer:for(int dx = -1; dx <= 1; dx++)
                            {
                                for(int dy = -1; dy <= 1; dy++)
                                {
                                    if(y+dy>=0 && y+dy < map.length && x+dx >= 0 && x+dx < map[y+dy].length)
                                    {
                                        int foundValue = map[y+dy][x+dx];
                                        if(foundValue >= 0)
                                        {
                                            if(foundValue < currentValue)
                                            {
                                                currentValue = foundValue;
                                                y += dy;
                                                x += dx;
                                                break outer;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //System.out.println(this.entity.name);
                    }
                    /*
                    //else
                    {
                        // we really can't find a path, I suspect a bug

                        System.out.println("map:");
                        for(int my = 0; my<map.length; my++)
                        {
                            for(int mx=0; mx<map[my].length; mx++)
                            {
                                String s;
                                int val = map[my][mx];
                                if(val < 0)
                                {
                                    s = "  ";
                                }else{
                                    s = Integer.toString(val);
                                }
                                while(s.length() < 2)
                                {
                                    s = "0"+s;
                                }
                                boolean inPath = false;
                                for(int i=0; i<this.path.size(); i++)
                                {
                                    int[] step = (int[])this.path.elementAt(i);
                                    if(step[0] == mx && step[1] == my)
                                    {
                                        inPath = true;
                                        break;
                                    }
                                }
                                String postChar;
                                if(inPath)
                                {
                                    postChar = "<";
                                }else{
                                    postChar = " ";
                                }
                                System.out.print(s+postChar);
                            }
                            System.out.println();
                        }
                    }
                    */
                }

                if(this.path != null && path.size() > 0)
                {
                    // pop off the first element
                    byte[] step = (byte[])this.path.elementAt(0);
                    this.path.removeElementAt(0);
                    // check that the spot on the map isn't actually blocked, it can happen
                    if(map[step[1]][step[0]] >= 0 ||
                            (this.entity.flags & Entity.COLLISION_EFFECT_MASK) == Entity.COLLISION_EFFECT_NONE)
                    {
                        MoveAction moveAction = (MoveAction)move.copy(this.entity);
                        moveAction.setMove(this.entity.x, this.entity.y, step[0], step[1]);
                        result = moveAction;
                    }
                }
            }
        }
        /*
        if(result != null && result.getAvailability(this.entity) != null)
        {
            // may not have enough time, end the turn and wait until next round
            Action wait = this.entity.getSimpleAction(SimpleAction.DEFEND);
            if(wait == null || wait.getAvailability(this.entity) != null)
            {
                wait = this.entity.getSimpleAction(SimpleAction.ENDTURN);
            }
            result = wait.copy(this.entity);
        }
        */
        /*
        if(result != null)
        {
            System.out.println("The "+this.entity.name+" decides to "+result.getName()+" as part of seeking the "+toSeek.name);
        }else{
            System.out.println("The "+this.entity.name+" cannot find a path to the "+toSeek.name);
        }
        */
        // if we return null there's no way of interacting with this object
        return result;
    }

    private final void pop(int costSoFar, int x, int y, byte[][] map)
    {
        if(y >= 0 && x >= 0 && y < map.length && x < map[y].length)
        {
            byte value = map[y][x];
            if(value == EMPTY_UNASSIGNED || value > costSoFar)
            {
                map[y][x] = (byte)costSoFar;
                int nextCost = costSoFar+1;
                // do the diagonals first, they're far more likely to get you where you want since
                // one diagonal can be equivalent to two non-diagonals, of course the monsters
                // will walk in a zigzag pattern
                pop(nextCost, x+1, y+1, map);
                pop(nextCost, x+1, y-1, map);
                pop(nextCost, x-1, y+1, map);
                pop(nextCost, x-1, y-1, map);
                pop(nextCost, x+1, y, map);
                pop(nextCost, x, y+1, map);
                pop(nextCost, x-1, y, map);
                pop(nextCost, x, y-1, map);
            }
        }
    }

    public final int getTotalSteps()
    {
        return -1;
    }

    public final int getStepsCompleted()
    {
        return 0;
    }

    public final int getCost()
    {
        return 0;
    }

    public final String getName()
    {
        return null;
    }

    public final boolean isCompleted()
    {
        return false;
    }

    public final Action copy(Entity entity)
    {
        return new MonsterAction(entity);
    }

    public final String getAvailability(Entity entity)
    {
        return null;
    }

    private static final boolean canReach(int x, int y, byte[][] map)
    {
        boolean reachable;
        if(map[y][x] == OBSTACLE || map[y][x] == EMPTY_UNASSIGNED)
        {
            reachable = false;
        }else if(map[y][x] == MONSTER){
            // monsters are reachable if adjacent tiles are reachable
            reachable = false;
            outer: for(int dx=-1; dx<=1; dx++)
            {
                for(int dy=-1; dy<=1; dy++)
                {
                    int tx = x+dx;
                    int ty = y+dy;
                    if(ty>=0 && tx>=0 && ty<map.length && tx<map[ty].length)
                    {
                        if(map[ty][tx] >=0)
                        {
                            reachable = true;
                            break outer;
                        }
                    }
                }
            }
        }else{
            reachable = true;
        }
        return reachable;
    }

    public static final AttackAction getBestAttack(Entity entity, Entity toSeek, boolean conservation, int dsq, boolean intime)
    {
        // see if we can assault it somehow
        Vector actions = entity.actions;
        Vector items = entity.getContainedEntities();
        int index=0;
        AttackAction bestAttackAction = null;
        int bestPower = 0;
        do
        {
            if(actions != null)
            {
                for(int i=0; i<actions.size(); i++)
                {
                    Action action = (Action)actions.elementAt(i);
                    if(action instanceof AttackAction)
                    {
                        AttackAction attack = (AttackAction)action;
                        if((attack.flags & AttackAction.GOOD_BIT)==0 &&
                                (!conservation || (attack.flags & AttackAction.CONSUME_BIT) == 0) &&
                                Entity.meets(entity.entityCategory, attack.prerequsiteCategory) &&
                                AttackAction.inRange(dsq, attack.range) &&
                                (attack.getTotalSteps() <= entity.unitsRemaining || !intime))
                        {
                            // it's bad for other monsters, excellent
                            int power = (attack.diceSides*attack.diceNumber)/2+attack.diceBonus;
                            if(attack.creates != null)
                            {
                                Entity creates = attack.creates;
                                power += getStrengthVs(creates, toSeek);
                            }
                            if(attack.confers != null)
                            {
                                // rate the various confer options
                                if(attack.confers instanceof SimpleAction)
                                {
                                    SimpleAction confers = (SimpleAction)attack.confers;
                                    if(toSeek == null || Entity.meets(toSeek.entityCategory, confers.filter))
                                    {
                                        int conferPower;
                                        switch(confers.getMode())
                                        {
                                            case SimpleAction.DIE:
                                            case SimpleAction.STONE:
                                            case SimpleAction.SWALLOW:
                                            case SimpleAction.CONVERT:
                                                conferPower = 200;
                                                break;
                                            case SimpleAction.POLYMORPH:
                                            case SimpleAction.TELEPORT:
                                            case SimpleAction.HURT:
                                                conferPower = 50;
                                                break;
                                            case SimpleAction.DAZED:
                                                conferPower = 10;
                                                break;
                                            default:
                                                conferPower = 0;
                                        }
                                        power += (attack.conferPercent * conferPower)/100;
                                    }
                                }
                            }
                            if(bestAttackAction == null || bestPower <= power)
                            {
                                //attack.setPosition(toSeek.x, toSeek.y);
                                //String availability = attack.getAvailability(this.entity, this.entity.unitsRemaining);
                                //if(availability == null)
                                int range;
                                if(attack.creates != null)
                                {
                                    Entity prototype = attack.creates;
                                    if(prototype.entityType == Entity.MONSTER_TYPE)
                                    {
                                        range = attack.range * 2;
                                    }else{
                                        range = attack.range;
                                    }
                                }else{
                                    range = attack.range;
                                }
                                if(toSeek == null || AttackAction.inRange(toSeek.x, toSeek.y, entity.x, entity.y, range))
                                {
                                    bestPower = power;
                                    bestAttackAction = attack;
                                }
                            }
                        }
                    }
                }
            }
            if(items != null && items.size() > index)
            {
                Entity item = (Entity)items.elementAt(index);
                if(item.entityType == Entity.ITEM_TYPE)
                {
                    actions = item.actions;
                }else{
                    actions = null;
                }
            }
            index++;
        }while(items != null && index <= items.size());
        return bestAttackAction;
    }
}
