package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.action.SimpleAction;
import com.zonski.godhood.duels.game.action.MoveAction;
import com.zonski.godhood.duels.game.action.AttackAction;

import java.io.*;
import java.util.*;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 23/03/2004
 * Time: 13:34:27
 * To change this template use Options | File Templates.
 */
public class XmlFileRoomStore extends FileRoomStore
{
    public static final String DEFAULT_XML_EXTENSION = "room.xml";

    public static final String WIDTH_KEY                = "width";
    public static final String HEIGHT_KEY               = "height";
    public static final String NAME_KEY                 = "name";
    public static final String WIN_STYLE_KEY            = "win-style";
    public static final String SIDE_KEY                 = "side";
    public static final String ENTITY_TYPE_ID_KEY       = "entity-type-id";
    public static final String ENTITY_TYPE_KEY          = "entity-type";
    public static final String COLLISION_EFFECT_KEY     = "collision-effect";
    public static final String HEALTH_KEY               = "health";
    public static final String MAX_HEALTH_KEY           = "health";
    public static final String LEVEL_KEY                = "level";
    public static final String SPEED_KEY                = "speed";
    public static final String FREQUENCY_KEY            = "frequency";
    public static final String ARMOUR_CLASS_KEY         = "armour-class";
    public static final String ATTACK_BONUS_KEY         = "attack-bonus";
    public static final String TYPE_KEY                 = "type";
    public static final String COST_KEY                 = "cost";
    public static final String ACTION_KEY               = "action";
    public static final String CONVERT_TO_SIDE_KEY      = "convert-to-side";
    public static final String POLYMORPH_TO_KEY         = "polymorph-to";
    public static final String CREATE_KEY               = "create";
    public static final String AMOUNT_KEY               = "amount";
    public static final String EXIT_NAME_KEY            = "exit-name";
    public static final String RANGE_KEY                = "range";
    public static final String DICE_NUM_KEY             = "number-of-dice";
    public static final String DICE_SIDES_KEY           = "number-of-sides";
    public static final String END_TURN_KEY             = "end-turn";
    public static final String EFFECT_GROUP_KEY         = "effect-group";
    public static final String KILL_PARENT_KEY          = "kill-parent";
    public static final String CONSUME_KEY              = "consume";
    public static final String PERCENT_SUCCESS_KEY      = "percent-success";
    public static final String ALWAYS_HITS_KEY          = "always-hits";
    public static final String SPAN_TURN_KEY            = "span-turn";
    public static final String DEFAULT_ATTACK_KEY       = "default-attack";
    public static final String X_KEY                    = "x";
    public static final String Y_KEY                    = "y";
    public static final String BOSS_KEY                 = "boss";
    public static final String RADIUS_KEY               = "radius";
    public static final String GOOD_KEY                 = "good";
    public static final String PACIFIST_KEY             = "pacifist";
    public static final String ARMOUR_BONUS_KEY         = "armour-bonus";
    public static final String SAVE_KEY                 = "save";

    public static final String ROOM_ELEMENT             = "Room";
    public static final String WELCOME_MESSAGE_ELEMENT  = "Welcome";
    public static final String WIN_MESSAGE_ELEMENT      = "Win";
    public static final String LOSE_MESSAGE_ELEMENT     = "Lose";
    public static final String FRIENDS_ELEMENT          = "Friends";
    public static final String FRIEND_ELEMENT           = "Friend";
    public static final String TILES_ELEMENT            = "Tiles";
    public static final String OBSTACLES_ELEMENT        = "Obstacles";
    public static final String PROTOTYPE_ELEMENT        = "Prototype";
    public static final String ENTITY_ELEMENT           = "Entity";
    public static final String CATEGORIES_ELEMENT       = "Categories";
    public static final String PREREQUISITE_CATEGORIES_ELEMENT = "PrerequisiteCategories";
    public static final String CATEGORY_ELEMENT         = "Category";
    public static final String ITEMS_ELEMENT            = "Items";
    public static final String ITEM_ELEMENT             = "Item";
    public static final String ACTIONS_ELEMENT          = "Actions";
    public static final String ENTITIES_ELEMENT         = "Entities";
    public static final String CONFERS_ELEMENT          = "Confers";
    public static final String FILTER_ELEMENT           = "Filter";
    public static final String TO_LEARN_ELEMENT         = "ToLearn";

    public static final String WIN_STYLE_CONTINUOUS     = "continuous";
    public static final String WIN_STYLE_LAST_MAN       = "last-man-standing";
    public static final String WIN_STYLE_LAST_BOSS      = "last-boss-standing";
    public static final String WIN_STYLE_ITEM           = "item";

    public static final String ENTITY_TYPE_MONSTER      = "monster";
    public static final String ENTITY_TYPE_PHENOMINA    = "phenomina";
    public static final String ENTITY_TYPE_INHERIT      = "inherit";
    public static final String ENTITY_TYPE_ITEM         = "item";
    public static final String ENTITY_TYPE_IMPASSABLE   = "impassable";

    public static final String COLLISION_EFFECT_NONE    = "none";
    public static final String COLLISION_EFFECT_BOUNCE  = "bounce";
    public static final String COLLISION_EFFECT_MONSTER = "monster";

    public static final String ATTACK_TYPE_HAND_TO_HAND = "hand-to-hand";
    public static final String ATTACK_TYPE_ARROW        = "arrow";
    public static final String ATTACK_TYPE_MAGIC        = "magic";
    public static final String ATTACK_TYPE_THROWN_OBJECT= "thrown";
    public static final String ATTACK_TYPE_IMPLICIT     = "implicit";

    public static final String CATEGORY_NOT             = "not";
    public static final String CATEGORY_ELEMENTAL       = "elemental";
    public static final String CATEGORY_ANIMAL          = "animal";
    public static final String CATEGORY_HUMANOID        = "humanoid";
    public static final String CATEGORY_INTELLIGENT     = "intelligent";
    public static final String CATEGORY_UNDEAD          = "undead";
    public static final String CATEGORY_FLYING          = "flying";
    public static final String CATEGORY_MAGIC           = "magic";

    private static final String[] CATEGORIES = {
        CATEGORY_NOT,
        CATEGORY_ELEMENTAL,
        CATEGORY_ANIMAL,
        CATEGORY_HUMANOID,
        CATEGORY_INTELLIGENT,
        CATEGORY_UNDEAD,
        CATEGORY_FLYING,
        CATEGORY_MAGIC
    };

