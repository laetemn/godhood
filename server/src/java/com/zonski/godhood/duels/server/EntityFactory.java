package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.action.MoveAction;
import com.zonski.godhood.duels.game.action.SimpleAction;
import com.zonski.godhood.duels.game.action.AttackAction;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Action;

import java.util.Vector;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 9/03/2004
 * Time: 11:51:11
 * To change this template use Options | File Templates.
 * <p>
 * Factory for obtaining hard coded implementations of common monsters
 */
public class EntityFactory
{
    // entity types

    // walls
    public static final byte EARTH_WALL_TYPE     = Entity.MIN_OBSTACLE;
    public static final byte ROCK_WALL_TYPE      = Entity.MIN_OBSTACLE + 1;
    public static final byte BRICK_WALL_TYPE     = Entity.MIN_OBSTACLE + 2;
    public static final byte FENCE_WALL_TYPE     = Entity.MIN_OBSTACLE + 3;
    public static final byte ROOM_WALL_TYPE      = Entity.MIN_OBSTACLE + 4;
    // trees
    public static final byte BUSH_TYPE           = Entity.MIN_OBSTACLE + 5;
    public static final byte OAK_TREE_TYPE       = Entity.MIN_OBSTACLE + 6;
    public static final byte PINE_TREE_TYPE      = Entity.MIN_OBSTACLE + 7;
    public static final byte DEAD_TREE_TYPE      = Entity.MIN_OBSTACLE + 8;
    public static final byte MALEVOLENT_TREE_TYPE= Entity.MIN_OBSTACLE + 9;
    // stuff
    public static final byte TOMBSTONE_TYPE      = Entity.MIN_OBSTACLE + 10;
    public static final byte FIRE_TYPE           = Entity.MIN_OBSTACLE + 11;

    public static final byte STATUE_TYPE         = Entity.MIN_MONSTER;

    // monsters
    public static final byte RED_DRAGON_TYPE     = Entity.MIN_MONSTER + 1;
    public static final byte FLOATING_EYE_TYPE   = Entity.MIN_MONSTER + 2;
    public static final byte STORM_CLOUD_TYPE    = Entity.MIN_MONSTER + 3;
    public static final byte GHOST_TYPE          = Entity.MIN_MONSTER + 4;
    public static final byte WIND_ELEMENTAL_TYPE = Entity.MIN_MONSTER + 5;
    public static final byte FIRE_ELEMENTAL_TYPE = Entity.MIN_MONSTER + 6;
    public static final byte WATER_ELEMENTAL_TYPE= Entity.MIN_MONSTER + 7;
    public static final byte EARTH_ELEMENTAL_TYPE= Entity.MIN_MONSTER + 8;
    public static final byte WORM_TYPE           = Entity.MIN_MONSTER + 9;
    public static final byte BEHOLDER_TYPE       = Entity.MIN_MONSTER + 10;

    // animals
    public static final byte DOG_TYPE            = Entity.MIN_MONSTER + 11;
    public static final byte CAT_TYPE            = Entity.MIN_MONSTER + 12;
    public static final byte SPIDER_TYPE         = Entity.MIN_MONSTER + 13;
    public static final byte DISPLACER_TYPE      = Entity.MIN_MONSTER + 14;
    public static final byte SHEEP_TYPE          = Entity.MIN_MONSTER + 15;

    // men
    public static final byte KNIGHT_TYPE         = Entity.MIN_MONSTER + 16;
    public static final byte MAGE_TYPE           = Entity.MIN_MONSTER + 17;
    public static final byte GOBLIN_TYPE         = Entity.MIN_MONSTER + 18;
    public static final byte DEMON_TYPE          = Entity.MIN_MONSTER + 19;
    public static final byte NYMPH_TYPE          = Entity.MIN_MONSTER + 20;
    public static final byte ANGEL_TYPE          = Entity.MIN_MONSTER + 21;
    public static final byte OGRE_TYPE           = Entity.MIN_MONSTER + 22;
    public static final byte ZOMBI_TYPE          = Entity.MIN_MONSTER + 23;
    public static final byte GOLEM_TYPE          = Entity.MIN_MONSTER + 24;
    public static final byte WIZARD_TYPE         = Entity.MIN_MONSTER + 25;
    public static final byte PRIEST_TYPE         = Entity.MIN_MONSTER + 26;
    public static final byte MAN_TYPE            = Entity.MIN_MONSTER + 27;
    public static final byte WOMAN_TYPE          = Entity.MIN_MONSTER + 28;

    // items
    public static final byte RING_OF_PROTECTION_TYPE = Entity.MIN_MONSTER + 29;
    public static final byte SCROLL_TYPE         = Entity.MIN_MONSTER + 30;
    public static final byte PLATE_ARMOUR_TYPE   = Entity.MIN_MONSTER + 31;
    public static final byte TOWER_SHIELD_TYPE   = Entity.MIN_MONSTER + 32;
    public static final byte FIRE_SWORD_TYPE     = Entity.MIN_MONSTER + 33;
    public static final byte ICE_SWORD_TYPE      = Entity.MIN_MONSTER + 34;
    public static final byte STAFF_TYPE          = Entity.MIN_MONSTER + 35;
    public static final byte KEY_TYPE            = Entity.MIN_MONSTER + 36;
    public static final byte BOOTS_OF_SPEED_TYPE = Entity.MIN_MONSTER + 37;
    public static final byte CROSS_TYPE          = Entity.MIN_MONSTER + 38;
    public static final byte RED_POTION_TYPE     = Entity.MIN_MONSTER + 39;
    public static final byte GREEN_POTION_TYPE   = Entity.MIN_MONSTER + 40;
    public static final byte BLUE_POTION_TYPE    = Entity.MIN_MONSTER + 41;
    public static final byte LIGHTNING_MACE_TYPE = Entity.MIN_MONSTER + 42;
    public static final byte DARK_BOW_TYPE       = Entity.MIN_MONSTER + 43;

    // spells and books
    public static final byte SPELLBOOK_TYPE      = Entity.MIN_MONSTER + 44;

    // furniture and stuff

    public static final byte TABLE_TYPE          = Entity.MIN_MONSTER + 46;
    public static final byte FOUNTAIN_TYPE       = Entity.MIN_MONSTER + 47;

    // phenomina
    public static final byte SPOT_FIRE_TYPE      = Entity.MIN_MONSTER + 48;
    public static final byte BIG_SPOT_FIRE_TYPE  = Entity.MIN_MONSTER + 49;
    public static final byte SMALL_LIGHTNING_TYPE= Entity.MIN_MONSTER + 50;
    public static final byte LIGHTNING_TYPE      = Entity.MIN_MONSTER + 51;
    public static final byte SLEEPING_GAS_TYPE   = Entity.MIN_MONSTER + 52;
    public static final byte WHIRLWIND_TYPE      = Entity.MIN_MONSTER + 53;
    public static final byte WATER_TYPE          = Entity.MIN_MONSTER + 54;
    public static final byte AVALANCHE_TYPE      = Entity.MIN_MONSTER + 55;
    public static final byte POISON_GAS_TYPE     = Entity.MIN_MONSTER + 56;

    // portals to other rooms
    public static final byte LADDER_UP_TYPE      = Entity.MIN_MONSTER + 57;
    public static final byte LADDER_DOWN_TYPE    = Entity.MIN_MONSTER + 58;
    public static final byte LEFT_ARCH_TYPE      = Entity.LEFT_ARCH_TYPE;
    public static final byte RIGHT_ARCH_TYPE     = Entity.RIGHT_ARCH_TYPE;
    public static final byte DOORWAY_TYPE        = Entity.MIN_MONSTER + 61;

    // more monsters
    public static final byte MANTICORE_TYPE      = Entity.MIN_MONSTER + 62;
    public static final byte HYDRA_TYPE          = Entity.MIN_MONSTER + 63;
    public static final byte THIEF_TYPE          = Entity.MIN_MONSTER + 64;

    // doors n' things
    public static final byte LEFT_DOOR_TYPE      = Entity.LEFT_DOOR_TYPE;
    public static final byte RIGHT_DOOR_TYPE     = Entity.RIGHT_DOOR_TYPE;

    // demi gods
    public static final byte DEMI_GOD_BAROMOPHET = Entity.MIN_MONSTER + 67;
    public static final byte EYE_OF_BAROMOPHET   = Entity.MIN_MONSTER + 68;

    // holy phenomina
    public static final byte HOLY_EFFECT_TYPE    = Entity.MIN_MONSTER + 69;

    // more monsters
    public static final byte MUMMY_TYPE          = Entity.MIN_MONSTER + 70;

    // worshipper of baromophet
    public static final byte WORSHIPPER_TYPE     = Entity.MIN_MONSTER + 71;

    public static final Hashtable ENTITIES                 = new Hashtable(100);

    private static final Vector DEFAULT_MONSTER_ACTIONS = new Vector();

