package breakout;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ScoreBoard {

	private int score;
	private int lives;
	private int level; // level index, start from 1
	private boolean needResetBall = false;
	
	public ScoreBoard(int initScore, int initLives, int initLevel) {
		score = initScore;
		lives = initLives;
		level = initLevel;
	}
	
	public void render(GraphicsContext gc) {
		gc.setFont(Font.font(12));
		gc.setFill(Color.FORESTGREEN);
		gc.fillText("Score: "+score, 10, 20);
		gc.fillText("Level: "+level, 10, 32);
		gc.fillText("Lives: "+lives, 10, 44);
		gc.setFill(Color.BLACK);
	}

	public int level() {
		return level;
	}
	public void addLevel(int x) {
		level += x;
	}
	public int score() {
		return score;
	}
	public void addScore(int ds) {
		score += ds;
	}
	
	public void addLife(int dl) {
		lives += dl;
		if (dl < 0) { needResetBall = true; }
		// when no life, switch to split screen;
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
	
	public void update(double dt) {
		
	}

}
