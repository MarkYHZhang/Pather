package me.markyhzhang.projectpather.menu;

import me.markyhzhang.projectpather.Logger;
import me.markyhzhang.projectpather.ProjectPather;
import me.markyhzhang.projectpather.menu.customcomponents.CustomButton;
import me.markyhzhang.projectpather.menu.customcomponents.CustomPanel;
import me.markyhzhang.projectpather.menu.listeners.ExitButtonListener;
import me.markyhzhang.projectpather.menu.listeners.InfoButtonListener;
import me.markyhzhang.projectpather.menu.listeners.StartButtonListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class is the main menu frame that has
 * a animated background generation and start,
 * info and exit buttons
 */
public class MenuFrame extends JFrame implements Runnable{

    /**
     * The custom panel for the menu frame
     */
    private CustomPanel panel;

    /**
     *     used for window dragging, these are the location of the mouse
     */
    private int posX=0,posY=0;

    /**
     * The constructor for the main frame
     * @param instance Instance of the main class
     */
    public MenuFrame(ProjectPather instance){

        Logger.log("Initializing MenuFrame window");
        //sets the size of the window
        setSize(1000,650);
        //spawn location to center
        setLocationRelativeTo(null);
        //sets the title
        setTitle("ProjectPather by Yi Han (Mark) Zhang");
        //disable resize
        setResizable(false);

        //borderless and draggable
        setUndecorated(true);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                posX=e.getX();
                posY=e.getY();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent evt) {
                //sets frame position when mouse dragged
                setLocation (evt.getXOnScreen()-posX,evt.getYOnScreen()-posY);
            }
        });

        Logger.log("Initializing CustomPanel...");
        //initialize the custom panel
        panel = new CustomPanel();

        //initialize the logo
        JLabel logo = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("resources/logo.png"))));
        //make it center aligned
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        //init the start button with its listener
        CustomButton startButton = new CustomButton(panel, "Start", 200, 60, 25);
        startButton.addActionListener(new StartButtonListener(instance));

        //init the info button with its listener
        CustomButton infoButton = new CustomButton(panel, "Info", 200, 60, 25);
        infoButton.addActionListener(new InfoButtonListener());

        //init the exit button with its listener
        CustomButton exitButton = new CustomButton(panel, "Exit", 200, 60, 25);
        exitButton.addActionListener(new ExitButtonListener(0, this));

        //adds the element to the panel with spacing
        panel.add(Box.createRigidArea(new Dimension(0,100)));
        panel.add(logo);
        panel.add(Box.createRigidArea(new Dimension(0,60)));
        panel.add(startButton);
        panel.add(Box.createRigidArea(new Dimension(0,15)));
        panel.add(infoButton);
        panel.add(Box.createRigidArea(new Dimension(0,15)));
        panel.add(exitButton);

        //adds the panel to the frame
        add(panel);

        //make it visible
        setVisible(true);

        //sets the the closing operation to be halt on close
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Logger.log("Starting MenuFrame...");
        //start this thread
        new Thread(this).start();
    }

    /**
     * The main "game" loop for the menu screen, mainly for
     * the custom background animation
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

        while(true){
            //get the current time in seconds
            double currTime = (double) System.nanoTime() / 1000000000.0;

            //If the loop is fallen too much behind, render ASAP to prevent non-updating screen
            if ((currTime - nextTime) > maxTimeDiff) nextTime = currTime;

            //if we are a
            if (currTime >= nextTime) {//if the loop is behind or at the time to render

                // assign the time for the next update
                nextTime += delta;

                /*
                 * Render if the program got the game logic done early
                 * OR
                 * If the game logic is too slow that the loop is already
                 * 5 frames behind
                 */
                if ((currTime < nextTime) || (skippedFrames > maxSkippedFrames)) {
                    //render the panel
                    panel.repaint();
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
        }
    }

}
