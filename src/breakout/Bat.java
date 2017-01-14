package breakout;

import javafx.scene.canvas.GraphicsContext;

public class Bat extends Sprite {

	private final double vBat = 20 * 60;
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
	
	public void setX(double x) {
		centerX = x;
		if (centerX - width/2 < limitLeft) {
			centerX = limitLeft + width/2;
		} else if (centerX + width/2 > limitRight) {
			centerX = limitRight - width/2;
		}
	}
	public double getX() {
		return centerX;
	}
	public double width() {
		return width;
	}
	
	public void update(int direction, double deltaTime) {
		centerX += direction * vBat * deltaTime;
		if (centerX - width/2 < limitLeft) {
			centerX = limitLeft + width/2;
		} else if (centerX + width/2 > limitRight) {
			centerX = limitRight - width/2;
		}
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillRoundRect(centerX-width/2, centerY+height/2, width, height, height, height);
	}
}
