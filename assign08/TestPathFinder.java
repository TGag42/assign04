package assign08;

/**
 * Example demonstration of how to use the pathfinder to solve mazes. Shows both
 * BFS (shortest path) and DFS (any path) usage.
 *
 * <p>
 * This class demonstrates solving a maze with both algorithms and writing the
 * solutions to output files for visualization.
 *
 * @author Daniel Kopta
 * @version November 6, 2025
 */
public class TestPathFinder {

    /**
     * Demonstrates solving bigMaze.txt with both BFS and DFS algorithms.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        /*
		 * The below code assumes you have a folder called assignment8_files 
		 * in your project folder, and in that folder is a file called bigMaze.txt.
		 * If your solution is implemented correctly, it will produce a file called
		 * testOutput.txt which has the solution to the maze.
		 * You will have to browse to that folder to view the output, it will not 
		 * automatically show up in Eclipse.
		 * testOutput1.txt - solution maze with shortest path to closest goal
		 * testOutput2.txt - solution maze with any path to any goal
         */

        // Run the pathfinder with a final argument of true to find the shortest path to the closest goal
        PathFinder.solveMaze("assignment8_files/bigMaze.txt", "assignment8_files/testOutput1.txt", true);
        // Run the pathfinder with a final argument of false to find any path to any goal
        PathFinder.solveMaze("assignment8_files/bigMaze.txt", "assignment8_files/testOutput2.txt", false);
    }
}