    public static final String BITE_ACTION_NAME = "Bite";
    public static final String BITE_ACTION_ACTION = "bites";

    public static final String CLAW_ACTION_NAME = "Claw";
    public static final String CLAW_ACTION_ACTION = "claws";

    public static final String BURN_ACTION_NAME = "Burn";
    public static final String BURN_ACTION_ACTION = "burns";

    public static final String ELECTROCUTE_ACTION_NAME = "Zap";
    public static final String ELECTROCUTE_ACTION_ACTION = "zaps";

    public static final String SHOOT_ACTION_NAME = "Shoot";
    public static final String SHOOT_ACTION_ACTION = "shoots";

    public static final String SWORD_ACTION_NAME = "Slash";
    public static final String SWORD_ACTION_ACTION = "slashes";

    public static final String PUNCH_ACTION_NAME = "Punch";
    public static final String PUNCH_ACTION_ACTION = "punches";

    public static final String STAB_ACTION_NAME = "Stab";
    public static final String STAB_ACTION_ACTION = "stabs";

    public static final String FLAME_ACTION_NAME = "Flame";
    public static final String FLAME_ACTION_ACTION = "flames";

    public static final String GAZE_ACTION_NAME = "Gaze";
    public static final String GAZE_ACTION_ACTION = "gazes at";

    public static final String CLUB_ACTION_NAME = "Bludgeon";
    public static final String CLUB_ACTION_ACTION = "bashes";

    public static final String GAS_ACTION_NAME = "Envelop";
    public static final String GAS_ACTION_ACTION = "envelops";

    public static final String CAST_SLEEP_ACTION_NAME = "Cast Sleep";
    public static final String CAST_SLEEP_ACTION_ACTION = "casts sleep at";

    public static final String CAST_FIREBALL_NAME = "Fireball";
    public static final String CAST_FIREBALL_ACTION = "casts a fireball at";

    public static final String HYPNOTIC_STARE_ACTION_NAME = "Hypnotise";
    public static final String HYPNOTIC_STARE_ACTION_ACTION = "stares hypnotically at";

    public static final String DEATH_STARE_ACTION_NAME = "Death Gaze";
    public static final String DEATH_STARE_ACTION_ACTION = "gazes balefully at";

    public static final String WOUND_STARE_ACTION_NAME = "Cause Wounds";
    public static final String WOUND_STARE_ACTION_ACTION = "stares daggers at";

    public static final String STONE_STARE_ACTION_NAME = "Turn to Stone";
    public static final String STONE_STARE_ACTION_ACTION = "gazes stonily at";

    public static final String SLEEP_STARE_ACTION_NAME = "Lull to Sleep";
    public static final String SLEEP_STARE_ACTION_ACTION = "stares dreamily at";

    public static final String POSSESS_ACTION_NAME = "Possess";
    public static final String POSSESS_ACTION_ACTION = "possesses";

    public static final String DROWN_ACTION_NAME = "Drown";
    public static final String DROWN_ACTION_ACTION = "drowns";

    public static final String SPLASH_ACTION_NAME = "Splash";
    public static final String SPLASH_ACTION_ACTION = "splashes";

    public static final String WEB_ACTION_NAME = "Web";
    public static final String WEB_ACTION_ACTION = "fires webs at";
    public static final String STUCK = "Stuck";

    public static final String CRUSH_ACTION_NAME = "Crush";
    public static final String CRUSH_ACTION_ACTION = "crushes";

    public static final String CHARM_ACTION_NAME = "Charm";
    public static final String CHARM_ACTION_ACTION = "charms";

    public static final String HEAL_ACTION_NAME = "Heal";
    public static final String HEAL_ACTION_ACTION = "heals";

    public static final String HOLY_WORD_NAME = "Holy Word";
    public static final String SHOUT_ACTION_ACTION = "shouts at";

    public static final String BREATH_POISON_NAME = "Breath Poison Gas";
    public static final String BREATH_POISON_ACTION = "breaths poisonous gas at";

    public static final String DISPLACE_ACTION_NAME = "Displace";

    public static final String SLAP_ACTION_NAME = "Slap";
    public static final String SLAP_ACTION_ACTION = "slaps";

