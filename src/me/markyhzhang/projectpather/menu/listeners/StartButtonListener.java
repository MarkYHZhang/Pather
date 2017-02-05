package me.markyhzhang.projectpather.menu.listeners;

import me.markyhzhang.projectpather.ProjectPather;
import me.markyhzhang.projectpather.menu.customcomponents.CustomButton;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class is both a listener and a frame
 * constructor of the start button and start menu
 * repectively
 */
public class StartButtonListener implements ActionListener {

    /**
     * Instance for main class
     */
    private ProjectPather instance;

    /**
     * Frame to be constructed
     */
    private JFrame frame;

    /**
     * Name field
     */
    private JTextField nameField = new JTextField(" Name");

    /**
     * IP field
     */
    private JTextField ipField = new JTextField(" localhost");

    /**
     * Port field
     */
    private JTextField portField = new JTextField(" 5000");

    /**
     * Dropdown menu
     */
    private JComboBox<String> characterDropDown = new JComboBox<>(new String[]{"Select a character","ALPHA", "BETA", "GAMMA", "DELTA"});;

    /**
     * Mouse position for "dragg anywhere" function
     */
    private int posX = 0, posY = 0;

    /**
     * Constructor for this class
     * @param instance Instance of main class
     */
    public StartButtonListener(ProjectPather instance){
        this.instance = instance;
    }

