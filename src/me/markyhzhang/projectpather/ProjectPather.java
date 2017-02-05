package me.markyhzhang.projectpather;

import me.markyhzhang.projectpather.menu.MenuFrame;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This is the main/driver class for
 * this program.
 */
public class ProjectPather {

	/**
	 * The player self instance
	 */
	private Player self;

	/**
	 * The PlayersManager instance
	 */
	private PlayersManager playersManager;

	/**
	 * The connection manager instance
	 */
	private ConnectionManager connectionManager;

    /**
     * The MenuFrame instance
     */
	private MenuFrame menuFrame;

    /**
     * The map int[][] that will be filled with
     * the map downloaded from server
     */
	private int[][] map;

    /**
     * The main method of this program
     * @param args String[]
     */
    public static void main(String [] args) {
    	Logger.log("Starting game instance");
    	//starts the instance of this program
    	new ProjectPather().run();
    }

    /**
     * The method that starts this program
     */
    private void run(){
		Logger.log("Starting MenuFrame");
		//initializes the menu frame
    	menuFrame = new MenuFrame(this);
    }

    /**
     * Getter for the self player object
     * @return Player
     */
	public Player getSelf() {
		return self;
	}

    /**
     * Getter for the PlayerManager
     * @return PlayersManager
     */
	public PlayersManager getPlayersManager(){
		return playersManager;
	}

    /**
     * Sets the player manager
     * @param playersManager Player list manager
     */
	public void setPlayersManager(PlayersManager playersManager) {
		this.playersManager = playersManager;
	}

    /**
     * Setter for the game map once retrieved from the server
     * @param map int[][]
     */
	public void setMap(int[][] map){
		this.map = map;
	}

    /**
     * Getter for the game/maze map
     * @return int[][]
     */
	public int[][] getMap() {
		return map;
	}

    /**
     * Getter for the connection manager
     * @return ConnectionManager
     */
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

    /**
     * Sets the the connection manager
     * @param connectionManager ConnectionManager
     */
	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

    /**
     * Sets the self player
     * @param self Player
     */
	public void setSelf(Player self) {
		this.self = self;
	}

    /**
     * Getter for the MenuFrame
     * @return GameFrame
     */
	public MenuFrame getMenuFrame() {
		return menuFrame;
	}
}
