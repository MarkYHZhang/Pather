package me.markyhzhang.projectpather.gameengine.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Yi Han (Mark) Zhang
 * This class encapsulates and processes an image
 */
public class GameImage {

    /**
     * This is the pixel array
     * it is a 2D array folded into 1D
     */
    private int[] pixels;

    /**
     * Path string to the location of image
     */
    private String loc;

    /**
     * Height and width of the image
     */
    private int size;

    /**
     * Constructor for this class that takes in the location
     * of the image with the width and the height of the image
     * and processes the image by calling the load() method
     * @param location
     * @param size
     */
    public GameImage(String location, int size) {
        loc = location;
        this.size = size;
        pixels = new int[size * size];
        load();
    }

    /**
     * This method processes the image into a readable
     * 1D pixel array for later access
     */
    private void load() {
        try {
            BufferedImage image = ImageIO.read(ClassLoader.getSystemResource(loc));
            int w = image.getWidth();
            int h = image.getHeight();
            image.getRGB(0, 0, w, h, pixels, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the size(width and height) of the image
     * @return int
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the pixel array
     * @return int[]
     */
    public int[] getPixels() {
        return pixels;
    }
}
