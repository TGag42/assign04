/**
 * Assignment 05 - Sorting algorithms implementation and analysis.
 *
 * This package contains implementations of efficient sorting algorithms including
 * mergesort and quicksort, with comprehensive testing and performance analysis.
 * The algorithms are optimized for different data patterns and include hybrid
 * approaches that switch to insertion sort for small subarrays.
 */
package assign05;

import java.util.ArrayList;
import java.util.List;

/**
 * ListSorter provides static methods for sorting lists using mergesort and
 * quicksort algorithms.
 *
 * This utility class implements high-performance sorting algorithms with the
 * following features: - Mergesort with adaptive threshold switching to
 * insertion sort for small subarrays - Quicksort with 3-way partitioning (Dutch
 * National Flag) for optimal duplicate handling - Generic implementation
 * supporting any Comparable type - Utility methods for generating test data
 *
 * Both sorting algorithms are stable, efficient, and handle edge cases
 * gracefully. The mergesort implementation guarantees O(n log n) performance in
 * all cases, while quicksort provides average O(n log n) performance with
 * excellent handling of duplicate elements through 3-way partitioning.
 *
 * @author Alex Waldmann
 * @author [Your Partner's Name if applicable]
 * @version 1.0
 * @since 2025-10-02
 */
public final class ListSorter {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ListSorter() {
    }

    /**
     * Sorts the given list using a hybrid mergesort algorithm.
     *
     * This implementation uses a divide-and-conquer approach that recursively
     * splits the list into smaller sublists, sorts them, and merges them back
     * together. For performance optimization, when the sublist size falls below
     * the specified threshold, the algorithm switches to insertion sort, which
     * is more efficient for small arrays.
     *
     * Time Complexity: O(n log n) in all cases (worst, average, and best) Space
     * Complexity: O(n) for the temporary array used during merging.
     *
     * @param <T> the type of elements in the list, must implement Comparable
     * @param list the list to be sorted in-place (must not be null)
     * @param threshold the size threshold below which insertion sort is used
     * (must be positive)
     *
     * @throws IllegalArgumentException if threshold <= 0
     * @throws NullPointerException if list is null (handled gracefully by
     * returning early)
     */
    public static <T extends Comparable<? super T>> void mergesort(List<T> list, int threshold) {
        if (list == null || list.size() <= 1) {
            return;
        }
        if (threshold <= 0) {
            throw new IllegalArgumentException("Threshold must be positive, got: " + threshold);
        }

        int n = list.size();
        ArrayList<T> temp = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            temp.add(null);
        }

