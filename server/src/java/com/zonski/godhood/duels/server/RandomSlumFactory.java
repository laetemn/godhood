package com.zonski.godhood.duels.server;

import com.zonski.godhood.duels.game.Room;
import com.zonski.godhood.duels.game.Entity;

import java.io.OutputStream;
import java.util.Random;
import java.util.BitSet;

/**
 * Created by IntelliJ IDEA.
 * User: Chris Glover
 * Date: Dec 16, 2003
 * Time: 5:24:50 PM
 * To change this template use Options | File Templates.
 */
public class RandomSlumFactory extends AbstractRandomRoomFactory
{
    public static final int DEFAULT_MINIMUM_ROOM_DIMENSIONS = 3;
    public static final int DEFAULT_MAXIMUM_ROOM_DIMENSIONS = 9;

    public static final int MAX_FALSE_STARTS = 12;
    public static final int MAX_BREAKS = 6;

    private static final int CORNERS = 0;
    private static final int EDGES   = 1;
    private static final int DONE    = 2;

    private static final int NORTH   = 1;
    private static final int EAST    = 2;
    private static final int SOUTH   = 4;
    private static final int WEST    = 8;

    private int minimumRoomDimensions = DEFAULT_MINIMUM_ROOM_DIMENSIONS;
    private int maximumRoomDimensions = DEFAULT_MAXIMUM_ROOM_DIMENSIONS;
    private int maximumAdjustedRoomDimensions = this.maximumRoomDimensions - this.minimumRoomDimensions;

    public RandomSlumFactory()
    {
    }

