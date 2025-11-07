package assign08;

/**
 * Simple test class to verify BFS and DFS pathfinding implementations. Tests
 * both algorithms on a simple maze and outputs results to files.
 *
 * <p>
 * This class provides a quick verification that both BFS and DFS can
 * successfully solve a basic maze.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class TestPathFinderSimple {

    public static void main(String[] args) {
        System.out.println("Testing Simple Maze with BFS (shortest path)...");
        PathFinder.solveMaze("assignment8_files/simpleMaze.txt", "assignment8_files/simpleOutput_BFS.txt", true);
        System.out.println("✓ BFS output written to assignment8_files/simpleOutput_BFS.txt");

        System.out.println("\nTesting Simple Maze with DFS (any path)...");
        PathFinder.solveMaze("assignment8_files/simpleMaze.txt", "assignment8_files/simpleOutput_DFS.txt", false);
        System.out.println("✓ DFS output written to assignment8_files/simpleOutput_DFS.txt");

        System.out.println("\n✓ All tests complete!");
    }
}
