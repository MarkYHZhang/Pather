package me.markyhzhang.projectpather.gameengine;

import me.markyhzhang.projectpather.PathFinder;
import me.markyhzhang.projectpather.Player;
import me.markyhzhang.projectpather.ProjectPather;
import me.markyhzhang.projectpather.ReturnPackage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class is the main game frame
 */
public class GameFrame extends JFrame implements Runnable{


    /**
     * Width of window
      */
    private int widths = 900;

    /**
     * Height of window
     */
    private int heights = 550;

    /**
     * The instance of the main class
     */
    private ProjectPather instance;

    /**
     * Instance of the motionlistener for retrieving location and vector info
     */
    private MotionListener motionListener;

    /**
     * Screen instance that is used for rendering in-game frames
     */
    private Screen screen;

    /**
     * This thread is for the game loop
     */
    private Thread thread;

    /**
     * Variable keeping track either the game loop
     * should be running or not
     */
    private boolean running;

    /**
     * This is used for alternating between
     * the normal hand the the attacking hand
     * for animation purposes
     */
    private int attackCnt = 0;

    /**
     * An image object that contains the current image of the frame
     */
    private BufferedImage image;

    /**
     * Pixels of the current image
     */
    private int[] pixels;

    /**
     * Instance of the path finder for shortest distance BFS calculations
     */
    private PathFinder pathFinder;

    /**
     * Images for the attack hand first
     * person view
     */
    private BufferedImage[] attackImages;

    /**
     * Hand for the first person view
     */
    private BufferedImage handImage;

    /**
     * Store the previous random player so that
     * the path will keep at this player until the
     * player want to change
     */
    private Player preRandomPlayer = null;

