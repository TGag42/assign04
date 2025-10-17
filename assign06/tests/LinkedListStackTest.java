package assign06;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test suite for LinkedListStack.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 16, 2025
 */
public class LinkedListStackTest {

    private LinkedListStack<Integer> stack;
    private LinkedListStack<String> stringStack;

    @BeforeEach
    void setUp() {
        stack = new LinkedListStack<>();
        stringStack = new LinkedListStack<>();
    }

    // ========== Constructor Tests ==========
    @Test
    void testConstructor() {
        assertEquals(0, stack.size());
        assertTrue(stack.isEmpty());
    }

    // ========== push Tests ==========
    @Test
    void testPushSingle() {
        stack.push(1);
        assertEquals(1, stack.size());
        assertEquals(1, stack.peek());
    }

    @Test
    void testPushMultiple() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.size());
        assertEquals(3, stack.peek());
    }

    @Test
    void testPushNull() {
        stack.push(null);
        assertEquals(1, stack.size());
        assertNull(stack.peek());
    }

    @Test
    void testPushManyElements() {
        for (int i = 0; i < 100; i++) {
            stack.push(i);
        }
        assertEquals(100, stack.size());
        assertEquals(99, stack.peek());
    }

    // ========== pop Tests ==========
    @Test
    void testPopOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }

    @Test
    void testPopSingle() {
        stack.push(42);
        assertEquals(42, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void testPopMultiple() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void testPopLIFOOrder() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop()); // Last in
        assertEquals(2, stack.peek());
    }

    @Test
    void testPopNull() {
        stack.push(1);
        stack.push(null);
        assertNull(stack.pop());
        assertEquals(1, stack.pop());
    }

    // ========== peek Tests ==========
    @Test
    void testPeekOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }

    @Test
    void testPeekDoesNotRemove() {
        stack.push(42);
        assertEquals(42, stack.peek());
        assertEquals(42, stack.peek()); // Should still be there
        assertEquals(1, stack.size());
    }

    @Test
    void testPeekAfterPush() {
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.peek());
        stack.push(3);
        assertEquals(3, stack.peek());
    }

    @Test
    void testPeekAfterPop() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.pop();
        assertEquals(2, stack.peek());
    }

    // ========== isEmpty Tests ==========
    @Test
    void testIsEmptyOnNew() {
        assertTrue(stack.isEmpty());
    }

    @Test
    void testIsEmptyAfterPush() {
        stack.push(1);
        assertFalse(stack.isEmpty());
    }

    @Test
    void testIsEmptyAfterPushAndPop() {
        stack.push(1);
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    void testIsEmptyAfterClear() {
        stack.push(1);
        stack.push(2);
        stack.clear();
        assertTrue(stack.isEmpty());
    }

    // ========== size Tests ==========
    @Test
    void testSizeEmpty() {
        assertEquals(0, stack.size());
    }

    @Test
    void testSizeAfterPushes() {
        stack.push(1);
        assertEquals(1, stack.size());
        stack.push(2);
        assertEquals(2, stack.size());
        stack.push(3);
        assertEquals(3, stack.size());
    }

    @Test
    void testSizeAfterPops() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.pop();
        assertEquals(2, stack.size());
        stack.pop();
        assertEquals(1, stack.size());
    }

    @Test
    void testSizeAfterMixedOperations() {
        stack.push(1);
        stack.push(2);
        stack.pop();
        stack.push(3);
        stack.push(4);
        stack.pop();
        assertEquals(2, stack.size());
    }

    // ========== clear Tests ==========
    @Test
    void testClearOnEmpty() {
        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void testClearOnPopulated() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.clear();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void testClearAndReuse() {
        stack.push(1);
        stack.push(2);
        stack.clear();
        stack.push(3);
        assertEquals(1, stack.size());
        assertEquals(3, stack.peek());
    }

    @Test
    void testClearMultipleTimes() {
        stack.push(1);
        stack.clear();
        stack.clear(); // Should not cause issues
        assertTrue(stack.isEmpty());
    }

    // ========== Stack Behavior Tests ==========
    @Test
    void testLIFOBehavior() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        assertEquals(4, stack.pop());
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void testInterleavedPushPop() {
        stack.push(1);
        assertEquals(1, stack.pop());
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        stack.push(4);
        assertEquals(4, stack.pop());
        assertEquals(2, stack.pop());
    }

    @Test
    void testPushPopSequence() {
        for (int i = 0; i < 10; i++) {
            stack.push(i);
        }
        for (int i = 9; i >= 0; i--) {
            assertEquals(i, stack.pop());
        }
        assertTrue(stack.isEmpty());
    }

    // ========== String Stack Tests ==========
    @Test
    void testStringStack() {
        stringStack.push("first");
        stringStack.push("second");
        stringStack.push("third");
        assertEquals("third", stringStack.pop());
        assertEquals("second", stringStack.peek());
    }

    @Test
    void testStringStackEmpty() {
        assertThrows(NoSuchElementException.class, () -> stringStack.pop());
    }

    // ========== Edge Cases ==========
    @Test
    void testLargeStack() {
        for (int i = 0; i < 1000; i++) {
            stack.push(i);
        }
        assertEquals(1000, stack.size());
        for (int i = 999; i >= 0; i--) {
            assertEquals(i, stack.pop());
        }
        assertTrue(stack.isEmpty());
    }

    @Test
    void testAlternatingOperations() {
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.pop());
        stack.push(3);
        assertEquals(3, stack.peek());
        assertEquals(3, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void testStackWithDuplicates() {
        stack.push(1);
        stack.push(1);
        stack.push(1);
        assertEquals(3, stack.size());
        assertEquals(1, stack.pop());
        assertEquals(1, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void testStackOrderPreservation() {
        // Push sequence
        for (int i = 1; i <= 5; i++) {
            stack.push(i);
        }

        // Verify LIFO order
        assertEquals(5, stack.pop());
        assertEquals(4, stack.pop());

        // Push more
        stack.push(6);
        stack.push(7);

        // Continue verification
        assertEquals(7, stack.pop());
        assertEquals(6, stack.pop());
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void testEmptyAfterFullCycle() {
        stack.push(1);
        stack.pop();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        assertThrows(NoSuchElementException.class, () -> stack.peek());
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }
}
