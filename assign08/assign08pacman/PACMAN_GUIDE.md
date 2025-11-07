# Pacman Visualization Tool - User Guide

## Authors
- Alex Waldmann
- Tyler Gagliardi

## Overview

The Pacman visualization tool helps you visualize your maze solutions and verify that your pathfinding algorithms work correctly. This guide shows you how to use the tool and generate images for your analysis answers.

---

## Setup

### Location
The pacman tool is located at:
```
./school/src/main/java/assign08pacman/
```

### Python Requirement
The tool requires Python 3. Check if you have it installed:
```bash
python3 --version
```

If not installed, download from: https://www.python.org/downloads/

### Optional: Install Pillow for PNG Export
To generate PNG images of your mazes:
```bash
pip3 install Pillow
```

---

## Basic Usage

### View a Maze (Static)
```bash
cd "src/main/java/assign08pacman"
python3 pacman.py -l <mazefile>
```

Example:
```bash
python3 pacman.py -l bigMaze_multigoal.txt
```

This displays the maze without pacman moving. You can examine the path layout.

### Auto Pacman (Animated)
To watch pacman automatically follow the path:
```bash
python3 pacman.py -l <mazefile> -p auto
```

Example:
```bash
python3 pacman.py -l bigMazeSol.txt -p auto
```

**Note:** Auto mode only works on mazes with a single continuous path (not multiple goals or open mazes).

### Zoom Controls
Adjust the window size:
```bash
# Make window smaller (50%)
python3 pacman.py -l bigMaze.txt -z 0.5

# Make window larger (200%)
python3 pacman.py -l bigMaze.txt -z 2
```

---

## Generating PNG Images for Analysis

### Method 1: Using the Capture Script (Recommended)

We've created a helper script to generate PNG images:

```bash
cd "src/main/java/assign08pacman"
python3 capture_maze_image.py <maze_file> <output_png>
```

Examples:
```bash
# Question 2: bigMaze_multigoal
python3 capture_maze_image.py bigMaze_multigoal.txt q2_bfs.png
python3 capture_maze_image.py bigMaze_multigoal_DFS.txt q2_dfs.png

# Question 3: Example maze
python3 capture_maze_image.py q3_example.txt q3_example.png

# Question 4: Example maze
python3 capture_maze_image.py q4_example.txt q4_example.png
```

### Method 2: Manual Screenshot

If the capture script doesn't work:

1. Run the pacman visualization:
   ```bash
   python3 pacman.py -l <mazefile> -z 2
   ```

2. Take a screenshot:
   - **Mac:** Cmd + Shift + 4, then select the window
   - **Windows:** Windows + Shift + S
   - **Linux:** Use screenshot tool (varies by distro)

3. Save with descriptive name (e.g., `bigMaze_multigoal_BFS.png`)

---

## Visualizing Your Test Results

### Step 1: Generate Solution Files

Your Graph.java already generates solution files when you call `printGraph()`:

```java
Graph g = new Graph("assignment8_files/bigMaze_multigoal.txt");
g.CalculateShortestPath();  // BFS
g.printGraph("assignment8_files/bigMaze_multigoal_BFS.txt");
```

### Step 2: Copy to Pacman Directory

Copy the solution file to the pacman directory:
```bash
cp "src/test/java/assignment8_files/bigMaze_multigoal_BFS.txt" \
   "src/main/java/assign08pacman/bigMaze_multigoal_BFS.txt"
```

### Step 3: Visualize

```bash
cd "src/main/java/assign08pacman"
python3 pacman.py -l bigMaze_multigoal_BFS.txt -z 1.5
```

### Step 4: Capture Image

```bash
python3 capture_maze_image.py bigMaze_multigoal_BFS.txt bigMaze_BFS.png
```

---

## Creating Images for All Analysis Questions

Here's a complete workflow to generate all images needed:

