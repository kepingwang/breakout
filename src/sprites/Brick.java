package sprites;

import javafx.scene.canvas.GraphicsContext;

public class Brick extends Sprite {

		
	public Brick(double x, double y) {
		centerX = x;
		centerY = y;
		width = 100;
		height = 40;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.fillRect(centerX-width/2, centerY-height/2, width, height);
	}

	@Override
	public void update(double deltaTime) {

	}

	@Override
	public String toString() {
		return "Brick ("+String.format("%.2f", centerX)+","+
				String.format("%.2f", centerY)+")";
	}
	
}
