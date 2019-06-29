package test;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.media.AudioClip;

public class Ball extends Circle {

    private static final double BALL_RADIUS = 15;
    private static final int INIT_BALL_SPEED = 2;
    private static final int MAX_BALL_SPEED = 50;
    
    private int[] speed;


    public Ball() {
        speed = new int[2];
       
        this.setRadius(BALL_RADIUS);
        this.setFill(Color.ALICEBLUE);

        this.resetSpeed();
        this.resetPosition();
    }

    public boolean move(Player p1, Player p2) {
        this.autosetPosition();

        // Ball trifft oben oder unten
        if (this.getPosition(Var.Y) > Var.minScreenSize[1] || this.getPosition(Var.Y) < 0) {
            speed[Var.Y] *= -1;
            playAudio("hitwall");
        }

        // Ball triff Spieler
        if ( (this.getLayout(Var.X) < p1.getLayout(Var.X) + p1.getSize(Var.X) && isInRangeOf(p1)) || 
             (this.getLayout(Var.X) > p2.getLayout(Var.X) && isInRangeOf(p2)) ) {

            increaseSpeed();
            playAudio("hit");

            return true;

        // P1 verfehlt Ball
        } else if (this.getLayout(Var.X) < 0 + BALL_RADIUS) {
            p2.increaseScore();
            playAudio("lost");
            return false;
            
        // P2 verfehlt Ball
        } else if (this.getLayout(Var.X) > Var.minScreenSize[0] - BALL_RADIUS) {
            p1.increaseScore();
            playAudio("lost");
            return false;

        // Ball im Nichts
        } else {
            return true;
        }
    }

    public boolean isInRangeOf(Player player) {
        if (this.getPosition(Var.Y) >= player.getStartPos() && this.getPosition(Var.Y) <= player.getEndPos()) {
            System.out.println("is in range of player");
            return true;
        } else {
            System.out.println("is not in range of player");
            return false;
        }
    }

    public void increaseSpeed() {
        for (int i=0; i < speed.length; i++) {

            int oldSpeed = speed[i];
            
            double signum = Math.signum(oldSpeed); // dreht Bewegung um
            double ln = 0.5 * Math.log(Math.abs(oldSpeed)) + 3;
            double random = 1 - (1 * (Math.random() - 0.5));

            speed[i] = (int) (random * signum * ln);
    
            if (Math.abs(speed[i]) < INIT_BALL_SPEED) {
                speed[i] =  (int) Math.signum(speed[i]) * INIT_BALL_SPEED;
    
            } else if (Math.abs(speed[i]) > MAX_BALL_SPEED) {
                speed[i] = (int) Math.signum(speed[i]) * MAX_BALL_SPEED;
    
            } else {
                speed[i] = speed[i] * -1;
            }
        }
    }

    public void playAudio(String title) {
        AudioClip note = new AudioClip(this.getClass().getResource(title + ".mp3").toString());
        note.play();
    }

    public void resetSpeed() {
        speed[Var.X] = INIT_BALL_SPEED;
        speed[Var.Y] = INIT_BALL_SPEED;
    }

    public void resetPosition() {
        this.setPosition(Var.X, Var.minScreenSize[Var.X] / 2);
        this.setPosition(Var.Y, Var.minScreenSize[Var.Y] / 2);
    }


    // Getter & Setter

    public int getSpeed(int index) {
        return speed[index];
    }

    public void setSpeed(int index, int speed) {
        this.speed[index] = speed;
    }

    public double getPosition(int index) {
        if(index == Var.X) {
            return this.getLayoutX();
        } else {
            return this.getLayoutY();
        }
    }

    public void setPosition(int index, double position) {
        if(index == Var.X) {
            this.setLayoutX(position);
        } else {
            this.setLayoutY(position);
        }
    }

    public void autosetPosition() {
        this.setLayoutX(this.getPosition(Var.X) + speed[Var.X]);
        this.setLayoutY(this.getPosition(Var.Y) + speed[Var.Y]);
    }


    // Layout
    public void setLayout(int index, double value) {
        switch(index) {
            case 0: this.setLayoutX(value); break;
            case 1: this.setLayoutY(value); break;
        }
    }

    public double getLayout(int index) {
        switch(index) {
            case 0: return this.getLayoutX();
            case 1: return this.getLayoutY();
            default: return -1.0;
        }
    }
}