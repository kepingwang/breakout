package breakout;

public class BallBatCollision extends Collision {

	private Ball ball;
	private Bat bat;
	
	public BallBatCollision(Ball ball, Bat bat, double tOn, double t) {
		this.ball = ball;
		this.bat = bat;
		this.tOn = tOn;
		this.t = t;
	}
	
	@Override
	public void resolve() {
		double v = ball.v();
		double angle = 0.8 * (Math.PI / 2) * ( ( ball.centerX() - bat.centerX() ) / (bat.width()/2));
		ball.setVY( - v * Math.cos(angle));
		ball.setVX(v * Math.sin(angle));
		ball.setY(bat.centerY()-bat.height()/2-ball.r()-World.epsDist);
		ball.tLastCollision = t;
	}

	@Override
	public boolean isValid() {
		return ball.tLastCollision <= tOn;
	}

	@Override
	public String toString() {
		return "Ball Bat "+t;
	}
	
}
