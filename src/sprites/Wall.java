package sprites;

import breakout.GameApp;
import breakout.GameWorld;
import collidables.Collidable;
import collidables.HLine;
import collidables.VLine;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;

public class Wall extends Sprite {

	public static final String TOP = "top";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String BOTTOM = "bottom";
	
	private String pos;
	
	public Wall(GameWorld world, double x, double y, String pos) {
		super(world, x, y, -1);
		this.pos = pos;
		initCollidables();
	}
	@Override
	protected void initCollidables() {
		collidables = new Collidable[1];
		if (pos.equals(TOP) || pos.equals(BOTTOM)) {
			collidables[0] = new HLine(this, GameApp.WIDTH/2, - GameApp.WIDTH/2, 0);
		} else if (pos.equals(LEFT) || pos.equals(RIGHT)) {
			collidables[0] = new VLine(this, 0, 
					GameApp.HEIGHT/2+2*Ball.INIT_RADIUS, -GameApp.HEIGHT/2-2*Ball.INIT_RADIUS
				);
		}
	}
	
	public String pos() { return pos; }
	
	@Override
	public void update(double dt) { }

	@Override
	public void render(GraphicsContext gc) { }

	@Override
	protected Collision predictCollisionSpec(Ball ball) {
		return ball.predictCollisionSpec(this);
	}
	@Override
	protected Collision predictCollisionSpec(PowerUp powerUp) {
		return powerUp.predictCollisionSpec(this);
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
		powerUp.collisionEffectsSpec(this);
	}
	@Override
	public void collisionEffects(Sprite other) {
		other.collisionEffectsSpec(this);
	}

	@Override
	protected String spriteName() { return "Wall"; }
	
}
