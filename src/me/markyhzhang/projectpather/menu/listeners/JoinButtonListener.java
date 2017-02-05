package me.markyhzhang.projectpather.menu.listeners;

import me.markyhzhang.projectpather.ConnectionManager;
import me.markyhzhang.projectpather.Logger;
import me.markyhzhang.projectpather.Player;
import me.markyhzhang.projectpather.PlayersManager;
import me.markyhzhang.projectpather.ProjectPather;
import me.markyhzhang.projectpather.gameengine.GameFrame;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class is the join button listener
 * that error checks the server ip and port
 * as well as player's name, and make connecton
 * to the server as well as constructing the game
 * frame
 */

public class JoinButtonListener implements ActionListener{

    /**
     * Instance from main class
     */
    private ProjectPather instance;

    /**
     * start button listener instance
     * for getting all the field information
     */
    private StartButtonListener startButtonListener;

    /**
     * The constructor for this class
     * @param instance Main class instance
     * @param startButtonListener instance for startbuttonlistener for retrieving fields
     */
    JoinButtonListener(ProjectPather instance, StartButtonListener startButtonListener){
        this.instance = instance;
        this.startButtonListener = startButtonListener;
    }

    /**
     * This method is triggered when the player
     * presses the join button, this method attempts
     * make connection to server and then start the game
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //gets the name of the player
        String name = startButtonListener.getName();
        //error checks if the name field is valid
        if (name.replace(" ", "").equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(null,"Please provide an name!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //gets the IP and port for the server
        String ip = startButtonListener.getIP().replace(" ", "");
        String port = startButtonListener.getPort().replace(" ", "");

        //error checks for the validity for the port
        try {
            int portInt = Integer.parseInt(port);
            if (portInt<5000 || portInt>6000){
                JOptionPane.showMessageDialog(null,"5000 <= port <= 6000 !!!", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null,"Port must be a positive integer!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //gets the character string and make it uppercase
        String character = startButtonListener.getCharacterStr().toUpperCase();

        //if the player didn't select anything, notify him/her
        if (character.equalsIgnoreCase("Select a character")){
            JOptionPane.showMessageDialog(null,"Please select a character!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Logger.log("Logging into server");
        //sets the self player object
        instance.setSelf(new Player(name, character));
        //sets the playersmanger as a new object
        instance.setPlayersManager(new PlayersManager());

        //initialize the game frame
        GameFrame gameFrame = new GameFrame(instance);

        //initialize dummy synic object for synchronization purposes
        Object dummySyncObj = new Object();

        //initializes the connection manager with ip and port name and etc
        ConnectionManager connectionManager = new ConnectionManager(dummySyncObj, gameFrame, instance, ip, port, name);

        //wait 0.5 second for the connection manager to connect
        try {
            Thread.sleep(500);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        //check if connection is successful
        if (connectionManager.isConnectionSuccessful()) {
            Logger.log("connection success");
            //sets the connection manager for the main class
            instance.setConnectionManager(connectionManager);
            Logger.log("Opening connection gateway");
            //starting the connection manager connection tunnel
            new Thread(connectionManager).start();

            /*
             * The purpose of this is to make sure that the map is fully
             * received from the server before the game frame actually
             * trying to render the map.
             */
            try {
                //synchronized using a dummy object
                synchronized(dummySyncObj) {
                    //while that the connection manager thread is not done, pause this thread
                    while(!connectionManager.isReceivedMap()) {
                        dummySyncObj.wait();
                    }
                    Logger.log("Initializing gameFrame");
                    //once finished downloading the map then start the game
                    gameFrame.init();
                    //dispose the joining frame
                    startButtonListener.disposeFrame();
                    //hide the start frame
                    instance.getMenuFrame().setVisible(false);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        }
    }
}
