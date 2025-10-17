package assign06;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic singly linked list that implements the {@code List} interface.
 * This class maintains elements in sequential order and provides operations
 * for insertion, deletion, retrieval, and traversal using an iterator.
 *
 * <p>The list uses zero-based indexing, where the first element has an index of 0.</p>
 *
 * @param <E> the type of elements stored in this list
 * 
 * @author Tyler Gagliardi
 * @author Alex Waldmann
 * @version October 2025
 */
public class SinglyLinkedList<E> implements List<E> {

    /**
     * Represents a single node within the singly linked list.
     * Each node holds an element and a reference to the next node.
     *
     * @param <E> the type of element stored in this node
     */
    private static final class Node<E> {
        E data;
        Node<E> next;

        /**
         * Constructs a new node with the specified element and reference to the next node.
         *
         * @param data the element stored in this node
         * @param next the next node in the list
         */
        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    /** Reference to the first node in the list. */
    private Node<E> head;

    /** The number of elements currently in the list. */
    private int size;

    /**
     * Constructs an empty singly linked list.
     */
    public SinglyLinkedList() {
        head = null;
        size = 0;
    }

    /**
     * Inserts a new element at the beginning of the list.
     *
     * @param element the element to insert
     */
    @Override
    public void insertFirst(E element) {
        head = new Node<>(element, head);
        size++;
    }

    /**
     * Inserts a new element at a specified position in the list.
     *
     * @param index   the index at which the element should be inserted (0-based)
     * @param element the element to insert
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public void insert(int index, E element) throws IndexOutOfBoundsException {
        checkPositionIndex(index);
        if (index == 0) {
            insertFirst(element);
            return;
        }
        Node<E> prev = nodeAt(index - 1);
        prev.next = new Node<>(element, prev.next);
        size++;
    }

    /**
     * Retrieves the first element in the list.
     *
     * @return the first element
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E getFirst() throws NoSuchElementException {
        if (head == null)
            throw new NoSuchElementException("List is empty");
        return head.data;
    }

    /**
     * Retrieves the element at the specified index.
     *
     * @param index the index of the element to retrieve
     * @return the element at the given index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public E get(int index) throws IndexOutOfBoundsException {
        checkElementIndex(index);
        return nodeAt(index).data;
    }

    /**
     * Removes and returns the first element in the list.
     *
     * @return the removed element
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E deleteFirst() throws NoSuchElementException {
        if (head == null)
            throw new NoSuchElementException("List is empty");
        E val = head.data;
        head = head.next;
        size--;
        return val;
    }

    /**
     * Removes and returns the element at the specified index.
     *
     * @param index the index of the element to remove
     * @return the removed element
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public E delete(int index) throws IndexOutOfBoundsException {
        checkElementIndex(index);
        if (index == 0)
            return deleteFirst();
        Node<E> prev = nodeAt(index - 1);
        Node<E> target = prev.next;
        prev.next = target.next;
        size--;
        return target.data;
    }

    /**
     * Finds the index of the first occurrence of the specified element.
     *
     * @param element the element to search for
     * @return the index of the element, or -1 if not found
     */
    @Override
    public int indexOf(E element) {
        Node<E> curr = head;
        int i = 0;
        if (element == null) {
            while (curr != null) {
                if (curr.data == null)
                    return i;
                curr = curr.next;
                i++;
            }
        } else {
            while (curr != null) {
                if (element.equals(curr.data))
                    return i;
                curr = curr.next;
                i++;
            }
        }
        return -1;
    }

    /**
     * Returns the number of elements in the list.
     *
     * @return the list size
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Checks whether the list is empty.
     *
     * @return true if the list contains no elements; false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all elements from the list, leaving it empty.
     */
    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Returns an array containing all elements in the list in order.
     *
     * @return an array representation of the list
     */
    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node<E> curr = head;
        int i = 0;
        while (curr != null) {
            arr[i++] = curr.data;
            curr = curr.next;
        }
        return arr;
    }

    /**
     * Returns an iterator that traverses the list from first to last element.
     *
     * @return an iterator over the elements in this list
     */
    @Override
    public Iterator<E> iterator() {
        return new SinglyLinkedListIterator();
    }

    /**
     * Iterator implementation for the singly linked list.
     * Provides sequential access and supports element removal during iteration.
     */
    private final class SinglyLinkedListIterator implements Iterator<E> {
        private Node<E> next = head;
        private Node<E> prev = null;
        private Node<E> lastReturned = null;
        private Node<E> lastReturnedPrev = null;
        private boolean canRemove = false;

        /**
         * Checks if the iteration has more elements.
         *
         * @return true if another element exists; false otherwise
         */
        @Override
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element
         * @throws NoSuchElementException if no elements remain
         */
        @Override
        public E next() {
            if (next == null)
                throw new NoSuchElementException();
            lastReturnedPrev = prev;
            lastReturned = next;
            prev = next;
            next = next.next;
            canRemove = true;
            return lastReturned.data;
        }

        /**
         * Removes the last element returned by {@code next()}.
         *
         * @throws IllegalStateException if {@code next()} has not been called or
         *                               {@code remove()} was already called after the last {@code next()}
         */
        @Override
        public void remove() {
            if (!canRemove)
                throw new IllegalStateException();
            if (lastReturnedPrev == null) {
                head = lastReturned.next;
            } else {
                lastReturnedPrev.next = lastReturned.next;
            }
            size--;
            prev = lastReturnedPrev;
            lastReturned = null;
            canRemove = false;
        }
    }

    /**
     * Validates an index for element access (0 ≤ index < size).
     *
     * @param index the index to validate
     * @throws IndexOutOfBoundsException if the index is outside valid range
     */
    private void checkElementIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    /**
     * Validates an index for insertion (0 ≤ index ≤ size).
     *
     * @param index the index to validate
     * @throws IndexOutOfBoundsException if the index is outside valid range
     */
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    /**
     * Retrieves the node at the specified index.
     *
     * @param index the index of the node to retrieve
     * @return the node at the specified index
     */
    private Node<E> nodeAt(int index) {
        Node<E> curr = head;
        for (int i = 0; i < index; i++)
            curr = curr.next;
        return curr;
    }
}
