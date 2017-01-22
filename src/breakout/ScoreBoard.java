package breakout;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import sprites.Displayable;

/**
 * ScoreBoard class to maintain score, lives, and level.
 * @author keping
 *
 */
public class ScoreBoard implements Displayable {

	private GameWorld world;
	private int score;
	private int lives;
	private int level; // level index, start from 1
	private boolean needResetBall = false;
	
	public ScoreBoard(GameWorld world, int initScore, int initLives, int initLevel) {
		this.world = world;
		score = initScore;
		lives = initLives;
		level = initLevel;
	}
	


	public int level() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int score() {
		return score;
	}
	public void addScore(int ds) {
		score += ds;
	}
	
	public void addLife(int dl) {
		lives += dl;
		if (lives <= 0) {
			world.endGame(false, score);
		} else if (dl < 0) {
			world.resetBall();
		}
	}
	
	public boolean isGameOver() {
		return lives <= 0;
	}
	
	public void setNeedResetBallFalse() {
		needResetBall = false;
	}
	public boolean needResetBall() {
		return needResetBall;
	}
	
	@Override
	public void update(double dt) { }
	
	@Override
	public void render(GraphicsContext gc) {
		gc.setFont(Font.font(12));
		gc.setFill(Color.FORESTGREEN);
		gc.fillText("Score: "+score, 10, 20);
		gc.fillText("Level: "+level, 10, 32);
		gc.fillText("Lives: "+lives, 10, 44);
		gc.setFill(Color.BLACK);
	}

}
