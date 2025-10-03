package assign05;

import java.util.List;

/**
 * FirstPivotChooser implements a pivot selection strategy that always selects
 * the first element in the given range as the pivot.
 *
 * This is the simplest pivot selection strategy, but it can lead to poor
 * performance (O(n²)) when the input data is already sorted or reverse sorted,
 * as it consistently produces unbalanced partitions. This strategy is primarily
 * useful for educational purposes and performance comparisons.
 *
 * @param <E> the type of elements that the pivot chooser works with
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version 1.0 | October 2nd, 2025 (Up-to-date)
 */
public class FirstPivotChooser<E extends Comparable<? super E>> implements PivotChooser<E> {

    /**
     * Constructs a new FirstPivotChooser. No initialization is needed for this
     * simple strategy.
     */
    public FirstPivotChooser() {
        // No initialization required
    }

    /**
     * Selects the first element in the specified range as the pivot.
     *
     * This method always returns leftIndex, making it the simplest possible
     * pivot selection strategy. While deterministic and fast, this approach can
     * lead to poor quicksort performance on sorted or reverse-sorted data.
     *
     * @param list the list containing elements to choose from
     * @param leftIndex the starting index of the range (inclusive)
     * @param rightIndex the ending index of the range (inclusive)
     * @return leftIndex (the index of the first element in the range)
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

        // Always return the first index in the range
        return leftIndex;
    }
}