    public static final String ACTION_ATTACK        = "Attack";
    public static final String ACTION_MOVE          = "Move";
    public static final String ACTION_WAIT          = "Wait";
    public static final String ACTION_PICKUP        = "PickUp";
    public static final String ACTION_PUTDOWN       = "PutDown";
    public static final String ACTION_ENDTURN       = "EndTurn";
    public static final String ACTION_DIE           = "Die";
    public static final String ACTION_SELF_DESTRUCT = "SelfDestruct";
    public static final String ACTION_DEFEND        = "Defend";
    public static final String ACTION_DAZED         = "Dazed";
    public static final String ACTION_EXIT          = "Exit";
    public static final String ACTION_CONVERT       = "Convert";
    public static final String ACTION_TELEPORT      = "Teleport";
    public static final String ACTION_POLYMORPH     = "Polymorph";
    public static final String ACTION_STONE         = "Stone";
    public static final String ACTION_ROOM_WIN      = "RoomWin";
    public static final String ACTION_HEAL          = "Heal";
    public static final String ACTION_HURT          = "Hurt";
    public static final String ACTION_BLEEDING      = "Bleeding";
    public static final String ACTION_TALK          = "Talk";
    public static final String ACTION_PROVOKE       = "Provoke";
    public static final String ACTION_FRIENDLY      = "Friendly";
    public static final String ACTION_UNFRIENDLY    = "Unfriendly";
    public static final String ACTION_SWALLOW       = "Swallow";
    public static final String ACTION_UNLOCK        = "Unlock";
    public static final String ACTION_LEARN         = "Learn";
    public static final String ACTION_RECRUIT       = "Recruit";
    public static final String ACTION_PRAY          = "Pray";
    public static final String ACTION_TURN_UNDEAD   = "TurnUndead";
    public static final String ACTION_PICKPOCKET    = "PickPocket";
    public static final String ACTION_LOSE          = "Lose";
    public static final String ACTION_TRANSMUTATION = "Transmutation";

    private static final String[] SIMPLE_ACTION_NAMES = {
        ACTION_WAIT,
        ACTION_PICKUP,
        ACTION_PUTDOWN,
        ACTION_ENDTURN,
        ACTION_DIE,
        ACTION_SELF_DESTRUCT,
        ACTION_DEFEND,
        ACTION_DAZED,
        ACTION_EXIT,
        ACTION_CONVERT,
        ACTION_TELEPORT,
        ACTION_POLYMORPH,
        ACTION_STONE,
        ACTION_ROOM_WIN,
        ACTION_HEAL,
        ACTION_HURT,
        ACTION_BLEEDING,
        ACTION_TALK,
        ACTION_PROVOKE,
        ACTION_FRIENDLY,
        ACTION_UNFRIENDLY,
        ACTION_SWALLOW,
        ACTION_UNLOCK,
        ACTION_LEARN,
        ACTION_RECRUIT,
        ACTION_PRAY,
        ACTION_TURN_UNDEAD,
        ACTION_PICKPOCKET,
        ACTION_LOSE,
        ACTION_TRANSMUTATION
    };

    private ArrayList prototypes = new ArrayList();

    public XmlFileRoomStore(File directory, boolean allowStore)
    {
        super(directory, DEFAULT_XML_EXTENSION, allowStore);
    }

    private Entity getPrototype(byte entityTypeId)
    {
        //return (Entity)this.prototypes.get(new Byte(entityTypeId));
        Entity prototype = null;
        for(int i=0; i<this.prototypes.size(); i++)
        {
            Entity found = (Entity)prototypes.get(i);
            if(found.entityTypeId == entityTypeId)
            {
                prototype = found;
                break;
            }
        }
        return prototype;
    }

    private void setPrototype(Entity prototype)
    {
        //this.prototypes.put(new Byte(prototype.entityTypeId), prototype);
        if(!this.prototypes.contains(prototype))
        {
            this.prototypes.add(prototype);
        }
    }


    private Entity getPlaceholder(byte typeId)
    {
        Entity prototype = getPrototype(typeId);
        if(prototype == null && typeId != Entity.NONE_TYPE)
        {
            prototype = new Entity();
            prototype.entityTypeId = typeId;
            setPrototype(prototype);
        }
        return prototype;
    }

    private Entity createEntity(byte entityTypeId, byte side)
    {
        Entity prototype = this.getPrototype(entityTypeId);
        if(prototype == null)
        {
            throw new RuntimeException("no prototype specified for "+entityTypeId);
        }
        Entity result = prototype.copy();
        result.side = side;
        return result;
    }

