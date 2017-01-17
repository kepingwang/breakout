package sprites;

import breakout.World;
import javafx.scene.canvas.GraphicsContext;

public class Bat extends Sprite {

	private final double limitLeft;
	private final double limitRight;
	private double r;
	
	public Bat(double x, double y, double w, double h, 
			double left, double right) {
		centerX = x;
		centerY = y;
		width = w;
		height = h;
		r = height / 2;
		limitLeft = left;
		limitRight = right;
	}
	
	public double r() { return r; }
	public void addVX(double deltaVX) {
		this.vX += deltaVX;
	}
	public void setVX(double vx) {
		this.vX = vx;
	}
	
	/**
	 * Limit vX of bat so that it doesn't move out of boundary within a frame.
	 */
	public void limitV(double dt) {
		if (centerX + vX*dt - width/2 < limitLeft) {
			vX = (limitLeft + World.epsDist + width/2 - centerX) / dt; 
		} else if (centerX + vX*dt + width/2 > limitRight) {
			vX = (limitRight- World.epsDist - width/2 - centerX) / dt;
		}
	}

	@Override
	public void update(double dt) {
		centerX += vX * dt;
	}
	
	public void stopMovement(double currTime) {
		vX = 0.0;
		tLastCollision = currTime;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillRoundRect(centerX-width/2, centerY-height/2, width, height, r*2, r*2);
	}
	
}
