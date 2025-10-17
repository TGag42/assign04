package assign06;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for SinglyLinkedList.
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version October 16, 2025
 */
public class SinglyLinkedListTest {

    private SinglyLinkedList<Integer> list;
    private SinglyLinkedList<String> stringList;

    @BeforeEach
    void setUp() {
        list = new SinglyLinkedList<>();
        stringList = new SinglyLinkedList<>();
    }

    // ========== Constructor Tests ==========
    @Test
    void testConstructor() {
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    // ========== insertFirst Tests ==========
    @Test
    void testInsertFirstOnEmpty() {
        list.insertFirst(1);
        assertEquals(1, list.size());
        assertEquals(1, list.getFirst());
    }

    @Test
    void testInsertFirstMultiple() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(3, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    void testInsertFirstNull() {
        list.insertFirst(null);
        assertEquals(1, list.size());
        assertNull(list.getFirst());
    }

    // ========== insert Tests ==========
    @Test
    void testInsertAtBeginning() {
        list.insertFirst(2);
        list.insert(0, 1);
        assertEquals(1, list.getFirst());
        assertEquals(2, list.get(1));
    }

    @Test
    void testInsertAtEnd() {
        list.insertFirst(1);
        list.insert(1, 2);
        assertEquals(2, list.size());
        assertEquals(2, list.get(1));
    }

    @Test
    void testInsertInMiddle() {
        list.insertFirst(3);
        list.insertFirst(1);
        list.insert(1, 2);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    void testInsertOnEmpty() {
        list.insert(0, 42);
        assertEquals(1, list.size());
        assertEquals(42, list.getFirst());
    }

    @Test
    void testInsertInvalidIndexNegative() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(-1, 1));
    }

    @Test
    void testInsertInvalidIndexTooLarge() {
        list.insertFirst(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(2, 2));
    }

    // ========== getFirst Tests ==========
    @Test
    void testGetFirstOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
    }

