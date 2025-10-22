package assign07;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Unbalanced binary search tree that implements the SortedSet interface.
 * Stores unique, comparable items in sorted order.
 *
 * @param <Type> the type of elements in this tree
 */
public class BinarySearchTree<Type extends Comparable<? super Type>> implements SortedSet<Type> {

    private Node root;
    private int size;

    /** Node structure for tree elements. */
    private class Node {
        Type data;
        Node left, right;
        Node(Type data) { this.data = data; }
    }

    /** Adds an item to the tree if not already present. */
    @Override
    public boolean add(Type item) {
        Objects.requireNonNull(item);
        int before = size;
        root = insert(root, item);
        return size != before;
    }

    private Node insert(Node cur, Type item) {
        if (cur == null) {
            size++;
            return new Node(item);
        }
        int cmp = item.compareTo(cur.data);
        if (cmp < 0)
            cur.left = insert(cur.left, item);
        else if (cmp > 0)
            cur.right = insert(cur.right, item);
        return cur;
    }

    /** Adds all items from the collection. */
    @Override
    public boolean addAll(Collection<? extends Type> items) {
        boolean changed = false;
        for (Type t : items)
            changed |= add(t);
        return changed;
    }

    /** Removes all elements from the tree. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Checks if the tree contains the given item. */
    @Override
    public boolean contains(Type item) {
        Objects.requireNonNull(item);
        Node cur = root;
        while (cur != null) {
            int cmp = item.compareTo(cur.data);
            if (cmp == 0) return true;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        return false;
    }

    /** Checks if the tree contains all items in the given collection. */
    @Override
    public boolean containsAll(Collection<? extends Type> items) {
        for (Type t : items)
            if (!contains(t)) return false;
        return true;
    }

    /** Returns the smallest element in the tree. */
    @Override
    public Type first() {
        if (root == null)
            throw new NoSuchElementException("empty");
        Node cur = root;
        while (cur.left != null)
            cur = cur.left;
        return cur.data;
    }

    /** Returns true if the tree is empty. */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the largest element in the tree. */
    @Override
    public Type last() {
        if (root == null)
            throw new NoSuchElementException("empty");
        Node cur = root;
        while (cur.right != null)
            cur = cur.right;
        return cur.data;
    }

    private boolean removed;

    /** Removes an item from the tree if present. */
    @Override
    public boolean remove(Type item) {
        Objects.requireNonNull(item);
        removed = false;
        root = delete(root, item);
        if (removed) size--;
        return removed;
    }

    private Node delete(Node cur, Type key) {
        if (cur == null) return null;
        int cmp = key.compareTo(cur.data);
        if (cmp < 0)
            cur.left = delete(cur.left, key);
        else if (cmp > 0)
            cur.right = delete(cur.right, key);
        else {
            removed = true;
            if (cur.left == null) return cur.right;
            if (cur.right == null) return cur.left;
            Node succ = cur.right;
            while (succ.left != null) succ = succ.left;
            cur.data = succ.data;
            cur.right = deleteMin(cur.right);
        }
        return cur;
    }

    private Node deleteMin(Node cur) {
        if (cur.left == null) return cur.right;
        cur.left = deleteMin(cur.left);
        return cur;
    }

    /** Removes all items in the given collection. */
    @Override
    public boolean removeAll(Collection<? extends Type> items) {
        boolean changed = false;
        for (Type t : items)
            changed |= remove(t);
        return changed;
    }

    /** Returns the number of items in the tree. */
    @Override
    public int size() {
        return size;
    }

    /** Returns an ArrayList containing all items in sorted order. */
    @Override
    public ArrayList<Type> toArrayList() {
        ArrayList<Type> list = new ArrayList<>(size);
        inorder(root, list);
        return list;
    }

    private void inorder(Node cur, ArrayList<Type> out) {
        if (cur == null) return;
        inorder(cur.left, out);
        out.add(cur.data);
        inorder(cur.right, out);
    }

    /** Returns a string representation of the tree. */
    @Override
    public String toString() {
        return toArrayList().toString();
    }
}
