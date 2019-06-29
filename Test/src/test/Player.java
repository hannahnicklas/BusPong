package test;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends Rectangle {

    private static final int[] PLAYER_SIZE = {15, 100};
    private int score;

    public Player(double side) {

        this.setWidth(PLAYER_SIZE[Var.X]);
        this.setHeight(PLAYER_SIZE[Var.Y]);

        this.setStartPosition(side);

        this.setFill(Color.WHITE);
        this.score = 0;
    }

    public void followBall(Ball ball) {
        if (ball.getLayout(Var.X) < (0.93 * Var.minScreenSize[0])) {
            this.setLayoutMiddle(Var.Y, ball.getLayout(Var.Y));

        } else {
            if (ball.getLayout(Var.Y) > this.getLayoutMiddle(Var.Y)) {
                this.setLayout(Var.Y, this.getLayout(Var.Y) + 1);
            } else {
                this.setLayout(Var.Y, this.getLayout(Var.Y) - 1);
            }
        }
    }

    private double calcEdgePosition(double position) {
        double newPos = position - PLAYER_SIZE[0];

        if (newPos < 0) {
            newPos = 0;
        }
        
        return newPos;
    }


    // ********************************************************************************************
    // Getter & Setter

    // Position
    public void setStartPosition(double side) {
        this.setLayout(Var.X, calcEdgePosition(side));
        this.setLayout(Var.Y, Var.minScreenSize[1]/2 - PLAYER_SIZE[1]/2);
    }

    public double getStartPos() {
        System.out.println(this.getLayout(Var.Y));
        return this.getLayout(Var.Y);
    }

    public double getEndPos() {
        System.out.println(this.getLayout(Var.Y) + PLAYER_SIZE[Var.Y]);
        return this.getLayout(Var.Y) + PLAYER_SIZE[Var.Y];
    }


    // Layout
    public void setLayout(int index, double value) {
        switch(index) {
            case 0: this.setLayoutX(value); break;
            case 1: this.setLayoutY(value); break;
        }
    }

    public void setLayoutMiddle(int index, double value) {
        switch(index) {
            case 0: this.setLayoutX(value - PLAYER_SIZE[1]/2); break;
            case 1: this.setLayoutY(value - PLAYER_SIZE[1]/2); break;
        }
    }

    public double getLayout(int index) {
        switch(index) {
            case 0: return this.getLayoutX();
            case 1: return this.getLayoutY();
            default: return -1.0;
        }
    }

    public double getLayoutMiddle(int index) {
        switch(index) {
            case 0: return this.getLayoutX() + PLAYER_SIZE[1]/2;
            case 1: return this.getLayoutY() + PLAYER_SIZE[1]/2;
            default: return -1.0;
        }
    }


    // Size
    public double getSize(int index) {
        switch(index) {
            case 0: return this.getWidth();
            case 1: return this.getHeight();
            default: return -1.0;
        }
    }


    // Score
    public int getScore() {
        return score;
    }

    public void increaseScore() {
        this.score++;
    }

    public void resetScore() {
        this.score = 0;
    }
}