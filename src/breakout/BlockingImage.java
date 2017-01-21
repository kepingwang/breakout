package breakout;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import sprites.Displayable;

public class BlockingImage implements Displayable {

	public static final String TROLL_FACE = "img/troll_face.png";
	public static final double INIT_WIDTH = 120;
	public static final double SHRINKING_SPEED = 25;
	public static final double H_W = 0.817;
	private Image image;
	private double x = -1;
	private double y = -1;
	private double w = -1; // width
	
	public BlockingImage() {
		ClassLoader classLoader = getClass().getClassLoader();
		image = new Image(classLoader.getResourceAsStream(TROLL_FACE));
	}
	private void setRandomPos() {
		x = Math.random()*GameApp.WIDTH*0.9 + GameApp.WIDTH*0.05;
		y = Math.random()*GameApp.HEIGHT*0.9 + GameApp.HEIGHT*0.05;
	}
	
	public void enlargeByFactor(double factor) {
		if (w <= 0) {
			w = INIT_WIDTH; 
			setRandomPos();
		} else {
			w *= factor; 
		}
	}
	
	@Override
	public void update(double dt) {
		if (w > 0) {
			w -= SHRINKING_SPEED * dt;
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		if (w > 0) {
			gc.drawImage(image, x-w/2, y-w*H_W/2, w, w*H_W);
		}
	}

}
