import java.util.HashMap;
import java.util.Map;

public class Grid {
    private static String grassMark = " # ";
    private static String emptyMark = " . ";
    private static String craterMark = "{O}";
    private static String chargePadMark = "[+]";
    private static int ROWS;
    private static int COLS;

    private static Square[][] squares;
    // Map between the lawn's Markers and their string depiction.
    private static Map<String, String> markerToDescription = new HashMap<>();
    private static Map<String, String> descriptionToMarker = new HashMap<>();
    // Map between mower's direction and the marker representing it.
    private static Map<String, String> directionMap;
    // Map between perimeter index and direction (0: north, 1: northeast, ... 7: northwest)
    private static Map<String,String> indexToDirection = new HashMap<>();
    // Map between direction and perimeter index (north: 0, northeast: 1, ... northwest: 7)
    private static Map<String,String> directionToIndex = new HashMap<>();
    // Map between mower's direction and image
    private static Map<String,String> directionToImage;
    // Map between opposing directions.
    private static Map<String,String> oppositeDirectionMap;


    public Grid(int ROWS, int COLS) {
        Grid.ROWS = ROWS;
        Grid.COLS = COLS;
        squares = new Square[ROWS][COLS];

        directionToIndex.put("north","0"); directionToIndex.put("northeast","1");
        directionToIndex.put("east","2");  directionToIndex.put("southeast","3");
        directionToIndex.put("south","4"); directionToIndex.put("southwest","5");
        directionToIndex.put("west","6");  directionToIndex.put("northwest","7");

        for(Map.Entry<String, String> entry : directionToIndex.entrySet()){
            indexToDirection.put(entry.getValue(), entry.getKey());
        }

        directionMap = new HashMap<>();
        directionMap.put("north","N "); directionMap.put("south","S ");
        directionMap.put("east","E ");  directionMap.put("west","W ");
        directionMap.put("northeast","NE"); directionMap.put("northwest","NW");
        directionMap.put("southeast","SE"); directionMap.put("southwest","SW");

        directionToImage = new HashMap<>();
        directionToImage.put("north","mowerNorth.jpg"); directionToImage.put("northeast","mowerNortheast.jpg");
        directionToImage.put("east","mowerEast.jpg"); directionToImage.put("southeast","mowerSoutheast.jpg");
        directionToImage.put("south","mowerSouth.jpg"); directionToImage.put("southwest","mowerSouthwest.jpg");
        directionToImage.put("west","mowerWest.jpg"); directionToImage.put("northwest","mowerNorthwest.jpg");

        oppositeDirectionMap = new HashMap<>();
        oppositeDirectionMap.put("north","south"); oppositeDirectionMap.put("northwest","southeast");
        oppositeDirectionMap.put("northeast","southwest"); oppositeDirectionMap.put("east","west");
        oppositeDirectionMap.put("southeast","northwest"); oppositeDirectionMap.put("south","north");
        oppositeDirectionMap.put("southwest","northeast"); oppositeDirectionMap.put("west","east");

        markerToDescription.put(getGrassMark(),"grass"); markerToDescription.put(getCraterMark(),"crater");
        markerToDescription.put(" . ", "empty"); markerToDescription.put("[+]","energy");
        markerToDescription.put(null, "fence");

        // Add all possible mower icons to markerToDescription.
        for(Map.Entry<String, String> entry : directionMap.entrySet()){
            for(int i=1; i<=10; i++) {
                String icon = i % 10 + entry.getValue();
                markerToDescription.put(icon, "mower_" + i % 10);
            }
        }
        // Add all inactive mower icons to markerToDescription.
        for(int i=1; i<=10; i++) {
            String icon = i % 10 + "+-";
            markerToDescription.put(icon, "mower_" + i % 10);
        }
        // Create descriptionToMarker Map from MarkerToDescription
        for(Map.Entry<String, String> entry : markerToDescription.entrySet()){
            descriptionToMarker.put(entry.getValue(), entry.getKey());
        }

         /////////////////////////////////////////////////////////////////////////////
        ///    Populate squares[][] with Square objects; set image to grassMark.  ///
       /////////////////////////////////////////////////////////////////////////////

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                squares[row][col] = new Square(row,col,grassMark);
            }
        }
    }
    // Return Markers of a specific square.
    public static String getSquareMarker(int x_pos, int y_pos){
        return markerToDescription.get(squares[y_pos][x_pos].getMarker());
    }

    // Setters and Getters.
    public static int getROWS(){
        return ROWS;
    }

    public static int getCOLS(){
        return COLS;
    }

    public static Square[][] getSquares() {
        return squares;
    }

    public static String getGrassMark() {
        return grassMark;
    }

    public static String getEmptyMark() {
        return emptyMark;
    }

    public static String getCraterMark() {
        return craterMark;
    }

    public static String getChargePadMark() {
        return chargePadMark;
    }

    public static Map<String,String> getDirectionMap(){
        return directionMap;
    }

    public static Map<String,String> getOppositeDirectionMap(){
        return oppositeDirectionMap;
    }

    public static Map<String, String> getIndexToDirection() {
        return indexToDirection;
    }

    public static Map<String, String> getDirectionToIndex() {
        return directionToIndex;
    }

    public static Map<String, String> getMarkerToDescription() {
        return markerToDescription;
    }

    public static Map<String, String> getDescriptionToMarker(){
        return descriptionToMarker;
    }

    public static Map<String, String> getDirectionToImage() {
        return directionToImage;
    }

}