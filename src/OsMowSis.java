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
        ArrayList<Mower> mowers = FileReader.mowers;
        Lawn lawn = FileReader.lawn;
        SimController sim = FileReader.sim;
        OsMowSisViewer viewer = new OsMowSisViewer();

        // Draw the lawn's initial state.
        viewer.createDisplay();
        SimController.pause(4000);
        SimController.draw();
        // Run the simulation loop.
        sim.simulationLoop(lawn,mowers);
        // Print the simulation results.
        //OsMowSisViewer.drawLawn();
        sim.displayResults();
    }
}