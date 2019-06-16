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
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
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
//import javafx.util.Duration;


public class PongController extends Application {

    private final SimpleLeapListener listener;
    private final Controller leapController;

    private final AnchorPane scene;
    private final Scene field;
    private final Rectangle player1;
    private final Rectangle player2;
    private final Circle ball;

    private Text startText;
    private Text restartText;

    private static final Color ELEMENT_COLOR = Color.WHITE; 
    private static final int width = 800;
    private static final int height = 600;
    private static final int PLAYER_HEIGHT = 100;
    private static final int PLAYER_WIDTH = 15;
    private static final double BALL_R = 15;
    private int ballYSpeed = 1;
    private int ballXSpeed = 1;
    private double p1TopPosition = height / 2;
    private double p1BottomPosition = p1TopPosition + PLAYER_HEIGHT;
    private double p2TopPosition = height / 2;
    private double p2BottomPosition = p2TopPosition + PLAYER_HEIGHT;
    private double ballXPos = width / 2;
    private double ballYPos = height / 2;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private int p1XPosition = 0;
    private double p2XPosition = width - PLAYER_WIDTH;

    public PongController() {
        listener = new SimpleLeapListener();
        leapController = new Controller();


        // GUI

        // create scene
        scene = new AnchorPane();
        field = new Scene(scene, width, height);
        field.setFill(Color.GRAY);

        // add elements
        player1 = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, ELEMENT_COLOR);
        player2 = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, ELEMENT_COLOR);
        ball = new Circle(15, ELEMENT_COLOR);

        startText = new Text(300, 300, "Swipe right to start and move your hand up and down");
        restartText = new Text(width / 2, height / 2, "Swipe right to start again and move your hand up and down");
    }
    
    /**
     *
     * @param player
     * @return
     */
    public boolean ballIsInRange(int player) { //double ballYPos, 
            
        double playerTop = 0;
        double playerBottom = 0;

        switch(player) {
            case 1:
                playerTop = p1TopPosition;
                playerBottom = p1BottomPosition;
                break;
            case 2:
                playerTop = p2TopPosition;
                playerBottom = p2BottomPosition;
                break;
        }
        
//        System.out.println(ballYPos);
//        System.out.println(playerTop);
//        System.out.println(playerBottom);
        

        if (ballYPos >= playerTop && ballYPos <= playerBottom) {
//            System.out.println("ball in range");
            return true;
        } else {
//            System.out.println("ball not in range");
            return false;
        }
    }
    
    public void setP1Position(double p1TopPosition) {
        this.p1TopPosition = p1TopPosition;
        this.p1BottomPosition = p1TopPosition + PLAYER_HEIGHT;
    }
    
    public void setP2Position(double p2TopPosition) {
        this.p2TopPosition = p2TopPosition;
        this.p2BottomPosition = p2TopPosition + PLAYER_HEIGHT;
    }

    @Override
    public void start(Stage stage) throws Exception {

        leapController.addListener(listener);

        player1.setLayoutY(p1TopPosition);
        player1.setLayoutX(p1XPosition);
        scene.getChildren().add(player1);

//        player2.setLayoutX(player2.getX());
//        player2.setLayoutY(player2.getY());
        player2.setLayoutY(p2TopPosition);
        player2.setLayoutX(p2XPosition);
        scene.getChildren().add(player2);

//        ball.setLayoutX(ball.getRadius());
//        ball.setLayoutY(ball.getRadius());
        ball.setLayoutY(ballYPos);
        ball.setLayoutX(ballXPos);
        scene.getChildren().add(ball);

        scene.getChildren().add(startText);
        scene.setOnMouseClicked(e -> listener.setStarted(true));

    

        listener.pointProperty().addListener(new ChangeListener<Point2D>() {
            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (listener.isStarted()) {
                            scene.getChildren().remove(restartText);
                                               
                            // bewegt den Player Balken
                            Point2D leapCapture = scene.sceneToLocal(t1.getX() - field.getX() - field.getWindow().getX(),
                                                                    t1.getY() - field.getY() - field.getWindow().getY());
                            double handYPos = leapCapture.getY();
                            
                            if (handYPos >= 0d && handYPos <= scene.getHeight() - 2d * player1.getY()) {
                                player1.setTranslateY(handYPos - (scene.getHeight()/2 + PLAYER_HEIGHT/2));
                                setP1Position(player1.getTranslateY());
                            }
                            
//                            alt
//                            Point2D d = field.sceneToLocal(t1.getX() - scene.getX() - scene.getWindow().getX(),
//                                    t1.getY() - scene.getY() - scene.getWindow().getY());
//                            double dx = d.getX();
//                            double dy = d.getY();
//                            if (dx >= 0d && dx <= field.getWidth() - 2d * player1.getX()
//                                    && dy >= 0d && dy <= field.getHeight() - 2d * player1.getY()) {
//                                player1.setTranslateY(dy);
//                            }
                            
                            
                            // bewegt den Ball
                            ballXPos += ballXSpeed;
                            ballYPos += ballYSpeed;
                            
                            // bewegt NPC Balken
                            if (ballXPos < width - width / 4) {
                                setP2Position(ballYPos - PLAYER_HEIGHT / 2);
                            } else {
                                setP2Position(ballYPos > p2TopPosition + PLAYER_HEIGHT / 2 ? p2TopPosition += 1 : p2TopPosition - 1);
                            }

                            // lädt Scene neu
                            scene.getChildren().remove(startText);
                            field.setFill(Color.WHITE);

                            ball.setLayoutY(ballYPos);
                            ball.setLayoutX(ballXPos);

                        } else {

                            scene.getChildren().add(restartText);
//                            field.setFill(Color.RED);
                            ballXPos = width / 2;
                            ballYPos = height / 2;
                            ballXSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
                            ballYSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
                        }
                        
                        // Ball versucht zu fliehen
                        if (ballYPos > height || ballYPos < 0) { ballYSpeed *= -1; }
                        
                        
                        // prüft, ob Ball einen Balken trifft
                        if ( ((ballXPos + BALL_R > p2XPosition) && ballIsInRange(2)) || ((ballXPos < p1XPosition + PLAYER_WIDTH) && ballIsInRange(1)) ) {
                            System.out.println(1 * Math.signum(ballYSpeed));
                            ballYSpeed += 1 * Math.signum(ballYSpeed);
                            ballXSpeed += 1 * Math.signum(ballXSpeed);
                            ballXSpeed *= -1;
                            ballYSpeed *= -1;
                        }
                        
                        // wenn Ball nicht gehalten
                        if (ballXPos < p1XPosition - PLAYER_WIDTH) {
//                            scoreP2++;
                            listener.setStarted(false);
                        }
                        if (ballXPos > p2XPosition + PLAYER_WIDTH) {
//                            scoreP1++;
                            listener.setStarted(false);
                        }
                        
                        
//                        if (  ((ballXPos + BALL_R > p2XPosition) && ballYPos >= p2TopPosition && ballYPos <= p2BottomPosition)
//                           || ((ballXPos < playerOneXPos + PLAYER_WIDTH) && ballYPos >= p1TopPosition && ballYPos <= p1BottomPosition)) {

                        

                        player1.setLayoutY(p1TopPosition);
                        player1.setLayoutX(p1XPosition);
                        player1.setFill(Color.CADETBLUE);

                        player2.setLayoutY(p2TopPosition);
                        player2.setLayoutX(p2XPosition);
                        player2.setFill(Color.CADETBLUE);

//                        System.out.println("P1: " + p1TopPosition);

                    } // end run
                });
            } // end changed
        });

        stage.setScene(field);

        stage.show();

    }

    @Override
    public void stop() {
        leapController.removeListener(listener);
//        leapController.removeListener(listener2);

    }
}
