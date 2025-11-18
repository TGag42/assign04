package assign09;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A hash table implementation of the Map<K, V> interface using separate
 * chaining. Collisions are resolved by storing entries in a linked list at each
 * bucket.
 *
 * Average-case behavior (assuming a good hash function): - put, get, remove,
 * containsKey: O(1) - containsValue, keys, values, entries: O(N)
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 13, 2025
 *
 * @param <K> key type
 * @param <V> value type
 */
public class HashTable<K, V> implements Map<K, V> {

    /**
     * Backing "array" of chains.
     */
    private ArrayList<LinkedList<MapEntry<K, V>>> table;

    /**
     * Number of key:value pairs currently stored.
     */
    private int size;

    /**
     * Current capacity (table length).
     */
    private int capacity;

    /**
     * Maximum allowed load factor before rehashing (λ ≤ 10.0 per assignment).
     */
    private static final double MAX_LOAD_FACTOR = 10.0;

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 101;

    /**
     * Constructs an empty HashTable with default capacity.
     */
    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs an empty HashTable with the given initial capacity.
     *
     * @param initialCapacity initial number of buckets
     */
    public HashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.table = new ArrayList<LinkedList<MapEntry<K, V>>>(capacity);
        for (int i = 0; i < capacity; i++) {
            table.add(new LinkedList<MapEntry<K, V>>());
        }
    }

    /**
     * Removes all mappings from this map.
     *
     * O(table length)
     */
    @Override
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table.get(i).clear();
        }
        size = 0;
    }

    /**
     * Determines whether this map contains the specified key.
     *
     * Average-case: O(1)
     *
     * @param key the key being searched for
     * @return true if this map contains the key, false otherwise
     */
    @Override
    public boolean containsKey(K key) {
        int index = hashIndex(key);
        LinkedList<MapEntry<K, V>> chain = table.get(index);
        for (MapEntry<K, V> entry : chain) {
            if (entry.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether this map contains the specified value.
     *
     * O(N)
     *
     * @param value the value being searched for
     * @return true if this map contains one or more keys to the specified
     * value, false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        for (LinkedList<MapEntry<K, V>> chain : table) {
            for (MapEntry<K, V> entry : chain) {
                if (entry.getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a list view of the mappings contained in this map, where the
     * ordering of mappings in the list is insignificant.
     *
     * O(N)
     *
     * @return a list containing all mappings (i.e., entries) in this map
     */
    @Override
    public List<MapEntry<K, V>> entries() {
        List<MapEntry<K, V>> result = new ArrayList<MapEntry<K, V>>(size);
        for (LinkedList<MapEntry<K, V>> chain : table) {
            result.addAll(chain);
        }
        return result;
    }

    /**
     * Gets the value to which the specified key is mapped.
     *
     * Average-case: O(1)
     *
     * @param key the key for which to get the mapped value
     * @return the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key
     */
    @Override
    public V get(K key) {
        int index = hashIndex(key);
        LinkedList<MapEntry<K, V>> chain = table.get(index);
        for (MapEntry<K, V> entry : chain) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Determines whether this map is empty (contains no mappings).
     *
     * O(1)
     *
     * @return true if this map contains no key:value pairs, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns a list view of the keys contained in this map, where the ordering
     * of keys in the list is insignificant.
     *
     * O(N)
     *
     * @return a list containing all keys in this map
     */
    public List<K> keys() {
        List<K> result = new ArrayList<K>(size);
        for (LinkedList<MapEntry<K, V>> chain : table) {
            for (MapEntry<K, V> entry : chain) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is replaced
     * by the specified value.
     *
     * Average-case: O(1)
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no
     * mapping for key
     */
    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Null keys or values are not supported");
        }

        // Rehash *before* inserting if needed
        if (loadFactor() > MAX_LOAD_FACTOR) {
            rehash(capacity * 2);
        }

        int index = hashIndex(key);
        LinkedList<MapEntry<K, V>> chain = table.get(index);

        // Check if key already exists
        for (MapEntry<K, V> entry : chain) {
            if (entry.getKey().equals(key)) {
                V oldVal = entry.getValue();
                entry.setValue(value);
                return oldVal;
            }
        }

        // Insert new entry
        chain.add(new MapEntry<K, V>(key, value));
        size++;
        return null;
    }

    /**
     * Copies all of the mappings from the specified map to this map. The effect
     * of this call is equivalent to that of calling put(k, v) on this map once
     * for each mapping from key k to value v in the specified map.
     *
     * O(N)
     *
     * @param other mappings to be stored in this map
     */
    public void putAll(Map<? extends K, ? extends V> other) {
        for (MapEntry<? extends K, ? extends V> entry : other.entries()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * Average-case: O(1)
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     * mapping for key.
     */
    @Override
    public V remove(K key) {
        int index = hashIndex(key);
        LinkedList<MapEntry<K, V>> chain = table.get(index);

        java.util.Iterator<MapEntry<K, V>> iter = chain.iterator();
        while (iter.hasNext()) {
            MapEntry<K, V> entry = iter.next();
            if (entry.getKey().equals(key)) {
                V oldVal = entry.getValue();
                iter.remove();
                size--;
                return oldVal;
            }
        }
        return null;
    }

    /**
     * Returns the number of key:value pairs in this map.
     *
     * O(1)
     *
     * @return the number of mappings in this map
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns a list view of the values contained in this map, where the
     * ordering of values in the list is insignificant.
     *
     * O(N)
     *
     * @return a list containing all values in this map
     */
    public List<V> values() {
        List<V> result = new ArrayList<V>(size);
        for (LinkedList<MapEntry<K, V>> chain : table) {
            for (MapEntry<K, V> entry : chain) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    /**
     * Returns the number of buckets (chains) in the table. This method is
     * useful for testing and analysis.
     *
     * @return the capacity of the hash table
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the bucket (chain) at the specified index. This method is useful
     * for testing and analysis.
     *
     * @param index the bucket index
     * @return the list of entries at that bucket
     */
    public java.util.List<MapEntry<K, V>> getBucket(int index) {
        if (index < 0 || index >= capacity) {
            throw new IndexOutOfBoundsException("Bucket index out of range: " + index);
        }
        return new java.util.ArrayList<>(table.get(index));
    }

    /* ==========================
     *  Private helper methods
     * ==========================
     */
    /**
     * Computes the current load factor λ = size / capacity.
     */
    private double loadFactor() {
        return (double) size / (double) capacity;
    }

    /**
     * Computes the bucket index for the given key.
     */
    private int hashIndex(K key) {
        int hash = key.hashCode() & 0x7fffffff; // ensure non-negative
        return hash % capacity;
    }

    /**
     * Rehashes the table to have the specified new capacity.
     *
     * O(N)
     */
    private void rehash(int newCapacity) {
        ArrayList<LinkedList<MapEntry<K, V>>> oldTable = this.table;

        this.capacity = newCapacity;
        this.table = new ArrayList<LinkedList<MapEntry<K, V>>>(capacity);
        for (int i = 0; i < capacity; i++) {
            table.add(new LinkedList<MapEntry<K, V>>());
        }

        // Re-insert all entries
        int oldSize = this.size;
        this.size = 0;
        for (LinkedList<MapEntry<K, V>> chain : oldTable) {
            for (MapEntry<K, V> entry : chain) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
        // size is now equal to oldSize again (just re-confirm)
        this.size = oldSize;
    }
}
