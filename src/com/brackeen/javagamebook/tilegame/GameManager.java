package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.*;

/**
 * GameManager manages all parts of the game.
 */
public class GameManager extends GameCore {

	public static void main(String[] args) {
		new GameManager().run();
	}

	// uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
	private static final AudioFormat PLAYBACK_FORMAT = new AudioFormat(44100,
			16, 1, true, false);

	private static final int DRUM_TRACK = 1;

	public static final float GRAVITY = 0.002f;

	private Point pointCache = new Point();
	private TileMap map;
	private MidiPlayer midiPlayer;
	private SoundManager soundManager;
	private ResourceManager resourceManager;
	private Sound prizeSound;
	private Sound boopSound;
	private InputManager inputManager;
	private TileMapRenderer renderer;
	private long healthTimer = 0;
	private long bulletTimer = 0;
	private GameAction moveLeft;
	private GameAction moveRight;
	private GameAction jump;
	private GameAction exit;
	private GameAction shoot;

	public void init() {
		super.init();

		// set up input manager
		initInput();

		// start resource manager
		resourceManager = new ResourceManager(screen.getFullScreenWindow()
				.getGraphicsConfiguration());

		// load resources
		renderer = new TileMapRenderer();
		renderer.setBackground(resourceManager.loadImage("background2.png"));

		// load first map
		map = resourceManager.loadNextMap();

		// load sounds
		soundManager = new SoundManager(PLAYBACK_FORMAT);
		prizeSound = soundManager.getSound("sounds/prize.wav");
		boopSound = soundManager.getSound("sounds/boop2.wav");

		// start music
		midiPlayer = new MidiPlayer();
		Sequence sequence = midiPlayer.getSequence("sounds/music.midi");
		midiPlayer.play(sequence, true);
		toggleDrumPlayback();
	}

	/**
	 * Closes any resources used by the GameManager.
	 */
	public void stop() {
		super.stop();
		midiPlayer.close();
		soundManager.close();
	}

	private void initInput() {
		moveLeft = new GameAction("moveLeft");
		moveRight = new GameAction("moveRight");
		jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
		exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		shoot = new GameAction("shoot");

		inputManager = new InputManager(screen.getFullScreenWindow());
		inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

		inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
		inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(shoot, KeyEvent.VK_S);
	}

	private void checkInput(long elapsedTime) {

		if (exit.isPressed()) {
			stop();
		}

		float accel = 1.05f;
		float decel = 0.96f;
		Player player = (Player) map.getPlayer();
		// System.out.println(GameCore.timeElapsed - healthTimer +
		// ":"+player.getVelocityX()+":"+player.getVelocityX());
		if (player.getVelocityX() != 0.0 || player.getVelocityY() != 0.0) {
			healthTimer = 0;
		}
		if (player.getVelocityX() == 0 && player.getVelocityY() == 0
				&& healthTimer == 0 && player.getSpawn() != 1) {
			healthTimer = GameCore.timeElapsed;
		}

		if (GameCore.timeElapsed - healthTimer > 1000 && healthTimer != 0) {
			map.getPlayer().incrementHealth(5);
			healthTimer = 0;
		}
		Sprite projectileSprite = resourceManager.newProjectileSprite();
		if (player.isAlive()) {
			float velocityX = player.getVelocityX();
			if (moveLeft.isPressed()) {
				player.setSpawn(0);
				player.setOrientation(-1);
				if (player.getVelocityX() == 0) {
					velocityX = -player.getMaxSpeed() / 2.0f;
				} else {
					velocityX *= accel; // ACCELERATION
				}
			}
			if (moveRight.isPressed()) {
				player.setOrientation(1);
				player.setSpawn(0);
				if (player.getVelocityX() == 0) {
					velocityX = player.getMaxSpeed() / 2.0f;
				} else {
					velocityX *= accel; // ACCELERATION
				}
			}
			if (jump.isPressed()) {
				player.setSpawn(0);
				player.jump(false);
			}
			if (player.getBullets() == 0) {
				if (GameCore.timeElapsed - bulletTimer > 1000) {
					player.setBullets(10);
					bulletTimer = 0;
				}
			}
			if (shoot.isPressed()) {
				player.setSpawn(0);
				if (player.getBullets() > 0) {
					System.out.println("timeElapse: " + GameCore.timeElapsed
							+ " bulletTimer: " + bulletTimer);
					System.out.println("Differece: "
							+ (GameCore.timeElapsed - bulletTimer));
					if (GameCore.timeElapsed - bulletTimer > 200
							|| bulletTimer == 0) {
						projectileSprite.setX((int) player.getX());
						projectileSprite.setY((int) player.getY());
						projectileSprite.setVelocityX(player.getMaxSpeed()
								* player.getOrientation());
						map.addSprite(projectileSprite);
						player.decBullets(1);
						bulletTimer = GameCore.timeElapsed;
					}
				}

			}
			if (velocityX > 0.1f) {

				if (moveLeft.isPressed()) {
					velocityX *= Math.pow(decel, 6); // aggro
					// System.out.println("aggroSlowRight"); // DECELLERATION
				} else // if (!moveRight.isPressed()) {
				{
					velocityX *= decel; // low DECELLERATION
					// System.out.println("regSlowRight"); // DECELLERATION
				}
				if (velocityX > player.getMaxSpeed()) {
					velocityX = player.getMaxSpeed();
				}
			} else if (velocityX < -0.1f) {

				if (moveRight.isPressed()) {
					velocityX *= Math.pow(decel, 6); // aggro
					// System.out.println("aggroSlowLeft"); // DECELLERATION
				} else // if (!moveRight.isPressed()) {
				{
					// System.out.println(velocityX + " * " + decel + " = " +
					// velocityX*decel);

					velocityX *= decel; // low DECELLERATION
					// System.out.println("regSlowLeft"); // DECELLERATION
				}
				if (velocityX < -player.getMaxSpeed()) {
					velocityX = -player.getMaxSpeed();
				}
			}
			if (velocityX < 0.1f && velocityX > -0.1f) {
				velocityX = 0;
			}

			// System.out.println(velocityX);
			player.setVelocityX(velocityX);
		} else
			player.setVelocityX(0);

	}

