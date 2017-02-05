package me.markyhzhang.projectpather.menu.listeners;

import me.markyhzhang.projectpather.menu.customcomponents.CustomButton;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class a exit button listener that are
 * used by mutiple buttons with different closing
 * functions. It offers halt program on close, and
 * dispose window but not halt on close.
 */
public class InfoButtonListener  implements ActionListener {

    private JFrame frame;

    /**
     * Position of the mouse for "drag anywhere" feature
     */
    private int posX = 0, posY = 0;

    /**
     * This method creates and shows the info/credit
     * window when the info button is clicked on the
     * start screen
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        /*
         * if the info window is already displaying then
         * don't display it again, just bring it to front
         */
        if (frame!=null){
            if (frame.isVisible()){
                frame.toFront();
                return;
            }
        }

        //creates a frame
        frame = new JFrame();

        //remove system default top tray
        frame.setUndecorated(true);
        //sets the size
        frame.setSize(new Dimension(400,340));
        //make the background to be transparent grey
        frame.setBackground(new Color(53, 59, 63, 220));
        //sets the spawn location to be center
        frame.setLocationRelativeTo(null);

        /*
         * Listeners for the "drag anywhere feature"
         */
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                posX=e.getX();
                posY=e.getY();
            }
        });
        frame.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent evt) {
                //sets frame position when mouse dragged
                frame.setLocation (evt.getXOnScreen()-posX,evt.getYOnScreen()-posY);
            }
        });

        //creates the panel for the frame
        JPanel panel = new JPanel();

        //sets the background to be completely transparent
        panel.setBackground(new Color(0,0,0,0));
        //sets the layout to be boxedLayout and center aligned
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //creates a close custom button with exitbuttonlistener set to invisible on close rather than halt on close
        CustomButton exitButton = new CustomButton(panel, "Close", 200, 35, 16);
        exitButton.addActionListener(new ExitButtonListener(2, frame));


        //the credit JLabel
        JLabel credit = new JLabel("Programmed with ❤ by Yi Han (Mark) Zhang");

        //sets the font for the credit
        credit.setFont(new Font("Serif", Font.ITALIC, 18));
        //make it center aligned
        credit.setAlignmentX(Component.CENTER_ALIGNMENT);
        //sets the color to be while
        credit.setForeground(Color.WHITE);

        //Creates a JLabel for the control instruction with html list formatting
        JLabel control = new JLabel("<html>Control:<br><ul>" +
                "<li>W A S D: movement control</li>" +
                "<li>Mouse: first person view control</li>" +
                "<li>Left mouse key: attack</li>" +
                "<li>Right mouse key: path-finding wand</li>" +
                "<li>Esc key: pause toggle</li>" +
                "</ul></html>");
        //sets the font
        control.setFont(new Font("Serif", Font.BOLD, 22));
        //make it center alignment
        control.setAlignmentX(Component.CENTER_ALIGNMENT);
        //sets color to be white
        control.setForeground(Color.WHITE);

        //add the control, credit and exit button with spaces in between
        panel.add(control);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(credit);
        panel.add(Box.createRigidArea(new Dimension(0,50)));
        panel.add(exitButton);

        //add the panel to the frame
        frame.add(panel);

        //make the frame visible
        frame.setVisible(true);
    }

}
