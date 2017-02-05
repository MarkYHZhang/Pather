package me.markyhzhang.projectpather.gameengine.graphics;

/**
 * @author Yi Han (Mark) Zhang
 * This class manages the sprites, it contains
 * the image for the sprite and the x and y
 * location of this sprite on the map
 */
public class Sprite {

    /**
     * X and Y location of this sprite
     * on the game/maze map
     */
    private double x;
    private double y;

    /**
     * An has-a relationship with GameImage
     * this is the image for this sprite
     */
    private GameImage image;

    /**
     * Constructor for sprites with their
     * x and y location and the image of it
     * @param x
     * @param y
     * @param image
     */
    public Sprite(double x, double y, GameImage image){
        this.x = x;
        this.y = y;
        this.image = image;
    }

    /**
     * Gets the X value
     * @return double
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y value
     * @return double
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the image of this sprite
     * @return GameImage
     */
    public GameImage getImage() {
        return image;
    }

    /**
     * Gets the X location
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the Y location
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }
}
