package me.markyhzhang.projectpather.gameengine;

import me.markyhzhang.projectpather.Player;
import me.markyhzhang.projectpather.PlayersManager;
import me.markyhzhang.projectpather.gameengine.graphics.GameImage;
import me.markyhzhang.projectpather.gameengine.graphics.Texture;

import java.awt.Color;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;

/**
 * @author Yi Han (Mark) Zhang
 *
 * This class renders the current screen frame
 * based on the player's location, FOV and other
 * player's location
 */
class Screen {

    /**
     * The map int[][] instance
     */
    private int[][] map;

    /**
     * The width of the frame
     */
    private int width;

    /**
     * The height of the frame
     */
    private int height;

    /**
     * Half the width
     */
    private int halfWidth;

    /**
     * Half the height
     */
    private int halfHeight;

    /**
     * The textures for the walls
     */
    private GameImage[] textures = Texture.textures;

    /**
     * Store the perpendicular distance of each
     * vertical stripe to a wall to prevent
     * recalculation.
     */
    private double perpendicularStripDistance[];

    /**
     * The self player instance
     */
    private Player self;

    /**
     * The PlayersManager instance
     */
    private PlayersManager players;

    /**
     * This stores the attack count animation integer
     */
    private int attackAnimationCnt = 0;

    /**
     * Sprite size
     */
    private final int spriteSize = 64;

    /**
     * The constructor for this class
     * @param self Player
     * @param players PlayersManager
     * @param map int[][]
     * @param width integer
     * @param height integer
     */
    Screen(Player self, PlayersManager players, int[][] map, int width, int height) {
        perpendicularStripDistance = new double[width];
        this.self = self;
        this.players = players;
        this.map = map;
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        halfHeight = height / 2;
    }

    /**
     * This method renders the current frame
     * @param motionListener MotionListener
     * @param pixels int[]
     * @param pathFind int[][]
     */
    void update(MotionListener motionListener, int[] pixels, int[][] pathFind) {

        //loop through every strip of pixel horizontally on the screen
        for (int x = 0; x < width; x++) {
            //calculate for the x location of the camera
            double cameraX = 2 * x / (double) (width) - 1;

            //calculate for the direction vector for the ray that shoots out of the player's "eye"
            double rayDirX = motionListener.getxDirection() + motionListener.getxPlane() * cameraX;
            double rayDirY = motionListener.getyDirection() + motionListener.getyPlane() * cameraX;

            //PathFinder position
            int mapX = (int) motionListener.getxPos();
            int mapY = (int) motionListener.getyPos();

            //length of ray from current position to next x or y-side
            double sideDistX;
            double sideDistY;

            //Length of ray from one side to next in map
            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));

            //perpWallDist is the distance from the player to the first wall the ray collides with.
            double perpWallDist;

            //Direction to go in x and y
            int stepX, stepY;

            //was a wall hit
            boolean hit = false;

            //was the wall vertical or horizontal
            int side = 0;

