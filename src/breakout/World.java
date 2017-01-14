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
	
	double currTime = 0.0;
	long prevNanos = 0;
	
	// canvas size
	private final double canvasWidth = 400;
	private final double canvasHeight = 600;
	private final double barY = 550;
	
	
	
	// sprites
	Bat bat;
	Ball ball;
	LinkedList<Brick> bricks;
	// other displays
	ScoreBoard scoreBoard;

	// keyboard and mouse events
	ArrayList<String> keyInput = new ArrayList<>();
	ArrayList<Double> mouseInput = new ArrayList<>();
	boolean mouseClicked = false;
	
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
			mouseInput.add(e.getSceneX());
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
				 double deltaTime = deltaNanos / 1.0e9;
				 currTime += deltaTime;
				 prevNanos = now;
				 
				 // Move the bat according to keyboard and mouse input
				 // reset event queue
				 // Assume that the bat is moving with uniform velocity within frame
				 double prevBatX = bat.getX();
				 if (mouseInput.isEmpty()) {
					 if (keyInput.contains("LEFT") || keyInput.contains("A")) {
						 bat.update(-1, deltaTime);
					 }
					 if (keyInput.contains("RIGHT") || keyInput.contains("D")) {
						 bat.update(1, deltaTime);
					 }
				 } else {
					 bat.setX(mouseInput.get(mouseInput.size()-1)-1);
				 }
				 keyInput.clear();
				 mouseInput.clear();
				 
				 if (ball.stuckOnBat()) {
					 ball.update(bat);
					 if (mouseClicked) {
						 ball.shootFromBat();
						 predictCollisions(ball);
					 }
				 } else {
					 ball.update(deltaTime);
					 
					 
					 
					 
				 }
				 
				 
				 // priority queue stores the ball-brick collision events
				 if (!collisions.isEmpty()) {
					 while (collisions.peek().time() < currTime) {
						 Collision collision = collisions.poll();
						 if (collision.isValid()) {
							 // deal with collision, update ball, bat, and bricks
							 // update future collisions
						 }
					 }
				 }
				 
				 // render the images
				 gc.clearRect(0, 0, canvasWidth, canvasHeight);
				 render(gc);
			}
		};
		
		timer.start();
		stage.show();
	}
	
	private void predictCollisions(Ball ball) {
		
	}
	
	private void initializeSprites() {
		bat = new Bat(canvasWidth / 2, barY, 150, 30, 0, canvasWidth);
		ball = new Ball(bat);
		bricks = new LinkedList<Brick>();
		scoreBoard = new ScoreBoard();
		// TODO layout all breaks
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
