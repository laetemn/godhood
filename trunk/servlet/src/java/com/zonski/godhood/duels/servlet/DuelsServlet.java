package com.zonski.godhood.duels.servlet;

import com.zonski.godhood.duels.server.*;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.action.SimpleAction;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 11:52:52 AM
 * To change this template use Options | File Templates.
 */
public class DuelsServlet extends HttpServlet
{
    private RoomFactory defaultRoomFactory;

    private static final String LEVEL_PATH = "Path";

    /**
     * Load request type, loads a specific level
     */
    public static final String LOAD = "L";

    /**
     * Enter request type, enters a level and leaves another
     */
    public static final String ENTER = "E";

    /**
     * Create request type, creates a new session for this player
     */
    public static final String NEW = "N";

    private static final String ROOM_FACTORY = "RoomFactory";
    private static int NEXT_PLAYER_ID = 1;


    public DuelsServlet()
    {
        super();
    }

    public void init()
    {
        ServletConfig config = this.getServletConfig();
        ExitAddingRoomFactoryProxy proxy = new ExitAddingRoomFactoryProxy(new RandomSlumFactory());
        proxy.addExit("Slum1", "Slum1-Slum2-A", Entity.LADDER_DOWN_TYPE, SimpleAction.EXIT_DOWN_NAME);
        proxy.addExit("Slum2", "Slum2-Slum1-A", Entity.LADDER_UP_TYPE, SimpleAction.EXIT_UP_NAME);
        proxy.addExit("Slum1", "Slum1-Training-A", Entity.LADDER_UP_TYPE, SimpleAction.EXIT_UP_NAME);
        this.defaultRoomFactory = proxy;
    }


    public void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException
    {
        String roomName;
        long playerId;
        Room room;
        InputStream ins = null;
        try
        {
            ins = request.getInputStream();
            // read in the request type
            playerId = Long.parseLong(readLine(ins));
            AdventureRoomFactory roomFactory;
            // if player id is 0 assign a new one
            if(playerId == 0)
            {
                // TODO : centralise generation of player ids and storage of level files
                playerId = NEXT_PLAYER_ID++;
            }
            roomFactory = (AdventureRoomFactory)request.getAttribute(ROOM_FACTORY);
            if(roomFactory == null)
            {
                roomFactory = new AdventureRoomFactory(Long.toString(playerId));
                request.setAttribute(ROOM_FACTORY, roomFactory);
            }
            String requestType = readLine(ins);
            if(requestType != null)
            {
                if(requestType.equals(LOAD))
                {
                    roomName = readLine(ins);
                    room = this.defaultRoomFactory.getRoom(roomName);
                }else if(requestType.equals(ENTER)){
                    String exitName = readLine(ins);
//                    StringBuffer sb = new StringBuffer();
//                    String line = readLine(ins);
//                    while(line != null)
//                    {
//                        sb.append(line);
//                        line = readLine(ins);
//                    }
//                    System.out.println(sb);
//                    unchecksum(sb);
//                    while(uncompress(sb));
//                    System.out.println(sb);
//                    InputStream sbis = new StringBufferInputStream(sb.toString());
                    Room from = Room.create(ins);
                    room = roomFactory.getRoom(from, exitName);
                }else{
                    throw new Exception("unrecognised request type:"+requestType);
                }
            }else{
                throw new Exception("no request type!!");
            }
        }catch(Exception ex){
            System.err.println("error reading input");
            ex.printStackTrace();
            this.log("error reading input", ex);
            throw new ServletException(ex);
        }finally{
            try
            {
                ins.close();
            }catch(Exception ex){
                this.log("error closing input stream", ex);
            }
        }



        OutputStream outs = null;
        try
        {
            response.setContentType("text/plain");
            outs = response.getOutputStream();
            Room.writeLine(outs, Long.toString(playerId));
            Room.store(room, outs);
            outs.flush();
        }catch(Exception ex){
            System.err.println("error writing output");
            ex.printStackTrace();
            this.log("error writing output", ex);
            throw new ServletException(ex);
        }finally{
            try
            {
                outs.close();
            }catch(Exception ex){
                this.log("error closing output stream", ex);
            }
        }
    }

    private static final String readLine(InputStream ins)
        throws IOException
    {
        int read = ins.read();
        if(read == -1)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        while(read != ((int)'\n') && read >= 0)
        {
            if(read != ((int) '\r'))
            {
                sb.append((char)read);
            }
            read = ins.read();
        }
        return sb.toString();
    }

    private static final char DELIMITER = '*';
    private static final char NEWLINE = '.';

    public static final void unchecksum(StringBuffer sb)
    {
        // read in the first line
        int i=0;
        StringBuffer l = new StringBuffer(64);
        char c;
        while((c = sb.charAt(i)) != NEWLINE)
        {
            l.append(c);
            i++;
        }
        sb.delete(0, i+1);

        long checksum = Long.parseLong(l.toString());
        long sum = 0;
        for(int j = sb.length(); j>0; )
        {
            j--;
            sum += sb.charAt(j);
        }
        if(sum != checksum)
        {
            throw new RuntimeException("Invalid checksum! "+checksum+"!="+sum);
        }
    }

    public static final boolean uncompress(StringBuffer sb)
    {
        boolean compressed;
        if(sb.charAt(0) == DELIMITER)
        {
            sb.deleteCharAt(0);
            // replace all new lines
            for(int i=sb.length(); i>0; )
            {
                i--;
                if(sb.charAt(i) == NEWLINE)
                {
                    sb.setCharAt(i, '\n');
                }
            }
            compressed = false;
        }else{
            // get the first 3 characters, replace the pairs
            char replaceChar = sb.charAt(0);
            char c1 = sb.charAt(1);
            char c2 = sb.charAt(2);
            sb.delete(0, 3);
            for(int i=sb.length(); i>0; )
            {
                i--;
                if(sb.charAt(i) == replaceChar)
                {
                    sb.setCharAt(i, c2);
                    sb.insert(i, c1);
                }
            }
            compressed = true;
        }
        return compressed;
    }
}
