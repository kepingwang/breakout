package collidables;

import sprites.Sprite;

public class HLine extends Collidable {

	private double dx0;
	private double dx1;
	private double dy0;
	private double dy1;

	public HLine(Sprite sprite, double dx0, double dx1, double dy) {
		super(sprite);
		this.dx0 = dx0;
		this.dx1 = dx1;
		this.dy0 = dy;
		this.dy1 = dy;
	}
	
	public double x0() { return s.x() + dx0; }
	public double x1() { return s.x() + dx1; }
	public double y0() { return s.y() + dy0; }
	public double y1() { return s.y() + dy1; }

	@Override
	protected double collisionTimeSpec(HLine hl) {
		double dvx = hl.vx() - vx();
		double dvy = hl.vy() - vy();
		double dy = hl.y0() - y0();
		if (dvy * dy >= 0) { return -1;	}
		double dt = - dy / dvy;
		if (intervalIntersect(
				x0() + dvx * dt, x1() + dvx * dt, hl.x0(), hl.x1()
			)) {
			return dt;
		} else {
			return -1;
		}
	}
	@Override
	protected double collisionTimeSpec(VLine vl) {
		return vl.collisionTimeSpec(this);
	}
	@Override
	protected double collisionTimeSpec(Circle circle) {
		return circle.collisionTimeSpec(this);
	}
	@Override
	public double collisionTime(Collidable other) {
		return other.collisionTimeSpec(this);
	}

	@Override
	protected void collidesSpec(Circle circle) {
		circle.collidesSpec(this);
	}
	@Override
	public void collides(Collidable other) {
		other.collidesSpec(this);
	}

	@Override
	public String toString() {
		return String.format("HLine (%.1f, %.1f), %.1f", x0(), x1(), y0());
	}
	
}