    protected Room createRoom(DataInputStream ins, String name)
        throws RoomCreationException
    {

        try
        {
            SAXBuilder builder = new SAXBuilder(false);
            Document document = builder.build(ins);
            Element roomElement = document.getRootElement();
            int width = Integer.parseInt(roomElement.getAttributeValue(WIDTH_KEY));
            int height = Integer.parseInt(roomElement.getAttributeValue(HEIGHT_KEY));
            boolean save = !"false".equals(roomElement.getAttributeValue(SAVE_KEY));
            Room room = new Room(null, (byte)width, (byte)height);
            if(!save)
            {
                room.flags = 1;
            }
            String roomName = roomElement.getAttributeValue(NAME_KEY);
            room.name = roomName;
            Element welcomeMessageElement = roomElement.getChild(WELCOME_MESSAGE_ELEMENT);
            if(welcomeMessageElement != null)
            {
                String welcomeMessage = welcomeMessageElement.getText();
                room.welcomeMessage = welcomeMessage;
            }
            Element winMessageElement = roomElement.getChild(WIN_MESSAGE_ELEMENT);
            if(winMessageElement != null)
            {
                String winMessage = winMessageElement.getText();
                room.winMessage = winMessage;
                String winStyle = winMessageElement.getAttributeValue(WIN_STYLE_KEY);
                byte winMode;
                if(winStyle == null || winStyle.equals(WIN_STYLE_CONTINUOUS))
                {
                    winMode = Room.MODE_CONTINUOUS;
                }else if(winStyle.equals(WIN_STYLE_LAST_MAN)){
                    winMode = Room.MODE_LAST_SIDE;
                }else if(winStyle.equals(WIN_STYLE_LAST_BOSS)){
                    winMode = Room.MODE_LAST_BOSS;
                }else if(winStyle.equals(WIN_STYLE_ITEM)){
                    winMode = Room.MODE_WIN_ITEM;
                }else{
                    winMode = Room.WIN_EXTERNAL;
                }
                room.mode = winMode;
            }
            Element loseMessageElement = roomElement.getChild(LOSE_MESSAGE_ELEMENT);
            if(loseMessageElement != null)
            {
                String loseMessage = loseMessageElement.getText();
                room.loseMessage = loseMessage;
            }
            // make friends
            int friends = 0;
            Element friendsElement = roomElement.getChild(FRIENDS_ELEMENT);
            if(friendsElement != null)
            {
                List friendsList = friendsElement.getChildren(FRIEND_ELEMENT);
                for(int i=0; i<friendsList.size(); i++)
                {
                    Element friendElement = (Element)friendsList.get(i);
                    String sideString = friendElement.getAttributeValue(SIDE_KEY);
                    if(sideString != null)
                    {
                        int side = Integer.parseInt(sideString);
                        friends |= 0x01 << side;
                    }
                }
            }
            room.friendlySideFilter = friends;
            // fill in the tiles
            Element tilesElement = roomElement.getChild(TILES_ELEMENT);
            if(tilesElement != null)
            {
                String tilesString = tilesElement.getText();
                int index = 0;
                for(int y=0; y<height; y++)
                {
                    for(int x=0; x<width; x++)
                    {
                        char c = tilesString.charAt(index);
                        int tileType;
                        if(c == ' ')
                        {
                            // spaces make room files more readable
                            tileType = 0;
                        }else if(c == '\n'){
                            // populate the rest and break
                            for(int ix=x; ix<width; ix++)
                            {
                                room.tiles[y][ix] = 0;
                            }
                            break;
                        }else{
                            tileType = (int)c - (int)'A';
                        }
                        room.tiles[y][x] = (byte)tileType;
                        index++;
                    }
                    if(index >= tilesString.length())
                    {
                        if(y < height-1)
                        {
                            throw new Exception("expected tile string of length "+(room.width*room.height));
                        }
                    }else{
                        if(tilesString.charAt(index) != '\n')
                        {
                            throw new Exception("expected \\n at index "+index+" in "+tilesString);
                        }
                    }
                    index++;
                }
            }else{
                throw new Exception("no element "+TILES_ELEMENT+" specified");
            }
            // fill in the obstacles
            Element obstaclesElement = roomElement.getChild(OBSTACLES_ELEMENT);
            if(obstaclesElement != null)
            {
                String obstacleString = obstaclesElement.getText();
                int index = 0;
                for(int y=0; y<height; y++)
                {
                    for(int x=0; x<width; x++)
                    {
                        char c = obstacleString.charAt(index);
                        int obstacleType;
                        if(c == ' ')
                        {
                            // spaces make room files more readable
                            obstacleType = 0;
                        }else if(c == '\n'){
                            // populate the rest and break
                            for(int ix=x; ix<width; ix++)
                            {
                                room.obstacles[y][ix] = 0;
                            }
                            break;
                        }else{
                            obstacleType = Entity.MIN_OBSTACLE + ((int)c - (int)'A');
                        }
                        room.obstacles[y][x] = (byte)obstacleType;
                        index++;
                    }
                    if(index >= obstacleString.length())
                    {
                        if(y < height-1)
                        {
                            throw new Exception("expected obstacle string of length "+(room.width*room.height));
                        }
                    }else{
                        if(obstacleString.charAt(index) != '\n')
                        {
                            throw new Exception("expected \\n at index "+index+" in "+obstacleString);
                        }
                    }
                    index++;
                }
            }
            // read in room actions (for praying)
            readActionsAndItems(room, room, roomElement, true);

            // read in prototypes
            List prototypeElements = roomElement.getChildren(PROTOTYPE_ELEMENT);
            for(int i=0; i<prototypeElements.size(); i++)
            {
                Element prototypeElement = (Element)prototypeElements.get(i);
                byte entityTypeId = (byte)getMandatoryInt(prototypeElement, ENTITY_TYPE_ID_KEY);
                Entity prototype = getPrototype(entityTypeId);
                if(prototype == null)
                {
                    // actions may set placeholders for prototypes
                    prototype = getPlaceholder(entityTypeId);
                }
                readPrototype(room, prototype, prototypeElement);
            }
            // read in entities
            Element entitiesElement = roomElement.getChild(ENTITIES_ELEMENT);
            List entitiesList = entitiesElement.getChildren(ENTITY_ELEMENT);
            for(int i=0; i<entitiesList.size(); i++)
            {
                Element entityElement = (Element)entitiesList.get(i);
                int typeId = getMandatoryInt(entityElement, ENTITY_TYPE_ID_KEY);
                int side = getMandatoryInt(entityElement, SIDE_KEY);
                Entity entity;
                Element prototypeElement = entityElement.getChild(PROTOTYPE_ELEMENT);
                if(prototypeElement == null)
                {
                    entity = createEntity((byte)typeId, (byte)side);
                }else{
                    entity = new Entity();
                    readPrototype(room, entity, prototypeElement);
                    entity.side = (byte)side;
                }
                String entityName = entityElement.getAttributeValue(NAME_KEY);
                if(entityName != null)
                {
                    entity.name = entityName;
                }
                String exitName = entityElement.getAttributeValue(EXIT_NAME_KEY);
                int health = getOptionalInt(entityElement, HEALTH_KEY, entity.maxHealth);
                entity.health = (byte)health;
                String bossString = entityElement.getAttributeValue(BOSS_KEY);
                entity.setFlag(Entity.BOSS_FLAG, (bossString != null && "true".equals(bossString)));
                String pacifistString = entityElement.getAttributeValue(PACIFIST_KEY);
                entity.setFlag(Entity.PACIFIST_FLAG, ("true".equals(pacifistString)));
                readActionsAndItems(room, entity, entityElement, false);
                if(exitName != null)
                {
                    room.exit(exitName, entity);
                }else{
                    int x = getMandatoryInt(entityElement, X_KEY);
                    int y = getMandatoryInt(entityElement, Y_KEY);
                    entity.x = (byte)x;
                    entity.y = (byte)y;
                    room.addContainedEntity(entity);
                }
            }
            ActionHelper.packActions(room);
            return room;
        }catch(Exception ex){
            throw new RoomCreationException(name, "error loading room", ex);
        }
    }

