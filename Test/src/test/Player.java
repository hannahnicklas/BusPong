package test;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends Rectangle {

    private static final int PLAYER_HEIGHT = 100;
    private static final int PLAYER_WIDTH = 15;

    private double topXPos;
    private double middleXPos;
    
    private double topYPos;
    private double middleYPos;
    private double bottomYPos;

    private int score;

    public Player(int startXPos, int startYPos) {
        this.setXPosition(startXPos);
        this.setYPosition(startYPos);

        this.setWidth(PLAYER_WIDTH);
        this.setHeight(PLAYER_HEIGHT);

        this.setFill(Color.WHITE);

        this.score = 0;
    }


    // -------------------------------------------------
    // eigene Setter

    public void setXPosition(double middleXPos) {
        if(middleXPos < PLAYER_WIDTH/2) {
            this.middleXPos = PLAYER_WIDTH/2;
        } else {
            this.middleXPos = middleXPos - PLAYER_WIDTH/2;
        }

        this.setTopXPosition();
    }

    public double getXPosition() {
        return this.middleXPos;
    }

    private void setTopXPosition() {
        this.topXPos = middleXPos - PLAYER_WIDTH/2;
        this.setLayoutX(this.topXPos);
    }

    public void setYPosition(double middleYPos) {
        this.middleYPos = middleYPos;
        this.setTopYPosition();
        this.setBottomYPosition();
    }

    public double getYPosition() {
        return this.middleYPos;
    }

    private void setTopYPosition() {
        this.topYPos = middleYPos - PLAYER_HEIGHT/2;
        this.setLayoutY(this.topYPos);
    }

    private void setBottomYPosition() {
        this.bottomYPos = middleYPos + PLAYER_HEIGHT/2;
    }


    // -------------------------------------------------
    // generic Getter and Setter

    public static int getPlayerHeight() {
        return PLAYER_HEIGHT;
    }

    public static int getPlayerWidth() {
        return PLAYER_WIDTH;
    }

    // Positions
    public double getTopXPos() {
        return topXPos;
    }

    public double getTopYPos() {
        return topYPos;
    }

    public double getBottomYPos() {
        return bottomYPos;
    }

    // Score
    public int getScore() {
        return score;
    }

    public void increaseScore() {
        this.score++;
    }
}