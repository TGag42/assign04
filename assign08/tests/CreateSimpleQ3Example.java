package assign08;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Creates a SIMPLE and CLEAR example for Question 3.
 * Both goals must be reachable. The concept:
 * - BFS finds CLOSER goal but searches MANY nodes (explores all branches)
 * - DFS finds FARTHER goal but searches FEW nodes (goes straight to it)
 * 
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 */
public class CreateSimpleQ3Example {
    
    public static void main(String[] args) {
        System.out.println("Creating SIMPLE Question 3 Example:");
        System.out.println("=" .repeat(70));
        
        try {
            String filename = "assignment8_files/q3_example.txt";
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            
            // Simple 11x11 maze:
            // Start at top
            // Closer goal to the RIGHT (requires exploring branches)
            // Farther goal STRAIGHT DOWN (direct path)
            out.println("11 11");
            out.println("XXXXXXXXXXX");
            out.println("XS        X");  // Start at (1,1)
            out.println("X X X X X X");  // Walls creating branches to the right
            out.println("X   G     X");  // Goal 1 (closer) at (3, 4) - distance 3
            out.println("X X X X X X");  // More walls
            out.println("X         X");  // Open path
            out.println("X         X");  // Continuing down
            out.println("X         X");
            out.println("X         X");
            out.println("X    G    X");  // Goal 2 (farther) at (9, 5) - distance 8
            out.println("XXXXXXXXXXX");
            out.close();
            
            System.out.println("✓ Created: " + filename);
            System.out.println();
            System.out.println("Maze Layout:");
            System.out.println("XXXXXXXXXXX");
            System.out.println("XS        X  ← Start (top left)");
            System.out.println("X X X X X X  ← Walls creating branches");
            System.out.println("X   G     X  ← Goal 1 (CLOSER - distance 3)");
            System.out.println("X X X X X X  ← More obstacles");
            System.out.println("X         X");
            System.out.println("X         X  ← Open corridor down");
            System.out.println("X         X");
            System.out.println("X         X");
            System.out.println("X    G    X  ← Goal 2 (FARTHER - distance 8)");
            System.out.println("XXXXXXXXXXX");
            System.out.println();
            System.out.println("Key Points:");
            System.out.println("- Both goals are reachable from start");
            System.out.println("- Goal 1 is closer (3 steps) but to the right");
            System.out.println("- Goal 2 is farther (8 steps) but straight down");
            System.out.println("- Walls around Goal 1 force BFS to explore many cells");
            System.out.println("- Open path to Goal 2 lets DFS go straight there");
            System.out.println();
            
            // Test it
            System.out.println("Testing...");
            Graph gBFS = new Graph(filename);
            int bfsPath = gBFS.CalculateShortestPath();
            int bfsNodes = gBFS.getNodesSearched();
            gBFS.printGraph("assignment8_files/q3_example_BFS.txt");
            
            Graph gDFS = new Graph(filename);
            int dfsPath = gDFS.CalculateAPath();
            int dfsNodes = gDFS.getNodesSearched();
            gDFS.printGraph("assignment8_files/q3_example_DFS.txt");
            
            System.out.println("BFS: path=" + bfsPath + ", nodes=" + bfsNodes + " (finds Goal 1 - closer)");
            System.out.println("DFS: path=" + dfsPath + ", nodes=" + dfsNodes + " (finds Goal 2 - farther)");
            System.out.println();
            
            if (dfsPath > bfsPath && dfsNodes < bfsNodes) {
                System.out.println("✓ SUCCESS! DFS found farther goal with fewer nodes!");
                System.out.println("  DFS path is " + (dfsPath - bfsPath) + " steps longer");
                System.out.println("  but DFS searched " + (bfsNodes - dfsNodes) + " fewer nodes!");
            } else if (dfsNodes < bfsNodes) {
                System.out.println("✓ DFS searched fewer nodes (" + dfsNodes + " vs " + bfsNodes + ")");
                System.out.println("  Path lengths: BFS=" + bfsPath + ", DFS=" + dfsPath);
            } else {
                System.out.println("⚠️  Note: Results depend on neighbor order.");
                System.out.println("   Current order may not demonstrate the concept optimally.");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
