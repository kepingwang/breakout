package collisions;

import java.util.List;

import breakout.ScoreBoard;
import sprites.Ball;
import sprites.Brick;
import sprites.PowerUp;

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
		return ball.tLastCollision() <= tOn && brick.tLastCollision() <= tOn && !brick.dead();
	}
	
	public void attack() {
		brick.hit(ball.attack());
	}
	
	@Override
	public void resolve(ScoreBoard scoreBoard, List<PowerUp> powerUps) {
		resolve();
		if (brick.dead()) { brick.spawnPowerUp(powerUps); }
		scoreBoard.addScore(score());
		scoreBoard.addLife(life());
	}
	
	
	@Override
	public int score() { return 100; }

}
