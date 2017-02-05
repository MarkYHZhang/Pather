package me.markyhzhang.projectpather.menu;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class generates the background maze
 * intger 2d array using DFS graph algorithm
 *
 * This class uses similar approach like how
 * the server generates the game/maze map
 */
public class BackgroundGenerator implements Runnable{

    /**
     * The 2d array for the maze
     */
    private int[][] maze;

    /**
     * Number of rows
     */
    private int row;

    /**
     * Number of cols
     */
    private int col;

    /**
     * Running flag to false
     */
    private boolean running = false;

    /**
     * Stopping flag to false
     */
    private boolean stop = false;

    /**
     * A collection of direction point
     * vectors in all four directions
     */
    private ArrayList<Point> directions = new ArrayList<Point>() {{
        add(new Point(-2,0));//up
        add(new Point(2,0));//down
        add(new Point(0,-2));//left
        add(new Point(0,2));//right
    }};

    /**
     * The constructor of this map generator
     * @param row integer
     * @param col integer
     */
    public BackgroundGenerator(int row, int col){
        maze = new int[row][col];
        this.row = row;
        this.col = col;
    }

    /**
     * This method is the helper method of the
     * DPS recursive map generation algorithm
     */
    private void generate() {
        running = true;
        // Initialize
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                maze[i][j] = 1;
            }
        }
        //gets random starting point
        int randomRow = (int) (Math.random() * row);
        int randomCol = (int) (Math.random() * col);

        //Generate the maze with recursive method
        dfs(randomRow, randomCol);
        running = false;
    }

    /**
     * Return the current state
     * of map.
     * @return int[][] map
     */
    public int[][] getMaze() {
        return maze;
    }

    /**
     * This method generates the maze background
     * animation by using a Graph theory technique
     * called Depth First Search (namely DFS). It works
     * by applying the following logic:
     *
     * 1. looping through the four random directions.
     * 2. check if the new point is with in the maze
     *    and such that it hasn't been visited.
     * 3. Set that point to be visited and the point
     *    right before it. Since we want there to be
     *    walls.
     * 4. sleep for 0.025 second before doing the next
     *    generation, so it won't generate based on the
     *    computer's speed.
     * 5. Recursively run this method on the new point
     *    that the was just generated.
     *
     * @param r integer row
     * @param c integer column
     */
    private void dfs(int r, int c) {
        //if stop is true then stop this generation
        if (stop)
            return;

        //Making the direction random
        Collections.shuffle(directions);
        Point[] randomDirections = directions.toArray(new Point[4]);

        // Examine each direction
        for (Point randomDirection : randomDirections) {
            //gets the row and column direction vector
            int rVect = randomDirection.x;
            int cVect = randomDirection.y;

            //creates the new point
            int newR = r + rVect;
            int newC = c + cVect;

            //check if the new point is in the map and that it is not visited
            if (newR >= 0 && newR < row && newC >= 0 && newC < col && maze[newR][newC] != 0) {
                //set the two points in that direction to be visited
                maze[newR][newC] = 0;
                maze[r + rVect / 2][c + cVect / 2] = 0;

                //set running to be true
                running = true;

                //sleep for 0.025 second
                try {
                    Thread.sleep(25L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //recursively call the method on these new point
                dfs(newR, newC);
            }
        }
    }

    /**
     * Stop this map generation
     */
    public void stop(){
        stop = true;
    }

    /**
     * Return the state of the generation
     * @return boolean state
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Called when this thread is started
     */
    @Override
    public void run() {
        generate();
    }
}