            //Figure out the step direction and initial distance to a side
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (motionListener.getxPos() - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - motionListener.getxPos()) * deltaDistX;
            }
            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (motionListener.getyPos() - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - motionListener.getyPos()) * deltaDistY;
            }

            //Loop to find where the ray hits a wall
            while (!hit) {
                //Jump to next square
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                //Check if ray has hit a wall
                if (map[mapX][mapY] > 2) hit = true;
            }

            //Calculate distance to the point of impact
            if (side == 0)
                perpWallDist = Math.abs((mapX - motionListener.getxPos() + (1 - stepX) / 2) / rayDirX);
            else
                perpWallDist = Math.abs((mapY - motionListener.getyPos() + (1 - stepY) / 2) / rayDirY);
            //Now calculate the height of the wall based on the distance from the motionListener
            int lineHeight;
            if (perpWallDist > 0) lineHeight = Math.abs((int) (height / perpWallDist));
            else lineHeight = height;
            //calculate lowest and highest pixel to fill in current stripe

            int halfLineHeight = lineHeight/2;

            int drawStart = -halfLineHeight + halfHeight;
            if (drawStart < 0)
                drawStart = 0;
            int drawEnd = halfLineHeight + halfHeight;
            if (drawEnd >= height)
                drawEnd = height - 1;
            //add a texture
            int texNum = map[mapX][mapY];
            double wallX;//Exact position of where wall was hit
            if (side == 1) {//If its a y-axis wall
                wallX = (motionListener.getxPos() + ((mapY - motionListener.getyPos() + (1 - stepY) / 2) / rayDirY) * rayDirX);
            } else {//X-axis wall
                wallX = (motionListener.getyPos() + ((mapX - motionListener.getxPos() + (1 - stepX) / 2) / rayDirX) * rayDirY);
            }
            wallX -= Math.floor(wallX);
            //x coordinate on the texture
            int texX = (int) (wallX * (textures[texNum].getSize()));
            if (side == 0 && rayDirX > 0) texX = textures[texNum].getSize() - texX - 1;
            if (side == 1 && rayDirY < 0) texX = textures[texNum].getSize() - texX - 1;
            //calculate y coordinate on texture
            for (int y = drawStart; y < drawEnd; y++) {
                int texY = ((((y << 1) - height + lineHeight) << 6) / lineHeight) >> 1;
                int color = textures[texNum].getPixels()[texX + (texY * textures[texNum].getSize())];
                if (self.isDamaging() && attackAnimationCnt > 15) {
                    int damageColor = (color >> 16) & 0xFF;
                    damageColor = (damageColor << 8) + (((color >> 8) & 0xFF) >> 1);
                    damageColor = (damageColor << 8) + ((color & 0xFF) >> 1);
                    pixels[x + y * width] = damageColor;
                } else {
                    pixels[x + y * (width)] = color;
                }
            }


            //SET THE perpendicularStripDistance FOR THE SPRITE CASTING
            perpendicularStripDistance[x] = perpWallDist; //perpendicular distance is used

            //FLOOR + ceiling CASTING
            double floorXWall, floorYWall; //x, y position of the floor texel at the bottom of the wall

            //4 different wall directions possible
            if (side == 0 && rayDirX > 0) {
                floorXWall = mapX;
                floorYWall = mapY + wallX;
            } else if (side == 0 && rayDirX < 0) {
                floorXWall = mapX + 1.0;
                floorYWall = mapY + wallX;
            } else if (side == 1 && rayDirY > 0) {
                floorXWall = mapX + wallX;
                floorYWall = mapY;
            } else {
                floorXWall = mapX + wallX;
                floorYWall = mapY + 1.0;
            }

            //the distance to the wall
            double distWall = perpWallDist;

            //current distance to the wall
            double currentDist;

            //if end smaller than zero then reset to height
            if (drawEnd < 0){
                drawEnd = height; //becomes < 0 when the integer overflows
            }

            //draw the floor from drawEnd to the bottom of the screen
            for (int y = drawEnd + 1; y < height; y++) {
                //calculate for the current distances
                currentDist = height / (2.0 * y - height);

                //the eight factor to get the exact floor location based on the current distance and the wall distance
                double weight = (currentDist) / (distWall);

                //Gets the current floor X and Y
                double currentFloorX = weight * floorXWall + (1.0 - weight) * motionListener.getxPos();
                double currentFloorY = weight * floorYWall + (1.0 - weight) * motionListener.getyPos();

                //if the current location is a floor (id smaller or equals to 2)
                if (map[(int)currentFloorX][(int)currentFloorY] <= 2) {
                    //the texture X and Y
                    int floorTexX = (int) (currentFloorX * 64) % 64;
                    int floorTexY = (int) (currentFloorY * 64) % 64;

                    //prevents recalculation
                    int sixfourTimesFloorTexY = floorTexY << 6;

                    //index of the floor and ceil to put on the pixel[]
                    int indFloor = x + y * width;
                    int indCeil = x + (height - y) * width;

                    //except for center, add shadow to all other floors
                    int floorColorInt;
                    if (map[(int)currentFloorX][(int)currentFloorY]==2){
                        floorColorInt = (textures[map[(int) currentFloorX][(int) currentFloorY]].getPixels()[sixfourTimesFloorTexY + floorTexX]);
                    }else{
                        // applied gray color
                        floorColorInt = (textures[map[(int) currentFloorX][(int) currentFloorY]].getPixels()[sixfourTimesFloorTexY + floorTexX] >> 1) & 8355711;
                    }
                    //calculate the ceiling color rgb int
                    int ceilingColorInt = (textures[map[(int) currentFloorX][(int) currentFloorY] + 6].getPixels()[sixfourTimesFloorTexY + floorTexX]);

                    //if the current location is a walk-into-able location
                    if (map[(int) currentFloorX][(int) currentFloorY] >= 0 && map[(int) currentFloorX][(int) currentFloorY] <= 2) {
                        //if self is getting damaged
                        if (self.isDamaging() && attackAnimationCnt > 15) {
                            //apply red color shift by dividing the RGB Green and Blue by 2
                            int damageFloorColor = (floorColorInt >> 16) & 0xFF;
                            damageFloorColor = (damageFloorColor << 8) + (((floorColorInt >> 8) & 0xFF) >> 1);
                            damageFloorColor = (damageFloorColor << 8) + ((floorColorInt & 0xFF) >> 1);
                            pixels[indFloor] = damageFloorColor;

                            //same as above divide the RGB green and blue by 2 ">> 1" means divide by 2
                            int damageCeilingColor = (ceilingColorInt >> 16) & 0xFF;
                            damageCeilingColor = (damageCeilingColor << 8) + (((ceilingColorInt >> 8) & 0xFF) >> 1);
                            damageCeilingColor = (damageCeilingColor << 8) + ((ceilingColorInt & 0xFF) >> 1);
                            pixels[indCeil] = damageCeilingColor;

                            //if the block is a path, then just change that color to red
                            if (pathFind[(int) currentFloorX][(int) currentFloorY] == 8 || pathFind[(int) currentFloorX][(int) currentFloorY] == 11){
                                pixels[indFloor] = 15758195;//pink-ish red
                            }
                        } else {
                            //sets the floor and ceiling
                            pixels[indFloor] = floorColorInt;
                            pixels[indCeil] = ceilingColorInt;

                            //if is Path to center
                            if (pathFind[(int) currentFloorX][(int) currentFloorY] == 8) {
                                Color orgFloorColor = new Color(floorColorInt);
                                //apply yellow color shift based on RGB value
                                pixels[indFloor] = new Color(orgFloorColor.getRed() + ((255 - orgFloorColor.getRed()) >> 1), orgFloorColor.getGreen() + ((255 - orgFloorColor.getGreen()) >> 1), orgFloorColor.getBlue() >> 2).getRGB();
                            }else if (pathFind[(int)currentFloorX][(int) currentFloorY] == 11) {//Path to player
                                Color orgFloorColor = new Color(floorColorInt);
                                //apply cyan color shift based on RGB
                                pixels[indFloor] = new Color(orgFloorColor.getRed() >> 2, orgFloorColor.getGreen() + ((255 - orgFloorColor.getGreen()) >> 1), orgFloorColor.getBlue() + ((255 - orgFloorColor.getBlue()) >> 1)).getRGB();
                            }
                        }
                    }
                }
            }
        }

        //SPRITE CASTING
        //sort sprites from far to close
        TreeSet<Player> sorted = new TreeSet<>();
        Iterator<UUID> playersUUID = players.getIterator();
        while (playersUUID.hasNext()) {
            Player curPlayer = players.getPlayer(playersUUID.next());
            //calculate for the distance using the distance formula with out squarerooting
            curPlayer.setDistance((motionListener.getxPos() - curPlayer.getX()) * (motionListener.getxPos() - curPlayer.getX()) + (motionListener.getyPos() - curPlayer.getY()) * (motionListener.getyPos() - curPlayer.getY()));
            sorted.add(curPlayer);
        }

        //after sorting the sprites, do the projection and draw them
        while (!sorted.isEmpty()) {
            //gets the current player
            Player curPlayer = sorted.pollFirst();

            //if the current player is alive
            if (curPlayer.isAlive()) {
                /// /translate sprite position to relative to motionListener
                double spriteX = curPlayer.getX() - motionListener.getxPos();
                double spriteY = curPlayer.getY() - motionListener.getyPos();

                //transform sprite with the inverse motionListener matrix
                // [ planeX   dirX ] -1                                       [ dirY      -dirX ]
                // [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
                // [ planeY   dirY ]                                          [ -planeY  planeX ]

                //required for correct inverse matrix calculation
                double inverse = 1.0 / (motionListener.getxPlane() * motionListener.getyDirection() - motionListener.getxDirection() * motionListener.getyPlane());

                //apply the inverse and calculate for the x,y of the sprite considering the FOV
                double transformX = inverse * (motionListener.getyDirection() * spriteX - motionListener.getxDirection() * spriteY);
                double transformY = inverse * (-motionListener.getyPlane() * spriteX + motionListener.getxPlane() * spriteY); //this is actually the depth inside the screen, that what Z is in 3D

                //calculate for the screen x location for the sprite
                int spriteScreenX = (int) ((halfWidth) * (1 + transformX / transformY));

                //the horizontal down translation of the sprite to make it look on ground
                int spriteHorizontalDownValue = (int) (64 / transformY);

                //calculate height of the sprite on screen
                int spriteHeight = Math.abs((int) (height / (transformY))); //using "transformY" instead of the real distance prevents fisheye
                int halfSpriteHeight = spriteHeight >> 1;

                //calculate lowest and highest pixel to fill in current stripe
                int drawStartY = -halfSpriteHeight + halfHeight + spriteHorizontalDownValue;
                if (drawStartY < 0){
                    drawStartY = 0;
                }
                int drawEndY = halfSpriteHeight + halfHeight + spriteHorizontalDownValue;
                if (drawEndY >= height){
                    drawEndY = height - 1;
                }

                //calculate width of the sprite
                int spriteWidth = Math.abs((int) (height / (transformY)));
                int halfSpriteWidth = spriteWidth >> 1;
                int drawStartX = -halfSpriteWidth + spriteScreenX;
                if (drawStartX < 0){
                    drawStartX = 0;
                }
                int drawEndX = halfSpriteWidth + spriteScreenX;
                if (drawEndX >= width){
                    drawEndX = width - 1;
                }

                //gets the walking and attacking image of the current player
                GameImage walkImage = curPlayer.getWalkImage();
                GameImage attackImage = curPlayer.getAttackImage();

                //prevent recalculation
                int oneTwoEightTimeeSpriteHeight = spriteHeight << 7;
                int oneTwoEightTimesHeight = height << 7;

                //loop through every vertical stripe of the sprite on screen
                for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                    //calculate for the sprite texture X
                    int spriteTexX = (256 * (stripe - (-halfSpriteWidth + spriteScreenX)) * spriteSize / spriteWidth) / 256;

                    /* the conditions in the if are:
                     * 1) it's in front of player's view plane so you don't see things behind you
                     * 2) it's on the screen (left)
                     * 3) it's on the screen (right)
                     * 4) perpendicularStripDistance, with perpendicular distance
                    */
                    if (transformY > 0 && stripe > 0 && stripe < width && transformY < perpendicularStripDistance[stripe]) {
                        //for every pixel of the current stripe
                        for (int y = drawStartY; y < drawEndY; y++){
                            //calculate for the texture y value
                            int texY = ((((y-spriteHorizontalDownValue) * 256 - oneTwoEightTimesHeight + oneTwoEightTimeeSpriteHeight) * spriteSize) / spriteHeight) / 256;

                            //if this pixel is valid
                            if (spriteSize * texY + spriteTexX >= 0) {
                                //gets the rgb color from the walking image
                                int color = (walkImage.getPixels()[spriteSize * texY + spriteTexX]);


                                //if the current player is attacking
                                if (curPlayer.isAttacking()) {
                                    //animation change every 15 num change
                                    if (attackAnimationCnt > 30) {
                                        attackAnimationCnt = 0;
                                    } else if (attackAnimationCnt > 15) {
                                        color = (attackImage.getPixels()[spriteSize * texY + spriteTexX]);
                                    }
                                }

                                //paint pixel if it isn't black, black is the invisible color
                                if ((color & 0x00FFFFFF) != 0) {
                                    if (curPlayer.isDamaging()){
                                        //color shifting to red color for damaging effect
                                        int hit = (color >> 16) & 0xFF;
                                        hit = (hit << 8) + (((color >> 8) & 0xFF) >> 2);
                                        hit = (hit << 8) + ((color & 0xFF) >> 2);
                                        pixels[stripe + y * (width)] = hit;
                                    }else
                                        pixels[stripe + y * (width)] = color;
                                }
                            }
                        }
                    }
                }
            }
        }
        //increase the animation value by 1
        attackAnimationCnt++;
    }

}
