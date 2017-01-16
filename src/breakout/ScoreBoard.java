package breakout;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public class ScoreBoard {

	private int score;
	private int lives;
	private boolean needResetBall = false;
	
	public ScoreBoard(int initScore, int initLives) {
		score = initScore;
		lives = initLives;
	}
	
	public void render(GraphicsContext gc) {
		gc.setFont(Font.font(12));
		gc.fillText("Score: "+score, 20, 20);
		gc.fillText("Lives: "+lives, 20, 50);
		
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
