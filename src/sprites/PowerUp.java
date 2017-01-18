package sprites;

import javafx.scene.canvas.GraphicsContext;

public class PowerUp extends Sprite {
	
	private static final double powerUpWidth = 50;
	private static final double powerUpHeight = 50;
	private static final double powerUpFallingSpeed = 300;
	
	private int type = -1;
	
	public PowerUp(double x, double y, int powerUpType) {
		centerX = x;
		centerY = y;
		width = powerUpWidth;
		height= powerUpHeight;
		vY = powerUpFallingSpeed;
		type = powerUpType;
	}

	@Override
	public void update(double dt) {
		centerX += vX*dt;
		centerY += vY*dt;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.fillRect(centerX-width/2, centerY-height/2, width, height);
	}

}
