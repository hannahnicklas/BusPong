package test;

import com.leapmotion.leap.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;

//import java.io.*;
//import java.util.Random;
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.beans.value.ChangeListener;
//import javafx.scene.effect.DropShadow;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.shape.SVGPath;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.*;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.scene.text.TextAlignment;
//import javafx.stage.Stage;
//import javafx.util.Duration;


public class SimpleLeapListener extends Listener {

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

    public void swipeGesture(Frame frame) {
        
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

//    @Override
    public void onFrame(Controller controller) {

        Frame frame = controller.frame();
        if (!this.isStarted()) {
            swipeGesture(frame);
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