    private void readPrototype(Room room, Entity prototype, Element prototypeElement)
        throws Exception
    {
        // prototype name
        String entityName = prototypeElement.getAttributeValue(NAME_KEY);
        if(entityName == null)
        {
            throw new Exception("no attribute "+NAME_KEY+" on "+PROTOTYPE_ELEMENT);
        }
        prototype.name = entityName;
        // prototype id
        int entityTypeId = getMandatoryInt(prototypeElement, ENTITY_TYPE_ID_KEY);
        prototype.entityTypeId = (byte)entityTypeId;
        // prototype type
        String entityTypeString = prototypeElement.getAttributeValue(ENTITY_TYPE_KEY);
        int entityType;
        if(entityTypeString == null)
        {
            throw new Exception("no attribute "+ENTITY_TYPE_KEY+" on "+PROTOTYPE_ELEMENT);
        }else if(entityTypeString.equals(ENTITY_TYPE_ITEM)){
            entityType = Entity.ITEM_TYPE;
        }else if(entityTypeString.equals(ENTITY_TYPE_MONSTER)){
            entityType = Entity.MONSTER_TYPE;
        }else if(entityTypeString.equals(ENTITY_TYPE_IMPASSABLE)){
            entityType = Entity.IMPASSABLE_TYPE;
        }else if(entityTypeString.equals(ENTITY_TYPE_INHERIT)){
            entityType = Entity.INHERIT_TYPE;
        }else if(entityTypeString.equals(ENTITY_TYPE_PHENOMINA)){
            entityType = Entity.PHENOMINA_TYPE;
        }else{
            throw new Exception("unknown value for "+
                    ENTITY_TYPE_KEY+", "+entityTypeString+" in "+PROTOTYPE_ELEMENT);
        }
        prototype.entityType = (byte)entityType;
        // prototype health
        prototype.maxHealth = (byte)getOptionalInt(prototypeElement, MAX_HEALTH_KEY);
        // prototype collision effect
        String collisionEffectString = prototypeElement.getAttributeValue(COLLISION_EFFECT_KEY);
        byte collisionEffect;
        if(collisionEffectString == null)
        {
            throw new Exception("no attribute "+COLLISION_EFFECT_KEY+" on "+PROTOTYPE_ELEMENT);
        }else if(collisionEffectString.equals(COLLISION_EFFECT_NONE)){
            collisionEffect = Entity.COLLISION_EFFECT_NONE;
        }else if(collisionEffectString.equals(COLLISION_EFFECT_MONSTER)){
            collisionEffect = Entity.COLLISION_EFFECT_MONSTER;
        }else if(collisionEffectString.equals(COLLISION_EFFECT_BOUNCE)){
            collisionEffect = Entity.COLLISION_EFFECT_BOUNCE;
        }else{
            throw new Exception("unrecognised value for attribute "+COLLISION_EFFECT_KEY+", "+
                    collisionEffectString+" on "+PROTOTYPE_ELEMENT);
        }
        prototype.flags = collisionEffect;
        // level
        prototype.level = (byte)getOptionalInt(prototypeElement, LEVEL_KEY);
        // speed
        prototype.speed = (byte)getOptionalInt(prototypeElement, SPEED_KEY);
        // frequency
        prototype.frequency = (byte)getOptionalInt(prototypeElement, FREQUENCY_KEY);
        // armour class
        prototype.baseArmourClass = (byte)getOptionalInt(prototypeElement, ARMOUR_CLASS_KEY);
        // attack bonus
        prototype.baseAttack = (byte)getOptionalInt(prototypeElement, ATTACK_BONUS_KEY);
        // entity category
        prototype.entityCategory = getCategory(prototypeElement);
        // read in the actions and items inherent to this monster
        readActionsAndItems(room, prototype, prototypeElement, true);

    }

    private void readActionsAndItems(Room room, Entity entity, Element entityElement, boolean inherent)
        throws Exception
    {
        Element itemsElement = entityElement.getChild(ITEMS_ELEMENT);
        if(itemsElement != null)
        {
            List itemElements = itemsElement.getChildren(ITEM_ELEMENT);
            for(int i=0; i<itemElements.size(); i++)
            {
                Element itemElement = (Element)itemElements.get(i);
                int id = getMandatoryInt(itemElement, ENTITY_TYPE_ID_KEY);
                Entity item = createEntity((byte)id, (byte)0);
                // if it's defined here, it's obviously not inherent
                readActionsAndItems(room, item, itemElement, false);
                entity.addContainedEntity(item);
            }
        }
        Element actionsElement = entityElement.getChild(ACTIONS_ELEMENT);
        if(actionsElement != null)
        {
            List actionElements = actionsElement.getChildren();
            if(entity.actions == null)
            {
                entity.actions = new Vector(actionElements.size());
            }else{
                entity.actions.ensureCapacity(entity.actions.size() + actionElements.size());
            }
            for(int i=0; i<actionElements.size(); i++)
            {
                Element actionElement = (Element)actionElements.get(i);
                Action action = getAction(room, entity, actionElement, inherent);
                entity.actions.addElement(action);
            }
        }
    }

