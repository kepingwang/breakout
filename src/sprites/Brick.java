package sprites;

import breakout.GameWorld;
import collidables.Circle;
import collidables.Collidable;
import collidables.HLine;
import collidables.VLine;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Brick extends Sprite {

	private final static int INIT_SCORE = 100;
	private final static double EXPLOSION_TIME = 1.0;
	private double timeRemain = EXPLOSION_TIME; // before disappears
	private int lives;
	private boolean breakable;
	private int powerUpType; // TODO specify powerUpType int type correspondence
	// -1 for no power up
	
	private double w;
	private double h;
	
	public Brick(GameWorld world, double x, double y, double w, double h,
				 int initLives, int powerUpType) {		
		super(world, x, y, -1);
		this.w = w;
		this.h = h;
		lives = initLives;
		this.breakable = (initLives <= 3);
		this.powerUpType = powerUpType;
		initCollidables();
	}
	@Override
	protected void initCollidables() {
		collidables = new Collidable[8]; 
		collidables[0] = new HLine(this, -w/2, +w/2, -h/2);
		collidables[1] = new VLine(this, +w/2, -h/2, +h/2);
		collidables[2] = new HLine(this, -w/2, +w/2, +h/2);
		collidables[3] = new VLine(this, -w/2, -h/2, +h/2);
		collidables[4] = new Circle(this, -w/2, -h/2, 0); 
		collidables[5] = new Circle(this, +w/2, -h/2, 0); 
		collidables[6] = new Circle(this, -w/2, +h/2, 0); 
		collidables[7] = new Circle(this, +w/2, +h/2, 0); 
	}
	
	
	private void spawnPowerUp() {
		if (powerUpType >= 0) {
			world.addPowerUp(new PowerUp(world, x(), y(), powerUpType));
		}
	}
	
	public void takesDamage(int damage) { // if killed, call kill()
		if (breakable) {
			lives -= damage;
			if (dead()) { kill(); }
		}
	}
	public void kill() {
		lives = -1;
		world.addScore(INIT_SCORE);
		world.removeBrick(this);
		world.addToFadings(this);
		spawnPowerUp();
	}
	
	public boolean dead() {
		return lives <= 0;
	}
	
	public void hit(int damage) {
		if (breakable) {
			lives -= damage;
		}
	}
	public boolean breakable() {
		return breakable;
	}
	
	@Override
	public void update(double dt) {
		if (dead()) { timeRemain -= dt; }
		if (timeRemain <= 0) { world.removeFromFadings(this); }
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.setGlobalAlpha(timeRemain / EXPLOSION_TIME);
		if (lives <= 1) {
			gc.setFill(Color.BLACK);
		} else if (lives == 2) {
			gc.setFill(Color.BLUE);
		} else if (lives == 3) {
			gc.setFill(Color.LIGHTBLUE);
		} else if (lives >= 4) {
			gc.setFill(Color.GOLD);
		}
		gc.fillRect(x()-w/2, y()-h/2, w, h);
		gc.setFill(Color.BLACK);
		gc.setGlobalAlpha(1);
	}


	@Override
	protected Collision predictCollisionSpec(Ball ball) {
		return ball.predictCollisionSpec(this);
	}
	@Override
	protected Collision predictCollisionSpec(Bullet bullet) {
		return bullet.predictCollisionSpec(this);
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
	protected void collisionEffectsSpec(Bullet bullet) {
		bullet.collisionEffectsSpec(this);
	}
	@Override
	public void collisionEffects(Sprite other) {
		other.collisionEffectsSpec(this);
	}

	@Override
	protected String spriteName() { return "Brick"; }
	
}
