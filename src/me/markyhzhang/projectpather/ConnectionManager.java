package me.markyhzhang.projectpather;

import me.markyhzhang.projectpather.gameengine.GameFrame;

import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class is manages the connection to the
 * server and communicates synchronizly with the
 * server.
 */
public class ConnectionManager implements Runnable{

    /**
     * The instance of the main class
     */
	private ProjectPather instance;

    /**
     * Information reader that reads the
     * information from the server
     */
	private BufferedReader fromServer;

    /**
     * The print stream that sents info
     * to the server
     */
	private PrintStream toServer;

    /**
     * The connection socket tunnel to the server
     */
	private Socket socket;

    /**
     * Run determines either the communication
     * should continue
     */
	private boolean run = true;

    /**
     * The flag stores if first output is out yet
     */
	private boolean firstOut = true;

    /**
     * The flag stores if the first input is in yet
     */
	private boolean firstIn = true;

    /**
     * This queue stores the information that
     * is to be send to the server
     */
    private Queue<String> dataQ = new LinkedList<String>();

    /**
     * PlayersManager instance from main class
     */
	private PlayersManager playersManager;

    /**
     * Get the self player instance
     */
	private Player self;

    /**
     * variable that allow other classes to know if the player won this round
     */
    private boolean won = false;

    /**
     * game frame instance
     */
    private GameFrame gameFrame;

    /**
     * Connection state flag
     */
    private boolean connectionSuccess = false;

    /**
     * Fully received map flag
     */
    private boolean receivedMap = false;

    /**
     * The dummy synchronization object
     */
    private Object dummySyncObj;

    /**
     * This is the constructor method the the ConnectionManager class
     * that reads in the IP, port and the name of the player.
     * @param dummySyncObj Synchronization object
     * @param gameFrame main game frame
     * @param instance instance of main c lass
     * @param IP ip string
     * @param port port string
     * @param name name string
     */
	public ConnectionManager(Object dummySyncObj, GameFrame gameFrame, ProjectPather instance, String IP, String port, String name){
	    //sets the variables
	    this.dummySyncObj = dummySyncObj;
	    this.gameFrame = gameFrame;
		this.instance = instance;
		self = instance.getSelf();
		playersManager = instance.getPlayersManager();
        self.setName(name);

        /*
         * Initialize connection to the server
         */
        try {
            socket = new Socket(IP, Integer.parseInt(port));//TODO throws numberformatexception
            fromServer = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            toServer = new PrintStream(socket.getOutputStream());
        }catch (IOException e){
            //if the server is invalid then tells the player server is invalid
            JOptionPane.showMessageDialog(null,"Server unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //let the other classes know that connection is success
        connectionSuccess = true;
    }

    /**
     * This method concludes the function of this class
     * It is responsible for both sending and receiving
     * information from and to the server.
     *
     * Sent packet format:
     * - initialization packet: init,x,y,playerName,PlayerTypeEnum
     * - location update packet: update,x,y
     * - attack packet: attack,x,y,damage(Double type)
     *
     * Received packet format:
     * DIED/NORMAL|ATTACKING/NORMAL|DAMAGING/NORMAL|UUID|playerName|PlayerTypeEnum|x|y|health
     * % sign is used to differentiate multiple players, example:
     * NORMAL|ATTACKING|NORMAL|b039d756-3377-11e6-ac61-9e71128cae77|Mark|ALPHA|650|100|0|100.0%NORMAL|NORMAL|DAMAGING|cf2fccce-3377-11e6-ac61-9e71128cae77|Mango|BETA|750|150|1|100.0
     *
     * @throws IOException thrown if connection is unsuccessful
     */
	private void communicate() throws IOException {
        //runs if communication is still allowed
        if (run) {

            //if it is the first time, send initialization packet
            if (firstOut) {
                if (toServer!=null) {
                    toServer.println("init," + self.getName() + "," + self.getType().getStr());
                    firstOut = false;
                }else{
                    //if server invalid then stop the connection
                    end();
                }
            } else if (!dataQ.isEmpty()) { // if the to-be-send information queue is not empty, send it
                if (self.isAlive()) {
                    toServer.println(dataQ.poll());
                }
            }else{
                //keep alive packet
                toServer.println("kl,0");
            }

            try {

                //gets the input from the server and split the sections
                String[] input = fromServer.readLine().split("[#]");
                int playersInfoStrInd = 0;

                /*
                 * Initial packet from server
                 * UUID#map(rows separated by |)#rest information
                 * example:
                 * UUID#x,y#01020|10120|12021
                 */
                if (firstIn) {
                    //For later reading to skip first packet params
                    playersInfoStrInd = 3;

                    //retrieve the uuid
                    UUID id = UUID.fromString(input[0]);

                    //get the rows of map
                    String[] mapRows = input[2].split("[|]");

                    //initialize map 2d array
                    int[][] map = new int[mapRows.length][mapRows[0].length()];

                    //load the 2d array
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[0].length; j++) {
                            map[i][j] = Character.getNumericValue(mapRows[i].charAt(j));
                        }
                    }

                    //gets the pos
                    String[] posStr = input[1].split("[,]");

                    //initlaizes the location of self player sprite
                    self.initSprite(Double.parseDouble(posStr[0] + ".0") + 0.5, Double.parseDouble(posStr[1] + ".0") + 0.5);

                    //sets the pos of this player
                    self.setX(Double.parseDouble(posStr[0] + ".0") + 0.5);
                    self.setY(Double.parseDouble(posStr[1] + ".0") + 0.5);

                    //sets the map
                    instance.setMap(map);
                    //instructs the JoinButtonListener class that the map is fully received
                    synchronized (dummySyncObj) {
                        receivedMap = true;
                        dummySyncObj.notify();
                    }

                    //sets the id for this player
                    self.setId(id);

                    //sets first flag to false
                    firstIn = false;
                }

                //updated players list for left player check
                ArrayList<UUID> updatedPlayers = new ArrayList<UUID>();

                //sets the section of the players information
                String[] playersStr = input[playersInfoStrInd].split("[%]");

                //loop through every player
                for (String playerStr : playersStr) {
                    //split the current player's info
                    String[] playerInfo = playerStr.split("[|]");

                    //retrieve the information and stores it
                    boolean attacking = playerInfo[1].equals("ATTACKING");
                    boolean damaging = playerInfo[2].equals("DAMAGING");
                    UUID id = UUID.fromString(playerInfo[3]);
                    String name = playerInfo[4];
                    String type = playerInfo[5];
                    double x = Double.parseDouble(playerInfo[6]);
                    double y = Double.parseDouble(playerInfo[7]);
                    double health = Double.parseDouble(playerInfo[8]);

                    //if this player is self then only update the health and damage
                    if (id.equals(self.getId())) {
                        self.setHealth(health);
                        self.setDamaging(damaging);
                    } else if (playersManager.containsPlayer(id)) {//if this player already store locally
                        updatedPlayers.add(id);
                        Player player = playersManager.getPlayer(id);
                        player.setAttacking(attacking);
                        player.setDamaging(damaging);
                        player.setX(x);
                        player.setY(y);
                        player.setHealth(health);
                    } else {//if this player never store locally
                        updatedPlayers.add(id);
                        Player player = new Player(id, name, type, x, y);
                        player.setHealth(health);
                        player.setAttacking(attacking);
                        player.setDamaging(damaging);
                        playersManager.addPlayer(player);
                    }
                }

                /*
                this segment of code determines either any player have left the game
                by comparing the updatePlayer UUID list with the local players hashmap
                */
                if (updatedPlayers.size() < playersManager.size()) {
                    Iterator<UUID> ids = playersManager.getIterator();
                    while (ids.hasNext()) {
                        UUID id = ids.next();
                        if (!updatedPlayers.contains(id)) {
                            ids.remove();
                            playersManager.removePlayer(id);
                        }
                    }
                }

                /*
                 * Check if this player won by looping through to see
                 * if there are any other player alive
                 */
                Iterator<UUID> playerIds = playersManager.getIterator();
                boolean wonFlag = true;
                while (playerIds.hasNext()) {
                    UUID id = playerIds.next();
                    if (!id.equals(self.getId())) {
                        Player curPlayer = playersManager.getPlayer(id);
                        if (curPlayer.isAlive()) wonFlag = false;
                    }
                }
                /*
                 * Won if wonFlag is true and that there are no player
                 * alive or there are no player but there were players
                 */
                won = wonFlag && (playersManager.size() != 0 || playersManager.size() == 0 && playersManager.wasOtherPlayers());
            }catch (SocketException | NullPointerException e){
                //if server connection fails and that it still supposed to run
                if (run) {
                    //close the connections
                    if (fromServer == null) return;
                    fromServer.close();
                    toServer.close();
                    socket.close();

                    //bring the game frame to front and stop it
                    gameFrame.toFront();
                    gameFrame.stop();

                    //close the connection
                    end();

                    //shows connection lost
                    JOptionPane.showMessageDialog(null, "Connection lost!", "ProjectPather", JOptionPane.ERROR_MESSAGE);

                    //close the game frame
                    gameFrame.dispose();

                    //set the menu frame visible
                    instance.getMenuFrame().setVisible(true);
                }
            }
        } else {
            //close the input/output streams and socket
            if (fromServer==null) return;
            fromServer.close();
            toServer.close();
            socket.close();
            end();
        }
    }

