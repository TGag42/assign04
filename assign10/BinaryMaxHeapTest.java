package assign10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test class for BinaryMaxHeap and FindKLargest.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 18, 2025
 */
class BinaryMaxHeapTest {

    // Test fixtures for BinaryMaxHeap
    private BinaryMaxHeap<Integer> emptyHeap;

    // Test data
    private List<Integer> smallList;
    private List<Integer> largeList;
    private List<String> stringList;

    @BeforeEach
    void setUp() {
        emptyHeap = new BinaryMaxHeap<>();

        // Small list for basic tests
        smallList = new ArrayList<>(Arrays.asList(5, 3, 8, 1, 9, 2));

        // Large list for more comprehensive tests
        largeList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            largeList.add(i);
        }

        // String list for testing non-integer types
        stringList = new ArrayList<>(Arrays.asList("apple", "banana", "cherry", "date", "elderberry"));
    }

    // ==================== Constructor Tests ====================
    @Test
    void testDefaultConstructor() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>();
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testComparatorConstructor() {
        // Reverse comparator (min heap behavior)
        Comparator<Integer> reverseComp = Comparator.reverseOrder();
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(reverseComp);

        heap.add(5);
        heap.add(3);
        heap.add(8);

        assertEquals(3, heap.peek()); // Should be min with reversed comparator
    }

    @Test
    void testListConstructor() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(smallList);
        assertEquals(6, heap.size());
        assertEquals(9, heap.peek()); // Max should be 9
    }

    @Test
    void testListConstructorMaintainsHeapProperty() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(smallList);

        // Extract all and verify descending order
        List<Integer> extracted = new ArrayList<>();
        while (!heap.isEmpty()) {
            extracted.add(heap.extractMax());
        }

        // Should be in descending order
        for (int i = 0; i < extracted.size() - 1; i++) {
            assertTrue(extracted.get(i) >= extracted.get(i + 1));
        }
    }

    @Test
    void testListComparatorConstructor() {
        Comparator<String> lengthComp = Comparator.comparingInt(String::length);
        BinaryMaxHeap<String> heap = new BinaryMaxHeap<>(stringList, lengthComp);

        assertEquals(5, heap.size());
        assertEquals("elderberry", heap.peek()); // Longest string
    }

    // ==================== Add Tests ====================
    @Test
    void testAddToEmptyHeap() {
        emptyHeap.add(42);
        assertEquals(1, emptyHeap.size());
        assertEquals(42, emptyHeap.peek());
    }

    @Test
    void testAddMultipleElements() {
        emptyHeap.add(5);
        emptyHeap.add(3);
        emptyHeap.add(8);
        emptyHeap.add(1);

        assertEquals(4, emptyHeap.size());
        assertEquals(8, emptyHeap.peek()); // Max should be 8
    }

    @Test
    void testAddDuplicates() {
        emptyHeap.add(5);
        emptyHeap.add(5);
        emptyHeap.add(5);

        assertEquals(3, emptyHeap.size());
        assertEquals(5, emptyHeap.peek());
        assertEquals(5, emptyHeap.extractMax());
        assertEquals(5, emptyHeap.extractMax());
        assertEquals(5, emptyHeap.extractMax());
    }

    @Test
    void testAddMaintainsMaxAtTop() {
        for (int i = 1; i <= 100; i++) {
            emptyHeap.add(i);
            assertEquals(i, emptyHeap.peek()); // Current max should always be at top
        }
    }

    // ==================== Peek Tests ====================
    @Test
    void testPeekEmptyHeap() {
        assertThrows(NoSuchElementException.class, () -> emptyHeap.peek());
    }

    @Test
    void testPeekDoesNotRemove() {
        emptyHeap.add(10);
        assertEquals(10, emptyHeap.peek());
        assertEquals(1, emptyHeap.size()); // Size should not change
        assertEquals(10, emptyHeap.peek()); // Should still be there
    }

    @Test
    void testPeekWithComparator() {
        Comparator<Integer> reverseComp = Comparator.reverseOrder();
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(reverseComp);

        heap.add(5);
        heap.add(3);
        heap.add(8);

        assertEquals(3, heap.peek()); // Min with reversed comparator
    }

    // ==================== ExtractMax Tests ====================
    @Test
    void testExtractMaxEmptyHeap() {
        assertThrows(NoSuchElementException.class, () -> emptyHeap.extractMax());
    }

    @Test
    void testExtractMaxSingleElement() {
        emptyHeap.add(42);
        assertEquals(42, emptyHeap.extractMax());
        assertEquals(0, emptyHeap.size());
        assertTrue(emptyHeap.isEmpty());
    }

    @Test
    void testExtractMaxMultipleElements() {
        emptyHeap.add(5);
        emptyHeap.add(3);
        emptyHeap.add(8);
        emptyHeap.add(1);
        emptyHeap.add(9);

        assertEquals(9, emptyHeap.extractMax());
        assertEquals(8, emptyHeap.extractMax());
        assertEquals(5, emptyHeap.extractMax());
        assertEquals(3, emptyHeap.extractMax());
        assertEquals(1, emptyHeap.extractMax());
        assertTrue(emptyHeap.isEmpty());
    }

    @Test
    void testExtractMaxMaintainsHeapProperty() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(largeList);

        int previousMax = Integer.MAX_VALUE;
        while (!heap.isEmpty()) {
            int current = heap.extractMax();
            assertTrue(current <= previousMax, "Heap property violated");
            previousMax = current;
        }
    }

    // ==================== Size and isEmpty Tests ====================
    @Test
    void testSizeEmptyHeap() {
        assertEquals(0, emptyHeap.size());
    }

    @Test
    void testSizeAfterAdds() {
        for (int i = 0; i < 10; i++) {
            emptyHeap.add(i);
            assertEquals(i + 1, emptyHeap.size());
        }
    }

    @Test
    void testSizeAfterExtracts() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(smallList);
        int initialSize = heap.size();

        for (int i = 0; i < 3; i++) {
            heap.extractMax();
            assertEquals(initialSize - i - 1, heap.size());
        }
    }

    @Test
    void testIsEmptyInitially() {
        assertTrue(emptyHeap.isEmpty());
    }

    @Test
    void testIsEmptyAfterOperations() {
        emptyHeap.add(1);
        assertFalse(emptyHeap.isEmpty());
        emptyHeap.extractMax();
        assertTrue(emptyHeap.isEmpty());
    }

    // ==================== Clear Tests ====================
    @Test
    void testClearEmptyHeap() {
        emptyHeap.clear();
        assertEquals(0, emptyHeap.size());
        assertTrue(emptyHeap.isEmpty());
    }

    @Test
    void testClearNonEmptyHeap() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(smallList);
        assertFalse(heap.isEmpty());

        heap.clear();
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
        assertThrows(NoSuchElementException.class, () -> heap.peek());
    }

    @Test
    void testClearThenAdd() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(smallList);
        heap.clear();

        heap.add(42);
        assertEquals(1, heap.size());
        assertEquals(42, heap.peek());
    }

    // ==================== ToArray Tests ====================
    @Test
    void testToArrayEmptyHeap() {
        Object[] array = emptyHeap.toArray();
        assertEquals(0, array.length);
    }

    @Test
    void testToArraySingleElement() {
        emptyHeap.add(42);
        Object[] array = emptyHeap.toArray();
        assertEquals(1, array.length);
        assertEquals(42, array[0]);
    }

    @Test
    void testToArrayMultipleElements() {
        BinaryMaxHeap<Integer> heap = new BinaryMaxHeap<>(smallList);
        Object[] array = heap.toArray();

        assertEquals(smallList.size(), array.length);

        // First element should be max
        assertEquals(9, array[0]);

        // All elements should be present
        List<Object> arrayList = Arrays.asList(array);
        for (Integer item : smallList) {
            assertTrue(arrayList.contains(item));
        }
    }

    // ==================== FindKLargest Heap Tests ====================
    @Test
    void testFindKLargestHeapBasic() {
        List<Integer> result = FindKLargest.findKLargestHeap(smallList, 3);

        assertEquals(3, result.size());
        assertEquals(Arrays.asList(9, 8, 5), result);
    }

    @Test
    void testFindKLargestHeapKEqualsZero() {
        List<Integer> result = FindKLargest.findKLargestHeap(smallList, 0);
        assertEquals(0, result.size());
    }

    @Test
    void testFindKLargestHeapKEqualsSize() {
        List<Integer> result = FindKLargest.findKLargestHeap(smallList, smallList.size());
        assertEquals(smallList.size(), result.size());
        assertEquals(9, result.get(0)); // Should be in descending order
    }

    @Test
    void testFindKLargestHeapInvalidK() {
        assertThrows(IllegalArgumentException.class, ()
                -> FindKLargest.findKLargestHeap(smallList, -1));
        assertThrows(IllegalArgumentException.class, ()
                -> FindKLargest.findKLargestHeap(smallList, smallList.size() + 1));
    }

    @Test
    void testFindKLargestHeapWithComparator() {
        Comparator<String> lengthComp = Comparator.comparingInt(String::length);
        List<String> result = FindKLargest.findKLargestHeap(stringList, 2, lengthComp);

        assertEquals(2, result.size());
        assertEquals("elderberry", result.get(0)); // Longest
        assertEquals("banana", result.get(1)); // Second longest
    }

    // ==================== FindKLargest Sort Tests ====================
    @Test
    void testFindKLargestSortBasic() {
        List<Integer> result = FindKLargest.findKLargestSort(smallList, 3);

        assertEquals(3, result.size());
        assertEquals(Arrays.asList(9, 8, 5), result);
    }

    @Test
    void testFindKLargestSortKEqualsZero() {
        List<Integer> result = FindKLargest.findKLargestSort(smallList, 0);
        assertEquals(0, result.size());
    }

    @Test
    void testFindKLargestSortKEqualsSize() {
        List<Integer> result = FindKLargest.findKLargestSort(smallList, smallList.size());
        assertEquals(smallList.size(), result.size());
        assertEquals(9, result.get(0)); // Should be in descending order
    }

    @Test
    void testFindKLargestSortInvalidK() {
        assertThrows(IllegalArgumentException.class, ()
                -> FindKLargest.findKLargestSort(smallList, -1));
        assertThrows(IllegalArgumentException.class, ()
                -> FindKLargest.findKLargestSort(smallList, smallList.size() + 1));
    }

    @Test
    void testFindKLargestSortWithComparator() {
        Comparator<String> lengthComp = Comparator.comparingInt(String::length);
        List<String> result = FindKLargest.findKLargestSort(stringList, 2, lengthComp);

        assertEquals(2, result.size());
        assertEquals("elderberry", result.get(0)); // Longest
        assertEquals("banana", result.get(1)); // Second longest
    }

    // ==================== Heap vs Sort Comparison Tests ====================
    @Test
    void testHeapAndSortProduceSameResults() {
        for (int k = 0; k <= smallList.size(); k++) {
            List<Integer> heapResult = FindKLargest.findKLargestHeap(smallList, k);
            List<Integer> sortResult = FindKLargest.findKLargestSort(smallList, k);

            assertEquals(heapResult, sortResult, "Heap and sort should produce same results for k=" + k);
        }
    }

    @Test
    void testHeapAndSortWithComparatorProduceSameResults() {
        Comparator<String> lengthComp = Comparator.comparingInt(String::length);

        for (int k = 0; k <= stringList.size(); k++) {
            List<String> heapResult = FindKLargest.findKLargestHeap(stringList, k, lengthComp);
            List<String> sortResult = FindKLargest.findKLargestSort(stringList, k, lengthComp);

            assertEquals(heapResult, sortResult, "Heap and sort with comparator should produce same results for k=" + k);
        }
    }

    // ==================== Edge Case Tests ====================
    @Test
    void testSingleElementList() {
        List<Integer> singleList = Arrays.asList(42);

        List<Integer> heapResult = FindKLargest.findKLargestHeap(singleList, 1);
        List<Integer> sortResult = FindKLargest.findKLargestSort(singleList, 1);

        assertEquals(Arrays.asList(42), heapResult);
        assertEquals(Arrays.asList(42), sortResult);
    }

    @Test
    void testDuplicateElements() {
        List<Integer> duplicates = Arrays.asList(5, 5, 5, 3, 3, 8, 8);

        List<Integer> result = FindKLargest.findKLargestHeap(duplicates, 4);
        assertEquals(4, result.size());
        assertEquals(8, result.get(0));
        assertEquals(8, result.get(1));
        assertEquals(5, result.get(2));
        assertEquals(5, result.get(3));
    }

    @Test
    void testOriginalListNotModified() {
        List<Integer> original = new ArrayList<>(smallList);

        FindKLargest.findKLargestHeap(smallList, 3);
        assertEquals(original, smallList, "Heap method should not modify original list");

        FindKLargest.findKLargestSort(smallList, 3);
        assertEquals(original, smallList, "Sort method should not modify original list");
    }
}
