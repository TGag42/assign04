package assign08;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a 2D maze as a graph for pathfinding algorithms. Supports both
 * Breadth-First Search (BFS) and Depth-First Search (DFS) for finding paths
 * from a start position to goal positions.
 *
 * <p>
 * The maze is represented as a 2D grid where:
 * <ul>
 * <li>'X' represents walls (impassable)</li>
 * <li>'S' represents the start position</li>
 * <li>'G' represents goal positions</li>
 * <li>' ' (space) represents open paths</li>
 * </ul>
 *
 * @author Daniel Kopta (original structure)
 * @author Alex Waldmann (BFS/DFS implementation and node counting)
 * @author Tyler Gagliardi (BFS/DFS implementation and node counting)
 * @version November 6, 2025
 */
public class Graph {

    // The graph itself is just a 2D array of nodes
    private Node[][] nodes;

    // The node to start the path finding from
    private Node start;

    // The size of the maze
    private int width;
    private int height;

    // Counter for analysis: tracks how many nodes are searched
    private int nodesSearched;

    /**
     * Constructs a maze graph from the given text file.
     *
     * @param filename - the file containing the maze
     * @throws Exception
     */
    public Graph(String filename) throws Exception {
        BufferedReader input;
        input = new BufferedReader(new FileReader(filename));

        if (!input.ready()) {
            input.close();
            throw new FileNotFoundException();
        }

        // read the maze size from the file
        String[] dimensions = input.readLine().split(" ");
        height = Integer.parseInt(dimensions[0]);
        width = Integer.parseInt(dimensions[1]);

        // instantiate and populate the nodes
        nodes = new Node[height][width];
        for (int i = 0; i < height; i++) {
            String row = input.readLine().trim();

            for (int j = 0; j < row.length(); j++) {
                switch (row.charAt(j)) {
                    case 'X':
                        nodes[i][j] = new Node(i, j);
                        nodes[i][j].isWall = true;
                        break;
                    case ' ':
                        nodes[i][j] = new Node(i, j);
                        break;
                    case 'S':
                        nodes[i][j] = new Node(i, j);
                        nodes[i][j].isStart = true;
                        start = nodes[i][j];
                        break;
                    case 'G':
                        nodes[i][j] = new Node(i, j);
                        nodes[i][j].isGoal = true;
                        break;
                    default:
                        throw new IllegalArgumentException("maze contains unknown character: \'" + row.charAt(j) + "\'");
                }
            }
        }
        input.close();
    }

