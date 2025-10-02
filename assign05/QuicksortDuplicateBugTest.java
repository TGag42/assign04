package assign05;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Test to isolate the quicksort duplicate handling bug
 */
public class QuicksortDuplicateBugTest {

    @Test
    public void testSimpleDuplicatesCase() {
        // Create a simple case with duplicates that should expose the bug
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(1);
        list.add(3);
        list.add(2);
        list.add(3);

        System.out.println("Original list: " + list);

        PivotChooser<Integer> chooser = new PivotChooser<>();
        ListSorter.quicksort(list, chooser);

        System.out.println("Sorted list: " + list);

        // Check if sorted
        List<Integer> expected = new ArrayList<>(list);
        Collections.sort(expected);
        assertEquals(expected, list, "List should be properly sorted");
    }

    @Test
    public void testAllSameElements() {
        // All elements are the same - should be easy to handle
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(5);
        }

        System.out.println("Original all-same list: " + list);

        PivotChooser<Integer> chooser = new PivotChooser<>();
        ListSorter.quicksort(list, chooser);

        System.out.println("Sorted all-same list: " + list);

        // All elements should still be 5
        for (Integer value : list) {
            assertEquals(5, value, "All elements should remain 5");
        }
    }

    @Test
    public void testManyDuplicatesPattern() {
        // Pattern that might trigger the bug: many duplicates with some variation
        List<Integer> list = new ArrayList<>();
        // Add pattern: 1,1,1,2,2,2,3,3,3 but scrambled
        list.add(2);
        list.add(1);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(3);

        System.out.println("Original pattern: " + list);

        PivotChooser<Integer> chooser = new PivotChooser<>();
        ListSorter.quicksort(list, chooser);

        System.out.println("Sorted pattern: " + list);

        // Check if properly sorted
        List<Integer> expected = new ArrayList<>(list);
        Collections.sort(expected);
        assertEquals(expected, list, "Pattern should be properly sorted");
    }
}
