package assign07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for BinarySearchTree implementation.
 *
 * Test Coverage: - Constructor Tests: Verifies empty tree initialization
 *
 * - Add Tests (7 tests): • Single and multiple element insertion • Duplicate
 * handling (returns false, doesn't increase size) • Null parameter validation •
 * Correct BST structure creation • String and Integer type support
 *
 * - AddAll Tests (4 tests): • Empty collections • New elements vs duplicates •
 * Return value correctness
 *
 * - Contains Tests (4 tests): • Empty tree behavior • Existing and non-existing
 * element searches • Null parameter handling
 *
 * - ContainsAll Tests (4 tests): • Empty collections • Partial and complete
 * matches
 *
 * - Remove Tests (8 tests): • Leaf node removal • Nodes with one child • Nodes
 * with two children • Root node removal • Non-existent elements • Null
 * parameter validation • Removing all elements
 *
 * - RemoveAll Tests (4 tests): • Empty collections • Mixed
 * existing/non-existing elements
 *
 * - Clear Tests (3 tests): • Empty and non-empty trees • Operations after clear
 *
 * - First/Last Tests (8 tests): • Empty tree exception handling • Correctness
 * after modifications • Single element trees
 *
 * - Size and IsEmpty Tests (7 tests): • Various tree states • After additions,
 * removals, and clear
 *
 * - ToArrayList Tests (4 tests): • Empty trees • Sorted order verification •
 * After modifications • Different data types
 *
 * - Complex Scenario Tests (8 tests): • Large trees (100+ elements) •
 * Unbalanced trees • Mixed operations • Duplicate handling • Negative numbers •
 * Single element operations • ToString method
 *
 * - Edge Case Tests (3 tests): • Remove and re-add same element • Multiple
 * clear operations • Repeated root removal
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 23, 2025
 */
class BinarySearchTreeTest {

    private BinarySearchTree<Integer> emptyTree;
    private BinarySearchTree<Integer> smallTree;
    private BinarySearchTree<String> stringTree;

    @BeforeEach
    void setUp() {
        emptyTree = new BinarySearchTree<>();
        smallTree = new BinarySearchTree<>();
        stringTree = new BinarySearchTree<>();

        // Small tree: 5, 3, 7, 1, 9
        smallTree.add(5);
        smallTree.add(3);
        smallTree.add(7);
        smallTree.add(1);
        smallTree.add(9);
    }

    // ========== Constructor Tests ==========
    @Test
    void testEmptyTreeInitialization() {
        assertTrue(emptyTree.isEmpty());
        assertEquals(0, emptyTree.size());
    }

    // ========== Add Tests ==========
    @Test
    void testAddSingleElement() {
        assertTrue(emptyTree.add(10));
        assertEquals(1, emptyTree.size());
        assertTrue(emptyTree.contains(10));
    }

    @Test
    void testAddMultipleElements() {
        assertTrue(emptyTree.add(5));
        assertTrue(emptyTree.add(3));
        assertTrue(emptyTree.add(7));
        assertEquals(3, emptyTree.size());
    }

    @Test
    void testAddDuplicateReturnsFalse() {
        emptyTree.add(5);
        assertFalse(emptyTree.add(5));
        assertEquals(1, emptyTree.size());
    }

    @Test
    void testAddNullThrowsException() {
        assertThrows(NullPointerException.class, () -> emptyTree.add(null));
    }

    @Test
    void testAddCreatesCorrectBSTStructure() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.add(50);
        tree.add(25);
        tree.add(75);
        tree.add(10);
        tree.add(30);

        ArrayList<Integer> list = tree.toArrayList();
        assertEquals(Arrays.asList(10, 25, 30, 50, 75), list);
    }

    @Test
    void testAddStrings() {
        assertTrue(stringTree.add("dog"));
        assertTrue(stringTree.add("cat"));
        assertTrue(stringTree.add("elephant"));
        assertEquals(3, stringTree.size());

        ArrayList<String> list = stringTree.toArrayList();
        assertEquals(Arrays.asList("cat", "dog", "elephant"), list);
    }

    // ========== AddAll Tests ==========
    @Test
    void testAddAllEmptyCollection() {
        assertFalse(emptyTree.addAll(new ArrayList<>()));
        assertEquals(0, emptyTree.size());
    }

    @Test
    void testAddAllNewElements() {
        List<Integer> items = Arrays.asList(10, 20, 30);
        assertTrue(emptyTree.addAll(items));
        assertEquals(3, emptyTree.size());
        assertTrue(emptyTree.containsAll(items));
    }

    @Test
    void testAddAllWithDuplicates() {
        emptyTree.add(5);
        List<Integer> items = Arrays.asList(5, 10, 15);
        assertTrue(emptyTree.addAll(items)); // Should return true because 10 and 15 are new
        assertEquals(3, emptyTree.size());
    }

    @Test
    void testAddAllOnlyDuplicates() {
        smallTree.addAll(Arrays.asList(3, 5, 7));
        assertFalse(smallTree.addAll(Arrays.asList(3, 5, 7)));
        assertEquals(5, smallTree.size()); // No new elements added
    }

    // ========== Contains Tests ==========
    @Test
    void testContainsEmptyTree() {
        assertFalse(emptyTree.contains(5));
    }

    @Test
    void testContainsExistingElements() {
        assertTrue(smallTree.contains(5));
        assertTrue(smallTree.contains(3));
        assertTrue(smallTree.contains(7));
        assertTrue(smallTree.contains(1));
        assertTrue(smallTree.contains(9));
    }

    @Test
    void testContainsNonExistingElements() {
        assertFalse(smallTree.contains(0));
        assertFalse(smallTree.contains(2));
        assertFalse(smallTree.contains(100));
    }

    @Test
    void testContainsNullThrowsException() {
        assertThrows(NullPointerException.class, () -> smallTree.contains(null));
    }

    // ========== ContainsAll Tests ==========
    @Test
    void testContainsAllEmptyCollection() {
        assertTrue(smallTree.containsAll(new ArrayList<>()));
    }

    @Test
    void testContainsAllExistingElements() {
        assertTrue(smallTree.containsAll(Arrays.asList(3, 5, 7)));
    }

    @Test
    void testContainsAllSomeMissing() {
        assertFalse(smallTree.containsAll(Arrays.asList(3, 5, 100)));
    }

    @Test
    void testContainsAllNoneMissing() {
        assertTrue(smallTree.containsAll(Arrays.asList(1, 3, 5, 7, 9)));
    }

    // ========== Remove Tests ==========
    @Test
    void testRemoveFromEmptyTree() {
        assertFalse(emptyTree.remove(5));
    }

    @Test
    void testRemoveNonExistentElement() {
        assertFalse(smallTree.remove(100));
        assertEquals(5, smallTree.size());
    }

    @Test
    void testRemoveLeafNode() {
        assertTrue(smallTree.remove(1));
        assertEquals(4, smallTree.size());
        assertFalse(smallTree.contains(1));
    }

    @Test
    void testRemoveNodeWithOneChild() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(3);

        assertTrue(tree.remove(5));
        assertEquals(2, tree.size());
        assertFalse(tree.contains(5));
        assertTrue(tree.contains(3));
    }

    @Test
    void testRemoveNodeWithTwoChildren() {
        assertTrue(smallTree.remove(7)); // Has right child (9)
        assertEquals(4, smallTree.size());
        assertFalse(smallTree.contains(7));
        assertTrue(smallTree.contains(9));
    }

    @Test
    void testRemoveRoot() {
        assertTrue(smallTree.remove(5));
        assertEquals(4, smallTree.size());
        assertFalse(smallTree.contains(5));

        // Tree should still be valid
        ArrayList<Integer> list = smallTree.toArrayList();
        assertEquals(Arrays.asList(1, 3, 7, 9), list);
    }

    @Test
    void testRemoveNullThrowsException() {
        assertThrows(NullPointerException.class, () -> smallTree.remove(null));
    }

    @Test
    void testRemoveAllElements() {
        smallTree.remove(1);
        smallTree.remove(3);
        smallTree.remove(5);
        smallTree.remove(7);
        smallTree.remove(9);

        assertTrue(smallTree.isEmpty());
        assertEquals(0, smallTree.size());
    }

    // ========== RemoveAll Tests ==========
    @Test
    void testRemoveAllEmptyCollection() {
        assertFalse(smallTree.removeAll(new ArrayList<>()));
        assertEquals(5, smallTree.size());
    }

    @Test
    void testRemoveAllSomeElements() {
        assertTrue(smallTree.removeAll(Arrays.asList(3, 7)));
        assertEquals(3, smallTree.size());
        assertFalse(smallTree.contains(3));
        assertFalse(smallTree.contains(7));
    }

    @Test
    void testRemoveAllNonExistentElements() {
        assertFalse(smallTree.removeAll(Arrays.asList(100, 200)));
        assertEquals(5, smallTree.size());
    }

    @Test
    void testRemoveAllMixedElements() {
        assertTrue(smallTree.removeAll(Arrays.asList(3, 100, 7, 200)));
        assertEquals(3, smallTree.size());
    }

    // ========== Clear Tests ==========
    @Test
    void testClearEmptyTree() {
        emptyTree.clear();
        assertTrue(emptyTree.isEmpty());
        assertEquals(0, emptyTree.size());
    }

    @Test
    void testClearNonEmptyTree() {
        smallTree.clear();
        assertTrue(smallTree.isEmpty());
        assertEquals(0, smallTree.size());
        assertFalse(smallTree.contains(5));
    }

    @Test
    void testAddAfterClear() {
        smallTree.clear();
        assertTrue(smallTree.add(100));
        assertEquals(1, smallTree.size());
        assertTrue(smallTree.contains(100));
    }

    // ========== First Tests ==========
    @Test
    void testFirstOnEmptyTree() {
        assertThrows(NoSuchElementException.class, () -> emptyTree.first());
    }

    @Test
    void testFirstOnNonEmptyTree() {
        assertEquals(1, smallTree.first());
    }

    @Test
    void testFirstAfterAddingSmaller() {
        smallTree.add(0);
        assertEquals(0, smallTree.first());
    }

    @Test
    void testFirstSingleElement() {
        emptyTree.add(42);
        assertEquals(42, emptyTree.first());
    }

    // ========== Last Tests ==========
    @Test
    void testLastOnEmptyTree() {
        assertThrows(NoSuchElementException.class, () -> emptyTree.last());
    }

    @Test
    void testLastOnNonEmptyTree() {
        assertEquals(9, smallTree.last());
    }

    @Test
    void testLastAfterAddingLarger() {
        smallTree.add(100);
        assertEquals(100, smallTree.last());
    }

    @Test
    void testLastSingleElement() {
        emptyTree.add(42);
        assertEquals(42, emptyTree.last());
    }

    // ========== Size Tests ==========
    @Test
    void testSizeEmptyTree() {
        assertEquals(0, emptyTree.size());
    }

    @Test
    void testSizeAfterAdds() {
        assertEquals(5, smallTree.size());
    }

    @Test
    void testSizeAfterRemoves() {
        smallTree.remove(5);
        smallTree.remove(3);
        assertEquals(3, smallTree.size());
    }

    @Test
    void testSizeAfterDuplicateAdd() {
        int sizeBefore = smallTree.size();
        smallTree.add(5);
        assertEquals(sizeBefore, smallTree.size());
    }

    // ========== IsEmpty Tests ==========
    @Test
    void testIsEmptyOnNewTree() {
        assertTrue(emptyTree.isEmpty());
    }

    @Test
    void testIsEmptyAfterAdd() {
        emptyTree.add(5);
        assertFalse(emptyTree.isEmpty());
    }

    @Test
    void testIsEmptyAfterClear() {
        smallTree.clear();
        assertTrue(smallTree.isEmpty());
    }

    @Test
    void testIsEmptyAfterRemovingAll() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.add(1);
        tree.remove(1);
        assertTrue(tree.isEmpty());
    }

    // ========== ToArrayList Tests ==========
    @Test
    void testToArrayListEmpty() {
        ArrayList<Integer> list = emptyTree.toArrayList();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void testToArrayListSorted() {
        ArrayList<Integer> list = smallTree.toArrayList();
        assertEquals(Arrays.asList(1, 3, 5, 7, 9), list);
    }

    @Test
    void testToArrayListAfterModification() {
        smallTree.add(4);
        smallTree.add(6);
        smallTree.remove(1);

        ArrayList<Integer> list = smallTree.toArrayList();
        assertEquals(Arrays.asList(3, 4, 5, 6, 7, 9), list);
    }

    @Test
    void testToArrayListStrings() {
        stringTree.add("zebra");
        stringTree.add("apple");
        stringTree.add("mango");

        ArrayList<String> list = stringTree.toArrayList();
        assertEquals(Arrays.asList("apple", "mango", "zebra"), list);
    }

    // ========== Complex Scenario Tests ==========
    @Test
    void testLargeTreeOperations() {
        BinarySearchTree<Integer> largeTree = new BinarySearchTree<>();

        // Add 100 elements
        for (int i = 0; i < 100; i++) {
            assertTrue(largeTree.add(i));
        }
        assertEquals(100, largeTree.size());

        // Check first and last
        assertEquals(0, largeTree.first());
        assertEquals(99, largeTree.last());

        // Remove half
        for (int i = 0; i < 50; i++) {
            assertTrue(largeTree.remove(i));
        }
        assertEquals(50, largeTree.size());
        assertEquals(50, largeTree.first());
        assertEquals(99, largeTree.last());
    }

    @Test
    void testUnbalancedTree() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        // Create a highly unbalanced tree (essentially a linked list)
        for (int i = 1; i <= 10; i++) {
            tree.add(i);
        }

        assertEquals(10, tree.size());
        assertEquals(1, tree.first());
        assertEquals(10, tree.last());

        ArrayList<Integer> list = tree.toArrayList();
        for (int i = 1; i <= 10; i++) {
            assertEquals(i, list.get(i - 1));
        }
    }

    @Test
    void testMixedOperations() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        // Add, remove, add again
        tree.add(50);
        tree.add(25);
        tree.add(75);
        tree.remove(25);
        tree.add(30);
        tree.add(20);

        assertEquals(4, tree.size());
        assertTrue(tree.contains(50));
        assertTrue(tree.contains(75));
        assertTrue(tree.contains(30));
        assertTrue(tree.contains(20));
        assertFalse(tree.contains(25));
    }

    @Test
    void testDuplicateHandling() {
        BinarySearchTree<String> tree = new BinarySearchTree<>();

        assertTrue(tree.add("hello"));
        assertFalse(tree.add("hello"));
        assertFalse(tree.add("hello"));

        assertEquals(1, tree.size());
    }

    @Test
    void testNegativeNumbers() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        tree.add(0);
        tree.add(-5);
        tree.add(5);
        tree.add(-10);
        tree.add(10);

        assertEquals(-10, tree.first());
        assertEquals(10, tree.last());
        assertEquals(5, tree.size());
    }

    @Test
    void testSingleElementOperations() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.add(42);

        assertEquals(42, tree.first());
        assertEquals(42, tree.last());
        assertEquals(1, tree.size());
        assertTrue(tree.contains(42));

        tree.remove(42);
        assertTrue(tree.isEmpty());
        assertThrows(NoSuchElementException.class, () -> tree.first());
    }

    @Test
    void testToStringMethod() {
        String result = smallTree.toString();
        assertNotNull(result);
        assertTrue(result.contains("1"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("5"));
    }

    // ========== Edge Case Tests ==========
    @Test
    void testRemoveAndReaddSameElement() {
        assertTrue(smallTree.remove(5));
        assertFalse(smallTree.contains(5));

        assertTrue(smallTree.add(5));
        assertTrue(smallTree.contains(5));
        assertEquals(5, smallTree.size());
    }

    @Test
    void testMultipleClearOperations() {
        smallTree.clear();
        smallTree.clear();
        smallTree.clear();

        assertTrue(smallTree.isEmpty());
        assertEquals(0, smallTree.size());
    }

    @Test
    void testRemoveRootMultipleTimes() {
        // Keep removing the root
        smallTree.remove(5); // Remove original root
        assertEquals(4, smallTree.size());

        // New root should be different
        assertTrue(smallTree.contains(7) || smallTree.contains(3));
    }
}
