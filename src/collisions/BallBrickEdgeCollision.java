package collisions;

import breakout.World;
import sprites.Ball;
import sprites.Brick;

public class BallBrickEdgeCollision extends BallBrickCollision {

	private String pos;
	
	public BallBrickEdgeCollision(Ball ball, Brick brick, String pos, 
			double tOn, double t) {
		super(ball, brick, tOn, t);
		this.pos = pos;
	}

	@Override
	public void resolve() {
		System.out.println(this);
		if (pos.equals("top")) {
			ball.setVY(-ball.vY());
			ball.setY(brick.centerY()-brick.height()/2-ball.r()-World.epsDist);
		} else if (pos.equals("bottom")) {
			ball.setVY(-ball.vY());
			ball.setY(brick.centerY()+brick.height()/2+ball.r()+World.epsDist);
		} else if (pos.equals("left")) {
			ball.setVX(-ball.vX());
			ball.setX(brick.centerX()-brick.width()/2-ball.r()-World.epsDist);
		} else if (pos.equals("right")) {
			ball.setVX(-ball.vX());
			ball.setX(brick.centerX()+brick.width()/2+ball.r()+World.epsDist);
		}
		ball.setTimeLastCollision(t);
		brick.setTimeLastCollision(t);
	}

	@Override
	public String toString() {
		return ball.toString() + " hit edge "+pos+" "+brick.toString() + " tOn "+
				String.format("%.2f", tOn)+","+String.format("%.2f", t-tOn);
	}
	
}
