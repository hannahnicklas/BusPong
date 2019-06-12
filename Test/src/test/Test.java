/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.leapmotion.leap.*;
import java.io.*;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.SVGPath;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author hannah
 */
class SimpleLeapListener extends Listener {

    private final ObjectProperty<Point2D> point = new SimpleObjectProperty<>();
    private int xCount = 0;
    private final int MINCOUNT = 5;
    private float startPos;
    private float lastPos;
    private boolean started = false;
    
    public SimpleLeapListener () {
        startPos = 0f;
        lastPos = 0f;
    }

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    public void swipe(Frame frame) {
        
        if (frame.hands().count() == 1 && !started) {
            float velocity = Math.abs(frame.hands().get(0).palmVelocity().getX());
            float xPos = frame.hands().get(0).palmPosition().getX();


            if ( velocity > 50 && velocity < 500 ) {
                
                if (startPos == 0) {
                    startPos = xPos;
                    lastPos = xPos;

                    System.out.println("startPos: " + startPos);
                    
                } else if (xPos > lastPos) {
                    lastPos = xPos;
                    System.out.println("lastPos: " + lastPos);
                    System.out.println("diff: " + (lastPos - startPos));
                    
                    if ((lastPos - startPos) >= 50) {
                        started = true;
                    }
                }

            } else {
                System.out.println("too slow");

                startPos = 0;
                lastPos = 0;
            }
        }
    }

    @Override
    public void onFrame(Controller controller) {

        Frame frame = controller.frame();
        if (!this.isStarted()) {
            swipe(frame);
        } else {
            if (!frame.hands().isEmpty()) {
                Screen screen = controller.locatedScreens().get(0);
                if (screen != null && screen.isValid()) {
                    Hand hand = frame.hands().get(0);

                    if (hand.isValid()) {
                        Vector intersect = screen.intersect(hand.palmPosition(), hand.direction(), true);
                        point.setValue(new Point2D(screen.widthPixels() * Math.min(1d, Math.max(0d, intersect.getX())),
                                screen.heightPixels() * Math.min(1d, Math.max(0d, (1d - intersect.getY())))));
                    }
                }
            }
        }
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void setStarted(boolean started) {
        this.started = started;
    }

}

public class Test extends Application {

    private final SimpleLeapListener listener = new SimpleLeapListener();
    private final Controller leapController = new Controller();

    private final AnchorPane root = new AnchorPane();
    private final Rectangle player1 = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, Color.CADETBLUE);
    private final Rectangle player2 = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT);

    private final Circle ball = new Circle(15, Color.CORAL);

    private Text startText = new Text(300, 300, "Swipe right to start and move your hand up and down");
    Text restart = new Text(width / 2, height / 2, "Swipe right to start again and move your hand up and down");

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
//    private boolean gameStarted;
    private int p1XPosition = 0;
    private double p2XPosition = width - PLAYER_WIDTH;
    
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
        root.getChildren().add(player1);

//        player2.setLayoutX(player2.getX());
//        player2.setLayoutY(player2.getY());
        player2.setLayoutY(p2TopPosition);
        player2.setLayoutX(p2XPosition);
        root.getChildren().add(player2);

//        ball.setLayoutX(ball.getRadius());
//        ball.setLayoutY(ball.getRadius());
        ball.setLayoutY(ballYPos);
        ball.setLayoutX(ballXPos);
        root.getChildren().add(ball);

        root.getChildren().add(startText);

        final Scene scene = new Scene(root, width, height);
        scene.setFill(Color.GRAY);
//        scene.setOnMouseClicked(e -> gameStarted = true);

    

        listener.pointProperty().addListener(new ChangeListener<Point2D>() {
            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (listener.isStarted()) {
                            root.getChildren().remove(restart);
                                               
                            // bewegt den Player Balken 
                            Point2D leapCapture = root.sceneToLocal(t1.getX() - scene.getX() - scene.getWindow().getX(),
                                                                    t1.getY() - scene.getY() - scene.getWindow().getY());
                            double handYPos = leapCapture.getY();
                            
                            if (handYPos >= 0d && handYPos <= root.getHeight() - 2d * player1.getY()) {
                                player1.setTranslateY(handYPos - (root.getHeight()/2 + PLAYER_HEIGHT/2));
                                setP1Position(player1.getTranslateY());
                            }
                            
//                            alt
//                            Point2D d = root.sceneToLocal(t1.getX() - scene.getX() - scene.getWindow().getX(),
//                                    t1.getY() - scene.getY() - scene.getWindow().getY());
//                            double dx = d.getX();
//                            double dy = d.getY();
//                            if (dx >= 0d && dx <= root.getWidth() - 2d * player1.getX()
//                                    && dy >= 0d && dy <= root.getHeight() - 2d * player1.getY()) {
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
                            root.getChildren().remove(startText);
                            scene.setFill(Color.WHITE);

                            ball.setLayoutY(ballYPos);
                            ball.setLayoutX(ballXPos);

                        } else {

                            root.getChildren().add(restart);
//                            scene.setFill(Color.RED);
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

        stage.setScene(scene);

        stage.show();

    }

    @Override
    public void stop() {
        leapController.removeListener(listener);
//        leapController.removeListener(listener2);

    }
}
