package breakout;

import javafx.application.Application;
import javafx.stage.Stage;

public class World extends Application {
	
	public static final double epsDist = 1e-6;
	public static final double width = 600;
	public static final double height = 600;

	@Override
	public void start(Stage stage) throws Exception {
		stage.setResizable(false);
		stage.setTitle("Ugly Breakout Game");
		GamePlay game = new GamePlay(stage);
		Splash splash = new Splash(stage);
		game.setSplash(splash);
		splash.setGamePlay(game);
		splash.init();
		splash.go();
		stage.sizeToScene();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
