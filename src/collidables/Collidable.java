package collidables;

import sprites.Sprite;

/**
 * Each collidable belongs to a sprite. The Collidable class remembers the relative 
 * positions of its points with respect to the center (x, y) of its sprite. On 
 * collision detection, the Collidable get its position and velocity from its sprite.
 * 
 * @author keping
 */
public abstract class Collidable {
	
	/**
	 * The sprite where this Collidable belongs.
	 */
	protected Sprite s;
	
	public Collidable(Sprite sprite) {
		this.s = sprite;
	}
	
	public double vx() { return s.vx(); }
	public double vy() { return s.vy(); }
	public double m() { return s.m(); }
	public Sprite sprite() { return s; }
	
	/**
	 * [a0, a1] or [a1, a0]. [b0, b1] or [b1, b0]
	 * A helper method.
	 * 
	 * @param a0
	 * @param a1
	 * @param b0
	 * @param b1
	 * @return
	 */
	public static boolean intervalIntersect(double a0, double a1, double b0, double b1) {
		// make sure a0 <= a1 and b0 <= b1
		if (a0 > a1) {
			double aTmp = a0;
			a0 = a1;
			a1 = aTmp;
		}
		if (b0 > b1) {
			double bTmp = b0;
			b0 = b1;
			b1 = bTmp;
		}
		return !(a1 < b0 || b1 < a0);
	}
	
	protected double collisionTimeSpec(VLine vl) { return -1; }
	protected double collisionTimeSpec(HLine hl) { return -1; }
	protected double collisionTimeSpec(Circle circle) { return -1; }
	/**
	 * Return collision time, -1 if no collision.
	 * @param other
	 * @return 
	 */
	public abstract double collisionTime(Collidable other);	
	
	protected void collidesSpec(VLine vl) { }
	protected void collidesSpec(HLine hl) { }
	protected void collidesSpec(Circle circle) { }
	/**
	 * Physical effects of the collision take place.
	 * @param other
	 */
	public abstract void collides(Collidable other);
}
