package sprites;

import breakout.GameWorld;
import collidables.Collidable;
import collidables.HLine;
import collidables.VLine;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;

public class PowerUp extends Sprite {
	
	private static final double powerUpWidth = 50;
	private static final double powerUpHeight = 50;
	private static final double powerUpFallingSpeed = 300;
	
	private double w;
	private double h;
	private int type = -1;
	
	public PowerUp(GameWorld world, double x, double y, int powerUpType) {
		super(world, x, y, -1);
		w = powerUpWidth;
		h = powerUpHeight;
		setVY(powerUpFallingSpeed);
		type = powerUpType;
		initCollidables();
	}
	@Override
	protected void initCollidables() {
		collidables = new Collidable[4]; // top right bottom left
		collidables[0] = new HLine(this, -w/2, +w/2, -h/2);
		collidables[1] = new VLine(this, +w/2, -h/2, +h/2);
		collidables[2] = new HLine(this, -w/2, +w/2, +h/2);
		collidables[3] = new VLine(this, -w/2, -h/2, +h/2);
	}

	public void takeEffect() {
		if (type >= 0) { System.out.println("Power UP!!"); }
		// TODO;
	}
	
	@Override
	public void update(double dt) {
		updatePos(dt);
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.fillRect(x()-w/2, y()-h/2, w, h);
	}


	@Override
	protected Collision predictCollisionSpec(Bat bat) {
		return bat.predictCollisionSpec(this);
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
	protected void collisionEffectsSpec(Wall wall) {
		world.removePowerUp(this);
	}
	@Override
	public void collisionEffects(Sprite other) {
		other.collisionEffectsSpec(this);
	}
	
	@Override
	protected String spriteName() { return "PowerUp"; }

}
