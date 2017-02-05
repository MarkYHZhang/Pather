package me.markyhzhang.projectpather.gameengine;

import me.markyhzhang.projectpather.Player;
import me.markyhzhang.projectpather.ProjectPather;

import javax.swing.JFrame;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class is responsible for all in-game input
 * from the user. It listens for any key presses, movement
 * of the mouse and clicks on the mouse.
 *
 * With these information it will process them into a
 * direction vector, plane vector the location of the
 * player.
 */
public class MotionListener implements KeyListener, MouseMotionListener, MouseListener {

    /**
     * Direction vector is a ray that shoots
     * directly out of the player's view
     *
     * Ex.
     *        |
     *        |  <--- this is the direction vector
     *        |
     *        O  <--- this is the player's head
     */
    private double xDirection, yDirection;

    /**
     * Plane vector is a ray that is perpendicular to
     * the direction vector.
     *
     * Ex.
     * ---------------
     *          ^^^ this is the plane vector
     *
     *        O  <--- this is the player's head
     */
    private double xPlane, yPlane;

    /**
     * These boolean variable keeps track
     * of which button is currently pressed
     */
    private boolean left, right, forward, back;

    /**
     * The movement speed of the player
     */
    private final double MOVE_SPEED = .065;

    /**
     * If the player got a weapon from
     * the center of the map yet
     */
    private boolean canAttack = false;

    /**
     * Determines if the current path finding wand
     * leads to the center
     */
    private boolean pathToCenter = true;

    /**
     * If the user's cursor is focus on this window
     */
    private boolean focused = true;

    /**
     * Instance of the main class for accessing
     * variables in main class
     */
    private ProjectPather instance;

    /**
     * The instance of the gameframe for cursor hiding
     * purposes and mouse rotation purposes
     */
    private GameFrame frame;

    /**
     * Instance of the this player
     */
    private Player self;

    /**
     * Map/maze of game
     */
    private int[][] map;

    /**
     * The constructor for this class that takes in
     * the instance of the game frame, the instance of
     * main class, the x and y direction and plane values
     * arguments and apply them to this class's local variables
     * @param frame GameFrame instance
     * @param instance MainClass instance
     * @param xDirection x direction vector
     * @param yDirection y Direction vector
     * @param xPlane x plane vector
     * @param yPlane y plane vector
     */
    public MotionListener(GameFrame frame, ProjectPather instance, int[][] map, double xDirection, double yDirection, double xPlane, double yPlane){
        this.frame = frame;
        this.instance = instance;
        self = instance.getSelf();

        this.map = map;

        this.xDirection = xDirection;
        this.yDirection = yDirection;
        this.xPlane = xPlane;
        this.yPlane = yPlane;
    }


    /**
     * This implementation listener method listens
     * for any key presses and handles them
     * @param key KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent key) {
        //If the key is esc
        if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //if the window is not focused
            if (!focused){
                //create an blank cursor
                BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(  cursorImg, new Point(0, 0), "blank cursor");
                //set the cursor to nothing
                ((JFrame) key.getSource()).getContentPane().setCursor(blankCursor);
            }else{
                //make the default cursor reappear
                ((JFrame) key.getSource()).getContentPane().setCursor(Cursor.getDefaultCursor());
            }
            //make focused flag to false
            focused = !focused;
        }
        //for movement controls
        if ((key.getKeyCode() == KeyEvent.VK_A))
            left = true;
        if ((key.getKeyCode() == KeyEvent.VK_D))
            right = true;
        if ((key.getKeyCode() == KeyEvent.VK_W))
            forward = true;
        if ((key.getKeyCode() == KeyEvent.VK_S))
            back = true;
    }

    /**
     * This implementation listener method listens
     * for any key releases and processes them
     * @param key KeyEvent
     */
    @Override
    public void keyReleased(KeyEvent key) {
        //for movment controls
        if((key.getKeyCode() == KeyEvent.VK_A))
            left = false;
        if((key.getKeyCode() == KeyEvent.VK_D))
            right = false;
        if((key.getKeyCode() == KeyEvent.VK_W))
            forward = false;
        if((key.getKeyCode() == KeyEvent.VK_S))
            back = false;
    }


