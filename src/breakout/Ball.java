package breakout;

import javafx.scene.canvas.GraphicsContext;

public class Ball extends Sprite {
	
	private final double initVelocity = 240;
	
	private double r;
	private double vX;
	private double vY;
	private boolean stuckOnBat = false;
	
	public Ball() {
		centerX = 100;
		centerY = 100;
		r = 15;
	}
	
	// Initialize the ball on the bar. 
	public Ball(Bat bat) {
		r = 15;
		centerX = bat.centerX;
		centerY = bat.centerY-bat.height/2-r;
		stuckOnBat = true;
	}
	
	public void shootFromBat() {
		vX = 0.0;
		vY = -initVelocity;
		stuckOnBat = false;
	}
	
	public boolean stuckOnBat() {
		return stuckOnBat;
	}
	public void update(Bat bat) {
		centerX = bat.centerX;
	}
	public void update(double deltaTime) {
		centerX += vX * deltaTime;
		centerY += vY * deltaTime;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillOval(centerX-r, centerY+r, 2*r, 2*r);
	}
}
