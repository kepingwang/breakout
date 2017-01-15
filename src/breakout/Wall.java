package breakout;

import javafx.scene.canvas.GraphicsContext;

public class Wall extends Sprite {

	private String type;
	
	public Wall(String type) {
		this.type = type;
	}
	
	public String type() { return type; }
	
	@Override
	public void render(GraphicsContext gc) {
	
	}

	@Override
	public void update(double deltaTime) {
		
	}

}
