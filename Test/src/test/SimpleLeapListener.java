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
    private int xCount = 0;
    private final int MINCOUNT = 5;
    private float startPos;
    private float lastPos;
    private boolean started = false;

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

            if (velocity > 50 && velocity < 500) {

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

                    //Winkel der mit Hand gezeichnet wird 
//                    double sweptAngle = 0;
//                    if (circle.state() != State.STATE_START) {
//                        //greift auf vorherige Kreis Geste zurück 
//                        CircleGesture previous = new CircleGesture(controller.frame(1).gesture(circle.id()));
//                        sweptAngle = (circle.progress() - previous.progress()) * 2 * Math.PI;
//                    }
                    FingerList fingers = frame.fingers();
                    for (int j = 0; j < fingers.count(); j++) {
                        Finger finger = fingers.get(j);

                        switch (finger.type()) {
                            case TYPE_INDEX:

                                if (!this.isStarted()) {
                                    if (clockwise && finger.isExtended()) {
                                        if (turns >= 0 && turns <= 1.5) {
                                            System.out.println("Game restarted: " + finger.type());
                                            started = true;
                                        }
                                    }
                                }
                                break;

                            case TYPE_MIDDLE:
                                System.out.println("Nicht starten");
                                break;
                        }

//                    // solange die selbe ID bis andere Hand erkannt wird 
//                    System.out.println("Circle ID:" + circle.id()
//                                            + "State:" + circle.state()
//                                              // Anzahl wie oft Kreis nacheinander gezeichnet wurde 
//                                            + "Progress: " + turns
//                                            + "Radius: " + circle.radius()
//                                            + "Angle: " + Math.toDegrees(sweptAngle)
//                                            + "" + clockwiseness
//                                            + "Type " + finger.type());
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

        circleGesture(frame);
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
