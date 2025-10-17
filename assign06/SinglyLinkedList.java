package assign06;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic singly-linked list that implements the List interface.
 *
 * @author Tyler Gagliardi
 * @version October 2025
 * 
 * @param <E> element type
 */
public class SinglyLinkedList<E> implements List<E> {

    /**
     * Node in the singly linked list.
     *
     * @param <E> element type
     */
    private static final class Node<E> {
        E data;
        Node<E> next;

        /**
         * Creates a node.
         *
         * @param data element stored
         * @param next next node
         */
        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    /** First node in the list. */
    private Node<E> head;

    /** Number of elements in the list. */
    private int size;

    /**
     * Constructs an empty list.
     */
    public SinglyLinkedList() {
        head = null;
        size = 0;
    }

    /**
     * Inserts an element at the beginning.
     *
     * @param element element to insert
     */
    @Override
    public void insertFirst(E element) {
        head = new Node<>(element, head);
        size++;
    }

    /**
     * Inserts an element at a position.
     *
     * @param index position to insert at (0-based, inclusive)
     * @param element element to insert
     * @throws IndexOutOfBoundsException if index is invalid
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
     * Returns the first element.
     *
     * @return first element
     * @throws NoSuchElementException if empty
     */
    @Override
    public E getFirst() throws NoSuchElementException {
        if (head == null) throw new NoSuchElementException("List is empty");
        return head.data;
    }

    /**
     * Returns the element at an index.
     *
     * @param index index to get
     * @return element at index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    @Override
    public E get(int index) throws IndexOutOfBoundsException {
        checkElementIndex(index);
        return nodeAt(index).data;
    }

    /**
     * Deletes and returns the first element.
     *
     * @return removed element
     * @throws NoSuchElementException if empty
     */
    @Override
    public E deleteFirst() throws NoSuchElementException {
        if (head == null) throw new NoSuchElementException("List is empty");
        E val = head.data;
        head = head.next;
        size--;
        return val;
    }

    /**
     * Deletes and returns the element at an index.
     *
     * @param index index to delete
     * @return removed element
     * @throws IndexOutOfBoundsException if index is invalid
     */
    @Override
    public E delete(int index) throws IndexOutOfBoundsException {
        checkElementIndex(index);
        if (index == 0) return deleteFirst();
        Node<E> prev = nodeAt(index - 1);
        Node<E> target = prev.next;
        prev.next = target.next;
        size--;
        return target.data;
    }

    /**
     * Returns the index of the first occurrence of an element.
     *
     * @param element element to find
     * @return index or -1 if not found
     */
    @Override
    public int indexOf(E element) {
        Node<E> curr = head;
        int i = 0;
        if (element == null) {
            while (curr != null) {
                if (curr.data == null) return i;
                curr = curr.next; i++;
            }
        } else {
            while (curr != null) {
                if (element.equals(curr.data)) return i;
                curr = curr.next; i++;
            }
        }
        return -1;
    }

    /**
     * Returns the number of elements.
     *
     * @return size
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns whether the list is empty.
     *
     * @return true if empty
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all elements.
     */
    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Returns an array of all elements in order.
     *
     * @return array of elements
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
     * Returns an iterator over the elements from first to last.
     *
     * @return iterator
     */
    @Override
    public Iterator<E> iterator() {
        return new SinglyLinkedListIterator();
    }

    /**
     * Iterator over the list that supports removal.
     */
    private final class SinglyLinkedListIterator implements Iterator<E> {
        private Node<E> next = head;
        private Node<E> prev = null;
        private Node<E> lastReturned = null;
        private Node<E> lastReturnedPrev = null;
        private boolean canRemove = false;

        /**
         * Returns whether another element exists.
         *
         * @return true if another element is available
         */
        @Override
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Returns the next element.
         *
         * @return next element
         * @throws NoSuchElementException if none remain
         */
        @Override
        public E next() {
            if (next == null) throw new NoSuchElementException();
            lastReturnedPrev = prev;
            lastReturned = next;
            prev = next;
            next = next.next;
            canRemove = true;
            return lastReturned.data;
        }

        /**
         * Removes the last element returned by next().
         *
         * @throws IllegalStateException if next() not called or remove() already used
         */
        @Override
        public void remove() {
            if (!canRemove) throw new IllegalStateException();
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
     * Checks an index for element access (0 <= index < size).
     *
     * @param index index to check
     * @throws IndexOutOfBoundsException if invalid
     */
    private void checkElementIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    /**
     * Checks an index for insertion (0 <= index <= size).
     *
     * @param index index to check
     * @throws IndexOutOfBoundsException if invalid
     */
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    /**
     * Returns the node at an index.
     *
     * @param index index to locate
     * @return node at index
     */
    private Node<E> nodeAt(int index) {
        Node<E> curr = head;
        for (int i = 0; i < index; i++) curr = curr.next;
        return curr;
    }
}
