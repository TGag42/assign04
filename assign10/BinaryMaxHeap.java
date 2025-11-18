package assign10;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class represents a binary max heap data structure, backed by an array.
 * The maximum item is always at the root of the heap.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 18, 2025
 *
 * @param <E> - the type of elements contained in the binary max heap
 */
public class BinaryMaxHeap<E> implements PriorityQueue<E> {

    private E[] heap;
    private int size;
    private final Comparator<? super E> comparator;
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * Creates an empty binary max heap with natural ordering. Assumes E
     * implements Comparable<? super E>.
     */
    @SuppressWarnings("unchecked")
    public BinaryMaxHeap() {
        //Horrible casting because Java won't let you create generic arrays directly
        this.heap = (E[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = null;
    }

    /**
     * Creates an empty binary max heap with the given comparator.
     *
     * @param cmp - the comparator to use for ordering elements
     */
    @SuppressWarnings("unchecked")
    public BinaryMaxHeap(Comparator<? super E> cmp) {
        this.heap = (E[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = cmp;
    }

    /**
     * Creates a binary max heap from the given list using natural ordering.
     * Uses the buildHeap operation for O(N) construction time.
     *
     * @param items - the list of items to add to the heap
     */
    @SuppressWarnings("unchecked")
    public BinaryMaxHeap(List<? extends E> items) {
        this.size = items.size();
        this.comparator = null;

        // Create backing array with sufficient capacity
        int capacity = Math.max(DEFAULT_CAPACITY, size + 1);
        this.heap = (E[]) new Object[capacity];

        // Copy items into backing array
        for (int i = 0; i < size; i++) {
            heap[i] = items.get(i);
        }

        // Build heap in O(N) time
        buildHeap();
    }

    /**
     * Creates a binary max heap from the given list using the given comparator.
     * Uses the buildHeap operation for O(N) construction time.
     *
     * @param items - the list of items to add to the heap
     * @param cmp - the comparator to use for ordering elements
     */
    @SuppressWarnings("unchecked")
    public BinaryMaxHeap(List<? extends E> items, Comparator<? super E> cmp) {
        this.size = items.size();
        this.comparator = cmp;

        // Create backing array with sufficient capacity
        int capacity = Math.max(DEFAULT_CAPACITY, size + 1);
        this.heap = (E[]) new Object[capacity];

        // Copy items into backing array
        for (int i = 0; i < size; i++) {
            heap[i] = items.get(i);
        }

        // Build heap in O(N) time
        buildHeap();
    }

    /**
     * Adds the given item to this priority queue. O(1) in the average case,
     * O(log N) in the worst case
     *
     * @param item - the item to add
     */
    @Override
    public void add(E item) {
        // Resize if necessary
        if (size >= heap.length) {
            resize();
        }

        // Add item at end and percolate up
        heap[size] = item;
        percolateUp(size);
        size++;
    }

    /**
     * Returns, but does not remove, the maximum item in this priority queue.
     * O(1)
     *
     * @return the maximum item
     * @throws NoSuchElementException if this priority queue is empty
     */
    @Override
    public E peek() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return heap[0];
    }

    /**
     * Returns and removes the maximum item in this priority queue. O(log N)
     *
     * @return the maximum item
     * @throws NoSuchElementException if this priority queue is empty
     */
    @Override
    public E extractMax() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }

        E max = heap[0];

        // Move last item to root and percolate down
        size--;
        if (size > 0) {
            heap[0] = heap[size];
            heap[size] = null;  // Clear reference
            percolateDown(0);
        } else {
            heap[0] = null;
        }

        return max;
    }

    /**
     * Returns the number of items in this priority queue. O(1)
     *
     * @return the number of items
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns true if this priority queue is empty, false otherwise. O(1)
     *
     * @return true if empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Empties this priority queue of items. O(1)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        heap = (E[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Creates and returns an array of the items in this priority queue, in the
     * same order they appear in the backing array. O(N)
     *
     * @return an array containing all items in heap order
     */
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = heap[i];
        }
        return result;
    }

    // ==================== Private Helper Methods ====================
    /**
     * Builds a max heap from the current backing array in O(N) time. Starts
     * from the last non-leaf node and percolates down.
     */
    private void buildHeap() {
        // Start from last non-leaf node (parent of last element)
        for (int i = (size - 1) / 2; i >= 0; i--) {
            percolateDown(i);
        }
    }

    /**
     * Percolates the item at the given index up the heap until heap property is
     * restored.
     *
     * @param index - the index of the item to percolate up
     */
    private void percolateUp(int index) {
        E item = heap[index];

        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            E parent = heap[parentIndex];

            // If item is not greater than parent, heap property is satisfied
            if (innerCompare(item, parent) <= 0) {
                break;
            }

            // Swap with parent
            heap[index] = parent;
            index = parentIndex;
        }

        heap[index] = item;
    }

    /**
     * Percolates the item at the given index down the heap until heap property
     * is restored.
     *
     * @param index - the index of the item to percolate down
     */
    private void percolateDown(int index) {
        E item = heap[index];

        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            // If no left child, we're at a leaf
            if (leftChild >= size) {
                break;
            }

            // Find the larger child
            int largerChild = leftChild;
            if (rightChild < size && innerCompare(heap[rightChild], heap[leftChild]) > 0) {
                largerChild = rightChild;
            }

            // If item is greater than or equal to larger child, heap property is satisfied
            if (innerCompare(item, heap[largerChild]) >= 0) {
                break;
            }

            // Swap with larger child
            heap[index] = heap[largerChild];
            index = largerChild;
        }

        heap[index] = item;
    }

    /**
     * Compares two items using either natural ordering or the provided
     * comparator.
     *
     * @param item1 - the first item
     * @param item2 - the second item
     * @return negative if item1 < item2, 0 if equal, positive if item1 > item2
     */
    @SuppressWarnings("unchecked")
    private int innerCompare(E item1, E item2) {
        if (comparator != null) {
            return comparator.compare(item1, item2);
        } else {
            return ((Comparable<? super E>) item1).compareTo(item2);
        }
    }

    /**
     * Doubles the capacity of the backing array.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = heap.length * 2;
        E[] newHeap = (E[]) new Object[newCapacity];

        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }

        heap = newHeap;
    }
}
