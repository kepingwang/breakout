package collidables;

import breakout.GameApp;
import sprites.Bat;
import sprites.Sprite;

public class Circle extends Collidable {
	
	private double dx;
	private double dy;
	private double r;
	
	public Circle(Sprite sprite, double dx, double dy, double r) {
		super(sprite);
		this.dx = dx;
		this.dy = dy;
		this.r = r;
	}
	
	public double x() { return s.x() + dx; }
	public double y() { return s.y() + dy; }
	public double r() { return r; }
	public void setDPos(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	public void setR(double r) { this.r = r; } 
	
	@Override
	protected double collisionTimeSpec(VLine vl) {
		double dvx = vl.vx() - vx();
		double dvy = vl.vy() - vy();
		double dx = vl.x0() - x();
		if (dvx * dx >= 0 || Math.abs(dx) <= r()) { return -1; }
		double dt = (Math.abs(dx) - r()) / Math.abs(dvx); 
		if (intervalIntersect(
				y(), y(), vl.y0() + dvy * dt, vl.y1() + dvy * dt
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
		double dy = hl.y0() - y();
		if (dvy * dy >= 0 || Math.abs(dy) <= r()) { return -1; }
		double dt = (Math.abs(dy) - r()) / Math.abs(dvy);	
		if (intervalIntersect(
				x(), x(), hl.x0() + dvx * dt, hl.x1() + dvx * dt
			)) {
			return dt;
		} else {
			return -1;
		}
	}
	@Override
	protected double collisionTimeSpec(Circle c1) {
		Circle c0 = this;
		double dx = c1.x() - c0.x();
		double dy = c1.y() - c0.y();
		double dvx = c1.vx() - c0.vx();
		double dvy = c1.vy() - c0.vy();
		double R = c0.r() + c1.r();
		// solve || d + dv*dt || = R
		// ||(dx+dvx*t, dy+dvy*t)|| = R
		// (dvx^2+dvy^2)t^2 + 2(dvx*dx+dvy*dy)t + (dx^2+dy^2-R^2) = 0
		// a*t^2 + b*t + c = 0
		// delta = b^2 - 4ac
		// t = （- b +- \sqrt{delta}）/ (2a)
		double a = dvx * dvx + dvy * dvy;
		double b = 2 * (dvx * dx + dvy * dy);
		double c = dx * dx + dy * dy - R * R;
		double delta = b * b - 4 * a * c;

		// t cannot be negative. cannot be dvx*dx+dvy*dy >= 0
		if (b >= 0 || delta < 0 || c <= 0) {
			return -1;
		}
		return (-b - Math.sqrt(delta)) / (2 * a);
	}
	@Override
	public double collisionTime(Collidable other) {
		return other.collisionTimeSpec(this);
	}

	@Override
	protected void collidesSpec(VLine vl) {
		s.setVX(-vx());
		if (vl.x0() < x()) {
			s.setX(vl.x0() + r() + GameApp.EPS_DIST);
		} else {
			s.setX(vl.x0() - r() - GameApp.EPS_DIST);
		}
	}
	protected void ballCollidesBatEdge(HLine hl) {
		double v = Math.sqrt(vx()*vx() + vy()*vy());
		double angle = 0.9 * (Math.PI / 2) * ( 
				(x() - (hl.x0() + hl.x1()) / 2) / Math.abs(hl.x1() - hl.x0())
			);
		s.setV(v * Math.sin(angle), - v * Math.cos(angle));
	}
	@Override
	protected void collidesSpec(HLine hl) {
		if (hl.s instanceof Bat) { ballCollidesBatEdge(hl); }
		else { s.setVY(-vy()); }
		if (hl.y0() < y()) {
			s.setY(hl.y0() + r() + GameApp.EPS_DIST);
		} else {
			s.setY(hl.y0() - r() - GameApp.EPS_DIST);
		}
	}
	@Override
	protected void collidesSpec(Circle other) {
		// call the movable circle c0, the immovable circle c1;
		Circle c0 = this;
		Circle c1 = other;
		if (c0.s.m() < 0) {
			c0 = other;
			c1 = this;
		}
		// vOut = vIn - 2(vIn \dot n) n  // n going orthogonal out of mirror, normalized
		double nx = (c0.x() - c1.x()) / (c0.r() + c1.r());
		double ny = (c0.y() - c1.y()) / (c0.r() + c1.r());
		double coeff = 2 * (c0.vx() * nx + c0.vy() * ny);
		double vxOut = c0.vx() - coeff * nx;
		double vyOut = c0.vy() - coeff * ny;
		c0.s.setV(vxOut, vyOut);
		// Move the ball out of the brick / bat
		double dx = c0.x() - c1.x();
		double dy = c0.y() - c1.y();
		double dr = Math.sqrt(dx*dx + dy*dy);
		c0.s.setPos(c1.x()+dx/dr*(dr+GameApp.EPS_DIST), 
					c1.y()+dy/dr*(dr+GameApp.EPS_DIST));
		
		if (c1.s instanceof Bat) {
			c1.s.setVX(0);
		}
	}
	@Override
	public void collides(Collidable other) {
		// For all these collisions, if one is a line, the other is a circle,
		// we assume that m(line) = -1, m(circle) > 0. Bricks, Bat, and Walls
		// cannot be hit to move by the ball.
		// For line-line collisions, we assume them not to happen in this
		// breakout game.
		other.collidesSpec(this);
	}
	
	@Override
	public String toString() {
		return String.format("Circle (%.1f, %.1f), r=%.1f", x(), y(), r);
	}
}