    static
    {
        ActionFactory actionFactory = ActionFactory.INSTANCE;

        // get some monster actions
        DEFAULT_MONSTER_ACTIONS.addElement(new MoveAction((byte)2, null));

        DEFAULT_MONSTER_ACTIONS.addElement(actionFactory.createEndturn(null));
        DEFAULT_MONSTER_ACTIONS.addElement(actionFactory.createDefend(null));
        DEFAULT_MONSTER_ACTIONS.addElement(actionFactory.createDie(null));
        DEFAULT_MONSTER_ACTIONS.addElement(actionFactory.createPickup(null));

        Action defaultChatAction = actionFactory.createChat(null);
        // add it to monsters that can make other monsters talk (ve vill make you talk)

        Entity spotFire = new Entity();
        spotFire.entityTypeId = SPOT_FIRE_TYPE;
        spotFire.entityType = Entity.PHENOMINA_TYPE;
        spotFire.flags |= Entity.COLLISION_EFFECT_NONE;
        spotFire.name = "Spot Fire";
        spotFire.frequency = 50;
        spotFire.speed = 4;
        spotFire.level = 5;
        spotFire.entityCategory = Entity.CATEGORY_ELEMENTAL;
        spotFire.baseAttack = 0;
        spotFire.actions = new Vector(1);
        AttackAction spotFireBurn = new AttackAction(
                null, BURN_ACTION_NAME, BURN_ACTION_ACTION,
                (byte)0, (byte)0, (byte)1, (byte)12, (byte)0, spotFire.entityCategory);
        spotFireBurn.flags |= AttackAction.EFFECT_GROUP_BIT | AttackAction.ALWAYS_HITS_BIT;
        spotFire.actions.addElement(spotFireBurn);
        spotFire.thinking = new SimpleAction(null, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(spotFire);

        Entity bigSpotFire = new Entity();
        bigSpotFire.entityTypeId = BIG_SPOT_FIRE_TYPE;
        bigSpotFire.entityType = Entity.PHENOMINA_TYPE;
        bigSpotFire.flags |= Entity.COLLISION_EFFECT_NONE;
        bigSpotFire.name = "Fire";
        bigSpotFire.frequency = 50;
        bigSpotFire.speed = 4;
        bigSpotFire.baseAttack = 0;
        bigSpotFire.level = 10;
        bigSpotFire.entityCategory = Entity.CATEGORY_ELEMENTAL;
        bigSpotFire.actions = new Vector(1);
        AttackAction bigSpotFireBurn = new AttackAction(
                null, BURN_ACTION_NAME, BURN_ACTION_ACTION,
                (byte)0, (byte)0, (byte)2, (byte)12, (byte)0, spotFire.entityCategory);
        bigSpotFireBurn.flags |= AttackAction.EFFECT_GROUP_BIT | AttackAction.ALWAYS_HITS_BIT;
        bigSpotFire.actions.addElement(bigSpotFireBurn);
        bigSpotFire.thinking = new SimpleAction(null, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(bigSpotFire);

        Entity smallLightning = new Entity();
        smallLightning.entityTypeId = SMALL_LIGHTNING_TYPE;
        smallLightning.entityType = Entity.PHENOMINA_TYPE;
        smallLightning.flags = Entity.COLLISION_EFFECT_NONE;
        smallLightning.name = "Lightning";
        smallLightning.frequency = 50;
        smallLightning.speed = 4;
        smallLightning.baseAttack = 0;
        smallLightning.entityCategory = Entity.CATEGORY_ELEMENTAL;
        smallLightning.level = 4;
        smallLightning.actions = new Vector(1);
        smallLightning.actions.addElement(new AttackAction(
                null, ELECTROCUTE_ACTION_NAME, ELECTROCUTE_ACTION_ACTION,
                (byte)0, (byte)0, (byte)1, (byte)8, (byte)0, smallLightning.entityCategory));
        smallLightning.thinking = new SimpleAction(null, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(smallLightning);

        Entity lightning = new Entity();
        lightning.entityTypeId = LIGHTNING_TYPE;
        lightning.entityType = Entity.PHENOMINA_TYPE;
        lightning.flags = Entity.COLLISION_EFFECT_NONE;
        lightning.name = "Lightning";
        lightning.frequency = 50;
        lightning.speed = 4;
        lightning.baseAttack = 0;
        lightning.level = 8;
        lightning.entityCategory = Entity.CATEGORY_ELEMENTAL;
        lightning.actions = new Vector(1);
        lightning.actions.addElement(new AttackAction(
                null, ELECTROCUTE_ACTION_NAME, ELECTROCUTE_ACTION_ACTION,
                (byte)0, (byte)0, (byte)3, (byte)8, (byte)0, lightning.entityCategory));
        lightning.thinking = new SimpleAction(null, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(lightning);

        Entity sleepingGas = new Entity();
        sleepingGas.entityTypeId = SLEEPING_GAS_TYPE;
        sleepingGas.entityType = Entity.PHENOMINA_TYPE;
        sleepingGas.flags = Entity.COLLISION_EFFECT_NONE;
        sleepingGas.name = "Sleeping Gas";
        sleepingGas.frequency = 50;
        sleepingGas.speed = 4;
        sleepingGas.level = 8;
        sleepingGas.entityCategory = Entity.CATEGORY_ELEMENTAL;
        sleepingGas.actions = new Vector(1);
        AttackAction sleepingGasAction = new AttackAction(
                null, GAS_ACTION_NAME, GAS_ACTION_ACTION,
                (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, sleepingGas.entityCategory);
        sleepingGasAction.conferPercent = 70;
        sleepingGasAction.confers = new SimpleAction(sleepingGas, SimpleAction.DAZED, (byte)20, Entity.NONE_TYPE, false);
        sleepingGas.actions.addElement(sleepingGasAction);
        sleepingGas.thinking = new SimpleAction(null, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(sleepingGas);

        Entity poisonGas = new Entity();
        poisonGas.entityTypeId = POISON_GAS_TYPE;
        poisonGas.entityType = Entity.PHENOMINA_TYPE;
        poisonGas.flags = Entity.COLLISION_EFFECT_NONE;
        poisonGas.name = "Poison Gas";
        poisonGas.frequency = 50;
        poisonGas.speed = 4;
        poisonGas.baseAttack = 0;
        poisonGas.level = 6;
        poisonGas.entityCategory = Entity.CATEGORY_ELEMENTAL;
        poisonGas.actions = new Vector(1);
        AttackAction poisonGasAction = new AttackAction(
                null, GAS_ACTION_NAME, GAS_ACTION_ACTION,
                (byte)0, (byte)0, (byte)1, (byte)4, (byte)1, poisonGas.entityCategory
        );
        // TODO : should poison gas be potentially fatal?
        // TODO : should poison gas drain strength?
        poisonGas.actions.addElement(poisonGasAction);
        poisonGas.thinking = new SimpleAction(null, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(poisonGas);

        Entity wind = new Entity();
        wind.entityTypeId = WHIRLWIND_TYPE;
        wind.entityType = Entity.PHENOMINA_TYPE;
        wind.name = "Whirlwind";
        wind.frequency = 50;
        wind.speed = 4;
        wind.entityCategory = Entity.CATEGORY_ELEMENTAL;
        wind.actions = new Vector(1);
        wind.actions.addElement(new AttackAction(wind, GAS_ACTION_NAME, GAS_ACTION_ACTION,
                (byte)4, (byte)0, (byte)1, (byte)20, (byte)0, wind.entityCategory));
        wind.thinking = new SimpleAction(wind, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(wind);

        Entity avalanche = new Entity();
        avalanche.entityTypeId = AVALANCHE_TYPE;
        avalanche.entityType = Entity.PHENOMINA_TYPE;
        avalanche.name = "Avalanche";
        avalanche.frequency = 50;
        avalanche.speed = 4;
        avalanche.entityCategory = Entity.CATEGORY_ELEMENTAL;
        avalanche.actions = new Vector(1);
        avalanche.actions.addElement(new AttackAction(avalanche, CRUSH_ACTION_NAME, CRUSH_ACTION_ACTION,
                (byte)4, (byte)0, (byte)5, (byte)4, (byte)0, avalanche.entityCategory));
        avalanche.thinking = new SimpleAction(avalanche, SimpleAction.SELF_DESTRUCT, (byte)0, Entity.NONE_TYPE, false);
        putPrototype(avalanche);

        Entity rockWall = new Entity();
        rockWall.entityTypeId = ROCK_WALL_TYPE;
        rockWall.entityType = Entity.IMPASSABLE_TYPE;
        rockWall.flags = Entity.COLLISION_EFFECT_BOUNCE;
        rockWall.name = "Rock Wall";
        putPrototype(rockWall);

        Entity brickWall = new Entity();
        brickWall.entityTypeId = BRICK_WALL_TYPE;
        brickWall.entityType = Entity.IMPASSABLE_TYPE;
        brickWall.flags = Entity.COLLISION_EFFECT_BOUNCE;
        brickWall.name = "Brick Wall";
        putPrototype(brickWall);

        Entity fenceWall  = new Entity();
        fenceWall.entityTypeId = FENCE_WALL_TYPE;
        fenceWall.entityType = Entity.IMPASSABLE_TYPE;
        fenceWall.flags = Entity.COLLISION_EFFECT_BOUNCE;
        fenceWall.name = "Fence";
        putPrototype(fenceWall);

        Entity roomWall = new Entity();
        roomWall.entityTypeId = ROOM_WALL_TYPE;
        roomWall.entityType = Entity.IMPASSABLE_TYPE;
        roomWall.flags = Entity.COLLISION_EFFECT_BOUNCE;
        roomWall.name = "Wall";
        putPrototype(roomWall);

        Entity bush = new Entity();
        bush.entityTypeId = BUSH_TYPE;
        bush.entityType = Entity.IMPASSABLE_TYPE;
        bush.flags = Entity.COLLISION_EFFECT_BOUNCE;
        bush.name = "Bush";
        putPrototype(bush);

        Entity pineTree = new Entity();
        pineTree.entityTypeId = PINE_TREE_TYPE;
        pineTree.entityType = Entity.IMPASSABLE_TYPE;
        pineTree.flags = Entity.COLLISION_EFFECT_BOUNCE;
        pineTree.name = "Pine Tree";
        putPrototype(pineTree);

        Entity deadTree = new Entity();
        deadTree.entityTypeId = DEAD_TREE_TYPE;
        deadTree.entityType = Entity.IMPASSABLE_TYPE;
        deadTree.flags = Entity.COLLISION_EFFECT_BOUNCE;
        deadTree.name = "Dead Tree";
        putPrototype(deadTree);

        Entity statue = new Entity();
        statue.entityTypeId = STATUE_TYPE;
        statue.entityType = Entity.IMPASSABLE_TYPE;
        statue.flags = Entity.COLLISION_EFFECT_BOUNCE;
        statue.entityCategory = Entity.CATEGORY_ELEMENTAL;
        statue.name = "Statue";
        statue.health = 10;
        statue.baseArmourClass = 0;
        statue.actions = new Vector(1);
        Action statueDie = actionFactory.createDie(statue, null, "The {0} crumbles to dust",  Entity.NONE_TYPE, false);
        statue.actions.addElement(statueDie);
        putPrototype(statue);

        Entity fountain = new Entity();
        fountain.entityTypeId = FOUNTAIN_TYPE;
        fountain.entityType = Entity.INHERIT_TYPE;
        fountain.flags = Entity.COLLISION_EFFECT_NONE;
        fountain.name = "Fountain";
        putPrototype(fountain);

        Entity malevolentTree = new Entity();
        malevolentTree.entityTypeId = MALEVOLENT_TREE_TYPE;
        malevolentTree.entityType = Entity.MONSTER_TYPE;
        malevolentTree.level = 10;
        malevolentTree.flags = Entity.COLLISION_EFFECT_MONSTER;
        malevolentTree.name = "Evil Tree";
        malevolentTree.speed = 3;
        malevolentTree.frequency = 100;
        malevolentTree.health = 40;
        malevolentTree.baseAttack = 5;
        malevolentTree.baseArmourClass = -2;
        malevolentTree.actions = new Vector(2);
        AttackAction malevolentTreeAttack
                = new AttackAction(malevolentTree, CLUB_ACTION_NAME, CLUB_ACTION_ACTION,
                        (byte)3, (byte)1, (byte)1, (byte)5, (byte)2, Entity.CATEGORY_ELEMENTAL);
        malevolentTree.actions.addElement(actionFactory.createEndturn(malevolentTree));
        malevolentTree.actions.addElement(malevolentTreeAttack);
        malevolentTree.entityCategory = Entity.CATEGORY_ELEMENTAL;
        putPrototype(malevolentTree);

        Entity dog = new Entity();
        dog.entityTypeId = DOG_TYPE;
        dog.entityType = Entity.MONSTER_TYPE;
        dog.level = 1;
        dog.speed = 10;
        dog.frequency = 50;
        dog.health = 20;
        dog.baseAttack = 4;
        dog.baseArmourClass = 8;
        dog.name = "Dog";
        dog.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        dog.flags = Entity.COLLISION_EFFECT_MONSTER;
        dog.entityCategory = Entity.CATEGORY_ANIMAL;
        AttackAction dogAttack = new AttackAction(dog, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)4, (byte)1, Entity.CATEGORY_NONE);
        dog.actions.addElement(dogAttack);
        putPrototype(dog);

        Entity cat = new Entity();
        cat.entityTypeId = CAT_TYPE;
        cat.entityType = Entity.MONSTER_TYPE;
        cat.level = 0;
        cat.speed = 8;
        cat.frequency = 42;
        cat.health = 12;
        cat.baseAttack = 1;
        cat.baseArmourClass = 7;
        cat.name = "Cat";
        cat.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        cat.flags = Entity.COLLISION_EFFECT_MONSTER;
        cat.entityCategory = Entity.CATEGORY_ANIMAL | Entity.CATEGORY_MAGIC; // cat's are a little bit magic, why not
        AttackAction catAttack = new AttackAction(cat, CLAW_ACTION_NAME, CLAW_ACTION_ACTION,
                (byte)4, (byte)1, (byte)1, (byte)3, (byte)0, Entity.CATEGORY_NONE);
        cat.actions.addElement(catAttack);
        putPrototype(cat);

        Entity spider = new Entity();
        spider.entityTypeId = SPIDER_TYPE;
        spider.entityType = Entity.MONSTER_TYPE;
        spider.level = 2;
        spider.speed = 8;
        spider.frequency = 60;
        spider.health = 25;
        spider.baseAttack = 5;
        spider.baseArmourClass = 5;
        spider.name = "Spider";
        spider.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        spider.flags = Entity.COLLISION_EFFECT_MONSTER;
        spider.entityCategory = Entity.CATEGORY_ANIMAL;
        AttackAction spiderAttack = new AttackAction(spider, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)4, (byte)1, (byte)1, (byte)4, (byte)0, Entity.CATEGORY_NONE);
        spiderAttack.conferPercent = 4;
        SimpleAction spiderBiteConfers = new SimpleAction(spider, SimpleAction.DIE, (byte)0, Entity.NONE_TYPE, false);
        spiderBiteConfers.name = "fatally poisoned";
        spiderBiteConfers.filter = Entity.CATEGORY_NOT | Entity.CATEGORY_UNDEAD;
        spiderAttack.confers = spiderBiteConfers;
        spider.actions.addElement(spiderAttack);
        AttackAction spiderWebAttack = new AttackAction(spider, WEB_ACTION_NAME, WEB_ACTION_ACTION,
                (byte)8, (byte)3, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_ELEMENTAL);
        spiderWebAttack.conferPercent = 80;
        SimpleAction spiderWebAttackConfers = new SimpleAction(spider, SimpleAction.DAZED, (byte)30, Entity.NONE_TYPE, false);
        spiderWebAttackConfers.name = STUCK;
        spiderWebAttack.confers = spiderWebAttackConfers;
        spider.actions.addElement(spiderWebAttack);
        putPrototype(spider);

        Entity displacer = new Entity();
        displacer.entityTypeId = DISPLACER_TYPE;
        displacer.entityType = Entity.MONSTER_TYPE;
        displacer.level = 8;
        displacer.speed = 18;
        displacer.frequency = 40;
        displacer.health = 40;
        displacer.baseAttack = 4;
        displacer.baseArmourClass = -2;
        displacer.name = "Displacer Beast";
        displacer.flags = Entity.COLLISION_EFFECT_MONSTER;
        displacer.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        AttackAction displacerAttack = new AttackAction(displacer, DISPLACE_ACTION_NAME, CLUB_ACTION_ACTION,
                (byte)9, (byte)1, (byte)1, (byte)6, (byte)0, Entity.CATEGORY_NONE);
        displacerAttack.conferPercent = 95;
        SimpleAction displaceAttackConfers = new SimpleAction(displacer, SimpleAction.TELEPORT, (byte)0, Entity.NONE_TYPE, false);
        displaceAttackConfers.name = "displaced";
        displacerAttack.confers = displaceAttackConfers;
        displacer.actions.addElement(displacerAttack);
        AttackAction displacerBiteAttack = new AttackAction(displacer, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)4, (byte)1, (byte)2, (byte)4, (byte)0, Entity.CATEGORY_NONE);
        displacer.actions.addElement(displacerBiteAttack);
        putPrototype(displacer);

        Entity sheep = new Entity();
        sheep.entityTypeId = SHEEP_TYPE;
        sheep.entityType = Entity.MONSTER_TYPE;
        sheep.level = 0;
        sheep.speed = 9;
        sheep.frequency = 50;
        sheep.health = 12;
        sheep.baseAttack = 0;
        sheep.baseArmourClass = 10;
        sheep.name = "Sheep";
        sheep.flags = Entity.COLLISION_EFFECT_MONSTER;
        sheep.entityCategory = Entity.CATEGORY_ANIMAL;
        sheep.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        AttackAction sheepAttack = new AttackAction(sheep, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)2, (byte)0, Entity.CATEGORY_NONE);
        sheep.actions.addElement(sheepAttack);
        putPrototype(sheep);

        Entity knight = new Entity();
        knight.entityTypeId = KNIGHT_TYPE;
        knight.entityType = Entity.MONSTER_TYPE;
        knight.level = 7;
        knight.speed = 12;
        knight.frequency = 40;
        knight.health = 30;
        knight.baseAttack = 3;
        knight.baseArmourClass = 0;
        knight.name = "Knight";
        knight.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        knight.flags = Entity.COLLISION_EFFECT_MONSTER;
        knight.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_INTELLIGENT;
        AttackAction knightAttack = new AttackAction(knight, SWORD_ACTION_NAME, SWORD_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)8, (byte)0, Entity.CATEGORY_NONE);
        knight.actions.addElement(knightAttack);
        putPrototype(knight);

        Entity mage = new Entity();
        mage.entityTypeId = MAGE_TYPE;
        mage.entityType = Entity.MONSTER_TYPE;
        mage.level = 12;
        mage.speed = 12;
        mage.frequency = 50;
        mage.health = 30;
        mage.baseAttack = 0;
        mage.baseArmourClass = 7;
        mage.name = "Mage";
        mage.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        mage.flags = Entity.COLLISION_EFFECT_MONSTER;
        mage.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_ANIMAL | Entity.CATEGORY_MAGIC | Entity.CATEGORY_INTELLIGENT;
        AttackAction mageAttack = new AttackAction(mage, PUNCH_ACTION_NAME, PUNCH_ACTION_ACTION,
                (byte)2, (byte)1, (byte)1, (byte)2, (byte)0, Entity.CATEGORY_NONE);
        mage.actions.addElement(mageAttack);
        AttackAction mageFlameAction = new AttackAction(mage, FLAME_ACTION_NAME, FLAME_ACTION_ACTION,
                (byte)10, (byte)4, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_ELEMENTAL);
        mageFlameAction.flags |= AttackAction.CONSUME_BIT;
        mageFlameAction.type = AttackAction.MAGIC;
        mageFlameAction.creates = create(BIG_SPOT_FIRE_TYPE);
        mage.actions.addElement(mageFlameAction);
        AttackAction mageLightningAction = new AttackAction(mage, ELECTROCUTE_ACTION_NAME, ELECTROCUTE_ACTION_ACTION,
                (byte)10, (byte)4, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_ELEMENTAL);
        mageLightningAction.flags |= AttackAction.CONSUME_BIT;
        mageLightningAction.type = AttackAction.MAGIC;
        mageLightningAction.creates = create(LIGHTNING_TYPE);
        mage.actions.addElement(mageLightningAction);
        AttackAction mageSleepAction = new AttackAction(mage, CAST_SLEEP_ACTION_NAME, CAST_SLEEP_ACTION_ACTION,
                (byte)4, (byte)4, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_MAGIC);
        mageSleepAction.flags |= AttackAction.CONSUME_BIT;
        mageSleepAction.radius = 1;
        mageSleepAction.type = AttackAction.MAGIC;
        mageSleepAction.creates = create(SLEEPING_GAS_TYPE);
        mage.actions.addElement(mageSleepAction);
        AttackAction mageFireballAction = new AttackAction(mage, CAST_FIREBALL_NAME, CAST_FIREBALL_ACTION,
                (byte)10, (byte)4, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_ELEMENTAL);
        mageFireballAction.flags |= AttackAction.CONSUME_BIT;
        mageFireballAction.type = AttackAction.MAGIC;
        mageFireballAction.radius = 2;
        mageFireballAction.creates = create(SPOT_FIRE_TYPE);
        mage.actions.addElement(mageFireballAction);
        mage.actions.addElement(defaultChatAction);
        putPrototype(mage);

        Entity goblin = new Entity();
        goblin.entityTypeId = GOBLIN_TYPE;
        goblin.entityType = Entity.MONSTER_TYPE;
        goblin.level = 2;
        goblin.speed = 9;
        goblin.frequency = 45;
        goblin.health = 20;
        goblin.baseAttack = 0;
        goblin.baseArmourClass = 7;
        goblin.name = "Goblin";
        goblin.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        goblin.flags = Entity.COLLISION_EFFECT_MONSTER;
        goblin.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_INTELLIGENT;
        AttackAction goblinAttack = new AttackAction(goblin, STAB_ACTION_NAME, STAB_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)4, (byte)1, Entity.CATEGORY_NONE);
        goblin.actions.addElement(goblinAttack);
        putPrototype(goblin);

        Entity demon = new Entity();
        demon.entityTypeId = DEMON_TYPE;
        demon.entityType = Entity.MONSTER_TYPE;
        demon.level = 8;
        demon.speed = 12;
        demon.frequency = 50;
        demon.health = 60;
        demon.baseAttack = 4;
        demon.baseArmourClass = 2;
        demon.name = "Demon";
        demon.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        demon.flags = Entity.COLLISION_EFFECT_MONSTER;
        demon.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_ELEMENTAL | Entity.CATEGORY_INTELLIGENT;
        AttackAction demonAttack = new AttackAction(demon, CLAW_ACTION_NAME, CLAW_ACTION_ACTION,
                (byte)3, (byte)1, (byte)2, (byte)4, (byte)0, Entity.CATEGORY_NONE);
        demon.actions.addElement(demonAttack);
        putPrototype(demon);

        Entity nymph = new Entity();
        nymph.entityTypeId = NYMPH_TYPE;
        nymph.entityType = Entity.MONSTER_TYPE;
        nymph.level = 3;
        nymph.speed = 14;
        nymph.frequency = 48;
        nymph.health = 18;
        nymph.baseAttack = 0;
        nymph.baseArmourClass = 4;
        nymph.name = "Nymph";
        nymph.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        nymph.flags = Entity.COLLISION_EFFECT_MONSTER;
        nymph.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_ELEMENTAL | Entity.CATEGORY_MAGIC | Entity.CATEGORY_INTELLIGENT;
        AttackAction nymphCharmAttack = new AttackAction(nymph, CHARM_ACTION_NAME, CHARM_ACTION_ACTION,
                (byte)12, (byte)1, (byte)0, (byte)0, (byte)0, nymph.entityCategory);
        SimpleAction nymphCharmAttackConfers = new SimpleAction(nymph, SimpleAction.CONVERT, (byte)0, Entity.NONE_TYPE, false);
        nymphCharmAttackConfers.filter = Entity.CATEGORY_NOT | Entity.CATEGORY_UNDEAD;
        nymphCharmAttackConfers.action = "the {0} falls in love with the nymph";
        nymphCharmAttack.confers = nymphCharmAttackConfers;
        nymphCharmAttack.conferPercent = 30;
        nymph.actions.addElement(nymphCharmAttack);
        AttackAction nymphAttack = new AttackAction(nymph, CLAW_ACTION_NAME, CLAW_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)2, (byte)0, Entity.CATEGORY_NONE);
        nymph.actions.addElement(nymphAttack);
        putPrototype(nymph);

        Entity angel = new Entity();
        angel.entityTypeId = ANGEL_TYPE;
        angel.entityType = Entity.MONSTER_TYPE;
        angel.level = 10;
        angel.speed = 16;
        angel.frequency = 50;
        angel.health = 70;
        angel.baseAttack = 8;
        angel.baseArmourClass = -2;
        angel.name = "Angel";
        angel.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        angel.flags = Entity.COLLISION_EFFECT_MONSTER;
        angel.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_INTELLIGENT | Entity.CATEGORY_FLYING;
        AttackAction angelLightningAttack = new AttackAction(angel, ELECTROCUTE_ACTION_NAME, ELECTROCUTE_ACTION_ACTION,
                (byte)12, (byte)6, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_NONE);
        angelLightningAttack.conferPercent = 100;
        angelLightningAttack.creates = create(SMALL_LIGHTNING_TYPE);
        angelLightningAttack.type = AttackAction.MAGIC;
        angel.actions.addElement(angelLightningAttack);
        AttackAction angelHolyWordAttack = new AttackAction(angel, HOLY_WORD_NAME, SHOUT_ACTION_ACTION,
                (byte)6, (byte)2, (byte)1, (byte)20, (byte)0, Entity.CATEGORY_NONE);
        angelHolyWordAttack.confers = new SimpleAction(angel, SimpleAction.DAZED, (byte)15, Entity.NONE_TYPE, false);
        angelHolyWordAttack.conferPercent = 10;
        angel.actions.addElement(angelHolyWordAttack);
        AttackAction angelHealAttack = new AttackAction(angel, HEAL_ACTION_NAME, HEAL_ACTION_ACTION,
                (byte)10, (byte)1, (byte)1, (byte)-10, (byte)0, Entity.CATEGORY_NONE);
        angelHealAttack.conferPercent = 6;
        angelHealAttack.confers = new SimpleAction(angel, SimpleAction.CONVERT, (byte)0, Entity.NONE_TYPE, false);
        angel.actions.addElement(angelHealAttack);
        putPrototype(angel);

        Entity ogre = new Entity();
        ogre.entityTypeId = OGRE_TYPE;
        ogre.entityType = Entity.MONSTER_TYPE;
        ogre.level = 7;
        ogre.speed = 14;
        ogre.frequency = 60;
        ogre.baseAttack = 12;
        ogre.baseArmourClass = 4;
        ogre.health = 50;
        ogre.name = "Ogre";
        ogre.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        ogre.flags = Entity.COLLISION_EFFECT_MONSTER;
        ogre.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_INTELLIGENT;
        AttackAction ogreAttack = new AttackAction(ogre, CLUB_ACTION_NAME, CLUB_ACTION_ACTION,
                (byte)6, (byte)1, (byte)1, (byte)6, (byte)0, Entity.CATEGORY_NONE);
        ogre.actions.addElement(ogreAttack);
        putPrototype(ogre);

        Entity zombi = new Entity();
        zombi.entityTypeId = ZOMBI_TYPE;
        zombi.entityType = Entity.MONSTER_TYPE;
        zombi.level = 3;
        zombi.speed = 8;
        zombi.frequency = 40;
        zombi.baseAttack = 1;
        zombi.baseArmourClass = 10;
        zombi.health = 55;
        zombi.name = "Zombi";
        zombi.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        zombi.flags = Entity.COLLISION_EFFECT_MONSTER;
        zombi.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_UNDEAD;
        AttackAction zombiAttack = new AttackAction(zombi, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)2, (byte)1, (byte)1, (byte)4, (byte)0, Entity.CATEGORY_UNDEAD);
        zombiAttack.conferPercent = 5;
        SimpleAction zombiAttackConfers = new SimpleAction(zombi, SimpleAction.POLYMORPH, (byte)0, ZOMBI_TYPE, false);
        zombiAttackConfers.name = "turned";
        zombiAttackConfers.filter = Entity.CATEGORY_NOT | Entity.CATEGORY_UNDEAD;
        zombiAttack.confers = zombiAttackConfers;
        zombi.actions.addElement(zombiAttack);
        putPrototype(zombi);

        Entity mummy = new Entity();
        mummy.entityTypeId = MUMMY_TYPE;
        mummy.entityType = Entity.MONSTER_TYPE;
        mummy.level = 10;
        mummy.speed = 12;
        mummy.frequency = 50;
        mummy.baseAttack = 2;
        mummy.baseArmourClass = 5;
        mummy.health = 60;
        mummy.name = "Mummy";
        mummy.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        mummy.flags = Entity.COLLISION_EFFECT_MONSTER;
        mummy.entityCategory = Entity.CATEGORY_HUMANOID | Entity.CATEGORY_UNDEAD;
        AttackAction mummyAttack = new AttackAction(mummy, CLAW_ACTION_NAME, CLAW_ACTION_ACTION,
                (byte)2, (byte)1, (byte)2, (byte)6, (byte)0, Entity.CATEGORY_UNDEAD);
        mummyAttack.conferPercent = 8;
        SimpleAction mummyAttackConfers = new SimpleAction(zombi, SimpleAction.POLYMORPH, (byte)0, MUMMY_TYPE, false);
        mummyAttackConfers.name = "turned";
        mummyAttackConfers.filter = Entity.CATEGORY_NOT | Entity.CATEGORY_UNDEAD;
        mummyAttack.confers = mummyAttackConfers;
        mummy.actions.addElement(mummyAttack);
        putPrototype(mummy);

        Entity golem = new Entity();
        golem.entityTypeId = GOLEM_TYPE;
        golem.entityType = Entity.MONSTER_TYPE;
        golem.level = 3;
        golem.speed = 11;
        golem.frequency = 54;
        golem.baseAttack = 5;
        golem.baseArmourClass = 0;
        golem.health = 30;
        golem.name = "Golem";
        golem.flags = Entity.COLLISION_EFFECT_MONSTER;
        golem.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        golem.entityCategory = Entity.CATEGORY_MAGIC | Entity.CATEGORY_HUMANOID;
        AttackAction golemAttack = new AttackAction(golem, PUNCH_ACTION_NAME, PUNCH_ACTION_ACTION,
                (byte)5, (byte)1, (byte)1, (byte)2, (byte)0, Entity.CATEGORY_MAGIC);
        AttackAction golemGasAttack = new AttackAction(golem, BREATH_POISON_NAME, BREATH_POISON_ACTION,
                (byte)7, (byte)2, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_ELEMENTAL);
        golemGasAttack.creates = create(POISON_GAS_TYPE);
        golemGasAttack.conferPercent = 100;
        golem.actions.addElement(golemGasAttack);
        golem.actions.addElement(golemAttack);
        putPrototype(golem);

        Entity wizard = mage.copy();
        wizard.entityTypeId = WIZARD_TYPE;
        wizard.name = "Wizard";
        putPrototype(wizard);

        Entity priest = mage.copy();
        priest.entityTypeId = PRIEST_TYPE;
        priest.name = "Priest";
        putPrototype(priest);

        Entity man = new Entity();
        man.entityTypeId = MAN_TYPE;
        man.entityType = Entity.MONSTER_TYPE;
        man.level = 1;
        man.speed = 10;
        man.frequency = 50;
        man.baseAttack = 1;
        man.baseArmourClass = 8;
        man.health = 12;
        man.name = "Man";
        man.flags = Entity.COLLISION_EFFECT_MONSTER;
        man.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        man.entityCategory = Entity.CATEGORY_HUMANOID |  Entity.CATEGORY_INTELLIGENT;
        AttackAction manAttack = new AttackAction(man, CLUB_ACTION_NAME, CLUB_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)6, (byte)0, Entity.CATEGORY_NONE);
        man.actions.addElement(manAttack);
        putPrototype(man);

