package breakout;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameApp extends Application {
	
	public static final double WIDTH = 600;
	public static final double HEIGHT = 600;
	public static final int NUM_LEVELS = 3;
	
	private Level[] levels;
	public void readLevelConfig() {
		levels = new Level[NUM_LEVELS];
		for (int i = 0; i < levels.length; i++) {
			levels[i] = new Level("config/level"+(i+1)+".txt");
		}
	}
	public Level[] levels() { return levels; }
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setResizable(false);
		stage.setTitle("Ugly Breakout Game");
		readLevelConfig();
		GameWorld game = new GameWorld(this, stage);
		Splash splash = new Splash(stage);
		game.setSplash(splash);
		splash.setGamePlay(game);
		splash.init();
		splash.show();
		stage.sizeToScene();
		game.initEnvironment();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
