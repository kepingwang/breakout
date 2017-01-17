package breakout;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A wrapper of the splash scene 
 * @author keping
 */
public class Splash {

	private Stage stage;
	private Scene scene;
	private GamePlay game;
	private VBox vBox;
	private Text welcome;
	
	public Splash(Stage stage) {
		this.stage = stage;
	}
	public void setGamePlay(GamePlay game) {
		this.game = game;
	}
	
	public void init() {
		vBox = new VBox();
		scene = new Scene(vBox, World.width, World.height);

		welcome = new Text("Welcome!");
		welcome.setFont(Font.font(35));
		
		Text startText = new Text("Click to Start\n\n");
		startText.setFont(Font.font(40));
		startText.setOnMouseClicked(e -> {
			startGame();
		});
		scene.setOnKeyPressed(e -> {
			String code = e.getCode().toString();
			if (code.equals("SPACE") || code.equals("ENTER")) {
				startGame();
			}
		});
		
		vBox.getChildren().addAll(welcome, startText);
		vBox.setSpacing(World.height / 10);
		vBox.setAlignment(Pos.CENTER);
	}
	
	public void go() {
		stage.setScene(scene);
		stage.show();
	}
	
	private void startGame() {
		game.init();
		game.play();
	}
	
	public void setMsg(String msg) {
		welcome.setText(msg);
	}
	public Scene scene() {
		return scene;
	}
	
}
