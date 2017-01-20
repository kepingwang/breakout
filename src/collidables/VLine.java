package collidables;

import sprites.Sprite;

public class VLine extends Collidable {
	
	private double dx0;
	private double dx1;
	private double dy0;
	private double dy1;

	public VLine(Sprite sprite, double dx, double dy0, double dy1) {
		super(sprite);
		this.dx0 = dx;
		this.dx1 = dx;
		this.dy0 = dy0;
		this.dy1 = dy1;
	}
	
	public double x0() { return s.x() + dx0; }
	public double x1() { return s.x() + dx1; }
	public double y0() { return s.y() + dy0; }
	public double y1() { return s.y() + dy1; }

	@Override
	protected double collisionTimeSpec(VLine vl) {
		double dvx = vl.vx() - vx();
		double dvy = vl.vy() - vy();
		double dx = vl.x0() - x0();
		if (dvx * dx >= 0) { return -1; }
		double dt = - dx / dvx;
		if (intervalIntersect(
				y0() + dvy * dt, y1() + dvy * dt, vl.y0(), vl.y1()
			)) {
			return dt;
		} else {
			return -1;
		}
	}
	@Override
	protected double collisionTimeSpec(HLine hl) {
		double dvx = hl.vx() - vx();
		double dvy = hl.vy() - vy();
		double dtMin = -1;
		double dt, dx, dy;
		// horizontal collision
		if (!intervalIntersect(x0(), x1(), hl.x0(), hl.x1())) {
			if (x0() < Math.min(hl.x0(), hl.x1())) {
				dx = Math.min(hl.x0(), hl.x1()) - x0();
			} else {
				dx = Math.max(hl.x0(), hl.x1()) - x0();
			}
			if (dvx * dx >= 0) { return -1; }
			dt = - dx / dvx;
			if (intervalIntersect(
					y0(), y1(), hl.y0() + dvy * dt, hl.y1() + dvy * dt
				)) {
				dtMin = dt;
			}
		}
		// vertical collision
		if (!intervalIntersect(y0(), y1(), hl.y0(), hl.y1())) {
			if (hl.y0() < Math.min(y0(), y1())) {
				dy = hl.y0() - Math.min(y0(), y1());
			} else {
				dy = hl.y0() - Math.max(y0(), y1());
			}
			if (dvy * dy >= 0) { return -1; }
			dt = - dy / dvy;
			if (intervalIntersect(
					x0(), x1(), hl.x0() + dvx * dt, hl.x1() + dvx * dt
				)) {
				if (dtMin < 0 || dt < dtMin) {
					dtMin = dt;
				}
			}
		}
		return dtMin;
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
		return String.format("VLine %.1f, (%.1f, %.1f)", x0(), y0(), y1());
	}
	
}
