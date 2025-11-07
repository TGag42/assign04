package assign08;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Creates a CLEARER example for Question 3.
 * The goal: Show DFS finding a FARTHER goal with FEWER nodes than BFS.
 * 
 * Key insight: 
 * - BFS explores in all directions evenly, so it gets "trapped" exploring many branches
 * - DFS goes deep in one direction, so if the farther goal is in that direction, it finds it quickly
 * 
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 6, 2025
 */
public class CreateClearQ3Example {
    
    public static void main(String[] args) {
        createImprovedQ3Example();
    }
    
    private static void createImprovedQ3Example() {
        System.out.println("Creating CLEARER Question 3 Example:");
        System.out.println("=" .repeat(70));
        
        try {
            String filename = "assignment8_files/q3_example.txt";
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            
            // Improved maze design:
            // - Start at top
            // - Closer goal to the RIGHT (distance 3) but surrounded by walls that force BFS to explore
            // - Farther goal STRAIGHT DOWN (distance 6) - direct path for DFS
            out.println("15 15");
            out.println("XXXXXXXXXXXXXXX");
            out.println("XS            X");  // Start at row 1, col 1
            out.println("X XXXXXXXXXXX X");  // Wall blocking right
            out.println("X X         X X");
            out.println("X X XXXXXXX X X");
            out.println("X X X  G  X X X");  // Goal 1 (closer, col 7, row 5) - distance ~3-4, but BFS explores maze
            out.println("X X XXXXXXX X X");
            out.println("X X         X X");
            out.println("X XXXXXXXXXXX X");
            out.println("X             X");  // Open corridor down from start
            out.println("X             X");
            out.println("X             X");
            out.println("X             X");
            out.println("X      G      X");  // Goal 2 (farther, col 7, row 13) - distance 12 straight down
            out.println("XXXXXXXXXXXXXXX");
            out.close();
            
            System.out.println("✓ Created: " + filename);
            System.out.println();
            System.out.println("Maze Concept:");
            System.out.println("-------------");
            System.out.println("S = Start (top left)");
            System.out.println("G = Goal 1 (middle, trapped in maze) - CLOSER but hard to reach");
            System.out.println("G = Goal 2 (bottom, straight corridor) - FARTHER but easy path");
            System.out.println();
            System.out.println("Expected Behavior:");
            System.out.println("-----------------");
            System.out.println("BFS: Explores level-by-level, gets stuck exploring the maze structure");
            System.out.println("     around Goal 1. Must check many cells in all directions.");
            System.out.println("     Expected nodes: ~40-60");
            System.out.println();
            System.out.println("DFS: With Down-first order, goes straight down the corridor to Goal 2");
            System.out.println("     Direct path, minimal exploration.");
            System.out.println("     Expected nodes: ~12-15");
            System.out.println();
            System.out.println("Result: DFS finds FARTHER goal (Goal 2) with FEWER nodes!");
            System.out.println();
            
            // Test it
            System.out.println("Testing with current implementation...");
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
                System.out.println("⚠️  Note: Results depend on neighbor order.");
                System.out.println("   For best demonstration, use Down-first neighbor order.");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
