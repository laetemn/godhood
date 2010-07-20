package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 5:26:32 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractRandomRoomFactory implements RoomFactory
{

    public static final int DEFAULT_WIDTH = 25;
    public static final int DEFAULT_HEIGHT = 25;

    private static final Random RANDOM = new Random();

    public Room getRoom(String name) throws RoomCreationException
    {
        int commaIndex = name.indexOf(',');
        int endIndex = name.indexOf(',', commaIndex);
        String subname;
        if(endIndex < 0)
        {
            endIndex = name.length();
            subname = name;
        }else{
            subname = name.substring(endIndex+1);
        }
        int width;
        int height;
        if(commaIndex < 0)
        {
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
        }else{
            try
            {
                width = Integer.parseInt(name.substring(0, commaIndex));
                height = Integer.parseInt(name.substring(commaIndex+1, endIndex));
            }catch(Exception ex){
                ex.printStackTrace();
                width = DEFAULT_WIDTH;
                height = DEFAULT_HEIGHT;
            }
        }
        // TODO : get the seed?
        return getRoom(subname, width, height, RANDOM);
    }

    public void writeRoom(String name, OutputStream outs) throws RoomCreationException
    {
        Room room = getRoom(name);
        try
        {
            DataOutputStream dos = new DataOutputStream(outs);
            Room.store(room, dos);
        }catch(Exception ex){
            throw new RoomCreationException(name, "error writing output", ex);
        }
    }

    public abstract Room getRoom(String name, int width, int height, Random random)
            throws RoomCreationException;
}
