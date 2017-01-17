package sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends Sprite {

	private final static double explosionTime = 1.0;
	private double timeRemain = explosionTime; // before disappears
	private int lives;
	
	/**
	 * Create a new Brick.
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param initLives
	 */
	public Brick(double x, double y, double w, double h, int initLives) {
		centerX = x;
		centerY = y;
		width = w;
		height = h;
		lives = initLives;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.setGlobalAlpha(timeRemain / explosionTime);
		if (lives == 2) {
			gc.setFill(Color.BLUE);
		} else if (lives == 3) {
			gc.setFill(Color.LIGHTBLUE);
		}
		gc.fillRect(centerX-width/2, centerY-height/2, width, height);
		gc.setFill(Color.BLACK);
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
