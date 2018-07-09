package me.markyhzhang.projectpather;

import me.markyhzhang.projectpather.gameengine.graphics.GameImage;
import me.markyhzhang.projectpather.gameengine.graphics.Sprite;

import java.util.UUID;

public class Player implements Comparable<Player>{

    /**
     * Sorts the player from far to close
     * @param player target comparsion player
     * @return integer
     */
	@Override
	public int compareTo(Player player) {
		//sort from far to close
		return -(int)(distance - player.distance);
	}

    /**
     * Enum for player type with their
     * string names initialized with constructor
     */
	public enum PlayerType {
		ALPHA("ALPHA"), BETA("BETA"), GAMMA("GAMMA"), DELTA("DELTA");
		private final String str;
		PlayerType(String str) {
			this.str = str;
		}
		public String getStr(){
			return str;
		}
	}

	
    /**
     * The UUID of player
     */
	private UUID id;

    /**
     * Name of player
     */
	private String name;

    /**
     * Player character type
     */
	private PlayerType type;

    /**
     * Health for this player
     */
	private double health;

    /**
     * Boolean damaging flag
     */
    private boolean isDamaging;

    /**
     * Boolean attacking flag
     */
    private boolean isAttacking;

    /**
     * The sprite of this player
     */
    private Sprite sprite;

    /**
     * Walking frames
     */
    private GameImage[] walkImages;

    /**
     * Attacking frames
     */
    private GameImage[] attackImages;

    /**
     * Distance away from the self player
     */
    private double distance;

    /**
     * For alternation between walking frames
     */
    private int walkCnt = 0;

    /**
     * For alternation between attacking frames
     */
    private int attackCnt = 0;

    /**
     * Simple constructor for this class
     * @param name String
     * @param type String
     */
    public Player(String name, String type){
		this.name = name;
		this.type = PlayerType.valueOf(type);
    }

    /**
     * Complete constructor for a player
     * @param id UUIID
     * @param name String
     * @param type String
     * @param x double
     * @param y double
     */
	Player(UUID id, String name, String type, double x, double y){
	    //calls simple constructor
		this(name,type);
		this.id = id;

		//initialize sprite
		initSprite(x,y);
		health = 100;
		isDamaging = false;
		isAttacking = false;
	}

    /**
     * Initalizes the sprite and images images
     * @param x double
     * @param y double
     */
	void initSprite(double x, double y){
		String path = "resources/characters/";
		sprite = new Sprite(x,y,new GameImage(path +this.type.getStr()+"_WALK/1.png",64));
		walkImages = new GameImage[]{
				new GameImage(path +this.type.getStr()+"_WALK/1.png",64),
				new GameImage(path +this.type.getStr()+"_WALK/2.png",64),
				new GameImage(path +this.type.getStr()+"_WALK/3.png",64),
				new GameImage(path +this.type.getStr()+"_WALK/4.png",64),
				new GameImage(path +this.type.getStr()+"_WALK/5.png",64),
				new GameImage(path +this.type.getStr()+"_WALK/6.png",64),
				new GameImage(path +this.type.getStr()+"_WALK/7.png",64)
		};
		attackImages = new GameImage[]{
				new GameImage(path + this.type.getStr()+"_ATTACK/1.png", 64),
				new GameImage(path + this.type.getStr()+"_ATTACK/2.png", 64),
				new GameImage(path + this.type.getStr()+"_ATTACK/3.png", 64),
				new GameImage(path + this.type.getStr()+"_ATTACK/4.png", 64),
				new GameImage(path + this.type.getStr()+"_ATTACK/5.png", 64),
				new GameImage(path + this.type.getStr()+"_ATTACK/6.png", 64),
				new GameImage(path + this.type.getStr()+"_ATTACK/7.png", 64)
		};
	}

    /**
     * Getter for damaging flag
     * @return boolean
     */
	public boolean isDamaging() {
		return isDamaging;
	}

    /**
     * Setter damaging
     * @param isDamaging boolean
     */
	void setDamaging(boolean isDamaging) {
		this.isDamaging = isDamaging;
	}

    /**
     * Getter for is attacking
     * @return boolean
     */
	public boolean isAttacking() {
		return isAttacking;
	}

    /**
     * Setter for attacking
     * @param isAttacking boolean
     */
	void setAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
	}

    /**
     * Getter for UUID
     * @return UUID
     */
	public UUID getId() {
		return id;
	}

    /**
     * Getter for name
     * @return String
     */
	String getName() {
		return name;
	}

    /**
     * Getter for player type
     * @return PlayerType
     */
	PlayerType getType() {
		return type;
	}

    /**
     * Getter for health
     * @return double
     */
	public double getHealth() {
		return health;
	}

    /**
     * Getter for if player is still alive
     * @return boolean
     */
	public boolean isAlive(){
		return getHealth() > 0;
	}

    /**
     * Setter for UUID
     * @param id UUID
     */
	void setId(UUID id) {
		this.id = id;
	}

    /**
     * Setter for nmae
     * @param name String
     */
	void setName(String name) {
		this.name = name;
	}

    /**
     * Setter for health
     * @param health double
     */
	void setHealth(double health) {
		this.health = health;
	}

    /**
     * Returns the current walking image
     * @return GameImage
     */
	public GameImage getWalkImage() {
		if (walkCnt==70){
			walkCnt = 0;
		}
		return walkImages[walkCnt++/10];
	}

    /**
     * Returns the current attack image
     * @return GameImage
     */
    public GameImage getAttackImage() {
        if (attackCnt==70){
            attackCnt = 0;
        }
        return attackImages[attackCnt++/10];
    }

    /**
     * Getter for the x value
     * @return double
     */
	public double getX(){
		return sprite.getX();
	}

    /**
     * Setter for the x value
     * @param x double
     */
	public void setX(double x){
		sprite.setX(x);
	}

    /**
     * Getter for the y value
     * @return double
     */
	public double getY(){
		return sprite.getY();
	}

    /**
     * Setter for the y value
     * @param y double
     */
	public void setY(double y){
        sprite.setY(y);
	}

    /**
     * Setter for the distance between this player to the self player
     * @param distance double
     */
	public void setDistance(double distance) {
		this.distance = distance;
	}

    /**
     * Override toString() method for debugging
     * @return String
     */
	@Override
	public String toString() {
		return "Player{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
