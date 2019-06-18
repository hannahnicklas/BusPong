package test;

import com.leapmotion.leap.*;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//import java.io.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.scene.effect.DropShadow;
//import javafx.scene.shape.SVGPath;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.text.Font;
//import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class Pong extends Application {

    private final SimpleLeapListener listener;
    private final Controller leapController;

    private final AnchorPane root;
    private final Scene scene;
    private final Circle ball;

    private Text startText;
    private Text restartText;

    private static final Color ELEMENT_COLOR = Color.WHITE;
    private static final int width = 800;
    private static final int height = 600;

    private static final double BALL_RADIUS = 15;
    private int ballYSpeed = 1;
    private int ballXSpeed = 1;
    private double ballXPos = width / 2;
    private double ballYPos = height / 2;

    public Player p1;
    public Player p2;

    public boolean gameStarted;
    public double mouseX;
    public double mouseY;

    public Pong() {
        listener = new SimpleLeapListener();
        leapController = new Controller();

        // GUI
        // create scene
        root = new AnchorPane();
        scene = new Scene(root, width, height);

        // add elements
        p1 = new Player(0, height/2);
        p2 = new Player(width, height/2);
        ball = new Circle(15, ELEMENT_COLOR);

        startText = new Text(300, 300, "Swipe right to start and move your hand up and down");
        restartText = new Text(width / 2, height / 2, "Swipe right to start again and move your hand up and down");
    }

    

    // @Override
    public void start(Stage stage) throws Exception {

        leapController.addListener(listener);

        // add Player
        root.getChildren().add(p1);
        root.getChildren().add(p2);

        // add Ball
        ball.setLayoutY(ballYPos);
        ball.setLayoutX(ballXPos);
        root.getChildren().add(ball);

        // add StartText
        root.getChildren().add(startText);

        // game not started
        scene.setFill(Color.GRAY);

        // game started by mouseclick
        scene.setOnMouseClicked(event -> {
            gameStarted = true;
            startGame(stage);
        });
        
        // game started by leap
        listener.pointProperty().addListener(new ChangeListener<Point2D>() {

            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                gameStarted = listener.isStarted();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        runGame(t1.getX(), t1.getY());
                    }
                });
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    public void run(double posX, double posY) {
        runGame(posX, posY);
    }

    public void startGame(Stage stage) {
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> run(mouseX, mouseY)));
		tl.setCycleCount(Timeline.INDEFINITE);
        scene.setOnMouseMoved(e ->  mouseY  = e.getY());
        
		stage.setScene(scene);
        stage.show();
		tl.play();
    }

    public void runGame(double posX, double posY) {
        if (gameStarted) {
            root.getChildren().remove(restartText);

            // double windowTLPosX = scene.getWindow().getX();
            // double windowTLPosY = scene.getWindow().getY();

            // bewegt den Player Balken 
            // Point2D leapCapture = root.sceneToLocal(posX - scene.getX() - windowTLPosX,
            //                                         posY - scene.getY() - windowTLPosY;
            // double handYPos = leapCapture.getY();

            // if (handYPos >= 0d && handYPos <= root.getHeight() - 2d * player1.getY()) {
            //     player1.setTranslateY(handYPos - (root.getHeight()/2 + PLAYER_HEIGHT/2));
            //     setP1Position(player1.getTranslateY());
            // }

            p1.setYPosition(posY);

            // bewegt den Ball
            ballXPos += ballXSpeed;
            ballYPos += ballYSpeed;

            // bewegt NPC Balken
            if (ballXPos < (0.75 * width)) { // wenn Ball weit weg
                p2.setYPosition(ballYPos);
            } else { // wenn Ball nah dran
                p2.setYPosition(ballYPos > p2.getYPosition() ? p2.getYPosition() + 1 : p2.getYPosition() - 1);
            }

            // lädt Scene neu
            root.getChildren().remove(startText);
            scene.setFill(Color.BLACK);

            ball.setLayoutY(ballYPos);
            ball.setLayoutX(ballXPos);

        } else {

            // root.getChildren().add(restartText);
            ballXPos = width / 2;
            ballYPos = height / 2;
            ballXSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
            ballYSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
        }
        
        // Ball versucht zu fliehen
        if (ballYPos > height || ballYPos < 0) { ballYSpeed *= -1; }
        
        
        // prüft, ob Ball einen Balken trifft
        if (   ((ballXPos + BALL_RADIUS > p2.getXPosition()) && ballIsInRange(p2)) 
            || ((ballXPos - BALL_RADIUS < p1.getXPosition()) && ballIsInRange(p1)) ) { // getroffen
            
            ballXSpeed = increasedSpeed(ballXSpeed);
            ballYSpeed = increasedSpeed(ballYSpeed);
            
            System.out.println("X Speed: " + Math.abs(ballXSpeed) + ", Y Speed: " + Math.abs(ballYSpeed));

        } else if (ballXPos < p1.getXPosition()) { // P1 nichte getroffen
            gameStarted = false;

        } else if (ballXPos > p2.getXPosition()) { // P2 nichte getroffen
            gameStarted = false;
        }

        p1.setFill(Color.CADETBLUE);
        p2.setFill(Color.CADETBLUE);
    }

    @Override
    public void stop() {
        leapController.removeListener(listener);

    }

    // Helfermethoden

    public boolean ballIsInRange(Player player) {
        if ( ballYPos >= player.getTopYPos() 
             && ballYPos <= player.getBottomYPos()) {
            return true;
        } else {
            return false;
        }
    }

    public int increasedSpeed(int speed) {
        double factor = 0.5;
        double ln = (int) Math.log(Math.abs(speed)) + 3;
        double signum = Math.signum(speed);
        double random = 1 - (1 * (Math.random() - 0.5)); // sorgt für Abweichung vom originalen Speed, damit unregelmasiger Ball
        
        double newSpeedD = (speed * random) + (factor * signum * ln);

        return (int) newSpeedD * -1;
    }
}
