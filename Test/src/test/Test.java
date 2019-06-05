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

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
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

public class Test extends Application {

    private final SimpleLeapListener listener = new SimpleLeapListener();
    private final Controller leapController = new Controller();

    private final AnchorPane root = new AnchorPane();
    private final Rectangle player1 = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, Color.CADETBLUE);
    private final Rectangle player2 = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT);

    private final Circle ball = new Circle(15, Color.CORAL);

    private Text startText = new Text(300, 300, "Click anywhere and then move your hand in to start");
    Text restart = new Text(width / 2, height / 2, "Click and then move your hand in to start again");

    private static final int width = 800;
    private static final int height = 600;
    private static final int PLAYER_HEIGHT = 100;
    private static final int PLAYER_WIDTH = 15;
    private static final double BALL_R = 15;
    private int ballYSpeed = 1;
    private int ballXSpeed = 1;
    private double playerOneYPos = height / 2;
    private double playerTwoYPos = height / 2;
    private double ballXPos = width / 2;
    private double ballYPos = height / 2;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private boolean gameStarted;
    private int playerOneXPos = 0;
    private double playerTwoXPos = width - PLAYER_WIDTH;
    
   

    @Override
    public void start(Stage stage) throws Exception {
    
        leapController.addListener(listener);

        player1.setLayoutY(playerOneYPos);
        player1.setLayoutX(playerOneXPos);
        root.getChildren().add(player1);

//        player2.setLayoutX(player2.getX());
//        player2.setLayoutY(player2.getY());
        player2.setLayoutY(playerTwoYPos);
        player2.setLayoutX(playerTwoXPos);
        root.getChildren().add(player2);

//        ball.setLayoutX(ball.getRadius());
//        ball.setLayoutY(ball.getRadius());
        ball.setLayoutY(ballYPos);
        ball.setLayoutX(ballXPos);
        root.getChildren().add(ball);

        root.getChildren().add(startText);

        final Scene scene = new Scene(root, width, height);
        scene.setFill(Color.GRAY);
        scene.setOnMouseClicked(e -> gameStarted = true);
         
   
     

        listener.pointProperty().addListener(new ChangeListener<Point2D>() {
            @Override
            public void changed(ObservableValue ov, Point2D t, final Point2D t1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (gameStarted) {
                            root.getChildren().remove(restart);
                            //Bewegt den Curser 
                            Point2D d = root.sceneToLocal(t1.getX() - scene.getX() - scene.getWindow().getX(),
                                    t1.getY() - scene.getY() - scene.getWindow().getY());
                            double dx = d.getX();
                            double dy = d.getY();
                            if (dx >= 0d && dx <= root.getWidth() - 2d * player1.getX()
                                    && dy >= 0d && dy <= root.getHeight() - 2d * player1.getY()) {
                                player1.setTranslateY(dy);
                            }
                            ballXPos += ballXSpeed;
                            ballYPos += ballYSpeed;
                            if (ballXPos < width - width / 4) {
                                playerTwoYPos = ballYPos - PLAYER_HEIGHT / 2;
                            } else {
                                playerTwoYPos = ballYPos > playerTwoYPos + PLAYER_HEIGHT / 2 ? playerTwoYPos += 1 : playerTwoYPos - 1;
                            }

                            root.getChildren().remove(startText);
                            scene.setFill(Color.WHITE);

                            ball.setLayoutY(ballYPos);
                            ball.setLayoutX(ballXPos);

                        } else {

                            root.getChildren().add(restart);
                            scene.setFill(Color.RED);
                            ballXPos = width / 2;
                            ballYPos = height / 2;
                            ballXSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
                            ballYSpeed = new Random().nextInt(2) == 0 ? 1 : -1;
                        
                        }
                        if (ballYPos > height || ballYPos < 0) {
                            ballYSpeed *= -1;
                        }
                        if (ballXPos < playerOneXPos - PLAYER_WIDTH) {
//                            scoreP2++;
                            gameStarted = false;
                        }
                        if (ballXPos > playerTwoXPos + PLAYER_WIDTH) {
//                            scoreP1++;
                            gameStarted = false;
                        }
                        if (((ballXPos + BALL_R > playerTwoXPos) && ballYPos >= playerTwoYPos && ballYPos <= playerTwoYPos + PLAYER_HEIGHT)
                                || ((ballXPos < playerOneXPos + PLAYER_WIDTH) && ballYPos >= playerOneYPos && ballYPos <= playerOneYPos + PLAYER_HEIGHT)) {
                            ballYSpeed += 1 * Math.signum(ballYSpeed);
                            ballXSpeed += 1 * Math.signum(ballXSpeed);
                            ballXSpeed *= -1;
                            ballYSpeed *= -1;
                        }

                        player1.setLayoutY(playerOneYPos-300);
                        player1.setLayoutX(playerOneXPos);
                        player1.setFill(Color.CADETBLUE);

                        player2.setLayoutY(playerTwoYPos);
                        player2.setLayoutX(playerTwoXPos);
                        player2.setFill(Color.CADETBLUE);

                        System.out.println(playerOneYPos + "P2");

                    }

                }
                );

            }
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
