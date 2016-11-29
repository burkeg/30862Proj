package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {
    private static final float JUMP_SPEED = -.95f;
    private int shots_left = 10;
    private boolean onGround;
    private float bulletSpeed = 0.5f;
    public static final int fireRate = 200;
    private int score = 0;
    private long iFrameTimer = 0;
    public final float yVelocity = 0.5f; 
    private long iFrameDistance = 0;
    public int collisionTile = 0;
    private boolean flying = false;
    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }
    
    public int getScore() {
    	return score;
    }
    public void setScore(int s) {
    	this.score = s;
    }
    public void incScore(int s) {
    	if (s < 0 && this.isInvincible())
    		return;
    	this.score += s;
    }
    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }

    public boolean canShoot() {
    	this.shots_left--;
    	System.out.println(shots_left + " shots_left.");
    	if (this.shots_left == 0) {
    		//reload
    		this.shots_left = 10;
    		return false;
    	} 
    	return true;
    }
    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }
    
    public boolean isFlying() {
        return flying;
    }
    
    public void setFlying(boolean state) {
    	flying = state;
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }


	public float getBulletSpeed() {
		return bulletSpeed;
	}

	public long getiFrameTimer() {
		return iFrameTimer;
	}

	public void setiFrameTimer(long iFrameTimer) {
		this.iFrameTimer = iFrameTimer;
	}

	public long getiFrameDistance() {
		return iFrameDistance;
	}

	public void setiFrameDistance(long iFrameDistance) {
		this.iFrameDistance = iFrameDistance;
	}

}
