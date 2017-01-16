package sprites;

import javafx.scene.canvas.GraphicsContext;

public class Brick extends Sprite {

	private final static double explosionTime = 1.0;
	private double timeRemain = 1.0; // before disappears
	private int lives;
	
	public Brick(double x, double y) {
		centerX = x;
		centerY = y;
		width = 100;
		height = 40;
		lives = 1;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.setGlobalAlpha(timeRemain / explosionTime);
		gc.fillRect(centerX-width/2, centerY-height/2, width, height);
		gc.setGlobalAlpha(1);
	}

	public boolean dead() {
		return lives <= 0;
	}
	public boolean gone() {
		return dead() && timeRemain <= 0;
	}
	public void hit(int damage) {
		lives -= damage;
	}
	
	@Override
	public void update(double dt) {
		if (dead()) { timeRemain -= dt; }
	}

	@Override
	public String toString() {
		return "Brick ("+String.format("%.2f", centerX)+","+
				String.format("%.2f", centerY)+") gone "+gone();
	}
	
}
