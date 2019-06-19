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
//import com.leapmotion.leap.Gesture.State;
public class SimpleLeapListener extends Listener {

    private final ObjectProperty<Point2D> point = new SimpleObjectProperty<>();

    // swipeGesture
    private float startPos;
    private float lastPos;

    private boolean started = false;
    private boolean isFirstStart = true;
    private final int SWIPE_DISTANCE = 70;
    
    private boolean gestureStarted = false;
    private double gestureProgress = 0;

    public SimpleLeapListener() {
        startPos = 0f;
        lastPos = 0f;
    }

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    //Swipe to start
    public void swipeGesture(Frame frame) {

        if (frame.hands().count() == 1 && !started) {

            float velocity = Math.abs(frame.hands().get(0).palmVelocity().getX());
            float xPos = frame.hands().get(0).palmPosition().getX();

            if (velocity > 20 && velocity < 500) {

                if (startPos == 0) {
                    startPos = xPos;
                    lastPos = xPos;
                    
                    gestureStarted = true;

                } else if (xPos > lastPos) {
                    lastPos = xPos;
                    gestureStarted = true;
                    
                    gestureProgress = (lastPos - startPos) / SWIPE_DISTANCE;
                    System.out.println(this.gestureProgress);
                    
                    if ((lastPos - startPos) >= SWIPE_DISTANCE) {
                        this.setStarted(true);
                    }
                }

            } else {
                this.gestureStarted = false;
                startPos = 0;
                lastPos = 0;
            }
        }
    }

    //Restart 
    public void circleGesture(Frame frame) {
        //Circle Geste         
        //Liste von Gesten, Gestenobjet aus Gestenliste  
        GestureList gestures = frame.gestures();
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);

            switch (gesture.type()) {
                // Nur Circle Gesture wird behandelt 
                case TYPE_CIRCLE:

                    CircleGesture circle = new CircleGesture(gesture);
                    float turns = circle.progress();

                    //Returns the normal vector for the circle being traced. (aus Leap API)   
                    String clockwiseness;
                    boolean clockwise = false;
                    if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 4) {
                        // Winkel ist kleiner als 90 Grad 
                        clockwiseness = "clockwise";
                        clockwise = true;
                    } else {
                        clockwiseness = "counter-clockwise";
                        clockwise = false;
                    }

                    FingerList fingers = frame.fingers();
                    for (int j = 0; j < fingers.count(); j++) {
                        Finger finger = fingers.get(j);

                        switch (finger.type()) {
                            case TYPE_INDEX:

                                if (!this.isStarted()) {
                                    if (clockwise && finger.isExtended()) {
                                        if (turns >= 0 && turns <= 1.5) {
                                            System.out.println("Game restarted: " + finger.type());
                                            this.setStarted(true);
                                        }
                                    }
                                }
                                break;

                            case TYPE_MIDDLE:
                                // System.out.println("Nicht starten");
                                break;
                        }
                    }
                    break;
            }
        }
    }

    public void onConnect(Controller controller) {
        System.out.println("Controller connected");
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
    }

//    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();

        if (!this.isStarted() && this.isFirstStart) {
            swipeGesture(frame);
            gestureStarted = true; 
        }
        if (!this.isStarted() && !this.isFirstStart) {
            circleGesture(frame);

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
    
    public boolean gestureDetected() {
        return this.gestureStarted;
    }
    
    public double gestureProgress() {
        return this.gestureProgress;
    }

    public void setStarted(boolean started) {
        this.gestureStarted = false;
        this.isFirstStart = false;
        this.started = started;
        this.startPos = 0;
        this.lastPos = 0;
    }

}