    @Test
    void testGetFirstAfterInserts() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(1, list.getFirst());
    }

    // ========== get Tests ==========
    @Test
    void testGetValidIndices() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    void testGetInvalidIndexNegative() {
        list.insertFirst(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void testGetInvalidIndexTooLarge() {
        list.insertFirst(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @Test
    void testGetOnEmpty() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    // ========== deleteFirst Tests ==========
    @Test
    void testDeleteFirstOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> list.deleteFirst());
    }

    @Test
    void testDeleteFirstSingleElement() {
        list.insertFirst(42);
        assertEquals(42, list.deleteFirst());
        assertTrue(list.isEmpty());
    }

    @Test
    void testDeleteFirstMultiple() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(1, list.deleteFirst());
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
    }

    // ========== delete Tests ==========
    @Test
    void testDeleteAtBeginning() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(1, list.delete(0));
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
    }

    @Test
    void testDeleteAtEnd() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(3, list.delete(2));
        assertEquals(2, list.size());
    }

    @Test
    void testDeleteInMiddle() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(2, list.delete(1));
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    void testDeleteInvalidIndexNegative() {
        list.insertFirst(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(-1));
    }

    @Test
    void testDeleteInvalidIndexTooLarge() {
        list.insertFirst(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(1));
    }

    @Test
    void testDeleteOnEmpty() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(0));
    }

    // ========== indexOf Tests ==========
    @Test
    void testIndexOfFound() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(0, list.indexOf(1));
        assertEquals(1, list.indexOf(2));
        assertEquals(2, list.indexOf(3));
    }

    @Test
    void testIndexOfNotFound() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(-1, list.indexOf(99));
    }

    @Test
    void testIndexOfEmpty() {
        assertEquals(-1, list.indexOf(1));
    }

    @Test
    void testIndexOfNull() {
        list.insertFirst(3);
        list.insertFirst(null);
        list.insertFirst(1);
        assertEquals(1, list.indexOf(null));
    }

    @Test
    void testIndexOfNullNotFound() {
        list.insertFirst(1);
        list.insertFirst(2);
        assertEquals(-1, list.indexOf(null));
    }

    @Test
    void testIndexOfFirstOccurrence() {
        list.insertFirst(1);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals(0, list.indexOf(1)); // Should return first occurrence
    }

    // ========== size Tests ==========
    @Test
    void testSizeEmpty() {
        assertEquals(0, list.size());
    }

    @Test
    void testSizeAfterInserts() {
        list.insertFirst(1);
        assertEquals(1, list.size());
        list.insertFirst(2);
        assertEquals(2, list.size());
        list.insert(1, 3);
        assertEquals(3, list.size());
    }

    @Test
    void testSizeAfterDeletes() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        list.deleteFirst();
        assertEquals(2, list.size());
        list.delete(1);
        assertEquals(1, list.size());
    }

    // ========== isEmpty Tests ==========
    @Test
    void testIsEmptyOnNewList() {
        assertTrue(list.isEmpty());
    }

    @Test
    void testIsEmptyAfterInsert() {
        list.insertFirst(1);
        assertFalse(list.isEmpty());
    }

    @Test
    void testIsEmptyAfterClear() {
        list.insertFirst(1);
        list.clear();
        assertTrue(list.isEmpty());
    }

    // ========== clear Tests ==========
    @Test
    void testClearOnEmpty() {
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void testClearOnPopulatedList() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void testClearAndReuse() {
        list.insertFirst(1);
        list.clear();
        list.insertFirst(2);
        assertEquals(1, list.size());
        assertEquals(2, list.getFirst());
    }

    // ========== toArray Tests ==========
    @Test
    void testToArrayEmpty() {
        Object[] arr = list.toArray();
        assertEquals(0, arr.length);
    }

    @Test
    void testToArraySingleElement() {
        list.insertFirst(42);
        Object[] arr = list.toArray();
        assertEquals(1, arr.length);
        assertEquals(42, arr[0]);
    }

    @Test
    void testToArrayMultipleElements() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        Object[] arr = list.toArray();
        assertEquals(3, arr.length);
        assertEquals(1, arr[0]);
        assertEquals(2, arr[1]);
        assertEquals(3, arr[2]);
    }

    @Test
    void testToArrayWithNull() {
        list.insertFirst(2);
        list.insertFirst(null);
        list.insertFirst(1);
        Object[] arr = list.toArray();
        assertEquals(3, arr.length);
        assertEquals(1, arr[0]);
        assertNull(arr[1]);
        assertEquals(2, arr[2]);
    }

    // ========== Iterator Tests ==========
    @Test
    void testIteratorEmpty() {
        Iterator<Integer> iter = list.iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    void testIteratorHasNext() {
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        assertTrue(iter.hasNext());
    }

    @Test
    void testIteratorNext() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        assertEquals(1, iter.next());
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    void testIteratorNextThrowsException() {
        Iterator<Integer> iter = list.iterator();
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    @Test
    void testIteratorRemoveFirst() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        iter.next(); // Move to first element
        iter.remove();
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
    }

    @Test
    void testIteratorRemoveMiddle() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        iter.next(); // 1
        iter.next(); // 2
        iter.remove();
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    void testIteratorRemoveLast() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        iter.next(); // 1
        iter.next(); // 2
        iter.next(); // 3
        iter.remove();
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    void testIteratorRemoveWithoutNext() {
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        assertThrows(IllegalStateException.class, () -> iter.remove());
    }

    @Test
    void testIteratorRemoveTwice() {
        list.insertFirst(2);
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        iter.next();
        iter.remove();
        assertThrows(IllegalStateException.class, () -> iter.remove());
    }

    @Test
    void testIteratorRemoveAll() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
        assertTrue(list.isEmpty());
    }

    @Test
    void testIteratorWithStrings() {
        stringList.insertFirst("c");
        stringList.insertFirst("b");
        stringList.insertFirst("a");
        Iterator<String> iter = stringList.iterator();
        assertEquals("a", iter.next());
        assertEquals("b", iter.next());
        assertEquals("c", iter.next());
    }

    @Test
    void testIteratorRemoveAndContinue() {
        list.insertFirst(4);
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        Iterator<Integer> iter = list.iterator();
        iter.next(); // 1
        iter.next(); // 2
        iter.remove(); // Remove 2
        assertEquals(3, iter.next()); // Should continue with 3
        assertEquals(4, iter.next()); // Then 4
        assertFalse(iter.hasNext());
    }

    // ========== Edge Case Tests ==========
    @Test
    void testLargeList() {
        for (int i = 0; i < 1000; i++) {
            list.insertFirst(i);
        }
        assertEquals(1000, list.size());
        assertEquals(999, list.getFirst());
        assertEquals(0, list.get(999));
    }

    @Test
    void testAlternatingOperations() {
        list.insertFirst(1);
        assertEquals(1, list.deleteFirst());
        list.insertFirst(2);
        list.insertFirst(3);
        assertEquals(3, list.deleteFirst());
        assertEquals(1, list.size());
    }
}