    /**
     * Outputs this graph to the specified file. Use this method after you have
     * found a path to one of the goals. Before using this method, for the nodes
     * on the path, you will need to set their isOnPath value to true.
     *
     * @param filename - the file to write to
     */
    public void printGraph(String filename) {
        try {
            PrintWriter output = new PrintWriter(new FileWriter(filename));
            output.println(height + " " + width);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    output.print(nodes[i][j]);
                }
                output.println();
            }
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helpers
    private void resetSearchState() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Node n = nodes[r][c];
                n.visited = false;
                n.parent = null;
                n.isOnPath = false;
            }
        }
    }

    private void addIfOpen(List<Node> out, int r, int c) {
        if (r < 0 || r >= height || c < 0 || c >= width) {
            return;
        }
        Node n = nodes[r][c];
        if (!n.isWall) {
            out.add(n);
        }
    }

    /**
     * 4-neighbors (no diagonals). Order: Up, Left, Down, Right.
     */
    private List<Node> neighbors(Node n) {
        List<Node> out = new ArrayList<>(4);
        int r = n.row, c = n.col;
        addIfOpen(out, r - 1, c); // Up
        addIfOpen(out, r, c - 1); // Left
        addIfOpen(out, r + 1, c); // Down
        addIfOpen(out, r, c + 1); // Right
        return out;
    }

    /**
     * Walk parents back and flag '.' on open spaces (never overwrites S/G/X).
     * Returns path length (counting only dots).
     */
    private int markPath(Node end) {
        int len = 0;
        for (Node cur = end; cur != null; cur = cur.parent) {
            if (!cur.isStart && !cur.isGoal && !cur.isWall) {
                cur.isOnPath = true;
                len++;
            }
        }
        return len;
    }
    // ------------------------------------------------------

    /**
     * Traverses the graph using Breadth-First Search (BFS) to find the shortest
     * path to the closest goal. BFS explores nodes level-by-level, guaranteeing
     * the shortest path is found.
     *
     * <p>
     * A side-effect of this method is that nodes on the path will have their
     * isOnPath member set to true, allowing the path to be visualized when the
     * graph is printed.
     *
     * <p>
     * For analysis purposes, this method counts nodes as they are pulled off
     * the queue. The count can be retrieved with {@link #getNodesSearched()}.
     *
     * @return the length of the path (number of intermediate nodes, not
     * including start and goal), or 0 if no path exists to any goal
     */
    public int CalculateShortestPath() {
        resetSearchState();
        nodesSearched = 0;
        if (start == null) {
            return 0;
        }

        java.util.Queue<Node> q = new LinkedList<>();
        start.visited = true;
        q.add(start);

        Node found = null;

        while (!q.isEmpty()) {
            Node cur = q.remove();
            nodesSearched++; // Count node when pulled off queue (BFS)
            if (cur.isGoal) {
                found = cur;
                break;
            }
            for (Node nxt : neighbors(cur)) {
                if (!nxt.visited) {
                    nxt.visited = true;
                    nxt.parent = cur;
                    q.add(nxt);
                }
            }
        }

        if (found == null) {
            return 0;
        }
        return markPath(found);
    }

    /**
     * Traverses the graph using Depth-First Search (DFS) to find any path to
     * any goal. DFS explores deeply in one direction before backtracking, which
     * may result in finding a longer path than BFS would find.
     *
     * <p>
     * A side-effect of this method is that nodes on the path will have their
     * isOnPath member set to true, allowing the path to be visualized when the
     * graph is printed.
     *
     * <p>
     * For analysis purposes, this method counts nodes as they are popped off
     * the stack. The count can be retrieved with {@link #getNodesSearched()}.
     *
     * <p>
     * Note: The path found depends on the neighbor visiting order. The current
     * implementation visits neighbors in the order: Up, Left, Down, Right.
     *
     * @return the length of the path (number of intermediate nodes, not
     * including start and goal), or 0 if no path exists to any goal
     */
    public int CalculateAPath() {
        resetSearchState();
        nodesSearched = 0;
        if (start == null) {
            return 0;
        }

        Deque<Node> st = new java.util.ArrayDeque<>();
        start.visited = true;
        st.push(start);

        Node found = null;

        while (!st.isEmpty()) {
            Node cur = st.pop();
            nodesSearched++; // Count node when popped off stack (DFS)
            if (cur.isGoal) {
                found = cur;
                break;
            }
            for (Node nxt : neighbors(cur)) {
                if (!nxt.visited) {
                    nxt.visited = true;
                    nxt.parent = cur;
                    st.push(nxt);
                }
            }
        }

        if (found == null) {
            return 0;
        }
        return markPath(found);
    }

    /**
     * Gets the number of nodes searched during the last pathfinding operation.
     * This count is used for algorithm analysis and performance comparison.
     *
     * <p>
     * For BFS: counts nodes as they are pulled off the queue (when they are
     * examined for being a goal or having their neighbors added).
     *
     * <p>
     * For DFS: counts nodes as they are popped off the stack (when they are
     * examined for being a goal or having their neighbors added).
     *
     * <p>
     * The count is reset to 0 at the start of each pathfinding operation.
     *
     * @return the number of nodes searched in the last pathfinding operation,
     * or 0 if no pathfinding has been performed
     */
    public int getNodesSearched() {
        return nodesSearched;
    }

    /**
     * Represents a single node/cell in the maze graph. Each node knows its
     * position, type (wall, start, goal, or open space), and maintains state
     * for pathfinding algorithms.
     *
     * @author Daniel Kopta (original structure)
     * @author Alex Waldmann (pathfinding state fields)
     * @author Tyler Gagliardi (pathfinding state fields)
     */
    private static class Node {
        // The node's position in the maze

        private int row, col;

        // The type of the node
        private boolean isStart;
        private boolean isGoal;
        private boolean isOnPath;
        private boolean isWall;

        // minimal additions for search
        private boolean visited;
        private Node parent;

        public Node(int r, int c) {
            isStart = false;
            isGoal = false;
            isOnPath = false;
            row = r;
            col = c;
        }

        @Override
        public String toString() {
            if (isWall) {
                return "X";
            }
            if (isStart) {
                return "S";
            }
            if (isGoal) {
                return "G";
            }
            if (isOnPath) {
                return ".";
            }
            return " ";
        }
    }

}