    /**
     * This is the listener for the start button
     * it will construct the server joining page
     * with name, ip, ppor and character field
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

        //construction of frame
        frame = new JFrame();

        //remove system default tray
        frame.setUndecorated(true);
        //set size
        frame.setSize(new Dimension(400,300));
        //set the background to transparent black
        frame.setBackground(new Color(53, 59, 63, 220));
        //spawn window in the center of screen
        frame.setLocationRelativeTo(null);

        //for the drag anywhere function
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

        //creates a transparent JPanel with boxedlayout center aligned
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0,0,0,0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        /*
         * Configures the name field to be center aligned, with size of
          * 600 x 35 and with Serif font with transparent background
          * and removed system default border and some custom coloring for
          * text and selection color as well as the caret color
         */
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(600,35));
        nameField.setFont(new Font("Serif", Font.BOLD, 20));
        nameField.setBackground(new Color(30, 33, 35));
        nameField.setBorder(null);
        nameField.setForeground(Color.YELLOW);
        nameField.setSelectionColor(Color.ORANGE);
        nameField.setCaretColor(new Color(63, 219, 255));

        //this will remove the default "Name" text that were in the field to empty so the player can input their own
        nameField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if (nameField.getText().replace(" ", "").equalsIgnoreCase("Name"))
                    nameField.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().replace(" ", "").equalsIgnoreCase(""))
                    nameField.setText("Name");
            }
        });

        /*
         * Configures the IP field to be center aligned, with size of
         * 600 x 35 and with Serif font with transparent background
         * and removed system default border and some custom coloring for
         * text and selection color as well as the caret color
         */
        ipField.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipField.setMaximumSize(new Dimension(600,35));
        ipField.setFont(new Font("Serif", Font.BOLD, 20));
        ipField.setBackground(new Color(30, 33, 35));
        ipField.setBorder(null);
        ipField.setForeground(Color.YELLOW);
        ipField.setSelectionColor(Color.ORANGE);
        ipField.setCaretColor(new Color(63, 219, 255));

        //this will remove the default "localhost" text that were in the field to empty so the player can input their own
        ipField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if (ipField.getText().replace(" ", "").equalsIgnoreCase("localhost"))
                    ipField.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (ipField.getText().replace(" ", "").equalsIgnoreCase(""))
                    ipField.setText("localhost");
            }
        });

        /*
         * Configures the IP field to be center aligned, with size of
         * 600 x 35 and with Serif font with transparent background
         * and removed system default border and some custom coloring for
         * text and selection color as well as the caret color
         */
        portField.setAlignmentX(Component.CENTER_ALIGNMENT);
        portField.setMaximumSize(new Dimension(600,35));
        portField.setFont(new Font("Serif", Font.BOLD, 20));
        portField.setBackground(new Color(30, 33, 35));
        portField.setBorder(null);
        portField.setForeground(Color.YELLOW);
        portField.setSelectionColor(Color.ORANGE);
        portField.setCaretColor(new Color(63, 219, 255));

        //this will remove the default "5000" port that were in the field to empty so the player can input their own
        portField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if (portField.getText().replace(" ", "").equalsIgnoreCase("5000"))
                    portField.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (portField.getText().replace(" ", "").equalsIgnoreCase(""))
                    portField.setText("5000");
            }
        });

        /*
         * Custom drop down with custom borders and opaque
         * background with center alignment with Serif text
         */
        characterDropDown.setBorder(null);
        characterDropDown.setOpaque(false);
        characterDropDown.setBackground(new Color(30, 33, 35));
        characterDropDown.setAlignmentX(Component.CENTER_ALIGNMENT);
        characterDropDown.setFont(new Font("Serif", Font.BOLD, 18));
        characterDropDown.setSelectedIndex(1);

        /*
            This is to remove the border and arrow of the drop down menu
            This is a known "bug" that setBorder(null) has no effect
            http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4515838

            Below is a work around where it changes the border of the components
            though still not fully removed border but still looks better than before
         */
        for (int i = 0; i < characterDropDown.getComponentCount(); i++) {
            if (characterDropDown.getComponent(i) instanceof JComponent) {
                ((JComponent) characterDropDown.getComponent(i)).setBorder(new EmptyBorder(0, 0, 0, 0));
            }
            if (characterDropDown.getComponent(i) instanceof AbstractButton) {
                ((AbstractButton) characterDropDown.getComponent(i)).setBorderPainted(false);
            }
        }

        //this is to remove the selection color as it is unmodifiable
        characterDropDown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public void paint(Graphics g) {
                setBackground(new Color(30, 33, 35));
                setForeground(Color.YELLOW);
                paintBorder(null);
                super.paint(g);
            }
        });

        //the two button panel with flowlayout with transparent background
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(0,0,0,0));

        //the custom exit button
        CustomButton exitButton = new CustomButton(panel, "Close", 100, 35, 16);
        exitButton.addActionListener(new ExitButtonListener(2, frame));

        //custom join button with the join button listener
        CustomButton joinButton = new CustomButton(panel, "Join", 100, 35, 16);
        joinButton.addActionListener(new JoinButtonListener(instance, this));

        //append these two custom button to the button panel
        buttonPanel.add(exitButton);
        buttonPanel.add(joinButton);

        //this is to prevent from auto selecting on name field
        JLabel dummyLabel = new JLabel();

        //append the dummylabel and all the sub-panel to the main panel with 20 spacings
        panel.add(dummyLabel);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(Box.createRigidArea(new Dimension(0,30)));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(ipField);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(portField);
        panel.add(Box.createRigidArea(new Dimension(0,20)));
        panel.add(characterDropDown);
        panel.add(Box.createRigidArea(new Dimension(0,30)));
        panel.add(buttonPanel);

        //add the panel to the frame and make it visible
        frame.add(panel);
        frame.setVisible(true);

        //make the focus on to the dummy label
        dummyLabel.requestFocus();
    }

    /**
     * displose this frame when the connection to server is complet
     */
    void disposeFrame(){
        frame.setVisible(false);
        frame.dispose();
    }

    /**
     * Getter for the name field string
     * @return name string
     */
    String getName(){
        return nameField.getText();
    }

    /**
     * Getter for the ip field string
     * @return ip string
     */
    String getIP(){
        return ipField.getText();
    }

    /**
     * Getter for the port string
     * @return port string
     */
    String getPort(){
        return portField.getText();
    }

    /**
     * Getter for the character string
     * @return character string
     */
    String getCharacterStr(){
        return characterDropDown.getItemAt(characterDropDown.getSelectedIndex());
    }
}