package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 10:58:46 AM
 * To change this template use Options | File Templates.
 */
public class FileRoomFactory implements RoomFactory
{
    public static final String DEFAULT_EXTENSION = "room";

    protected File directory;
    protected String extension = DEFAULT_EXTENSION;

    public FileRoomFactory(File directory)
    {
        this(directory, DEFAULT_EXTENSION);
    }

    public FileRoomFactory(File directory, String extension)
    {
        this.directory = directory;
        if(!directory.exists())
        {
            directory.mkdirs();
        }
        this.extension = extension;
    }

    public Room getRoom(String name)
        throws RoomCreationException
    {
        Room room;
        File file = getFile(name);
        if(file.exists())
        {
            DataInputStream ins = null;
            try
            {
                ins = new DataInputStream(new FileInputStream(file));
                room = createRoom(ins, name);
            }catch(IOException ex){
                throw new RoomCreationException(name, "error reading file "+file.getPath(), ex);
            }finally{
                if(ins != null)
                {
                    try
                    {
                        ins.close();
                    }catch(IOException ex){
                        // we don't really care
                        System.err.println("error closing file "+file.getPath());
                        ex.printStackTrace();
                    }
                }
            }
        }else{
            room = null;
        }
        return room;
    }

    protected Room createRoom(DataInputStream ins, String name)
        throws RoomCreationException
    {
        Room room;
        try
        {
            room = Room.create(null, ins);
        }catch(Exception ex){
            throw new RoomCreationException(name, "Error loading room", ex);
        }finally{
            if(ins != null)
            {
                try
                {
                    ins.close();
                }catch(Exception ex){
                    // oh well, what are you going to do?
                    System.err.println("couldn't close file input stream for room "+name);
                    ex.printStackTrace();
                }
            }
        }
        return room;
    }

    public void writeRoom(String name, OutputStream outs) throws RoomCreationException
    {
        InputStream ins = getInputStream(name);
        try
        {
            int b;

            while((b = ins.read()) != -1)
            {
                outs.write(b);
            }
        }catch(Exception ex){
            throw new RoomCreationException(name, "error copying room file", ex);
        }finally{
            try
            {
                ins.close();
            }catch(Exception ex){
                // oh well, what are you going to do?
                System.err.println("couldn't close file input stream for room "+name);
                ex.printStackTrace();
            }
        }
    }

    private InputStream getInputStream(String name)
        throws RoomCreationException
    {
        File file = new File(this.directory, name+"."+this.extension);
        if(!file.exists())
        {
            throw new RoomCreationException(name, "the file "+file.getPath()+" does not exist");
        }else{
            InputStream ins = null;
            try
            {
                ins = new FileInputStream(file);
            }catch(Exception ex){
                throw new RoomCreationException(name, "error reading file "+file.getPath(), ex);
            }
            return ins;
        }
    }

    protected final File getFile(String roomName)
    {
        return new File(this.directory, roomName+"."+this.extension);
    }
}
