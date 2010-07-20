package com.zonski.godhood.duels.tools;

import com.zonski.godhood.duels.server.*;
import com.zonski.godhood.duels.game.Room;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 11/03/2004
 * Time: 10:29:34
 * To change this template use Options | File Templates.
 */
public class FileFormatConverter
{
    public static final String DIRECTORY =      "-d";
    public static final String TARGET =         "-t";
    public static final String SOURCE =         "-s";
    public static final String ROOM =           null;

    public static final String BINARY =         "binary";
    public static final String ASCII =          "ascii";
    public static final String XML =            "xml";


    public static int main(String[] args)
    {
        // load in the properties
        HashMap properties = ToolUtil.getArguments(args);
        String roomname = (String)properties.get(null);
        String directory = (String)properties.get(DIRECTORY);
        String targetType = (String)properties.get(TARGET);
        String sourceType = (String)properties.get(SOURCE);
        if(directory == null)
        {
            directory = ".";
        }
        if(targetType == null)
        {
            targetType = BINARY;
        }
        if(sourceType == null)
        {
            sourceType = XML;
        }
        if(roomname == null)
        {
            printUsage("no room name specified");
            return 2;
        }else{
            try
            {
                convert(sourceType, targetType, directory, roomname);
                return 0;
            }catch(Exception ex){
                ex.printStackTrace();
                return 1;
            }
        }
    }

    public static void convert(String sourceType, String targetType, String directory, String roomname)
        throws IOException, RoomCreationException
    {
        RoomStore source = getRoomStore(sourceType, directory);
        RoomStore target = getRoomStore(targetType, directory);
        Room room = source.getRoom(roomname);
        if(room == null)
        {
            throw new RoomCreationException(roomname, "the room doens't exist");
        }
        target.storeRoom(roomname, room);
    }

    private static final void printUsage(String message)
    {
        System.err.println(message);
        System.err.println("usage : java "+(FileFormatConverter.class.getName())+" [-d directory] [-s binary|ascii|xml] [-t binary|ascii|xml] roomname");
        System.err.println("-s = source type, defaults to ascii");
        System.err.println("-t = target type, defaults to binary");
        System.err.println("the source and target types define the extension used on the generated and read room files");
        System.err.println("ascii  = roomname.room.txt");
        System.err.println("binary = roomname.room");
        System.err.println("xml    = roomname.room.xml");
        System.err.println("ascii files are not implemented at this point in time");
    }

    private static final RoomStore getRoomStore(String type, String directory)
    {
        RoomStore roomStore;
        if(type.equals(ASCII))
        {
            //roomStore = new TextFileRoomStore(new File(directory), true);
            throw new RuntimeException("ASCII files are no longer supported");
        }else if(type.equals(BINARY)){
            roomStore = new FileRoomStore(new File(directory), true);
        }else if(type.equals(XML)){
            roomStore = new XmlFileRoomStore(new File(directory), true);
        }else{
            throw new RuntimeException("unsupported type "+type);
        }
        return roomStore;
    }
}
