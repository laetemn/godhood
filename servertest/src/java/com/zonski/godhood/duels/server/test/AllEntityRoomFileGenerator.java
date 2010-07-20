package com.zonski.godhood.duels.server.test;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.action.SimpleAction;
import com.zonski.godhood.duels.server.EntityFactory;
import com.zonski.godhood.duels.server.XmlFileRoomStore;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 9/03/2004
 * Time: 17:25:34
 * To change this template use Options | File Templates.
 * <p>
 * This class produces a room file that contains every entity in the game then reads it in
 */
public class AllEntityRoomFileGenerator
{
    public static final void main(String[] args)
    {

        try {
            Room room = generateRoom();
            String filename = "all.room.xml";
            FileOutputStream fos = new FileOutputStream(filename);
            XmlFileRoomStore roomStore = new XmlFileRoomStore(new File("."), false);
            DataOutputStream dos = new DataOutputStream(fos);
            roomStore.storeRoom(dos, room);
            dos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final Room generateRoom()
    {
        Room room = new Room(null, (byte)1, (byte)1);
        Enumeration keys = EntityFactory.ENTITIES.keys();
        while(keys.hasMoreElements())
        {
            Byte key = (Byte)keys.nextElement();
            Entity entity = EntityFactory.create(key.byteValue());
            entity.x = 0;
            entity.y = 0;
            entity.side = 1;
            room.addContainedEntity(entity);
        }
        return room;
    }

    public AllEntityRoomFileGenerator()
    {

    }
}
