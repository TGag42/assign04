#!/bin/bash
# generate_analysis_images.sh
# Generates all PNG images needed for Assignment 8 analysis questions
# 
# Authors: Alex Waldmann & Tyler Gagliardi
# Date: November 6, 2025

echo "=========================================="
echo "Assignment 8 - Analysis Image Generator"
echo "=========================================="
echo ""

# Get the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PACMAN_DIR="$SCRIPT_DIR"
TEST_DIR="$SCRIPT_DIR/../../../test/java/assignment8_files"

# Check if we're in the right directory
if [ ! -f "pacman.py" ]; then
    echo "❌ Error: Must run this script from the pacman directory"
    echo "   Expected location: src/main/java/assign08pacman/"
    exit 1
fi

# Check if Python 3 is available
if ! command -v python3 &> /dev/null; then
    echo "❌ Error: python3 not found. Please install Python 3."
    exit 1
fi

# Check if Pillow is installed
python3 -c "from PIL import Image" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "⚠️  Warning: Pillow not installed. Installing now..."
    pip3 install Pillow
    if [ $? -ne 0 ]; then
        echo "❌ Error: Failed to install Pillow. Please run: pip3 install Pillow"
        exit 1
    fi
fi

echo "✓ Python 3 and Pillow are installed"
echo ""

# Create output directory
mkdir -p analysis_images

echo "Generating images for analysis questions..."
echo ""

# Question 2: bigMaze_multigoal BFS vs DFS
echo "📸 Question 2: Neighbor visiting order comparison"
if [ -f "$TEST_DIR/bigMaze_multigoal.txt" ]; then
    cp "$TEST_DIR/bigMaze_multigoal.txt" .
    python3 capture_maze_image.py bigMaze_multigoal.txt analysis_images/q2_original.png
    echo "   ✓ Generated q2_original.png"
fi

if [ -f "$TEST_DIR/bigMaze_multigoal_BFS.txt" ]; then
    cp "$TEST_DIR/bigMaze_multigoal_BFS.txt" .
    python3 capture_maze_image.py bigMaze_multigoal_BFS.txt analysis_images/q2_bfs.png
    echo "   ✓ Generated q2_bfs.png"
else
    echo "   ⚠️  bigMaze_multigoal_BFS.txt not found - run Question2_NeighborOrderTester first"
fi

if [ -f "$TEST_DIR/bigMaze_multigoal_DFS.txt" ]; then
    cp "$TEST_DIR/bigMaze_multigoal_DFS.txt" .
    python3 capture_maze_image.py bigMaze_multigoal_DFS.txt analysis_images/q2_dfs.png
    echo "   ✓ Generated q2_dfs.png"
else
    echo "   ⚠️  bigMaze_multigoal_DFS.txt not found - run Question2_NeighborOrderTester first"
fi
echo ""

# Question 3: Example maze where DFS finds farther goal with fewer nodes
echo "📸 Question 3: DFS farther goal, fewer nodes"
if [ -f "$TEST_DIR/q3_example.txt" ]; then
    cp "$TEST_DIR/q3_example.txt" .
    python3 capture_maze_image.py q3_example.txt analysis_images/q3_example.png
    echo "   ✓ Generated q3_example.png"
else
    echo "   ⚠️  q3_example.txt not found - run AnalysisExampleMazes first"
fi
echo ""

# Question 4: Example maze BFS O(1) vs DFS O(N)
echo "📸 Question 4: BFS O(1) vs DFS O(N)"
if [ -f "$TEST_DIR/q4_example.txt" ]; then
    cp "$TEST_DIR/q4_example.txt" .
    python3 capture_maze_image.py q4_example.txt analysis_images/q4_example.png
    echo "   ✓ Generated q4_example.png"
else
    echo "   ⚠️  q4_example.txt not found - run AnalysisExampleMazes first"
fi
echo ""

echo "=========================================="
echo "Summary"
echo "=========================================="
echo ""
echo "Images saved to: analysis_images/"
ls -lh analysis_images/*.png 2>/dev/null | awk '{print "  -", $9, "("$5")"}'
echo ""
echo "✅ Image generation complete!"
echo ""
echo "Next steps:"
echo "1. Review the images in the analysis_images/ directory"
echo "2. Insert these images into your written analysis answers"
echo "3. Add captions explaining what each image shows"
echo ""
echo "Tip: You can also view mazes interactively with:"
echo "  python3 pacman.py -l <maze_file> -z 1.5"
echo ""
