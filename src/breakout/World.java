package breakout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import collisions.BallBatCollision;
import collisions.BallBrickCornerCollision;
import collisions.BallBrickEdgeCollision;
import collisions.BallWallCollision;
import collisions.Collision;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import sprites.Ball;
import sprites.Bat;
import sprites.Brick;

public class World extends Application {
	
	public static final double epsDist = 1e-6;
	
	/**
	 * Records the current time 
	 * @author keping
	 */
	class Time {
		private double t;
		public Time(double t) { this.t = t; }
		public void add(double dt) { t += dt; }
	}
	
	Time time = new Time(0.0);
	long prevNanos = 0;
	
	// canvas size
	public static final double canvasWidth = 400;
	public static final double canvasHeight = 600;
	final double batY = 550;
	
	// sprites
	Bat bat;
	Ball ball;
	LinkedList<Brick> bricks;
	// other displays
	ScoreBoard scoreBoard;

	// keyboard and mouse events
	ArrayList<String> keyInput = new ArrayList<>();
	ArrayList<Double> mouseMove = new ArrayList<>();
	boolean mouseClicked = false;
	boolean paused = false;
	
	// collision events
	PriorityQueue<Collision> collisions = new PriorityQueue<>();
	
	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		Canvas canvas = new Canvas(canvasWidth, canvasHeight);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Breakout Game");
		
		// Initialize sprites and displays
		initializeSprites();
		
		// Set keyboard and mouse events
		scene.setOnKeyPressed(e -> {
			String code = e.getCode().toString();
			if (!keyInput.contains(code)) {
				keyInput.add(code);
			}
		});
		scene.setOnKeyReleased(e -> {
			String code = e.getCode().toString();
			keyInput.remove(code);
		});
		scene.setOnMouseMoved(e -> {
			mouseMove.add(e.getSceneX());
//			System.out.println(e.getSceneX()+","+e.getSceneY());
		});
		scene.setOnMouseClicked(e -> {
			mouseClicked = true;
		});
		scene.setOnKeyPressed(e -> {
			if (e.getCode().toString().equals("S")) { // start debugging
				ball.setX(ball.centerX());
			}
		});

		
		// Game loop
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				 if (prevNanos == 0) {
					 prevNanos = now;
					 return;
				 }
				 long deltaNanos = now - prevNanos;
				 double dt = deltaNanos / 1.0e9;
				 double endTime = time.t + dt; // time end of frame
				 prevNanos = now;
				 
				 // update vx for bat (vx only for current frame)
				 if (mouseMove.isEmpty()) {
					 if (keyInput.contains("LEFT") || keyInput.contains("A")) {
						 bat.addVX(-600);
					 }
					 if (keyInput.contains("RIGHT") || keyInput.contains("D")) {
						 bat.addVX(600);
					 }
				 } else {
					 bat.addVX((mouseMove.get(mouseMove.size()-1) - bat.centerX())/dt);
				 }
				 mouseMove.clear();
				 predictCollision(ball, bat, dt);
				 
				 // priority queue stores the collision events
				 while (!collisions.isEmpty() &&
						 collisions.peek().time() < endTime) {
					 Collision collision = collisions.poll();
					 if (collision.isValid()) {
						 update(collision.time()-time.t);
						 collision.resolve();
						 scoreBoard.addScore(collision.score());
						 predictCollisions(ball);
					 }
				 }
				 
				 update(dt, mouseClicked);
				 mouseClicked = false; // reset
				 
