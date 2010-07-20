package com.zonski.godhood.duels.server.test;

import com.zonski.godhood.duels.server.RandomSlumFactory;
import com.zonski.godhood.duels.game.Room;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 17, 2003
 * Time: 10:00:55 PM
 * To change this template use Options | File Templates.
 */
public class RandomSlumFactoryTestMain
{
    public static final void main(String[] args)
    {
        RandomSlumFactory factory = new RandomSlumFactory();
        try
        {
            Room room = factory.getRoom("test");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
