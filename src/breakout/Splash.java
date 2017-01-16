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

	private Scene scene;
	private VBox vBox;
	
	public Splash(Scene gameScene, Stage stage) {
		vBox = new VBox();
		scene = new Scene(vBox);
		Rectangle button = new Rectangle(300, 150);
		button.setFill(Color.BLUE);
		
		Text text = new Text("Start");
		text.setOnMouseClicked(e -> {
			stage.setScene(gameScene);
		});
		
		vBox.getChildren().addAll(button, text);
	}

	public Scene scene() {
		return scene;
	}
	
}
