package test;

import com.leapmotion.leap.*;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.scene.text.Font;
import javafx.util.Duration;


public class PongController extends Application {

    private final SimpleLeapListener listener;
    private final Controller leapController;

    private static final Color ELEMENT_COLOR = Color.WHITE;

    private final AnchorPane root;
    private final Scene scene;

    private final Text startMessage;
    private final Text restartMessage;
    private final Text gestureText;
    private final Text playerChangedMessage;

    private final Text arrivalL9;
    private final Text arrivalL50;


    public Ball ball;

    public Player p1;
    public Player p2;
    public Player activePlayer;
    public Player npcPlayer;

    public Text scoreP1;
    public Text scoreP2;

    public boolean isFirstStart;
    public double mouse;

    public PongController() {
        Var.setOrientation("vertical");

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
        activePlayer = p1;
        npcPlayer = p2;

        scoreP1 = new Text(0 + Var.minScreenSize[Var.X]/4, Var.minScreenSize[Var.Y]/4, "" + p1.getScore());
        scoreP2 = new Text(Var.minScreenSize[Var.X] - Var.minScreenSize[Var.X]/4, Var.minScreenSize[Var.Y]/4, "" + p2.getScore());

        startMessage = new Text(Var.minScreenSize[Var.X]/2 - 220, Var.minScreenSize[Var.Y] / 1.5, "SWIPE RIGHT TO START");
        restartMessage = new Text(Var.minScreenSize[Var.X] / 2, Var.minScreenSize[Var.Y] / 2, "DRAW A CLOCKWISE CIRCLE WITH YOUR INDEX FINGER");
        playerChangedMessage = new Text(Var.minScreenSize[Var.X] / 2, Var.minScreenSize[Var.Y] / 2, "PLAYER CHANGED");

        gestureText = new Text(250, Var.minScreenSize[Var.Y] / 2.5, "SWIPING");

        arrivalL9 = new Text(50, Var.minScreenSize[Var.Y] - 50, "Bus 9: 5 min");
        arrivalL50 = new Text(Var.minScreenSize[Var.X] - 200, Var.minScreenSize[Var.Y] - 50, "Bus 50: 13 min");


        // style elemets
        scoreP1.setFill(ELEMENT_COLOR);
        scoreP1.setFont(Font.font("consolas", 40));

        scoreP2.setFill(ELEMENT_COLOR);
        scoreP2.setFont(Font.font("consolas", 40));

        startMessage.setFill(Color.CADETBLUE);
        startMessage.setFont(Font.font("consolas", 40));

        gestureText.setFill(Color.CADETBLUE);
        gestureText.setFont(Font.font("consolas", 80));

        playerChangedMessage.setFill(ELEMENT_COLOR);
        playerChangedMessage.setFont(Font.font("consolas", 80));
        
        arrivalL9.setFill(ELEMENT_COLOR);
        arrivalL9.setFont(Font.font("consolas", 20));
        
        arrivalL50.setFill(ELEMENT_COLOR);
        arrivalL50.setFont(Font.font("consolas", 20));
    }


    @Override
    public void start(Stage stage) throws Exception {

        leapController.addListener(listener);

        root.getChildren().add(ball);
        root.getChildren().add(p1);
        root.getChildren().add(p2);
        root.getChildren().add(scoreP1);
        root.getChildren().add(scoreP2);

        root.getChildren().add(arrivalL9);
        root.getChildren().add(arrivalL50);

        root.getChildren().add(startMessage);

        // game not started
        scene.setFill(Color.DARKGREY);

        // game started by mouseclick
        // scene.setOnMouseClicked(event -> {
        //     Var.setGameStarted(true);
        //     clickStartGame(stage);
        // });

        // game started by leap
        listener.pointProperty().addListener(new ChangeListener<Point2D>() {

            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                Platform.runLater(() -> {

                    if (listener.isGrabGestureCompleted()) {
                        changeActivePlayer();
                    }
                    
                    if (Var.orientation.equals("vertical")) {
                        runGame(t1.getY());
                        
                    } else {
                        runGame(t1.getX());
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
        if (Var.isGameStarted()) {
            root.getChildren().remove(gestureText);
            playGame(pointerPos);
         
        } else if (listener.getSwipeGestureProgress() > 0 && listener.isSwipeGestureDetected()) {
            root.getChildren().remove(gestureText);
            gestureText.setOpacity(listener.getSwipeGestureProgress());
            root.getChildren().add(gestureText);
        }
    }

    public void playGame(double pointerPos) {
        root.getChildren().remove(restartMessage);

        // bewegt den Ball
        if (!ball.move(p1, p2)) { stopGame(); }

        activePlayer.setLayout(Var.Y, pointerPos);
        npcPlayer.followBall(ball);

        // lädt Scene neu nach erstem Start
        root.getChildren().remove(startMessage);
        scene.setFill(Color.BLACK);
    }

    public void stopGame() {
        scoreP1.setText("" + p1.getScore());
        scoreP2.setText("" + p2.getScore());
        
        Var.setGameStarted(false);

        scene.setFill(Color.GRAY);
        // root.getChildren().add(restartText);
        ball.resetPosition();
        ball.resetSpeed();
    }

    @Override
    public void stop() {
        leapController.removeListener(listener);
    }


    public void restartGame() {
        System.out.println("Game restarted");

        scene.setFill(Color.DARKORCHID);

        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        scene.setFill(Color.BLACK);
    }

    public void changeActivePlayer() {
        System.out.println("Players changed");
        if (activePlayer == p1) {
            activePlayer = p2;
            npcPlayer = p1;
        } else {
            activePlayer = p1;
            npcPlayer = p2;
        }

        // root.getChildren().add(playerChangedMessage);
        // scene.setFill(Color.CRIMSON);
        
        // this.sleep(5000);
        
        // root.getChildren().remove(playerChangedMessage);
        // scene.setFill(Color.BLACK);

        p1.resetScore();
        p2.resetScore();

        scoreP1.setText("" + p1.getScore());
        scoreP2.setText("" + p2.getScore());

        listener.setGrabGestureCompleted(false);
    }

    // public void sleep(int millisec) {
    //     listener.sleep(millisec);
    //     try {
    //         Thread.sleep(millisec);
    //     } catch (InterruptedException ex) {}
    // }
}
