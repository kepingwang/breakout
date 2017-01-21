package sprites;

import java.util.ArrayList;
import java.util.List;

import breakout.GameWorld;
import collidables.Circle;
import collidables.Collidable;
import collidables.HLine;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bat extends Sprite {

	public static final double KEYBOARD_SPEED = 600;
	public static final double INIT_WIDTH = 150;
	public static final double INIT_HEIGHT = 30;
	public static final double BULLET_SPACING_TIME = 0.20;
	
	private double w;
	private double h;
	
	private List<Ball> balls;
	private double stickyTime = -1;
	private double bulletWait = -1;
	private double gunnerTime = -1;
	
	public Bat(GameWorld world, double x, double y) {
		super(world, x, y, -1);
		this.w = INIT_WIDTH;
		this.h = INIT_HEIGHT;
		balls = new ArrayList<Ball>();
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

	public double w() { return w; }
	public double h() { return h; }
	public void setW(double w) {
		this.w = w;
		((HLine) collidables[0]).setDPos(-(w-h)/2, +(w-h)/2, -h/2); 
		((HLine) collidables[1]).setDPos(-(w-h)/2, +(w-h)/2, +h/2);
		((Circle) collidables[2]).setDPos(-(w-h)/2, 0);
		((Circle) collidables[3]).setDPos(+(w-h)/2, 0);
		setPos(x(), y());
	}
	
	public void addBall(Ball ball) {
		balls.add(ball);
	}
	public void removeBall(Ball ball) {
		balls.remove(ball);
	}
	public void makeSticky(double t) {
		stickyTime = t;
	}
	public boolean isSticky() {
		return stickyTime > 0;
	}
	public void makeGunner(double t) {
		gunnerTime = t;
	}
	public boolean isGunner() {
		return gunnerTime > 0;
	}
	public void shootBullet() {
		if (isGunner() && bulletWait < 0) {
			Bullet bullet = new Bullet(this);
			world.addBullet(bullet);
			bullet.setV(0, -Bullet.SPEED);
			bulletWait = BULLET_SPACING_TIME;
		}
	}
	
	/**
	 * If a ball is stuck on bat, update the ball together with the bat.
	 */
	@Override
	public void update(double dt) {
		for (Ball ball : balls) {
			ball.setX(ball.x() + vx()*dt);
		}
		updatePos(dt);
		if (stickyTime >= 0) { stickyTime -= dt; }
		if (gunnerTime >= 0) { gunnerTime -= dt; }
		if (bulletWait >= 0) { bulletWait -= dt; }
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillRoundRect(x()-w/2, y()-h/2, w, h, h, h);
		if (isGunner()) {
			gc.setFill(Color.WHITE);
			gc.fillRoundRect(x()-Bullet.WIDTH/2, y()-Bullet.LENGTH/2,
					Bullet.WIDTH, Bullet.LENGTH, Bullet.WIDTH, Bullet.WIDTH);
			gc.setFill(Color.BLACK);
		}
		if (isSticky()) {
			gc.setFill(Color.WHITE);
			gc.fillRoundRect(x()-(w-h)/2, y()-Bullet.WIDTH/2,
					(w-h), Bullet.WIDTH, Bullet.WIDTH, Bullet.WIDTH);
			gc.setFill(Color.BLACK);
		}
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
		ball.speedUp(1.03);
		if (isSticky()) {
			ball.stickOnBat(this);
		}
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
