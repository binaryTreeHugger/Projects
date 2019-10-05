import javax.swing.*;
import java.awt.*;

public class Square {
    private final int x_pos;
    private final int y_pos;
    private String marker;
    private String description;
    private int visitCount = 0;
    private Color squareColor = Color.green;
    private ImageIcon squareIcon;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Square(int x_pos, int y_pos, String marker){
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.marker = marker;
        ImageIcon icon = new ImageIcon("grass.jpg");
        setSquareIcon(icon);
        description = "grass";
        description = Grid.getMarkerToDescription().get(marker);
    }

    public void displayInfo(){
        System.out.println("[" + x_pos + "," + y_pos + "," + description + "]");
    }

    // Setters and Getters.
    public int getX_pos() {
        return x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public void setSquareColor(Color color) {
        squareColor = color;
    }

    public Color getSquareColor() {
        return squareColor;
    }

    public void setSquareIcon(ImageIcon icon) {
        int max = (Grid.getCOLS() + Grid.getROWS())/2 ;
        ImageIcon image = new ImageIcon(icon.getImage().getScaledInstance(650/max, 650/max, Image.SCALE_DEFAULT));
        squareIcon = image;
    }

    public ImageIcon getSquareIcon() {
        return squareIcon;
    }

}
