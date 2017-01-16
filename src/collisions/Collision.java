package collisions;

public abstract class Collision implements Comparable<Collision> {

	
	protected double tOn;
	/**
	 * Predicted time of collision.
	 */
	protected double t;

	public abstract void resolve();
	
	@Override
	public int compareTo(Collision o) {
		if (t < o.t) { return -1; }
		else if (t > o.t) { return 1; }
		else { return 0; }
	}
	
	public abstract boolean isValid();
	
	public double time() {
		return t;
	}
	
	public int score() {
		return 0;
	}
	public int life() {
		return 0;
	}
	
}