        mergeSortRecursive(list, temp, 0, n - 1, threshold);
    }

    /**
     * Sorts the given list using a quicksort algorithm with 3-way partitioning.
     *
     * This implementation uses a divide-and-conquer approach that selects a
     * pivot element using the provided PivotChooser, partitions the list around
     * the pivot, and recursively sorts the sublists. The algorithm uses 3-way
     * partitioning (Dutch National Flag algorithm) to efficiently handle lists
     * with many duplicate elements by creating three regions: less than, equal
     * to, and greater than the pivot.
     *
     * Time Complexity: - Average case: O(n log n) - Best case: O(n log n) -
     * Worst case: O(n²) (rare with good pivot selection) Space Complexity:
     * O(log n) for recursion stack Stability: No, does not maintain relative
     * order of equal elements
     *
     * @param <T> the type of elements in the list, must implement Comparable
     * @param list the list to be sorted in-place (must not be null)
     * @param chooser the pivot selection strategy (must not be null)
     *
     * @throws IllegalArgumentException if chooser is null
     * @throws NullPointerException if list is null (handled gracefully by
     * returning early)
     */
    public static <T extends Comparable<? super T>> void quicksort(List<T> list, PivotChooser<T> chooser) {
        if (list == null || list.size() <= 1) {
            return;
        }
        if (chooser == null) {
            throw new IllegalArgumentException("PivotChooser cannot be null");
        }

        quickSortRecursive(list, 0, list.size() - 1, chooser);
    }

    /**
     * Generates a list of integers in ascending order from 1 to size.
     *
     * This utility method creates test data for performance analysis and
     * correctness testing. The generated list contains consecutive integers
     * starting from 1, which represents the best-case scenario for many sorting
     * algorithms.
     *
     * @param size the number of elements to generate (must be non-negative)
     * @return a new ArrayList containing integers [1, 2, 3, ..., size]
     *
     * @throws IllegalArgumentException if size is negative
     */
    public static List<Integer> generateAscending(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative, got: " + size);
        }
        ArrayList<Integer> out = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            out.add(i);
        }
        return out;
    }

    /**
     * Recursively implements the mergesort algorithm with adaptive threshold
     * optimization.
     *
     * This method divides the specified range of the list into two halves,
     * recursively sorts each half, and then merges the sorted halves back
     * together. For small subarrays (size <= threshold), it switches to
     * insertion sort for better performance.
     *
     * @param <T> the type of elements in the list
     * @param list the list being sorted
     * @param temp temporary array used for merging (same size as list)
     * @param left the starting index of the range to sort (inclusive)
     * @param right the ending index of the range to sort (inclusive)
     * @param threshold the size below which insertion sort is used
     */
    private static <T extends Comparable<? super T>> void mergeSortRecursive(
            List<T> list, List<T> temp, int left, int right, int threshold) {

        int len = right - left + 1;
        if (len <= 1) {
            return;
        }

        if (len <= threshold) {
            insertionSortRange(list, left, right);
            return;
        }

        int mid = (left + right) >>> 1;
        //Sort left half
        mergeSortRecursive(list, temp, left, mid, threshold);

        //Sort right half
        mergeSortRecursive(list, temp, mid + 1, right, threshold);

        merge(list, temp, left, mid, right);

        for (int i = left; i <= right; i++) {
            list.set(i, temp.get(i));
        }
    }

    /**
     * Merges two sorted sublists into a single sorted sublist.
     *
     * This method assumes that list[left..mid] and list[mid+1..right] are
     * already sorted, and merges them into temp[left..right] in sorted order.
     * The merge operation maintains stability by always choosing the left
     * element when elements are equal.
     *
     * @param <T> the type of elements in the list
     * @param list the source list containing the two sorted sublists
     * @param temp the temporary list where the merged result is stored
     * @param left the starting index of the first sorted sublist
     * @param mid the ending index of the first sorted sublist
     * @param right the ending index of the second sorted sublist
     */
    private static <T extends Comparable<? super T>> void merge(
            List<T> list, List<T> temp, int left, int mid, int right) {

        int i = left, j = mid + 1, k = left;

        while (i <= mid && j <= right) {
            T a = list.get(i);
            T b = list.get(j);
            if (a.compareTo(b) <= 0) {
                temp.set(k++, a);
                i++;
            } else {
                temp.set(k++, b);
                j++;
            }
        }
        while (i <= mid) {
            temp.set(k++, list.get(i++));
        }
        while (j <= right) {
            temp.set(k++, list.get(j++));
        }
    }

    /**
     * Sorts a range of the list using insertion sort algorithm.
     *
     * Insertion sort is efficient for small arrays and is used as the base case
     * in the hybrid mergesort implementation. It works by iteratively taking
     * elements from the unsorted portion and inserting them into their correct
     * position in the sorted portion.
     *
     * Time Complexity: O(n²) worst case, O(n) best case (already sorted) Space
     * Complexity: O(1) Stability: Yes, maintains relative order of equal
     * elements
     *
     * @param <T> the type of elements in the list
     * @param list the list to sort
     * @param left the starting index of the range to sort (inclusive)
     * @param right the ending index of the range to sort (inclusive)
     */
    private static <T extends Comparable<? super T>> void insertionSortRange(
            List<T> list, int left, int right) {

        for (int i = left + 1; i <= right; i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= left && list.get(j).compareTo(key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    /**
     * Recursively implements the quicksort algorithm with 3-way partitioning.
     *
     * This method selects a pivot using the provided chooser, moves it to the
     * end, partitions the array using 3-way partitioning (which handles
     * duplicates efficiently), and recursively sorts the sublists containing
     * elements less than and greater than the pivot. Elements equal to the
     * pivot are already in their correct positions after partitioning.
     *
     * @param <T> the type of elements in the list
     * @param list the list being sorted
     * @param left the starting index of the range to sort (inclusive)
     * @param right the ending index of the range to sort (inclusive)
     * @param chooser the pivot selection strategy
     */
    private static <T extends Comparable<? super T>> void quickSortRecursive(
            List<T> list, int left, int right, PivotChooser<T> chooser) {

        if (left >= right) {
            return;
        }

        int pIdx = chooser.choosePivotIndex(list, left, right);
        swap(list, pIdx, right);

        // Use 3-way partitioning to handle duplicates properly
        int[] bounds = partition3Way(list, left, right);
        int lt = bounds[0];  // end of < region
        int gt = bounds[1];  // start of > region

        quickSortRecursive(list, left, lt - 1, chooser);
        quickSortRecursive(list, gt + 1, right, chooser);
    }

    /**
     * Partitions the list using 3-way partitioning (Dutch National Flag
     * algorithm).
     *
     * This method rearranges the elements in list[left..right] so that: -
     * Elements in list[left..lt-1] are less than the pivot - Elements in
     * list[lt..gt] are equal to the pivot - Elements in list[gt+1..right] are
     * greater than the pivot
     *
     * The 3-way partitioning is particularly efficient for lists with many
     * duplicate elements, as it groups all equal elements together and avoids
     * unnecessary recursive calls on the equal region.
     *
     * Time Complexity: O(n) where n is the size of the range Space Complexity:
     * O(1)
     *
     * @param <T> the type of elements in the list
     * @param list the list to partition
     * @param left the starting index of the range to partition (inclusive)
     * @param right the ending index of the range to partition (inclusive,
     * contains pivot)
     * @return an array [lt, gt] indicating the boundaries of the three regions
     */
    private static <T extends Comparable<? super T>> int[] partition3Way(
            List<T> list, int left, int right) {

        T pivot = list.get(right);
        int lt = left;      // boundary of < region
        int i = left;       // current element being examined
        int gt = right;     // boundary of > region

        while (i < gt) {
            int cmp = list.get(i).compareTo(pivot);
            if (cmp < 0) {
                swap(list, lt++, i++);
            } else if (cmp > 0) {
                swap(list, i, --gt);
            } else {
                i++;
            }
        }

        // Move pivot from right to its final position
        swap(list, gt, right);

        return new int[]{lt, gt};
    }

    /**
     * Swaps two elements in the list.
     *
     * This utility method exchanges the elements at positions i and j in the
     * list. If the indices are the same, no operation is performed for
     * efficiency.
     *
     * @param <T> the type of elements in the list
     * @param list the list containing the elements to swap
     * @param i the index of the first element
     * @param j the index of the second element
     */
    private static <T> void swap(List<T> list, int i, int j) {
        if (i == j) {
            return;
        }
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }
}
