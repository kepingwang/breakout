package collisions;

import breakout.World;
import sprites.Ball;
import sprites.Bat;

public class BallBatEdgeCollision extends BallBatCollision {

	public BallBatEdgeCollision(Ball ball, Bat bat, double tOn, double t) {
		super(ball, bat, tOn, t);
	}

	@Override
	public void resolve() {
		double v = ball.v();
		double angle = 0.9 * (Math.PI / 2) * ( ( ball.centerX() - bat.centerX() ) / (bat.width()/2));
		ball.setVY( - v * Math.cos(angle));
		ball.setVX(v * Math.sin(angle));
		ball.setY(bat.centerY()-bat.height()/2-ball.r()-World.epsDist);
		ball.setTimeLastCollision(t);
	}
	
	@Override
	public String toString() {
		return "ball bat edge collision";
	}

}
