package assign10;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class contains generic static methods for finding the k largest items in
 * a list.
 *
 * @author Erin Parker
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 18, 2025
 */
public class FindKLargest {

    /**
     * Determines the k largest items in the given list, using a binary max heap
     * and the natural ordering of the items.
     *
     * @param items - the given list
     * @param k - the number of largest items
     * @return a list of the k largest items, in descending order
     * @throws IllegalArgumentException if k is negative or larger than the size
     * of the given list
     */
    public static <E extends Comparable<? super E>> List<E> findKLargestHeap(List<E> items, int k) throws IllegalArgumentException {
        // Validate k
        if (k < 0 || k > items.size()) {
            throw new IllegalArgumentException("k must be between 0 and the size of the list");
        }

        // Build heap from all items in O(N) time
        BinaryMaxHeap<E> heap = new BinaryMaxHeap<>(items);

        // Extract k largest items in O(k log N) time
        List<E> result = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            result.add(heap.extractMax());
        }

        return result;
    }

    /**
     * Determines the k largest items in the given list, using a binary max
     * heap.
     *
     * @param items - the given list
     * @param k - the number of largest items
     * @param cmp - the comparator defining how to compare items
     * @return a list of the k largest items, in descending order
     * @throws IllegalArgumentException if k is negative or larger than the size
     * of the given list
     */
    public static <E> List<E> findKLargestHeap(List<E> items, int k, Comparator<? super E> cmp) throws IllegalArgumentException {
        // Validate k
        if (k < 0 || k > items.size()) {
            throw new IllegalArgumentException("k must be between 0 and the size of the list");
        }

        // Build heap from all items in O(N) time
        BinaryMaxHeap<E> heap = new BinaryMaxHeap<>(items, cmp);

        // Extract k largest items in O(k log N) time
        List<E> result = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            result.add(heap.extractMax());
        }

        return result;
    }

    /**
     * Determines the k largest items in the given list, using Java's sort
     * routine and the natural ordering of the items.
     *
     * @param items - the given list
     * @param k - the number of largest items
     * @return a list of the k largest items, in descending order
     * @throws IllegalArgumentException if k is negative or larger than the size
     * of the given list
     */
    public static <E extends Comparable<? super E>> List<E> findKLargestSort(List<E> items, int k) throws IllegalArgumentException {
        // Validate k
        if (k < 0 || k > items.size()) {
            throw new IllegalArgumentException("k must be between 0 and the size of the list");
        }

        // Create a copy to avoid modifying original list
        List<E> sortedList = new ArrayList<>(items);

        // Sort in descending order using natural ordering in O(N log N) time
        Collections.sort(sortedList, Collections.reverseOrder());

        // Extract first k items in O(k) time
        return new ArrayList<>(sortedList.subList(0, k));
    }

    /**
     * Determines the k largest items in the given list, using Java's sort
     * routine.
     *
     * @param items - the given list
     * @param k - the number of largest items
     * @param cmp - the comparator defining how to compare items
     * @return a list of the k largest items, in descending order
     * @throws IllegalArgumentException if k is negative or larger than the size
     * of the given list
     */
    public static <E> List<E> findKLargestSort(List<E> items, int k, Comparator<? super E> cmp) throws IllegalArgumentException {
        // Validate k
        if (k < 0 || k > items.size()) {
            throw new IllegalArgumentException("k must be between 0 and the size of the list");
        }

        // Create a copy to avoid modifying original list
        List<E> sortedList = new ArrayList<>(items);

        // Sort in descending order using the comparator in O(N log N) time
        Collections.sort(sortedList, cmp.reversed());

        // Extract first k items in O(k) time
        return new ArrayList<>(sortedList.subList(0, k));
    }
}
