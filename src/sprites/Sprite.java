package sprites;

import javafx.scene.canvas.GraphicsContext;

public abstract class Sprite {
	
	protected double centerX;
	protected double centerY;
	protected double width;
	protected double height;
	protected double vX = 0;
	protected double vY = 0;
	
	/**
	 * Current time for the Sprite.
	 */
	protected double t;
	/**
	 * Time of last collision
	 */
	protected double tLastCollision = -1;
	
	public abstract void update(double dt);
	
	
	public double centerX() { return centerX; }
	public double centerY() { return centerY; }
	public double width()  { return width; }
	public double height() { return height; }
	public double vX() { return vX; }
	public double vY() { return vY; }
	
	public abstract void render(GraphicsContext gc);
	
	public double tLastCollision() {
		return tLastCollision;
	}
	public void setTimeLastCollision(double t) {
		tLastCollision = t;
	}
}
