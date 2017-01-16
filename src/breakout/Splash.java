package breakout;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A wrapper of the splash scene 
 * @author keping
 */
public class Splash {

	private Stage stage;
	private Scene scene;
	private VBox vBox;
	private GamePlay game;
	
	public Splash(Stage stage) {
		this.stage = stage;
	}
	public void setGamePlay(GamePlay game) {
		this.game = game;
	}
	
	public void init() {
		vBox = new VBox();
		scene = new Scene(vBox);
		Rectangle button = new Rectangle(300, 150);
		button.setFill(Color.BLUE);
		
		Text text = new Text("Start");
		text.setOnMouseClicked(e -> {
			game.init();
			game.play();
		});
		
		vBox.getChildren().addAll(button, text);
	}
	public void go() {
		stage.setScene(scene);
		stage.show();
	}

	public Scene scene() {
		return scene;
	}
	
}
