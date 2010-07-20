package com.zonski.godhood.duels.tools;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: 12/03/2004
 * Time: 09:23:27
 * To change this template use Options | File Templates.
 */
public class ToolUtil
{
    public static final String FLAG_PREFIX =    "-";

    public static final HashMap getArguments(String[] args)
    {
        HashMap properties = new HashMap(args.length/2+1);
        for(int i=0; i<args.length; i++)
        {
            String arg = args[i];
            if(arg.startsWith(FLAG_PREFIX))
            {
                properties.put(arg, args[i+1]);
                i++;
            }else if(i == args.length - 1){
                properties.put(null, args[i]);
            }
        }
        return properties;
    }
}
