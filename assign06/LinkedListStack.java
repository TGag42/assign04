package assign06;

import java.util.NoSuchElementException;

/**
 * A generic last-in-first-out (LIFO) stack implemented using a singly linked list
 * as the underlying data structure. Each element added is placed at the top of
 * the stack and can be removed or accessed in reverse order of insertion.
 *
 * @param <E> the type of elements stored in this stack
 * 
 * @author Tyler Gagliardi
 * @author Alex Waldmann
 */
public class LinkedListStack<E> implements Stack<E> {

    /** The singly linked list that stores all elements in the stack. */
    private final SinglyLinkedList<E> list;

    /**
     * Constructs an empty stack with no elements.
     */
    public LinkedListStack() {
        this.list = new SinglyLinkedList<>();
    }

    /**
     * Removes all elements from the stack, leaving it empty.
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * Checks whether the stack is empty.
     *
     * @return true if the stack contains no elements; false otherwise
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Retrieves, but does not remove, the element at the top of the stack.
     *
     * @return the element currently at the top of the stack
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public E peek() throws NoSuchElementException {
        return list.getFirst();
    }

    /**
     * Removes and returns the element at the top of the stack.
     *
     * @return the element removed from the top of the stack
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public E pop() throws NoSuchElementException {
        return list.deleteFirst();
    }

    /**
     * Adds an element to the top of the stack.
     *
     * @param element the element to push onto the stack
     */
    @Override
    public void push(E element) {
        list.insertFirst(element);
    }

    /**
     * Returns the total number of elements currently in the stack.
     *
     * @return the number of elements in the stack
     */
    @Override
    public int size() {
        return list.size();
    }
}
