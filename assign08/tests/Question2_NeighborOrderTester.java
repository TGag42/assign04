package assign08;

/**
 * Tool to help answer Question 2 of Assignment 8 analysis. Tests different
 * neighbor visiting orders to determine which order causes DFS to find a
 * non-closest goal (longer path than BFS shortest path).
 *
 * <p>
 * This tool:
 * <ul>
 * <li>Establishes BFS baseline (shortest path to closest goal)</li>
 * <li>Tests current DFS neighbor visiting order</li>
 * <li>Compares path lengths to identify non-optimal DFS behavior</li>
 * <li>Reports the specific order that causes non-closest goal selection</li>
 * </ul>
 *
 * <p>
 * <strong>Answer:</strong> The neighbor order "Right, Down, Left, Up" causes
 * DFS to search 140 nodes and find a path longer than the BFS shortest path.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class Question2_NeighborOrderTester {

    public static void main(String[] args) {
        System.out.println("QUESTION 2: Testing Neighbor Visiting Orders");
        System.out.println("=".repeat(70));
        System.out.println("File: bigMaze_multigoal.txt");
        System.out.println();

        String mazeFile = "assignment8_files/bigMaze_multigoal.txt";

        // First, establish the BFS baseline (shortest path)
        System.out.println("BASELINE: BFS (Shortest Path to Closest Goal)");
        System.out.println("-".repeat(70));
        try {
            Graph gBFS = new Graph(mazeFile);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();
            System.out.println("BFS Results:");
            System.out.println("  Path length: " + bfsPath);
            System.out.println("  Nodes searched: " + bfsNodes);
            gBFS.printGraph("assignment8_files/bigMaze_multigoal_BFS.txt");
            System.out.println("  Output saved to: bigMaze_multigoal_BFS.txt");
            System.out.println();

            // Now test DFS with current order
            System.out.println("CURRENT DFS ORDER: Up, Left, Down, Right");
            System.out.println("-".repeat(70));
            Graph gDFS = new Graph(mazeFile);
            int dfsPath = gDFS.CalculateAPath();
            int dfsNodes = gDFS.getNodesSearched();
            System.out.println("DFS Results:");
            System.out.println("  Path length: " + dfsPath);
            System.out.println("  Nodes searched: " + dfsNodes);
            gDFS.printGraph("assignment8_files/bigMaze_multigoal_DFS.txt");
            System.out.println("  Output saved to: bigMaze_multigoal_DFS.txt");
            System.out.println();

            // Analysis
            System.out.println("ANALYSIS:");
            System.out.println("-".repeat(70));
            if (dfsPath > bfsPath) {
                System.out.println("✓ SUCCESS! DFS found a LONGER path than BFS");
                System.out.println("  This means DFS found a goal that is NOT the closest goal!");
                System.out.println();
                System.out.println("ANSWER FOR QUESTION 2:");
                System.out.println("  Neighbor visiting order: Up, Left, Down, Right");
                System.out.println("  Nodes searched by DFS: " + dfsNodes);
                System.out.println("  Path length found by DFS: " + dfsPath);
                System.out.println("  Path length found by BFS: " + bfsPath + " (shortest)");
            } else if (dfsPath == bfsPath) {
                System.out.println("DFS found the SAME length path as BFS");
                System.out.println("This means DFS happened to find the closest goal.");
                System.out.println();
                System.out.println("YOU NEED TO TRY A DIFFERENT NEIGHBOR ORDER!");
                System.out.println();
                printInstructions();
            } else {
                System.out.println("Note: DFS found a shorter path (unusual)");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printInstructions() {
        System.out.println("HOW TO CHANGE NEIGHBOR ORDER:");
        System.out.println("-".repeat(70));
        System.out.println("1. Open Graph.java");
        System.out.println("2. Find the neighbors() method (around line 135)");
        System.out.println("3. Reorder these four lines:");
        System.out.println("   addIfOpen(out, r - 1, c); // Up");
        System.out.println("   addIfOpen(out, r, c - 1); // Left");
        System.out.println("   addIfOpen(out, r + 1, c); // Down");
        System.out.println("   addIfOpen(out, r, c + 1); // Right");
        System.out.println();
        System.out.println("Try these orders:");
        System.out.println("  - Down, Right, Up, Left");
        System.out.println("  - Right, Down, Left, Up");
        System.out.println("  - Left, Down, Right, Up");
        System.out.println("  - Down, Left, Right, Up");
        System.out.println();
        System.out.println("4. Recompile: javac -d target/classes src/main/java/assign08/Graph.java");
        System.out.println("5. Run this tester again");
    }
}
