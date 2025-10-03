package assign05;

import java.util.List;
import java.util.Random;

/**
 * RandomPivotChooser implements a pivot selection strategy that randomly
 * selects an element from the given range to serve as the pivot.
 *
 * This strategy provides good average-case performance for quicksort by
 * avoiding worst-case scenarios that can occur with deterministic pivot
 * selection (such as always choosing the first element). Random pivot selection
 * makes it highly unlikely to consistently pick poor pivots.
 *
 * @param <E> the type of elements that the pivot chooser works with
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version 1.0 | October 2nd, 2025 (Up-to-date)
 */
public class RandomPivotChooser<E extends Comparable<? super E>> implements PivotChooser<E> {

    /**
     * Random number generator for pivot selection
     */
    private final Random random;

    /**
     * Constructs a new RandomPivotChooser with a default Random instance.
     */
    public RandomPivotChooser() {
        this.random = new Random();
    }

    /**
     * Constructs a new RandomPivotChooser with a specified Random instance.
     * This constructor is useful for testing with a seeded random generator.
     *
     * @param random the Random instance to use for pivot selection
     */
    public RandomPivotChooser(Random random) {
        this.random = random;
    }

    /**
     * Randomly selects an element from the specified range to serve as the
     * pivot.
     *
     * This method generates a random index within the range [leftIndex,
     * rightIndex] and returns that index. Each element in the range has an
     * equal probability of being selected as the pivot.
     *
     * @param list the list containing elements to choose from
     * @param leftIndex the starting index of the range (inclusive)
     * @param rightIndex the ending index of the range (inclusive)
     * @return a randomly selected index within the specified range
     * @throws IllegalArgumentException if the list is null or empty, or if
     * leftIndex or rightIndex are out of bounds or invalid
     */
    @Override
    public int getPivotIndex(List<E> list, int leftIndex, int rightIndex) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        if (leftIndex < 0 || rightIndex >= list.size() || leftIndex > rightIndex) {
            throw new IllegalArgumentException("Invalid left or right index");
        }

        // If only one element, return that index
        if (leftIndex == rightIndex) {
            return leftIndex;
        }

        // Generate random index in range [leftIndex, rightIndex]
        int rangeSize = rightIndex - leftIndex + 1;
        return leftIndex + random.nextInt(rangeSize);
    }
}
