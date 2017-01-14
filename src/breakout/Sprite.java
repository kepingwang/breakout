package breakout;

import javafx.scene.canvas.GraphicsContext;

public abstract class Sprite {
	
	protected double centerX;
	protected double centerY;
	protected double width;
	protected double height;
	
	public abstract void render(GraphicsContext gc);
	
}