	public void draw(Graphics2D g) {
		renderer.draw(g, map, screen.getWidth(), screen.getHeight());
		g.drawString("Health: " + map.getPlayer().getHealth(), 20, 50);
		g.drawString("Bullets: " + map.getPlayer().getBullets(), 140, 50);
		g.drawString("Score:", 20, 80);
	}

	/**
	 * Gets the current map.
	 */
	public TileMap getMap() {
		return map;
	}

	/**
	 * Turns on/off drum playback in the midi music (track 1).
	 */
	public void toggleDrumPlayback() {
		Sequencer sequencer = midiPlayer.getSequencer();
		if (sequencer != null) {
			sequencer.setTrackMute(DRUM_TRACK,
					!sequencer.getTrackMute(DRUM_TRACK));
		}
	}

	/**
	 * Gets the tile that a Sprites collides with. Only the Sprite's X or Y
	 * should be changed, not both. Returns null if no collision is detected.
	 */
	public Point getTileCollision(Sprite sprite, float newX, float newY) {
		float fromX = Math.min(sprite.getX(), newX);
		float fromY = Math.min(sprite.getY(), newY);
		float toX = Math.max(sprite.getX(), newX);
		float toY = Math.max(sprite.getY(), newY);

		// get the tile locations
		int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
		int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
		int toTileX = TileMapRenderer
				.pixelsToTiles(toX + sprite.getWidth() - 1);
		int toTileY = TileMapRenderer.pixelsToTiles(toY + sprite.getHeight()
				- 1);

		// check each tile for a collision
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if (x < 0 || x >= map.getWidth() || map.getTile(x, y) != null) {
					// collision found, return the tile
					pointCache.setLocation(x, y);
					return pointCache;
				}
			}
		}

		// no collision found
		return null;
	}

	/**
	 * Checks if two Sprites collide with one another. Returns false if the two
	 * Sprites are the same. Returns false if one of the Sprites is a Creature
	 * that is not alive.
	 */
	public boolean isCollision(Sprite s1, Sprite s2) {
		// if the Sprites are the same, return false
		if (s1 == s2) {
			return false;
		}

		// if one of the Sprites is a dead Creature, return false
		if (s1 instanceof Creature && !((Creature) s1).isAlive()) {
			return false;
		}
		if (s2 instanceof Creature && !((Creature) s2).isAlive()) {
			return false;
		}

		// get the pixel location of the Sprites
		int s1x = Math.round(s1.getX());
		int s1y = Math.round(s1.getY());
		int s2x = Math.round(s2.getX());
		int s2y = Math.round(s2.getY());

		// check if the two sprites' boundaries intersect
		return (s1x < s2x + s2.getWidth() && s2x < s1x + s1.getWidth()
				&& s1y < s2y + s2.getHeight() && s2y < s1y + s1.getHeight());
	}

	/**
	 * Gets the Sprite that collides with the specified Sprite, or null if no
	 * Sprite collides with the specified Sprite.
	 */
	public Sprite getSpriteCollision(Sprite sprite) {

		// run through the list of Sprites
		Iterator i = map.getSprites();
		while (i.hasNext()) {
			Sprite otherSprite = (Sprite) i.next();
			if (isCollision(sprite, otherSprite)) {
				// collision found, return the Sprite
				return otherSprite;
			}
		}

		// no collision found
		return null;
	}

	/**
	 * Updates Animation, position, and velocity of all Sprites in the current
	 * map.
	 */
	public void update(long elapsedTime) {
		Creature player = (Creature) map.getPlayer();
		timeElapsed += elapsedTime;
		// player is dead! start map over
		if (player.getState() == Creature.STATE_DEAD) {
			map = resourceManager.reloadMap();
			return;
		}

		// get keyboard/mouse input
		checkInput(elapsedTime);

		// update player
		updateCreature(player, elapsedTime);
		player.update(elapsedTime);

		// update other sprites
		Iterator i = map.getSprites();
		while (i.hasNext()) {
			Sprite sprite = (Sprite) i.next();
			if (sprite instanceof Creature) {
				Creature creature = (Creature) sprite;
				if (creature.getState() == Creature.STATE_DEAD) {
					i.remove();
				} else {
					updateCreature(creature, elapsedTime);
				}
			}
			// normal update
			sprite.update(elapsedTime);
		}

	}

	/**
	 * Updates the creature, applying gravity for creatures that aren't flying,
	 * and checks collisions.
	 */
	private void updateCreature(Creature creature, long elapsedTime) {

		// apply gravity
		if (!creature.isFlying()) {
			creature.setVelocityY(creature.getVelocityY() + GRAVITY
					* elapsedTime);
		}

		// change x
		float dx = creature.getVelocityX();
		float oldX = creature.getX();
		float newX = oldX + dx * elapsedTime; // HERE IS DISTANCE TRAVELLED
		Point tile = getTileCollision(creature, newX, creature.getY());
		if (tile == null) {
			creature.setX(newX);
		} else {
			// line up with the tile boundary
			if (dx > 0) {
				creature.setX(TileMapRenderer.tilesToPixels(tile.x)
						- creature.getWidth());
			} else if (dx < 0) {
				creature.setX(TileMapRenderer.tilesToPixels(tile.x + 1));
			}
			creature.collideHorizontal();
		}
		if (creature instanceof Player) {
			checkPlayerCollision((Player) creature, false);
		}
		if (creature instanceof Projectile) {
			checkProjectileCollision((Projectile) creature, true);
		}

		// change y
		float dy = creature.getVelocityY();
		float oldY = creature.getY();
		float newY = oldY + dy * elapsedTime;
		tile = getTileCollision(creature, creature.getX(), newY);
		if (tile == null) {
			creature.setY(newY);
		} else {
			// line up with the tile boundary
			if (dy > 0) {
				creature.setY(TileMapRenderer.tilesToPixels(tile.y)
						- creature.getHeight());
			} else if (dy < 0) {
				creature.setY(TileMapRenderer.tilesToPixels(tile.y + 1));
			}
			creature.collideVertical();
		}
		if (creature instanceof Player) {
			boolean canKill = (oldY < creature.getY());
			checkPlayerCollision((Player) creature, canKill);
		}
		if (creature instanceof Projectile) {
			checkProjectileCollision((Projectile) creature, true);
		}

	}

	/**
	 * Checks for Player collision with other Sprites. If canKill is true,
	 * collisions with Creatures will kill them.
	 */
	public void checkPlayerCollision(Player player, boolean canKill) {
		if (!player.isAlive()) {
			map.getPlayer().setHealth(20);
			map.getPlayer().setSpawn(1);
			return;
		}

		// check for player collision with other sprites
		Sprite collisionSprite = getSpriteCollision(player);
		if (collisionSprite instanceof PowerUp) {
			acquirePowerUp((PowerUp) collisionSprite);
		} else if (collisionSprite instanceof Creature) {
			Creature badguy = (Creature) collisionSprite;
			if (badguy instanceof Projectile
					&& !((Projectile) badguy).getIsFriendly()) {
				player.setState(Creature.STATE_DYING);
				return;
			}
			if (canKill) {
				// kill the badguy and make player bounce
				if (!(badguy instanceof Projectile)) {
					soundManager.play(boopSound);
					badguy.setState(Creature.STATE_DYING);
					player.incrementHealth(10);
					player.setY(badguy.getY() - player.getHeight());
					player.jump(true);
				}
			} else {
				// player dies!
				if (badguy instanceof Projectile
						&& !((Projectile) badguy).getIsFriendly()) {
					player.setState(Creature.STATE_DYING);
				}
			}
		}
	}

	/**
	 * Checks for Player collision with other Sprites. If canKill is true,
	 * collisions with Creatures will kill them.
	 */
	public void checkProjectileCollision(Projectile proj, boolean canKill) {
		if (!proj.isAlive()) {
			return;
		}

		// check for player collision with other sprites
		Sprite collisionSprite = getSpriteCollision(proj);
		if (collisionSprite instanceof Creature) {
			Creature badguy = (Creature) collisionSprite;
			if (badguy instanceof Projectile || badguy instanceof Player
					&& proj.getIsFriendly()) {
				return;
			}

			soundManager.play(boopSound);
			badguy.setState(Creature.STATE_DYING);
			proj.setState(Creature.STATE_DEAD);

		}
	}

	/**
	 * Gives the player the speicifed power up and removes it from the map.
	 */
	public void acquirePowerUp(PowerUp powerUp) {
		// remove it from the map
		map.removeSprite(powerUp);

		if (powerUp instanceof PowerUp.Star) {
			// do something here, like give the player points
			soundManager.play(prizeSound);
		} else if (powerUp instanceof PowerUp.Music) {
			// change the music
			soundManager.play(prizeSound);
			toggleDrumPlayback();
		} else if (powerUp instanceof PowerUp.Goal) {
			// advance to next map
			soundManager.play(prizeSound, new EchoFilter(2000, .7f), false);
			map = resourceManager.loadNextMap();
		}
	}

}
