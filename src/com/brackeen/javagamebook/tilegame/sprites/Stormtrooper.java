package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Stormtrooper is a Creature that moves slowly on the ground and shoots at the player.
*/
public class Stormtrooper extends Creature {
	private long timeWithPlayerOnScreen = 0;
	private long timeSinceLastShot = 0;
	private float bulletSpeed = 0.25f;
	private long bulletTimer = 0;
	public static int fireRate; //once everty 400ms
	
    public Stormtrooper(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        fireRate = Player.fireRate*2;
    }


    public float getMaxSpeed() {
        return 0.05f;
    }
    
    public float getBulletSpeed() {
		return bulletSpeed ;
	}


	public long getTimeWithPlayerOnScreen() {
		return timeWithPlayerOnScreen;
	}


	public void setTimeWithPlayerOnScreen(long timeWithPlayerOnScreen) {
		this.timeWithPlayerOnScreen = timeWithPlayerOnScreen;
	}
	
	public void incTimeWithPlayerOnScreen(long timeWithPlayerOnScreen) {
		this.timeWithPlayerOnScreen += timeWithPlayerOnScreen;
	}


	public long getTimeSinceLastShot() {
		return timeSinceLastShot;
	}


	public void setTimeSinceLastShot(long timeSinceLastShot) {
		this.timeSinceLastShot = timeSinceLastShot;
	}
	
	public void incTimeSinceLastShot(long timeSinceLastShot) {
		this.timeSinceLastShot += timeSinceLastShot;
	}


	public long getBulletTimer() {
		return bulletTimer;
	}


	public void setBulletTimer(long bulletTimer) {
		this.bulletTimer = bulletTimer;
	}
	
	public void incBulletTimer(long bulletTimer) {
		this.bulletTimer += bulletTimer;
	}

}
