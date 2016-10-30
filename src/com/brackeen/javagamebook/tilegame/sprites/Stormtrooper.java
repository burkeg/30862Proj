package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    A Stormtrooper is a Creature that moves slowly on the ground and shoots at the player.
*/
public class Stormtrooper extends Creature {
	private long timeWithPlayerOnScreen = 0;
	private long timeSinceLastShot = 0;
    public Stormtrooper(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
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

}
