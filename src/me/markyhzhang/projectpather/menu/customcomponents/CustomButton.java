package me.markyhzhang.projectpather.menu.customcomponents;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class a custom JButton class
 * that makes the button to be transparent
 * and with custom coloring theme
 */
public class CustomButton extends JButton {

    /**
     * Defines the coloring of button background
     * depending on either it is: noAction, hover
     * or pressed.
     *
     * They are colored yellow, purple and blue
     * respectively.
     */
    private final Color noActionBackgroundColor = new Color(229, 214, 45,80);
    private final Color hoverBackgroundColor = new Color(32, 45, 191,60);
    private final Color pressedBackgroundColor = new Color(52, 152, 219,60);

    /**
     * Background Panel instance for the change
     * color of the path start screen path color
     */
    private JPanel panel;

    /**
     * The constructor of this customButton
     * @param panel The parent component
     * @param text The text to be displayed
     * @param width The width
     * @param height the Height
     * @param fontSize the fontSize
     */
    public CustomButton(JPanel panel, String text, int width, int height, int fontSize) {
        //make mandatory call to the super constructor
        super(text);
        //remove the button's default coloring
        super.setContentAreaFilled(false);

        //sets the panel
        this.panel = panel;

        //sets the size
        setMaximumSize(new Dimension(width,height));
        //make it align center
        setAlignmentX(Component.CENTER_ALIGNMENT);
        //remove border
        setBorderPainted(false);
        //sets the text color to be white
        setForeground(Color.WHITE);
        //remove the focus dotted border
        setFocusPainted(false);
        //sets the font of text on the button
        setFont(new Font("Serif", Font.BOLD, fontSize));
    }

    /**
     * Override method that changes the background color
     * of the button depending on either it is being: pressed
     * hovering or has no action acted upon
     * @param g Graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        //if it is pressed
        if (getModel().isPressed()) {
            g.setColor(pressedBackgroundColor);
            if (panel instanceof CustomPanel)
                ((CustomPanel)panel).setPathSelected();
        } else if (getModel().isRollover()) {//if the button is hovered
            g.setColor(hoverBackgroundColor);
            if (panel instanceof CustomPanel)
                ((CustomPanel)panel).setPathSelected();
        } else {//if the button has no action acted upon
            g.setColor(noActionBackgroundColor);
        }
        //fills the background with the color determined above
        g.fillRect(0, 0, getWidth(), getHeight());

        //make call to the super method to paint the text above the background
        super.paintComponent(g);
    }
}