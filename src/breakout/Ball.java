package breakout;

import javafx.scene.canvas.GraphicsContext;

public class Ball extends Sprite {
	
	private final double initVelocity = 360;
	
	private double r;
	private boolean stuckOnBat = false;
	private Bat bat;
	
	public Ball() {
		centerX = 100;
		centerY = 100;
		r = 15;
	}
	
	// Initialize the ball on the bar. 
	public Ball(Bat bat) {
		r = 15;
		this.bat = bat;
		centerX = bat.centerX;
		centerY = bat.centerY-bat.height/2-r;
		stuckOnBat = true;
	}
	
	public void shootFromBat() {
		vX = 0;
		vY = -initVelocity;
		stuckOnBat = false;
	}
	
	public boolean stuckOnBat() { return stuckOnBat; }
	
	public double r() { return r; }
	public double v() { return Math.sqrt(vX*vX + vY*vY); }
	public void setVX(double vX) { this.vX = vX; }
	public void setVY(double vY) { this.vY = vY; }
	public void setX(double x) { centerX = x; }
	public void setY(double y) { centerY = y; }
	
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillOval(centerX-r, centerY-r, 2*r, 2*r);
	}

	@Override
	public void update(double dt) {
		if (stuckOnBat) {
			centerX = bat.centerX;
		} else {
			centerX += vX * dt;
			centerY += vY * dt;
		}
	}
}
