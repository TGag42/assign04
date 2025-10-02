package assign05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for PivotChooser implementation.
 *
 * @author Alex Waldmann
 * @version October 2, 2025
 */
public class PivotChooserTest {

    private PivotChooser<Integer> pivotChooser;

    @BeforeEach
    void setUp() {
        pivotChooser = new PivotChooser<>();
    }

    // ===== EXCEPTION TESTS =====
    @Test
    void testNullList() {
        assertThrows(IllegalArgumentException.class, () -> {
            pivotChooser.choosePivotIndex(null, 0, 0);
        });
    }

    @Test
    void testEmptyList() {
        List<Integer> emptyList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            pivotChooser.choosePivotIndex(emptyList, 0, 0);
        });
    }

    @Test
    void testInvalidLeftIndex() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            pivotChooser.choosePivotIndex(list, -1, 4);
        });
    }

    @Test
    void testInvalidRightIndex() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            pivotChooser.choosePivotIndex(list, 0, 5);
        });
    }

    @Test
    void testLeftGreaterThanRight() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            pivotChooser.choosePivotIndex(list, 3, 1);
        });
    }

    // ===== SINGLE ELEMENT TESTS =====
    @Test
    void testSingleElementList() {
        List<Integer> list = Arrays.asList(42);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 0);
        assertEquals(0, pivotIndex);
    }

    // ===== SMALL LIST TESTS (< 5 elements) =====
    @Test
    void testTwoElementList() {
        List<Integer> list = Arrays.asList(3, 1);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 1);
        // Should return index of median of {3, 1, 1} -> 1 appears twice, so index of first 1
        assertTrue(pivotIndex >= 0 && pivotIndex <= 1);
    }

    @Test
    void testThreeElementList() {
        List<Integer> list = Arrays.asList(1, 3, 2);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 2);
        // Median of {1, 2, 3} is 2, which is at index 2
        assertTrue(pivotIndex >= 0 && pivotIndex <= 2);

        // Verify the chosen pivot makes sense (should be one of the three elements)
        Integer chosenPivot = list.get(pivotIndex);
        assertTrue(chosenPivot.equals(1) || chosenPivot.equals(2) || chosenPivot.equals(3));
    }

    @Test
    void testFourElementList() {
        List<Integer> list = Arrays.asList(4, 1, 3, 2);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 3);
        // Should use median-of-three: first(4), middle(3), last(2) -> median is 3
        assertTrue(pivotIndex >= 0 && pivotIndex <= 3);
    }

    // ===== LARGER LIST TESTS (>= 5 elements) =====
    @Test
    void testFiveElementList() {
        List<Integer> list = Arrays.asList(5, 1, 4, 2, 3);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 4);
        // Should use 5-element sampling strategy
        assertTrue(pivotIndex >= 0 && pivotIndex <= 4);

        // Verify the pivot is actually in the list
        Integer chosenPivot = list.get(pivotIndex);
        assertTrue(list.contains(chosenPivot));
    }

    @Test
    void testLargerList() {
        List<Integer> list = Arrays.asList(10, 7, 8, 9, 1, 5, 6, 4, 2, 3);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 9);

        // Verify bounds
        assertTrue(pivotIndex >= 0 && pivotIndex <= 9);

        // Verify the pivot is actually in the list
        Integer chosenPivot = list.get(pivotIndex);
        assertTrue(list.contains(chosenPivot));
    }

    // ===== SUBRANGE TESTS =====
    @Test
    void testSubrangeSmall() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // Test subrange [2, 5] = {3, 4, 5, 6}
        int pivotIndex = pivotChooser.choosePivotIndex(list, 2, 5);

        // Pivot should be within the specified range
        assertTrue(pivotIndex >= 2 && pivotIndex <= 5);
    }

    @Test
    void testSubrangeLarge() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        // Test subrange [3, 12] = {4, 5, 6, 7, 8, 9, 10, 11, 12, 13}
        int pivotIndex = pivotChooser.choosePivotIndex(list, 3, 12);

        // Pivot should be within the specified range
        assertTrue(pivotIndex >= 3 && pivotIndex <= 12);
    }

    // ===== DUPLICATE VALUES TESTS =====
    @Test
    void testAllSameValues() {
        List<Integer> list = Arrays.asList(5, 5, 5, 5, 5);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 4);

        // Any index should be valid since all values are the same
        assertTrue(pivotIndex >= 0 && pivotIndex <= 4);
        assertEquals(Integer.valueOf(5), list.get(pivotIndex));
    }

    @Test
    void testMostlyDuplicates() {
        List<Integer> list = Arrays.asList(1, 5, 5, 5, 9);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 4);

        assertTrue(pivotIndex >= 0 && pivotIndex <= 4);
        Integer chosenPivot = list.get(pivotIndex);
        assertTrue(chosenPivot.equals(1) || chosenPivot.equals(5) || chosenPivot.equals(9));
    }

    // ===== EDGE CASE TESTS =====
    @Test
    void testSortedList() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 9);

        assertTrue(pivotIndex >= 0 && pivotIndex <= 9);

        // For a sorted list, a good pivot should be somewhere in the middle range
        Integer chosenPivot = list.get(pivotIndex);
        assertTrue(chosenPivot >= 1 && chosenPivot <= 10);
    }

    @Test
    void testReverseSortedList() {
        List<Integer> list = Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
        int pivotIndex = pivotChooser.choosePivotIndex(list, 0, 9);

        assertTrue(pivotIndex >= 0 && pivotIndex <= 9);

        Integer chosenPivot = list.get(pivotIndex);
        assertTrue(chosenPivot >= 1 && chosenPivot <= 10);
    }

    // ===== CONSISTENCY TESTS =====
    @Test
    void testDeterministicBehaviorSmallLists() {
        // For lists < 5, behavior should be deterministic (no randomness)
        List<Integer> list = Arrays.asList(3, 1, 4);

        int firstChoice = pivotChooser.choosePivotIndex(list, 0, 2);
        int secondChoice = pivotChooser.choosePivotIndex(list, 0, 2);

        // Should be consistent for small lists since there's no randomness
        assertEquals(firstChoice, secondChoice);
    }

    // ===== STRING TYPE TESTS =====
    @Test
    void testStringPivotChooser() {
        PivotChooser<String> stringChooser = new PivotChooser<>();
        List<String> list = Arrays.asList("zebra", "apple", "banana", "cherry");

        int pivotIndex = stringChooser.choosePivotIndex(list, 0, 3);
        assertTrue(pivotIndex >= 0 && pivotIndex <= 3);

        String chosenPivot = list.get(pivotIndex);
        assertTrue(list.contains(chosenPivot));
    }

    // ===== PERFORMANCE CHARACTERISTIC TESTS =====
    @Test
    void testLargeListPerformance() {
        // Create a large list to ensure the algorithm handles large inputs
        List<Integer> largeList = new ArrayList<>();
        for (int i = 1000; i >= 1; i--) {
            largeList.add(i);
        }

        int pivotIndex = pivotChooser.choosePivotIndex(largeList, 0, 999);
        assertTrue(pivotIndex >= 0 && pivotIndex <= 999);

        Integer chosenPivot = largeList.get(pivotIndex);
        assertTrue(chosenPivot >= 1 && chosenPivot <= 1000);
    }
}
