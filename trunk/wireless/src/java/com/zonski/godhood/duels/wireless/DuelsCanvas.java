package com.zonski.godhood.duels.wireless;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.RoomListener;
import com.zonski.godhood.duels.game.Entity;
import com.zonski.godhood.duels.game.Action;
import com.zonski.godhood.duels.game.action.AttackAction;
import com.zonski.godhood.duels.game.action.SimpleAction;
import com.zonski.godhood.duels.game.action.MoveAction;
import com.zonski.godhood.duels.game.action.MonsterAction;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordEnumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Random;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 1, 2003
 * Time: 4:35:18 PM
 * To change this template use Options | File Templates.
 */
public final class DuelsCanvas extends Canvas implements RoomListener, Runnable, CommandListener

{
    private static final byte DEFAULT_TILE_WIDTH = 16;
    private static final byte DEFAULT_TILE_HEIGHT = 12;

    private static final byte SIDE_SHAPE_RECT = 0;
    private static final byte SIDE_SHAPE_OVAL = 1;
    private static final byte SIDE_SHAPE_LINE = 2;

    private static final byte PAN_SHOOT   = 1;
    private static final byte PAN_ENTITY  = 2;

    private static final byte HEALTH_WIDTH = 2;

    private static final byte BIG_HEALTH_WIDTH = 3;
    private static final byte BIG_HEALTH_HEIGHT = 20;

    private static final byte DOING_BLEEDING  = Entity.MAX_MONSTER + 1;
    private static final byte DOING_DIE       = Entity.MAX_MONSTER + 2;
    private static final byte DOING_SHOOT     = Entity.MAX_MONSTER + 3;
    private static final byte DOING_MAGIC     = 0;
    private static final byte DOING_THROW     = 0;
    private static final byte DOING_WALK      = Entity.MAX_MONSTER + 6;
    //private static final String DOING_FLY       = "FLY";
    private static final byte DOING_PICKUP    = Entity.MAX_MONSTER + 7;
    private static final byte DOING_PUTDOWN   = Entity.MAX_MONSTER + 8;
    private static final byte DOING_DAZED     = Entity.MAX_MONSTER + 9;
    private static final byte DOING_HAND_TO_HAND = Entity.MAX_MONSTER + 10;
    private static final byte DOING_DESCEND   = Entity.MAX_MONSTER + 11;
    //private static final String DOING_ASCEND    = "ASC";
    //private static final String DOING_WEB       = "WEB";
    //private static final String DOING_ENDTURN   = "END";
    private static final byte DOING_WAIT      = Entity.MAX_MONSTER + 12;
    private static final byte DOING_DEFEND    = Entity.MAX_MONSTER + 13;
    private static final byte DOING_PRAY      = Entity.MAX_MONSTER + 17;
    private static final byte DOING_FLY       = Entity.MAX_MONSTER + 18;

    private static final byte OCTAGRAM        = Entity.MAX_MONSTER + 14;
    //public static final byte MOVE_DIAGONAL    = Entity.MAX_MONSTER + 15;
    //public static final byte MOVE_STRAIGHT    = Entity.MAX_MONSTER + 16;

    private static final int getSideColor(int side)
    {
        int result;
        int s = side - 1;
        int color = 0;
        int add = 0;
        int mod = s%8;
        int div = (s/8)%8;
        for(int i=0; i<3; i++)
        {
            color <<= 8;
            add <<= 8;
            if((mod & 0x01) != 0)
            {
                color |= 0xFF;
            }
            if((div & 0x01) != 0)
            {
                add |= 0x80;
            }
            mod >>= 1;
            div >>= 1;
        }
        result = color-add;
        return result;
    }

    private Entity currentEntity;

    private String currentMessage;

    private int tileWidth = DEFAULT_TILE_WIDTH;
    private int tileHeight = DEFAULT_TILE_HEIGHT;

    private AttackAction attack;
    private boolean shooting;
    private int projectileX;
    private int projectileY;
    private int targetX;
    private int targetY;
    private int originX;
    private int originY;

    private int oldX;
    private int oldY;

    //private Image image;
    //private Graphics graphics;

    /**
     * x coordinate of the centre of the screen
     */
    private int x;
    /**
     * y coordinate of the centre of the screen
     */
    private int y;

    private static final Font FONT = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

    private DuelsMIDlet midlet;

    private static final byte getKey(Action doing, Entity entity)
    {
        byte doingKey;
        if(doing != null)
        {
            if(doing instanceof SimpleAction)
            {
                SimpleAction simpleDoing = (SimpleAction)doing;
                switch(simpleDoing.getMode())
                {
                    case SimpleAction.BLEEDING:
                        doingKey = DOING_BLEEDING;
                        break;
                    case SimpleAction.DAZED:
                        doingKey = DOING_DAZED;
                        break;
                    case SimpleAction.DIE:
                        doingKey = DOING_DIE;
                        break;
                    case SimpleAction.PICKUP:
                        doingKey = DOING_PICKUP;
                        break;
                    case SimpleAction.PUTDOWN:
                        doingKey = DOING_PUTDOWN;
                        break;
                    case SimpleAction.EXIT:
                        // TODO : ascend/descend
                        doingKey = DOING_DESCEND;
                        break;
                    case SimpleAction.DEFEND:
                        doingKey = DOING_DEFEND;
                        break;
                    case SimpleAction.PRAY:
                    case SimpleAction.HEAL:
                        doingKey = DOING_PRAY;
                        break;
                    default:
                        doingKey = Entity.NONE_TYPE;
                }
            }else if(doing instanceof AttackAction){
                AttackAction doingAttack = (AttackAction)doing;
                switch(doingAttack.type)
                {
                    case AttackAction.HAND_TO_HAND:
                        doingKey = DOING_HAND_TO_HAND;
                        break;
                    case AttackAction.MAGIC:
                        doingKey = DOING_MAGIC;
                        break;
                    case AttackAction.ARROW:
                        doingKey = DOING_SHOOT;
                        break;
                    case AttackAction.THROWN_OBJECT:
                        doingKey = DOING_THROW;
                        break;
                    default:
                        doingKey = Entity.NONE_TYPE;
                }
            }else if(doing instanceof MoveAction){
                // doing fly
                if(entity != null && (entity.entityCategory & Entity.CATEGORY_FLYING)>0)
                {
                    doingKey = DOING_FLY;
                }else{
                    doingKey = DOING_WALK;
                }
            }else{
                doingKey = Entity.NONE_TYPE;
            }
        }else{
            doingKey = Entity.NONE_TYPE;
        }
        return doingKey;
    }

    public DuelsCanvas(DuelsMIDlet midlet)
    {
        this.midlet = midlet;
        this.currentMessage = null;
    }

    private void setRoom(Room room)
    {
        if(this.currentRoom != null)
        {
            this.currentRoom.removeRoomListener(this);
        }
        this.currentRoom = room;
        this.currentMessage = null;
        this.currentEntity = null;
        this.mode = AC_NONE;
        if(this.currentRoom != null)
        {
            //this.room.addRoomListener(this);
            // centre in the room
            //int w = (room.width * this.tileWidth + room.height * this.tileWidth)/2+this.tileWidth;
            //int h = (room.width * this.tileHeight + room.height * this.tileHeight)/2+this.tileHeight;
            this.x = this.getWidth()/2;
            this.y = this.getHeight()/2;
        }else{
            this.x = 0;
            this.y = 0;
        }
    }

    /*
    private final void createFloor()
    {
        int mintx = 0;
        int minty = 0;
        int maxtx = this.room.width-1;
        int maxty = this.room.height-1;

        int w = (maxtx * this.tileWidth + maxty * this.tileWidth)/2+this.tileWidth;
        int h = (maxtx * this.tileHeight + maxty * this.tileHeight)/2+this.tileHeight + this.tileHeight;

        this.floor = Image.createImage(w, h);
        Graphics g = this.floor.getGraphics();
        drawFloor(g, mintx, minty, maxtx, maxty, w, h, 0, 0);
    }
    */

    private final void drawFloor(Graphics g, int mintx, int minty, int maxtx, int maxty, int w, int offx, int offy)
    {
        g.setColor(0);
        int cw = this.getWidth();
        int ch = this.getHeight();
        g.fillRect(0, 0, cw, ch);

        for(int ty = minty; ty<=maxty; ty++)
        {
            for(int tx = mintx; tx<=maxtx; tx++)
            {
                int x = w/2 + (tx * this.tileWidth - ty * this.tileWidth)/2 + offx;
                if(x > cw+this.tileWidth/2)
                {
                    break;
                }else if(x >= -this.tileWidth/2 - 1){
                    int y = (tx * this.tileHeight + ty * this.tileHeight)/2 + offy;
                    if(y > ch+this.tileHeight/2)
                    {
                        break;
                    }else if(y >= -this.tileHeight){
                        byte tile = this.currentRoom.getTile(tx, ty);
                        byte[] tileImage = this.getPosition(tile);
                        if(tileImage != null)
                        {
                            this.render(g, tileImage, x, y, false);
                        }
                    }
                }
            }
        }
    }

    public final void paint(Graphics g)
    {
        /*
        if(this.bufferImage == null)
        {
            int w = getWidth();
            int h = getHeight();
            this.bufferImage = Image.createImage(w, h);
            this.bufferGraphics = this.bufferImage.getGraphics();
        }

        draw(this.bufferGraphics);
        g.drawImage(this.bufferImage, 0, 0, Graphics.TOP | Graphics.LEFT);
        */
        //draw(g);
        //this.eventThread = Thread.currentThread();
        //synchronized(this.paintLock)
        if(this.currentRoom != null)
        {
            this.draw(g);
            //g.drawImage(this.image, 0, 0, Graphics.TOP | Graphics.LEFT);
            //this.paintLock.notify();
        }else{
            g.setColor(0x000000);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.setColor(0xFFFFFF);
            g.drawString("Loading...", this.getWidth()/2, 0, Graphics.TOP | Graphics.HCENTER);
        }
    }

