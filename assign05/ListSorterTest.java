package assign05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for ListSorter mergesort and quicksort
 * implementations.
 *
 * @author Alex Waldmann
 * @version 1.0 | October 2nd, 2025 (Up-to-date)
 */
public class ListSorterTest {

    private List<Integer> emptyList;
    private List<Integer> singleElement;
    private List<Integer> sortedList;
    private List<Integer> reverseSortedList;
    private List<Integer> randomList;
    private List<Integer> duplicatesList;
    private List<String> stringList;
    private PivotChooser<Integer> pivotChooser;

    @BeforeEach
    void setUp() {
        emptyList = new ArrayList<>();
        singleElement = new ArrayList<>(Arrays.asList(42));
        sortedList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        reverseSortedList = new ArrayList<>(Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
        randomList = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6, 10));
        duplicatesList = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3));
        stringList = new ArrayList<>(Arrays.asList("zebra", "apple", "banana", "cherry", "date"));
        pivotChooser = new MedianOfFivePivotChooser<>();
    }

    // ===== MERGESORT TESTS =====
    @Test
    void testMergesortEmptyList() {
        List<Integer> original = new ArrayList<>(emptyList);
        ListSorter.mergesort(emptyList, 5);
        assertEquals(original, emptyList);
    }

    @Test
    void testMergesortNullList() {
        assertDoesNotThrow(() -> ListSorter.mergesort(null, 5));
    }

    @Test
    void testMergesortSingleElement() {
        List<Integer> expected = new ArrayList<>(singleElement);
        ListSorter.mergesort(singleElement, 5);
        assertEquals(expected, singleElement);
    }

    @Test
    void testMergesortAlreadySorted() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ListSorter.mergesort(sortedList, 5);
        assertEquals(expected, sortedList);
    }

    @Test
    void testMergesortReverseSorted() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ListSorter.mergesort(reverseSortedList, 5);
        assertEquals(expected, reverseSortedList);
    }

    @Test
    void testMergesortRandomList() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ListSorter.mergesort(randomList, 5);
        assertEquals(expected, randomList);
    }

    @Test
    void testMergesortWithDuplicates() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 6, 9));
        ListSorter.mergesort(duplicatesList, 5);
        assertEquals(expected, duplicatesList);
    }

    @Test
    void testMergesortStrings() {
        List<String> expected = new ArrayList<>(Arrays.asList("apple", "banana", "cherry", "date", "zebra"));
        ListSorter.mergesort(stringList, 3);
        assertEquals(expected, stringList);
    }

    @Test
    void testMergesortDifferentThresholds() {
        // Test with threshold = 1 (always use insertion sort)
        List<Integer> list1 = new ArrayList<>(randomList);
        ListSorter.mergesort(list1, 1);
        Collections.sort(randomList);
        assertEquals(randomList, list1);

        // Test with threshold = 100 (always use merge sort)
        List<Integer> list2 = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6, 10));
        ListSorter.mergesort(list2, 100);
        assertEquals(randomList, list2);
    }

    @Test
    void testMergesortInvalidThreshold() {
        assertThrows(IllegalArgumentException.class, () -> {
            ListSorter.mergesort(randomList, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ListSorter.mergesort(randomList, -1);
        });
    }

    // ===== QUICKSORT TESTS =====
    @Test
    void testQuicksortEmptyList() {
        List<Integer> original = new ArrayList<>(emptyList);
        ListSorter.quicksort(emptyList, pivotChooser);
        assertEquals(original, emptyList);
    }

    @Test
    void testQuicksortNullList() {
        assertDoesNotThrow(() -> ListSorter.quicksort(null, pivotChooser));
    }

    @Test
    void testQuicksortNullChooser() {
        assertThrows(IllegalArgumentException.class, () -> {
            ListSorter.quicksort(randomList, null);
        });
    }

    @Test
    void testQuicksortSingleElement() {
        List<Integer> expected = new ArrayList<>(singleElement);
        ListSorter.quicksort(singleElement, pivotChooser);
        assertEquals(expected, singleElement);
    }

    @Test
    void testQuicksortAlreadySorted() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ListSorter.quicksort(sortedList, pivotChooser);
        assertEquals(expected, sortedList);
    }

    @Test
    void testQuicksortReverseSorted() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ListSorter.quicksort(reverseSortedList, pivotChooser);
        assertEquals(expected, reverseSortedList);
    }

    @Test
    void testQuicksortRandomList() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        ListSorter.quicksort(randomList, pivotChooser);
        assertEquals(expected, randomList);
    }

    @Test
    void testQuicksortWithDuplicates() {
        List<Integer> expected = new ArrayList<>(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 6, 9));
        ListSorter.quicksort(duplicatesList, pivotChooser);
        assertEquals(expected, duplicatesList);
    }

    @Test
    void testQuicksortStrings() {
        List<String> expected = new ArrayList<>(Arrays.asList("apple", "banana", "cherry", "date", "zebra"));
        PivotChooser<String> stringPivotChooser = new MedianOfFivePivotChooser<>();
        ListSorter.quicksort(stringList, stringPivotChooser);
        assertEquals(expected, stringList);
    }

    // ===== GENERATE ASCENDING TESTS =====
    @Test
    void testGenerateAscendingZero() {
        List<Integer> result = ListSorter.generateAscending(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGenerateAscendingNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            ListSorter.generateAscending(-1);
        });
    }

    @Test
    void testGenerateAscendingPositive() {
        List<Integer> result = ListSorter.generateAscending(5);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        assertEquals(expected, result);
    }

    @Test
    void testGenerateAscendingLargeSize() {
        List<Integer> result = ListSorter.generateAscending(1000);
        assertEquals(1000, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
        assertEquals(Integer.valueOf(1000), result.get(999));

        // Verify ascending order
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1) < result.get(i));
        }
    }

    // ===== STABILITY AND PERFORMANCE TESTS =====
    @Test
    void testLargeRandomList() {
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeList.add((int) (Math.random() * 1000));
        }

        List<Integer> mergeList = new ArrayList<>(largeList);
        List<Integer> quickList = new ArrayList<>(largeList);

        ListSorter.mergesort(mergeList, 10);
        ListSorter.quicksort(quickList, pivotChooser);

        // Both should produce sorted results
        Collections.sort(largeList);
        assertEquals(largeList, mergeList);
        assertEquals(largeList, quickList);
    }

    @Test
    void testAllSameElements() {
        List<Integer> sameElements = new ArrayList<>(Arrays.asList(5, 5, 5, 5, 5, 5, 5));
        List<Integer> expected = new ArrayList<>(sameElements);

        ListSorter.mergesort(sameElements, 3);
        assertEquals(expected, sameElements);

        List<Integer> sameElements2 = new ArrayList<>(Arrays.asList(5, 5, 5, 5, 5, 5, 5));
        ListSorter.quicksort(sameElements2, pivotChooser);
        assertEquals(expected, sameElements2);
    }
}