    private Action getAction(Room room, Entity owner, Element actionElement, boolean inherent)
        throws Exception
    {
        Action action;
        if(actionElement.getName().equals(ACTION_ATTACK))
        {
            String name = actionElement.getAttributeValue(NAME_KEY);
            String desc = actionElement.getAttributeValue(ACTION_KEY);
            int cost = getMandatoryInt(actionElement, COST_KEY);
            int range = getMandatoryInt(actionElement, RANGE_KEY);
            int diceNum = getOptionalInt(actionElement, DICE_NUM_KEY);
            int diceSides = getOptionalInt(actionElement, DICE_SIDES_KEY);
            int diceBonus = getOptionalInt(actionElement, ATTACK_BONUS_KEY);
            byte category = getCategory(actionElement);
            AttackAction attack = new AttackAction(owner, name, desc, (byte)cost, (byte)range,
                    (byte)diceNum, (byte)diceSides, (byte)diceBonus,
                    category);
            // load the attack type
            String attackType = actionElement.getAttributeValue(TYPE_KEY);
            int type;
            if(attackType == null)
            {
                throw new Exception("must specify "+TYPE_KEY+" for "+ACTION_ATTACK);
            }else if(attackType.equals(ATTACK_TYPE_HAND_TO_HAND)){
                type = AttackAction.HAND_TO_HAND;
            }else if(attackType.equals(ATTACK_TYPE_ARROW)){
                type = AttackAction.ARROW;
            }else if(attackType.equals(ATTACK_TYPE_MAGIC)){
                type = AttackAction.MAGIC;
            }else if(attackType.equals(ATTACK_TYPE_THROWN_OBJECT)){
                type = AttackAction.THROWN_OBJECT;
            }else if(attackType.equals(ATTACK_TYPE_IMPLICIT)){
                type = AttackAction.IMPLICIT;
            }else{
                throw new Exception("unknown "+TYPE_KEY+", "+attackType+" on "+ACTION_ATTACK);
            }
            attack.type = (byte)type;

            // set the parent entity type
            if(owner != null && owner.entityType == Entity.ITEM_TYPE)
            {
                attack.itemName = owner.name;
            }

            // set the prerequisite categories
            attack.prerequsiteCategory = this.getCategory(actionElement, PREREQUISITE_CATEGORIES_ELEMENT);

            // set the radius
            attack.radius = (byte)this.getOptionalInt(actionElement, RADIUS_KEY);

            // consume the attack once it's done?
            String consumeString = actionElement.getAttributeValue(CONSUME_KEY);
            int consume;
            if(consumeString != null && "true".equals(consumeString))
            {
                consume = AttackAction.CONSUME_BIT;
            }else{
                consume = 0;
            }
            attack.flags |= consume;

            // creates something
            int create = getOptionalInt(actionElement, CREATE_KEY);
            attack.creates = getPlaceholder((byte)create);

            // chance of creating or conferring an effect
            String percentSuccessString = actionElement.getAttributeValue(PERCENT_SUCCESS_KEY);
            int percentSuccess;
            if(percentSuccessString == null)
            {
                percentSuccess = 100;
            }else{
                percentSuccess = Integer.parseInt(percentSuccessString);
            }
            attack.conferPercent = (byte)percentSuccess;

            // always hits?
            String alwaysHitsString = actionElement.getAttributeValue(ALWAYS_HITS_KEY);
            int alwaysHits;
            if(alwaysHitsString != null && "true".equals(alwaysHitsString))
            {
                alwaysHits = AttackAction.ALWAYS_HITS_BIT;
            }else{
                alwaysHits = 0;
            }
            attack.flags |= alwaysHits;

            // kills the parent
            String killParentString = actionElement.getAttributeValue(KILL_PARENT_KEY);
            int killParent;
            if(killParentString != null && "true".equals(killParentString))
            {
                killParent = AttackAction.KILL_PARENT_BIT;
            }else{
                killParent = 0;
            }
            attack.flags |= killParent;

            // effects a group
            String effectGroupString = actionElement.getAttributeValue(EFFECT_GROUP_KEY);
            int effectGroup;
            if(effectGroupString != null && "true".equals(effectGroupString))
            {
                effectGroup = AttackAction.EFFECT_GROUP_BIT;
            }else{
                effectGroup = 0;
            }
            attack.flags |= effectGroup;

            // spans a turn
            String spanTurnString = actionElement.getAttributeValue(SPAN_TURN_KEY);
            int spanTurn;
            if(spanTurnString != null && "true".equals(spanTurnString))
            {
                spanTurn = AttackAction.SPAN_TURN_BIT;
            }else{
                spanTurn = 0;
            }
            attack.flags |= spanTurn;

            // is good for the target
            String goodString = actionElement.getAttributeValue(GOOD_KEY);
            int good;
            if(goodString != null && "true".equals(goodString))
            {
                good = AttackAction.GOOD_BIT;
            }else{
                good = 0;
            }
            attack.flags |= good;

            // ends the turn
            String endTurnString = actionElement.getAttributeValue(END_TURN_KEY);
            if(endTurnString != null && endTurnString.equals("false"))
            {
                attack.flags &= ~AttackAction.END_TURN_BIT;
            }

            // confer an effect?
            Element confersElement = actionElement.getChild(CONFERS_ELEMENT);
            if(confersElement != null)
            {
                List confers = confersElement.getChildren();
                if(confers.size() != 1)
                {
                    throw new Exception("expected single confers element, found "+confers.size());
                }
                attack.confers = getAction(room, owner, (Element)confers.get(0), false);
            }
            action = attack;
        }else if(actionElement.getName().equals(ACTION_MOVE)){
            int cost = getMandatoryInt(actionElement, COST_KEY);
            MoveAction move = new MoveAction((byte)cost, owner);
            action = move;
        }else{
            action = null;
            boolean found = false;
            for(int i=0; i<SIMPLE_ACTION_NAMES.length; i++)
            {
                String simpleActionName = SIMPLE_ACTION_NAMES[i];
                if(simpleActionName.equals(actionElement.getName()))
                {
                    byte mode = (byte)i;
                    int cost = getMandatoryInt(actionElement, COST_KEY);
                    SimpleAction simple = new SimpleAction(owner, mode, (byte)cost, Entity.NONE_TYPE, false);
                    simple.name = actionElement.getAttributeValue(NAME_KEY);
                    simple.action = actionElement.getAttributeValue(ACTION_KEY);
                    simple.filter = getCategory(actionElement, FILTER_ELEMENT);
                    switch(mode)
                    {
                        case SimpleAction.CONVERT:
                            simple.convertSide = (byte)getMandatoryInt(actionElement, CONVERT_TO_SIDE_KEY);
                            break;
                        case SimpleAction.STONE:
                            simple.convertSide = (byte)getOptionalInt(actionElement, CONVERT_TO_SIDE_KEY);
                        case SimpleAction.DIE:
                            simple.item = getPlaceholder((byte)getOptionalInt(actionElement, CREATE_KEY));
                            break;
                        case SimpleAction.POLYMORPH:
                            simple.convertSide = (byte)getOptionalInt(actionElement, CONVERT_TO_SIDE_KEY);
                            simple.item = getPlaceholder((byte)getOptionalInt(actionElement, POLYMORPH_TO_KEY));
                            break;
                        case SimpleAction.PUTDOWN:
                            // assign the value to the id of the owner
                            simple.type = owner.entityTypeId;
                            break;
                        case SimpleAction.PROVOKE:
                            String provokedActionName = actionElement.getAttributeValue(ACTION_KEY);
                            for(int j=0; j<SIMPLE_ACTION_NAMES.length; j++)
                            {
                                if(provokedActionName.equals(SIMPLE_ACTION_NAMES[j]))
                                {
                                    simple.convertSide = (byte)j;
                                    break;
                                }
                            }
                            break;
                        case SimpleAction.HEAL:
                        case SimpleAction.HURT:
                            simple.convertSide = (byte)getMandatoryInt(actionElement, AMOUNT_KEY);
                            break;
                        case SimpleAction.EXIT:
                            simple.exitName = actionElement.getAttributeValue(EXIT_NAME_KEY);
                            if(simple.exitName == null)
                            {
                                throw new Exception("must specify an exit name for an exit action");
                            }
                            break;
                        case SimpleAction.LEARN:
                            List learnElements = actionElement.getChild(TO_LEARN_ELEMENT).getChildren();
                            if(learnElements.size() != 1)
                            {
                                throw new Exception("expected "+learnElements.size()+" child elements for "+TO_LEARN_ELEMENT);
                            }
                            Element learnElement = (Element)learnElements.get(0);
                            simple.item = getAction(room, null, learnElement, false);
                            break;
                        case SimpleAction.TRANSMUTATION:
                            simple.convertSide = (byte)getOptionalInt(actionElement, ARMOUR_BONUS_KEY);
                            break;
                    }

                    found = true;
                    action = simple;
                    break;
                }
            }
            if(!found)
            {
                throw new Exception("unknown action type "+actionElement.getName());
            }
        }
        return action;
    }

