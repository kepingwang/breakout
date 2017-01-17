package collisions;

import sprites.Ball;
import sprites.Bat;

public abstract class BallBatCollision extends Collision {

	protected Ball ball;
	protected Bat bat;
	
	public BallBatCollision(Ball ball, Bat bat, double tOn, double t) {
		this.ball = ball;
		this.bat = bat;
		this.tOn = tOn;
		this.t = t;
	}	

	@Override
	public boolean isValid() {
		return ball.tLastCollision() <= tOn;
	}

	@Override
	public String toString() {
		return "Ball Bat "+t;
	}
	
}