    /**
     * This method is responsible for updating
     * the player's location depending on key presses
     * with error/collision checks
     */
    void update() {
        //if the game window is focused
        if (focused) {

            /*
             * If the block that the player want to go
             * is has an id smaller than 2 that means that
             * block is an floor, not a wall.
             *
             * If that is valid and if the player won't be
             * moving half of its body into the wall by calling
             * the canMove() method
             *
             * If the above two condition are true then update the
             * player's x or y value by adding or subtracting the player's
             * current x and y value buy the direction vector times the
             * movement speed.
             */

            //if the player is pressing forward
            if (forward) {
                if (map[(int) (self.getX() + xDirection * MOVE_SPEED)][(int) self.getY()] <= 2 && canMove(map, self.getX() + xDirection * MOVE_SPEED, self.getY())) {
                    self.setX(self.getX() + xDirection * MOVE_SPEED);
                }
                if (map[(int) self.getX()][(int) (self.getY() + yDirection * MOVE_SPEED)] <= 2 && canMove(map, self.getX(), self.getY() + yDirection * MOVE_SPEED))
                    self.setY(self.getY() + yDirection * MOVE_SPEED);
            }

            //if the player is pressing back
            if (back) {
                if (map[(int) (self.getX() - xDirection * MOVE_SPEED)][(int) self.getY()] <= 2 && canMove(map, self.getX() - xDirection * MOVE_SPEED, self.getY()))
                    self.setX(self.getX() - xDirection * MOVE_SPEED);
                if (map[(int) self.getX()][(int) (self.getY() - yDirection * MOVE_SPEED)] <= 2 && canMove(map, self.getX(), self.getY() - yDirection * MOVE_SPEED))
                    self.setY(self.getY() - yDirection * MOVE_SPEED);
            }

            //if the player is pressing right
            if (right) {
                if (map[(int) (self.getX() + yDirection * MOVE_SPEED)][(int) (self.getY())] <= 2 && canMove(map, self.getX() + yDirection * MOVE_SPEED, self.getY()))
                    self.setX(self.getX() + yDirection * MOVE_SPEED);
                if (map[(int) (self.getX())][(int) (self.getY() - xDirection * MOVE_SPEED)] <= 2 && canMove(map, self.getX(), self.getY() - xDirection * MOVE_SPEED))
                    self.setY(self.getY() - xDirection * MOVE_SPEED);
            }

            //if the player is pressing left
            if (left) {
                if (map[(int) (self.getX() - yDirection * MOVE_SPEED)][(int) (self.getY())] <= 2 && canMove(map, self.getX() - yDirection * MOVE_SPEED, self.getY()))
                    self.setX(self.getX() - yDirection * MOVE_SPEED);
                if (map[(int) (self.getX())][(int) (self.getY() + xDirection * MOVE_SPEED)] <= 2 && canMove(map, self.getX(), self.getY() + xDirection * MOVE_SPEED))
                    self.setY(self.getY() + xDirection * MOVE_SPEED);
            }

            /*
             * if the player can't attack yet and that
             * the player arrived the the center, set the
             * canAttack flag to true
             */
            if (!canAttack) {
                if (map[(int) self.getX()][(int) self.getY()] == 2) canAttack = true;
            }
        }

        //update the new location of the player to the server
        instance.getConnectionManager().updateLoc();
    }

    /**
     * This method checks if the player can move
     * in a manner that it won't appear too be in
     * walls.
     * @param map <- the map of the game
     * @param x <- the "maybe" X pos of the player
     * @param y <- the "maybe" Y pos of the player
     * @return either the player can move or not
     */
    private boolean canMove(int[][] map, double x, double y){
        /*
         * if there is a wall in all four direction
         * if the id is bigger than 2 that means it won't
         * be a block where the player can step on
         */
        boolean wallUp = map[(int)(x)-1][(int)(y)] > 2;
        boolean wallDown = map[(int)(x)+1][(int)(y)] > 2;
        boolean wallLeft = map[(int)(x)][(int)(y)-1] > 2;
        boolean wallRight = map[(int)(x)][(int)(y)+1] > 2;
        
        /*
         * Bigger the vertical difference the FURTHER the player is to the wall
         * Smaller the vertical difference the CLOSER the player is to the wall
         * Bigger the horizontal difference the RIGHER the player is to the wall
         * Smaller the horizontal difference the LEFTER the player is to the wall
         */
        double verticalWallDifference = x - ((int)x);
        double horizontalWallDifference = y - ((int)y);

        /*
         * check if walls exist in all four direction and
         * if so then check if the player is 0.21 units away
         * from it. If it doesn't satisfy the 0.21 units rule
         * then return false (player may not move that way)
         */
        return !(wallUp && verticalWallDifference < 0.21 || wallDown && verticalWallDifference > 0.79 || wallLeft && horizontalWallDifference < 0.21 || wallRight && horizontalWallDifference > 0.79);
    }

    /**
     * Getter for the x direction vector
     * @return double
     */
    double getxDirection() {
        return xDirection;
    }

    /**
     * Getter for the y direction vector
     * @return double
     */
    double getyDirection() {
        return yDirection;
    }

    /**
     * Getter for the x plane vector
     * @return double
     */
    double getxPlane() {
        return xPlane;
    }

