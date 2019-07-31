import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
    public static int MAX_TURNS;
    public static ArrayList<Square> craters = new ArrayList<>();
    public static ArrayList<Square> chargingPads = new ArrayList<>();
    public static ArrayList<Mower> mowers = new ArrayList<>();
    public static Lawn lawn;
    public static Grid grid;
    public static SimController sim;
    public static int collisionStalls;
    public static int chargeLevel;

    // Read the given file to set lawn dimensions, the number & location of mowers & craters, and maximum turns.
    public void readFile(String filename) throws IOException {
        int COLS;
        int ROWS;
        int MOWERS;
        int CRATERS;
        Scanner in = new Scanner(new File(filename));
        while (in.hasNext()){
            COLS = in.nextInt();
            ROWS = in.nextInt();
            MOWERS = in.nextInt();
            collisionStalls = in.nextInt();
            chargeLevel = in.nextInt();
            grid = new Grid(ROWS,COLS);
            int mowerNumber = 1;
            for (int i = 0; i< MOWERS; i++){
                String line = in.next();
                String[] tokens = line.split(",");
                int x = Integer.parseInt(tokens[0]);
                // Inverting y coordinates so array appears to grow upward.
                int y = ROWS-1 - Integer.parseInt(tokens[1]);
                String direction = tokens[2];
                Mower mower = new Mower(mowerNumber,x,y,direction);
                Square chargingPad = new Square(x,y,Grid.getChargePadMark());
                mowers.add(mower);
                chargingPads.add(chargingPad);
                mowerNumber++;
            }
            CRATERS = in.nextInt();
            for (int i = 0; i< CRATERS; i++){
                String line = in.next();
                String[] tokens = line.split(",");
                int x = Integer.parseInt(tokens[0]);
                // Inverting y coordinates so array appears to grow upward.
                int y = ROWS-1 - Integer.parseInt(tokens[1]);
                Square crater = new Square(x,y,Grid.getCraterMark());
                craters.add(crater);
            }
            MAX_TURNS = in.nextInt();
        }
        in.close();
        lawn = new Lawn(mowers, chargingPads, craters);
        sim =  new SimController(lawn);
    }
}
