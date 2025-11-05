package assign08;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * Represents a maze as a graph for pathfinding.
 */
public class Graph {

	private Node[][] nodes;
	private Node start;
	private int width;
	private int height;
	
	/** Builds the graph from an input file. */
	public Graph(String filename) throws Exception {
		BufferedReader input = new BufferedReader(new FileReader(filename));
		if (!input.ready()) {
			input.close();
			throw new FileNotFoundException();
		}

		String[] dimensions = input.readLine().split(" ");
		height = Integer.parseInt(dimensions[0]);
		width = Integer.parseInt(dimensions[1]);

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
					throw new IllegalArgumentException("maze contains unknown character: '" + row.charAt(j) + "'");
				}
			}
		}
		input.close();
	}
	
	/** Writes the maze to a file. */
	public void printGraph(String filename) {
		try {
			PrintWriter output = new PrintWriter(new FileWriter(filename));
			output.println(height + " " + width);
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++)
					output.print(nodes[i][j]);
				output.println();
			}
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Finds shortest path to nearest goal using BFS. */
	public int CalculateShortestPath() {
		if (start == null) return -1;
		reset();
		Queue<Node> q = new ArrayDeque<>();
		start.visited = true;
		q.add(start);

		while (!q.isEmpty()) {
			Node cur = q.remove();
			if (cur.isGoal)
				return markPath(cur);
			for (Node nb : neighbors(cur)) {
				if (nb != null && !nb.isWall && !nb.visited) {
					nb.visited = true;
					nb.parent = cur;
					q.add(nb);
				}
			}
		}
		return -1;
	}

	/** Finds any path to any goal using DFS. */
	public int CalculateAPath() {
		if (start == null) return -1;
		reset();
		Deque<Node> stack = new ArrayDeque<>();
		start.visited = true;
		stack.push(start);

		while (!stack.isEmpty()) {
			Node cur = stack.pop();
			if (cur.isGoal)
				return markPath(cur);
			for (Node nb : neighbors(cur)) {
				if (nb != null && !nb.isWall && !nb.visited) {
					nb.visited = true;
					nb.parent = cur;
					stack.push(nb);
				}
			}
		}
		return -1;
	}

	/** Clears search flags. */
	private void reset() {
		for (int r = 0; r < height; r++)
			for (int c = 0; c < width; c++) {
				Node n = nodes[r][c];
				n.visited = false;
				n.parent = null;
				n.isOnPath = false;
			}
	}

	/** Gets valid neighbors (up, down, left, right). */
	private Node[] neighbors(Node n) {
		int r = n.row, c = n.col;
		Node up = inBounds(r - 1, c) ? nodes[r - 1][c] : null;
		Node dn = inBounds(r + 1, c) ? nodes[r + 1][c] : null;
		Node lf = inBounds(r, c - 1) ? nodes[r][c - 1] : null;
		Node rt = inBounds(r, c + 1) ? nodes[r][c + 1] : null;
		return new Node[] { up, dn, lf, rt };
	}

	private boolean inBounds(int r, int c) {
		return r >= 0 && r < height && c >= 0 && c < width;
	}

	/** Marks found path and returns edge length. */
	private int markPath(Node goal) {
		int count = 0;
		for (Node cur = goal; cur != null; cur = cur.parent) {
			cur.isOnPath = true;
			count++;
		}
		return count - 1;
	}

	/** Represents a cell in the maze. */
	private static class Node {
		private int row, col;
		private boolean isStart, isGoal, isOnPath, isWall;
		private boolean visited;
		private Node parent;

		public Node(int r, int c) {
			row = r;
			col = c;
		}

		public String toString() {
			if (isWall) return "X";
			if (isStart) return "S";
			if (isGoal) return "G";
			if (isOnPath) return ".";
			return " ";
		}
	}
}