    /**
     * Getter for the y plane vector
     * @return double
     */
    double getyPlane() {
        return yPlane;
    }

    /**
     * Getter for the x position of the player
     * @return double
     */
    double getxPos(){
        return self.getX();
    }

    /**
     * Getter for the y position of the player
     * @return double
     */
    double getyPos(){
        return self.getY();
    }

    /**
     * These are required for the "drag anywhere" feature
     */
    private int mousePosX = 0, mousePosY = 0;

    /**
     * Getter for the canAttack flag
     * @return boolean
     */
    boolean canAttack(){
        return canAttack;
    }

    /**
     * Getter for the focused flag
     * @return boolean
     */
    boolean isFocused() {
        return focused;
    }

    /**
     * This is an override implementation method
     *
     * This is used for the "anywhere" drag
     * feature of the window, where you can
     * drag any where on the frame to move the
     * window
     * @param e MouseEvent
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        //if the window isn't focused then enable the movement feature
        if (!focused) {
            //set the frame location to the location of the frame subtract the mouse location
            frame.setLocation(e.getXOnScreen() - mousePosX, e.getYOnScreen() - mousePosY);
        }else{
            //if it is focused then update accordingly
            mouseMove(e);
        }
    }

    /**
     * This is yet another override method for the player's
     * first person view that modifies the x and y: plane and
     * direction vectors
     * @param e MouseEvent
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseMove(e);
    }

    /**
     * This method is responsible for the update of the
     * x and y: plane and direction vector
     * @param e MouseEvent for the x and y location
     */
    private void mouseMove(MouseEvent e){
        try {
            //if the window is focused
            if (focused) {
                //Create a robot that makes the cursor to keep in the center of screen
                Robot robot = new Robot();

                //Only x of mouse is needed since the game doesn't offer to look up and down
                double xMouse = (e.getXOnScreen() - (frame.getX() + frame.getWidth() / 2));

                /*
                Implementing rotation matrix
                    [ cosX  -sinX ]
                R = |             |
                    [ sinX  cosX  ]
                 */

                double prexDirection = xDirection;
                xDirection = xDirection * Math.cos(-xMouse / 500) - yDirection * Math.sin(-xMouse / 500);
                yDirection = prexDirection * Math.sin(-xMouse / 500) + yDirection * Math.cos(-xMouse / 500);

                //xPlane is the wideness and yPlane is the heightness of the player's FOV
                double prexPlane = xPlane;
                xPlane = xPlane * Math.cos(-xMouse / 500) - yPlane * Math.sin(-xMouse / 500);
                yPlane = prexPlane * Math.sin(-xMouse / 500) + yPlane * Math.cos(-xMouse / 500);

                //keep the mouse in the center of screen
                robot.mouseMove(frame.getX() + frame.getWidth() / 2, frame.getY() + frame.getHeight() / 2);
            }
        } catch (AWTException ex) {
            //catch AWTException that might be thrown by the Robot class
            ex.printStackTrace();
        }
    }

    /**
     * If the player is going to center now
     * @return boolean
     */
    public boolean isPathToCenter() {
        return pathToCenter;
    }

    /**
     * This method checks if the player click the exit button
     * @param e MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //if not focused in game
        if (!focused){
            //gets the x and y and sets it to mouse PosX and PosY
            mousePosX=e.getX();
            mousePosY=e.getY();
            //Checks if the mouse click occurred on the bottom right corner where the exit button is
            if (mousePosX<=frame.getWidth()&&mousePosX>=frame.getWidth()-80&&mousePosY<=frame.getHeight()&&mousePosY>=frame.getHeight()-55){
                //close the server connection
                instance.getConnectionManager().end();
                //wait for 1/10 of a second before closing the game frame
                try{
                    Thread.sleep(100);
                }catch (Exception ee){
                    ee.printStackTrace();
                }
                //close and stop the current game session
                frame.stop();
                frame.dispose();
                //open back up the menu screen
                instance.getMenuFrame().setVisible(true);
            }
        }else {
            /*
             * If focused in game and clicked the left button and is
             * eligible to attack, then send attack packets to the server
             *
             * Otherwise it will be consider a toggle for the path-finding
             * wand to switch between path to center or to a random player
             */
            if (e.getButton() == MouseEvent.BUTTON1 && canAttack()) {
                instance.getConnectionManager().startAttack();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                pathToCenter = !pathToCenter;
            }
        }
    }

    /**
     * If the player released the left mouse button
     * then stop the attack
     * @param e MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton()==MouseEvent.BUTTON1) {
            instance.getConnectionManager().endAttack();
        }
    }

    //UNUSED LISTENERS

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
