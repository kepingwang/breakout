package collisions;


/**
 * "Static" class to wrap static methods for collision prediction.
 * @author keping
 */
public class Prediction {

	/**
	 * Hide the constructor.
	 */
	private Prediction() {};
	
	/**
	 * Return the time over which two balls are going to collide. 
	 * Return -1 when two balls will not collide. 
	 * @param x0
	 * @param y0
	 * @param r0
	 * @param vx0
	 * @param vy0
	 * @param x1
	 * @param y1
	 * @param r1
	 * @param vx1
	 * @param vy1
	 * @return
	 */
	public static double tBallBallCollision(
			double x0, double y0, double r0, 
			double vx0, double vy0,
			double x1, double y1, double r1,
			double vx1, double vy1) {
		
		double dx = x1-x0;
		double dy = y1-y0;
		double dvx = vx1-vx0;
		double dvy = vy1-vy0;
		double R = r0+r1;
		// solve || d + dv*dt || = R
		// ||(dx+dvx*t, dy+dvy*t)|| = R
		// (dvx^2+dvy^2)t^2 + 2(dvx*dx+dvy*dy)t + (dx^2+dy^2-R^2) = 0 
		// a*t^2 + b*t + c = 0
		// delta = b^2 - 4ac
		// t = （- b +- \sqrt{delta}）/ (2a)
		double a = dvx*dvx+dvy*dvy;
		double b = 2*(dvx*dx+dvy*dy);
		double c = dx*dx+dy*dy-R*R;
		double delta = b*b - 4*a*c;
		
		// t cannot be negative. cannot be dvx*dx+dvy*dy >= 0
		if (b >= 0 || delta < 0) { return -1; }
		return (- b - Math.sqrt(delta)) / (2*a);
	}
	
}
