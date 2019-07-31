import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Mower {
    public ArrayList<Square> discoveredSquares = new ArrayList<>();
    public int relativeX = 0;
    public int relativeY = 0;
    public boolean hitLastTarget = true;
    public boolean stuckInPlace = false;
    public Square nextTarget = new Square(0,0,"energy");
    public int stuckCount = 0;
    public String mowerOutput;
    public static int activeMower;
    public static boolean flashON = true;

    private String mowerDirection;
    public ImageIcon mowerIcon;
    private String mowerMark;
    private String state = "ON";
    private boolean isCharging = false;
    private boolean isMovingForward = false;
    private int mowerNumber;
    private int stepCount = 0;
    private int x_pos;
    private int y_pos;
    private int stallCount = 0;
    private int chargeLevel = FileReader.chargeLevel;
    private int individualTurns = 0;
    final static int MAX_STALLS = FileReader.collisionStalls;
    static int totalTurns = 0;


    public Mower(int mowerNumber, int x_pos, int y_pos, String mowerDirection){
        this.mowerNumber = mowerNumber;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.mowerDirection = mowerDirection;
        mowerMark = mowerNumber % 10 + Grid.getDirectionMap().get(mowerDirection);
    }

    // Return the eight squares in the mower's vicinity, in clockwise order, beginning with North.
    public String[] scan(){
        int ROWS = Grid.getROWS();
        int COLS = Grid.getCOLS();
        Square[][] squares = Grid.getSquares();
        Map<Square,ImageIcon> iconMap = new HashMap<>();
        ImageIcon scannedSquare;
        scannedSquare = new ImageIcon("scannedSquare.jpg");

        String[] perim = new String[8];
        //North
        if (y_pos - 1 >= 0){
            perim[0] = Grid.getSquareMarker(x_pos,y_pos-1);
            ImageIcon squareIcon = squares[y_pos-1][x_pos].getSquareIcon();
            iconMap.put(squares[y_pos-1][x_pos], squareIcon);
        }
        else{
            perim[0] = "fence";
        }
        //Northeast
        if (y_pos -1 >= 0 && x_pos + 1 < COLS){
            perim[1] = Grid.getSquareMarker(x_pos+1,y_pos-1);
            ImageIcon squareIcon = squares[y_pos-1][x_pos+1].getSquareIcon();
            iconMap.put(squares[y_pos-1][x_pos+1], squareIcon);
        }
        else{
            perim[1] = "fence";
        }
        //East
        if (x_pos + 1 < COLS){
            perim[2] = Grid.getSquareMarker(x_pos+1,y_pos);
            ImageIcon squareIcon = squares[y_pos][x_pos+1].getSquareIcon();
            iconMap.put(squares[y_pos][x_pos+1], squareIcon);
        }
        else {
            perim[2] = "fence";
        }
        //Southeast
        if (y_pos +1 < ROWS && x_pos + 1 < COLS){
            perim[3] = Grid.getSquareMarker(x_pos+1,y_pos+1);
            ImageIcon squareIcon = squares[y_pos+1][x_pos+1].getSquareIcon();
            iconMap.put(squares[y_pos+1][x_pos+1], squareIcon);
        }
        else {
            perim[3] = "fence";
        }
        //South
        if (y_pos + 1 < ROWS){
            perim[4] = Grid.getSquareMarker(x_pos,y_pos+1);
            ImageIcon squareIcon = squares[y_pos+1][x_pos].getSquareIcon();
            iconMap.put(squares[y_pos+1][x_pos], squareIcon);
        }
        else {
            perim[4] = "fence";
        }
        //Southwest
        if (y_pos + 1 < ROWS && x_pos - 1 >= 0){
            perim[5] = Grid.getSquareMarker(x_pos-1,y_pos+1);
            ImageIcon squareIcon = squares[y_pos+1][x_pos-1].getSquareIcon();
            iconMap.put(squares[y_pos+1][x_pos-1], squareIcon);
        }
        else {
            perim[5] = "fence";
        }
        //West
        if (x_pos - 1 >= 0){
            perim[6] = Grid.getSquareMarker(x_pos-1,y_pos);
            ImageIcon squareIcon = squares[y_pos][x_pos-1].getSquareIcon();
            iconMap.put(squares[y_pos][x_pos-1], squareIcon);
        }
        else {
            perim[6] = "fence";
        }
        //Northwest
        if (y_pos -1 >= 0 && x_pos - 1 >= 0){
            perim[7] = Grid.getSquareMarker(x_pos-1,y_pos-1);
            ImageIcon squareIcon = squares[y_pos-1][x_pos-1].getSquareIcon();
            iconMap.put(squares[y_pos-1][x_pos-1], squareIcon);
        }
        else {
            perim[7] = "fence";
        }

        // highlight scanned squares
        if(flashON) {
            for (Square s : iconMap.keySet()) {
                s.setSquareIcon(scannedSquare);
            }

            for (int row = 0; row < Grid.getROWS(); row++){
                for (int col = 0; col < Grid.getCOLS(); col++){
                    OsMowSisViewer.getLabels()[row][col].setIcon(squares[row][col].getSquareIcon());
                }
            }
            OsMowSisViewer.pause(OsMowSisViewer.pauseTime/25);
            for (Square s : iconMap.keySet()) {
                s.setSquareIcon(iconMap.get(s));
            }
        }
        addScannedSquares(perim);
        printScan(perim);
        Mower.totalTurns++;
        individualTurns++;
        decrementChargeLevel();
        return perim;
    }

    public void addScannedSquares(String[] perim){
        //center square
        Square c = new Square(relativeX,relativeY,Grid.getEmptyMark());
        c.setDescription("empty");
        ArrayList<Square> toBeRemoved = new ArrayList<>();
        Map<String, String> markerMap = Grid.getDescriptionToMarker();
        for (Square square : discoveredSquares){
            if(square.getX_pos() == c.getX_pos() && square.getY_pos() == c.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(c);


        //north
        Square n = new Square(relativeX,relativeY+1, markerMap.get(perim[0]));
        toBeRemoved = new ArrayList<>();
        for (Square square : discoveredSquares){
            if(square.getX_pos() == n.getX_pos() && square.getY_pos() == n.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(n);

        //northeast
        Square ne = new Square(relativeX+1,relativeY+1,markerMap.get(perim[1]));

        for (Square square : discoveredSquares){
            if(square.getX_pos() == ne.getX_pos() && square.getY_pos() == ne.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(ne);

        //east
        Square e = new Square(relativeX+1,relativeY,markerMap.get(perim[2]));
        for (Square square : discoveredSquares){
            if(square.getX_pos() == e.getX_pos() && square.getY_pos() == e.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(e);

        //southeast
        Square se = new Square(relativeX+1,relativeY-1,markerMap.get(perim[3]));
        for (Square square : discoveredSquares){
            if(square.getX_pos() == se.getX_pos() && square.getY_pos() == se.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(se);

        //south
        Square s = new Square(relativeX,relativeY-1,markerMap.get(perim[4]));
        for (Square square : discoveredSquares){
            if(square.getX_pos() == s.getX_pos() && square.getY_pos() == s.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(s);

        //southwest
        Square sw = new Square(relativeX-1,relativeY-1,markerMap.get(perim[5]));
        for (Square square : discoveredSquares){
            if(square.getX_pos() == sw.getX_pos() && square.getY_pos() == sw.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(sw);

        //west
        Square w = new Square(relativeX-1,relativeY,markerMap.get(perim[6]));
        for (Square square : discoveredSquares){
            if(square.getX_pos() == w.getX_pos() && square.getY_pos() == w.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(w);

        //northwest
        Square nw = new Square(relativeX-1,relativeY+1,markerMap.get(perim[7]));
        for (Square square : discoveredSquares){
            if(square.getX_pos() == nw.getX_pos() && square.getY_pos() == nw.getY_pos()){
                toBeRemoved.add(square);
            }
        }
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        discoveredSquares.add(nw);
    }

    // Print scan results.
    public void printScan(String[] perim){
        System.out.println("mower_" + mowerNumber + " scan");
        mowerOutput = "scan\n";
        for (int i = 0; i < perim.length; i++) {
            String s = perim[i];
            if (i == 7) {
                mowerOutput += s + "";
            } else {
                mowerOutput += s + ",";
            }
        }
        String icon;
        for (int i=0; i< perim.length; i++) {
            if(perim[i].equals("mower_0")){
                icon = "mower_10";
            } else{
                icon = perim[i];
            }

            if (i == perim.length - 1) {
                System.out.println(icon);
            } else {
                System.out.print(icon + ",");
            }
        }
    }

    // Change the mower's direction.
    public void changeDirection(String direction){
        if (mowerDirection.equals(direction)){
            return;
        }
        isMovingForward = false;
        mowerDirection = direction;
        mowerMark = mowerNumber % 10 + Grid.getDirectionMap().get(mowerDirection);
        totalTurns++;
        individualTurns++;
        //OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
        move(0,direction);
        mowerOutput += "ok";
    }

    // Move the mower one square in a given direction.
    public void moveOnce(String direction) {
        String emptyMark;
        Color emptyColor;
        final ImageIcon emptyIcon;
        Square[][] squares = Grid.getSquares();
        if (stepCount < 0 || isCharging || (relativeX == 0 && relativeY == 0)) {
            emptyMark = Grid.getChargePadMark();
            emptyColor = Color.GREEN;
            emptyIcon = new ImageIcon("chargePad.jpg");
            isCharging = true;
        }
        else {
            emptyMark = Grid.getEmptyMark();
            emptyColor = Color.BLACK;
            emptyIcon = new ImageIcon("mowedGrass.jpg");
        }
        isCharging = false;

        if(!mowerDirection.equals(direction)){
            changeDirection(direction);
        }

        // Check for pending collision.
        String[] perim = memScan();
        //addScannedSquares(perim);
        int forward = Integer.parseInt(Grid.getDirectionToIndex().get(mowerDirection));
        if(perim[forward].equals("mower")){
            if(!state.equals("STALLED")){
                state = "STALLED";
                stallCount = MAX_STALLS;
                mowerDirection = Grid.getOppositeDirectionMap().get(mowerDirection);
                //changeDirection(backwards);
            }
            return;
        }

        if (direction.equals("north")) {
            if (y_pos-1 >= 0) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                y_pos -= 1;
                relativeY += 1;

              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                mowerIcon = emptyIcon;
            }
        }else if (direction.equals("northeast")) {
            if (y_pos-1 >= 0 && x_pos+1 < Grid.getCOLS()) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                x_pos += 1;
                y_pos -= 1;
                relativeX += 1;
                relativeY += 1;

              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                mowerIcon = emptyIcon;
            }
        }else if (direction.equals("east")) {
            if (x_pos+1 < Grid.getCOLS()) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                x_pos += 1;
                relativeX += 1;

              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                mowerIcon = emptyIcon;
            }
        }else if (direction.equals("southeast")) {
            if (y_pos+1 < Grid.getROWS() && x_pos+1 < Grid.getCOLS()) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                x_pos += 1;
                y_pos += 1;
                relativeX += 1;
                relativeY -= 1;

              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                mowerIcon = emptyIcon;
            }
        } else if (direction.equals("south")) {
            if (y_pos+1 < Grid.getROWS()) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                y_pos += 1;
                relativeY -= 1;
              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                mowerIcon = emptyIcon;
            }
        } else if (direction.equals("southwest")) {
            if (y_pos+1 < Grid.getROWS() && x_pos-1 >= 0) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                x_pos -= 1;
                y_pos += 1;
                relativeX -= 1;
                relativeY -= 1;

              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                mowerIcon = emptyIcon;
            }
        } else if (direction.equals("west")) {
            if (x_pos-1 >= 0) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                x_pos -= 1;
                relativeX -= 1;

              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                mowerIcon = emptyIcon;
            }
        } else if (direction.equals("northwest")) {
            if (y_pos-1 >= 0 && x_pos-1 >= 0) {
                squares[y_pos][x_pos].setMarker(emptyMark);
                squares[y_pos][x_pos].setSquareColor(emptyColor);
                squares[y_pos][x_pos].setSquareIcon(emptyIcon);
                x_pos -= 1;
                y_pos -= 1;
                relativeX -= 1;
                relativeY += 1;

              // Hit fence!
            } else {
                state = "OFF";
                mowerMark = " . ";
                mowerIcon = emptyIcon;
            }
        }
        Square currentSquare = new Square(relativeX,relativeY,Grid.getEmptyMark());
        boolean found = false;
        for (Square square : discoveredSquares){
            if(square.getX_pos() == relativeX && square.getY_pos() == relativeY){
                found = true;
            }
        }
        if (!found) {
            discoveredSquares.add(currentSquare);
        }
        isMovingForward = false;
        stepCount++;
    }

    // Move the mower a given number of steps in a given direction.
    public void move(int steps,String direction){
        if (steps > 0){
            isMovingForward = true;
            totalTurns++;
            individualTurns++;
            chargeLevel -= 2;
        }
        if (steps == 0) {
            chargeLevel--;
            isMovingForward = false;
        }
        System.out.println("mower_" + mowerNumber + " move," + steps+"," + direction);
        mowerOutput = "move," + steps + "," + direction + "\n";

        for (int i =0; i<steps; i++){
            if (state.equals("OFF")){
                //mowerMark = mowerNumber % 10 + "X " ;
                System.out.println("crash");
                mowerOutput += "crashed";
                OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                break;
            } else if (state.equals("STALLED")) {
                mowerOutput += "stalled";
                break;
            } else {
                mowerOutput += "ok";
            }

            moveOnce(direction);
            OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
        }
    }

    public void stuckCheck(){
        //stuckInPlace = false;
        for (Square square : discoveredSquares){
            boolean sameSpot = square.getX_pos() == relativeX && square.getY_pos() == relativeY;
            boolean energySpot = square.getDescription().equals("energy");
            if (!energySpot && sameSpot && stepCount > 10) {
                square.setVisitCount(square.getVisitCount() + 1);
            }
            if (square.getVisitCount() > 2){
                stuckInPlace = true;
                square.setVisitCount(0);
            }
        }
    }

    // Display discoveredSquares
    public void displayDiscoveredSquares(){
        ArrayList<Square> toBeRemoved = new ArrayList<>();
        /*for (Square square : discoveredSquares){
            if (square.getDescription().equals("fence")){
                toBeRemoved.add(square);
            }
        }*/
        discoveredSquares.removeAll(toBeRemoved);
        toBeRemoved.clear();
        for (Square square : discoveredSquares) {
            square.displayInfo();
        }
    }

    public String[] memScan(){
        String[] perim = new String[8];
        Map<String, String> descriptionMap = Grid.getMarkerToDescription();

        // Fill perim with " ? " for unknown squares.
        for(int i=0; i< perim.length; i++){
            perim[i] = " ? ";
        }

        if (discoveredSquares.size() == 0){
            return perim;
        }

        for (Square s : discoveredSquares) {
            // north
            if (s.getX_pos() == relativeX && s.getY_pos() == relativeY + 1) {
                perim[0] = descriptionMap.get(s.getMarker());
            }
            // northeast
            if (s.getX_pos() == relativeX + 1 && s.getY_pos() == relativeY + 1) {
                perim[1] = descriptionMap.get(s.getMarker());
            }
            // east
            if (s.getX_pos() == relativeX + 1 && s.getY_pos() == relativeY) {
                perim[2] = descriptionMap.get(s.getMarker());
            }
            // southeast
            if (s.getX_pos() == relativeX + 1 && s.getY_pos() == relativeY - 1) {
                perim[3] = descriptionMap.get(s.getMarker());
            }
            // south
            if (s.getX_pos() == relativeX && s.getY_pos() == relativeY-1) {
                perim[4] = descriptionMap.get(s.getMarker());
            }
            // southwest
            if (s.getX_pos() == relativeX - 1 && s.getY_pos() == relativeY - 1) {
                perim[5] = descriptionMap.get(s.getMarker());
            }
            // west
            if (s.getX_pos() == relativeX - 1 && s.getY_pos() == relativeY) {
                perim[6] = descriptionMap.get(s.getMarker());
            }
            // northwest
            if (s.getX_pos() == relativeX - 1 && s.getY_pos() == relativeY + 1) {
                perim[7] = descriptionMap.get(s.getMarker());
            }
        }
        //printMemScan(perim);
        return perim;
    }

    public void printMemScan(String[] memPerim){
        //String[] memPerim = memScan();
        System.out.println("mower_" + mowerNumber + " memory scan: ");
        for (int i=0; i< memPerim.length; i++) {
            if (i == memPerim.length - 1) {
                System.out.println(memPerim[i]);
            } else {
                System.out.print(memPerim[i] + ",");
            }
        }
    }

    public Square getNextTarget() {
        //addScannedSquares(optimalScan());
        for (Square s : discoveredSquares){
            if(s.getX_pos()==x_pos && s.getY_pos()==y_pos && s.getDescription().equals("empty")){
                hitLastTarget = true;
            }
        }

        if(hitLastTarget){
            for(Square square : discoveredSquares){
                if (square.getDescription().equals("grass")){
                    nextTarget = square;
                }
            }
        }
        return nextTarget;
    }

    public String[] optimalScan(){
        boolean somethingToScan = false;
        String[] perim = memScan();
        for(String s : perim){
            if(s.contains("mower") || s.equals(" ? ") || s.equals("grass")){
                somethingToScan = true;
                break;
            }
        }
        if (somethingToScan){
           return scan();
        }
        return perim;
    }

    public void markSquares(){
        for (Square square : discoveredSquares) {
            boolean sameSpot = square.getX_pos() == relativeX && square.getY_pos() == relativeY;
            boolean energySpot = square.getDescription().equals("energy");
            if (!energySpot && sameSpot) {
                square.setVisitCount(square.getVisitCount() + 1);
            }
        }
        for (Square square : discoveredSquares){
            if (square.getVisitCount() > 1){
            System.out.println("Blocked Off");
            SimController.pause(1000);
            square.setDescription("finished");
            }
        }
    }

    // Setters and Getters.
    public int getStepCount() {
        return stepCount;
    }

    public int getMowerNumber() {
        return mowerNumber;
    }

    public int getX_pos() {
        return x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }

    public int getStallCount() {
        return stallCount;
    }

    public void setStallCount(int stallCount) {
        this.stallCount = stallCount;
    }

    public void stallCountDown() {
        this.stallCount--;
    }

    public String getMowerDirection() {
        return mowerDirection;
    }

    public boolean isMovingForward() {
        return isMovingForward;
    }

    public boolean isCharging(){
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public int getChargeLevel() {
        return chargeLevel;
    }

    public void setChargeLevel(int chargeLevel) {
        this.chargeLevel = chargeLevel;
    }

    public void decrementChargeLevel(){
        chargeLevel--;
    }

    public String getMowerMark() {
        return mowerMark;
    }

    public void setMowerMark(String mowerMark) {
        this.mowerMark = mowerMark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getIndividualTurns() {
        return individualTurns;
    }
}