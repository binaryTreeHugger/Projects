import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class SimController {
    private OsMowSisViewer viewer = new OsMowSisViewer();
    private static Lawn lawn;
    private final int MAX_TURNS = FileReader.MAX_TURNS;
    private Square target;


    public SimController(Lawn lawn){
        this.lawn = lawn;
    }

    // Pause the simulation for a given number of milliseconds.
    // Code provided by Professor Moss.
    public static void pause(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    // Update the lawn without drawing it.
    public static void update(){
        ArrayList<Mower> mowers = FileReader.mowers;
        Square[][] squares = Grid.getSquares();
        for (Mower mower : mowers) {
            squares[mower.getY_pos()][mower.getX_pos()].setMarker(mower.getMowerMark());
        }
    }

    // Draw the lawn.
    public static void draw(){
        int ROWS = Grid.getROWS();
        int COLS = Grid.getCOLS();
        Square[][] squares = Grid.getSquares();
        update();
        // Line buffers between frames.
        for(int line = 0; line < 2; line++){
            System.out.println();
        }
        // draw lawn frame.
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                System.out.print(squares[row][col].getMarker() + " ");
            }
            System.out.println();
        }
        // Line buffers between frames.
        for(int line = 0; line < 2; line++){
            System.out.println();
        }
        pause(00);
    }

    // Checks to see if one mower is about to hit another.
    public boolean mowerInPath(Mower mower, String[] perim){
        int forward = Integer.parseInt(Grid.getDirectionToIndex().get(mower.getMowerDirection()));
        if(perim[forward].equals("mower") && mower.isMovingForward()){
            mower.setState("STALLED");
            mower.setStallCount(Mower.MAX_STALLS);
            //pause(2000);
            return true;
        }
        return false;
    }

    public void chargingCheck(){
        ArrayList<Mower> mowers = lawn.getMowers();
        ArrayList<Square> chargingPads = lawn.getChargingPads();
        for (Mower mower : mowers){
            if (mower.isCharging() || mower.isMovingForward()) {
                //System.out.println("charging ...");
                continue;
            }
            for(Square chargingPad : chargingPads){
                boolean onPad = mower.getX_pos()==chargingPad.getX_pos() && mower.getY_pos()==chargingPad.getY_pos();
                if (mower.getStepCount() > 0 && onPad ){
                    mower.setChargeLevel(FileReader.chargeLevel);
                    mower.setState("STALLED");
                    System.out.println("charging ...");
                    //pause(1500);
                    mower.setStallCount(1);
                    mower.setCharging(true);
                    return;
                }
                else {
                    mower.setCharging(false);
                }
            }
        }
    }

    public void crashCheck(){
        ArrayList<Mower> mowers = lawn.getMowers();
        ArrayList<Square> craters = lawn.getCraters();
        Square[][] squares = Grid.getSquares();
        // Determine if mower has crashed into a crater.
        for (Mower mower : mowers){
            for (Square crater: craters){
                if (mower.getX_pos() == crater.getX_pos() && mower.getY_pos() == crater.getY_pos()){
                    mower.setState("OFF");
                    System.out.println("crash");
                    //pause(1000);
                    mower.mowerIcon = new ImageIcon("crater.jpg");
                    squares[crater.getY_pos()][crater.getX_pos()] = crater;
                }
            }
        }
        // For multiple mower simulations, determine if two mowers have collided.
        for (int i=0; i<mowers.size(); i++){
            for (int j=i+1; j<mowers.size(); j++){
                boolean sameX = mowers.get(i).getX_pos() == mowers.get(j).getX_pos();
                boolean sameY = mowers.get(i).getY_pos() == mowers.get(j).getY_pos();
                if (sameX && sameY){
                    Mower mowerAtFault = mowers.get(j);
                    Mower mowerVictim = mowers.get(i);
                    System.out.println("crash");
                    //pause(1000);
                    mowerAtFault.move(0, mowerAtFault.getMowerDirection());
                    mowerAtFault.setState("STALLED");
                    mowerAtFault.setStallCount(Mower.MAX_STALLS);
                    grassHunter(mowerVictim, mowerVictim.scan());
                    //mowerAtFault.discoveredSquares = mowerVictim.discoveredSquares;
                    //mowerAtFault.relativeX = mowerVictim.relativeX;
                    //mowerAtFault.relativeY = mowerVictim.relativeY;
                    //mowers.get(j).setState("OFF");
                    OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                }
            }
        }
    }

    // Using the scan results (perim), provide a list of directions containing grass or empty squares.
    public ArrayList<String> getValidDirections(String[] perim){
        ArrayList<String> directions = new ArrayList<>();
        directions.add("north"); directions.add("northeast"); directions.add("east");
        directions.add("southeast"); directions.add("south"); directions.add("southwest");
        directions.add("west"); directions.add("northwest");

        ArrayList<String> itemsToRemove = new ArrayList<>();
        for (int i=0; i< perim.length; i++){
            if(!perim[i].equals("grass") && !perim[i].equals("empty") && !perim[i].equals("energy")){
                String index = String.valueOf(i);
                itemsToRemove.add(Grid.getIndexToDirection().get(index));
                //directions.remove(indexToDirection.get(index));
            }
        }
        directions.removeAll(itemsToRemove);
        return directions;
    }

    // Using the scan results (perim), provide a list of directions containing grass squares.
    public ArrayList<String> getGrassDirections(String[] perim){
        ArrayList<String> directions = new ArrayList<>();
        directions.add("north"); directions.add("northeast"); directions.add("east");
        directions.add("southeast"); directions.add("south"); directions.add("southwest");
        directions.add("west"); directions.add("northwest");

        for (int i=0; i< perim.length; i++){
            if(!(perim[i].equals("grass"))){
                String index = String.valueOf(i);
                directions.remove(Grid.getIndexToDirection().get(index));
            }
        }
        return directions;
    }

    // Using the scan results (perim), provide a list of directions containing chargingPads.
    public ArrayList<String> getEnergyDirections(String[] perim){
        ArrayList<String> directions = new ArrayList<>();
        directions.add("north"); directions.add("northeast"); directions.add("east");
        directions.add("southeast"); directions.add("south"); directions.add("southwest");
        directions.add("west"); directions.add("northwest");

        for (int i=0; i< perim.length; i++){
            if(!(perim[i].equals("energy"))){
                String index = String.valueOf(i);
                directions.remove(Grid.getIndexToDirection().get(index));
            }
        }
        return directions;
    }

    // Displays simulation results.
    public void displayResults(){
        // Display lawn data: total squares, grass squares, grass squares cut, turns count.
        int totalSquares = lawn.getTotalSquares();
        int totalGrass = lawn.numberOfGrassSquares();
        int grassCut = totalGrass - lawn.grassLeft();
        System.out.println(totalSquares + "," + totalGrass + "," + grassCut + "," + Mower.totalTurns);
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    // Performs a simple move strategy, no real use of mower's memory to map surroundings.//
    ////////////////////////////////////////////////////////////////////////////////////////

    public void basicMove(Mower mower, String[] perim){
        //String[] perim = mower.scan();
        // validMoves contains the scan results that are valid turns.
        ArrayList<String> validMoves = getValidDirections(perim);
        // grassMoves contains the scan results that contain grass squares.
        ArrayList<String> grassMoves = getGrassDirections(perim);
        // energyMoves contains scan results with chargingPads.
        ArrayList<String> energyMoves = getEnergyDirections(perim);

        // Try to move in the mower's current direction, if it contains grass.
        if(grassMoves.contains(mower.getMowerDirection())){
            mower.move(1,mower.getMowerDirection());
        }
        // Otherwise, move towards a random grass square.
        else if (!grassMoves.isEmpty()){
            int randInt = (int) Math.floor(Math.random() * grassMoves.size());
            String nextMove = grassMoves.get(randInt);
            if (!mower.getMowerDirection().equals(nextMove)) {
                mower.changeDirection(nextMove);
            } else{
                mower.move(1,nextMove);
            }
            // If no grass squares are nearby, move in the mower's current direction, if it's valid.
        } else if (validMoves.contains(mower.getMowerDirection())) {
            mower.move(1,mower.getMowerDirection());

            // Go backwards if it's the only option (to avoid oscillations).
        } else {
            String backwards = Grid.getOppositeDirectionMap().get(mower.getMowerDirection());
            if (validMoves.size()==1 && validMoves.get(0).equals(backwards)){
                mower.changeDirection(backwards);
            }
            // If no grass squares are nearby & the current direction is invalid, move in a random valid direction.
            else if (validMoves.size() > 0){
                int randInt = (int) Math.floor(Math.random() * validMoves.size());
                while(validMoves.get(randInt).equals(backwards)){
                    randInt = (int) Math.floor(Math.random() * validMoves.size());
                }
                String nextMove = validMoves.get(randInt);
                if (!mower.getMowerDirection().equals(nextMove)) {
                    mower.changeDirection(nextMove);
                } else{
                    mower.move(1,nextMove);
                }
            }
        }
        if (mower.stuckInPlace){
            mower.stuckInPlace = false;
        }
    }

    public void recharge(Mower mower, String[] perim){
        goToSquare(mower, perim,0,0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //       Uses the stored squares from memScan to move to a known square.              //
    ////////////////////////////////////////////////////////////////////////////////////////

    public void goToSquare(Mower mower, String[] perim, int x_pos, int y_pos) {
        String horizontal;
        String vertical;
        chargingCheck();
        ArrayList<String> validMoves = getValidDirections(perim);
        System.out.println("Got valid directions");
        if (x_pos == mower.relativeX && y_pos == mower.relativeY) {
            return;
        }
        if (x_pos - mower.relativeX < 0) {
            horizontal = "west";
        } else {
            horizontal = "east";
        }
        if (y_pos - mower.relativeY > 0) {
            vertical = "north";
        } else {
            vertical = "south";
        }

        // If possible, move diagonally towards chosen square
        boolean validMove =  validMoves.contains(vertical + horizontal);
        boolean onAxis = mower.relativeX == x_pos || mower.relativeY == y_pos;
        if (validMove && !onAxis){
            String nextMove = vertical + horizontal;
            if (mower.getMowerDirection().equals(nextMove)){
                mower.move(1, nextMove);
            }
            else {
                mower.changeDirection(nextMove);
            }
            return;
        }
        if (mower.relativeX != x_pos) {
            //mower.decrementChargeLevel();
            int randInt = (int) Math.floor(Math.random() * validMoves.size());
            String nextMove = validMoves.get(randInt);
            if (validMoves.contains(horizontal)) {
                nextMove = horizontal;
            } else if (validMoves.contains(vertical + horizontal)){
                nextMove = vertical + horizontal;
            } else if (validMoves.contains(Grid.getOppositeDirectionMap().get(vertical) + horizontal)) {
                nextMove = Grid.getOppositeDirectionMap().get(vertical) + horizontal;
            } else if (validMoves.contains(vertical)) {
                nextMove = vertical;
            }

            if (mower.getMowerDirection().equals(nextMove)){
                mower.move(1, nextMove);
            }
            else {
                mower.changeDirection(nextMove);
            }

        } else {
            //mower.decrementChargeLevel();
            int randInt = (int) Math.floor(Math.random() * validMoves.size());
            if (validMoves.size() == 0) {
                return;
            }
            String nextMove = validMoves.get(randInt);
            if (validMoves.contains(vertical)) {
                nextMove = vertical;
            } else if (validMoves.contains(vertical + horizontal)){
                nextMove = vertical + horizontal;
            } else if (validMoves.contains(vertical + Grid.getOppositeDirectionMap().get(horizontal))) {
                nextMove = vertical + Grid.getOppositeDirectionMap().get(horizontal);
            }

            if (mower.getMowerDirection().equals(nextMove)){
                mower.move(1,nextMove);
            }
            else {
                mower.changeDirection(nextMove);
                mower.changeDirection(nextMove);
            }
        }
        OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
    }


    public void grassHunter(Mower mower,String[] perim){
        ArrayList<String> grassMoves = getGrassDirections(perim);
        if(grassMoves.contains(mower.getMowerDirection())){
            mower.move(1,mower.getMowerDirection());
        } else if (!grassMoves.isEmpty()){
            int randInt = (int) Math.floor(Math.random() * grassMoves.size());
            String nextMove = grassMoves.get(randInt);
            mower.changeDirection(nextMove);
        } else {
            target = mower.getNextTarget();
            if (target.getX_pos() == mower.relativeX && target.getY_pos() == mower.relativeY) {
                target = mower.getNextTarget();
                System.out.println(target.getX_pos() + "," + target.getY_pos());
                System.out.println("been there, done that!");

                mower.printMemScan(mower.memScan());
                //mower.displayDiscoveredSquares();
                //pause(1000);
                //mower.nextTarget = new Square(0,0,"energy");
                mower.hitLastTarget = false;
                mower.stuckInPlace = true;
                //return;
            }
            System.out.println(target.getX_pos() + " , " + target.getY_pos());
            goToSquare(mower, perim, target.getX_pos(),target.getY_pos());
        }
    }


    public void simulationLoop(Lawn lawn, ArrayList<Mower> mowers){
        // This loop runs until the lawn is finished, the turn limit is reached, or the mowers crash.
        while (lawn.isNotFinished()) {
            // If the Stop button is pressed, stop simulation.
            if (OsMowSisViewer.hitStop){
                System.out.println(String.format("%.2f",lawn.percentCut()) + "% of lawn mowed.");
                System.out.println(Mower.totalTurns + " turns.");
                break;
            }
            for (Mower mower : mowers){
                Mower.activeMower = mower.getMowerNumber();
                // If the Next button is clicked, pause simulation until it's clicked again.
                if (OsMowSisViewer.hitNext && !OsMowSisViewer.hitFastForward){
                    for(int i = 0; i < 1000000000; i++){
                        pause(10);
                        if (!OsMowSisViewer.hitNext){
                            OsMowSisViewer.hitNext = true;
                            break;
                        }
                        pause(10);
                    }
                }

                // Check each mower to see if it has crashed or exceeded turn limit
                crashCheck();
                chargingCheck();
                mower.stuckCheck();
                //mower.markSquares();
                if (mower.getState().equals("OFF") && lawn.isNotFinished()) {
                    //System.out.println("crash");
                    OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                    continue;
                } else if (mower.getChargeLevel() < 1 && mower.getState().equals("ON")){
                    System.out.println("mower_" + mower.getMowerNumber() + " charge level = 0%");
                    mower.setState("OFF");
                    mower.setMowerMark(mower.getMowerNumber() % 10 + "+-");
                    OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                    continue;
                } else if (mower.getState().equals("STALLED")){
                    if (mower.getStallCount() > 0) {
                        System.out.println("stalled");
                        //mower.incrementTurns();
                        mower.stallCountDown();
                        continue;
                    }
                    else if (mower.getStallCount() == 0){
                        mower.setState("ON");
                    }
                }

                if(mower.getChargeLevel() < FileReader.chargeLevel*0.37){
                    System.out.println("mower_"+ mower.getMowerNumber() + ": LOW ENERGY!!!");
                    String[] perim = mower.memScan();
                    recharge(mower,perim);
                    draw();
                    OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                    System.out.println(String.format("%.2f",lawn.percentCut()) + "% of lawn mowed.");
                    System.out.println(Mower.totalTurns + " turns.");
                    continue;
                }
                /////////////////////////////////////////////////
                // Set conditions for basicMove or grassHunter.//
                /////////////////////////////////////////////////

                //mower.displayDiscoveredSquares();
                //mower.printMemScan(mower.memScan());
                if (mower.discoveredSquares.size() == 0) {
                    mower.scan();
                    draw();
                    OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                    continue;
                }
                String[] perim = mower.memScan();
                if (mower.getIndividualTurns() % 2 == 0){
                     mower.scan();
                     draw();
                     OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                     continue;
                }

                if (lawn.grassLeft() > 100){
                    basicMove(mower, perim);
                } else {
                    if(mower.stuckInPlace){
                        //System.out.println("mower_"+ mower.getMowerNumber() + " stuck!");
                        basicMove(mower, perim);
                    } else {
                        grassHunter(mower, perim);
                    }
                }
                if (mower.getState().equals("ON")) {
                    System.out.println("ok");
                }
                draw();
                OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);
                // Display data about the lawn, for troubleshooting.
                System.out.println(String.format("%.2f",lawn.percentCut()) + "% of lawn mowed.");
                System.out.println(Mower.totalTurns + " turns.");
            }
            //OsMowSisViewer.drawLawn(OsMowSisViewer.pauseTime);

            // If totalTurns exceeds MAX_TURNS, end simulationLoop.
            if (Mower.totalTurns > MAX_TURNS){
                System.out.println("turn limit exceeded.");
                OsMowSisViewer.display.append("\nturn limit exceeded");
                break;
            }

            // If all the mower's are off, end simulationLoop.
            int countOFF = 0;
            for (Mower mower : mowers){
                if (mower.getState().equals("OFF")){
                    countOFF++;
                }
            }
            if (countOFF == mowers.size()){
                System.out.println("Out");
                break;
            }
        }
        // Turn off any mowers that aren't off.
        for (Mower mower : mowers) {
            if(!mower.getState().equals("OFF"))
                mower.setState("OFF");
        }
    }

    public static Lawn getLawn() {
        return lawn;
    }
}