    /**
     * The constructor for this class
     * that takes in a instance of the main class
     * @param instance Main class's instance
     */
    public GameFrame(ProjectPather instance) {
        this.instance = instance;

        //initializes this thread
        thread = new Thread(this);

        //init the image of current frame
        image = new BufferedImage(widths, heights, BufferedImage.TYPE_INT_RGB);

        //the pixels for the current image/frame
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        //        gets the hand the the attacking hand image from resource
        attackImages = new BufferedImage[3];
        try {
            handImage = ImageIO.read(ClassLoader.getSystemResource("resources/characters/hand.png"));
            attackImages[0] = ImageIO.read(ClassLoader.getSystemResource("resources/characters/attack0.png"));
            attackImages[1] = ImageIO.read(ClassLoader.getSystemResource("resources/characters/attack1.png"));
            attackImages[2] = ImageIO.read(ClassLoader.getSystemResource("resources/characters/attack2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set the size of screen
        setSize(widths, heights);

        //disable resizing
        setResizable(false);

        //remove the system default closing/mini/max tray
        setUndecorated(true);

        //sets the title
        setTitle("ProjectPather");

        //set close window but not halt program when closed is clicked
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //sets the background to black
        setBackground(Color.black);

        //spawn the window at the center of screen
        setLocationRelativeTo(null);

        //sets the cursor to blank when the player engages in game
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);

    }

    /**
     * FPS counter integer
     */
    private int fpsCount = 0;


    /**
     * This method initializes or resets the game window
     *
     * It is responsible for constructing the window and adding
     * of the textures and etc.
     */
    public void init(){

        /*
         * The map of the maze/game retrieved from
         * the server
         */
        int[][] map = instance.getMap();

        //if the map is null then stop initializing
        if (map ==null) return;

        //initializes the pathFinder with the map
        pathFinder = new PathFinder(map);

        /*
         * Init the motionlistener with the instance of this class,
         * main class and the x and y direction and plane vectors.
         *
         * The direction vectors sets the player to spawn looking east
         *
         * the plane vector sets the FOV
         */
        motionListener = new MotionListener(this, instance, map,1, 0, 0, -.8);

        //add motionlistener as keylistener, mouselistener and mousemotionlistener to the class
        addKeyListener(motionListener);
        addMouseMotionListener(motionListener);
        addMouseListener(motionListener);

        //init the screen with this player's object, playersmanager the map and the size of the screen
        screen = new Screen(instance.getSelf(), instance.getPlayersManager(), map, widths, heights);

        //make the window visible
        setVisible(true);

        //start the game loop
        start();
    }

    /**
     * This method starts the game loop and
     * set the running flag to true
     */
    private synchronized void start() {
        running = true;
        thread.start();
    }

    /**
     * This method stops the game loop and
     * sets the running flag to false
     */
    public synchronized void stop() {
        running = false;
        try {
            if (thread!=null) {
                thread.join();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for rendering everything
     * that is displayed in-game
     *
     * Credit, FPS, Health, etc.
     */
    private void render() {
        //Gets the current bufferstrategy for drawing purpose
        BufferStrategy bs = getBufferStrategy();
        //if null create new buffers
        if(bs == null) {
            //smooth screen update
            createBufferStrategy(3);
            return;
        }

        //gets the graphic object from the bufferstrategy
        Graphics g = bs.getDrawGraphics();

        //fill the entire frame with this frame
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

        /*
         * This retrieves information of a random player and the number of alive online player
         * the purpose of wrapping this is to increase the performance, since finding a
         * random player also calculates the alive online player number already so there
         * aren't any point of recalculating
        */
        ReturnPackage returnPackage = instance.getPlayersManager().getRandomAlivePlayer();

        //This sets the credit font
        g.setFont(new Font("TimesRoman", Font.ITALIC, 20));
        //Credit color green
        g.setColor(Color.GREEN);
        //Draws the string
        g.drawString("Project Pather by Mark Z.", 10, 25);

        //sets the font for enemies alive
        g.setFont(new Font("TimesRoman", Font.BOLD, 16));
        //Enemies alive color to white
        g.setColor(Color.WHITE);
        //draws the string of enemies alive
        g.drawString(returnPackage.getAlivePlayers() + " enemies alive", 10, 50);

        //sets the font for fps string
        g.setFont(new Font("TimesRoman", Font.BOLD, 16));
        //color for FPS yellow
        g.setColor(Color.yellow);
        //draws the fps string
        g.drawString("FPS: " + fpsCount,10,75);

        //If this player is alive
        if (instance.getSelf().isAlive()) {
            //sets the font for the health
            g.setFont(new Font("TimesRoman", Font.BOLD, 16));
            //sets the color to red
            g.setColor(Color.RED);
            //draws the health
            g.drawString("Health: " + ((int)instance.getSelf().getHealth()),10,100);
            //if this player has the weapon and if he/she is attacking
            if (motionListener.canAttack()&&instance.getSelf().isAttacking()) {
                    //resets the alternation value to 0 when it reach 20
                    if (attackCnt==15) attackCnt=0;
                    g.drawImage(attackImages[attackCnt++/5], getWidth() - 300, getHeight() - 300, this);
            }else{
                g.drawImage(handImage, getWidth() - 250, getHeight() - 250, this);
            }
            //if this player won
            if (instance.getConnectionManager().isWon()){
                //sets the winning message to color while
                g.setColor(Color.WHITE);
                //sets the font for the winning message
                g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                //draws the string
                g.drawString("You won this round!", getWidth()-275, 55);
            }
        }else{
            //if the player died

            //then sets the spectating color to while
            g.setColor(Color.WHITE);
            //sets the font
            g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
            //draws the string
            g.drawString("You are now spectating", getWidth()-320, 55);
        }
        //if the player has the pathfinder to center
        if (motionListener.isPathToCenter()){
            //sets the color to yellow
            g.setColor(Color.YELLOW);
            //sets the font
            g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
            //draws the string
            g.drawString("Path to center (for weapon)", 8, getHeight()-10);
        }else{
            //if the path is to a random player

            //if null then no player is online if not null then tells the player the path is leading to a random player
            String msg = returnPackage.getPlayer()!=null ? "Path to random player" : "No players online!";
            //sets the color to cyan
            g.setColor(Color.CYAN);
            //sets the font
            g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
            //draws the spectating string
            g.drawString(msg, 8, getHeight()-10);
        }

        //if the the window is not focused
        if (!motionListener.isFocused()){
            //sets the color to be transparent black
            g.setColor(new Color(0,0,0,100));
            //fill the center of the screen with a transparent black rectangle
            g.fillRect(0,getHeight()/2-100,getWidth(),200);
            //sets the font
            g.setFont(new Font("TimesRoman", Font.BOLD, 100));
            //sets the color
            g.setColor(Color.WHITE);
            //draws the paused string
            g.drawString("Paused",getWidth()/2-180,getHeight()/2+35);

            //sets the font of exit button
            g.setFont(new Font("TimesRoman", Font.BOLD, 30));
            //sets the transparent black background of the button
            g.setColor(new Color(0,0,0,150));
            //fills the rectangle with the background
            g.fillRect(getWidth()-80, getHeight()-55,80,55);
            //sets the exit button text to be red
            g.setColor(Color.RED);
            //draws the exit button
            g.drawString("Exit", getWidth()-70, getHeight()-15);
        }

        //display the final frame to the screen
        bs.show();
    }

    /**
     * This method updates the game logic
     */
    private void update(){
        //gets the path map of the pathFinder
        int[][] path = pathFinder.getMap();

        //gets a return package
        ReturnPackage returnPackage = instance.getPlayersManager().getRandomAlivePlayer();

        //gets a new random player if any
        Player newRandomPlayer = returnPackage.getPlayer();

        //This is prevent still showing to path to a offline player
        if (preRandomPlayer!=null) {
            if (returnPackage.getAlivePlayers() == 0 || !instance.getPlayersManager().contains(preRandomPlayer.getId())) {
                preRandomPlayer = null;
            }
        }

        //if the player want to go to the center with the path finding wand
        if (motionListener.isPathToCenter()) {
            /*
             * Sets the map with the path to center labeled
             * with number id 8
             */
            path = pathFinder.findPath((int) instance.getSelf().getX(), (int) instance.getSelf().getY(), -1, -1);

            //clears the previous random player so that next time a new random player will be chosen
            preRandomPlayer = null;
        }else{//if the player want to find other players

            //if there aren't any player don't do anything
            if (newRandomPlayer != null) {
                //initialize a randomPlayer Player object
                Player randomPlayer = null;
                //if the previous player isn't null
                if (preRandomPlayer != null){
                    //and if the previous player is alive then keep the previous player
                    if (preRandomPlayer.isAlive()){
                        randomPlayer = preRandomPlayer;
                    }else{
                        //if he/she died then switch to the new random player
                        randomPlayer = newRandomPlayer;
                    }
                }else {
                    //if the previous player is null then switch to a new playr
                    preRandomPlayer = newRandomPlayer;
                }

                /*
                 * If the current random player isn't null then
                 * sets the path to be the shortest distance from
                 * the current player to that player
                 */
                if (randomPlayer != null) {
                    path = pathFinder.findPath((int) instance.getSelf().getX(), (int) instance.getSelf().getY(), (int) randomPlayer.getX(), (int) randomPlayer.getY());
                    //updates the previous player to the current one
                    preRandomPlayer = randomPlayer;
                }
            }
        }

        /*
         * Calculate the screen of game depending on the motion control
         * the path and update it to the pixels of the current image
        */
        screen.update(motionListener, pixels, path);

        //update the motion location depending on map
        motionListener.update();
    }

    /**
     * This method is the method that run
     * when this thread starts, it contains the
     * main game loop that makes calls to other
     * methods
     */
    @Override
    public void run() {

        //Maximum 60fps
        double delta = 1.0/60.0;
        // convert the time to seconds
        double nextTime = (double)System.nanoTime() / 1000000000.0;

        /*
         * This variable is used to ensure that the rendering and
         * the game logic doesn't go desync 0.5 second with each other
         */
        double maxTimeDiff = 0.5;

        //this variable keep track of how many frames are skipped
        int skippedFrames = 1;

        //this variable determines the maximum frames that can be skipped
        int maxSkippedFrames = 5;

        //the current fps
        int fps = 0;

        //stores the current system nano time
        long preTime = System.nanoTime();

        //while the game runs
        while(running){

            //get the current time in seconds
            double currTime = (double) System.nanoTime() / 1000000000.0;

            //If the loop is fallen too much behind, render ASAP to prevent non-updating screen
            if ((currTime - nextTime) > maxTimeDiff) nextTime = currTime;

            //if we are a
            if (currTime >= nextTime) {//if the loop is behind or at the time to render

                // assign the time for the next update
                nextTime += delta;

                //update game logic
                update();

                /*
                 * Render if the program got the game logic done early
                 * OR
                 * If the game logic is too slow that the loop is already
                 * 5 frames behind
                 */
                if ((currTime < nextTime) || (skippedFrames > maxSkippedFrames)) {
                    render();
                    fps++;
                    skippedFrames = 1;
                } else {
                    skippedFrames++;
                }
            } else {// if the loop is generating too fast make it sleep for the program to catch up
                // calculate the time to sleep
                int sleepTime = (int) (1000.0 * (nextTime - currTime));
                // sanity check
                if (sleepTime > 0) {
                    // sleep until the next update
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (((double)(System.nanoTime()-preTime))/1000000000.0>=1){
                preTime = System.nanoTime();
                fpsCount = fps;
                fps=0;
            }
        }
    }
}
