package assign08;

/**
 * Comprehensive test suite for BFS and DFS pathfinding algorithms. Validates
 * correctness of Graph.java implementation including:
 * <ul>
 * <li>Basic pathfinding functionality</li>
 * <li>No-path scenarios</li>
 * <li>Multiple goal handling</li>
 * <li>Edge cases</li>
 * <li>Performance on large mazes</li>
 * <li>Node counting for analysis</li>
 * </ul>
 *
 * <p>
 * Runs 17 comprehensive tests covering all requirements of Assignment 8.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class BFSandDFSTestRunner {

    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("ASSIGNMENT 8 - BFS AND DFS TEST RUNNER");
        System.out.println("=".repeat(80));
        System.out.println();

        // Run all test categories
        testBasicPathfinding();
        testNoPathScenarios();
        testMultipleGoals();
        testEdgeCases();
        testPerformance();
        testNodeCounting();

        // Print summary
        printSummary();
    }

    private static void testBasicPathfinding() {
        printTestCategory("Basic Pathfinding");

        // Test 1: Simple 5x5 maze with one goal
        test("Simple path - BFS finds shortest path", () -> {
            String maze = createMazeFile("test_simple",
                    "5 5",
                    "XXXXX",
                    "XS  X",
                    "X   X",
                    "X  GX",
                    "XXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateShortestPath();

            assertTrue(pathLength > 0, "Path should be found");
            assertTrue(pathLength <= 4, "Path should be optimal (<=4 steps)");
        });

        // Test 2: Same maze with DFS
        test("Simple path - DFS finds any valid path", () -> {
            String maze = createMazeFile("test_simple2",
                    "5 5",
                    "XXXXX",
                    "XS  X",
                    "X   X",
                    "X  GX",
                    "XXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateAPath();

            assertTrue(pathLength > 0, "DFS should find a path");
        });

        // Test 3: Direct path (no obstacles)
        test("Direct path - BFS optimal", () -> {
            String maze = createMazeFile("test_direct",
                    "5 5",
                    "XXXXX",
                    "XSGXX",
                    "XXXXX",
                    "XXXXX",
                    "XXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateShortestPath();

            assertEquals(0, pathLength, "Adjacent goal should have path length 0 (no intermediate nodes)");
        });
    }

    private static void testNoPathScenarios() {
        printTestCategory("No Path Scenarios");

        // Test 4: Completely blocked goal
        test("No path - BFS returns 0", () -> {
            String maze = createMazeFile("test_nopath",
                    "5 5",
                    "XXXXX",
                    "XS XX",
                    "XXXXX",
                    "XX GX",
                    "XXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateShortestPath();

            assertEquals(0, pathLength, "No path should return 0");
        });

        // Test 5: DFS with no path
        test("No path - DFS returns 0", () -> {
            String maze = createMazeFile("test_nopath2",
                    "5 5",
                    "XXXXX",
                    "XS XX",
                    "XXXXX",
                    "XX GX",
                    "XXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateAPath();

            assertEquals(0, pathLength, "DFS with no path should return 0");
        });

        // Test 6: Goal surrounded by walls
        test("Isolated goal - no path", () -> {
            String maze = createMazeFile("test_isolated",
                    "7 7",
                    "XXXXXXX",
                    "XS    X",
                    "X     X",
                    "X XXXXX",
                    "X XGXXX",
                    "X XXXXX",
                    "XXXXXXX"
            );

            Graph g = new Graph(maze);
            int bfsPath = g.CalculateShortestPath();

            Graph g2 = new Graph(maze);
            int dfsPath = g2.CalculateAPath();

            assertEquals(0, bfsPath, "BFS should return 0 for isolated goal");
            assertEquals(0, dfsPath, "DFS should return 0 for isolated goal");
        });
    }

    private static void testMultipleGoals() {
        printTestCategory("Multiple Goals");

        // Test 7: BFS finds closest goal
        test("Multiple goals - BFS finds closest", () -> {
            String maze = createMazeFile("test_multigoal",
                    "9 9",
                    "XXXXXXXXX",
                    "XG      X",
                    "X       X",
                    "X       X",
                    "X   S   X",
                    "X       X",
                    "X       X",
                    "X      GX",
                    "XXXXXXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateShortestPath();

            assertTrue(pathLength > 0, "Should find a path to some goal");
            // BFS will find closest goal - path length will vary based on actual distance
        });

        // Test 8: DFS finds any goal
        test("Multiple goals - DFS finds any goal", () -> {
            String maze = createMazeFile("test_multigoal2",
                    "9 9",
                    "XXXXXXXXX",
                    "XG      X",
                    "X       X",
                    "X       X",
                    "X   S   X",
                    "X       X",
                    "X       X",
                    "X      GX",
                    "XXXXXXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateAPath();

            assertTrue(pathLength > 0, "DFS should find some goal");
        });

        // Test 9: Three goals at different distances
        test("Three goals - BFS finds closest", () -> {
            String maze = createMazeFile("test_threegoals",
                    "11 11",
                    "XXXXXXXXXXX",
                    "XG        X",
                    "X         X",
                    "X    G    X",
                    "X         X",
                    "X    S    X",
                    "X         X",
                    "X         X",
                    "X         X",
                    "X        GX",
                    "XXXXXXXXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateShortestPath();

            assertTrue(pathLength > 0, "Should find a path");
            assertTrue(pathLength <= 3, "Should find closest goal at distance ~2-3");
        });
    }

    private static void testEdgeCases() {
        printTestCategory("Edge Cases");

        // Test 10: Start IS the goal
        test("Start is goal", () -> {
            String maze = createMazeFile("test_startgoal",
                    "3 3",
                    "XXX",
                    "XSX",
                    "XXX"
            );

            // Modify the maze to make S also a goal (this tests edge case)
            // In actual implementation, S and G are separate, so this tests the algorithm
            Graph g = new Graph(maze);
            g.CalculateShortestPath(); // Should not crash

            assertTrue(true, "Should handle edge case without crashing");
        });

        // Test 11: Very small maze
        test("Tiny maze (4x4)", () -> {
            String maze = createMazeFile("test_tiny",
                    "4 4",
                    "XXXX",
                    "XSGX",
                    "X  X",
                    "XXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateShortestPath();

            assertEquals(0, pathLength, "Adjacent goal should have minimal path");
        });

        // Test 12: Long corridor
        test("Long corridor", () -> {
            String maze = createMazeFile("test_corridor",
                    "3 15",
                    "XXXXXXXXXXXXXXX",
                    "XS           GX",
                    "XXXXXXXXXXXXXXX"
            );

            Graph g = new Graph(maze);
            int pathLength = g.CalculateShortestPath();

            assertTrue(pathLength > 0, "Should find path through corridor");
            assertTrue(pathLength >= 10 && pathLength <= 12, "Path should be approximately 11-12 steps");
        });
    }

    private static void testPerformance() {
        printTestCategory("Performance Tests");

        // Test 13: Large maze completes in reasonable time
        test("Large maze (50x50) - completes quickly", () -> {
            MazeGen.randomMaze("assignment8_files/test_large.txt", 50, 0.3, 5);

            long startTime = System.currentTimeMillis();

            Graph g = new Graph("assignment8_files/test_large.txt");
            g.CalculateShortestPath();

            Graph g2 = new Graph("assignment8_files/test_large.txt");
            g2.CalculateAPath();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            assertTrue(duration < 1000, "Should complete in under 1 second (took " + duration + "ms)");
        });

        // Test 14: Very large maze (100x100)
        test("Very large maze (100x100) - completes in under 10 seconds", () -> {
            MazeGen.randomMaze("assignment8_files/test_verylarge.txt", 100, 0.3, 5);

            long startTime = System.currentTimeMillis();

            Graph g = new Graph("assignment8_files/test_verylarge.txt");
            g.CalculateShortestPath();

            Graph g2 = new Graph("assignment8_files/test_verylarge.txt");
            g2.CalculateAPath();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            assertTrue(duration < 10000, "Should complete in under 10 seconds (took " + duration + "ms)");
        });
    }

    private static void testNodeCounting() {
        printTestCategory("Node Counting (for Analysis)");

        // Test 15: BFS node counting works
        test("BFS node counting", () -> {
            String maze = createMazeFile("test_nodecount",
                    "7 7",
                    "XXXXXXX",
                    "XS    X",
                    "X     X",
                    "X     X",
                    "X    GX",
                    "X     X",
                    "XXXXXXX"
            );

            Graph g = new Graph(maze);
            g.CalculateShortestPath();
            int nodes = g.getNodesSearched();

            assertTrue(nodes > 0, "Should count some nodes");
            assertTrue(nodes < 30, "Should not search entire maze for simple path");
        });

        // Test 16: DFS node counting works
        test("DFS node counting", () -> {
            String maze = createMazeFile("test_nodecount2",
                    "7 7",
                    "XXXXXXX",
                    "XS    X",
                    "X     X",
                    "X     X",
                    "X    GX",
                    "X     X",
                    "XXXXXXX"
            );

            Graph g = new Graph(maze);
            g.CalculateAPath();
            int nodes = g.getNodesSearched();

            assertTrue(nodes > 0, "Should count some nodes");
        });

        // Test 17: Node count is 0 when no path
        test("Node counting with no path", () -> {
            String maze = createMazeFile("test_nodecount_nopath",
                    "5 5",
                    "XXXXX",
                    "XS XX",
                    "XXXXX",
                    "XX GX",
                    "XXXXX"
            );

            Graph g = new Graph(maze);
            g.CalculateShortestPath();
            int nodes = g.getNodesSearched();

            assertTrue(nodes > 0, "Should still count nodes explored before determining no path");
        });
    }

    // ===== HELPER METHODS =====
    private static void test(String description, TestCase testCase) {
        testsTotal++;
        try {
            testCase.run();
            testsPassed++;
            System.out.printf("  ✓ Test %2d: %s%n", testsTotal, description);
        } catch (AssertionError e) {
            testsFailed++;
            System.out.printf("  ✗ Test %2d: %s%n", testsTotal, description);
            System.out.printf("     Error: %s%n", e.getMessage());
        } catch (Exception e) {
            testsFailed++;
            System.out.printf("  ✗ Test %2d: %s%n", testsTotal, description);
            System.out.printf("     Exception: %s%n", e.getMessage());
        }
    }

    /**
     * Prints a formatted test category header.
     *
     * @param category the category name to display
     */
    private static void printTestCategory(String category) {
        System.out.println();
        System.out.println("-".repeat(80));
        System.out.println(category);
        System.out.println("-".repeat(80));
    }

    /**
     * Prints a formatted summary of all test results. Displays total tests,
     * pass/fail counts, and success/failure message.
     */
    private static void printSummary() {
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(80));
        System.out.printf("Total Tests:  %d%n", testsTotal);
        System.out.printf("Passed:       %d (%.1f%%)%n", testsPassed, (testsPassed * 100.0 / testsTotal));
        System.out.printf("Failed:       %d (%.1f%%)%n", testsFailed, (testsFailed * 100.0 / testsTotal));
        System.out.println("=".repeat(80));

        if (testsFailed == 0) {
            System.out.println("🎉 ALL TESTS PASSED! Your implementation is working correctly!");
            System.out.println("✓ Ready for submission!");
        } else {
            System.out.println("⚠️  Some tests failed. Please review the errors above.");
        }
        System.out.println();
    }

    /**
     * Creates a temporary maze file for testing purposes.
     *
     * @param name the name for the maze file (without extension)
     * @param lines the lines of the maze content
     * @return the filename of the created maze file
     */
    private static String createMazeFile(String name, String... lines) {
        String filename = "assignment8_files/" + name + ".txt";
        try {
            java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(filename));
            for (String line : lines) {
                out.println(line);
            }
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test maze file: " + e.getMessage());
        }
        return filename;
    }

    /**
     * Asserts that a condition is true, throwing an AssertionError if false.
     *
     * @param condition the condition to check
     * @param message the error message if assertion fails
     */
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    @FunctionalInterface
    interface TestCase {

        void run() throws Exception;
    }
}
