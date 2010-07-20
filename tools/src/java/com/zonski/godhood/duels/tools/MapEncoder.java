package com.zonski.godhood.duels.tools;

import com.zonski.godhood.duels.game.Room;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 12/03/2004
 * Time: 09:21:18
 * To change this template use Options | File Templates.
 * <p>
 * Tool to convert an image map text file into a binary image map file
 */
public class MapEncoder
{
    public static final String TARGET = "-t";
    public static final String DEFAULT_EXT = "map";

    private static final int MAX_NUMS = 64;

    public static final int main(String[] args)
    {
        HashMap properties = ToolUtil.getArguments(args);
        String sourcefile = (String)properties.get(null);
        if(sourcefile == null)
        {
            printUsage("must specify a source file");
            return 1;
        }

        String targetfile = (String)properties.get(TARGET);
        if(targetfile == null)
        {
            int index = sourcefile.lastIndexOf(".");
            if(index < 0)
            {
                targetfile = sourcefile + "." + DEFAULT_EXT;
            }else{
                String oldExt = sourcefile.substring(index+1);
                if(oldExt.equals(DEFAULT_EXT))
                {
                    printUsage("source file ends with '"+DEFAULT_EXT+"' will not overwrite");
                    return 1;
                }else{
                    targetfile = sourcefile.substring(0, index) + "." + DEFAULT_EXT;
                }
            }
        }

        System.out.println(sourcefile+" => "+targetfile);

        try
        {
            convert(sourcefile, targetfile);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public static final void convert(String sourcefile, String targetfile)
        throws Exception
    {
        Hashtable images;
        InputStream ins = null;
        DataOutputStream outs = null;
        try
        {
            ins = new FileInputStream(sourcefile);
            images = getImages(ins);
        }finally{
            if(ins != null)
            {
                ins.close();
            }
        }
        try
        {
            outs = new DataOutputStream(new FileOutputStream(targetfile));
            writeImages(outs, images);
        }finally{
            if(outs != null)
            {
                outs.close();
            }
        }
    }

    private static final Hashtable getImages(InputStream ins)
        throws Exception
    {
        int[] nums = new int[MAX_NUMS];
        Hashtable images = new Hashtable();
        String line;
        while((line = readLine(ins)) != null)
        {
            char c = line.charAt(0);
            int index = line.indexOf(",");
            String value = line.substring(1, index);
            Object key;
            switch(c)
            {
                case 's':
                    key = value;
                    break;
                case 'i':
                    key = new Integer(Integer.parseInt(value));
                    break;
                case 'c':
                    key = new Character(value.charAt(0));
                    break;
                default:
                    throw new Exception("unreadable line : "+line);
            }
            String rest = line.substring(index+1);
            index = rest.indexOf("//");
            if(index >= 0)
            {
                rest = rest.substring(0, index);
            }
            getNums(rest, nums);
            int[] position = new int[nums[0]];
            System.arraycopy(nums, 1, position, 0, position.length);
            if(images.containsKey(key))
            {
                throw new Exception("the key "+key+" already exists!");
            }
            images.put(key, position);
        }
        return images;
    }


    private static final void writeImages(DataOutputStream outs, Hashtable images)
        throws IOException
    {
        // we write out the max image here
        Enumeration keys = images.keys();
        int max = 0;
        int size = 0;
        while(keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            if(key instanceof Integer)
            {
                Integer i = (Integer)key;
                max = Math.max(i.intValue()+1, max);
                size ++;
            }
        }
        outs.writeByte(max);
        outs.writeByte(size);
        keys = images.keys();
        while(keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            if (key instanceof Integer)
            {
                Integer ib = (Integer) key;
                outs.writeByte((byte)ib.intValue());
                int[] value = (int[])images.get(key);
                outs.writeByte(value.length);
                for(int i=0; i<value.length; i++)
                {
                    int val = value[i];
                    if(val > Byte.MAX_VALUE)
                    {
                        val = Byte.MAX_VALUE - val;
                    }
                    outs.writeByte(val);
                }
            }
            else
            {
                System.err.println("WARNING : key "+key.toString()+" is not an integer, hence is discarded");
            }
                /*if (key instanceof String)
            {
                outs.writeByte(STRING);
                String s = (String) key;
                Room.writeString(outs, s);
            }
            else if (key instanceof Character)
            {
                outs.writeByte(CHAR);
                Character c = (Character) key;
                outs.writeChar(c.charValue());
            }
            */
        }
    }

    private static final void printUsage(String usage)
    {
        System.err.println(usage);
        System.err.println("usage : java "+MapEncoder.class.getName()+" [-t targetfile] sourcefile");
        System.err.println("the sourcefile extension is changed to .map if the targetfile parameter is omited");
    }

    public static final String readLine(InputStream ins)
        throws IOException
    {
        int read = ins.read();
        if(read == -1)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.delete(0, sb.length());
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

    /**
     * obtains the coma-seperated numbers from the line
     * @param line the line in the format 1,2,3,10
     * @param nums the numbers to populate, the first number is the length
     */
    public static final void getNums(String line, int[] nums)
    {
        int index = line.indexOf(',');
        int lastIndex = 0;
        int i = 1;
        while(index >= 0)
        {
            nums[i] = Integer.parseInt(line.substring(lastIndex, index));
            i++;
            lastIndex = index+1;
            index = line.indexOf(",", lastIndex);
        }
        nums[i] = Integer.parseInt(line.substring(lastIndex));
        nums[0] = i;
    }
}
