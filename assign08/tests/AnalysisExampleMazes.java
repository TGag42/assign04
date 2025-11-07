package assign08;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Helper tool to create example mazes for Assignment 8 analysis questions.
 * Generates custom mazes that demonstrate specific algorithmic behaviors:
 * <ul>
 * <li><strong>Question 3:</strong> Creates a maze where DFS finds a farther
 * goal but searches fewer nodes than BFS (demonstrates DFS efficiency in
 * certain maze configurations)</li>
 * <li><strong>Question 4:</strong> Creates a maze where BFS is O(1) and DFS is
 * O(N) (demonstrates worst-case DFS behavior)</li>
 * </ul>
 *
 * <p>
 * Each generated maze includes:
 * <ul>
 * <li>Visual layout preview in console</li>
 * <li>Expected behavior explanation</li>
 * <li>Actual test results with node counts</li>
 * </ul>
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class AnalysisExampleMazes {

    public static void main(String[] args) {
        System.out.println("Creating example mazes for analysis questions...\n");

        createQuestion3Example();
        createQuestion4Example();

        System.out.println("\n✓ All example mazes created!");
    }

    /**
     * Creates an example maze for Question 3. This maze demonstrates a scenario
     * where DFS finds a farther goal but searches fewer nodes than BFS.
     *
     * <p>
     * Maze design: Start in middle with one goal above requiring branching
     * exploration (many nodes for BFS) and one goal below in a straight line
     * (few nodes for DFS with Down-first ordering).
     */
    private static void createQuestion3Example() {
        System.out.println("QUESTION 3 EXAMPLE:");
        System.out.println("Conditions: DFS finds FARTHER goal but searches FEWER nodes");
        System.out.println("-".repeat(70));

        try {
            String filename = "assignment8_files/q3_example.txt";
            PrintWriter out = new PrintWriter(new FileWriter(filename));

            // Maze design:
            // - Start in middle
            // - Closest goal requires exploring multiple branches (BFS explores all)
            // - Farther goal is straight down (DFS with Down-first finds immediately)
            out.println("9 9");
            out.println("XXXXXXXXX");
            out.println("X   G   X");
            out.println("X XX XX X");
            out.println("X XX XX X");
            out.println("X   S   X");
            out.println("X       X");
            out.println("X       X");
            out.println("X   G   X");
            out.println("XXXXXXXXX");
            out.close();

            System.out.println("Created: " + filename);
            System.out.println("\nMaze layout:");
            System.out.println("XXXXXXXXX");
            System.out.println("X   G   X  <- Goal 1 (closer, row 1)");
            System.out.println("X XX XX X");
            System.out.println("X XX XX X");
            System.out.println("X   S   X  <- Start (row 4)");
            System.out.println("X       X");
            System.out.println("X       X");
            System.out.println("X   G   X  <- Goal 2 (farther, row 7)");
            System.out.println("XXXXXXXXX");

            System.out.println("\nExplanation:");
            System.out.println("- BFS explores all directions evenly, checking row 3, then 2, then 1");
            System.out.println("  Must explore walls and multiple paths to find Goal 1");
            System.out.println("- DFS with Down-first order goes straight down to Goal 2");
            System.out.println("  Fewer nodes explored even though goal is farther!");

            // Test it
            System.out.println("\nTesting with current neighbor order...");
            Graph gBFS = new Graph(filename);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();

            Graph gDFS = new Graph(filename);
            int dfsPath = gDFS.CalculateAPath();
            int dfsNodes = gDFS.getNodesSearched();

            System.out.println("BFS: path=" + bfsPath + ", nodes=" + bfsNodes);
            System.out.println("DFS: path=" + dfsPath + ", nodes=" + dfsNodes);

            if (dfsPath >= bfsPath && dfsNodes < bfsNodes) {
                System.out.println("✓ Perfect! DFS found farther/equal goal with fewer nodes!");
            } else {
                System.out.println("Note: Results depend on neighbor visiting order");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Question 4: BFS O(1) and DFS O(N)
     */
    private static void createQuestion4Example() {
        System.out.println("\n\nQUESTION 4 EXAMPLE:");
        System.out.println("Conditions: BFS O(1) [goal adjacent], DFS O(N) [explores away first]");
        System.out.println("-".repeat(70));

        try {
            String filename = "assignment8_files/q4_example.txt";
            PrintWriter out = new PrintWriter(new FileWriter(filename));

            // Maze design:
            // - Start at top left
            // - Goal immediately to the right (BFS finds in 2 nodes)
            // - Long corridor going down (DFS explores entire corridor if Down-first)
            out.println("10 5");
            out.println("XXXXX");
            out.println("XSGXX");
            out.println("X   X");
            out.println("X   X");
            out.println("X   X");
            out.println("X   X");
            out.println("X   X");
            out.println("X   X");
            out.println("X   X");
            out.println("XXXXX");
            out.close();

            System.out.println("Created: " + filename);
            System.out.println("\nMaze layout:");
            System.out.println("XXXXX");
            System.out.println("XSGXX  <- Start at (1,1), Goal at (1,2)");
            System.out.println("X   X");
            System.out.println("X   X  <- Long corridor");
            System.out.println("X   X");
            System.out.println("X   X");
            System.out.println("X   X");
            System.out.println("X   X");
            System.out.println("X   X");
            System.out.println("XXXXX");

            System.out.println("\nExplanation:");
            System.out.println("- Goal is ADJACENT to start (immediately to the right)");
            System.out.println("- BFS: Checks Up (wall), Left (wall), Down (open), Right (GOAL!)");
            System.out.println("  BFS finds goal in ~2 nodes -> O(1)");
            System.out.println("- DFS with Down-first order: Goes down entire corridor first");
            System.out.println("  Then must backtrack all the way up to find goal -> O(N)");
            System.out.println("  Where N = number of nodes in corridor");

            // Test it
            System.out.println("\nTesting with current neighbor order...");
            Graph gBFS = new Graph(filename);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();

            Graph gDFS = new Graph(filename);
            int dfsPath = gDFS.CalculateAPath();
            int dfsNodes = gDFS.getNodesSearched();

            System.out.println("BFS: path=" + bfsPath + ", nodes=" + bfsNodes + " (should be very small)");
            System.out.println("DFS: path=" + dfsPath + ", nodes=" + dfsNodes + " (should be large)");

            if (bfsNodes <= 3 && dfsNodes > 10) {
                System.out.println("✓ Perfect! BFS ~O(1), DFS ~O(N)");
            } else {
                System.out.println("Note: Results depend on neighbor visiting order");
                System.out.println("For DFS O(N), neighbor order must prioritize Down over Right");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
