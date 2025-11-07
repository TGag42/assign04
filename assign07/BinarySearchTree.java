package assign07;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Unbalanced binary search tree that implements the SortedSet interface. Stores
 * unique, comparable items in sorted order using BST property: left subtree
 * contains smaller items, right subtree contains larger items.
 *
 * This implementation does not maintain balance, so operations may degrade to
 * O(N) in worst case with skewed insertions.
 *
 * @param <Type> the type of elements in this tree, must be Comparable
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 23, 2025
 */
public class BinarySearchTree<Type extends Comparable<? super Type>> implements SortedSet<Type> {

    private Node root;
    private int size;

    /**
     * Inner class representing a node in the binary search tree. Each node
     * contains data and references to left and right children.
     */
    private class Node {

        Type data;
        Node left, right;

        /**
         * Constructs a node with the given data and null children.
         *
         * @param data the data to store in this node
         */
        Node(Type data) {
            this.data = data;
        }
    }

    /**
     * Ensures that this set contains the specified item. Does not add duplicate
     * items.
     *
     * @param item the item whose presence is ensured in this set
     * @return true if this set changed (item was added), false otherwise
     * @throws NullPointerException if item is null
     */
    @Override
    public boolean add(Type item) {
        Objects.requireNonNull(item);
        int before = size;
        root = insert(root, item);
        return size != before;
    }

    /**
     * Recursive helper method to insert an item into the tree.
     *
     * @param cur the current node being examined
     * @param item the item to insert
     * @return the root of the subtree after insertion
     */
    private Node insert(Node cur, Type item) {
        if (cur == null) {
            size++;
            return new Node(item);
        }
        int cmp = item.compareTo(cur.data);
        if (cmp < 0) {
            cur.left = insert(cur.left, item); 
        }else if (cmp > 0) {
            cur.right = insert(cur.right, item);
        }
        return cur;
    }

    /**
     * Ensures that this set contains all items in the specified collection.
     *
     * @param items the collection of items whose presence is ensured in this
     * set
     * @return true if this set changed as a result of this call, false
     * otherwise
     */
    @Override
    public boolean addAll(Collection<? extends Type> items) {
        boolean changed = false;
        for (Type t : items) {
            changed |= add(t);
        }
        return changed;
    }

    /**
     * Removes all items from this set. The set will be empty after this call.
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Determines if there is an item in this set that is equal to the specified
     * item.
     *
     * @param item the item sought in this set
     * @return true if the item is in this set, false otherwise
     * @throws NullPointerException if item is null
     */
    @Override
    public boolean contains(Type item) {
        Objects.requireNonNull(item);
        Node cur = root;
        while (cur != null) {
            int cmp = item.compareTo(cur.data);
            if (cmp == 0) {
                return true;
            }
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        return false;
    }

    /**
     * Determines if for each item in the specified collection, there is an item
     * in this set that is equal to it.
     *
     * @param items the collection of items sought in this set
     * @return true if all items in the collection are in this set, false
     * otherwise
     */
    @Override
    public boolean containsAll(Collection<? extends Type> items) {
        for (Type t : items) {
            if (!contains(t)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the first (i.e., smallest) item in this set.
     *
     * @return the smallest item in this set
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public Type first() {
        if (root == null) {
            throw new NoSuchElementException("empty");
        }
        Node cur = root;
        while (cur.left != null) {
            cur = cur.left;
        }
        return cur.data;
    }

    /**
     * Returns true if this set contains no items.
     *
     * @return true if this set is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the last (i.e., largest) item in this set.
     *
     * @return the largest item in this set
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public Type last() {
        if (root == null) {
            throw new NoSuchElementException("empty");
        }
        Node cur = root;
        while (cur.right != null) {
            cur = cur.right;
        }
        return cur.data;
    }

    private boolean removed;

    /**
     * Ensures that this set does not contain the specified item.
     *
     * @param item the item whose absence is ensured in this set
     * @return true if this set changed (item was removed), false otherwise
     * @throws NullPointerException if item is null
     */
    @Override
    public boolean remove(Type item) {
        Objects.requireNonNull(item);
        removed = false;
        root = delete(root, item);
        if (removed) {
            size--;
        }
        return removed;
    }

    /**
     * Recursive helper method to delete an item from the tree. Uses in-order
     * successor replacement for nodes with two children.
     *
     * @param cur the current node being examined
     * @param key the item to delete
     * @return the root of the subtree after deletion
     */
    private Node delete(Node cur, Type key) {
        if (cur == null) {
            return null;
        }
        int cmp = key.compareTo(cur.data);
        if (cmp < 0) {
            cur.left = delete(cur.left, key); 
        }else if (cmp > 0) {
            cur.right = delete(cur.right, key); 
        }else {
            removed = true;
            if (cur.left == null) {
                return cur.right;
            }
            if (cur.right == null) {
                return cur.left;
            }
            Node succ = cur.right;
            while (succ.left != null) {
                succ = succ.left;
            }
            cur.data = succ.data;
            cur.right = deleteMin(cur.right);
        }
        return cur;
    }

    /**
     * Recursive helper method to delete the minimum (leftmost) node from a
     * subtree.
     *
     * @param cur the root of the subtree
     * @return the root of the subtree after deletion
     */
    private Node deleteMin(Node cur) {
        if (cur.left == null) {
            return cur.right;
        }
        cur.left = deleteMin(cur.left);
        return cur;
    }

    /**
     * Ensures that this set does not contain any of the items in the specified
     * collection.
     *
     * @param items the collection of items whose absence is ensured in this set
     * @return true if this set changed as a result of this call, false
     * otherwise
     */
    @Override
    public boolean removeAll(Collection<? extends Type> items) {
        boolean changed = false;
        for (Type t : items) {
            changed |= remove(t);
        }
        return changed;
    }

    /**
     * Returns the number of items in this set.
     *
     * @return the number of items in this set
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns an ArrayList containing all of the items in this set, in sorted
     * order.
     *
     * @return an ArrayList of all items in sorted (ascending) order
     */
    @Override
    public ArrayList<Type> toArrayList() {
        ArrayList<Type> list = new ArrayList<>(size);
        inorder(root, list);
        return list;
    }

    /**
     * Recursive helper method to perform in-order traversal and build the
     * ArrayList. In-order traversal visits nodes in ascending order.
     *
     * @param cur the current node being visited
     * @param out the ArrayList to add elements to
     */
    private void inorder(Node cur, ArrayList<Type> out) {
        if (cur == null) {
            return;
        }
        inorder(cur.left, out);
        out.add(cur.data);
        inorder(cur.right, out);
    }

    /**
     * Returns a string representation of the tree in sorted order.
     *
     * @return a string representation of this tree
     */
    @Override
    public String toString() {
        return toArrayList().toString();
    }
}
