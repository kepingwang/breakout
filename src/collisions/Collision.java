package collisions;

import collidables.Collidable;

public class Collision implements Comparable<Collision> {
	
	protected Collidable c0;
	protected Collidable c1;
	protected double timePredictionMade;
	protected double timeHappening;
	
	public Collision(Collidable c0, Collidable c1, 
			double timePredictionMade, double timeHappening) {
		this.c0 = c0;
		this.c1 = c1;
		this.timePredictionMade = timePredictionMade;
		this.timeHappening = timeHappening;
	}
	
	public void resolve() {
		c0.collides(c1);
		c0.sprite().collisionEffects(c1.sprite());
	}
	
	@Override
	public int compareTo(Collision o) {
		if (timeHappening < o.timeHappening) { return -1; }
		else if (timeHappening > o.timeHappening) { return 1; }
		else { return 0; }
	}
	
	public boolean isValid() {
		return (c0.sprite().timeLastCollision() <= timePredictionMade &&
				c1.sprite().timeLastCollision() <= timePredictionMade);
	}
	
	public double timeHappening() {
		return timeHappening;
	}

	@Override
	public String toString() {
		return "Collision >< " + String.format(
				"t %.4f, %.4f", timePredictionMade, timeHappening) + "\n"
			 + c0.sprite() + ": " + c0 + "\n"
			 + c1.sprite() + ": " + c1 + "\n";
	}
	
}
