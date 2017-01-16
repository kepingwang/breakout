package collisions;

import breakout.World;
import sprites.Ball;
import sprites.Wall;

public class BallWallCollision extends Collision {

	private Ball ball;
	private Wall wall;
	
	public BallWallCollision(Ball ball, String pos, double tOn, double t) {
		this.ball = ball;
		this.wall = new Wall(pos);
		this.tOn = tOn;
		this.t = t;
	}

	@Override
	public void resolve() {
		if (wall.type().equals("top")) {
			ball.setVY(-ball.vY());
			ball.setY(0+ball.r()+World.epsDist);
		} else if (wall.type().equals("left") || wall.type().equals("right")) {
			ball.setVX(-ball.vX());
			if (wall.type().equals("left")) {
				ball.setX(0+ball.r()+World.epsDist);
			} else {
				ball.setX(World.canvasWidth-ball.r()-World.epsDist);
			}
		}
		ball.setTimeLastCollision(t);
	}

	@Override
	public boolean isValid() {
		return ball.tLastCollision() <= tOn;
	}
	
	@Override
	public String toString() {
		return "Ball "+wall.type()+" Wall "+t;
	}
	
	@Override
	public int life() {
		if (wall.type().equals("bottom")) { return -1; }
		else { return 0; }
	}
}
