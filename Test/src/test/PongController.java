package test;

import com.leapmotion.leap.*;
import com.sun.glass.events.SwipeGesture;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class PongController extends Application {

    private final SimpleLeapListener listener;
    private final Controller leapController;

    private static final Color ELEMENT_COLOR = Color.WHITE;

    private final AnchorPane root;
    private final Scene scene;

    private Text startMessage;
    private Text restartMessage;
    private Text gestureText;


    public Ball ball;

    public Player p1;
    public Player p2;

    public Text scoreP1;
    public Text scoreP2;

    public boolean gameStarted;
    public boolean isFirstStart;
    public double mouse;

    public PongController() {
        Var.setOrientation("horizontal");

        isFirstStart = true;

        listener = new SimpleLeapListener();
        leapController = new Controller();

        // GUI
        // create scene
        root = new AnchorPane();
        scene = new Scene(root, Var.minScreenSize[Var.X], Var.minScreenSize[Var.Y]);

        // declare elements
        ball = new Ball();
        p1 = new Player(0);
        p2 = new Player(Var.minScreenSize[0]);

        scoreP1 = new Text(0 + Var.minScreenSize[Var.X]/4, Var.minScreenSize[Var.Y]/4, "" + p1.getScore());
        scoreP2 = new Text(Var.minScreenSize[Var.X] - Var.minScreenSize[Var.X]/4, Var.minScreenSize[Var.Y]/4, "" + p2.getScore());

        startMessage = new Text(Var.minScreenSize[Var.X] / 2, Var.minScreenSize[Var.Y] / 2, "SWIPE RIGHT TO START");
        restartMessage = new Text(Var.minScreenSize[Var.X] / 2, Var.minScreenSize[Var.Y] / 2, "DRAW A CLOCKWISE CIRCLE WITH YOUR INDEX FINGER");

        gestureText = new Text(250, Var.minScreenSize[Var.Y] / 2.5, "SWIPING");


        // style elemets
        scoreP1.setFill(ELEMENT_COLOR);
        scoreP1.setFont(Font.font("consolas", 40));

        scoreP2.setFill(ELEMENT_COLOR);
        scoreP2.setFont(Font.font("consolas", 40));

        gestureText.setFill(Color.CADETBLUE);
        gestureText.setFont(Font.font("consolas", 80));
    }


    // @Override
    public void start(Stage stage) throws Exception {

        leapController.addListener(listener);

        root.getChildren().add(ball);
        root.getChildren().add(p1);
        root.getChildren().add(p2);
        root.getChildren().add(scoreP1);
        root.getChildren().add(scoreP2);

        root.getChildren().add(startMessage);

        // game not started
        scene.setFill(Color.DARKGREY);

        // game started by mouseclick
        // scene.setOnMouseClicked(event -> {
        //     gameStarted = true;
        //     clickStartGame(stage);
        // });

        // game started by leap
        listener.pointProperty().addListener(new ChangeListener<Point2D>() {

            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                gameStarted = listener.isStarted();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (Var.orientation.equals("vertical")) {
                            runGame(t1.getY());
                            
                        } else {
                            runGame(t1.getX());
                        }
                    }
                });
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    public void run(double pointerPos) {
        runGame(pointerPos);
    }


    public void clickStartGame(Stage stage) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(20), e -> run(mouse)));
        tl.setCycleCount(Timeline.INDEFINITE);
        scene.setOnMouseMoved(e -> mouse = e.getY());

        stage.setScene(scene);
        stage.show();
        tl.play();

    }

    public void runGame(double pointerPos) {
        if (gameStarted) {
            root.getChildren().remove(gestureText);
            playGame(pointerPos);
         
        } else if (listener.gestureProgress() > 0 && listener.gestureDetected()) {
            root.getChildren().remove(gestureText);
            gestureText.setOpacity(listener.gestureProgress());
            root.getChildren().add(gestureText);
        }
    }

    public void playGame(double pointerPos) {
        root.getChildren().remove(restartMessage);

        // bewegt den Ball
        if (!ball.move(p1, p2)) { stopGame(); }

        p1.setLayout(Var.Y, pointerPos);
        p2.followBall(ball);

        // lädt Scene neu nach erstem Start
        root.getChildren().remove(startMessage);
        scene.setFill(Color.BLACK);
    }

    public void stopGame() {
        scoreP1.setText("" + p1.getScore());
        scoreP2.setText("" + p2.getScore());
        
        gameStarted = false;
        listener.setStarted(false);

        scene.setFill(Color.GRAY);
        // root.getChildren().add(restartText);
        ball.resetPosition();
        ball.resetSpeed();
    }

    @Override
    public void stop() {
        leapController.removeListener(listener);
    }
}
