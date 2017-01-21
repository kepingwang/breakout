package sprites;

import breakout.GameApp;
import collidables.Circle;
import collidables.Collidable;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;

public class Ball extends Sprite {
	
	public static final double INIT_SPEED = 360;
	public static final double MIN_SPEED = 10;
	public static final double MAX_SPEED = 6000;
	public static final double INIT_RADIUS = 15;
	
	private double r;
	private Bat bat = null;
	private int attackPower = 1;
	
	public Ball(Bat bat) {
		super(bat.world, bat.x(), bat.y()-bat.h()/2-INIT_RADIUS-GameApp.EPS_DIST, 1);
		r = INIT_RADIUS;
		this.bat = bat;
		bat.addBall(this);
		initCollidables();
		setV(0, 0);
	}
	public Ball(Ball other) {
		super(other.world, other.x(), other.y(), 1);
		r = other.r;
		if (other.bat != null) {
			other.bat.addBall(this);
			bat = other.bat;
		}
		attackPower = other.attackPower;
		initCollidables();
		setV(other.vx(), other.vy());
	}
	
	@Override
	protected void initCollidables() {
		collidables = new Collidable[1];
		collidables[0] = new Circle(this, 0, 0, r); 
	}
	
	public double r() { return r; }
	public double v() { return Math.sqrt(vx()*vx() + vy()*vy()); }
	
	public void setR(double r) {
		this.r = r;
		((Circle) collidables[0]).setR(r);
		setPos(x(), y());
	}
	
	public void stickOnBat(Bat bat) {
		this.bat = bat;
		bat.addBall(this);
	}
	public void shootFromBat() { // No response if not stuck on bat
		if (bat != null) {
			double v = INIT_SPEED;
			double angle = 0.9 * (Math.PI / 2) * ( (x() - bat.x()) / bat.w() );
			setV(v * Math.sin(angle), - v * Math.cos(angle));
			bat.removeBall(this);
			bat = null;
		}
	}
	
	public void attack(Brick brick) {
		// TODO special attack effects.
		brick.takesDamage(attackPower);
	}

	/**
	 * Speed up (or slow down) the ball by a factor.
	 * @param factor
	 */
	public void speedUp(double factor) {
		// limit max speed and min speed
		if (v()*factor < MIN_SPEED || v()*factor > MAX_SPEED) { return; }
		setV(vx()*factor, vy()*factor);
	}
	
	@Override
	public void update(double dt) {
		updatePos(dt);
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillOval(x()-r, y()-r, 2*r, 2*r);
	}

	@Override
	protected Collision predictCollisionSpec(Bat bat) {
		if (this.bat != null) { return null; }
		else { return predictCollisionImpl(bat); }
	}
	@Override
	protected Collision predictCollisionSpec(Brick brick) {
		return predictCollisionImpl(brick);
	}
	@Override
	protected Collision predictCollisionSpec(Wall wall) {
		return predictCollisionImpl(wall);
	}
	@Override
	public Collision predictCollision(Sprite other) {
		return other.predictCollisionSpec(this);
	}


	@Override
	protected void collisionEffectsSpec(Bat bat) {
		bat.collisionEffectsSpec(this);
	}
	@Override
	protected void collisionEffectsSpec(Brick brick) {
		this.attack(brick);
	}
	@Override
	protected void collisionEffectsSpec(Wall wall) {
		if (wall.pos().equals(Wall.BOTTOM)) { 
			world.removeBall(this);
		}
	}
	@Override
	public void collisionEffects(Sprite other) {
		other.collisionEffectsSpec(this);
	}
	@Override
	protected String spriteName() { return "Ball"; }

}
