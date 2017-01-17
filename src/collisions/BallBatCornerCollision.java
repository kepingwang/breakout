package collisions;

import breakout.World;
import sprites.Ball;
import sprites.Bat;

public class BallBatCornerCollision extends BallBatCollision {

	private String pos;
	
	public BallBatCornerCollision(Ball ball, Bat bat, String pos, double tOn, double t) {
		super(ball, bat, tOn, t);
		this.pos = pos;
	}

	@Override
	public void resolve() {
		double cX = 0; // corner X
		double cY = 0; // corner Y
		if (pos.equals("left")) {
			cX = bat.centerX()-bat.width()/2+bat.r();
			cY = bat.centerY();
		} else if (pos.equals("right")) {
			cX = bat.centerX()+bat.width()/2-bat.r();
			cY = bat.centerY();
		} else { return; }
		
		// vOut = vIn - 2(vIn \dot n) n      // n going orthogonal out of mirror, normalized
		double nX = (ball.centerX()-cX) / (ball.r()+bat.r());
		double nY = (ball.centerY()-cY) / (ball.r()+bat.r());
		double coeff = 2 * (ball.vX()*nX + ball.vY()*nY);
		double vOutX = ball.vX() - coeff*nX;
		double vOutY = ball.vY() - coeff*nY;
		ball.setVX(vOutX);
		ball.setVY(vOutY);
		ball.setX(cX+(ball.r()+bat.r()+World.epsDist)*nX);
		ball.setY(cY+(ball.r()+bat.r()+World.epsDist)*nY);
		ball.setTimeLastCollision(t);
		bat.stopMovement(t);
	}

	@Override
	public String toString() {
		return "ball bat corner collision\n" + 
				"ball ("+ball.centerX()+","+ball.centerY()+"), " + 
				"bat (("+(bat.centerX()-bat.width()/2+bat.r())+","+
						 (bat.centerX()+bat.width()/2-bat.r())+"),"+
						 bat.centerY()+")";
	}
	
}
