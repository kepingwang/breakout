package sprites;

import collidables.Circle;
import collidables.Collidable;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;

public class Bullet extends Sprite {
	
	public static final double WIDTH = 6;
	public static final double LENGTH = 20;
	public static final double SPEED = 600;

	private int attackPower;
	
	// (x, y) is the center of the top circle.
	public Bullet(Bat bat) {
		super(bat.world, bat.x(), bat.y()-bat.h()/2, 1); // has mass, like ball
		attackPower = 1;
		initCollidables();
	}

	@Override
	protected void initCollidables() {
		collidables = new Collidable[1];
		collidables[0] = new Circle(this, 0, 0, WIDTH/2);
	}
	
	public void attack(Brick brick) {
		brick.takesDamage(attackPower);
	}

	@Override
	public void update(double dt) {
		updatePos(dt);
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.fillRoundRect(x()-WIDTH/2, y()-WIDTH/2, WIDTH, LENGTH,
				WIDTH, WIDTH);
	}
	
	@Override
	protected Collision predictCollisionSpec(Brick brick) {
		return predictCollisionImpl(brick);
	}
	@Override
	public Collision predictCollision(Sprite other) {
		return other.predictCollisionSpec(this);
	}

	@Override
	protected void collisionEffectsSpec(Brick brick) {
		this.attack(brick);
		world.removeBullet(this);
	}
	@Override
	public void collisionEffects(Sprite other) {
		other.collisionEffectsSpec(this);
	}

	@Override
	protected String spriteName() { return "Bullet"; }


}