    private int getMandatoryInt(Element e, String key)
        throws Exception
    {
        String s = e.getAttributeValue(key);
        if(s == null)
        {
            throw new Exception("no attribute "+key+" on "+e.getName());
        }
        try
        {
            return Integer.parseInt(s);
        }catch(Exception ex){
            throw new Exception("error parsing attribute "+key+" with value "+s, ex);
        }
    }

    private int getOptionalInt(Element e, String key)
    {
        return getOptionalInt(e, key, 0);
    }

    private int getOptionalInt(Element e, String key, int defaultValue)
    {
        String s = e.getAttributeValue(key);
        int value;
        if(s == null)
        {
            value = defaultValue;
        }else{
            value = Integer.parseInt(s);
        }
        return value;
    }

    private byte getCategory(Element element)
        throws Exception
    {
        return getCategory(element, CATEGORIES_ELEMENT);
    }

    private byte getCategory(Element element, String childName)
        throws Exception
    {
        byte category = Entity.CATEGORY_NONE;
        Element categoriesElement = element.getChild(childName);
        if(categoriesElement != null)
        {
            List categoriesList = categoriesElement.getChildren(CATEGORY_ELEMENT);
            for(int j=0; j<categoriesList.size(); j++)
            {
                Element categoryElement = (Element)categoriesList.get(j);
                String type = categoryElement.getAttributeValue(TYPE_KEY);
                int mask = 0x01;
                boolean found = false;
                for(int k=0; k<CATEGORIES.length; k++)
                {
                    if(type.equals(CATEGORIES[k]))
                    {
                        found = true;
                        break;
                    }
                    mask <<= 1;
                }
                if(found)
                {
                    category |= mask;
                }else{
                    throw new Exception("unknown value for "+TYPE_KEY+", "+type+" in "+CATEGORY_ELEMENT);
                }
            }
        }
        return category;
    }

    public void storeRoom(DataOutputStream outs, Room room)
        throws IOException
    {
        Element roomElement = new Element(ROOM_ELEMENT);

        roomElement.setAttribute(WIDTH_KEY, Integer.toString(room.width));
        roomElement.setAttribute(HEIGHT_KEY, Integer.toString(room.height));
        roomElement.setAttribute(SAVE_KEY, Boolean.toString(room.flags == 0));
        if(room.name != null)
        {
            roomElement.setAttribute(NAME_KEY, room.name);
        }

        Element welcomeElement = new Element(WELCOME_MESSAGE_ELEMENT);
        welcomeElement.setText(room.welcomeMessage);
        roomElement.addContent(welcomeElement);

        Element winElement = new Element(WIN_MESSAGE_ELEMENT);
        winElement.setText(room.winMessage);
        String winStyle;
        switch(room.mode)
        {
            default:
            case Room.MODE_CONTINUOUS:
                winStyle = WIN_STYLE_CONTINUOUS;
                break;
            case Room.MODE_LAST_BOSS:
                winStyle = WIN_STYLE_LAST_BOSS;
                break;
            case Room.MODE_LAST_SIDE:
                winStyle = WIN_STYLE_LAST_MAN;
                break;
            case Room.MODE_WIN_ITEM:
                winStyle = WIN_STYLE_ITEM;
                break;
            case Room.WIN_EXTERNAL:
                winStyle = "";
                break;
        }
        winElement.setAttribute(WIN_STYLE_KEY, winStyle);
        roomElement.addContent(winElement);

        Element loseElement = new Element(LOSE_MESSAGE_ELEMENT);
        loseElement.setText(room.loseMessage);
        roomElement.addContent(loseElement);

        // store the tiles
        Element tilesElement = new Element(TILES_ELEMENT);
        StringBuffer tileString = new StringBuffer(room.width*room.height);
        for(int y = 0; y<room.height; y++)
        {
            for(int x = 0; x<room.width; x++)
            {
                char c;
                int tile = room.tiles[y][x];
                if(tile == 0)
                {
                    c = ' ';
                }else{
                    c = (char)((int)'A' + tile);
                }
                tileString.append(c);
            }
            tileString.append('\n');
        }
        tilesElement.setText(tileString.toString());
        roomElement.addContent(tilesElement);

        // store the obstacles
        Element obstaclesElement = new Element(OBSTACLES_ELEMENT);
        StringBuffer obstaclesString = new StringBuffer(room.width*room.height);
        for(int y = 0; y<room.height; y++)
        {
            for(int x = 0; x<room.width; x++)
            {
                char c;
                int obstacle = room.obstacles[y][x];
                if(obstacle == 0)
                {
                    c = ' ';
                }else{
                    c = (char)((int)'A' + (obstacle-Entity.MIN_OBSTACLE));
                }
                obstaclesString.append(c);
            }
            obstaclesString.append('\n');
        }
        obstaclesElement.setText(obstaclesString.toString());
        roomElement.addContent(obstaclesElement);

        // store the friends
        Element friendsElement = new Element(FRIENDS_ELEMENT);
        int friends = room.friendlySideFilter;
        int side = 0;
        while(friends > 0)
        {
            if((friends & 0x01) > 0)
            {
                Element friendElement = new Element(FRIEND_ELEMENT);
                friendElement.setAttribute(SIDE_KEY, Integer.toString(side));
                friendsElement.addContent(friendElement);
            }
            side++;
            friends = friends >> 1;
        }
        roomElement.addContent(friendsElement);

        // store the room actions (prayers)
        addActionsAndItems(roomElement, room, true);

        // store the entities
        // store the entities on the field
        Vector contained = room.getContainedEntities();
        Element entitiesElement = new Element(ENTITIES_ELEMENT);
        if(contained != null)
        {
            for(int i=0; i<contained.size(); i++)
            {
                Entity entity = (Entity)contained.elementAt(i);
                Element entityElement = new Element(ENTITY_ELEMENT);
                entityElement.setAttribute(NAME_KEY, entity.name);
                entityElement.setAttribute(ENTITY_TYPE_ID_KEY, Integer.toString(entity.entityTypeId));
                entityElement.setAttribute(X_KEY, Integer.toString(entity.x));
                entityElement.setAttribute(Y_KEY, Integer.toString(entity.y));
                entityElement.setAttribute(SIDE_KEY, Integer.toString(entity.side));
                entityElement.setAttribute(HEALTH_KEY, Integer.toString(entity.health));
                entityElement.setAttribute(BOSS_KEY, ""+entity.isFlag(Entity.BOSS_FLAG));

                // add the actions and items elements
                addActionsAndItems(entityElement, entity, false);
                // store the prototype for this entity
                Element prototypeElement = getPrototypeElement(entity);
                entityElement.addContent(prototypeElement);
                entitiesElement.addContent(entityElement);
            }
        }
        // store the entities in the exits
        Enumeration keys = room.exits.keys();
        while(keys.hasMoreElements())
        {
            String exitName = (String)keys.nextElement();
            Vector entities = (Vector)room.exits.get(exitName);
            for(int i=0; i<entities.size(); i++)
            {
                Entity entity = (Entity)entities.elementAt(i);
                Element entityElement = new Element(ENTITY_ELEMENT);
                entityElement.setAttribute(NAME_KEY, entity.name);
                entityElement.setAttribute(HEALTH_KEY, Integer.toString(entity.health));
                entityElement.setAttribute(BOSS_KEY, ""+entity.isFlag(Entity.BOSS_FLAG));
                entityElement.setAttribute(PACIFIST_KEY, ""+entity.isFlag(Entity.PACIFIST_FLAG));
                entityElement.setAttribute(ENTITY_TYPE_ID_KEY, Integer.toString(entity.entityTypeId));
                entityElement.setAttribute(SIDE_KEY, Integer.toString(entity.side));
                entityElement.setAttribute(EXIT_NAME_KEY, exitName);

                // add the actions and items elements
                addActionsAndItems(entityElement, entity, false);

                Element prototypeElement = getPrototypeElement(entity);
                entityElement.addContent(prototypeElement);
                entitiesElement.addContent(entityElement);
            }
        }
        roomElement.addContent(entitiesElement);

        // store the prototypes
        ArrayList content = new ArrayList(this.prototypes.size());
        for(int i=0; i<this.prototypes.size(); i++)
        {
            Entity prototype = (Entity)this.prototypes.get(i);
            Element prototypeElement = getPrototypeElement(prototype);
            content.add(0, prototypeElement);

        }
        for(int i=0; i<content.size(); i++)
        {
            // add the elements in reverse order to preserve integrity
            roomElement.addContent((Element)content.get(i));
        }
        Document document = new Document(roomElement);

        XMLOutputter outputter = new XMLOutputter("  ", true);
        outputter.output(document, outs);
    }

