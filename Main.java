package puzzle;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Main extends Application implements Runnable {
    private static final int SCENE_WIDTH = 710;
    private static final int SCENE_HEIGHT = 710;
    private static final int NUMBER_OF_BRANCH_GENERATIONS = 8;
    private Group rootContent;
    private Group treeContent;

    @Override
    public void start(final Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.sizeToScene();
        stage.setScene(new AppScene());

        final Button close = new Button("X");
        close.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        close.setStyle("-fx-background-color:transparent;-fx-text-fill:red;");
        close.setOpacity(1);
        close.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent arg0) {
                Platform.exit();
                System.exit(0);
            }
        });
        close.setTranslateY(stage.getScene().getHeight() - 30);
        close.setTranslateX(stage.getScene().getWidth() - 1067);
        rootContent.getChildren().add(close);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                System.out.println("FPS " + com.sun.javafx.perf.PerformanceTracker.getSceneTracker(stage.getScene()).getInstantFPS());
            }
        }, 0, 1000);



        new Animator(new TreeGenerator(treeContent, NUMBER_OF_BRANCH_GENERATIONS)).run();

    }

    public void run(){
    	launch();
    }

    private class AppScene extends Scene {

        public AppScene() {
            super(rootContent = new Group(), SCENE_WIDTH, SCENE_HEIGHT, Color.TRANSPARENT);
            rootContent.setClip(new Rectangle(-SCENE_WIDTH / 2, 0, SCENE_WIDTH, SCENE_HEIGHT)); //Scene shape and size

            Rectangle background = new Rectangle(-SCENE_WIDTH / 2, 0, SCENE_WIDTH, SCENE_HEIGHT);
            background.setFill(new LinearGradient(0, 0, 0, SCENE_HEIGHT, false, CycleMethod.NO_CYCLE, new Stop(0, Color.YELLOWGREEN), new Stop(0.3, Color.LIGHTBLUE),
                    new Stop(1., new Color(1, 1, 1, 0)))); //background color
            rootContent.getChildren().add(background);
            rootContent.getChildren().add(treeContent = new Group()); // tree layout
            rootContent.getTransforms().addAll(new Translate(SCENE_WIDTH / 2, SCENE_HEIGHT), new Rotate(180));

        }
    }
}
