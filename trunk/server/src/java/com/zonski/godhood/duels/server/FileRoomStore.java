package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 28, 2004
 * Time: 11:01:57 AM
 * To change this template use Options | File Templates.
 */
public class FileRoomStore extends FileRoomFactory implements RoomStore
{
    private boolean allowStore = true;

    public FileRoomStore(File directory, String extension, boolean allowStore)
    {
        super(directory, extension);
        this.allowStore = allowStore;
    }

    public FileRoomStore(File directory, boolean allowStore)
    {
        super(directory);
        this.allowStore = allowStore;
    }

    public void storeRoom(String name, Room store)
        throws IOException
    {
        if(this.allowStore)
        {
            File file = getFile(name);
            DataOutputStream fos = new DataOutputStream(new FileOutputStream(file));
            try
            {
                storeRoom(fos, store);
            }finally{
                fos.close();
            }
        }else{
            throw new IOException("trying to write room "+name+" to non-writable store at "+this.directory.getName());
        }
    }

    public void storeRoom(DataOutputStream outs, Room room)
        throws IOException
    {
        Room.store(room, outs);
    }

    public boolean isStored(String name)
    {
        File file = getFile(name);
        return file.exists();
    }
    /*
    public Room getRoom(String name) throws RoomCreationException
    {
        Room room;
        File file = getFile(name);
        if(file.exists())
        {
            FileInputStream fis;
            try
            {
                fis = new FileInputStream(file);
            }catch(IOException ex){
                throw new RoomCreationException(name, "Unable to open file "+file.getName(), ex);
            }
            try
            {
                room = Room.create(null, fis);
            }catch(Exception ex){
                throw new RoomCreationException(name, "Error reading room file "+file.getName(), ex);
            }finally{
                try
                {
                    fis.close();
                }catch(IOException ex){
                    // just handle it quietly
                    System.err.println("error closing file "+file.getName());
                    ex.printStackTrace(System.err);
                }
            }

        }else{
            room = null;
        }
        return room;
    }
    */
    /*
    public void writeRoom(String name, OutputStream outs) throws RoomCreationException
    {
        // simply write out the input stream to the output stream
        File file = getFile(name);
        FileInputStream fis;
        try
        {
            fis = new FileInputStream(file);
        }catch(IOException ex){
            throw new RoomCreationException(name, "Unable to open file "+file.getName(), ex);
        }
        try
        {
            int b = fis.read();
            while(b >= 0)
            {
                outs.write(b);
                b = fis.read();
            }
        }catch(Exception ex){
            throw new RoomCreationException(name, "Error reading room file "+file.getName(), ex);
        }finally{
            try
            {
                fis.close();
            }catch(IOException ex){
                // just handle it quietly
                System.err.println("error closing file "+file.getName());
                ex.printStackTrace(System.err);
            }
        }
    }
    */
}
