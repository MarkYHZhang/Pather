package me.markyhzhang.projectpather;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class's purpose is to increase the performance when
 * the render() method needs both a random player and count
 * of alive players
 */
public class ReturnPackage {

    /**
     * The random alive player object
     */
    private Player p;

    /**
     * Amount of online alive player
     */
    private int alivePlayersCount;

    /**
     * Constructor of this class
     * @param p A random player
     * @param count Integer count of alive player
     */
    ReturnPackage(Player p, int count){
        this.p = p;
        alivePlayersCount = count;
    }

    /**
     * Returns the random player
     * @return Player
     */
    public Player getPlayer() {
        return p;
    }

    /**
     * Return the number of online alive player
     * @return Integer
     */
    public int getAlivePlayers() {
        return alivePlayersCount;
    }

}