```bash
#!/bin/bash
# Run this from: src/main/java/assign08pacman/

# Question 2: BFS vs DFS on bigMaze_multigoal
cp ../../test/java/assignment8_files/bigMaze_multigoal.txt .
python3 capture_maze_image.py bigMaze_multigoal.txt q2_original.png

# If you have solution files:
cp ../../test/java/assignment8_files/bigMaze_multigoal_BFS.txt .
cp ../../test/java/assignment8_files/bigMaze_multigoal_DFS.txt .
python3 capture_maze_image.py bigMaze_multigoal_BFS.txt q2_bfs.png
python3 capture_maze_image.py bigMaze_multigoal_DFS.txt q2_dfs.png

# Question 3: Example maze
cp ../../test/java/assignment8_files/q3_example.txt .
python3 capture_maze_image.py q3_example.txt q3_example.png

# Question 4: Example maze
cp ../../test/java/assignment8_files/q4_example.txt .
python3 capture_maze_image.py q4_example.txt q4_example.png

echo "✓ All images generated!"
```

Save this as `generate_all_images.sh` and run:
```bash
chmod +x generate_all_images.sh
./generate_all_images.sh
```

---

## Maze File Format

Understanding the maze format helps with debugging:

```
<height> <width>
<maze row 1>
<maze row 2>
...
<maze row height>
```

Example:
```
5 7
XXXXXXX
XS   GX
X XXX X
X     X
XXXXXXX
```

**Legend:**
- `X` = Wall
- `S` = Start
- `G` = Goal
- ` ` (space) = Open path
- `.` = Path taken by algorithm

---

## Troubleshooting

### "python3: command not found"
Install Python 3 from python.org or use `python` instead of `python3`.

### "No module named 'PIL'"
Install Pillow:
```bash
pip3 install Pillow
```

### "FileNotFoundError"
Make sure you're in the correct directory:
```bash
cd "src/main/java/assign08pacman"
```

And the maze file exists in that directory.

### Pacman window too small/large
Use the `-z` flag to adjust zoom:
```bash
python3 pacman.py -l bigMaze.txt -z 1.5  # 150% size
```

### Auto mode doesn't work
Auto mode only works on:
- Mazes with a single goal
- Paths where each dot has exactly 2 neighbors

For multi-goal mazes, use static visualization (without `-p auto`).

---

## Tips for Analysis Answers

### Best Practices

1. **Use descriptive filenames:**
   - `q2_bigMaze_BFS.png`
   - `q2_bigMaze_DFS.png`
   - `q3_example_farther_goal.png`

2. **Zoom appropriately:**
   - Small mazes (5x5): Use `-z 3` or `-z 4`
   - Medium mazes (20x20): Use `-z 1.5`
   - Large mazes (50x50+): Use `-z 0.8` or smaller

3. **Annotate your images:**
   - Add labels in your document pointing out key features
   - Highlight the start, goals, and path differences

4. **Include multiple views:**
   - Show the original maze
   - Show BFS solution
   - Show DFS solution
   - This makes comparison easier

---

## Example Workflow for Question 2

Complete example for generating Question 2 images:

```bash
# Navigate to pacman directory
cd "/Users/alexwaldmann/Desktop/School/Fall 2025/CS 2420/school/src/main/java/assign08pacman"

# Copy maze files from test directory
cp ../../test/java/assignment8_files/bigMaze_multigoal.txt .

# Run your analysis to generate solution files (if not already done)
cd ../../../test/java
java -cp ".:../../main/java" assign08.Question2_NeighborOrderTester

# Copy solution files to pacman directory
cp assignment8_files/bigMaze_multigoal_BFS.txt ../../main/java/assign08pacman/
cp assignment8_files/bigMaze_multigoal_DFS.txt ../../main/java/assign08pacman/

# Return to pacman directory
cd ../../main/java/assign08pacman

# Generate PNG images
python3 capture_maze_image.py bigMaze_multigoal.txt q2_original.png
python3 capture_maze_image.py bigMaze_multigoal_BFS.txt q2_bfs_solution.png
python3 capture_maze_image.py bigMaze_multigoal_DFS.txt q2_dfs_solution.png

echo "✓ Question 2 images generated!"
```

---

## Summary

- ✅ Pacman tool located at `src/main/java/assign08pacman/`
- ✅ Basic usage: `python3 pacman.py -l <maze>`
- ✅ Auto mode: `python3 pacman.py -l <maze> -p auto`
- ✅ Zoom: `python3 pacman.py -l <maze> -z <factor>`
- ✅ PNG export: `python3 capture_maze_image.py <maze> <output.png>`
- ✅ Copy test mazes to pacman directory before visualizing
- ✅ Use descriptive filenames for analysis images

**Ready to visualize your mazes!** 🎮