    private final Element getPrototypeElement(Entity prototype)
    {
        Element prototypeElement = new Element(PROTOTYPE_ELEMENT);
        prototypeElement.setAttribute(NAME_KEY, prototype.name);
        prototypeElement.setAttribute(ENTITY_TYPE_ID_KEY, Integer.toString(prototype.entityTypeId));
        String entityTypeName;
        switch(prototype.entityType)
        {
            case Entity.ITEM_TYPE:
                entityTypeName = ENTITY_TYPE_ITEM;
                break;
            case Entity.MONSTER_TYPE:
                entityTypeName = ENTITY_TYPE_MONSTER;
                break;
            case Entity.IMPASSABLE_TYPE:
                entityTypeName = ENTITY_TYPE_IMPASSABLE;
                break;
            case Entity.INHERIT_TYPE:
                entityTypeName = ENTITY_TYPE_INHERIT;
                break;
            case Entity.PHENOMINA_TYPE:
                entityTypeName = ENTITY_TYPE_PHENOMINA;
                break;
            default:
                entityTypeName = "unknown ("+prototype.entityType+")";
                break;
        }
        prototypeElement.setAttribute(ENTITY_TYPE_KEY, entityTypeName);
        prototypeElement.setAttribute(MAX_HEALTH_KEY, Integer.toString(prototype.maxHealth));

        String collisionEffectName;
        switch((prototype.flags & Entity.COLLISION_EFFECT_MASK))
        {
            case Entity.COLLISION_EFFECT_NONE:
                collisionEffectName = COLLISION_EFFECT_NONE;
                break;
            case Entity.COLLISION_EFFECT_BOUNCE:
                collisionEffectName = COLLISION_EFFECT_BOUNCE;
                break;
            case Entity.COLLISION_EFFECT_MONSTER:
                collisionEffectName = COLLISION_EFFECT_MONSTER;
                break;
            default:
                collisionEffectName = "unknown ("+prototype.flags+")";
                break;
        }
        prototypeElement.setAttribute(COLLISION_EFFECT_KEY, collisionEffectName);

        prototypeElement.setAttribute(LEVEL_KEY, Integer.toString(prototype.level));
        prototypeElement.setAttribute(ARMOUR_CLASS_KEY, Integer.toString(prototype.baseArmourClass));
        prototypeElement.setAttribute(ATTACK_BONUS_KEY, Integer.toString(prototype.baseAttack));
        prototypeElement.setAttribute(SPEED_KEY, Integer.toString(prototype.speed));
        prototypeElement.setAttribute(FREQUENCY_KEY, Integer.toString(prototype.frequency));

        Element categoryElement = getCategoryElement(prototype.entityCategory);
        prototypeElement.addContent(categoryElement);

        // add the actions and items elements
        addActionsAndItems(prototypeElement, prototype, true);
        return prototypeElement;
    }

    private void addActionsAndItems(Element entityElement, Entity entity, boolean inherent)
    {
        // store items
        Vector items = entity.getContainedEntities();
        if(items != null)
        {
            Element itemsElement = new Element(ITEMS_ELEMENT);
            for(int i=0; i<items.size(); i++)
            {
                Entity item = (Entity)items.elementAt(i);
                Element itemElement = new Element(ITEM_ELEMENT);
                itemElement.setAttribute(ENTITY_TYPE_ID_KEY, Integer.toString(item.entityTypeId));
                addActionsAndItems(itemElement, item, false);
                itemsElement.addContent(itemElement);
            }
            entityElement.addContent(itemsElement);
        }
        Vector actions = entity.actions;
        if(actions != null)
        {
            Element actionsElement = new Element(ACTIONS_ELEMENT);
            for(int i=0; i<actions.size(); i++)
            {
                Action action = (Action)actions.elementAt(i);
                if(inherent)
                {
                    Element actionElement = getActionElement(action);
                    actionsElement.addContent(actionElement);
                }
            }
            entityElement.addContent(actionsElement);
        }
    }

