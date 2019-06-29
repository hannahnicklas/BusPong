package test;

public class Var {

    static String orientation = "vertical";
    static int X = 0;
    static int Y = 1;
    static final int[] minScreenSize = {800, 600};


    public static void setOrientation(String orientation) {

        Var.orientation = orientation;

        switch (orientation) {
            case "horizontal":
                Var.X = 1;
                Var.Y = 0;
                break;
                
            default:
                Var.X = 0;
                Var.Y = 1;
                break;
        }
    }
}