import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Lawn {
    private ArrayList<Mower> mowers;
    private ArrayList<Square> chargingPads;
    private ArrayList<Square> craters;
    private Square[][] squares;

    public Lawn(ArrayList<Mower> mowers, ArrayList<Square> chargingPads, ArrayList<Square> craters){
       this.mowers = mowers;
       this.chargingPads = chargingPads;
       this.craters = craters;
       squares = Grid.getSquares();
       // Place craters on lawn.
       for (Square crater : craters){
           squares[crater.getY_pos()][crater.getX_pos()] = crater;
           ImageIcon icon = new ImageIcon("crater.jpg");
           squares[crater.getY_pos()][crater.getX_pos()].setSquareIcon(icon);
           squares[crater.getY_pos()][crater.getX_pos()].setDescription("crater");
       }
       // Place chargingPads on lawn.
       for (Square chargingPad : chargingPads) {
           squares[chargingPad.getY_pos()][chargingPad.getX_pos()] = chargingPad;
           BufferedImage img = null;
           try {
               img = ImageIO.read(new File("chargePad.jpg"));
           } catch (IOException e) {
               e.printStackTrace();
           }


           ImageIcon icon = new ImageIcon("chargePad.jpg");
           squares[chargingPad.getY_pos()][chargingPad.getX_pos()].setSquareIcon(icon);
           squares[chargingPad.getY_pos()][chargingPad.getX_pos()].setDescription("energy");
       }
    }

    // Determine if all the grass squares have been cut.
    public boolean isNotFinished(){
        for (int row=0; row< Grid.getROWS(); row++) {
            for (int col=0; col< Grid.getCOLS(); col++){
                if (squares[row][col].getMarker().equals(Grid.getGrassMark())){
                    return true;
                }
            }
        }
        for (Mower mower : mowers){
            mower.setState("OFF");
        }
        return false;
    }

    public int numberOfGrassSquares(){
        return Grid.getROWS()*Grid.getCOLS() - craters.size();
    }

    public int grassLeft(){
        int count = 0;
        for (int row=0; row< Grid.getROWS(); row++) {
            for (int col=0; col< Grid.getCOLS(); col++){
                if (squares[row][col].getMarker().equals(Grid.getGrassMark())){
                    count++;
                }
            }
        }
        return count;
     }

     public double percentCut() {
        double totalGrassSquares = Grid.getROWS()*Grid.getCOLS() - craters.size();
        return 100 - 100*(grassLeft()/totalGrassSquares);
     }

    public int getTotalSquares() {
        return Grid.getROWS()*Grid.getCOLS();
    }


    // Setters and Getters.
    public ArrayList<Mower> getMowers() {
        return mowers;
    }

    public ArrayList<Square> getChargingPads() {
        return chargingPads;
    }

    public ArrayList<Square> getCraters() {
        return craters;
    }
}