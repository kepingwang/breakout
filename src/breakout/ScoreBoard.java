package breakout;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public class ScoreBoard {

	private int score;
	private int lives;
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
	
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

}