    public Room getRoom(String name, int width, int height, Random random)
    {
        int roomCount = 1;
        int[][] tiles = new int[height][width];

        int minx = this.minimumRoomDimensions + 1;
        int miny = this.minimumRoomDimensions + 1;
        int maxx = width - (this.minimumRoomDimensions + 1);
        int maxy = height - (this.minimumRoomDimensions + 1);

        int x = minx + Math.abs(random.nextInt() % (maxx - minx - this.minimumRoomDimensions));
        int y = miny + Math.abs(random.nextInt() % (maxy - miny - this.minimumRoomDimensions));
        int w = this.minimumRoomDimensions + Math.abs(random.nextInt() % Math.min((maxx - x - this.minimumRoomDimensions), this.maximumAdjustedRoomDimensions));
        int h = this.minimumRoomDimensions + Math.abs(random.nextInt() % Math.min((maxy - y - this.minimumRoomDimensions), this.maximumAdjustedRoomDimensions));
        //makeRoom(tiles, x, y, w, h, roomCount);
        boolean running = true;
        int numBreaks = 0;

        // add in corridors
        // TODO : have more than one
        int cx;
        int cy;
        int cw;
        int ch;
        boolean horizontalCorridor = random.nextBoolean();
        if(horizontalCorridor)
        {
            // horizontal
            ch = 1;
            cw = width - 2*(Math.abs(random.nextInt()%(this.maximumRoomDimensions-1)));
            cx = (width - cw)/2;
            cy = Math.abs(random.nextInt()) % (height-2*(this.maximumRoomDimensions-1)) + this.maximumRoomDimensions;
        }else{
            // vertical
            cw = 1;
            ch = height - 2*(Math.abs(random.nextInt()%(this.maximumRoomDimensions-1)));
            cx = Math.abs(random.nextInt()) % (width-2*(this.maximumRoomDimensions-1)) + this.maximumRoomDimensions;
            cy = (height - ch)/2;
        }
        makeRoom(tiles, cx, cy, cw, ch, roomCount);
        x = cx;
        y = cy;
        w = cw;
        h = ch;

        outer: while(running && numBreaks < MAX_BREAKS)
        {
            // find a new spot
            // find the edge
            int px = x+w/2;
            int py = y+h/2;
            int falseStarts = 0;
            while(tiles[py][px]!=0)
            {
                px += random.nextInt()%2;
                py += random.nextInt()%2;
                if(px<=0 || px>=width-1 || py<=0 || py>=height-1)
                {
                    falseStarts++;
                    if(falseStarts > MAX_FALSE_STARTS)
                    {
                        running = false;
                        break outer;
                    }
                    px = x+w/2;
                    py = y+h/2;
                }
            }
            int mode = CORNERS;
            // px and py are now at the edge of the rooms
            // we want to move along the edge to find a point where two rooms join
            int dx = 0;
            int dy = 0;

            BitSet used = new BitSet(9);
            boolean foundSpot = false;
            boolean broken = false;
            int startX = px;
            int startY = py;

            while(!foundSpot && !broken)
            {
                getUsedDirections(tiles, px, py, width, height, used);
                switch(mode)
                {
                    default:
                    case CORNERS:
                        if(used.cardinality() == 2 &&
                                used.get(NORTH) != used.get(SOUTH) &&
                                used.get(EAST) != used.get(WEST) &&
                                isValidSpot(px, py, width, height))
                        {
                            foundSpot = true;
                        }
                        break;
                    case EDGES:
                        if(used.cardinality() ==1 &&
                                isValidSpot(px, py, width, height))
                        {
                            foundSpot = true;
                        }
                        break;
                }
                if(!foundSpot)
                {
                    switch(used.cardinality())
                    {
                        case 0:
                            // we're on a corner
                            //         *
                            //--------+
                            //        |
                            // check ne, nw, se, sw directions
                            if(px>0 && py>0 && tiles[py-1][px-1] != 0)
                            {
                                if(dx > 0)
                                {
                                    dx = 0;
                                    dy = -1;
                                }else if(dy > 0){
                                    dx = -1;
                                    dy = 0;
                                }else{
                                    broken = true;
                                }
                            }else if(px>0 && py<height-1 && tiles[py+1][px-1] != 0){
                                if(dx > 0)
                                {
                                    dx = 0;
                                    dy = 1;
                                }else if(dy < 0){
                                    dx = -1;
                                    dy = 0;
                                }else{
                                    broken = true;
                                }
                            }else if(px<width-1 && py>0 && tiles[py-1][px+1] != 0){
                                if(dx < 0)
                                {
                                    dx = 0;
                                    dy = -1;
                                }else if(dy > 0){
                                    dx = 1;
                                    dy = 0;
                                }else{
                                    broken = true;
                                }
                            }else if(px<width-1 && py<height-1 && tiles[py+1][px+1] != 0){
                                if(dx < 0)
                                {
                                    dx = 0;
                                    dy = 1;
                                }else if(dy < 0){
                                    dx = 1;
                                    dy = 0;
                                }else{
                                    broken = true;
                                }
                            }else{
                                broken = true;
                            }
                            break;
                        case 1:
                            // we're on a straight line
                            //      *
                            //--------------
                            if(used.get(NORTH) || used.get(SOUTH))
                            {
                                if(dx >= 0)
                                {
                                    dx = 1;
                                }else{
                                    dx = -1;
                                }
                                dy = 0;
                            }else if(used.get(EAST) || used.get(WEST)){
                                if(dy >= 0)
                                {
                                    dy = 1;
                                }else{
                                    dy = -1;
                                }
                                dx = 0;
                            }else{
                                broken = true;
                            }
                            break;
                        case 2:
                            // we're in a corner
                            //       |
                            //      *|
                            // ------+
                            if(dx > 0 && used.get(EAST) || dx <0 && used.get(WEST))
                            {
                                dx = 0;
                                if(used.get(NORTH))
                                {
                                    dy = 1;
                                }else if(used.get(SOUTH)){
                                    dy = -1;
                                }else{
                                    broken = true;
                                }
                            }else{
                                if(dy > 0 && used.get(SOUTH) || dy <0 && used.get(NORTH))
                                {
                                    dy = 0;
                                    if(used.get(WEST))
                                    {
                                        dx = 1;
                                    }else if(used.get(EAST)){
                                        dx = -1;
                                    }else{
                                        broken = true;
                                    }
                                }else if(dx == 0 && dy == 0){
                                    if(used.get(WEST))
                                    {
                                        dx = 1;
                                    }else if(used.get(NORTH)){
                                        dy = 1;
                                    }else if(used.get(EAST)){
                                        dx = -1;
                                    }else{
                                        dy = -1;
                                    }
                                }
                            }
                            break;
                       default:
                            // we're in a hole, assume that it's a fatal problem
                            broken = true;
                            break;

                    }
                    px += dx;
                    py += dy;
                    if(px == startX && py == startY)
                    {
                        mode++;
                        if(mode >= DONE)
                        {
                            running = false;
                            broken = true;
                        }
                    }
                }
            }
            if(broken)
            {
                numBreaks ++;
            }else if(foundSpot){
                if(used.get(EAST))
                {
                    w = this.minimumRoomDimensions+Math.abs(random.nextInt()%Math.min((px-this.minimumRoomDimensions), this.maximumAdjustedRoomDimensions));
                    x = px - w + 1;
                }else{// if(used.get(WEST)){
                    w = this.minimumRoomDimensions+Math.abs(random.nextInt()%Math.min((width-px-this.minimumRoomDimensions), this.maximumAdjustedRoomDimensions));
                    x = px;
                }
                if(used.get(SOUTH))
                {
                    h = this.minimumRoomDimensions+Math.abs(random.nextInt()%Math.min((py - this.minimumRoomDimensions), this.maximumAdjustedRoomDimensions));
                    y = py - h + 1;
                }else{ // if(used.get(NORTH);
                    h = this.minimumRoomDimensions+Math.abs(random.nextInt()%Math.min((height-py-this.minimumRoomDimensions), this.maximumAdjustedRoomDimensions));
                    y = py;
                }
                roomCount++;
                this.makeRoom(tiles, x, y, w, h, roomCount);
                // make some doors
                if(used.get(EAST))
                {
                    // start at a random point
                    int doory = Math.abs(random.nextInt())%h + y;
                    int doorx = px + 1;
                    int ey = doory;
                    do
                    {
                        if(tiles[ey][doorx+1]>0 && tiles[ey][doorx-1]>0 && tiles[ey][doorx]<0)
                        {
                            // make a door here
                            tiles[ey][doorx] = roomCount;
                            break;
                        }
                        ey = ((ey-y)+1)%h + y;
                    }while(ey != doory);
                }

                if(used.get(WEST))
                {
                    // start at a random point
                    int doory = Math.abs(random.nextInt())%h + y;
                    int doorx = px - 1;
                    int ey = doory;
                    do
                    {
                        if(tiles[ey][doorx+1]>0 && tiles[ey][doorx-1]>0 && tiles[ey][doorx]<0)
                        {
                            // make a door here
                            tiles[ey][doorx] = roomCount;
                            break;
                        }
                        ey = ((ey-y)+1)%h + y;
                    }while(ey != doory);
                }
                if(used.get(SOUTH))
                {
                    int doory = py + 1;
                    int doorx = Math.abs(random.nextInt())%w + x;
                    int ex = doorx;
                    do
                    {
                        if(tiles[doory+1][ex]>0 && tiles[doory-1][ex]>0 && tiles[doory][ex]<0)
                        {
                            // make a door here
                            tiles[doory][ex] = roomCount;
                            break;
                        }
                    }while(ex != doorx);
                }
                if(used.get(NORTH))
                {
                    int doory = py - 1;
                    int doorx = Math.abs(random.nextInt())%w + x;
                    int ex = doorx;
                    do
                    {
                        if(tiles[doory+1][ex]>0 && tiles[doory-1][ex]>0 && tiles[doory][ex]<0)
                        {
                            // make a door here
                            tiles[doory][ex] = roomCount;
                            break;
                        }
                    }while(ex != doorx);
                }
            }
        }

        // add in doorways to do


        return this.roomFromTiles(tiles, width, height);
    }

