package breakout;

import javafx.application.Application;
import javafx.stage.Stage;

public class World extends Application {
	
	public static final double epsDist = 1e-6;
	public static final double canvasWidth = 400;
	public static final double canvasHeight = 600;

	@Override
	public void start(Stage stage) throws Exception {
		GamePlay game = new GamePlay(stage);
		Splash splash = new Splash(stage);
		game.setSplash(splash);
		splash.setGamePlay(game);
		splash.init();
		splash.go();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
