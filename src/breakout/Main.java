package breakout;

import java.io.File;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	
	private Timeline timeline;

	private Integer counter = 0;
	
	Image earth;
	double earthX = 200.0;
	double earthY = 100.0;
	
	@Override
	public void start(Stage stage) throws Exception {

		Canvas canvas = new Canvas(400, 200);
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		
//		// draw "Hello World"
//		gc.setFill(Color.RED);
//		gc.setStroke(Color.BLACK);
//		gc.setLineWidth(2);
//		Font helloFont = Font.font("Times New Roman", FontWeight.BOLD, 48);
//		gc.setFont(helloFont);
//		gc.fillText("Hello, World!", 60, 50);
//		gc.strokeText("Hello, World!", 60, 50);
		
		// draw earth image
		String earthImg = new File("img/earth.png").toURI().toString();
		System.out.println(earthImg);
		earth = new Image(earthImg);

		
		Group root = new Group();
		root.getChildren().addAll(canvas);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Breakout");

		
//		// create a timeline for moving the circle
//		timeline = new Timeline();
//		timeline.setCycleCount(Timeline.INDEFINITE);
////		timeline.setAutoReverse(true);
//		Duration duration = Duration.millis(1000);
//		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent event) {
//				stack.setTranslateX(java.lang.Math.random()*200-100);
////				counter = 0;
//			}
//		};
//		KeyValue keyValueX = new KeyValue(stack.scaleXProperty(), 2);
//		KeyValue keyValueY = new KeyValue(stack.scaleYProperty(), 2);
//
//		KeyFrame keyFrame = new KeyFrame(duration, onFinished, keyValueX, keyValueY);
//		
//		timeline.getKeyFrames().add(keyFrame);
//		timeline.play();
		
		ArrayList<String> input = new ArrayList<String>();
		
		scene.setOnKeyPressed(e -> {
			String code = e.getCode().toString();
			if (!input.contains(code)) {
				input.add(code);
			}
		});

		scene.setOnKeyReleased(e -> {
			String code = e.getCode().toString();
			input.remove(code);
		});
		
		scene.setOnMouseMoved(e -> {
			earthX = e.getSceneX();
		});
		
		// add specific action when each frame is started
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				gc.clearRect(0, 0, 512, 512);
				gc.drawImage(earth, earthX, earthY);
				if (input.contains("LEFT")) {
					earthX -= 5;
				}
				if (input.contains("RIGHT")) {
					earthX += 5;
				}
			}
		};
		timer.start();
		
		stage.show();
	}


	public static void main(String[] args) {
		Application.launch(args);
	}

}
