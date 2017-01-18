package collisions;

import java.util.List;

import breakout.ScoreBoard;
import sprites.Bat;
import sprites.PowerUp;

public class BatPowerUpCollision extends Collision {

	private Bat bat;
	private PowerUp powerUp;
	
	public BatPowerUpCollision(Bat bat, PowerUp powerUp, double tOn, double t) {
		this.bat = bat;
		this.powerUp = powerUp;
		this.tOn = tOn;
		this.t = t;
	}
	
	@Override
	public void resolve(ScoreBoard scoreBoard, List<PowerUp> powerUps) {
		resolve();
		powerUps.remove(powerUp);
		scoreBoard.addScore(score());
		scoreBoard.addLife(life());		
	}
	
	@Override
	public void resolve() {
		
	}

	@Override
	public boolean isValid() {
		return bat.tLastCollision() <= tOn;
	}

}