    private final void draw(Graphics g)
    {
        try
        {
            int w = this.getWidth();
            int h = this.getHeight();
            g.setClip(0, 0, w, h);
            if(this.mode == LOSE || this.mode == WIN)
            {
                // it's gameover for you
                // draw some angels
                this.render(g);
            }else{

                g.setColor(0);
                g.fillRect(0, 0, w, h);

                //int cx = this.currentEntity.x;
                //int cy = this.currentEntity.y;

                //int tilesDown = h / (this.tileHeight/2) + 1;
                //int tilesAcross = w / (this.tileWidth/2) + 1;
                // the number of tiles to draw, both acrosss and down
                //int tiles = Math.max(tilesDown, tilesAcross);

                // TODO : use x and y
                //int mintx = Math.max(0, cx - tiles/2);
                //int minty = Math.max(0, cy - tiles/2);
                //int maxtx = Math.min(room.width-1, cx + tiles/2 + 1);
                //int maxty = Math.min(room.height-1, cy + tiles/2 + 1);
                int mintx = 0;
                int minty = 0;
                int maxtx = this.currentRoom.width-1;
                int maxty = this.currentRoom.height-1;

                int wa = (maxtx * this.tileWidth + maxty * this.tileWidth)/2+this.tileWidth;

                //g.drawImage(this.floor, (w-this.floor.getWidth())/2-this.x, h/2-this.y, Graphics.TOP | Graphics.LEFT);
                drawFloor(g, mintx, minty, maxtx, maxty, wa, (w-wa)/2-this.x, h/2-this.y);
                /*
                g.drawImage(this.floor,
                        (w + (-cx * this.tileWidth  + cy * this.tileWidth) - this.floor.getWidth())/2 + this.offX,
                        (h + (-cx * this.tileHeight - cy * this.tileHeight))/2 + this.offY,
                        Graphics.TOP | Graphics.LEFT);
                        */
                if(this.currentEntity != null && this.mode != AC_NONE)
                {
                    this.renderUnderTarget(g);
                }
                for(int ty = minty; ty<=maxty; ty++)
                {
                    for(int tx = mintx; tx<=maxtx; tx++)
                    {
                        int dx = (tx * this.tileWidth - ty * this.tileWidth)/2 - this.x + this.tileWidth/2;
                        int x = w/2 + dx;
                        if(x > -this.tileWidth/2)
                        {
                            if(x > w+this.tileWidth/2)
                            {
                                // we're off the edge of the screen
                                break;
                            }
                            int dy = (tx * this.tileHeight + ty * this.tileHeight)/2 - this.y;
                            int y = h/2 + dy;
                            if(y >= -this.tileHeight/2)
                            {
                                // TODO : have an upper limit for the height of monsters and break out
                                // of this loop if y exceeds that height
                                byte obstacle = this.currentRoom.getObstacle(tx, ty);
                                if(obstacle != 0)
                                {
                                    byte[] obstacleImage = this.getPosition(obstacle);
                                    if(obstacleImage != null)
                                    {
                                        //int offx = obstacleImage[0];
                                        //int offy = obstacleImage[1];
                                        int tw = obstacleImage[2];
                                        int th = obstacleImage[3];
                                        int yadjust;
                                        if(obstacleImage.length > 4)
                                        {
                                            yadjust = obstacleImage[4];
                                        }else{
                                            yadjust = this.tileHeight/2+2;
                                        }
                                        //g.setClip(x-tw/2, y+yadjust-th, tw, th);
                                        //g.drawImage(this.source, x-tw/2-offx, y+yadjust-th-offy, Graphics.LEFT | Graphics.TOP);
                                        this.render(g, obstacleImage, x-tw/2, y+yadjust-th, false);
                                    }
                                }
                                Vector entities = this.currentRoom.getEntitiesAt(tx, ty);
                                if(entities != null)
                                {
                                    for(int i=0; i<entities.size(); i++)
                                    {
                                        Entity entity = (Entity)entities.elementAt(i);
                                        if(entity == this.currentEntity)
                                        {
                                            this.render(g, OCTAGRAM, x, y+this.tileHeight/2, true);
                                        }
                                        byte type = entity.entityTypeId;
                                        byte[] entityImage = this.getPosition(type);
                                        if(entityImage != null)
                                        {
                                            //int offx = entityImage[0];
                                            //int offy = entityImage[1];
                                            int tw = entityImage[2];
                                            int th = entityImage[3];
                                            int yadjust;
                                            if(entityImage.length > 4)
                                            {
                                                yadjust = entityImage[4];
                                            }else{
                                                yadjust = this.tileHeight/2+2;
                                            }
                                            //g.setClip(x-tw/2, y+yadjust-th, tw, th);
                                            this.render(g, entityImage, x-tw/2, y+yadjust-th, false);
                                            // draw the entitys' side color
                                            int shape = 5;
                                            int color = getSideColor(entity.side);
                                            while(entity.side != 0 && entityImage.length > shape)
                                            {
                                                int sx = convert(entityImage[shape]);
                                                int sy = convert(entityImage[shape+1]);
                                                int sw = entityImage[shape+2];
                                                int sh = entityImage[shape+3];
                                                int stype = entityImage[shape+4];
                                                g.setColor(color);
                                                switch(stype)
                                                {
                                                    case SIDE_SHAPE_RECT:
                                                        g.fillRect(x-tw/2+sx, y+yadjust-th+sy, sw, sh);
                                                        break;
                                                    case SIDE_SHAPE_OVAL:
                                                        g.fillArc(x-tw/2+sx, y+yadjust-th+sy, sw, sh, 0, 360);
                                                        break;
                                                    case SIDE_SHAPE_LINE:
                                                        g.drawLine(x-tw/2+sx, y+yadjust-th+sy, x-tw/2+sx+sw, y+yadjust-th+sy+sh);
                                                        break;
                                                }
                                                g.setColor(0);
                                                switch(stype)
                                                {
                                                    case SIDE_SHAPE_RECT:
                                                        g.drawRect(x-tw/2+sx, y+yadjust-th+sy, sw, sh);
                                                        break;
                                                    case SIDE_SHAPE_OVAL:
                                                        g.drawArc(x-tw/2+sx, y+yadjust-th+sy, sw, sh, 0, 360);
                                                        break;
                                                }
                                                shape += 5;
                                            }

                                            //int top = y+yadjust-th;

                                            Action doing;
                                            if(entity.doing != null)
                                            {
                                                doing = entity.doing;
                                            }else if(entity.entityType == Entity.INHERIT_TYPE &&
                                                    entity.actions != null && entity.actions.size() == 1)
                                            {
                                                doing = (Action)entity.actions.elementAt(0);
                                            }else{
                                                doing = null;
                                            }

                                            if(doing != null)
                                            {
                                                byte doingKey = getKey(doing, entity);
                                                if(doingKey != Entity.NONE_TYPE)
                                                {
                                                    byte[] doingImage = getPosition(doingKey);
                                                    // if doingImage.length == 5 its for menu use only
                                                    if(doingImage != null && doingImage.length != 5)
                                                    {
                                                        //int actionx = doingImage[0];
                                                        //int actiony = doingImage[1];
                                                        int actionw = doingImage[2];
                                                        int actionh = doingImage[3];
                                                        if(doingKey == DOING_BLEEDING)
                                                        {
                                                            //top -= actionh;

                                                            //g.setClip(x-(actionw)/2,
                                                            //        y+yadjust-th-actionh-2,
                                                            //        actionw, actionh);
                                                            //g.drawImage(this.source,
                                                            //        x-(actionw)/2-actionx,
                                                            //        y+yadjust-th-actionh-actiony-2,
                                                            //        Graphics.LEFT | Graphics.TOP);
                                                            this.render(g, doingImage, x-actionw/2, y+yadjust-th-actionh-2, false);
                                                            g.setClip(0, 0, w, h);
                                                            // draw in the amount of damage and the amount of health
                                                            int maxHealth = entity.maxHealth;
                                                            if(maxHealth > 0)
                                                            {

                                                                int health = entity.health;
                                                                int damage = ((SimpleAction)entity.doing).type;
                                                                g.setColor(0xFF0000);
                                                                g.fillRect(x-(actionw)/2,
                                                                        y+yadjust-th-HEALTH_WIDTH/2-2,
                                                                        (actionw*(health+damage))/maxHealth, HEALTH_WIDTH);
                                                                g.setColor(0x00FF00);
                                                                g.fillRect(x-(actionw)/2,
                                                                        y+yadjust-th-HEALTH_WIDTH/2-2,
                                                                        (actionw*health)/maxHealth, HEALTH_WIDTH);
                                                                g.setColor(0x505050);
                                                                g.drawRect(x-(actionw)/2,
                                                                        y+yadjust-th-HEALTH_WIDTH/2-2,
                                                                        actionw, HEALTH_WIDTH);
                                                            }
                                                        }else{
                                                            //top -= actionh;
                                                            //g.setClip(x-actionw/2, y+yadjust-th-actionh-2, actionw, actionh);
                                                            //g.drawImage(this.source,
                                                            //        x-actionw/2-actionx, y+yadjust-th-actionh-actiony-2,
                                                            //        Graphics.LEFT | Graphics.TOP);
                                                            this.render(g, doingImage, x-actionw/2, y+yadjust-th-actionh-2, false);
                                                        }
                                                    }
                                                }
                                            }
                                        }else{

                                            //System.out.println("unknown entity "+entity.name+" at ("+x+","+y+")");
                                            g.setFont(Font.getDefaultFont());
                                            g.setClip(0, 0, w, h);
                                            g.setColor(0xFF0000);
                                            g.drawString(entity.name, x, y, Graphics.HCENTER | Graphics.BASELINE);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(this.shooting)
                {
                    if(this.attack.type == AttackAction.ARROW)
                    {
                        g.setColor(0x807040);
                        g.setClip(this.projectileX-2 - this.x + w/2, this.projectileY-2 - this.y + h/2, 4, 4);
                        g.drawLine(this.originX - this.x + w/2, this.originY - this.y + h/2,
                                this.targetX - this.x + w/2, this.targetY - this.y + h/2);
                    }else{
                        g.setClip(0, 0, w, h);
                        if(attack.type == AttackAction.MAGIC)
                        {
                            g.setColor(0x00FF0000);
                        }else{
                            g.setColor(0x90909090);
                        }
                        g.fillArc(this.projectileX-2 - this.x + w/2, this.projectileY-2 - this.y + h/2, 4, 4, 0, 360);
                        g.setColor(0x00);
                        g.drawArc(this.projectileX-2 - this.x + w/2, this.projectileY-2 - this.y + h/2, 4, 4, 0, 360);

                    }
                }
                if(this.currentEntity != null)
                {
                    // render the entitys name
                    StringBuffer sb = new StringBuffer(this.currentEntity.name);
                    if(sb.length() > 0)
                    {
                        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                        g.setClip(0, 0, w, h);
                        g.setColor(0xFFFFFF);
                        g.setFont(FONT);
                        g.drawString(sb.toString(), 2, h, Graphics.BOTTOM | Graphics.LEFT);
                    }
                }
                if(this.mode != AC_NONE && this.currentEntity != null)
                {
                    g.setFont(FONT);
                    g.setClip(0, 0, w, h);
                    g.setColor(0x0040FFFF);
                    g.drawString(Integer.toString(this.currentEntity.unitsRemaining), BIG_HEALTH_WIDTH+3, h-FONT.getHeight(), Graphics.BOTTOM | Graphics.LEFT);
                    drawHealth(g, this.currentEntity, 2, h-BIG_HEALTH_HEIGHT-2-FONT.getHeight());

                    byte doingKey = getKey(this.action, this.currentEntity);
                    byte[] position = this.getPosition(doingKey);
                    if(position != null)
                    {
                        int px = BIG_HEALTH_WIDTH+2;
                        int py = h-FONT.getHeight()-position[3]-FONT.getHeight();
                        //g.setClip(px, py, position[2], position[3]);
                        //g.drawImage(this.source, px-position[0], py-position[1], Graphics.LEFT | Graphics.TOP);
                        this.render(g, position, px, py, false);
                    }
                    g.setClip(0, 0, w, h);
                    render(g);
                }else{
                    // we must wait
                    byte[] waitImage = this.getPosition(DOING_WAIT);
                    //int ix = waitImage[0];
                    //int iy = waitImage[1];
                    //int iw = waitImage[2];
                    int ih = waitImage[3];
                    //g.setClip(2, h-ih-2, iw, ih);
                    //g.drawImage(this.source, -ix+2, h-ih-iy-2, Graphics.TOP | Graphics.LEFT);
                    this.render(g, waitImage, 2, h-ih-2-FONT.getHeight(), false);
                }
            }
            if(this.currentMessage != null)
            {
                g.setClip(0, 0, w, h);
                Font font = FONT;
                g.setFont(font);
                int messageHeight = font.getHeight();
                g.setColor(0x00FFFFFF);
                int y = 0;
                //for(int i=this.currentMessages.size(); i>0; )
                {
                    String message = this.currentMessage;
                    boolean done = false;
                    while(!done)
                    {
                        String line = message;

                        while(font.stringWidth(line)>w)
                        {
                            int index = line.length()-1;
                            while(index >= 0 && line.charAt(index) != ' ')
                            {
                                index --;
                            }
                            if(index >= 0)
                            {
                                line = line.substring(0, index);
                            }else{
                                break;
                            }
                        }
                        g.drawString(line, 0, y, Graphics.LEFT | Graphics.TOP);
                        if(line.length() >= message.length())
                        {
                            done = true;
                        }else{
                            message = message.substring(line.length());
                        }
                        y += messageHeight;
                    }
                    y += 1;
                }
            }else{

                Runtime runtime = Runtime.getRuntime();
                long used = (runtime.totalMemory()-runtime.freeMemory()) *100;
    //            String memory = ""+(runtime.totalMemory()-runtime.freeMemory())+"/"+runtime.totalMemory();
                RecordStore rs = RecordStore.openRecordStore(GAME, true);
                String memory = ""+used / runtime.totalMemory()+"% "+
                        (rs.getSize()*100)/(rs.getSize()+ rs.getSizeAvailable())+"%";
                rs.closeRecordStore();
                g.setClip(0, 0, w, h);
                g.setColor(0xFFFFFF);
                g.setFont(FONT);
                g.drawString(memory, 0, 0, Graphics.TOP | Graphics.LEFT);

            }
        }catch(Exception ex){
            ex.printStackTrace();
            //this.midlet.fatalError("drawing", ex.toString());
        }
    }

    private static final void drawHealth(Graphics g, Entity entity, int x, int y)
    {
        g.setColor(0x00FF00);
        int maxHealth = entity.maxHealth;
        int ybase = y + BIG_HEALTH_HEIGHT;
        if(maxHealth > 0)
        {
            int height = (BIG_HEALTH_HEIGHT*entity.health)/maxHealth;
            g.fillRect(x, ybase-height, BIG_HEALTH_WIDTH, height);
            g.setColor(0x505050);
            g.drawRect(x, y, BIG_HEALTH_WIDTH, BIG_HEALTH_HEIGHT);
        }
    }

    private final int[] getPoint(byte tx, byte ty)
    {
        int w = this.getWidth();
        int h = this.getHeight();
        /*
        Entity centre = this.currentEntity;
        int cx = centre.x;
        int cy = centre.y;
        int x = w/2 + ((tx-cx) * this.tileWidth - ((ty-cy) * this.tileWidth))/2 + this.offX;
        int y = h/2 + ((tx-cx) * this.tileHeight + (ty-cy) * this.tileHeight)/2 + this.offY;
        */
        int x = (tx * this.tileWidth - ty * this.tileWidth)/2 - this.x + w/2 + this.tileWidth/2;
        int y = (tx * this.tileHeight + ty * this.tileHeight)/2 - this.y + h/2;

        return new int[]{x, y};
    }


    /*
    public final byte[] getTile(int x, int y)
    {
        int w = this.getWidth();
        int h = this.getHeight();
        Entity centre = this.currentEntity;
        int cx = centre.x;
        int cy = centre.y;
        int tx = cx + ((x - w/2)*tileWidth - (y - h/2)*tileHeight)/2;
        int ty = cy + ((y - h/2)*tileHeight - (x - w/2)*tileWidth)/2;
        return new byte[]{(byte)tx, (byte)ty};
    }
    */

    public void entityUpdated(Room room, Entity entity, Action action, String[] messages)
    {
        this.checkGameStatus(entity, action);
        /*
        System.out.print("entity updated "+entity.name);
        if(action != null)
        {
            System.out.println(" doing "+action.getName());
        }else{
            System.out.println();
        }
        */

        long time = System.currentTimeMillis();
        if(!entity.isFlag(Entity.DONE_FLAG))
        {
            if(this.currentEntity != entity)
            {
                if(this.currentEntity != null && this.currentEntity.side == 0)
                {
                    this.clearMessages();
                }
                this.currentEntity = entity;
            }
        }
        boolean shot = false;
        if(action instanceof AttackAction){
            AttackAction attack = (AttackAction)action;
            if(attack.range > 1 && messages.length > 0 && attack.isCompleted() && attack != this.attack)
            {
                this.targetX = attack.attackX;
                this.targetY = attack.attackY;
                this.attack = attack;
                pan(PAN_SHOOT);
            }
        }
        boolean skip;
        if(action instanceof SimpleAction)
        {
            SimpleAction simple = (SimpleAction)action;
            skip = simple.getMode() == SimpleAction.ENDTURN;
        }else if(action == null && entity.side != Room.PLAYER_SIDE){
            skip = true;
        }else{
            skip = false;
        }
        if(!skip && !shot && this.currentEntity != null &&
                (this.oldX != this.currentEntity.x || this.oldY != this.currentEntity.y))
        {
            //pan(PAN_ENTITY);
            // check whether the entity is in an acceptable boundary of the screen
            //int[] point = getPoint(this.currentEntity.x, this.currentEntity.y);
            //int sx = point[0];
            //int sy = point[1];
            byte tx = this.currentEntity.x;
            byte ty = this.currentEntity.y;
            int eh;
            byte[] position = this.getPosition(this.currentEntity.entityTypeId);
            if(position != null)
            {
                eh = position[3];
            }else{
                eh = 0;
            }

            if(!this.isVisible(tx, ty, eh)){
                pan(PAN_ENTITY);
            }else{
                this.repaint();
            }
            this.oldX = this.currentEntity.x;
            this.oldY = this.currentEntity.y;
        }

        if(messages != null && messages.length > 0)
        {
            for(int i=0; i<messages.length; i++)
            {
                this.addMessage(messages[i]);
                this.repaint();
                synchronized(this)
                {
                    try
                    {
                        this.wait(2000);
                        //Thread.sleep(2000);
                    }catch(Exception ex){
                        // do nothing
                    }
                }
            }
        }else{
            int amount;
            if(this.currentEntity != null)
            {
                if(this.currentEntity.doing != null)
                {
                    Action doing = this.currentEntity.doing;
                    if(doing instanceof MoveAction)
                    {
                        amount = 100;
                    }else if(doing instanceof SimpleAction){
                        SimpleAction simple = (SimpleAction)doing;
                        if(simple.getMode() == SimpleAction.DIE || simple.getMode() == SimpleAction.ENDTURN)
                        {
                            amount = 0;
                        }else{
                            amount = 300;
                        }
                    }else if(doing instanceof AttackAction){
                        // if the object added no messages, but attacked then we're not going to wait
                        amount = 0;
                    }else{
                        amount = 300;
                    }
                }else{
                    amount = 0;
                }
            }else{
                amount = 0;
            }
            long wait = amount - (System.currentTimeMillis() - time);
            if(wait > 0)
            {
                synchronized(this)
                {
                    try
                    {
                        this.wait(amount);
                    }catch(Exception ex){
                        // do nothing
                    }
                }
            }
        }
    }

    private final boolean isVisible(int sx, int sy, int eh)
    {
        int w = this.getWidth();
        int h = this.getHeight();

        int dx = sx - this.x;
        int dy = sy - this.y;

        int xb = w/2;
        int yb = h/2;
        return !((Math.abs(dx) > (w-xb)/2 || Math.abs(dy) > (h-yb)/2 || dy < -(h/2-eh)));
    }

    private final boolean isVisible(byte tx, byte ty, int eh)
    {
        int sx = (tx * this.tileWidth - ty * this.tileWidth)/2 + this.tileWidth/2;
        int sy = (tx * this.tileHeight + ty * this.tileHeight)/2;
        return isVisible(sx, sy, eh);
    }

    private void addMessage(String message)
    {
        this.currentMessage = message;
    }

    private void clearMessages()
    {
        this.currentMessage = null;
    }

    private final void pan(int panMode)
    {
        switch(panMode)
        {
            case PAN_SHOOT:
                {
                    int w = getWidth();
                    int h = getHeight();
                    this.x = w/2;
                    this.y = h/2;
                    int[] point = getPoint(this.currentEntity.x, this.currentEntity.y);
                    this.originX = point[0];
                    this.originY = point[1];
                    this.projectileX = this.originX;
                    this.projectileY = this.originY;
                    int[] target = getPoint(this.attack.attackX, this.attack.attackY);
                    this.targetX = target[0];
                    this.targetY = target[1];
                    int dx = this.targetX - this.originX;
                    int dy = this.targetY - this.originY;
                    int distancesq = dx*dx + dy*dy;
                    this.shooting = true;
                    //this.x = originX;
                    //this.y = originY;
                    this.repaint();
                    int progress = 0;
                    int inc = 4;
                    while(true)
                    {
                        long time = System.currentTimeMillis();
                        progress += inc;
                        int progresssq = progress*progress;
                        if(progresssq > distancesq)
                        {
                            break;
                        }
                        this.projectileX = this.originX + (dx * progresssq)/distancesq;
                        this.projectileY = this.originY + (dy * progresssq)/distancesq;

                        if(!isVisible(this.projectileX, this.projectileY, 1))
                        {
                            this.x = this.projectileX;
                            this.y = this.projectileY;
                        }

                        repaint();
                        long diff = time - System.currentTimeMillis() + 110;
                        if(diff > 0)
                        {
                            try
                            {
                                Thread.sleep(diff);
                            }catch(Exception ex){
                                // do nothing
                            }
                        }
                    }
                    this.shooting = false;
                    //this.x = this.targetX;
                    //this.y = this.targetY;
                    repaint();
                }
                break;
            case PAN_ENTITY:
                // reduce the offsets to 0
                if(this.currentEntity != null)
                {
                    /*
                    int tx = this.currentEntity.x;
                    int ty = this.currentEntity.y;
                    int cx = (tx * this.tileWidth - ty * this.tileWidth)/2;
                    int cy = (tx * this.tileHeight + ty * this.tileHeight)/2;
                    while(this.x != cx || this.y != cy)
                    {
                        long time = System.currentTimeMillis();
                        int dx = cx - this.x;
                        int dy = cy - this.y;
                        this.x = this.x + dx/2 + dx%2;
                        this.y = this.y + dy/2 + dy%2;
                        repaint();
                        long diff = time - System.currentTimeMillis() + 50;
                        if(diff > 0)
                        {
                            try
                            {
                                Thread.sleep(diff);
                            }catch(Exception ex){
                                // do nothing
                            }
                        }
                    }
                    */
                    // not sure if this is a good idea, ah well
                    byte[][] points = AttackAction.getPoints(this.currentEntity.x, this.currentEntity.y, (byte)this.oldX, (byte)this.oldY);
                    long time = System.currentTimeMillis();
                    for(int i=0; i<points.length; i++)
                    {
                        byte tx = points[i][0];
                        byte ty = points[i][1];

                        if(!this.isVisible(tx, ty, 1))
                        {
                            int cx = (tx * this.tileWidth - ty * this.tileWidth)/2;
                            int cy = (tx * this.tileHeight + ty * this.tileHeight)/2;
                            this.x = cx;
                            this.y = cy;
                            repaint();
                            long diff = time - System.currentTimeMillis() + 100;
                            if(diff > 0)
                            {
                                try
                                {
                                    Thread.sleep(diff);
                                }catch(Exception ex){
                                    // do nothing
                                }
                            }
                            time = System.currentTimeMillis();
                        }
                    }

                    // centre on the entity
                    int tx = this.currentEntity.x;
                    int ty = this.currentEntity.y;
                    int cx = (tx * this.tileWidth - ty * this.tileWidth)/2;
                    int cy = (tx * this.tileHeight + ty * this.tileHeight)/2;
                    this.x = cx;
                    this.y = cy;
                    repaint();
                }
                break;
        }
    }

    //
    // from duels midlet
    //

    //private static final int MAX_ATTEMPTS = 3;

    private static final byte NONE = 0;

    /**
     * Load request type, loads a specific level
     */
    private static final byte LOAD = 1;

    /**
     * Create request type, creates a new session for this player
     */
    private static final byte NEW = 3;

    private static final String SAVE = "Save";
    private static final String GAME = "Game";
    private static final String RES  = "res://";


    private Screen introForm;

    private Screen systemForm;

    private boolean running;
    private boolean gameOver;
    private String currentRoomFile;
    private Room currentRoom;
    private Thread thread;

    private PlayerMind playerMind;

    private String roomToLoad;
    private byte requestType;

    private String deathMessage;

    private byte[][] imageLocations;
    private Image sourceImage;
    private Hashtable currentCommands;
    private Hashtable commandCache;
    private Hashtable localRoomIds;
    private PlayerMind currentPlayer;
    private boolean pausedAndSaved;

    private Command systemGameCommand;

    public void startApp()
        throws MIDletStateChangeException
    {
        try
        {
            Image image = Image.createImage("/images/wad.png");
            byte[][] images = getImages();


            createIntroForm();

            this.imageLocations = images;
            this.sourceImage = image;
            this.currentCommands = new Hashtable(10);
            this.commandCache = new Hashtable(20);
            this.localRoomIds = new Hashtable(30);

            this.systemGameCommand = new Command("System", Command.SCREEN, 100);
            // add command to exit game (always available)
            this.addCommand(this.systemGameCommand);
            this.playerMind = new PlayerMind(this, null);

            if(this.pausedAndSaved)
            {
                load();
                this.pausedAndSaved = false;
            }else{
                Display.getDisplay(this.midlet).setCurrent(this.introForm);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new MIDletStateChangeException(ex.toString());
        }
    }

    public void pauseApp()
    {
        // save the current game
        if(this.currentRoom != null)
        {
            this.pausedAndSaved = true;
            Room saveRoom = this.currentRoom;
            this.running = false;
            save(saveRoom);
            cleanup();
        }
    }

    public void destroyApp(boolean b)
    {
        this.running = false;
        cleanup();
    }

    private final void cleanup()
    {
        this.currentRoom = null;
        unloadActions();
        // unload the system action
        this.removeCommand(this.systemGameCommand);
        this.imageLocations = null;
        //this.currentCommands = null;
        this.commandCache = null;
        this.localRoomIds = null;
        this.sourceImage = null;
        this.mode = AC_NONE;
    }

    public final void run()
    {
        try
        {
            while (this.running)
            {
                if (this.roomToLoad != null)
                {
                    this.loadRoomFile(this.roomToLoad, this.requestType);
                    this.roomToLoad = null;
                }

                this.currentRoom.updateNext();
                if (this.gameOver)
                {
                    // update once more to display the win message
                    this.currentRoom.updateNext();
                    this.running = false;
                }
            }
            // wait until the user presses a button before quitting
            if(this.deathMessage != null)
            {
                if(this.mode != WIN)
                {
                    this.mode = LOSE;
                }
                this.addMessage(this.deathMessage);
                this.repaint();
            }else{
                quit(!this.pausedAndSaved);
            }
        }
        catch (Exception ex)
        {
            fatalError("Error", ex.getMessage());
            ex.printStackTrace();
        }
        finally
        {
            this.running = false;
            this.thread = null;
        }
    }

    private final byte[][] getImages()
            throws IOException
    {
        DataInputStream ins = null;
        try
        {
            InputStream is = this.getClass().getResourceAsStream("/images/wad.map");
            ins = new DataInputStream(is);
            int max = ins.readByte();
            int size = ins.readByte();
            byte[][] images = new byte[max][];
            for (int i = 0; i < size; i++)
            {
                byte key = ins.readByte();
                int length = ins.readByte();
                byte[] value = new byte[length];
                for (int j = 0; j < length; j++)
                {
                    byte val = ins.readByte();
                    value[j] = val;
                }
                images[key] = value;
            }
            return images;
        }
        finally
        {
            if (ins != null)
            {
                ins.close();
            }
        }
    }

    public final void showActions(PlayerMind mind, Entity entity)
    {
        // show the actions available for the entity
        Vector actions = entity.actions;
        unloadActions();

        // create a command for every action
        if (actions != null)
        {
            for (int i = 0; i < actions.size(); i++)
            {
                Action action = (Action) actions.elementAt(i);
                addCommand(action.getName(), action, entity, action.getTotalSteps()+2);
            }
        }
        this.currentPlayer = mind;
        // check that the entity isn't standing on anything that might allow it to perform additional
        // actions (eg. chests, stairs, fountains, etc...)
        Vector standingOn = ((Room) entity.container).getEntitiesAt(entity.x, entity.y);
        if (standingOn != null)
        {
            for (int i = 0; i < standingOn.size(); i++)
            {
                Entity e = (Entity) standingOn.elementAt(i);
                if (e != entity && e.entityType == Entity.INHERIT_TYPE)
                {
                    // can inherit this objects actions just by standing on it
                    actions = e.actions;
                    if (actions != null)
                    {
                        for (int j = 0; j < actions.size(); j++)
                        {
                            Action action = (Action) actions.elementAt(j);
                            String name = action.getName() + "-"+e.name;
                            addCommand(name, action, entity, action.getTotalSteps()+2);
                        }
                    }
                }
            }
        }
        // check that the entity hasn't inherited any actions from its items
        Vector items = entity.getContainedEntities();
        if (items != null)
        {
            for (int i = 0; i < items.size(); i++)
            {
                Entity item = (Entity) items.elementAt(i);
                actions = item.actions;
                // only inherit from items, sometimes we'll have other things in our inventory (like monsters we've eaten!)
                if (actions != null && item.entityType == Entity.ITEM_TYPE)
                {
                    for (int j = 0; j < actions.size(); j++)
                    {
                        Action action = (Action) actions.elementAt(j);
                        String name = action.getName() + "-" + item.name;
                        addCommand(name, action, entity, action.getTotalSteps()+2);
                    }
                }
            }
        }

        this.setCommandListener(this);
        if(this.mode == NONE || !this.isRunning())
        {
            if (entity.doing != null && !entity.doing.isCompleted())
            {
                this.setCurrentController(entity.doing);
            }
            else
            {
                Action moveAction = this.currentPlayer.getEntity().getAction(MoveAction.NAME);
                if (moveAction != null && this.currentPlayer.getEntity().unitsRemaining >= moveAction.getTotalSteps())
                {
                    setCurrentController(moveAction);
                }else{
                    setCurrentController(null);
                }
            }
        }
    }

    private final void addCommand(String name, Action action, Entity entity, int basePriority)
    {
        // TODO : reuse commands
        if (action.isVisible())
        {
            Command command;
            String availability = action.getAvailability(entity);
            String description = name;
            int priority;
            if (action instanceof MoveAction)
            {
                priority = basePriority + 1;
            }
            else
            {
                priority = basePriority + 3;
            }
            if (action.getTotalSteps() > 0)
            {
                description += " (" + Integer.toString(action.getTotalSteps()) + ")";
            }
            if (action instanceof AttackAction && (((AttackAction)action).flags & AttackAction.GOOD_BIT) == 0)
            {
                description += "†";
            }
            if (availability != null)
            {
                description += "ø";
            }
            command = (Command) this.commandCache.get(description);
            if (command == null)
            {
                // NOTE : sometimes there will be two commands with the same description
                // of course we may only want to present it once anyway?
                command = new Command(description, Command.SCREEN, priority);
                this.commandCache.put(description, command);
            }

            this.addCommand(command);
            this.currentCommands.put(command, action);
        }
    }

    private final void unloadActions()
    {
        if (this.currentCommands.size() != 0)
        {
            Enumeration commands = this.currentCommands.keys();
            while (commands.hasMoreElements())
            {
                Command command = (Command) commands.nextElement();
                //this.actionList.removeCommand(command);
                this.removeCommand(command);
            }
            this.currentCommands.clear();
            //this.canvas.setCommandListener(null);
        }
    }

    public final void commandAction(Command command, Displayable displayable)
    {
        try
        {
            if (command == this.systemGameCommand)
            {
                Display.getDisplay(this.midlet).setCurrent(this.systemForm);
            }
            else
            {
                Action action;
                action = (Action) this.currentCommands.get(command);
                if (action == null)
                {
                    if (displayable == this.introForm)
                    {
                        // find out the selected item
                        int index = ((List) this.introForm).getSelectedIndex();
                        switch (index)
                        {
                            case 0:
                                this.loadRoomFile("res:///rooms/start.room", NEW);
                                break;
                            case 1:
                                // load the saved game
                                load();
                                break;
                        }
                    }
                    else if (displayable == this.systemForm)
                    {
                        int index = ((List)this.systemForm).getSelectedIndex();
                        switch(index)
                        {
                            case 1:
                                // center the screen on the current entity
                                pan(PAN_ENTITY);
                            case 0:
                                Display.getDisplay(this.midlet).setCurrent(this);
                                break;
                            case 2:
                                save(this.currentRoom);
                                // allow it to fall through to the intro form
                            case 3:
                                quit(true);
                                break;
                        }
                    }
                }
                if (action != null)
                {
                    setCurrentController(action);
                }
                /*
                else if (command.getLabel() != null && command.getLabel().length() > 0)
                {
                    //System.out.println("WARNING : no action for "+command.getLabel());
                    fatalError(command.getLabel(), "no such controller");
                }
                */
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fatalError(command.getLabel(), ex.getClass().getName() + ":" + ex.getMessage());
        }
    }

    private final void quit(boolean toIntro)
    {
        this.running = false;
        this.gameOver = false;
        this.stop();
        this.currentRoom = null;
        this.mode = AC_NONE;
        // delete the game file
        /* no need to do this, the game files are deleted on starting a new game
        try
        {
            RecordStore.deleteRecordStore(GAME);
            this.localRoomIds.clear();
        }catch(Exception ex){
            // do nothing
            ex.printStackTrace();
        }
        */
        if(toIntro)
        {
            // if it's paused and saved, we don't want this coming back to haunt us
            Display.getDisplay(this.midlet).setCurrent(this.introForm);
        }
    }

    private void fatalError(String doing, String message)
    {
        Alert alert = new Alert(doing, message, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        Display display = Display.getDisplay(this.midlet);
        display.setCurrent(alert, this.introForm);
        quit(false);
    }

    private final void loadRoomFile(String roomFile, byte requestType)
        throws Exception
    {
        loadCurrentRoomLocal(roomFile, requestType);
        this.currentRoomFile = roomFile;
    }

    private final void loadCurrentRoomLocal(String roomName, byte requestType)
        throws Exception
    {
        //Room oldRoom = this.currentRoom;
        String oldRoomFile = this.currentRoomFile;
        RecordStore recordStore = null;
        try
        {
            Vector leaving = null;
            if(requestType == NEW)
            {
                if(this.localRoomIds.size() > 0)
                {
                    this.localRoomIds.clear();
                }
                try
                {
                    RecordStore.deleteRecordStore(GAME);
                }catch(Exception ex){
                    // assume that the record store just doesn't exist
                }
            }else if(this.currentRoom != null){
                recordStore = RecordStore.openRecordStore(GAME, true);
                // find any exits leaving this room going to the new room and remove the entities from them
                leaving = (Vector)this.currentRoom.exits.get(roomName);
                if(leaving != null)
                {
                    this.currentRoom.exits.remove(roomName);
                    // ensure that the leaving entities don't have a reference to the room
                    for(int i=0; i<leaving.size(); i++)
                    {
                        Entity l = (Entity)leaving.elementAt(i);
                        l.container = null;
                        l.thoughts = null;
                    }
                }
                // store the current room, if it isn't recurring
                if(this.currentRoom.flags == 0)
                {
                    // we've already welcomed them once, that's all they get
                    this.currentRoom.welcomeMessage = null;

                    byte[] oldRoomBytes = getBytes(this.currentRoom, null, null);
                    Integer oldRoomId = (Integer)this.localRoomIds.get(this.currentRoomFile);
                    if(oldRoomId == null)
                    {
                        oldRoomId = new Integer(recordStore.addRecord(oldRoomBytes, 0, oldRoomBytes.length));
                        this.localRoomIds.put(this.currentRoomFile, oldRoomId);
                    }else{
                        recordStore.setRecord(oldRoomId.intValue(), oldRoomBytes, 0, oldRoomBytes.length);
                    }
                }
            }else{
                leaving = null;
            }
            // allow the old room to be garbage collected before we load the new room
            if(this.currentRoom != null)
            {
                // TODO : setting some of these to NULL is not required
                this.currentRoom = null;
                this.attack = null;
                this.action = null;
                this.currentPlayer = null;
                this.currentEntity = null;
                this.entity = null;
                Runtime.getRuntime().gc();
            }
            // load the new room
            Integer newRoomId = (Integer)this.localRoomIds.get(roomName);
            Room newRoom;
            DataInputStream dis;
            if(newRoomId == null)
            {
                // obtain the room locally
                String roomResource = roomName.substring(RES.length());
                // load it from the resource file
                dis = new DataInputStream(this.getClass().getResourceAsStream(roomResource));
            }else{
                // load it from the record store
                byte[] newRoomBytes = recordStore.getRecord(newRoomId.intValue());
                dis = new DataInputStream(new ByteArrayInputStream(newRoomBytes));
            }
            newRoom = Room.create(this.playerMind, dis);
            dis.close();
            // add in the entities from the exit at the entrance
            // find the entrance from the old room
            Vector entities = newRoom.getContainedEntities();

            // put the entities in the middle if we can't find the exit
            int lx = newRoom.width/2;
            int ly = newRoom.height/2;
            for(int i=0; i<entities.size(); i++)
            {
                Entity entity = (Entity)entities.elementAt(i);
                SimpleAction exit = entity.getSimpleAction(SimpleAction.EXIT);
                if(exit != null)
                {
                    if(exit.exitName.equals(oldRoomFile))
                    {
                        // we've found the exit/entrance, add in the exiting entities from before
                        lx = entity.x;
                        ly = entity.y;
                        break;
                    }
                }
            }
            if(leaving != null)
            {
                // TODO : spread them out
                for(int j=0; j<leaving.size(); j++)
                {
                    Entity l = (Entity)leaving.elementAt(j);
                    l.x = (byte)lx;
                    l.y = (byte)ly;
                    l.unitsRemaining = l.speed;
                    l.setFlag(Entity.DONE_FLAG, false);
                    l.setFlag(Entity.DEAD_FLAG, false);
                    l.doing = null;
                    l.parent = null;
                    l.reaction = null;
                    l.thoughts = null;
                    newRoom.addContainedEntity(l);
                }
            }
            //verifyRoom(newRoom);
            setCurrentRoom(newRoom);
        }finally{
            if(recordStore != null)
            {
                recordStore.closeRecordStore();
            }
        }
    }
    /*
    private void verifyRoom(Room room)
    {
        Vector entities = room.getContainedEntities();
        verifyEntities(entities, room);
        Enumeration keys = room.exits.keys();
        while(keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Vector exiting = (Vector)room.exits.get(key);
            verifyEntities(exiting, room);
        }
    }

    private void verifyEntities(Vector entities, Room room)
    {
        for(int i=0; i<entities.size(); i++)
        {
            Entity entity = (Entity)entities.elementAt(i);
            verifyEntity(entity, room);
        }

    }

    private void verifyEntity(Entity entity, Room room)
    {
        if(entity != null)
        {
            if(entity.container != room && entity.container != null)
            {
                throw new RuntimeException("The entity "+entity.name+" has is in "+entity.container.name+
                        " expected "+room.name);
            }
            if(entity.thoughts instanceof Entity)
            {
                verifyEntity((Entity)entity.thoughts, room);
            }else if(entity.thoughts instanceof Action){
                verifyEntity(((Action)entity.thoughts).getParent(), room);
            }
            Vector actions = entity.actions;
            if(actions != null)
            {
                for(int i=0; i<actions.size(); i++)
                {
                    Action action = (Action)actions.elementAt(i);
                    verifyAction(action, entity, room);
                }
            }
        }
    }

    private void verifyAction(Action action, Entity owner, Room room)
    {
        if(action.getParent() != null && action.getParent() != owner)
        {
            throw new RuntimeException("The action "+action.getName()+" has the wrong owner. Expected "+
                    owner.name+" found "+action.getParent().name);
        }
        if(action instanceof SimpleAction)
        {
            SimpleAction simple = (SimpleAction)action;
            if(simple.item instanceof Entity && simple.item != owner)
            {
                verifyEntity((Entity)simple.item, room);
            }else if(simple.item instanceof Action){
                verifyAction((Action)simple.item, owner, room);
            }
        }else if(action instanceof AttackAction){
            AttackAction attack = (AttackAction)action;
            if(attack.confers != null)
            {
                verifyAction(attack.confers, owner, room);
            }
        }
    }
    */
    private final void setCurrentRoom(Room currentRoom)
    {
        this.clear();
        this.currentRoom = currentRoom;
        // queue the room
        this.setRoom(currentRoom);
        this.mode = AC_NONE;
        // empty the command cache, we've got to free that memory up, since it may have grown, we'll just create a new
        // one
        this.commandCache = new Hashtable(20);
        if (this.currentRoom != null)
        {
            this.currentRoom.queueEntities(this.currentRoom.getContainedEntities());
            this.currentRoom.addRoomListener(this);

            if (!this.running)
            {
                // run the loop
                this.roomToLoad = null;
                this.running = true;
                this.thread = new Thread(this);
                this.thread.start();
            }
            Display.getDisplay(this.midlet).setCurrent(this);
            if (this.currentRoom.welcomeMessage != null)
            {
                this.addMessage(this.currentRoom.welcomeMessage);
            }
        }
    }

    private final void checkGameStatus(Entity entity, Action action)
    {
        // find the player(s)
        if (entity.side != Room.PLAYER_SIDE)
        {
            // make sure that the player controls are disabled
            unloadActions();
            this.controllerDone();
        }

        // particularly interested in events where the players avatar
        // exits the room
        if (entity.isFlag(Entity.BOSS_FLAG) && entity.side == Room.PLAYER_SIDE && action instanceof SimpleAction)
        {
            SimpleAction exit = (SimpleAction) action;
            if (exit.getMode() == SimpleAction.EXIT)
            {
                // we're out of here
                /*
                try
                {
                    this.loadRoomFile(exit.exitName, ENTER);
                }catch(Exception ex){
                    ex.printStackTrace();
                    fatalError("Exiting Room", "couldn't leave via exit: "+exit.exitName);
                }
                */
                this.roomToLoad = exit.exitName;
                this.requestType = LOAD;
            }
        }

        if (action instanceof SimpleAction)
        {
            SimpleAction win = (SimpleAction) action;
            if (win.getMode() == SimpleAction.ROOM_WIN)
            {
                // the game is over
                String message;
                this.mode = WIN;

                if (entity != null && entity.side == Room.PLAYER_SIDE)
                {
                    // show the room win message
                    message = this.currentRoom.winMessage;
                }
                else
                {
                    // show the room lose message
                    message = this.currentRoom.loseMessage;
                }
                message = SimpleAction.format(message, entity.name, win.getName(), null);
                this.deathMessage = message;
                //this.currentRoom.addMessage(message);
                this.currentRoom.winner = entity;
                gameOver();
            }else if(win.getMode() == SimpleAction.ROOM_LOSE){
                String message;
                if(win.convertSide == Room.PLAYER_SIDE)
                {
                    message = this.currentRoom.loseMessage;
                    message = SimpleAction.format(message, entity.name, win.getName(), null);

                    // format up a neat death message

                    Entity killer;
                    Action finalStrike = (Action)entity.thoughts;
                    if(finalStrike != null)
                    {
                        killer = finalStrike.getParent();
                        while(killer.parent != null)
                        {
                            killer = killer.parent;
                        }
                    }else{
                        killer = null;
                    }
                    String deathDescription;
                    if(entity.doing instanceof SimpleAction)
                    {
                        SimpleAction simple = (SimpleAction)entity.doing;
                        switch(simple.getMode())
                        {
                            default:
                            case SimpleAction.DIE:
                                // TODO : use the die action name (killed, beheaded, fatally poisoned)
                                deathDescription = "killed";
                                break;
                            case SimpleAction.POLYMORPH:
                                // turned into an inanimate object
                                // TODO : assumes that it's stone
                                deathDescription = "turned to stone";
                                break;
                            case SimpleAction.STONE:
                                // TODO : use 'a','an' or 'the' when appropriate
                                deathDescription = "turned into the "+((Entity)simple.item).name;
                                break;
                            case SimpleAction.SWALLOW:
                                deathDescription = "eaten";
                                break;
                            case SimpleAction.CONVERT:
                                deathDescription = "brain washed";
                                break;
                        }
                    }else{
                        deathDescription = "killed";
                    }
                    String deathMessage;
                    if(killer == null)
                    {
                        deathMessage = "The "+entity.name+" was "+deathDescription;
                    }else if(killer == entity){
                        deathMessage = "The "+entity.name+" committed suicide by being "+deathDescription;
                    }else{
                        deathMessage = "The "+entity.name+" was "+deathDescription+" by the "+killer.name+" in "+killer.container.name;
                    }
                    this.deathMessage = deathMessage;

                    this.currentRoom.addMessage(message);
                    gameOver();
                }
            }
        }
    }

    private final void gameOver()
    {
        this.gameOver = true;
        // set all the player minds to brainless/AI players
        this.currentRoom.setPlayerMind(new MonsterAction(null));
    }

    private final void setCurrentController(Action action)
    {
        clear();
        resetAction(action);
        repaint();
        //Display.getDisplay(this.midlet).setCurrent(this);
        start(this.currentPlayer.getEntity());
        if (!isRunning())
        {
            this.mode = AC_NONE;
        }
    }

    private final void actionEvent(Action action, String message, boolean step, boolean end)
    {
        if (step)
        {
            // the player has done something, presumably this means that they have read
            // the messages
            this.clearMessages();
            if (this.currentPlayer != null)
            {
                this.currentPlayer.setAction(action);
            }
        }
        if (end)
        {
            controllerDone();
        }
        if (message != null)
        {
            this.addMessage(message);
            this.repaint();
        }
    }

    private final void controllerDone()
    {
        if (this.mode != AC_NONE)
        {
            this.mode = AC_NONE;
            this.stop();
            if (this.currentPlayer.getEntity().isFlag(Entity.DONE_FLAG))
            {
                // clear the available options
                unloadActions();
            }
        }
    }

    private final byte[] getPosition(byte key)
    {
        return this.imageLocations[key];
    }

    private final byte[] render(Graphics g, byte key, int x, int y, boolean centre)
    {
        return render(g, getPosition(key), x, y, centre);
    }

    private static final int convert(byte b)
    {
        int result;
        if (b < 0)
        {
            result = Byte.MAX_VALUE - b;
        }
        else
        {
            result = b;
        }
        return result;
    }

    private final byte[] render(Graphics g, byte[] position, int x, int y, boolean centre)
    {
        if (position != null)
        {
            int px = convert(position[0]);
            int py = convert(position[1]);
            int pw = position[2];
            int ph = position[3];
            int tlx;
            int tly;
            int cx;
            int cy;
            if (centre)
            {
                //g.setClip(x - pw / 2, y - ph / 2, pw, ph);
                //g.drawImage(this.sourceImage, x - px - pw / 2, y - py - ph / 2, Graphics.TOP | Graphics.LEFT);
                cx = x - pw/2;
                cy = y - ph/2;
                tlx = x - px - pw/2;
                tly = y - py - ph/2;
            }
            else
            {
                //g.setClip(x, y, pw, ph);
                //g.drawImage(this.sourceImage, x - px, y - py, Graphics.TOP | Graphics.LEFT);
                cx = x;
                cy = y;
                tlx = x - px;
                tly = y - py;
            }
            // maybe this is quicker than letting the drawImage method work it out?
            if(cx < this.getWidth() && cy < this.getHeight() && (cx + pw)>0 && (cy + ph)>0)
            {
                g.setClip(cx, cy, pw, ph);
                g.drawImage(this.sourceImage, tlx, tly, Graphics.TOP | Graphics.LEFT);
            }
        }
        return position;
    }

    private final void createIntroForm()
    {
        String name = this.midlet.getAppProperty(GAME);
        String beginQuest = this.midlet.getAppProperty("Q");
        String loadSaved = this.midlet.getAppProperty(SAVE);

        List introList = new List(name, Choice.IMPLICIT);
        introList.append(beginQuest, null);
        introList.append(loadSaved, null);

        createSystemForm();

        introList.setCommandListener(this);
        this.introForm = introList;
    }


    private final void createSystemForm()
    {
        List systemList = new List("System", Choice.IMPLICIT);

        systemList.append("Back", null);
        systemList.append("Center", null);
        systemList.append("Save", null);
        systemList.append("Quit", null);

        systemList.setCommandListener(this);

        this.systemForm = systemList;
    }

    private final int getRecordId(RecordStore record)
        throws RecordStoreException
    {
        int recordId = -1;
        RecordEnumeration enum = record.enumerateRecords(null, null, false);
        while(enum.hasNextElement())
        {
            // should be only ever one record
            recordId = enum.nextRecordId();
        }
        return recordId;
    }

    private final void load()
    {
        RecordStore record = null;
        try
        {
            record = RecordStore.openRecordStore(SAVE, true);
            int recordId = getRecordId(record);
            if(recordId >= 0)
            {
                ByteArrayInputStream bis = new ByteArrayInputStream(record.getRecord(recordId));
                DataInputStream dis = new DataInputStream(bis);
                // read the current room file name
                this.currentRoomFile = Room.readString(dis);
                // load the indicies
                int size = dis.readByte();
                Hashtable indicies = new Hashtable(size);
                for(int i=0; i<size; i++)
                {
                    String key = Room.readString(dis);
                    Integer index = new Integer(dis.readByte());
                    indicies.put(key, index);
                }
                this.localRoomIds = indicies;
                Room room = Room.create(this.playerMind,  dis);
                dis.close();
                record.deleteRecord(recordId);
                this.setCurrentRoom(room);
            }else{
                fatalError("Loading", "There is no save game");
            }
            record.closeRecordStore();
            RecordStore.deleteRecordStore(SAVE);
        }catch(Exception ex){
            ex.printStackTrace();
            fatalError("Error", ex.getMessage());
        }finally{
            try
            {
                if(record != null)
                {
                    record.closeRecordStore();
                }
            }catch(Exception ex){
                // do nothing
                ex.printStackTrace();
            }
        }
    }

    private final void save(Room room)
    {
        RecordStore record = null;
        try
        {
            record = RecordStore.openRecordStore(SAVE, true);
            byte[] bytes = getBytes(room, this.localRoomIds, this.currentRoomFile);
            int recordId = getRecordId(record);
            if(recordId >= 0)
            {
                record.setRecord(recordId, bytes, 0, bytes.length);
            }else{
                record.addRecord(bytes, 0, bytes.length);
            }


        }catch(Exception ex){
            ex.printStackTrace();
            fatalError("Error", ex.getMessage());
        }finally{
            try
            {
                if(record != null)
                {
                    record.closeRecordStore();
                }
            }catch(Exception ex){
                // do nothing
                ex.printStackTrace();
            }
        }
    }

    private static final byte[] getBytes(Room room, Hashtable indicies, String currentRoomFile)
        throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        if(indicies != null)
        {
            Room.writeString(dos, currentRoomFile);
            // store the indicies with the room
            dos.writeByte((byte)indicies.size());
            Enumeration keys = indicies.keys();
            while(keys.hasMoreElements())
            {
                String key = (String)keys.nextElement();
                Integer index = (Integer)indicies.get(key);
                Room.writeString(dos, key);
                dos.writeByte((byte)index.intValue());
            }
        }

        Room.store(room, dos);
        dos.close();
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    //
    // from action controller
    //

    // from attack
    private static final int LINE_LENGTH = 6;

    // from move
    private static boolean TILT_DOWN;

    private static final byte SIMPLE     = 0;
    private static final byte MOVE       = 1;
    private static final byte ATTACK     = 2;
    private static final byte WIN        = 3;
    private static final byte LOSE       = 4;
    private static final byte WAITING    = 5;
    private static final byte AC_NONE    = 6;

    private byte mode;

    // from action controller
    private boolean ac_running;
    private Action action;

    // from move
    private Entity entity;

    // from attack
    private byte ac_x;
    private byte ac_y;
    private byte targetIndex;
    private boolean info;

    private void resetAction(Action action)
    {
        this.action = action;
        if(action instanceof MoveAction)
        {
            this.mode = MOVE;
        }else if(action instanceof AttackAction){
            this.mode = ATTACK;
        }else if(action instanceof SimpleAction){
            this.mode = SIMPLE;
        }else{
            this.mode = WAITING;
        }
    }

    private void clear()
    {
        this.entity = null;
        this.action = null;
        this.info = false;
        this.ac_running = false;
        this.targetIndex = 0;
        this.mode = WAITING;
    }

    private void start(Entity entity)
    {
        switch(this.mode)
        {
            case SIMPLE:
                simpleStart(entity);
                break;
            case MOVE:
                moveStart(entity);
                break;
            case ATTACK:
                attackStart(entity);
                break;
            case WAITING:
                // we are a very inactive controller
                this.ac_running = true;
                break;
        }
    }

    private final void simpleStart(Entity entity)
    {
        this.setAction(entity, this.action.copy(entity), true);
    }

    private final void moveStart(Entity entity)
    {
        this.ac_running = true;
        this.entity = entity;
    }

    private final void attackStart(Entity entity)
    {
        this.ac_running = true;
        this.entity = entity;
        this.ac_x = entity.x;
        this.ac_y = entity.y;
        this.repaint();
        this.targetIndex = (byte)entity.container.getContainedEntities().indexOf(entity);
        if(this.targetIndex < 0)
        {
            this.targetIndex = 0;
        }
        AttackAction attack = (AttackAction)this.action;
        if(attack.range > 0)
        {
            //Room room = (Room)entity.container;
            // target an entity in range
            this.attackKeyPressed(this.getKeyCode(Canvas.GAME_C));
            /*
            target the closest entity in range - nope
            Vector contained = room.getContainedEntities();
            int hsq = Integer.MAX_VALUE;
            for(int i=0; i<contained.size(); i++)
            {
                Entity e = (Entity)contained.elementAt(i);
                if(e.entityType == Entity.MONSTER_TYPE && e.side != entity.side )
                {
                    if(AttackAction.inRange(e.x, e.y, entity.x, entity.y, attack.range))
                    {
                        Entity b = AttackAction.getBlocker(e.x, e.y, entity.x, entity.y, room);
                        if(b == null)
                        {
                            int dx = e.x - entity.x;
                            int dy = e.y - entity.y;
                            int ehsq = dx*dx + dy*dy;
                            if(ehsq < hsq)
                            {
                                this.targetIndex = (byte)i;
                                hsq = ehsq;
                                this.ac_x = e.x;
                                this.ac_y = e.y;
                            }
                        }
                    }
                }
            }
            */
        }else{
            // if it's got 0-range we'll use it immediately
            this.ac_x = entity.x;
            this.ac_y = entity.y;
            createAction();
        }
    }

    private void stop()
    {
        this.ac_running = false;
    }

    private boolean isRunning()
    {
        return this.ac_running;
    }

    private void render(Graphics g)
    {
        switch(this.mode)
        {
            case ATTACK:
                attackRender(g);
                break;
            case WIN:
                winRender(g);
                break;
            case LOSE:
                loseRender(g);
                break;
            case MOVE:
                moveRender(g);
        }
    }


    private static final byte[][][] MOVE_DATA = {{{119, 123, -1, -1, 10, 17},
                                                  {120, 124, 1, 1, 10, 3},
                                                  {121, 125, -1, 1, 18, 10},
                                                  {122, 126, 1,-1, 2, 10}},
                                                 {{45, 99, 0, -1, 6, 14},
                                                  {104, 71, 0, 1, 14, 6},
                                                  {100, 70, -1, 0, 14, 14},
                                                  {105, 79, 1, 0, 5, 6}}
    };


    private void moveRender(Graphics g)
    {
        // draw the move icon
        byte x = this.entity.x;
        byte y = this.entity.y;
        int w = this.getWidth();
        int h = this.getHeight();


        int select;
        if(TILT_DOWN)
        {
            select = 0;
        }else{
            select = 1;
        }
        for(int i=0; i<4; i++)
        {
            int tx = x + MOVE_DATA[select][i][2];
            int ty = y + MOVE_DATA[select][i][3];
            int moveMode = getMoveMode(tx, ty);
            if(moveMode >= 0)
            {
                byte key = MOVE_DATA[select][i][moveMode];
                int dx = MOVE_DATA[select][i][4];
                int dy = MOVE_DATA[select][i][5];
                this.render(g, key, w-dx, h-dy, true);
            }
        }
        byte key = 35;
        Vector center = this.currentRoom.getEntitiesAt(x, y);
        for(int i=0; i<center.size(); i++)
        {
            Entity entity = (Entity)center.elementAt(i);
            if(entity.entityType == Entity.ITEM_TYPE)
            {
                key = 32;
            }else if(entity.entityType == Entity.MONSTER_TYPE &&
                    !this.currentRoom.isFriendly(entity.side, this.entity.side)){
                // it's a monster!
                key = 34;
                break;
            }
        }

        this.render(g, key, w-10, h-10, true);
    }

    private int getMoveMode(int x, int y)
    {
        int moveMode;
        if(this.currentRoom.getObstacle(x, y) == 0)
        {
            // we can move in this direction
            Vector entities = this.currentRoom.getEntitiesAt(x, y);
            moveMode = 0;
            if(entities != null)
            {
                for(int i=0; i<entities.size(); i++)
                {
                    Entity entity = (Entity)entities.elementAt(i);
                    if(entity.entityType == Entity.MONSTER_TYPE &&
                            !this.currentRoom.isFriendly(this.entity.side, entity.side))
                    {
                        moveMode = 1;
                        break;
                    }
                }
            }
            if(moveMode == 0 && this.currentRoom.getTile(x, y) == 0 &&
                (this.entity.entityCategory & Entity.CATEGORY_FLYING) == 0)
            {
                moveMode = -1;
            }
        }else{
            moveMode = -1;
        }
        return moveMode;
    }

    private void winRender(Graphics g)
    {
        int w = this.getWidth();
        int h = this.getHeight();
        g.setColor(0x6060BB);
        g.fillRect(0, 0, w, h);
        g.setColor(0x40BB40);
        g.fillRect(0, h-5, w, 5);
        g.setColor(0x000000);
        g.drawLine(0, h-5, w, h-5);

        if(this.currentRoom.winner != null)
        {

            Random random = new Random(this.currentRoom.width + this.currentRoom.height);
            byte[] treePosition = this.getPosition((byte)20);
            byte[] bushPosition = this.getPosition((byte)18);
            int times = Math.abs(random.nextInt() % 30);
            for(int i=0; i<times; i++)
            {
                byte[] position;
                boolean tree = random.nextInt() > 0;
                if(tree)
                {
                    position = treePosition;
                }else{
                    position = bushPosition;
                }
                int x = Math.abs(random.nextInt()%(w-position[2]));
                this.render(g, position, x, h-5-position[3], false);
            }
            this.render(g, treePosition, w/2-treePosition[2], h-5-treePosition[3], false);

            byte[] position = this.getPosition(this.currentRoom.winner.entityTypeId);
            this.render(g, position, (w-position[2])/2, h-5-position[3], false);
            // render the items in the entitys posession
            Vector contained = this.currentRoom.winner.getContainedEntities();
            if(contained != null)
            {
                int length = 0;
                for(int i=0; i<contained.size(); i++)
                {
                    Entity item = (Entity)contained.elementAt(i);
                    position = this.getPosition(item.entityTypeId);
                    length += position[2];
                }
                int x = (w - length)/2;
                for(int i=0; i<contained.size(); i++)
                {
                    Entity item = (Entity)contained.elementAt(i);
                    position = this.getPosition(item.entityTypeId);
                    this.render(g, position, x, h-5-position[3], false);
                    x += position[2];
                }
            }
        }
    }


    private void loseRender(Graphics g)
    {
        int w = this.getWidth();
        int h = this.getHeight();
        g.setColor(0x303030);
        g.fillRect(0, 0, w, h);
        byte[] position = this.getPosition((byte)46);
        this.render(g, position, w/2, h-position[3], true);
    }

    private void attackRender(Graphics g)
    {
        Vector entities = ((Room)this.entity.container).getEntitiesAt(this.ac_x, this.ac_y);
        int[] point = this.getPoint(this.ac_x, this.ac_y);
        int color;
        boolean baddy = false;
        int lx = this.getWidth();
        int ly = this.getHeight();
        if(entities != null)
        {
            for(int i=0; i<entities.size(); i++)
            {
                Entity found = (Entity)entities.elementAt(i);
                if(found.side != 0 && found.side != entity.side)
                {
                    baddy = true;
                }
                if(found.name != null)
                {
                    g.setColor(0xFFFFFF);
                    g.setFont(DuelsCanvas.FONT);
                    g.drawString(found.name, lx, ly, Graphics.RIGHT | Graphics.BOTTOM);
                    ly -= g.getFont().getHeight();
                }
            }
        }
        if(AttackAction.inRange(this.ac_x, this.ac_y, this.entity.x, this.entity.y, ((AttackAction)this.action).range))
        {
            if(AttackAction.getBlocker(this.ac_x, this.ac_y, this.entity.x, this.entity.y, (Room)this.entity.container) != null)
            {
                if(baddy)
                {
                    color = 0xFF00FF;
                }else{
                    color = 0x0000FF;
                }
            }else if(baddy){
                color = 0xFF0000;
            }else{
                color = 0x00FF00;
            }
        }else{
            if(baddy)
            {
                color = 0xFFFF00;
            }else{
                color = 0x808080;
            }
        }
        g.setColor(color);
        g.drawArc(point[0] - DuelsCanvas.DEFAULT_TILE_WIDTH/2, point[1]-DuelsCanvas.DEFAULT_TILE_WIDTH,
                DuelsCanvas.DEFAULT_TILE_WIDTH, DuelsCanvas.DEFAULT_TILE_WIDTH, 0, 360);
        g.drawLine(point[0] - DuelsCanvas.DEFAULT_TILE_WIDTH/2 - LINE_LENGTH/2,
                   point[1] - DuelsCanvas.DEFAULT_TILE_WIDTH/2,
                   point[0] - DuelsCanvas.DEFAULT_TILE_WIDTH/2 + LINE_LENGTH/2,
                   point[1] - DuelsCanvas.DEFAULT_TILE_WIDTH/2);
        g.drawLine(point[0],
                   point[1] - DuelsCanvas.DEFAULT_TILE_WIDTH - LINE_LENGTH/2,
                   point[0],
                   point[1] - DuelsCanvas.DEFAULT_TILE_WIDTH + LINE_LENGTH/2);
        g.drawLine(point[0] + DuelsCanvas.DEFAULT_TILE_WIDTH/2 - LINE_LENGTH/2,
                   point[1] - DuelsCanvas.DEFAULT_TILE_WIDTH/2,
                   point[0] + DuelsCanvas.DEFAULT_TILE_WIDTH/2 + LINE_LENGTH/2,
                   point[1] - DuelsCanvas.DEFAULT_TILE_WIDTH/2);
        g.drawLine(point[0],
                   point[1] - LINE_LENGTH/2,
                   point[0],
                   point[1] + LINE_LENGTH/2);
        // lay down some info
        if(this.info)
        {
            // display information about the selected entity
            int w = this.getHeight();
            int y = 2;
            int x = w - 4;
            boolean leftJustify = false;
            for(int i=0; i<entities.size(); i++)
            {
                Entity found = (Entity)entities.elementAt(i);
                y = renderEntity(g, found, x, y, leftJustify);
                if(y < 0)
                {
                    y = 2 - y;
                    x = 2;
                    leftJustify = true;
                }else{
                    y+=2;
                }
            }
        }
    }

    private void renderUnderTarget(Graphics g)
    {
        switch(this.mode)
        {
            case ATTACK:
                attackRenderUnderTarget(g);
                break;
        }
    }

    private final void attackRenderUnderTarget(Graphics g)
    {
        byte[][] points = AttackAction.getPoints(this.ac_x,  this.ac_y, this.entity.x, this.entity.y);
        g.setClip(0, 0, this.getWidth(), this.getHeight());
        for(int i=0; i<points.length; i++)
        {
            int point[] = getPoint(points[i][0], points[i][1]);
            if(point != null)
            {
                int x = point[0];
                int y = point[1];
                g.setColor(0xFF0000);
                g.fillArc(x-this.tileWidth/4, y+this.tileHeight/4,
                        this.tileWidth/2, this.tileHeight/2, 0, 360);
                g.setColor(0x000000);
                g.drawArc(x-this.tileWidth/4, y+this.tileHeight/4,
                        this.tileWidth/2, this.tileHeight/2, 0, 360);
            }
        }
    }

    private final void setAction(Entity entity, Action action, boolean end)
    {
        String reason = action.getAvailability(entity);
        if(reason == null)
        {
            this.actionEvent(action, null, true, end);
            if(end)
            {
                this.controllerDone();
            }
        }else{
            this.actionEvent(null, "The "+entity.name+" cannot "+action.getName().toLowerCase()+" because "+ reason, false, false);
        }
    }

    public void keyPressed(int keyCode)
    {
        // if a key is pressed we'll stop waiting for something to happen
        synchronized(this)
        {
            this.notify();
        }
        // we'll clear out the messages too
        this.clearMessages();

        switch(this.mode)
        {
            case MOVE:
                moveKeyPressed(keyCode);
                break;
            case ATTACK:
                attackKeyPressed(keyCode);
                break;
            case WIN:
            case LOSE:
                this.quit(true);
                break;
        }
    }

    private final void attackKeyPressed(int keyCode)
    {
        int dx = 0;
        int dy = 0;
        int gameAction = this.getGameAction(keyCode);
        switch(gameAction)
        {
            case Canvas.UP:
                dy = -1;
                break;
            case Canvas.DOWN:
                dy = 1;
                break;
            case Canvas.LEFT:
                dx = -1;
                break;
            case Canvas.RIGHT:
                dx = 1;
                break;
        }
        if(gameAction == Canvas.FIRE || gameAction == Canvas.GAME_A)
        {
            if(this.action != null)
            {
                createAction();
                return;
            }
        }else if(dx == 0 && dy == 0){
            if(gameAction == Canvas.GAME_B){
                if(!this.info)
                {
                    showInfo();
                }else{
                    hideInfo();
                }
                return;
            }else if(gameAction == Canvas.GAME_C){
                // find the next enemy in range
                int size = this.entity.container.getContainedEntities().size();
                for(int i=(this.targetIndex+1)%size; i!=this.targetIndex; )
                {
                    Entity found = (Entity)this.entity.container.getContainedEntities().elementAt(i);
                    if(found.side != 0 &&
                            AttackAction.inRange(found.x, found.y, this.entity.x, this.entity.y, ((AttackAction)this.action).range) &&
                            (!(this.currentRoom.isFriendly(found.side, this.entity.side) ^
                            (((AttackAction)this.action).flags & AttackAction.GOOD_BIT) != 0)))
                    {
                        this.targetIndex = (byte)i;
                        dx = found.x - this.ac_x;
                        dy = found.y - this.ac_y;
                        break;
                    }
                    i = (i+1)%size;
                }
                /* uncomment if space is ever not an issue - cycle backwards
            }else if(gameAction == Canvas.GAME_D){
                // find the next enemy in range
                int startIndex = targetIndex - 1;
                if(startIndex < 0)
                {
                    startIndex = this.entity.container.getContainedEntities().size()-1;
                }
                for(int i=this.targetIndex-1; i!=this.targetIndex; )
                {
                    Entity found = (Entity)this.entity.container.getContainedEntities().elementAt(i);
                    if(found.side != 0 &&
                            AttackAction.inRange(found.x, found.y, this.entity.x, this.entity.y, ((AttackAction)this.action).range) &&
                            (!(this.currentRoom.isFriendly(found.side, this.entity.side) ^
                            (((AttackAction)this.action).flags & AttackAction.GOOD_BIT) != 0)))
                    {
                        this.targetIndex = (byte)i;
                        dx = found.x - this.ac_x;
                        dy = found.y - this.ac_y;
                        break;
                    }
                    i--;
                    if(i < 0)
                    {
                        i = this.entity.container.getContainedEntities().size()-1;
                    }
                }
                */
            }
        }
        if(this.info)
        {
            hideInfo();
        }
        this.ac_x += dx;
        this.ac_y += dy;
        this.repaint();
    }

    private final void moveKeyPressed(int keyCode)
    {
        int dx = 0;
        int dy = 0;
        // allow diagonals through a tilt button
        int gameAction = this.getGameAction(keyCode);
        if(TILT_DOWN)
        {
            if(gameAction == Canvas.UP)
            {
                dy = -1;
                dx = -1;
            }else if(gameAction == Canvas.DOWN){
                dy = 1;
                dx = 1;
            }else if(gameAction == Canvas.LEFT){
                dy = 1;
                dx = -1;
            }else if(gameAction == Canvas.RIGHT){
                dy = -1;
                dx = 1;
            }
        }else{
            if(gameAction == Canvas.UP)
            {
                dy = -1;
                dx = 0;
            }else if(gameAction == Canvas.DOWN){
                dy = 1;
                dx = 0;
            }else if(gameAction == Canvas.LEFT){
                dy = 0;
                dx = -1;
            }else if(gameAction == Canvas.RIGHT){
                dy = 0;
                dx = 1;
            }
        }
        if(gameAction == Canvas.FIRE || gameAction == Canvas.GAME_A)
        {
            dx = 0;
            dy = 0;
            TILT_DOWN = !TILT_DOWN;
            this.repaint();
        }else if(dx == 0 && dy == 0){
            if(gameAction == Canvas.GAME_B)
            {
                dx = 0;
                dy = 0;
                // pick up whatever is here
                Action pickup = this.entity.getSimpleAction(SimpleAction.PICKUP);
                if(pickup != null)
                {
                    pickup = pickup.copy(this.entity);
                    this.setAction(this.entity, pickup, true);
                }
            }
        }
        if(dx != 0 || dy != 0)
        {
            MoveAction copy = (MoveAction)this.action.copy(this.entity);
            copy.setMove(this.entity.x, this.entity.y, (byte)(this.entity.x + dx), (byte)(this.entity.y + dy));
            this.setAction(this.entity, copy, true);
        }
    }

    /*
    public void pointerPressed(int x, int y)
    {
        switch(this.mode)
        {
            case MOVE:
                break;
            case ATTACK:
                break;
        }
    }
    */

    // from attack action controller
    private final int renderEntity(Graphics g, Entity entity, int jx, int jy, boolean leftJustify)
    {
        byte[] position = this.getPosition(entity.entityTypeId);

        Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

        int iw = position[2]+4;
        int ih = position[3];
        if(entity.health > 0)
        {
            iw += DuelsCanvas.BIG_HEALTH_WIDTH+2;
            ih = Math.max(ih, DuelsCanvas.BIG_HEALTH_HEIGHT);
        }
        int w = Math.max(iw, font.stringWidth(entity.name)+4);
        int h = ih+6+font.getHeight();

        int rh;
        int y;
        if(jy + h > this.getHeight())
        {
            rh = -h;
            y = 2;
            leftJustify = true;
            jx = 2;
        }else{
            rh = h;
            y = jy;
        }
        int x;
        if(leftJustify || rh < 0)
        {
            x = jx;
        }else{
            x = jx - w;
        }
        g.setClip(x, y, w+1, h+1);
        g.setColor(0xA0A0A0);
        g.fillRect(x, y, w, h);
        g.setColor(DuelsCanvas.getSideColor(entity.side));
        g.drawRect(x+1, y+1, w-2, h-2);
        g.setColor(0x000000);
        g.drawRect(x, y, w, h);

        int ix;
        if(entity.health > 0)
        {
            DuelsCanvas.drawHealth(g, entity, x+2, y+h-2-DuelsCanvas.BIG_HEALTH_HEIGHT);
            ix = x+4+DuelsCanvas.BIG_HEALTH_WIDTH;
        }else{
            ix = x+2;
        }

        int iy = y+font.getHeight();

        this.render(g, position, ix, iy+2, false);
        int ly = y+2;
        int lx = x+2;

        g.setClip(x, y, w, h);
        g.setColor(0x00FFFFFF);
        g.setFont(font);
        g.drawString(entity.name, lx, ly, Graphics.TOP | Graphics.LEFT);
        ly += font.getBaselinePosition();
        int ry;
        if(rh < 0)
        {
            ry = rh-2;
        }else{
            ry = y + h;
        }
        return ry;
    }

    private void createAction()
    {
        if(this.action != null)
        {
            if(this.info)
            {
                hideInfo();
            }
            AttackAction action;
            action = (AttackAction)this.action.copy(this.entity);
            action.setPosition(this.ac_x, this.ac_y);
            this.setAction(this.entity, action, true);
        }
    }

    private final void showInfo()
    {
        this.info = true;
    }

    private final void hideInfo()
    {
        this.info = false;
    }
}
