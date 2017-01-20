package sprites;

import javafx.scene.canvas.GraphicsContext;

public interface Displayable {

	/**
	 * The main update method to be called in the GameWorld.
	 * @param dt
	 */
	public void update(double dt);
	
	/**
	 * The main rendering method to be called in the GameWorld.
	 * @param gc
	 */
	public void render(GraphicsContext gc);
	
}
