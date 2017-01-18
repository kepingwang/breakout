package sprites;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends Sprite {

	private final static double explosionTime = 1.0;
	private double timeRemain = explosionTime; // before disappears
	private int lives;
	private boolean breakable;
	private int powerUpType; // TODO specify powerUpType int type correspondence
	// -1 for no power up
	
	public Brick(double x, double y, double w, double h,
				 int initLives, int powerUpType) {
		centerX = x;
		centerY = y;
		width = w;
		height = h;
		lives = initLives;
		this.breakable = (initLives <= 3);
		this.powerUpType = powerUpType;
	}
	
	
	@Override
	public void render(GraphicsContext gc) {
		gc.setGlobalAlpha(timeRemain / explosionTime);
		if (lives <= 1) {
			gc.setFill(Color.BLACK);
		} else if (lives == 2) {
			gc.setFill(Color.BLUE);
		} else if (lives == 3) {
			gc.setFill(Color.LIGHTBLUE);
		} else if (lives >= 4) {
			gc.setFill(Color.GOLD);
		}
		gc.fillRect(centerX-width/2, centerY-height/2, width, height);
		gc.setFill(Color.BLACK);
		gc.setGlobalAlpha(1);
	}
	
	public void spawnPowerUp(List<PowerUp> powerUps) {
		if (powerUpType >= 0) {
			powerUps.add(new PowerUp(centerX, centerY, powerUpType));
		}
	}

	public boolean dead() {
		return lives <= 0;
	}
	public boolean gone() {
		return dead() && timeRemain <= 0;
	}
	public void kill() {
		lives = -1;
	}
	public void hit(int damage) {
		if (breakable) {
			lives -= damage;
		}
	}
	public boolean breakable() {
		return breakable;
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
