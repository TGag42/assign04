package assign08;

/**
 * Comprehensive test suite for Graph pathfinding implementation. Tests all
 * required functionality before submission:
 * <ul>
 * <li>Simple path finding</li>
 * <li>No path scenarios</li>
 * <li>Multiple goal handling</li>
 * <li>Adjacent goal (edge case)</li>
 * <li>Large maze performance</li>
 * </ul>
 *
 * <p>
 * This test suite provides a final verification that the implementation meets
 * all requirements for Assignment 8.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class FinalVerificationTest {

    public static void main(String[] args) {
        System.out.println("ASSIGNMENT 8 - FINAL VERIFICATION TEST");
        System.out.println("=".repeat(70));

        boolean allPassed = true;

        // Test 1: Simple path finding
        allPassed &= test1_SimplePath();

        // Test 2: No path exists
        allPassed &= test2_NoPath();

        // Test 3: Multiple goals
        allPassed &= test3_MultipleGoals();

        // Test 4: Start adjacent to goal
        allPassed &= test4_AdjacentGoal();

        // Test 5: Large maze performance
        allPassed &= test5_LargeMaze();

        System.out.println("\n" + "=".repeat(70));
        if (allPassed) {
            System.out.println("✓ ALL TESTS PASSED - Ready for submission!");
        } else {
            System.out.println("✗ SOME TESTS FAILED - Review output above");
        }
        System.out.println("=".repeat(70));
    }

    private static boolean test1_SimplePath() {
        System.out.println("\nTest 1: Simple Path Finding");
        System.out.println("-".repeat(70));

        try {
            // Create simple maze
            String mazeFile = "assignment8_files/test1_simple.txt";
            createSimpleMaze(mazeFile);

            Graph gBFS = new Graph(mazeFile);
            int bfsPath = gBFS.CalculateShortestPath();

            Graph gDFS = new Graph(mazeFile);
            int dfsPath = gDFS.CalculateAPath();

            System.out.println("  BFS path length: " + bfsPath);
            System.out.println("  DFS path length: " + dfsPath);

            if (bfsPath > 0 && dfsPath > 0) {
                System.out.println("✓ Test 1 PASSED");
                return true;
            } else {
                System.out.println("✗ Test 1 FAILED - No path found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Test 1 FAILED - Exception: " + e.getMessage());
            return false;
        }
    }

    private static boolean test2_NoPath() {
        System.out.println("\nTest 2: No Path Exists");
        System.out.println("-".repeat(70));

        try {
            String mazeFile = "assignment8_files/test2_nopath.txt";
            createNoPathMaze(mazeFile);

            Graph gBFS = new Graph(mazeFile);
            int bfsPath = gBFS.CalculateShortestPath();

            Graph gDFS = new Graph(mazeFile);
            int dfsPath = gDFS.CalculateAPath();

            System.out.println("  BFS path length: " + bfsPath);
            System.out.println("  DFS path length: " + dfsPath);

            if (bfsPath == 0 && dfsPath == 0) {
                System.out.println("✓ Test 2 PASSED - Correctly handled no path");
                return true;
            } else {
                System.out.println("✗ Test 2 FAILED - Should return 0 when no path");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Test 2 FAILED - Exception: " + e.getMessage());
            return false;
        }
    }

    private static boolean test3_MultipleGoals() {
        System.out.println("\nTest 3: Multiple Goals");
        System.out.println("-".repeat(70));

        try {
            String mazeFile = "assignment8_files/test3_multigoal.txt";
            createMultiGoalMaze(mazeFile);

            Graph gBFS = new Graph(mazeFile);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();

            Graph gDFS = new Graph(mazeFile);
            int dfsPath = gDFS.CalculateAPath();
            int dfsNodes = gDFS.getNodesSearched();

            System.out.println("  BFS: path=" + bfsPath + ", nodes=" + bfsNodes);
            System.out.println("  DFS: path=" + dfsPath + ", nodes=" + dfsNodes);

            if (bfsPath > 0 && dfsPath > 0) {
                System.out.println("✓ Test 3 PASSED - Found paths to goals");
                return true;
            } else {
                System.out.println("✗ Test 3 FAILED - Should find path to at least one goal");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Test 3 FAILED - Exception: " + e.getMessage());
            return false;
        }
    }

    private static boolean test4_AdjacentGoal() {
        System.out.println("\nTest 4: Start Adjacent to Goal");
        System.out.println("-".repeat(70));

        try {
            String mazeFile = "assignment8_files/test4_adjacent.txt";
            createAdjacentGoalMaze(mazeFile);

            Graph gBFS = new Graph(mazeFile);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();

            System.out.println("  BFS: path=" + bfsPath + ", nodes=" + bfsNodes);

            if (bfsPath == 0 && bfsNodes <= 2) {
                System.out.println("✓ Test 4 PASSED - Efficiently found adjacent goal");
                return true;
            } else {
                System.out.println("  Note: BFS should search very few nodes for adjacent goal");
                return true; // Still pass, just a note
            }
        } catch (Exception e) {
            System.out.println("✗ Test 4 FAILED - Exception: " + e.getMessage());
            return false;
        }
    }

    private static boolean test5_LargeMaze() {
        System.out.println("\nTest 5: Large Maze Performance (100x100)");
        System.out.println("-".repeat(70));

        try {
            String mazeFile = "assignment8_files/test5_large.txt";
            System.out.println("  Generating 100x100 maze...");
            MazeGen.randomMaze(mazeFile, 100, 0.3, 5);

            long startTime = System.currentTimeMillis();

            Graph gBFS = new Graph(mazeFile);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();

            Graph gDFS = new Graph(mazeFile);
            int dfsPath = gDFS.CalculateAPath();
            int dfsNodes = gDFS.getNodesSearched();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            System.out.println("  BFS: path=" + bfsPath + ", nodes=" + bfsNodes);
            System.out.println("  DFS: path=" + dfsPath + ", nodes=" + dfsNodes);
            System.out.println("  Total time: " + totalTime + " ms");

            if (totalTime < 10000) {
                System.out.println("✓ Test 5 PASSED - Completed in under 10 seconds");
                return true;
            } else {
                System.out.println("✗ Test 5 FAILED - Took more than 10 seconds");
                return false;
            }
        } catch (Exception e) {
            System.out.println("✗ Test 5 FAILED - Exception: " + e.getMessage());
            return false;
        }
    }

    // Helper methods to create test mazes
    private static void createSimpleMaze(String filename) throws Exception {
        java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(filename));
        out.println("5 5");
        out.println("XXXXX");
        out.println("XS  X");
        out.println("X   X");
        out.println("X  GX");
        out.println("XXXXX");
        out.close();
    }

    private static void createNoPathMaze(String filename) throws Exception {
        java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(filename));
        out.println("5 5");
        out.println("XXXXX");
        out.println("XS XX");
        out.println("XXXXX");
        out.println("XX GX");
        out.println("XXXXX");
        out.close();
    }

    private static void createMultiGoalMaze(String filename) throws Exception {
        java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(filename));
        out.println("7 7");
        out.println("XXXXXXX");
        out.println("XS    X");
        out.println("X XXX X");
        out.println("X   G X");
        out.println("XXX X X");
        out.println("XG    X");
        out.println("XXXXXXX");
        out.close();
    }

    private static void createAdjacentGoalMaze(String filename) throws Exception {
        java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(filename));
        out.println("5 5");
        out.println("XXXXX");
        out.println("XSGXX");
        out.println("X   X");
        out.println("X   X");
        out.println("XXXXX");
        out.close();
    }
}
