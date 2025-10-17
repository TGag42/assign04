package assign06;

import java.util.NoSuchElementException;

/**
 * A generic last-in-first-out (LIFO) stack implemented on top of a
 * singly-linked list.
 *
 * @param <E> element type stored in the stack
 */
public class LinkedListStack<E> implements Stack<E> {

    /** Backing storage for the stack; the list's head represents the stack top. */
    private final SinglyLinkedList<E> list;

    /**
     * Constructs an empty stack.
     */
    public LinkedListStack() {
        this.list = new SinglyLinkedList<>();
    }

    /**
     * Removes all elements from the stack.
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * Reports whether the stack contains no elements.
     *
     * @return true if empty; false otherwise
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Returns, but does not remove, the element at the top of the stack.
     *
     * @return the top element
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public E peek() throws NoSuchElementException {
        return list.getFirst();
    }

    /**
     * Returns and removes the element at the top of the stack.
     *
     * @return the removed top element
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public E pop() throws NoSuchElementException {
        return list.deleteFirst();
    }

    /**
     * Pushes an element onto the top of the stack.
     *
     * @param element element to add
     */
    @Override
    public void push(E element) {
        list.insertFirst(element);
    }

    /**
     * Returns the number of elements in the stack.
     *
     * @return current size
     */
    @Override
    public int size() {
        return list.size();
    }
}
