package test;

import com.leapmotion.leap.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.media.AudioClip;

public class SimpleLeapListener extends Listener {

    private final ObjectProperty<Point2D> point = new SimpleObjectProperty<>();

    // swipeGesture
    private float startPos;
    private float lastPos;

    private final int SWIPE_DISTANCE = 100;
    private final int SWIPE_MIN_VELOCITY = 50;
    private final int SWIPE_MAX_VELOCITY = 1000;

    private boolean grabGestureCompleted = false;
    boolean added = false;
    boolean grabStarted = false;

    private boolean swipeGestureStarted = false;
    private double swipeGestureProgress = 0;

    public SimpleLeapListener() {
        startPos = 0f;
        lastPos = 0f;
    }

    public ObservableValue<Point2D> pointProperty() {
        return point;
    }

    //Swipe to start
    public void swipeGesture(Frame frame) {
        
        if (frame.hands().count() == 1 && !Var.isGameStarted()) {
            
            float velocity = Math.abs(frame.hands().get(0).palmVelocity().getX());
            float xPos = frame.hands().get(0).palmPosition().getX();

            
            if (velocity > SWIPE_MIN_VELOCITY && velocity < SWIPE_MAX_VELOCITY) {
                if (startPos == 0) {
                    startPos = xPos;
                    lastPos = xPos;
                    
                    swipeGestureStarted = true;
                    
                } else if (xPos > lastPos) {
                    lastPos = xPos;
                    swipeGestureStarted = true;
                    
                    swipeGestureProgress = (lastPos - startPos) / SWIPE_DISTANCE;
                    System.out.println("Progress: " + swipeGestureProgress);

                    if ((lastPos - startPos) >= SWIPE_DISTANCE) {
                        this.setStarted(true);
                        System.out.println("Swipe Gesture detected");
                    }
                }
                
            } else {
                this.setStarted(false);
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

                                if (!Var.isGameStarted() && clockwise && finger.isExtended() && turns >= 0 && turns <= 1.5) {
                                    this.setStarted(true);
                                    System.out.println("Circle Gesture detected");
                                }
                                break;

                            case TYPE_MIDDLE:
                                break;
                        }
                    }
                    break;
            }
        }
    }

    //Change Player
    public void grabGesture(Frame frame) {
        HandList hands = frame.hands();
        Hand firstHand = hands.get(0);
        FingerList fingers = firstHand.fingers();

        float fistProgress = 0;

        float fistStart = firstHand.grabStrength();

        if (frame.hands().count() == 1) {
            fistProgress = firstHand.grabStrength();

            if (fistStart == 0) {
                grabStarted = true;
            }

            if (grabStarted && !grabGestureCompleted && fistProgress == 1.0) {
                this.setGrabGestureCompleted(true);
                this.setStarted(true);
                System.out.println("Grab Gesture detected");
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

        if (!Var.isGameStarted() && Var.isFirstStart()) {
            swipeGesture(frame);
            swipeGestureStarted = true;
            
        } else if (!Var.isGameStarted() && !Var.isFirstStart()) {
            grabGesture(frame);
            circleGesture(frame);

        } else if (!frame.hands().isEmpty()) {
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

    public boolean isSwipeGestureDetected() {
        return this.swipeGestureStarted;
    }

    public double getSwipeGestureProgress() {
        return this.swipeGestureProgress;
    }

    public void setStarted(boolean started) {
        Var.setGameStarted(started);

        if (started) {
            Var.setFirstStart(false);
            this.swipeGestureStarted = false;
            this.startPos = 0;
            this.lastPos = 0;
        }
    }

    public boolean isGrabGestureCompleted() {
        return grabGestureCompleted;
    }

    public void setGrabGestureCompleted(boolean grabGestureCompleted) {
        this.grabGestureCompleted = grabGestureCompleted;
    }

    public void sleep(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ex) {}
    }
}
