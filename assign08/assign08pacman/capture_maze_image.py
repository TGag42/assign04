#!/usr/bin/env python3
"""
Captures maze visualizations and saves them as PNG files.
This script runs the pacman visualization and captures screenshots.

Usage:
    python3 capture_maze_image.py <maze_file> <output_png>

Example:
    python3 capture_maze_image.py bigMaze_multigoal.txt bigMaze_multigoal.png
"""

import sys
import os

def create_simple_visualization(maze_file, output_file):
    """
    Creates a simple text-based visualization of the maze and saves it.
    This is a fallback if graphical capture doesn't work.
    """
    try:
        from PIL import Image, ImageDraw, ImageFont
        
        # Read maze file
        with open(maze_file, 'r') as f:
            lines = f.readlines()
        
        # Parse dimensions
        height, width = map(int, lines[0].strip().split())
        maze_lines = [line.rstrip() for line in lines[1:height+1]]
        
        # Calculate image size
        cell_size = 20
        img_width = width * cell_size
        img_height = height * cell_size
        
        # Create image
        img = Image.new('RGB', (img_width, img_height), color='black')
        draw = ImageDraw.Draw(img)
        
        # Color mapping
        colors = {
            'X': (33, 33, 222),    # Blue walls
            ' ': (0, 0, 0),        # Black space
            'S': (255, 255, 0),    # Yellow start
            'G': (255, 0, 0),      # Red goal
            '.': (255, 255, 255),  # White path
        }
        
        # Draw maze
        for row_idx, line in enumerate(maze_lines):
            for col_idx, char in enumerate(line):
                if char in colors:
                    x = col_idx * cell_size
                    y = row_idx * cell_size
                    draw.rectangle(
                        [x, y, x + cell_size - 1, y + cell_size - 1],
                        fill=colors[char],
                        outline=(50, 50, 50)
                    )
        
        # Save image
        img.save(output_file)
        print(f"✓ Saved visualization to {output_file}")
        return True
        
    except ImportError:
        print("Error: PIL/Pillow not installed. Install with: pip3 install Pillow")
        return False
    except Exception as e:
        print(f"Error creating visualization: {e}")
        return False

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python3 capture_maze_image.py <maze_file> <output_png>")
        print("Example: python3 capture_maze_image.py bigMaze_multigoal.txt bigMaze.png")
        sys.exit(1)
    
    maze_file = sys.argv[1]
    output_file = sys.argv[2]
    
    if not os.path.exists(maze_file):
        print(f"Error: Maze file '{maze_file}' not found")
        sys.exit(1)
    
    if create_simple_visualization(maze_file, output_file):
        print(f"✓ Successfully created {output_file}")
    else:
        print("✗ Failed to create visualization")
        sys.exit(1)
