package breakout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import collisions.BallBatCornerCollision;
import collisions.BallBatEdgeCollision;
import collisions.BallBrickCornerCollision;
import collisions.BallBrickEdgeCollision;
import collisions.BallWallCollision;
import collisions.Collision;
import collisions.Prediction;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import sprites.Ball;
import sprites.Bat;
import sprites.Brick;

public class GamePlay {

	private Stage stage;
	private Splash splash;
	private Scene scene;
	private GraphicsContext gc;
	private AnimationTimer animationTimer;
	private Level[] levels;
	
	public GamePlay(Stage stage) {
		this.stage = stage;
	}
	public void setSplash(Splash splash) {
		this.splash = splash;
	}
	public Scene scene() { return scene; }
	public void readLevelConfig() {
		levels = new Level[3];
		for (int i = 0; i < levels.length; i++) {
			levels[i] = new Level("config/level"+(i+1)+".txt");
		}
	}
	
	/**
	 * Records the current time 
	 * @author keping
	 */
	class Time {
		private double t;
		public Time(double t) { this.t = t; }
		public void add(double dt) { t += dt; }
	}
	Time time;
	long prevNanos;
	// sprites
	Bat bat;
	Ball ball;
	LinkedList<Brick> bricks;
	// other displays
	ScoreBoard scoreBoard;
	// keyboard and mouse events
	ArrayList<String> keyInput;
	ArrayList<Double> mouseMove;
	boolean shoot;
	boolean paused;
	// collision events
	PriorityQueue<Collision> collisions;
	
	public void init() {
		initScene();
		scoreBoard = new ScoreBoard(0, 3, 1);
		initAnimationTimer();
		initEventHandlers();
		readLevelConfig();
		initLevel();
	}
	
	// level index comes from scoreBoard
	public void initLevel() {
		initSprites();
		collisions = new PriorityQueue<Collision>();
		keyInput = new ArrayList<String>();
		mouseMove = new ArrayList<Double>();
		shoot = false;
		paused = false;
		time = new Time(0.0);
		prevNanos = 0;
	}
	
	public void nextLevel() {
		animationTimer.stop();
		scoreBoard.addLevel(1);
		if (scoreBoard.level() > levels.length) {
			endGame(true, scoreBoard.score());
		} else {
			initLevel();
			animationTimer.start();
		}
	}
	
	private void initScene() {
		Group root = new Group();
		Canvas canvas = new Canvas(World.width, World.height);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		scene = new Scene(root);
	}
	
	private void initSprites() {
		bat = new Bat(World.width/2, World.height-60, 150, 30, 0, World.width);
		ball = new Ball(bat);
		bricks = new LinkedList<Brick>();
		levels[scoreBoard.level()-1].layoutBricks(bricks);
	}
	
