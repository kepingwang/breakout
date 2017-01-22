package breakout;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import collisions.Collision;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import sprites.Ball;
import sprites.Bat;
import sprites.Brick;
import sprites.Bullet;
import sprites.Displayable;
import sprites.PowerUp;
import sprites.Sprite;
import sprites.Wall;

/**
 * The world where the game is played.
 * @author keping
 *
 */
public class GameWorld {

	// JavaFX components
	private GameApp gameApp;
	private Stage stage;
	private Splash splash;
	private Group root;
	private Scene scene;
	private GraphicsContext gc;
	private AnimationTimer animationTimer;
	
	// Time
	private double currTime;
	public double currTime() { return currTime; }
	long prevNanos;
	 
	// Sprites
	Bat bat;
	List<Ball> balls;
	List<Wall> walls;
	List<Brick> bricks;
	List<PowerUp> powerUps;
	List<Bullet> bullets;
	
	// Other displays
	ScoreBoard scoreBoard;
	List<Displayable> fadings; // contains fading bricks
	BlockingImage blockingImage;
	
	// User input events
	ArrayList<String> keyInput;
	ArrayList<Double> mouseMove;
	boolean shootFromBat;
	boolean paused;
	
	// Collision events
	PriorityQueue<Collision> collisions;
	
	// Initialize the object
	public GameWorld(GameApp gameApp, Stage stage) {
		this.gameApp = gameApp;
		this.stage = stage; 
	}
	public void setSplash(Splash splash) { this.splash = splash; }
	public Scene scene() { return scene; }
	
