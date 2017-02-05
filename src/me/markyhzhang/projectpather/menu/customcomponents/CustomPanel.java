package me.markyhzhang.projectpather.menu.customcomponents;

import me.markyhzhang.projectpather.menu.BackgroundGenerator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class a custom JPanel class that enables the
 * background to have a randomly generating path and change
 * color when a button is being hovered
 */
public class CustomPanel extends JPanel {

    /**
     * If any button is being hovered/selected
     */
    private boolean pathSelected = false;

    /**
     * Instance of the background animation generator
     */
    private BackgroundGenerator backgroundGenerator;

    /**
     * The thread that the background generator runs on
     */
    private Thread generatorThread;

    /**
     * The constructor of this custom panel
     */
    public CustomPanel(){
        //sets the layout of the panel to boxed layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //initializes the background generator with dimension 96 by 64
        backgroundGenerator = new BackgroundGenerator(96,64);

        //initializes the generator thread and starts it
        generatorThread = new Thread(backgroundGenerator);
        generatorThread.start();
    }

    /**
     * This methods sets the path selected to true
     * it is called by the custombutton listeners when
     * they are hovered
     */
    void setPathSelected(){
        pathSelected = true;
    }

    /**
     * Gets a random color from:
     * blue
     * green
     * red
     * yellow
     * @return random Color
     */
    private Color getRandomColor(){
        Color[] colors = {new Color(66, 244, 235, 50),
                new Color(66, 244, 134, 50),
                new Color(244, 66, 95, 50),
                new Color(255, 255, 0, 50)};
        return colors[(int)(Math.random()*colors.length)];
    }

    /**
     * This method overrides its parent method and adds the
     * background maze generation animation/effect to it
     * @param g Graphic
     */
    @Override
    protected void paintComponent(Graphics g) {
        //Call to super method for all other necessary functions
        super.paintComponent(g);

        //gets the background map
        int[][] m = backgroundGenerator.getMaze();

        //fills the background with black rectangle
        g.fillRect(0,0,1000,650);

        /*
         * if any button is being hovered make the path
         * flash random colors, otherwise paint it with
         * while color
         */
        if (pathSelected) g.setColor(getRandomColor());
        else g.setColor(new Color(255, 255, 255, 50));

        //set the path selected to false
        pathSelected = false;

        //loop through the background maze map and fill them with small squares
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                if (m[i][j]==0){
                    g.fillRect(i*10+10,j*10+6,10,10);
                }
            }
        }

        //if the background generator is not running or in other words finished generating
        if (!backgroundGenerator.isRunning()){
            //stop it
            backgroundGenerator.stop();

            //create a new background generator
            backgroundGenerator = new BackgroundGenerator(96,64);

            //set the thread to this new one and start it
            generatorThread = new Thread(backgroundGenerator);
            generatorThread.start();
        }
    }
}