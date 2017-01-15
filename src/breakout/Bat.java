package breakout;

import javafx.scene.canvas.GraphicsContext;

public class Bat extends Sprite {

	private final double limitLeft;
	private final double limitRight;
	
	public Bat(double x, double y, double w, double h, 
			double left, double right) {
		centerX = x;
		centerY = y;
		width = w;
		height = h;
		limitLeft = left;
		limitRight = right;
	}
	
	
	public void addVX(double deltaVX) {
		this.vX += deltaVX;
	}

	@Override
	public void update(double dt) {
		centerX += vX * dt;
		if (centerX - width/2 < limitLeft) {
			centerX = limitLeft + width/2;
		} else if (centerX + width/2 > limitRight) {
			centerX = limitRight - width/2;
		}
		vX = 0.0;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillRoundRect(centerX-width/2, centerY-height/2, width, height, height, height);
	}


}
