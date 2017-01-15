package breakout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

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
				 
				 // priority queue stores the ball-brick collision events
				 while (!collisions.isEmpty() &&
						 collisions.peek().time() < endTime) {
					 Collision collision = collisions.poll();
					 if (collision.isValid()) {
						 update(collision.time()-time.t);
						 collision.resolve();
						 predictCollisions(ball);
					 } else {
						 System.out.println("Invalid: "+collision);
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
		// TODO
		
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
		System.out.println(collisions);
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
		scoreBoard = new ScoreBoard();
		// TODO layout all breaks
		bricks.add(new Brick(100, 100));
		bricks.add(new Brick(300, 400));
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
