package collisions;

import sprites.Ball;
import sprites.Brick;

public abstract class BallBrickCollision extends Collision {

	protected Ball ball;
	protected Brick brick;
	
	public BallBrickCollision(Ball ball, Brick brick, double tOn, double t) {
		this.ball = ball;
		this.brick = brick;
		this.tOn = tOn;
		this.t = t;
	}
	
	@Override
	public boolean isValid() {
		return ball.tLastCollision() <= tOn && brick.tLastCollision() <= tOn;
	}

}
