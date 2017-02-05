package me.markyhzhang.projectpather.menu.listeners;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class is the exit button listener that are
 * used by mutiple buttons with different closing
 * functions. It offers halt program on close, and
 * dispose window but not halt on close.
 */
public class ExitButtonListener implements ActionListener {

    /**
     * Determines either quit the program or dispose the window
     * System.exit(0) = exitType: 0
     * dispose() = exitType: 1
     * setvisible(false) = exitType 2;
     */
    private int exitType = 0;

    /**
     * Instance of the Frame for disposing purpose
     */
    private JFrame frame;

    /**
     * The constructor for the ExitButtonListener
     * @param exitType the type of exit function that wanted to be performed
     * @param frame its parent component for the dispose function
     */
    public ExitButtonListener(int exitType, JFrame frame){
        this.exitType = exitType;
        this.frame = frame;
    }

    /**
     * This is the method that is triggered when
     * the button is triggered
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //if 0 then halt program, otherwise dispose window
        if (exitType==0)
            System.exit(0);
        else if (exitType==1)
            frame.dispose();
        else
            frame.setVisible(false);
    }
}