package sprites;

import breakout.GameWorld;
import collidables.Collidable;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;

public abstract class Sprite implements Displayable {
	
	protected GameWorld world;
	private double x;
	private double y;
	private double vx = 0;
	private double vy = 0;
	/**
	 * Mass of the sprite. -1 if infinity.
	 */
	private double m;
	protected Collidable[] collidables = null;
	private long sysTimeLastCollision;
	
	public Sprite(GameWorld world, double x, double y, double m) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.m = m;
	}
	protected abstract void initCollidables();
	
	public double x() { return x; }
	public double y() { return y; }
	public double vx() { return vx; }
	public double vy() { return vy; }
	public double m() { return m; }
	
	/**
	 * Set v and predict collisions in the game world for this Sprite. 
	 * Thus <b>all changes (including initialization) in speed shall be
	 * made using this method call</b>. 
	 * @param vx
	 * @param vy
	 */
	public void setV(double vx, double vy) {
		this.vx = vx;
		this.vy = vy;
		handlePostCollision();
	}
	public void setVX(double vx) { setV(vx, vy); }
	public void setVY(double vy) { setV(vx, vy); }
	/**
	 * Resetting x, y position (not including updating with time) 
	 * requires calling this method.
	 * @param x
	 * @param y
	 */
	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
		handlePostCollision();
	}
	public void setX(double x) { setPos(x, y); }
	public void setY(double y) { setPos(x, y); }
	public Collidable[] collidables() { return collidables; }
	
	public boolean exists() { return world.getAllSprites().contains(this); }

	public boolean trajectoryUnchangedAfter(double t) {
		return (sysTimeLastCollision <= t);
	}
	public void handlePostCollision() {
		sysTimeLastCollision = System.nanoTime();
		world.predictCollisions(this);
	}
	
	protected Collision predictCollisionImpl(Sprite other) {
		if (collidables == null || other.collidables == null) { return null; }
		double dtMin = -1;
		Collidable collidable0 = null;
		Collidable collidable1 = null;
		for (Collidable c0 : collidables) {
			for (Collidable c1 : other.collidables) {
				double dt = c0.collisionTime(c1);
				if (dt > 0 && (dtMin < 0 || dt < dtMin)) {
					dtMin = dt;
					collidable0 = c0;
					collidable1 = c1;
				}
			}
		}
		if (dtMin > 0) {
			return new Collision(collidable0, collidable1, world.currTime() + dtMin);
		} else {
			return null;
		}
	}
	protected Collision predictCollisionSpec(Ball ball) { return null; }
	protected Collision predictCollisionSpec(Bat bat) { return null; }
	protected Collision predictCollisionSpec(Wall wall) { return null; }
	protected Collision predictCollisionSpec(Brick brick) { return null; }
	protected Collision predictCollisionSpec(PowerUp powerUp) { return null; }
	public abstract Collision predictCollision(Sprite other);

	protected void collisionEffectsSpec(Ball ball) { }
	protected void collisionEffectsSpec(Bat bat) { }
	protected void collisionEffectsSpec(Wall wall) { }
	protected void collisionEffectsSpec(Brick brick) { }
	protected void collisionEffectsSpec(PowerUp powerUP) { }
	public abstract void collisionEffects(Sprite other);
	
	
	/**
	 * Only update (x, y) positions according to (vx, vy). Assume no
	 * acceleration. No other effects.
	 * @param dt
	 */
	protected void updatePos(double dt) {
		x += vx * dt;
		y += vy * dt;
	}

	public abstract void update(double dt);

	public abstract void render(GraphicsContext gc);
	
	protected abstract String spriteName();
	@Override
	public String toString() {
		return String.format(
				spriteName()+" (%.1f, %.1f), v(%.1f, %.1f)",
				x, y, vx, vy);
	}
}
