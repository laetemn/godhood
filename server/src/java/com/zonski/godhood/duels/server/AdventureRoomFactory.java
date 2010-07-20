package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.action.SimpleAction;

import java.io.OutputStream;
import java.io.File;
import java.io.DataOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 28, 2004
 * Time: 11:49:24 AM
 * To change this template use Options | File Templates.
 */
public class AdventureRoomFactory implements RoomFactory
{
    private LevelMoveRoomFactoryProxy roomFactory;
    private RoomStore levelStore;

    public AdventureRoomFactory(String uid)
    {
        this.levelStore = new FileRoomStore(new File("c:/temp/store/"+uid), true);
        ExitAddingRoomFactoryProxy exitAdder = new ExitAddingRoomFactoryProxy(new RandomSlumFactory());
        exitAdder.addExit("Slum1", "Slum1-Training-A",
                EntityFactory.LADDER_UP_TYPE, ActionFactory.EXIT_UP_NAME);
        exitAdder.addExit("Slum1", "Slum1-Slum2-A",
                EntityFactory.LADDER_DOWN_TYPE, ActionFactory.EXIT_DOWN_NAME);
        exitAdder.addExit("Slum2", "Slum2-Slum1-A",
                EntityFactory.LADDER_UP_TYPE, ActionFactory.EXIT_UP_NAME);
        exitAdder.addExit("Slum2", "Slum2-Slum3-A",
                EntityFactory.LADDER_DOWN_TYPE, ActionFactory.EXIT_DOWN_NAME);
        exitAdder.addExit("Slum3", "Slum3-Slum2-A",
                EntityFactory.LADDER_UP_TYPE, ActionFactory.EXIT_UP_NAME);
        exitAdder.addExit("Slum3", "Slum3-Slum4-A",
                EntityFactory.LADDER_DOWN_TYPE, ActionFactory.EXIT_DOWN_NAME);
        exitAdder.addExit("Slum4", "Slum4-Slum3-A",
                EntityFactory.LADDER_UP_TYPE, ActionFactory.EXIT_UP_NAME);

        MonsterRoomFactoryProxy monsterAdder = new MonsterRoomFactoryProxy(exitAdder);
        Entity goblin = EntityFactory.create(EntityFactory.GOBLIN_TYPE);
        goblin.side = 2;
        Entity floatingEye = EntityFactory.create(EntityFactory.FLOATING_EYE_TYPE);
        floatingEye.side = 2;
        Entity dog = EntityFactory.create(EntityFactory.DOG_TYPE);
        dog.side = 2;
        Entity knight = EntityFactory.create(EntityFactory.KNIGHT_TYPE);
        knight.side = 3;
        Entity redDragon = EntityFactory.create(EntityFactory.RED_DRAGON_TYPE);
        redDragon.side = 2;
        Entity cat = EntityFactory.create(EntityFactory.CAT_TYPE);
        cat.side = 2;
        Entity demon = EntityFactory.create(EntityFactory.DEMON_TYPE);
        demon.side = 2;

        //monsterAdder.setRoomInfestation("Slum1", MonsterRoomFactoryProxy.INFESTATION_MINIMAL);
        //monsterAdder.addMonsterDescription("Slum1", goblin, 30);
        //monsterAdder.addMonsterDescription("Slum1", floatingEye, 10);
        //monsterAdder.addMonsterDescription("Slum1", dog, 20);
        //monsterAdder.addMonsterDescription("Slum1", cat, 10);

        monsterAdder.setRoomInfestation("Slum2", MonsterRoomFactoryProxy.INFESTATION_MINIMAL);
        monsterAdder.addMonsterDescription("Slum2", goblin, 30);
        monsterAdder.addMonsterDescription("Slum2", knight, 10);
        monsterAdder.addMonsterDescription("Slum2", dog, 20);

        monsterAdder.setRoomInfestation("Slum3", MonsterRoomFactoryProxy.INFESTATION_MINIMAL);
        monsterAdder.addMonsterDescription("Slum3", goblin, 10);
        monsterAdder.addMonsterDescription("Slum3", floatingEye, 10);
        monsterAdder.addMonsterDescription("Slum3", knight, 20);
        monsterAdder.addMonsterDescription("Slum3", demon, 20);

        monsterAdder.setRoomInfestation("Slum1", MonsterRoomFactoryProxy.INFESTATION_SOLITARY);
        monsterAdder.addMonsterDescription("Slum1", redDragon, 100);

        PersistentRoomFactory persistentFactory = new PersistentRoomFactory(this.levelStore, monsterAdder);
        this.roomFactory = new LevelMoveRoomFactoryProxy(persistentFactory);
    }


    public Room getRoom(String name) throws RoomCreationException
    {
        // obtains a fresh room on level 1
        Room room = this.roomFactory.getRoom(name);
        return room;
    }

    public Room getRoom(Room previous, String exitName)
        throws RoomCreationException
    {
        // find out the room being exited
        int hyphen1Index = exitName.indexOf("-");
        int hyphen2Index = exitName.indexOf("-", hyphen1Index+1);
        String roomExited = exitName.substring(0, hyphen1Index);
        // find out the room being entered
        String roomEntered = exitName.substring(hyphen1Index+1, hyphen2Index);
        // found out the identifier of the exit
        String exitIdentifier = exitName.substring(hyphen2Index+1);

        return getRoom(previous, roomExited, roomEntered, exitIdentifier);
    }

    public Room getRoom(Room previous, String previousRoomName, String nextRoomName, String exitIdentifier)
        throws RoomCreationException
    {
        // store the previous room
        Room room;
        // create the identifier of the entrance (it's the reverse)
        String entranceName = nextRoomName+"-"+previousRoomName+"-"+exitIdentifier;
        String exitName = previousRoomName+"-"+nextRoomName+"-"+exitIdentifier;
        room = this.roomFactory.getRoom(nextRoomName, previous, exitName, entranceName);
        try
        {
            this.levelStore.storeRoom(previousRoomName, previous);
        }catch(Exception ex){
            throw new RoomCreationException(nextRoomName,
                    "couldn't store room "+previousRoomName+" while changing to "+nextRoomName, ex);
        }
        return room;
    }

    public void writeRoom(String name, OutputStream outs) throws RoomCreationException
    {
        Room room = getRoom(name);
        try
        {
            DataOutputStream dos = new DataOutputStream(outs);
            Room.store(room, dos);
        }catch(Exception ex){
            throw new RoomCreationException(name, "couldn't write room to stream", ex);
        }
    }
}
