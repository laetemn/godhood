package com.zonski.godhood.duels.wireless;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 1, 2003
 * Time: 4:30:19 PM
 * To change this template use Options | File Templates.
 */
public final class DuelsMIDlet extends MIDlet
{
    private DuelsCanvas canvas;
    public DuelsMIDlet()
    {
        this.canvas = new DuelsCanvas(this);
    }

    protected final void startApp() throws MIDletStateChangeException
    {
        this.canvas.startApp();
    }

    protected final void pauseApp()
    {
        this.canvas.pauseApp();
    }

    protected final void destroyApp(boolean b) throws MIDletStateChangeException
    {
        this.canvas.destroyApp(b);
    }

//
//    public static final void checksum(StringBuffer sb)
//    {
//        long checksum = 0;
//        for(int i=0; i<sb.length(); i++)
//        {
//            checksum += sb.charAt(i);
//        }
//        sb.insert(0, Long.toString(checksum)+NEWLINE);
//
//    }
//
//    /**
//     * Compresses the buffer
//     * @param sb the buffer to compress
//     * @return whether or not any compression was possible
//     */
//    public static final boolean compress(StringBuffer sb)
//    {
//        // find a pair that occurs more than once
//        Object o = new Object();
//        Hashtable pairs = new Hashtable(sb.length());
//        int lastc = 0;
//        boolean found = false;
//        for(int i=sb.length(); i>1; )
//        {
//            i--;
//            // TODO : check that char is 16 bits
//            int c2 = sb.charAt(i);
//            int c1 = sb.charAt(i-1);
//            int c = c1 | c2 << 16;
//            // pack the sequence
//            Integer pair = new Integer(c);
//            if(pairs.containsKey(pair))
//            {
//                if(c != lastc)
//                {
//                    // we've got a pair!
//                    found = true;
//                    doCompress(sb, (char)c1, (char)c2);
//                    break;
//                }
//            }else{
//                pairs.put(pair, o);
//            }
//            lastc = c;
//        }
//        return found;
//    }
//
//    private static final char DELIMITER = '*';
//    private static final char NEWLINE = '.';
//
//    private static final void doCompress(StringBuffer sb, char c1, char c2)
//    {
//        int pos = 0;
//
//        // search and replace
//        // want to get a character that hasn't been used yet
//        int starPos = -1;
//        char replaceChar = Character.MIN_VALUE;
//        for(int i=sb.length(); i>0; )
//        {
//            i--;
//            char c = sb.charAt(i);
//            if(c == '\n')
//            {
//                sb.setCharAt(i, NEWLINE);
//                c = NEWLINE;
//            }
//            if(c >= replaceChar)
//            {
//                replaceChar = (char)(((int)c)+1);
//            }
//            if(c == DELIMITER)
//            {
//                starPos = i;
//            }
//        }
//        if(starPos == -1)
//        {
//            // TODO : assumes that '*' isn't used anywhere
//            sb.insert(pos, DELIMITER);
//        }
//        sb.insert(pos, c2);
//        sb.insert(pos, c1);
//        sb.insert(pos, replaceChar);
//
//        // find every occurrence of the pair, replace with the replace character
//        for(int i=sb.length(); i>4; )
//        {
//            i--;
//            char f2 = sb.charAt(i);
//            char f1 = sb.charAt(i-1);
//            if(f2 == c2 && f1 == c1)
//            {
//                sb.setCharAt(i, replaceChar);
//                sb.deleteCharAt(i-1);
//                i--;
//            }
//        }
//    }
//
//    public static final void unchecksum(StringBuffer sb)
//    {
//        // read in the first line
//        int i=0;
//        StringBuffer l = new StringBuffer(64);
//        char c;
//        while((c = sb.charAt(i)) != NEWLINE)
//        {
//            l.append(c);
//            i++;
//        }
//        sb.delete(0, i+1);
//
//        long checksum = Long.parseLong(l.toString());
//        long sum = 0;
//        for(int j = sb.length(); j>0; )
//        {
//            j--;
//            sum += sb.charAt(j);
//        }
//        if(sum != checksum)
//        {
//            throw new RuntimeException("Invalid checksum! "+checksum+"!="+sum);
//        }
//    }
//
//    public static final boolean uncompress(StringBuffer sb)
//    {
//        boolean compressed;
//        if(sb.charAt(0) == DELIMITER)
//        {
//            sb.deleteCharAt(0);
//            // replace all new lines
//            for(int i=sb.length(); i>0; )
//            {
//                i--;
//                if(sb.charAt(i) == NEWLINE)
//                {
//                    sb.setCharAt(i, '\n');
//                }
//            }
//            compressed = false;
//        }else{
//            // get the first 3 characters, replace the pairs
//            char replaceChar = sb.charAt(0);
//            char c1 = sb.charAt(1);
//            char c2 = sb.charAt(2);
//            sb.delete(0, 3);
//            for(int i=sb.length(); i>0; )
//            {
//                i--;
//                if(sb.charAt(i) == replaceChar)
//                {
//                    sb.setCharAt(i, c2);
//                    sb.insert(i, c1);
//                }
//            }
//            compressed = true;
//        }
//        return compressed;
//    }
}