    private boolean isValidSpot(int x, int y, int width, int height)
    {
        int minx = this.minimumRoomDimensions + 1;
        int miny = this.minimumRoomDimensions + 1;
        int maxx = width - (this.minimumRoomDimensions + 1);
        int maxy = height - (this.minimumRoomDimensions + 1);

        return x >= minx && x < maxx && y >= miny && y < maxy;
    }

    private void getUsedDirections(int[][] tiles, int px, int py, int width, int height, BitSet result)
    {
        result.clear();
        if(px > 0 && py>=0 && py<height && tiles[py][px-1] != 0)
        {
            result.set(WEST);
        }
        if(px < width-1 && py>=0 && py<height && tiles[py][px+1] != 0)
        {
            result.set(EAST);
        }
        if(py > 0 && px>=0 && px<width && tiles[py-1][px] != 0)
        {
            result.set(NORTH);
        }
        if(py < height-1 && px>=0 && px<width && tiles[py+1][px] != 0)
        {
            result.set(SOUTH);
        }
    }

    public void makeRoom(int[][] tiles, int x, int y, int w, int h, int roomCount)
    {
        // double check those edges
        if(x <= 0)
        {
            System.err.println("WARNING : x="+x+" less than or equal to 0");
            x = 1;
        }
        if(y <= 0)
        {
            System.err.println("WARNING : y="+y+" less than or equal to 0");
            y = 1;
        }
        if(w > tiles[0].length-x-1)
        {
            System.err.println("WARNING : w="+w+" more than "+(tiles[0].length-x-1));
            w = tiles[0].length-x-1;
        }
        if(h > tiles.length-y-1)
        {
            System.err.println("WARNING : h="+h+" more than "+(tiles.length-y-1));
            h = tiles.length-y-1;
        }

        // make the room
        for(int py = y; py < y+h; py++)
        {
            for(int px = x; px < x+w; px++)
            {
                // only add the tiles if the spot is unoccupied
                if(tiles[py][px] == 0)
                {
                    tiles[py][px] = roomCount;
                }
            }
        }

        // do the walls
        for(int py = y-1; py < y+h+1; py++)
        {
            if(tiles[py][x-1] == 0)
            {
                tiles[py][x-1] = -roomCount;
            }
            if(tiles[py][x+w] == 0)
            {
                tiles[py][x+w] = -roomCount;
            }
        }
        for(int px = x; px < x+w; px++)
        {
            if(tiles[y-1][px] == 0)
            {
                tiles[y-1][px] = -roomCount;
            }
            if(tiles[y+h][px] == 0)
            {
                tiles[y+h][px] = -roomCount;
            }
        }
        /*
        for(int ty=0; ty<tiles.length; ty++)
        {
            for(int tx = 0; tx<tiles[ty].length; tx++)
            {
                int tile = tiles[ty][tx];
                char c;
                if(tile == 0)
                {
                    c = ' ';
                }else if(tile > 0){
                    c = (char)(((int)'A')+tile);
                }else{
                    c = '#';
                }
                System.out.print(c);
            }
            System.out.println();
        }
        */
    }