    //close the communication gateway
	public void end() {
        toServer.println("bye,1");
        run = false;
	}

    /**
     * This method is responsible for updating the player
     * information to the dataQ. The purpose of the preL
     * system time is to prevent multiple information to
     * be send during one tick. The consequences of not
     * having the variable will lead to not synchronized
     * movement/information between the server and the client
     */
    private long preL = System.currentTimeMillis();
	private void update(double x, double y, double damage){
        long curL = System.currentTimeMillis();
        //if the current time and last sent time is bigger than a tick then execute
        if (curL-preL>=5) {
            //if the damage is bigger than -1 that means it is a attack
            if (damage>-1){
                sendData("attack,"+x+","+y+","+damage);
            }//other wise it is only location update packet
            else sendData("update," + x + "," + y);
            //set current time to be the previous time
			preL=curL;
        }
    }

    /**
     * Update location helper method
     */
    public void updateLoc(){
        update(self.getX(),self.getY(),-1);
    }

    /**
     * Start sending attacking packet
     */
	public void startAttack(){
        self.setAttacking(true);
	}

    /**
     * Stop sending attacking packet
     */
	public void endAttack(){
        self.setAttacking(false);
    }

    /**
     * Return if the player won the game
     * @return boolean
     */
    public boolean isWon() {
        return won;
    }

    /**
     * Return if connection successful
     * @return boolean
     */
    public boolean isConnectionSuccessful() {
        return connectionSuccess;
    }

    /**
     * If map is fully received
     * @return boolean
     */
    public boolean isReceivedMap() {
        return receivedMap;
    }

    //dataQ query method
	private void sendData(String data) {
		dataQ.add(data);
	}

    /**
     * The main connection loop to the server
     */
    @Override
    public void run() {
        while (true){
            try{
                //if attacking then send attacking connection
                if (self.isAttacking()){
                    update(self.getX(),self.getY(),0.08);
                }
                communicate();
            }catch (IOException e){
                e.printStackTrace();
                break;
            }
        }
    }
}