				 // render the images
				 gc.clearRect(0, 0, canvasWidth, canvasHeight);
				 render(gc);
			}
		};
		
		scene.setOnKeyReleased(e -> {
			if (e.getCode().toString().equals("P")) {
				System.out.println("P pressed");
				paused = !paused;
				if (paused) { timer.stop(); }
				else {
					prevNanos = 0;
					timer.start(); 
				}
			}
		});
		
		timer.start();
	
		Splash splash = new Splash(scene, stage);
		
		scene.setOnKeyPressed(e -> {
			if (e.getCode().toString().equals("S")) {
				stage.setScene(splash.scene());
			}
		});

		stage.setScene(splash.scene());
		stage.show();
	}
	
	private void predictCollision(Ball ball, Bat bat, double dt) {
		if (ball.stuckOnBat()) { return; }
		if (ball.vY() <= 0) { return; }
		double yTarget = bat.centerY() - bat.height()/2 - ball.r();
		if (ball.centerY() > yTarget) { return; }
		double tToTarget = (yTarget - ball.centerY()) / ball.vY();
		if (tToTarget > dt) { return; } // ball hasn't reached bottom
		double ballTargetX = ball.centerX() + ball.vX()*tToTarget;
		double batTargetX  = bat.centerX() + bat.vX()*tToTarget;
		if (batTargetX + bat.width()/2 > ballTargetX &&
			batTargetX - bat.width()/2 < ballTargetX) {
			collisions.add(new BallBatCollision(ball, bat, time.t, time.t+tToTarget));
		}
		// TODO limit left right positions
	}
	
	private void predictCollision(Ball ball, Brick brick) {

		if (ball.stuckOnBat()) { return; }
		double dX = brick.centerX() - ball.centerX();
		double dY = brick.centerY() - ball.centerY();
		double dt = -1;
		String pos = "";
		double dW = ball.r()+brick.width()/2;
		double dH = ball.r()+brick.height()/2;
		
		double timeToEdge = -1; 

		if (dY < -dH && ball.vY() < 0) {
			timeToEdge = (dY+dH) / ball.vY();
			if (dt < 0 || timeToEdge < dt) {
				if (dX-brick.width()/2 < ball.vX()*timeToEdge && 
					dX+brick.width()/2 > ball.vX()*timeToEdge) {
					dt = timeToEdge;
					pos = "bottom";
				}
			}
		}
		if (dY > dH && ball.vY() > 0) {
			timeToEdge = (dY-dH) / ball.vY();
			if (dt < 0 || timeToEdge < dt) {
				if (dX-brick.width()/2 < ball.vX()*timeToEdge && 
					dX+brick.width()/2 > ball.vX()*timeToEdge) {
					dt = timeToEdge;
					pos = "top";
				}
			}
		}
		if (dX < -dW && ball.vX() < 0) {
			timeToEdge = (dX+dW) / ball.vX();
			if (dt < 0 || timeToEdge < dt) {
				if (dY-brick.height()/2 < ball.vY()*timeToEdge && 
					dY+brick.height()/2 > ball.vY()*timeToEdge) {
					dt = timeToEdge;
					pos = "right";
				}
			}
		}
		if (dX > dW && ball.vX() > 0) {
			timeToEdge = (dX-dW) / ball.vX();
			if (dt < 0 || timeToEdge < dt) {
				if (dY-brick.height()/2 < ball.vY()*timeToEdge && 
					dY+brick.height()/2 > ball.vY()*timeToEdge) {
					dt = timeToEdge;
					pos = "left";
				}
			}
		}

		double timeToCircle = 0;
		timeToCircle = timeToCircle(ball.centerX(), ball.centerY(), ball.vX(), ball.vY(),
				brick.centerX()-brick.width()/2, brick.centerY()-brick.height()/2, ball.r());
		if (timeToCircle > 0) {
			if (dt < 0 || timeToCircle < dt) {
				dt = timeToCircle;
				pos = "top-left";
			}
		}
		timeToCircle = timeToCircle(ball.centerX(), ball.centerY(), ball.vX(), ball.vY(),
				brick.centerX()+brick.width()/2, brick.centerY()-brick.height()/2, ball.r());
		if (timeToCircle > 0) {
			if (dt < 0 || timeToCircle < dt) {
				dt = timeToCircle;
				pos = "top-right";
			}
		}
		timeToCircle = timeToCircle(ball.centerX(), ball.centerY(), ball.vX(), ball.vY(),
				brick.centerX()-brick.width()/2, brick.centerY()+brick.height()/2, ball.r());
		if (timeToCircle > 0) {
			if (dt < 0 || timeToCircle < dt) {
				dt = timeToCircle;
				pos = "bottom-left";
			}
		}
		timeToCircle = timeToCircle(ball.centerX(), ball.centerY(), ball.vX(), ball.vY(),
				brick.centerX()+brick.width()/2, brick.centerY()+brick.height()/2, ball.r());
		if (timeToCircle > 0) {
			if (dt < 0 || timeToCircle < dt) {
				dt = timeToCircle;
				pos = "bottom-right";
			}
		}
		
		if (dt > 0) {
			if (pos.indexOf('-') == -1) {
				collisions.add(new BallBrickEdgeCollision(ball, brick, pos, time.t, time.t+dt));
			} else {
				collisions.add(new BallBrickCornerCollision(ball, brick, pos, time.t, time.t+dt));
			}
		}
		
	}

	/**
	 * The time of the ball from (x0, y0) to the circle centered at (x1, y1) corner.
	 */
	private double timeToCircle(double x0, double y0, double vX, double vY, 
			double x1, double y1, double r) {
		// (x0 + vX*t, y0 + vY*t) - (x1, y1) orthogonal to (vX, vY)
		// (x0-x1)*vX + vX*vX*t  + (y0-y1)*vY + vY*vY*t = 0
		// dt is time to the closest point on line
		double dt = ((y1-y0)*vY + (x1-x0)*vX) / (vX*vX + vY*vY);
		if (dt < 0) { return -1; }
		double dx = x0 + vX*dt - x1; // closet point on line to (x1, y1)
		double dy = y0 + vY*dt - y1;
		double dist = Math.sqrt(dx*dx + dy*dy); 
		if (dist > r) { return -1; }
		return dt - Math.sqrt(r*r-(dx*dx+dy*dy)) / Math.sqrt(vX*vX+vY*vY);
	}
	
	private void predictBallWallCollision(Ball ball) {
		if (ball.stuckOnBat()) { return; }
		double tLeft = -1;
		double tRight = -1;
		double tTop = -1;
		if (ball.vX() != 0) {
			tLeft = (ball.r()-ball.centerX()) / ball.vX();
			tRight= (canvasWidth-ball.r()-ball.centerX()) / ball.vX();
		}
		if (ball.vY() != 0) {
			tTop = (ball.r()-ball.centerY()) / ball.vY();
		}
		String type = "";
		double tDest = Double.POSITIVE_INFINITY;
		if (tLeft > 0 && tLeft < tDest) {
			type = "left";
			tDest = tLeft;
		}
		if (tRight > 0 && tRight < tDest) {
			type = "right";
			tDest = tRight;
		}
		if (tTop > 0 && tTop < tDest) {
			type = "top";
			tDest = tTop;
		}
		if (!type.equals("")) {
			collisions.add(new BallWallCollision(ball, type, time.t, time.t+tDest));
		}
	}
	
	private void predictCollisions(Ball ball) {
		predictBallWallCollision(ball);
		for (Brick brick : bricks) {
			predictCollision(ball, brick);
		}
		System.out.println(collisions);
	}
	
	private void update(double dt) {
		bat.update(dt);
		ball.update(dt); // update the ball after bat (in case ball is stuck)
		time.add(dt);
	}
	
	private void update(double dt, boolean mouseClicked) {
		update(dt);
		if (ball.stuckOnBat() && mouseClicked) {
			ball.shootFromBat();
			predictCollisions(ball);
		}
	}
	
	private void initializeSprites() {
		bat = new Bat(canvasWidth / 2, batY, 150, 30, 0, canvasWidth);
		ball = new Ball(bat);
		bricks = new LinkedList<Brick>();
		// TODO layout all breaks
		bricks.add(new Brick(100, 100));
		bricks.add(new Brick(300, 400));
		scoreBoard = new ScoreBoard(0, 3);
	}
	
	private void render(GraphicsContext gc) {
		bat.render(gc);
		ball.render(gc);
		for (Brick brick : bricks) { brick.render(gc); }
		scoreBoard.render(gc);
	}
	

	public static void main(String[] args) {
		Application.launch(args);
	}
}