    private Element getActionElement(Action action)
    {
        Element actionElement;
        if(action instanceof MoveAction)
        {
            actionElement = new Element(ACTION_MOVE);
            actionElement.setAttribute(COST_KEY, Integer.toString(action.getTotalSteps()));
        }else if(action instanceof AttackAction){
            AttackAction attack = (AttackAction)action;
            actionElement = new Element(ACTION_ATTACK);
            actionElement.setAttribute(NAME_KEY, attack.getName());
            actionElement.setAttribute(ACTION_KEY, attack.action);
            actionElement.setAttribute(RANGE_KEY, Integer.toString(attack.range));
            actionElement.setAttribute(DICE_SIDES_KEY, Integer.toString(attack.diceSides));
            actionElement.setAttribute(DICE_NUM_KEY, Integer.toString(attack.diceNumber));
            actionElement.setAttribute(ATTACK_BONUS_KEY, Integer.toString(attack.diceBonus));
            if(attack.creates != null)
            {
                actionElement.setAttribute(CREATE_KEY, Integer.toString(attack.creates.entityTypeId));
                this.setPrototype(attack.creates);
            }
            actionElement.setAttribute(PERCENT_SUCCESS_KEY, Integer.toString(attack.conferPercent));
            actionElement.setAttribute(END_TURN_KEY, ""+attack.endsTurn());
            actionElement.setAttribute(RADIUS_KEY, Integer.toString(attack.radius));
            actionElement.setAttribute(COST_KEY, Integer.toString(attack.getTotalSteps()));
            String typeName;
            switch(attack.type)
            {
                case AttackAction.HAND_TO_HAND:
                    typeName = ATTACK_TYPE_HAND_TO_HAND;
                    break;
                case AttackAction.THROWN_OBJECT:
                    typeName = ATTACK_TYPE_THROWN_OBJECT;
                    break;
                case AttackAction.MAGIC:
                    typeName = ATTACK_TYPE_MAGIC;
                    break;
                case AttackAction.IMPLICIT:
                    typeName = ATTACK_TYPE_IMPLICIT;
                    break;
                case AttackAction.ARROW:
                    typeName = ATTACK_TYPE_ARROW;
                    break;
                default:
                    typeName = "unknown ("+attack.type+")";
                    break;
            }
            actionElement.setAttribute(TYPE_KEY, typeName);

            actionElement.addContent(getCategoryElement(attack.prerequsiteCategory, PREREQUISITE_CATEGORIES_ELEMENT));
            actionElement.addContent(getCategoryElement(attack.category));

            if((attack.flags & AttackAction.ALWAYS_HITS_BIT)>0)
            {
                actionElement.setAttribute(ALWAYS_HITS_KEY, "true");
            }
            if((attack.flags & AttackAction.CONSUME_BIT)>0)
            {
                actionElement.setAttribute(CONSUME_KEY, "true");
            }
            if((attack.flags & AttackAction.EFFECT_GROUP_BIT)>0)
            {
                actionElement.setAttribute(EFFECT_GROUP_KEY, "true");
            }
            if((attack.flags & AttackAction.KILL_PARENT_BIT)>0)
            {
                actionElement.setAttribute(KILL_PARENT_KEY, "true");
            }
            if((attack.flags & AttackAction.SPAN_TURN_BIT)>0)
            {
                actionElement.setAttribute(SPAN_TURN_KEY, "true");
            }
            if((attack.flags & AttackAction.GOOD_BIT) > 0)
            {
                actionElement.setAttribute(GOOD_KEY, "true");
            }
            if((attack.flags & AttackAction.END_TURN_BIT) == 0)
            {
                actionElement.setAttribute(END_TURN_KEY, "false");
            }
            if(attack.confers != null)
            {
                Element confersElement = new Element(CONFERS_ELEMENT);
                confersElement.addContent(getActionElement(attack.confers));
                actionElement.addContent(confersElement);
            }
        }else if(action instanceof SimpleAction){
            SimpleAction simple = (SimpleAction)action;
            String name = SIMPLE_ACTION_NAMES[simple.getMode()];
            actionElement = new Element(name);
            if(simple.name != null)
            {
                actionElement.setAttribute(NAME_KEY, simple.name);
            }
            if(simple.action != null)
            {
                actionElement.setAttribute(ACTION_KEY, simple.action);
            }
            actionElement.setAttribute(COST_KEY, Integer.toString(simple.getTotalSteps()));
            actionElement.addContent(getCategoryElement(simple.filter, FILTER_ELEMENT));
            switch(simple.getMode())
            {
                case SimpleAction.STONE:
                    actionElement.setAttribute(CONVERT_TO_SIDE_KEY, Integer.toString(simple.convertSide));
                case SimpleAction.DIE:
                    if(simple.item != null)
                    {
                        this.setPrototype((Entity)simple.item);
                        actionElement.setAttribute(CREATE_KEY, Integer.toString(((Entity)simple.item).entityTypeId));
                    }
                    break;
                case SimpleAction.CONVERT:
                    actionElement.setAttribute(CONVERT_TO_SIDE_KEY, Integer.toString(simple.convertSide));
                    break;
                case SimpleAction.POLYMORPH:
                    actionElement.setAttribute(CONVERT_TO_SIDE_KEY, Integer.toString(simple.convertSide));
                    if(simple.item != null)
                    {
                        this.setPrototype((Entity)simple.item);
                        actionElement.setAttribute(POLYMORPH_TO_KEY, Integer.toString(((Entity)simple.item).entityTypeId));
                    }
                    break;
                case SimpleAction.PROVOKE:
                    actionElement.setAttribute(ACTION_KEY, SIMPLE_ACTION_NAMES[simple.convertSide]);
                    break;
                case SimpleAction.HURT:
                case SimpleAction.HEAL:
                    actionElement.setAttribute(AMOUNT_KEY, Integer.toString(simple.convertSide));
                    break;
                case SimpleAction.EXIT:
                    if(simple.exitName == null)
                    {
                        throw new RuntimeException("action "+simple.name+" doesn't have an exit specified");
                    }
                    actionElement.setAttribute(EXIT_NAME_KEY, simple.exitName);
                    break;
                case SimpleAction.LEARN:
                    Element toLearnElement = new Element(TO_LEARN_ELEMENT);
                    toLearnElement.addContent(getActionElement((Action)simple.item));
                    actionElement.addContent(toLearnElement);
                    break;
                case SimpleAction.TRANSMUTATION:
                    actionElement.setAttribute(ARMOUR_BONUS_KEY, Integer.toString(simple.convertSide));
                    break;
            }
        }else{
            actionElement = null;
        }
        return actionElement;
    }

    private Element getCategoryElement(int category)
    {
        return getCategoryElement(category, CATEGORIES_ELEMENT);
    }

    private Element getCategoryElement(int category, String name)
    {
        Element categoriesElement = new Element(name);
        int copy = category;
        int categoryId = 0;
        while(copy > 0)
        {
            if((copy & 0x01)>0)
            {
                String categoryName = CATEGORIES[categoryId];
                Element categoryElement = new Element(CATEGORY_ELEMENT);
                categoryElement.setAttribute(TYPE_KEY, categoryName);
                categoriesElement.addContent(categoryElement);
            }
            copy = copy >> 1;
            categoryId ++;
        }
        return categoriesElement;
    }
}
