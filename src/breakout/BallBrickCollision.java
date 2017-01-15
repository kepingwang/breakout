package breakout;

public abstract class BallBrickCollision extends Collision {

	private Ball ball;
	private Brick brick;
	
	public BallBrickCollision(Ball ball, Brick brick, double tOn, double t) {
		this.ball = ball;
		this.brick = brick;
		this.tOn = tOn;
		this.t = t;
	}
	
	@Override
	public boolean isValid() {
		return ball.tLastCollision <= tOn && brick.tLastCollision <= tOn;
	}

}
