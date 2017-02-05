package me.markyhzhang.projectpather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class manages all the players. It encapsulates
 * a hashmap for the players online with their UUID as
 * key and Player object as the value
 */
public class PlayersManager {

    /**
     * The HashMap that stores ther player
     * as value and UUID as keys to it
     */
    private HashMap<UUID, Player> players;

    /**
     * If there were ever other player online
     */
    private boolean otherPlayers = false;

    /**
     * The constructor for the this class
     */
    public PlayersManager(){
        players = new HashMap<>();
    }

    /**
     * If there was other player
     * @return boolean
     */
    boolean wasOtherPlayers(){
        return otherPlayers;
    }

    /**
     * Adds a player to the hashmap
     * @param player Player object
     */
    void addPlayer(Player player){
        if (!otherPlayers) otherPlayers = true;
        players.put(player.getId(), player);
    }

    /**
     * Return player by their UUID
     * @param id UUID
     * @return Player object
     */
    public Player getPlayer(UUID id){
        return players.get(id);
    }

    /**
     * Size of player list
     * @return integer size
     */
    int size(){
        return players.size();
    }

    /**
     * Iterator for online players
     * @return Iterator<UUID>
     */
    public Iterator<UUID> getIterator(){
        return players.keySet().iterator();
    }

    /**
     * Checks if contains the player with
     * the provided UUID
     * @param id UUID
     * @return boolean
     */
    boolean containsPlayer(UUID id){
        return players.containsKey(id);
    }

    /**
     * Removes the player by their UUID
     * @param id UUID
     */
    void removePlayer(UUID id){
        players.remove(id);
    }

    /**
     * Check if the given player exists
     * @param id player's UUID
     */
    public boolean contains(UUID id){
        return players.containsKey(id);
    }

    /**
     * Return the return packages that contains
     * a random player and all the alive players
     * @return ReturnPackage
     */
    public ReturnPackage getRandomAlivePlayer(){
        ArrayList<Player> alive = new ArrayList<>();
        for(Player curPlayer : players.values()){
            if (curPlayer.isAlive())
                alive.add(curPlayer);
        }
        if (alive.size()==0) return new ReturnPackage(null, alive.size());
        return new ReturnPackage(alive.get((int)(Math.random()*alive.size())),alive.size());
    }

}
