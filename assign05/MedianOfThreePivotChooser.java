package assign05;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MedianOfThreePivotChooser implements a pivot selection strategy that chooses
 * the median of the first, middle, and last elements in the given range.
 *
 * This strategy provides better performance than random pivot selection by
 * avoiding worst-case scenarios where the pivot is always the smallest or
 * largest element. The median-of-three approach helps ensure more balanced
 * partitions in quicksort.
 *
 * @param <E> the type of elements that the pivot chooser works with
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version 1.0 | October 2nd, 2025 (Up-to-date)
 */
public class MedianOfThreePivotChooser<E extends Comparable<? super E>> implements PivotChooser<E> {

    /**
     * Selects the median of the first, middle, and last elements as the pivot.
     *
     * This method examines three elements: the element at leftIndex, the
     * element at the middle position, and the element at rightIndex. It returns
     * the index of the element with the median value among these three.
     *
     * @param list the list containing elements to choose from
     * @param leftIndex the starting index of the range
     * @param rightIndex the ending index of the range
     * @return the index of the median element among first, middle, and last
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

        // Get the three candidate elements with their indices
        int middleIndex = (leftIndex + rightIndex) >>> 1; // Use unsigned right shift to avoid overflow

        PivotItemWithIndex<E> leftItem = new PivotItemWithIndex<>(list.get(leftIndex), leftIndex);
        PivotItemWithIndex<E> middleItem = new PivotItemWithIndex<>(list.get(middleIndex), middleIndex);
        PivotItemWithIndex<E> rightItem = new PivotItemWithIndex<>(list.get(rightIndex), rightIndex);

        // Create list of the three candidates
        ArrayList<PivotItemWithIndex<E>> candidates = new ArrayList<>(3);
        candidates.add(leftItem);
        candidates.add(middleItem);
        candidates.add(rightItem);

        // Sort by value to find median
        candidates.sort(Comparator.comparing(PivotItemWithIndex::getItem));

        // Return index of median element (middle element after sorting)
        return candidates.get(1).getIndex();
    }
}

class PivotItemWithIndex<E> {

    /**
     * The element value
     */
    E item;

    /**
     * The index of this element in the original list
     */
    int index;

    /**
     * Constructs a new PivotItemWithIndex.
     *
     * @param item the element value
     * @param index the index of this element in the original list
     */
    PivotItemWithIndex(E item, int index) {
        this.item = item;
        this.index = index;
    }

    /**
     * Gets the element value.
     *
     * @return the element value
     */
    public E getItem() {
        return item;
    }

    /**
     * Gets the index of this element in the original list.
     *
     * @return the original index
     */
    public int getIndex() {
        return index;
    }
}