	private void initAnimationTimer() {
		animationTimer = new AnimationTimer() {
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
				 bat.limitV(dt);
				 mouseMove.clear();
				 predictCollisions(bat);
				 
				 // priority queue stores the collision events
				 while (!collisions.isEmpty() &&
						 collisions.peek().time() < endTime) {
					 Collision collision = collisions.poll();
					 if (collision.isValid()) {
						 // TODO comment out stdout
						 System.out.println(collision);
						 update(collision.time()-time.t);
						 collision.resolve();
						 scoreBoard.addScore(collision.score());
						 scoreBoard.addLife(collision.life());
						 if (scoreBoard.isGameOver()) {
							 endGame(false, scoreBoard.score());
						 }
						 if (scoreBoard.needResetBall()) {
							 ball = new Ball(bat); // reset ball
							 scoreBoard.setNeedResetBallFalse();
						 }
						 predictCollisions(ball);
					 }
				 }
				 
				 update(dt, shoot);
				 bat.stopMovement(time.t);
				 shoot = false; // reset
				 
				 removeGoneBricks();
				 if (levelClear()) {
					 nextLevel();
				 }
				 // render the images
				 gc.clearRect(0, 0, World.width, World.height);
				 render(gc);
			}
		};
	}
	
	private void initEventHandlers() {
		scene.setOnKeyPressed(e -> {
			String code = e.getCode().toString();
			if (!keyInput.contains(code)) {
				keyInput.add(code);
			}
			if (code.equals("P")) {
				paused = !paused;
				if (paused) { animationTimer.stop(); }
				else {
					prevNanos = 0;
					animationTimer.start(); 
				}
			}
			if (code.equals("SPACE") || code.equals("ENTER")) {
				shoot = true;
			}
		});
		scene.setOnKeyReleased(e -> {
			String code = e.getCode().toString();
			keyInput.remove(code);
			if (code.equals("EQUALS")) {
				speedUp(2);
			}
			if (code.equals("MINUS")) {
				speedUp(0.5);
			}
			if (code.equals("N")) {
				nextLevel();
			}
			if (code.equals("R")) {
				ball = new Ball(bat);
			}
			if (code.equals("K")) {
				killRandomBrick();
			}
			if (code.equals("L")) {
				scoreBoard.addLife(1);
			}
		});
		scene.setOnMouseMoved(e -> {
			mouseMove.add(e.getSceneX());
		});
		scene.setOnMouseClicked(e -> {
			shoot = true;
		});
	}

	/**
	 * Set stage scene to game play and start playing the game.
	 */
	public void play() {
		animationTimer.start();
		stage.setScene(scene);
	}

	public boolean levelClear() {
		if (bricks.isEmpty()) { return true; }
		for (Brick brick : bricks) {
			if (brick.breakable() && !brick.dead()) { return false;	}
		}
		return true;
	}
	
	public void endGame(boolean win, int score) {
		animationTimer.stop();
		if (win) { splash.setMsg("Score: "+score+". You just Won :)"); }
		else { splash.setMsg("Score: "+score+". You just Losed :("); }
		splash.go();
	}
	
	private void predictCollisions(Bat bat) {
		predictCollision(ball, bat);
	}
	
	/**
	 * Predict ball bat collisions
	 * @param ball
	 * @param bat
	 * @param t duration of the current frame.
	 */
	private void predictCollision(Ball ball, Bat bat) {
		if (ball.stuckOnBat()) { return; }
		if (ball.vY() <= 0) { return; }
		
		double dt = -1;
		String pos = "";
		double tEdge = (bat.centerY()-bat.height()/2 -ball.r()-ball.centerY()) / ball.vY();
		if (tEdge > 0 && 
			bat.centerX()+bat.vX()*tEdge-bat.width()/2+bat.r() < ball.centerX()+ball.vX()*tEdge &&
			bat.centerX()+bat.vX()*tEdge+bat.width()/2-bat.r() > ball.centerX()+ball.vX()*tEdge) {
			if (dt < 0 || tEdge < dt) {
				dt = tEdge;
				pos = "edge";
			}
		}
		double tLeft = Prediction.tBallBallCollision(
				ball.centerX(), ball.centerY(), ball.r(), ball.vX(), ball.vY(),
				bat.centerX()-bat.width()/2+bat.r(), bat.centerY(), bat.r(),
				bat.vX(), bat.vY()
				);
		if (tLeft > 0) {
			if (dt < 0 || tLeft < dt) {
				dt = tLeft;
				pos = "left";
			}
		}
		double tRight = Prediction.tBallBallCollision(
				ball.centerX(), ball.centerY(), ball.r(), ball.vX(), ball.vY(),
				bat.centerX()+bat.width()/2-bat.r(), bat.centerY(), bat.r(),
				bat.vX(), bat.vY()
				);
		if (tRight > 0) {
			if (dt < 0 || tRight < dt){
				dt = tRight;
				pos = "right";
			}
		}
		
		if (dt > 0) {
			if (pos.equals("edge")) {
				collisions.add(new BallBatEdgeCollision(ball, bat, time.t, time.t+dt));
			} else {
				collisions.add(new BallBatCornerCollision(ball, bat, pos, time.t, time.t+dt));
			}
		}

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
		double tBottom = -1;
		if (ball.vX() != 0) {
			tLeft = (ball.r()-ball.centerX()) / ball.vX();
			tRight= (World.width-ball.r()-ball.centerX()) / ball.vX();
		}
		if (ball.vY() != 0) {
			tTop = (ball.r()-ball.centerY()) / ball.vY();
			tBottom = (World.height+ball.r()*2-ball.centerY()) / ball.vY();
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
		if (tBottom > 0 && tBottom < tDest) {
			type = "bottom";
			tDest = tBottom;
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
	}
	
	private void update(double dt) {
		bat.update(dt);
		ball.update(dt); // update the ball after bat (in case ball is stuck)
		for (Brick brick : bricks) { brick.update(dt); }
		time.add(dt);
	}
	
	private void update(double dt, boolean mouseClicked) {
		update(dt);
		if (ball.stuckOnBat() && mouseClicked) {
			ball.shootFromBat();
			predictCollisions(ball);
		}
	}
	
	private void render(GraphicsContext gc) {
		bat.render(gc);
		ball.render(gc);
		for (Brick brick : bricks) { brick.render(gc); }
		scoreBoard.render(gc);
	}
	
	private void removeGoneBricks() {
		Iterator<Brick> it = bricks.iterator();
		while (it.hasNext()) {
			Brick brick = (Brick) it.next();
			if (brick.gone()) { it.remove(); }
		}
	}
	
	//// Cheat key and power up effects.
	private void speedUp(double factor) {
		ball.speedUp(factor, time.t);
		predictCollisions(ball);
	}
	
	private void killBrick(Brick brick) {
		brick.kill();
	}
	private void killRandomBrick() {
		if (bricks.isEmpty()) { return; }
		int count = (new Random()).nextInt(bricks.size());
		Iterator<Brick> it = bricks.iterator();
		for (int i = 0; i < count; i++) {
			it.next();
		}
		killBrick(it.next());
	}
	
}
