package assign08;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Analysis tool for comparing BFS and DFS pathfinding performance. This tool
 * helps answer the analysis questions for Assignment 8:
 * <ul>
 * <li>Question 2: Analyzes DFS neighbor visiting order behavior</li>
 * <li>Question 5: Compares BFS vs DFS node search efficiency</li>
 * </ul>
 *
 * <p>
 * Generates detailed reports showing:
 * <ul>
 * <li>Path lengths found by each algorithm</li>
 * <li>Number of nodes searched</li>
 * <li>Performance comparison statistics</li>
 * </ul>
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class PathfindingAnalysis {

    public static void main(String[] args) {
        System.out.println("Assignment 8 - Pathfinding Analysis Tool");
        System.out.println("=".repeat(70));

        // Question 2: Test neighbor visiting order on multi-goal maze
        System.out.println("\nQuestion 2: DFS Neighbor Visiting Order Analysis");
        System.out.println("-".repeat(70));
        analyzeNeighborOrder("assignment8_files/multiGoalMaze.txt");

        // Question 5: Compare BFS vs DFS on random mazes
        System.out.println("\n\nQuestion 5: BFS vs DFS Performance Comparison");
        System.out.println("-".repeat(70));
        compareAlgorithms();
    }

    /**
     * Tests different neighbor visiting orders to analyze DFS behavior.
     * Determines which order causes DFS to find a non-closest goal.
     *
     * @param mazeFile the maze file to analyze
     */
    private static void analyzeNeighborOrder(String mazeFile) {
        System.out.println("Testing maze: " + mazeFile);
        System.out.println("\nCurrent neighbor order: Up, Left, Down, Right");

        try {
            // Test BFS first to establish shortest path
            Graph gBFS = new Graph(mazeFile);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();
            System.out.println("\nBFS Results:");
            System.out.println("  Path length: " + bfsPath);
            System.out.println("  Nodes searched: " + bfsNodes);
            gBFS.printGraph(mazeFile.replace(".txt", "_BFS.txt"));

            // Test DFS
            Graph gDFS = new Graph(mazeFile);
            int dfsPath = gDFS.CalculateAPath();
            int dfsNodes = gDFS.getNodesSearched();
            System.out.println("\nDFS Results:");
            System.out.println("  Path length: " + dfsPath);
            System.out.println("  Nodes searched: " + dfsNodes);
            gDFS.printGraph(mazeFile.replace(".txt", "_DFS.txt"));

            if (dfsPath > bfsPath) {
                System.out.println("\n✓ DFS found a longer path (not closest goal)");
            } else if (dfsPath == bfsPath) {
                System.out.println("\n  DFS found same length path as BFS");
            } else {
                System.out.println("\n  Note: DFS path is shorter (unusual)");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates random mazes and compares BFS vs DFS performance
     */
    private static void compareAlgorithms() {
        int numTrials = 10;
        int mazeSize = 50;
        double wallDensity = 0.3;
        int numGoals = 5;

        System.out.println("Experiment Configuration:");
        System.out.println("  Maze size: " + mazeSize + "x" + mazeSize);
        System.out.println("  Wall density: " + (wallDensity * 100) + "%");
        System.out.println("  Number of goals: " + numGoals);
        System.out.println("  Number of trials: " + numTrials);
        System.out.println();

        int bfsTotalNodes = 0;
        int dfsTotalNodes = 0;
        int bfsWins = 0;
        int dfsWins = 0;
        int ties = 0;

        try (PrintWriter csvOut = new PrintWriter(new FileWriter("assignment8_files/analysis_results.csv"))) {
            csvOut.println("Trial,BFS_Nodes,DFS_Nodes,BFS_Path,DFS_Path,Winner");

            for (int i = 1; i <= numTrials; i++) {
                String mazeFile = "assignment8_files/random_maze_" + i + ".txt";

                // Generate random maze
                try {
                    MazeGen.randomMaze(mazeFile, mazeSize, wallDensity, numGoals);
                } catch (Exception e) {
                    System.out.println("Warning: Could not generate maze " + i);
                    continue;
                }

                try {
                    // Test BFS
                    Graph gBFS = new Graph(mazeFile);
                    int bfsPath = gBFS.CalculateShortestPath();
                    int bfsNodes = gBFS.getNodesSearched();

                    // Test DFS
                    Graph gDFS = new Graph(mazeFile);
                    int dfsPath = gDFS.CalculateAPath();
                    int dfsNodes = gDFS.getNodesSearched();

                    bfsTotalNodes += bfsNodes;
                    dfsTotalNodes += dfsNodes;

                    String winner;
                    if (bfsNodes < dfsNodes) {
                        bfsWins++;
                        winner = "BFS";
                    } else if (dfsNodes < bfsNodes) {
                        dfsWins++;
                        winner = "DFS";
                    } else {
                        ties++;
                        winner = "TIE";
                    }

                    csvOut.printf("%d,%d,%d,%d,%d,%s%n", i, bfsNodes, dfsNodes, bfsPath, dfsPath, winner);
                    System.out.printf("Trial %2d: BFS=%4d nodes, DFS=%4d nodes [%s wins]%n",
                            i, bfsNodes, dfsNodes, winner);

                } catch (Exception e) {
                    System.out.println("Error processing maze " + i + ": " + e.getMessage());
                }
            }

            System.out.println("\n" + "=".repeat(70));
            System.out.println("RESULTS SUMMARY");
            System.out.println("=".repeat(70));
            System.out.printf("Average BFS nodes searched: %.1f%n", bfsTotalNodes / (double) numTrials);
            System.out.printf("Average DFS nodes searched: %.1f%n", dfsTotalNodes / (double) numTrials);
            System.out.println("\nComparison:");
            System.out.println("  BFS searched fewer nodes: " + bfsWins + " times");
            System.out.println("  DFS searched fewer nodes: " + dfsWins + " times");
            System.out.println("  Tied: " + ties + " times");

            if (bfsTotalNodes < dfsTotalNodes) {
                double percent = ((dfsTotalNodes - bfsTotalNodes) / (double) dfsTotalNodes) * 100;
                System.out.printf("\n✓ BFS was %.1f%% more efficient on average%n", percent);
            } else if (dfsTotalNodes < bfsTotalNodes) {
                double percent = ((bfsTotalNodes - dfsTotalNodes) / (double) bfsTotalNodes) * 100;
                System.out.printf("\n✓ DFS was %.1f%% more efficient on average%n", percent);
            } else {
                System.out.println("\n  Both algorithms were equally efficient on average");
            }

            System.out.println("\n✓ Detailed results saved to assignment8_files/analysis_results.csv");

        } catch (Exception e) {
            System.out.println("Error writing results: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
