package assign08;

/**
 * Test class to demonstrate node counting functionality for algorithm analysis.
 *
 * <p>
 * This class runs both BFS and DFS on a test maze and reports:
 * <ul>
 * <li>Path length found by each algorithm</li>
 * <li>Number of nodes searched by each algorithm</li>
 * <li>Output files showing the paths found</li>
 * </ul>
 *
 * <p>
 * Useful for comparing algorithm efficiency and understanding search behavior.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class TestWithNodeCounting {

    public static void main(String[] args) {
        testMaze("assignment8_files/simpleMaze.txt", "Simple 5x5 Maze");
    }

    private static void testMaze(String inputFile, String mazeName) {
        System.out.println("=".repeat(60));
        System.out.println(mazeName);
        System.out.println("=".repeat(60));

        // Test BFS
        try {
            Graph g1 = new Graph(inputFile);
            int pathLength1 = g1.CalculateShortestPath();
            int nodesSearched1 = g1.getNodesSearched();
            System.out.println("\nBFS (Shortest Path):");
            System.out.println("  Path length: " + pathLength1);
            System.out.println("  Nodes searched: " + nodesSearched1);
            g1.printGraph(inputFile.replace(".txt", "_BFS_output.txt"));
        } catch (Exception e) {
            System.out.println("Error with BFS: " + e.getMessage());
        }

        // Test DFS
        try {
            Graph g2 = new Graph(inputFile);
            int pathLength2 = g2.CalculateAPath();
            int nodesSearched2 = g2.getNodesSearched();
            System.out.println("\nDFS (Any Path):");
            System.out.println("  Path length: " + pathLength2);
            System.out.println("  Nodes searched: " + nodesSearched2);
            g2.printGraph(inputFile.replace(".txt", "_DFS_output.txt"));
        } catch (Exception e) {
            System.out.println("Error with DFS: " + e.getMessage());
        }

        System.out.println();
    }
}
