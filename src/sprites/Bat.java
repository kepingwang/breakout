package sprites;

import breakout.GameWorld;
import collidables.Circle;
import collidables.Collidable;
import collidables.HLine;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;

public class Bat extends Sprite {

	public static final double KEYBOARD_SPEED = 600;
	public static final double WIDTH = 150;
	public static final double HEIGHT = 30;
	
	private double w;
	private double h;
	
	private Ball ball;
	
	public Bat(GameWorld world, double x, double y) {
		super(world, x, y, -1);
		this.w = WIDTH;
		this.h = HEIGHT;
		initCollidables();
	}
	@Override
	protected void initCollidables() {
		collidables = new Collidable[4];
		collidables[0] = new HLine(this, -(w-h)/2, +(w-h)/2, -h/2);
		collidables[1] = new HLine(this, -(w-h)/2, +(w-h)/2, +h/2);
		collidables[2] = new Circle(this, -(w-h)/2, 0, h/2); 
		collidables[3] = new Circle(this, +(w-h)/2, 0, h/2); 
	}
	
	public void setBall(Ball ball) {
		this.ball = ball;
	}
	
	public double w() { return w; }
	public double h() { return h; }

	/**
	 * If a ball is stuck on bat, update the ball together with the bat.
	 */
	@Override
	public void update(double dt) {
		updatePos(dt);
		if (ball.stuckOnBat()) {
			ball.setX(x());
		}
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillRoundRect(x()-w/2, y()-h/2, w, h, h, h);
	}

	@Override
	protected Collision predictCollisionSpec(Ball ball) {
		return ball.predictCollisionSpec(this);
	}
	@Override
	protected Collision predictCollisionSpec(PowerUp powerUp) {
		return predictCollisionImpl(powerUp);
	}
	@Override
	public Collision predictCollision(Sprite other) {
		return other.predictCollisionSpec(this);
	}
	

	@Override
	protected void collisionEffectsSpec(Ball ball) {
		ball.collisionEffectsSpec(this);
	}
	@Override
	protected void collisionEffectsSpec(PowerUp powerUp) {
		powerUp.takeEffect();
		world.removePowerUp(powerUp);
	}
	@Override
	public void collisionEffects(Sprite other) {
		other.collisionEffectsSpec(this);
	}

	@Override
	protected String spriteName() { return "Bat"; }
	
}
