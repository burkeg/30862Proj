package com.brackeen.javagamebook.graphics;

import java.awt.Image;

public class Sprite {

	protected Animation anim;
	// position (pixels)
	private float x;
	private float y;
	// velocity (pixels per millisecond)
	private float dx;
	private float dy;
	private int health = 20;
	private int bulletsLeft = 10;
	private int orientation = 1; // facing right
	private float distanceTraveled = 0;
	private int spawn = 1; // new spawn shouldn't be able to regenerate health
	private boolean invincible = false;
	protected float distanceLeft = 430.0f;
	private long total_distance_traveled = 0;

	/**
	 * Creates a new Sprite object with the specified Animation.
	 */
	public int getSpawn() {
		return spawn;
	}

	public void setSpawn(int i) {
		spawn = i;
	}

	public float getDistanceTraveled() {
		return distanceTraveled;
	}

	public void incDistanceTraveled(float dist) {
		this.distanceTraveled += dist;
		this.total_distance_traveled += dist;
	}

	public void setDistanceTraveled(float dist) {
		this.distanceTraveled = dist;
	}

	public int getOrientation() {
		return orientation;
	}

	public int getOrientationMoving() {
		return (this.getVelocityX() > 0) ? 1 : -1;
	}

	public void setOrientation(int dir) {
		this.orientation = dir;
	}

	public Sprite(Animation anim) {
		this.anim = anim;
	}

	public void setHealth(int h) {
		if (!isInvincible())
			this.health = h;
	}

	public void incrementHealth(int h) {
		if (!isInvincible()) {
			this.health += h;
			if (this.health > 40)
				this.health = 40;
		}
	}

	public int getHealth() {
		return health;
	}

	public void setBullets(int b) {
		this.bulletsLeft = b;
	}

	public int getBullets() {
		return bulletsLeft;
	}

	public void decBullets(int dec) {
		this.bulletsLeft = this.bulletsLeft - dec;
	}

	/**
	 * Updates this Sprite's Animation and its position based on the velocity.
	 */
	public void update(long elapsedTime) {
		x += dx * elapsedTime;
		y += dy * elapsedTime;
		anim.update(elapsedTime);
	}

	/**
	 * Gets this Sprite's current x position.
	 */
	public float getX() {
		return x;
	}

	/**
	 * Gets this Sprite's current y position.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets this Sprite's current x position.
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Sets this Sprite's current y position.
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Gets this Sprite's width, based on the size of the current image.
	 */
	public int getWidth() {
		return anim.getImage().getWidth(null);
	}

	/**
	 * Gets this Sprite's height, based on the size of the current image.
	 */
	public int getHeight() {
		return anim.getImage().getHeight(null);
	}

	/**
	 * Gets the horizontal velocity of this Sprite in pixels per millisecond.
	 */
	public float getVelocityX() {
		return dx;
	}

	/**
	 * Gets the vertical velocity of this Sprite in pixels per millisecond.
	 */
	public float getVelocityY() {
		return dy;
	}

	/**
	 * Sets the horizontal velocity of this Sprite in pixels per millisecond.
	 */
	public void setVelocityX(float dx) {
		this.dx = dx;
	}

	/**
	 * Sets the vertical velocity of this Sprite in pixels per millisecond.
	 */
	public void setVelocityY(float dy) {
		this.dy = dy;
	}

	/**
	 * Gets this Sprite's current image.
	 */
	public Image getImage() {
		return anim.getImage();
	}

	/**
	 * Clones this Sprite. Does not clone position or velocity info.
	 */
	public Object clone() {
		return new Sprite(anim);
	}

	public boolean isInvincible() {
		return invincible;
	}

	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}

	public long getTotal_distance_traveled() {
		return total_distance_traveled;
	}

}
