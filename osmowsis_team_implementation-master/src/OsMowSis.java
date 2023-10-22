import java.io.IOException;
import java.util.ArrayList;

// Driver class for OsMowSis Mower Simulator.
public class OsMowSis {
    private static FileReader fr = new FileReader();
    public static void main(String[] args) {


        // If program arguments contain the filename, read it.
        if (args.length > 0) {
            try {
                fr.readFile(args[0]);
            } catch (IOException e) {
                System.out.println("Error opening file.");
            }
            // Otherwise, load the provided default file.
        } else {
            try {
                fr.readFile("scenario6.csv");
            } catch (IOException e) {
                System.out.println("Error opening file.");
                return;
            }
        }

        // Use FileReader class to crate mowers, lawn, & sim objects, and initialize MAX_TURNS.
//        ArrayList<Mower> mowers = FileReader.mowers;
        ArrayList<Mower> mowers = fr.mowers;
//        Lawn lawn = FileReader.lawn;
        Lawn lawn = fr.lawn;
//        SimController sim = FileReader.sim;
        SimController sim = fr.sim;
//        OsMowSisViewer viewer = new OsMowSisViewer(); this is not needed as the sim owns/has the viewer
        // Draw the lawn's initial state.
        sim.viewer.createDisplay();
        //SimController.pause(500);
        sim.pause(500);
        //SimController.draw();
        sim.draw();
        // Run the simulation loop.
        int loopCount = 0;
        while(true)
        {
            sim.simulationLoop(lawn,mowers);

            if(!lawn.isNotFinished()){
                break;
            }
            Mower mw = mowers.get(1);
            if( mw.totalTurns >= fr.MAX_TURNS){
                break;
            }

        }

        // Print the simulation results.
        //OsMowSisViewer.drawLawn();
        sim.displayResults();
    }
}