        Entity woman = man.copy();
        woman.baseAttack = 0;
        woman.frequency = 47;
        woman.entityTypeId = WOMAN_TYPE;
        woman.name = "Woman";
        putPrototype(woman);

        Entity redDragon = new Entity();
        redDragon.entityTypeId = RED_DRAGON_TYPE;
        redDragon.entityType = Entity.MONSTER_TYPE;
        redDragon.level = 20;
        redDragon.speed = 20;
        redDragon.frequency = 38;
        redDragon.baseAttack = 10;
        redDragon.baseArmourClass = -6;
        redDragon.health = 80;
        redDragon.name = "Red Dragon";
        redDragon.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        redDragon.flags = Entity.COLLISION_EFFECT_MONSTER;
        redDragon.entityCategory = Entity.CATEGORY_ELEMENTAL | Entity.CATEGORY_MAGIC | Entity.CATEGORY_FLYING;
        AttackAction redDragonAttack = new AttackAction(redDragon, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)5, (byte)1, (byte)2, (byte)8, (byte)0, Entity.CATEGORY_ELEMENTAL);
        redDragon.actions.addElement(redDragonAttack);
        redDragon.actions.addElement(new AttackAction(redDragon, CLAW_ACTION_NAME, CLAW_ACTION_ACTION,
                (byte)3, (byte)1, (byte)2, (byte)6, (byte)0, Entity.CATEGORY_NONE));
        AttackAction redDragonFlame = new AttackAction(redDragon, FLAME_ACTION_NAME, FLAME_ACTION_ACTION,
                (byte)13, (byte)5, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_ELEMENTAL);
        redDragonFlame.type = AttackAction.MAGIC;
        redDragonFlame.creates = create(BIG_SPOT_FIRE_TYPE);
        redDragon.actions.addElement(redDragonFlame);
        putPrototype(redDragon);

        Entity floatingEye = new Entity();
        floatingEye.entityTypeId = FLOATING_EYE_TYPE;
        floatingEye.entityType = Entity.MONSTER_TYPE;
        floatingEye.level = 2;
        floatingEye.speed = 8;
        floatingEye.frequency = 38;
        floatingEye.baseAttack = 0;
        floatingEye.baseArmourClass = 8;
        floatingEye.health = 10;
        floatingEye.name = "Floating Eye";
        floatingEye.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        floatingEye.flags = Entity.COLLISION_EFFECT_MONSTER;
        floatingEye.entityCategory = Entity.CATEGORY_MAGIC | Entity.CATEGORY_ELEMENTAL;
        AttackAction floatingEyeAttack = new AttackAction(floatingEye, GAZE_ACTION_NAME, GAZE_ACTION_ACTION,
                (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_NONE);
        floatingEyeAttack.confers = new SimpleAction(null, SimpleAction.DAZED, (byte)20, Entity.NONE_TYPE, false);
        floatingEyeAttack.conferPercent = 85;
        floatingEye.actions.addElement(floatingEyeAttack);
        putPrototype(floatingEye);

        Entity stormCloud = new Entity();
        stormCloud.entityTypeId = STORM_CLOUD_TYPE;
        stormCloud.entityType = Entity.MONSTER_TYPE;
        stormCloud.level = 4;
        stormCloud.speed = 20;
        stormCloud.frequency = 100;
        stormCloud.baseAttack = 0;
        stormCloud.baseArmourClass = 0;
        stormCloud.health = 20;
        stormCloud.name = "Storm Cloud";
        stormCloud.actions = new Vector(5);
        stormCloud.actions.addElement(new MoveAction((byte)2, null));
        stormCloud.actions.addElement(new SimpleAction(null, SimpleAction.ENDTURN, (byte)0, Entity.NONE_TYPE, false));
        stormCloud.actions.addElement(new SimpleAction(null, SimpleAction.DEFEND, (byte)3, Entity.NONE_TYPE, false));
        SimpleAction stormCloudDeath = new SimpleAction(null, SimpleAction.DIE, (byte)0, LIGHTNING_TYPE, false);
        stormCloudDeath.name = "disappears in a flash of lightning";
        stormCloud.actions.addElement(stormCloudDeath);
        stormCloud.flags = Entity.COLLISION_EFFECT_NONE;
        stormCloud.entityCategory = Entity.CATEGORY_ELEMENTAL | Entity.CATEGORY_FLYING;
        AttackAction stormCloudAttack = new AttackAction(stormCloud, GAS_ACTION_NAME, GAS_ACTION_ACTION,
                (byte)18, (byte)0, (byte)0, (byte)0, (byte)0, stormCloud.entityCategory);
        stormCloudAttack.creates = create(SMALL_LIGHTNING_TYPE);
        stormCloud.actions.addElement(stormCloudAttack);
        putPrototype(stormCloud);

        Entity ghost = new Entity();
        ghost.entityTypeId = GHOST_TYPE;
        ghost.entityType = Entity.MONSTER_TYPE;
        ghost.level = 5;
        ghost.speed = 10;
        ghost.frequency = 55;
        ghost.baseAttack = 0;
        ghost.baseArmourClass = 0;
        ghost.health = 30;
        ghost.name = "Ghost";
        ghost.actions = new Vector(6);
        ghost.flags = Entity.COLLISION_EFFECT_NONE;
        ghost.entityCategory = Entity.CATEGORY_UNDEAD | Entity.CATEGORY_HUMANOID | Entity.CATEGORY_FLYING;
        AttackAction ghostAttack = new AttackAction(ghost, CLAW_ACTION_NAME, CLAW_ACTION_ACTION,
                (byte)4, (byte)1, (byte)1, (byte)4, (byte)0, Entity.CATEGORY_UNDEAD);
        AttackAction ghostPossess = new AttackAction(ghost, POSSESS_ACTION_NAME, POSSESS_ACTION_ACTION,
                (byte)10, (byte)0, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_UNDEAD);
        ghostPossess.conferPercent = 50;
        SimpleAction ghostPossession = new SimpleAction(ghost, SimpleAction.CONVERT, (byte)0, Entity.NONE_TYPE, false);
        ghostPossession.name = "Possessed";
        ghostPossess.flags |= AttackAction.KILL_PARENT_BIT;
        ghostPossess.confers = ghostPossession;
        ghost.actions.addElement(ghostAttack);
        ghost.actions.addElement(ghostPossess);
        ghost.actions.addElement(new MoveAction((byte)2, null));
        ghost.actions.addElement(new SimpleAction(null, SimpleAction.ENDTURN, (byte)0, Entity.NONE_TYPE, false));
        ghost.actions.addElement(new SimpleAction(null, SimpleAction.DEFEND, (byte)3, Entity.NONE_TYPE, false));
        ghost.actions.addElement(new SimpleAction(null, SimpleAction.DIE, (byte)0, Entity.NONE_TYPE, false));
        putPrototype(ghost);

        Entity worm = new Entity();
        worm.entityTypeId = WORM_TYPE;
        worm.entityType = Entity.MONSTER_TYPE;
        worm.level = 12;
        worm.speed = 8;
        worm.frequency = 38;
        worm.baseAttack = 10;
        worm.baseArmourClass = -4;
        worm.health = 60;
        worm.name = "Worm";
        worm.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        worm.flags = Entity.COLLISION_EFFECT_MONSTER;
        worm.entityCategory = Entity.CATEGORY_ELEMENTAL;
        AttackAction wormAttack = new AttackAction(worm, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)6, (byte)1, (byte)1, (byte)12, (byte)2, Entity.CATEGORY_ELEMENTAL);
        SimpleAction wormAttackConfers = new SimpleAction(worm, SimpleAction.SWALLOW, (byte)0, Entity.NONE_TYPE, false);
        wormAttackConfers.name = "Eaten";
        wormAttack.conferPercent = 20;
        wormAttack.confers = wormAttackConfers;
        worm.actions.addElement(wormAttack);
        putPrototype(worm);

        Entity beholder = new Entity();
        beholder.entityTypeId = BEHOLDER_TYPE;
        beholder.entityType = Entity.MONSTER_TYPE;
        beholder.level = 11;
        beholder.speed = 10;
        beholder.frequency = 60;
        beholder.baseAttack = 0;
        beholder.baseArmourClass = 2;
        beholder.health = 25;
        beholder.name = "Beholder";
        beholder.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        beholder.flags = Entity.COLLISION_EFFECT_MONSTER;
        beholder.entityCategory = Entity.CATEGORY_MAGIC | Entity.CATEGORY_FLYING | Entity.CATEGORY_INTELLIGENT;
        AttackAction beholderSleepAttack = new AttackAction(beholder, SLEEP_STARE_ACTION_NAME,
                SLEEP_STARE_ACTION_ACTION, (byte)3, (byte)4, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_MAGIC);
        SimpleAction beholderSleepConfers = new SimpleAction(null, SimpleAction.DAZED, (byte)20, Entity.NONE_TYPE, false);
        beholderSleepConfers.action = "The {0} snores loudly";
        beholderSleepAttack.confers = beholderSleepConfers;
        beholderSleepAttack.conferPercent = 70;
        beholderSleepAttack.type = AttackAction.MAGIC;
        beholder.actions.addElement(beholderSleepAttack);
        AttackAction beholderDeathAttack = new AttackAction(beholder, DEATH_STARE_ACTION_NAME,
                DEATH_STARE_ACTION_ACTION, (byte)10, (byte)2, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_MAGIC);
        beholderDeathAttack.type = AttackAction.MAGIC;
        beholderDeathAttack.conferPercent = 30;
        SimpleAction beholderDeathAttackConfers = new SimpleAction(null, SimpleAction.DIE, (byte)0, Entity.NONE_TYPE, false);
        beholderDeathAttackConfers.name = "dead";
        beholderDeathAttackConfers.action = "The {0}s heart stops";
        beholderDeathAttackConfers.filter = Entity.CATEGORY_NOT | Entity.CATEGORY_UNDEAD;
        beholderDeathAttack.confers = beholderDeathAttackConfers;
        beholder.actions.addElement(beholderDeathAttack);
        AttackAction beholderWoundAttack = new AttackAction(beholder, WOUND_STARE_ACTION_NAME,
                WOUND_STARE_ACTION_ACTION, (byte)6, (byte)5, (byte)1, (byte)20, (byte)0, Entity.CATEGORY_MAGIC);
        beholderWoundAttack.type = AttackAction.MAGIC;
        beholder.actions.addElement(beholderWoundAttack);
        AttackAction beholderStoneAttack = new AttackAction(beholder, STONE_STARE_ACTION_NAME,
                STONE_STARE_ACTION_ACTION, (byte)8, (byte)3, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_MAGIC);
        beholderStoneAttack.type = AttackAction.MAGIC;
        beholderStoneAttack.conferPercent = 60;
        SimpleAction beholderStoneConfers = new SimpleAction(beholder, SimpleAction.STONE, (byte)0, Entity.NONE_TYPE, false);
        beholderStoneAttack.confers = beholderStoneConfers;
        beholder.actions.addElement(beholderStoneAttack);
        AttackAction beholderHypnotizeAttack = new AttackAction(beholder, HYPNOTIC_STARE_ACTION_NAME,
                HYPNOTIC_STARE_ACTION_ACTION, (byte)8, (byte)3, (byte)0, (byte)0, (byte)0, Entity.CATEGORY_MAGIC);
        beholderHypnotizeAttack.conferPercent = 50;
        SimpleAction beholderHypnotizeConfers = new SimpleAction(beholder, SimpleAction.CONVERT, (byte)0, Entity.NONE_TYPE, false);
        beholderHypnotizeConfers.name = "hypnotized";
        beholderHypnotizeConfers.action = "The {0} falls under the beholders power";
        beholderHypnotizeAttack.type = AttackAction.MAGIC;
        beholderHypnotizeAttack.confers = beholderHypnotizeConfers;
        beholder.actions.addElement(beholderHypnotizeAttack);
        AttackAction beholderAttack = new AttackAction(beholder, BITE_ACTION_NAME, BITE_ACTION_ACTION,
                (byte)4, (byte)1, (byte)2, (byte)4, (byte)2, Entity.CATEGORY_NONE);
        beholder.actions.addElement(beholderAttack);
        putPrototype(beholder);

        Entity manticore = new Entity();
        manticore.entityTypeId = MANTICORE_TYPE;
        manticore.entityType = Entity.MONSTER_TYPE;
        manticore.flags = Entity.COLLISION_EFFECT_MONSTER;
        manticore.level = 8;
        manticore.speed = 15;
        manticore.frequency = 40;
        manticore.baseAttack = 0;
        manticore.baseArmourClass = 0;
        manticore.health = 30;
        manticore.entityCategory = Entity.CATEGORY_INTELLIGENT | Entity.CATEGORY_FLYING;
        manticore.name = "Manticore";
        manticore.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        AttackAction manticoreShoot = new AttackAction(manticore, SHOOT_ACTION_NAME,
                "shoots spikes at", (byte)6, (byte)4, (byte)3, (byte)4, (byte)0, Entity.CATEGORY_NONE);
        manticoreShoot.type = AttackAction.ARROW;
        manticoreShoot.confers = ActionFactory.INSTANCE.createDazed(null, 10);
        manticoreShoot.conferPercent = 25;
        manticore.actions.addElement(manticoreShoot);
        AttackAction manticoreBite = new AttackAction(manticore, BITE_ACTION_NAME,
                BITE_ACTION_ACTION, (byte)4, (byte)1, (byte)2, (byte)4, (byte)2, Entity.CATEGORY_NONE);
        manticore.actions.addElement(manticoreBite);
        putPrototype(manticore);

        Entity hydra = new Entity();
        hydra.entityTypeId = HYDRA_TYPE;
        hydra.entityType = Entity.MONSTER_TYPE;
        hydra.flags = Entity.COLLISION_EFFECT_MONSTER;
        hydra.level = 9;
        hydra.speed = 10;
        hydra.frequency = 50;
        hydra.baseAttack = 0;
        hydra.baseArmourClass = -2;
        hydra.health = 80;
        hydra.entityCategory = Entity.CATEGORY_ELEMENTAL;
        hydra.name = "Hydra";
        hydra.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        AttackAction hydraBite = new AttackAction(hydra, BITE_ACTION_NAME, BITE_ACTION_ACTION, (byte)2, (byte)1, (byte)1, (byte)6, (byte)1, Entity.CATEGORY_NONE);
        // the hydra can attack heaps
        hydraBite.flags &= ~AttackAction.END_TURN_BIT;
        hydra.actions.addElement(hydraBite);
        putPrototype(hydra);

        Entity windElemental = new Entity();
        windElemental.entityTypeId = WIND_ELEMENTAL_TYPE;
        windElemental.entityType = Entity.MONSTER_TYPE;
        windElemental.level = 6;
        windElemental.speed = 15;
        windElemental.frequency = 50;
        windElemental.baseAttack = 3;
        windElemental.baseArmourClass = -3;
        windElemental.health = 20;
        windElemental.name = "Wind Elemental";
        windElemental.flags = Entity.COLLISION_EFFECT_NONE;
        windElemental.entityCategory = Entity.CATEGORY_ELEMENTAL | Entity.CATEGORY_FLYING;
        AttackAction windElementalAttack = new AttackAction(windElemental, GAS_ACTION_NAME, GAS_ACTION_ACTION,
                (byte)1, (byte)0, (byte)1, (byte)6, (byte)1, windElemental.entityCategory);
        windElementalAttack.conferPercent = 40;
        windElementalAttack.confers = new SimpleAction(windElemental, SimpleAction.DAZED, (byte)15, Entity.NONE_TYPE, false);
        windElemental.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        windElemental.actions.addElement(windElementalAttack);
        putPrototype(windElemental);

        Entity fireElemental = new Entity();
        fireElemental.entityTypeId = FIRE_ELEMENTAL_TYPE;
        fireElemental.entityType = Entity.MONSTER_TYPE;
        fireElemental.level = 6;
        fireElemental.speed = 10;
        fireElemental.frequency = 50;
        fireElemental.baseAttack = 4;
        fireElemental.baseArmourClass = 4;
        fireElemental.health = 40;
        fireElemental.name = "Fire Elemental";
        fireElemental.flags = Entity.COLLISION_EFFECT_MONSTER;
        fireElemental.entityCategory = Entity.CATEGORY_ELEMENTAL;
        AttackAction fireElementalAttack = new AttackAction(fireElemental, FLAME_ACTION_NAME, FLAME_ACTION_ACTION,
                (byte)5, (byte)1, (byte)1, (byte)6, (byte)0, fireElemental.entityCategory);
        fireElementalAttack.conferPercent = 40;
        fireElementalAttack.creates = create(SPOT_FIRE_TYPE);
        fireElemental.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        fireElemental.actions.addElement(fireElementalAttack);
        putPrototype(fireElemental);

        Entity waterElemental = new Entity();
        waterElemental.entityTypeId = WATER_ELEMENTAL_TYPE;
        waterElemental.entityType = Entity.MONSTER_TYPE;
        waterElemental.level = 6;
        waterElemental.speed = 12;
        waterElemental.frequency = 50;
        waterElemental.baseAttack = 4;
        waterElemental.baseArmourClass = 5;
        waterElemental.health = 30;
        waterElemental.name = "Water Elemental";
        waterElemental.flags = Entity.COLLISION_EFFECT_NONE;
        waterElemental.entityCategory = Entity.CATEGORY_ELEMENTAL;
        waterElemental.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        AttackAction waterElementalAttack = new AttackAction(waterElemental, SPLASH_ACTION_NAME, SPLASH_ACTION_ACTION,
                (byte)3, (byte)2, (byte)1, (byte)3, (byte)1, waterElemental.entityCategory);
        waterElemental.actions.addElement(waterElementalAttack);
        AttackAction waterElementalDrown = new AttackAction(waterElemental, DROWN_ACTION_NAME, DROWN_ACTION_ACTION,
                (byte)10, (byte)0, (byte)1, (byte)10, (byte)0, waterElemental.entityCategory);
        waterElementalDrown.conferPercent = 8;
        SimpleAction waterElementalDrownConfer = new SimpleAction(waterElemental, SimpleAction.DIE, (byte)0, Entity.NONE_TYPE, false);
        waterElementalDrownConfer.name = "drowned";
        waterElementalDrown.confers = waterElementalDrownConfer;
        waterElemental.actions.addElement(waterElementalDrown);
        putPrototype(waterElemental);

        Entity earthElemental = new Entity();
        earthElemental.entityTypeId = EARTH_ELEMENTAL_TYPE;
        earthElemental.entityType = Entity.MONSTER_TYPE;
        earthElemental.level = 6;
        earthElemental.speed = 10;
        earthElemental.frequency = 52;
        earthElemental.baseAttack = 10;
        earthElemental.baseArmourClass = 10;
        earthElemental.health = 80;
        earthElemental.name = "Earth Elemental";
        earthElemental.flags = Entity.COLLISION_EFFECT_MONSTER;
        earthElemental.entityCategory = Entity.CATEGORY_ELEMENTAL;
        earthElemental.actions = copyVector(DEFAULT_MONSTER_ACTIONS);
        AttackAction earthElementalAttack = new AttackAction(earthElemental, CLUB_ACTION_NAME, CLUB_ACTION_ACTION,
                (byte)6, (byte)1, (byte)1, (byte)6, (byte)0, earthElemental.entityCategory);
        earthElemental.actions.addElement(earthElementalAttack);
        putPrototype(earthElemental);

        Entity ringOfProtection = new Entity();
        ringOfProtection.entityTypeId = RING_OF_PROTECTION_TYPE;
        ringOfProtection.entityType = Entity.ITEM_TYPE;
        ringOfProtection.flags = Entity.COLLISION_EFFECT_NONE;
        ringOfProtection.name = "Ring of Protection";
        ringOfProtection.entityCategory = Entity.CATEGORY_MAGIC;
        ringOfProtection.baseArmourClass = 3;
        Vector ringOfProtectionActions = new Vector(1);
        ringOfProtectionActions.addElement(new SimpleAction(null, SimpleAction.PUTDOWN, (byte)2, RING_OF_PROTECTION_TYPE, false));
        ringOfProtection.actions = ringOfProtectionActions;
        putPrototype(ringOfProtection);

        Entity fireSword = new Entity();
        fireSword.entityTypeId = FIRE_SWORD_TYPE;
        fireSword.entityType = Entity.ITEM_TYPE;
        fireSword.flags = Entity.COLLISION_EFFECT_NONE;
        fireSword.name = "Fire Sword";
        fireSword.entityCategory = Entity.CATEGORY_ELEMENTAL | Entity.CATEGORY_MAGIC;
        Vector fireSwordActions = new Vector(2);
        AttackAction fireSwordAttack = new AttackAction(null, SWORD_ACTION_NAME, SWORD_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)8, (byte)1, fireSword.entityCategory);
        fireSwordAttack.prerequsiteCategory = Entity.CATEGORY_HUMANOID;
        fireSwordAttack.creates = create(SPOT_FIRE_TYPE);
        fireSwordAttack.itemName = fireSword.name;
        fireSwordActions.addElement(fireSwordAttack);
        fireSwordActions.addElement(new SimpleAction(null, SimpleAction.PUTDOWN, (byte)2, FIRE_SWORD_TYPE, false));
        fireSword.actions = fireSwordActions;
        putPrototype(fireSword);

        Entity iceSword = new Entity();
        iceSword.entityTypeId = ICE_SWORD_TYPE;
        iceSword.entityType = Entity.ITEM_TYPE;
        iceSword.flags = Entity.COLLISION_EFFECT_NONE;
        iceSword.name = "Ice Sword";
        iceSword.entityCategory = Entity.CATEGORY_ELEMENTAL | Entity.CATEGORY_MAGIC;
        Vector iceSwordActions = new Vector(2);
        AttackAction iceSwordAttack = new AttackAction(null, SWORD_ACTION_NAME, SWORD_ACTION_ACTION,
                (byte)3, (byte)1, (byte)1, (byte)8, (byte)3, iceSword.entityCategory);
        iceSwordAttack.prerequsiteCategory = Entity.CATEGORY_HUMANOID;
        iceSwordAttack.itemName = iceSword.name;
        iceSwordActions.addElement(iceSwordAttack);
        iceSwordActions.addElement(new SimpleAction(null, SimpleAction.PUTDOWN, (byte)2, ICE_SWORD_TYPE, false));
        iceSword.actions = iceSwordActions;
        putPrototype(iceSword);

        Entity darkBow = new Entity();
        darkBow.entityTypeId = DARK_BOW_TYPE;
        darkBow.entityType = Entity.ITEM_TYPE;
        darkBow.flags = Entity.COLLISION_EFFECT_NONE;
        darkBow.name = "Dark Bow";
        Vector darkBowActions = new Vector(2);
        AttackAction darkBowAttack = new AttackAction(null, SHOOT_ACTION_NAME, SHOOT_ACTION_ACTION,
                (byte)2, (byte)5, (byte)1, (byte)6, (byte)1, darkBow.entityCategory);
        darkBowAttack.type = AttackAction.ARROW;
        darkBowAttack.prerequsiteCategory = Entity.CATEGORY_HUMANOID;
        darkBowAttack.itemName = darkBow.name;
        darkBowActions.addElement(darkBowAttack);
        darkBowActions.addElement(new SimpleAction(null, SimpleAction.PUTDOWN, (byte)2, DARK_BOW_TYPE, false));
        darkBow.actions = darkBowActions;
        putPrototype(darkBow);

        Entity ladderUp = new Entity();
        ladderUp.entityTypeId = LADDER_UP_TYPE;
        ladderUp.entityType = Entity.INHERIT_TYPE;
        ladderUp.flags = Entity.COLLISION_EFFECT_NONE;
        ladderUp.name = "Ladder";
        putPrototype(ladderUp);

        Entity ladderDown = new Entity();
        ladderDown.entityTypeId = LADDER_DOWN_TYPE;
        ladderDown.entityType = Entity.INHERIT_TYPE;
        ladderDown.flags = Entity.COLLISION_EFFECT_NONE;
        ladderDown.name = "Trapdoor";
        putPrototype(ladderDown);
    }

    public static final Vector copyVector(Vector toCopy)
    {
        Vector copied = new Vector(toCopy.size());
        for(int i=toCopy.size(); i>0; )
        {
            i--;
            copied.addElement(toCopy.elementAt(i));
        }
        return copied;
    }

    public static final Entity create(byte type)
    {
        Entity found = getPrototype(type);
        Entity result;
        if(found != null)
        {
            result = found.copy();
        }else{
            result = null;
        }
        return result;
    }

    public static final Entity getPrototype(byte type)
    {
        Byte b = new Byte(type);
        Entity found = (Entity)ENTITIES.get(b);
        return found;
    }

    public static final void putPrototype(Entity e)
    {
        Byte b = new Byte(e.entityTypeId);
        if(ENTITIES.containsKey(b))
        {
            Entity found = (Entity)ENTITIES.get(b);
            throw new RuntimeException("error inserting "+e.name+" the key "+b+" is already occupied by "+found.name);
        }
        ENTITIES.put(b, e);
    }
}
