package collisions;

import breakout.World;
import sprites.Ball;
import sprites.Brick;

public class BallBrickCornerCollision extends BallBrickCollision {

	private String pos;
	
	public BallBrickCornerCollision(Ball ball, Brick brick, String pos,
			double tOn, double t) {
		super(ball, brick, tOn, t);
		this.pos = pos;
	}

	@Override
	public void resolve() {
		attack();
		double cX = 0; // corner X
		double cY = 0; // corner Y
		if (pos.equals("top-left")) {
			cX = brick.centerX()-brick.width()/2;
			cY = brick.centerY()-brick.height()/2;
		} else if (pos.equals("top-right")) {
			cX = brick.centerX()+brick.width()/2;
			cY = brick.centerY()-brick.height()/2;
		} else if (pos.equals("bottom-left")) {
			cX = brick.centerX()-brick.width()/2;
			cY = brick.centerY()+brick.height()/2;
		} else if (pos.equals("bottom-right")) {
			cX = brick.centerX()+brick.width()/2;
			cY = brick.centerY()+brick.height()/2;
		} else { return; }
		
		// vOut = vIn - 2(vIn \dot n) n      // n going orthogonal out of mirror, normalized
		double nX = (ball.centerX()-cX) / ball.r();
		double nY = (ball.centerY()-cY) / ball.r();
		double coeff = 2 * (ball.vX()*nX + ball.vY()*nY);
		double vOutX = ball.vX() - coeff*nX;
		double vOutY = ball.vY() - coeff*nY;
		ball.setVX(vOutX);
		ball.setVY(vOutY);
		ball.setX(cX+(ball.r()+World.epsDist)*nX);
		ball.setY(cY+(ball.r()+World.epsDist)*nY);
		ball.setTimeLastCollision(t);
		brick.setTimeLastCollision(t);
	}
	
	@Override
	public String toString() {
		return ball.toString() + " hit corner "+pos+" "+ brick.toString() + " tOn "+
				String.format("%.2f", tOn)+","+String.format("%.2f", t-tOn);
	}
	
}
