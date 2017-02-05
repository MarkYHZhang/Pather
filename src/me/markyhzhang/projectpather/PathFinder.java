package me.markyhzhang.projectpather;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class utilizes Breadth First Search
 * graph algorithm to find the shortest
 * distance between two points on a 2d map
 */
public class PathFinder {

    /**
     * The map of the map/maze
     */
    private int[][] map;

    /**
     * The point map/maze array for ba
     */
    private Point[][] pathTemplate;

    /**
     * The constructor for this class
     * @param map 2d integer array
     */
    public PathFinder(int[][] map){
        this.map = map;
        pathTemplate = new Point[map.length][map[0].length];
    }

    /**
     * Returns a 2d integer array with marked path
     * using the id number 8.
     * @param startR start row
     * @param startC start column
     * @param r end row
     * @param c end column
     * @return 2D integer array
     */
    public int[][] findPath(int startR, int startC, int r, int c){

        //the path block (8 is to center 11 is to random player)
        int pathBlock = 8;

        /*
         * Determine either the player want to go the
         * center or a specific point.
         *
         * (-1,-1) is the center flag
         */
        int endR, endC;
        if (r != -1 && c != -1){
            pathBlock = 11;
            endR = r;
            endC = c;
        }else{
            endR = map.length/2;
            endC = map[0].length/2;
        }

        //initialize a tmp map for calculation
        int[][] tmpMap = new int[map.length][map[0].length];
        for(int i = 0; i < map.length; i++)
            tmpMap[i] = map[i].clone();

        //initialize the path history 2d array
        Point[][] pathHistory = new Point[pathTemplate.length][pathTemplate[0].length];
        for(int i = 0; i < pathTemplate.length; i++)
            pathHistory[i] = pathTemplate[i].clone();

        //north south east west direction vectors
        Point[] direction = {new Point(-1,0), new Point(1,0), new Point(0,-1), new Point(0,1)};

        //initialize starting point
        Point start = new Point(startR,startC);
        pathHistory[startR][startC] = start;

        //the BFS queue
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        //while queue not empty
        while (!queue.isEmpty()){
            //gets the current point
            Point cur = queue.poll();
            //try all four directions
            for (Point dirPnt : direction) {
                int rLoc = cur.x + dirPnt.x;
                int cLoc = cur.y + dirPnt.y;
                //if in map and is valid block to go and not visite
                if (rLoc >= 0 && rLoc < tmpMap.length && cLoc >= 0 && cLoc < tmpMap[0].length && tmpMap[rLoc][cLoc] >=0 && tmpMap[rLoc][cLoc] <=2 && pathHistory[rLoc][cLoc] == null) {
                    //append this to the queue
                    queue.add(new Point(rLoc, cLoc));
                    //store in path history
                    pathHistory[rLoc][cLoc] = cur;
                }
            }
        }

        //backtrace the path histroy
        Point cur = pathHistory[endR][endC];
        while (true){
            if (cur.x==startR&&cur.y==startC) break;
            tmpMap[cur.x][cur.y] = pathBlock;
            cur = pathHistory[cur.x][cur.y];
        }

        //return the shortest distance map with 8 (path block) labeled
        return tmpMap;
    }

    /**
     * Returns the map
     * @return 2d int array
     */
    public int[][] getMap() {
        return map;
    }
}