    protected Room roomFromTiles(int[][] tiles, int width, int height)
    {
        Room room = new Room(null, (byte)width, (byte)height);
        room.name = "Slum";
        /*
        Entity player = Entity.create(Entity.CAT_TYPE);
        player.x = width/2;
        player.y = height/2;
        player.side = 1;
        room.addContainedEntity(player);
        */

        for(byte y=0; y<height; y++)
        {
            for(byte x=0; x<width; x++)
            {
                int value = tiles[y][x];
                if(value != 0)
                {
                    room.tiles[y][x] = 3;
                }
                if(value < 0)
                {
                    Entity entity = EntityFactory.create(EntityFactory.ROOM_WALL_TYPE);
                    entity.x = x;
                    entity.y = y;
                    // check which way the wall faces
                    /*
                    if(x<width-1 && tiles[y][x+1] < 0)
                    {
                        // if it's surrounded on the x axis - east
                        entity.directionFacing = Entity.DIRECTION_WEST;
                    }else if(y<height-1 && tiles[y+1][x] < 0){
                        // if it's surrounded on the y axis - west
                        entity.directionFacing = Entity.DIRECTION_EAST;
                    }else{
                        // TODO : both (don't show it)
                        // neither show it all
                        entity.directionFacing = Entity.DIRECTION_NONE;
                    }
                    */
                    room.addContainedEntity(entity);
                }
            }
        }

        for(int ty=0; ty<tiles.length; ty++)
        {
            for(int tx = 0; tx<tiles[ty].length; tx++)
            {
                int tile = tiles[ty][tx];
                char c;
                if(tile == 0)
                {
                    c = ' ';
                }else if(tile > 0){
                    c = (char)(((int)'A')+tile);
                }else{
                    c = '#';
                }
                System.out.print(c);
            }
            System.out.println();
        }

        return room;
    }

}
