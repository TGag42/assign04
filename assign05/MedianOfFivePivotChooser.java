package assign05;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MedianOfFivePivotChooser implements a sophisticated pivot selection strategy
 * that adapts based on the size of the range being sorted.
 *
 * For small ranges (< 5 elements), it uses median-of-three selection.
 * For larger ranges (>= 5 elements), it uses median-of-five selection by
 * choosing the first, last, middle, and two random elements, then selecting the
 * median of these five candidates.
 *
 * This adaptive approach provides excellent performance across different data
 * patterns and sizes, making it an ideal default choice for quicksort.
 *
 * @param <E> the type of elements that the pivot chooser works with
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version 1.0 | October 2nd, 2025 (Up-to-date)
 */
public class MedianOfFivePivotChooser<E extends Comparable<? super E>> implements PivotChooser<E> {

    /**
     * Constructs a new MedianOfFivePivotChooser.
     */
    public MedianOfFivePivotChooser() {
        // No initialization required
    }

    /**
     * Selects a pivot using adaptive median selection based on range size.
     *
     * For ranges with fewer than 5 elements, uses median-of-three selection
     * (first, middle, last). For ranges with 5 or more elements, uses
     * median-of-five selection by including two additional random elements.
     *
     * @param list the list containing elements to choose from
     * @param leftIndex the starting index of the range (inclusive)
     * @param rightIndex the ending index of the range (inclusive)
     * @return the index of the selected pivot element
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

        if (list.size() == 1) {
            return 0;
        } else if (rightIndex - leftIndex + 1 < 5) {
            // If range length is less than 5, return median of first, middle, last
            PivotItemWithIndex<E> farLeftItem = new PivotItemWithIndex<>(list.get(leftIndex), leftIndex);
            PivotItemWithIndex<E> farRightItem = new PivotItemWithIndex<>(list.get(rightIndex), rightIndex);
            PivotItemWithIndex<E> middleItem = new PivotItemWithIndex<>(list.get((leftIndex + rightIndex) >>> 1), (leftIndex + rightIndex) >>> 1);

            ArrayList<PivotItemWithIndex<E>> optionsList = new ArrayList<>();
            optionsList.add(farLeftItem);
            optionsList.add(middleItem);
            optionsList.add(farRightItem);

            optionsList.sort(Comparator.comparing(PivotItemWithIndex::getItem));
            PivotItemWithIndex<E> median = optionsList.get(1);

            return median.getIndex();
        } else {
            // If larger than or equal to 5, get a larger sample size to find better median
            PivotItemWithIndex<E> farLeftItem = new PivotItemWithIndex<>(list.get(leftIndex), leftIndex);
            PivotItemWithIndex<E> farRightItem = new PivotItemWithIndex<>(list.get(rightIndex), rightIndex);
            PivotItemWithIndex<E> middleItem = new PivotItemWithIndex<>(list.get((leftIndex + rightIndex) >>> 1), (leftIndex + rightIndex) >>> 1);

            // Get random index from left half
            int midPoint = (leftIndex + rightIndex) >>> 1;
            int leftRandomIndex = leftIndex + (int) (Math.random() * (midPoint - leftIndex + 1));
            PivotItemWithIndex<E> randomLeftItem = new PivotItemWithIndex<>(list.get(leftRandomIndex), leftRandomIndex);

            // Get random index from right half
            int rightRandomIndex = midPoint + (int) (Math.random() * (rightIndex - midPoint + 1));
            PivotItemWithIndex<E> randomRightItem = new PivotItemWithIndex<>(list.get(rightRandomIndex), rightRandomIndex);

            // Create a list of options
            ArrayList<PivotItemWithIndex<E>> optionsList = new ArrayList<>();
            optionsList.add(farLeftItem);
            optionsList.add(middleItem);
            optionsList.add(farRightItem);
            optionsList.add(randomLeftItem);
            optionsList.add(randomRightItem);

            // Sort and get median object so then we can return index from original list
            optionsList.sort(Comparator.comparing(PivotItemWithIndex::getItem));
            PivotItemWithIndex<E> median = optionsList.get(2);

            return median.getIndex();
        }
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