	// Initialize the environment for each level to play. Called once.
	private void initScene() {
		root = new Group();
		Canvas canvas = new Canvas(GameApp.WIDTH, GameApp.HEIGHT);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().addAll(canvas);
		scene = new Scene(root);
	}
	private void initAnimationTimer() {
		animationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				
				// calculate elapsed time
				if (prevNanos == 0) {
					prevNanos = now;
					return;
				}
				long deltaNanos = now - prevNanos;
				prevNanos = now;
				double deltaTime = deltaNanos / 1.0e9;
				double endTime = currTime + deltaTime; 

				handleUserInput(deltaTime);
				
				// Event driven simulation. pq stores future collisions.
				while (!collisions.isEmpty() &&
						collisions.peek().timeHappening() < endTime) {
					Collision collision = collisions.poll();
					if (collision.isValid()) {
						updateTo(collision.timeHappening());
						collision.resolve();
					}
				}
				
				evaluateWinLose();
				
				updateTo(endTime);
				bat.setVX(0);
				
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
				shootFromBat = true;
			}
		});
		scene.setOnKeyReleased(e -> {
			String code = e.getCode().toString();
			keyInput.remove(code);
			if (code.equals("EQUALS")) { speedUp(1.5); }
			else if (code.equals("MINUS")) { speedUp(0.666);	}
			else if (code.equals("N")) { playNextLevel(); }
			else if (code.equals("R")) { resetBall(); }
			else if (code.equals("L")) { addLife(); }
			else if (code.equals("B")) { splitBalls(); }
			else if (code.equals("S")) { makeBatSticky(); }
			else if (code.equals("G")) { makeBatGunner(); }
			else if (code.equals("Z")) { smallBall(); }
			else if (code.equals("X")) { midBall(); }
			else if (code.equals("C")) { bigBall(); }
			else if (code.equals("DIGIT1")) { shortBat(); }
			else if (code.equals("DIGIT2")) { midBat(); }
			else if (code.equals("DIGIT3")) { longBat(); }
		});
		scene.setOnMouseMoved(e -> {
			mouseMove.add(e.getSceneX());
		});
		scene.setOnMouseClicked(e -> {
			shootFromBat = true;
		});
	}
	public void initEnvironment() {
		initScene();
		initAnimationTimer();
		initEventHandlers();
	}
	
	// Initialize level and play.
	public void initPlay() {
		scoreBoard = new ScoreBoard(this, 0, 3, 1);
	}
	private void initStates() {
		collisions = new PriorityQueue<Collision>();
		keyInput = new ArrayList<String>();
		mouseMove = new ArrayList<Double>();
		shootFromBat = false;
		paused = false;
		currTime = 0;
		prevNanos = 0;
	}
	private void initSprites(int level) {
		balls = new ArrayList<Ball>();
		walls = new ArrayList<Wall>();
		bricks = new ArrayList<Brick>();
		powerUps = new ArrayList<PowerUp>();
		bullets = new ArrayList<Bullet>();
		bat = new Bat(this, GameApp.WIDTH/2, GameApp.HEIGHT - 2 * Bat.INIT_HEIGHT);
		balls.add(new Ball(bat));
		walls.add(new Wall(this, 0, GameApp.HEIGHT / 2 + Ball.INIT_RADIUS * 2, Wall.LEFT));
		walls.add(new Wall(this, GameApp.WIDTH, GameApp.HEIGHT / 2 + Ball.INIT_RADIUS * 2, Wall.RIGHT));
		walls.add(new Wall(this, GameApp.WIDTH / 2, 0, Wall.TOP));
		walls.add(new Wall(this, GameApp.WIDTH / 2, 
				GameApp.HEIGHT + Ball.INIT_RADIUS * 4, Wall.BOTTOM));
		gameApp.levels()[scoreBoard.level()-1].layoutBricks(bricks, this);
	}
	private void playLevel(int level) {
		initStates();
		initSprites(level);
		fadings = new ArrayList<Displayable>();
		blockingImage = new BlockingImage();
		scoreBoard.setLevel(level);
		animationTimer.start();
		stage.setScene(scene);
	}
	public void play() {
		playLevel(scoreBoard.level());
	}

	// Methods for scoreBoard (scores, lives, levels)
	public void addScore(int score) {
		scoreBoard.addScore(score); 
	}
	private boolean winLevel() {
		for (Brick brick : bricks) {
			if (brick.breakable()) { return false; }
		}
		return true;
	}
	private boolean loseLife() {
		return balls.isEmpty();
	}
	private void evaluateWinLose() {
		if (winLevel()) { playNextLevel(); }
		else if (loseLife()) { scoreBoard.addLife(-1); }
	}
	private void playNextLevel() {
		animationTimer.stop();
		if (scoreBoard.level() == gameApp.levels().length) {
			endGame(true, scoreBoard.score());
		} else {
			scoreBoard.setLevel(scoreBoard.level() + 1);
			playLevel(scoreBoard.level());
		}
	}
	public void resetBall() {
		balls = new ArrayList<Ball>();
		balls.add(new Ball(bat));
	}
	public void endGame(boolean win, int score) {
		animationTimer.stop();
		if (win) { splash.setMsg("Score: "+score+". You just Won :)"); }
		else     { splash.setMsg("Score: "+score+". You just Losed :("); }
		splash.show();
	}
	
	/**
	 * Handle user input to update bat speed and shooting (ball or bullet).
	 * @param dt
	 */
	private void handleUserInput(double dt) {
		double x0 = bat.x();
		double x1 = x0;
		double vx = 0;
		// update vx for bat (vx only for current frame)
		if (mouseMove.isEmpty()) {
			if (keyInput.contains("LEFT") || keyInput.contains("A")) {
				vx -= Bat.KEYBOARD_SPEED;
			}
			if (keyInput.contains("RIGHT") || keyInput.contains("D")) {
				vx += Bat.KEYBOARD_SPEED;
			}
			x1 = x0 + vx * dt;
		} else {
			x1 = mouseMove.get(mouseMove.size()-1);
		}
		if (x1 - bat.w()/2 < 0) {
			x1 = bat.w()/2; 
		} else if (x1 + bat.w()/2 > GameApp.WIDTH) {
			x1 = GameApp.WIDTH - bat.w()/2; 
		}
		vx = (x1 - x0) / dt;
		bat.setVX(vx);
		mouseMove.clear();
		
		if (shootFromBat) {
			for (Ball ball : balls) { ball.shootFromBat(); }
			shootFromBat = false;
			bat.shootBullet();
		}
	}

	// Methods to get all super classes to loop over
	public List<Sprite> getAllSprites() {
		List<Sprite> sprites = new ArrayList<>();
		sprites.addAll(bullets);
		sprites.add(bat);
		sprites.addAll(balls);
		sprites.addAll(bricks);
		sprites.addAll(powerUps);
		sprites.addAll(walls);
		return sprites;
	}
	public List<Displayable> getOtherDisplays() {
		List<Displayable> otherDisplays = new ArrayList<>();
		otherDisplays.addAll(fadings);
		otherDisplays.add(blockingImage);
		otherDisplays.add(scoreBoard);
		return otherDisplays;
	}
	
	// Methods for adding and removing sprites/displays in the world 
	public void removeBall(Ball ball) { balls.remove(ball); }
	public void removeBrick(Brick brick) { bricks.remove(brick); }
	public void removePowerUp(PowerUp powerUp) { powerUps.remove(powerUp); }
	public void addPowerUp(PowerUp powerUp) { powerUps.add(powerUp); }
	public void addToFadings(Brick brick) {	fadings.add(brick);	}
	public void removeFromFadings(Brick brick) { fadings.remove(brick); }
	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
		blockingImage.enlargeByFactor(1.7);
	}
	public void removeBullet(Bullet bullet) { bullets.remove(bullet); }
	
	/**
	 * Predict the collisions that {@code sprite} is going to have with
	 * all other sprites, and add the collisions to the priority queue.
	 * 
	 * This method shall be called each time the trajectory of a sprite
	 * changes (initialization, v change, hard position reset, size change...)
	 * @param sprite
	 */
	public void predictCollisions(Sprite sprite) {
		if (!getAllSprites().contains(sprite)) { return; }
		for (Sprite other : getAllSprites()) {
			if (sprite == other) { continue; }
			Collision collision = sprite.predictCollision(other);
			if (collision != null) {
				collisions.add(collision); 
			}
		}
	}

	/**
	 * Update the positions (states) of the sprites and
	 * other displayables by time {@code dt}
	 * @param dt
	 */
	private void update(double dt) {
		for (Sprite sprite : getAllSprites()) {
			sprite.update(dt);
		}
		for (Displayable disp : getOtherDisplays()) {
			disp.update(dt);
		}
	}
	private void updateTo(double targetTime) {
		update(targetTime - currTime);
		currTime = targetTime;
	}
	
	/**
	 *  Render the displayables on the canvas
	 * @param gc
	 */
	private void render(GraphicsContext gc) {
		gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);
		for (Sprite sprite : getAllSprites()) {
			sprite.render(gc); 
		}
		for (Displayable disp : getOtherDisplays()) {
			disp.render(gc);
		}
	}
	
	//// Cheat key and power up effects.
	public void addLife() {
		scoreBoard.addLife(1);
	}
	public void speedUp(double factor) {
		for (Ball ball : balls) {
			ball.speedUp(factor);
		}
	}
	public void smallBall() {
		for (Ball ball : balls) {
			ball.setR(Ball.INIT_RADIUS / 2);
		}
	}
	public void midBall() {
		for (Ball ball : balls) {
			ball.setR(Ball.INIT_RADIUS);
		}
	}
	public void bigBall() {
		for (Ball ball : balls) {
			ball.setR(Ball.INIT_RADIUS * 2);
		}
	}
	public void midBat() {
		bat.setW(Bat.INIT_WIDTH);
	}
	public void shortBat() {
		bat.setW(Bat.INIT_WIDTH / 2);
	}
	public void longBat() {
		// TODO: buggy. ball may be in the middle of long bat.
		// Could possibly make the bat grow longer gradually.
		bat.setW(Bat.INIT_WIDTH * 2);
	}
	public void splitBalls() { // the other ball has opposite v
		List<Ball> oppoBalls = new ArrayList<>();
		for (Ball ball : balls) {
			oppoBalls.add(new Ball(ball));
		}
		balls.addAll(oppoBalls);
		for (Ball oppoBall : oppoBalls) {
			oppoBall.setV( - oppoBall.vx(), - oppoBall.vy());
		}
	}
	public void makeBatGunner() {
		bat.makeGunner(15);
	}
	public void makeBatSticky() {
		bat.makeSticky(15);
	}
}
