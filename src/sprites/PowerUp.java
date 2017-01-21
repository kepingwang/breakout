package sprites;

import breakout.GameWorld;
import collidables.Collidable;
import collidables.HLine;
import collidables.VLine;
import collisions.Collision;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class PowerUp extends Sprite {
	
	private static final double powerUpWidth = 50;
	private static final double powerUpHeight = 50;
	private static final double powerUpFallingSpeed = 300;

	public static final int GUNNER = 1;
	public static final int STICKY = 2;
	public static final int ADD_LIFE = 3;
	
	private double w;
	private double h;
	private int type = -1;
	
	public PowerUp(GameWorld world, double x, double y, int powerUpType) {
		super(world, x, y, -1);
		w = powerUpWidth;
		h = powerUpHeight;
		setVY(powerUpFallingSpeed);
		if (powerUpType <= 0 || powerUpType >= 4) { type = -1; } 
		else { type = powerUpType; }
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
		if (type == GUNNER) { world.makeBatGunner(); }
		else if (type == STICKY) { world.makeBatSticky(); }
		else if (type == ADD_LIFE) { world.addLife(); }
	}
	
	@Override
	public void update(double dt) {
		updatePos(dt);
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.fillRect(x()-w/2, y()-h/2, w, h);
		String typeSymbol = "";
		if (type == GUNNER) { typeSymbol = "G"; }
		else if (type == STICKY) { typeSymbol = "S"; }
		else if (type == ADD_LIFE) { typeSymbol = "L"; }
		gc.setFont(Font.font(40));
		gc.setFill(Color.WHITE);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText(typeSymbol, x(), y()+w*0.3, w);
		gc.setFont(Font.font(12));
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.LEFT);
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
