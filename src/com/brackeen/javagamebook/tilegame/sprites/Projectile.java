package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    The projectile
*/
public class Projectile extends Creature {
	private float maxSpeed;
	private boolean isFriendly;
	
	
	public void decDistanceLeft(float dec) {
		distanceLeft -= Math.abs(dec);
		if (distanceLeft < 0)
			this.setState(STATE_DEAD);
	}
	
    public Projectile(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        maxSpeed = 0.6f;
        isFriendly = true;
    }
    
    public boolean getIsFriendly() {
    	return isFriendly;
    }
    
    public void setIsFriendly(boolean state) {
    	isFriendly = state;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }
    
    public void setMaxSpeed(float maxSpeed) {
    	this.maxSpeed = maxSpeed;
    }

    public boolean isFlying() {
        return isAlive();
    }

}
