package sprites;

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
	private boolean stuckOnBat = false;
	private int attackPower = 1;
	
	public Ball(Bat bat) {
		super(bat.world, bat.x(), bat.y()-bat.h()/2-INIT_RADIUS, 1);
		r = INIT_RADIUS;
		bat.setBall(this);
		stuckOnBat = true;
		initCollidables();
		setV(0, 0);
	}
	@Override
	protected void initCollidables() {
		collidables = new Collidable[1];
		collidables[0] = new Circle(this, 0, 0, r); 
	}
	
	public double r() { return r; }
	public double v() { return Math.sqrt(vx()*vx() + vy()*vy()); }
	
	public boolean stuckOnBat() { return stuckOnBat; }
	public void shootFromBat() { // No response if not stuck on bat
		if (stuckOnBat) {
			setV(0, -INIT_SPEED);
			stuckOnBat = false;
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
		if (stuckOnBat) { return null; }
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
	protected void collisionEffectsSpec(Bat bat) {
		bat.setVX(0);
	}
	@Override
	public void collisionEffects(Sprite other) {
		other.collisionEffectsSpec(this);
	}
	@Override
	protected String spriteName() { return "Ball"; }

}
