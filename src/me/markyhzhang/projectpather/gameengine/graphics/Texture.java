package me.markyhzhang.projectpather.gameengine.graphics;

/**
 * @author Yi Han (Mark) Zhang
 * This class organizes the textures in a
 * specific order so that the map can represent
 * them in terms of id
 */
public class Texture{

    /**
     * Path to the textures folder
     */
    private static final String path = "resources/textures/";

    /**
     * Textures
     *
     * They are labeled from 0 to 9
     * each represents a texture with
     * a size of 64
     */
    public static GameImage[] textures = {
            new GameImage(path+"floor_0.png", 64),
            new GameImage(path+"floor_1.png", 64),
            new GameImage(path+"center.png", 64),
            new GameImage(path+"wall_0.png", 64),
            new GameImage(path+"wall_1.png", 64),
            new GameImage(path+"wall_2.png", 64),
            new GameImage(path+"ceiling_0.png", 64),
            new GameImage(path+"ceiling_1.png", 64),
            new GameImage(path+"ceiling_1.png", 64),//this is placeholder for pathfinder path
            new GameImage(path+"border.png", 64)
    };

}
