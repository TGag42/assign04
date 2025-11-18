package assign09;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests to verify HashTable correctness and behavior.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 13, 2025
 */
public class HashTableVerificationTest {

    private HashTable<String, Integer> table;

    @BeforeEach
    public void setup() {
        table = new HashTable<>();
    }

    // ============ Basic Functionality Tests ============
    @Test
    public void testPutAndGet() {
        table.put("key1", 100);
        table.put("key2", 200);
        table.put("key3", 300);

        assertEquals(100, table.get("key1"));
        assertEquals(200, table.get("key2"));
        assertEquals(300, table.get("key3"));
        assertEquals(3, table.size());
    }

    @Test
    public void testPutOverwrite() {
        table.put("key1", 100);
        Integer oldValue = table.put("key1", 200);

        assertEquals(100, oldValue);
        assertEquals(200, table.get("key1"));
        assertEquals(1, table.size());
    }

    @Test
    public void testRemove() {
        table.put("key1", 100);
        table.put("key2", 200);

        Integer removed = table.remove("key1");
        assertEquals(100, removed);
        assertNull(table.get("key1"));
        assertEquals(1, table.size());
    }

    @Test
    public void testContainsKey() {
        table.put("key1", 100);

        assertTrue(table.containsKey("key1"));
        assertFalse(table.containsKey("key2"));
    }

    @Test
    public void testContainsValue() {
        table.put("key1", 100);
        table.put("key2", 200);

        assertTrue(table.containsValue(100));
        assertTrue(table.containsValue(200));
        assertFalse(table.containsValue(300));
    }

    @Test
    public void testClear() {
        table.put("key1", 100);
        table.put("key2", 200);
        table.put("key3", 300);

        table.clear();

        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
        assertNull(table.get("key1"));
    }

    @Test
    public void testEntries() {
        table.put("key1", 100);
        table.put("key2", 200);
        table.put("key3", 300);

        List<MapEntry<String, Integer>> entries = table.entries();
        assertEquals(3, entries.size());
    }

    // ============ Collision and Distribution Tests ============
    @Test
    public void testCollisionCountingBadHash() {
        HashTable<StudentBadHash, Integer> badTable = new HashTable<>();

        // Insert 10 students - all should hash to same bucket
        for (int i = 0; i < 10; i++) {
            StudentBadHash student = new StudentBadHash(1000000 + i, "First" + i, "Last" + i);
            badTable.put(student, i);
        }

        // Count collisions
        int collisions = countCollisions(badTable);

        // Should have 9 collisions (10 entries, 1 bucket)
        assertEquals(9, collisions, "BadHash should cause N-1 collisions");

        // Verify all entries are in the same bucket
        int occupiedBuckets = 0;
        for (int i = 0; i < badTable.getCapacity(); i++) {
            if (!badTable.getBucket(i).isEmpty()) {
                occupiedBuckets++;
            }
        }
        assertEquals(1, occupiedBuckets, "All BadHash entries should be in 1 bucket");
    }

    @Test
    public void testDistributionMediumHash() {
        HashTable<StudentMediumHash, Integer> mediumTable = new HashTable<>();

        // Insert 100 students with sequential UIDs
        for (int i = 0; i < 100; i++) {
            StudentMediumHash student = new StudentMediumHash(1000000 + i, "First" + i, "Last" + i);
            mediumTable.put(student, i);
        }

        // Count occupied buckets
        int occupiedBuckets = 0;
        for (int i = 0; i < mediumTable.getCapacity(); i++) {
            if (!mediumTable.getBucket(i).isEmpty()) {
                occupiedBuckets++;
            }
        }

        // With 100 sequential UIDs and capacity 101, we expect excellent distribution
        // Sequential UIDs mod 101 should give ~99-100 occupied buckets
        assertTrue(occupiedBuckets > 90,
                "MediumHash with sequential UIDs should distribute entries across many buckets, found: " + occupiedBuckets);
    }

    @Test
    public void testDistributionGoodHash() {
        HashTable<StudentGoodHash, Integer> goodTable = new HashTable<>();

        Random rand = new Random(42);

        // Insert 100 students with random names
        for (int i = 0; i < 100; i++) {
            String firstName = generateRandomName(rand, 6);
            String lastName = generateRandomName(rand, 8);
            StudentGoodHash student = new StudentGoodHash(1000000 + i, firstName, lastName);
            goodTable.put(student, i);
        }

        // Count occupied buckets
        int occupiedBuckets = 0;
        for (int i = 0; i < goodTable.getCapacity(); i++) {
            if (!goodTable.getBucket(i).isEmpty()) {
                occupiedBuckets++;
            }
        }

        // GoodHash should distribute well - with 100 entries and capacity ~101,
        // we expect ~63-80 occupied buckets (birthday paradox: ~63% utilization)
        // This is GOOD distribution - not all buckets will be used at λ ≈ 1
        assertTrue(occupiedBuckets > 50,
                "GoodHash should distribute entries across many buckets, found: " + occupiedBuckets);
        
        // Verify it's significantly better than if all entries were in one bucket
        assertTrue(occupiedBuckets > 1,
                "GoodHash should not put all entries in one bucket like BadHash");
    }

    @Test
    public void testHighLoadFactorCollisions() {
        HashTable<String, Integer> smallTable = new HashTable<>(10);  // Small capacity

        // Insert 100 entries - this creates λ = 10
        for (int i = 0; i < 100; i++) {
            smallTable.put("key" + i, i);
        }

        int collisions = countCollisions(smallTable);

        // With 100 entries and ~10 buckets (after rehashing), expect many collisions
        // Expected: approximately 90 collisions (100 - 10)
        assertTrue(collisions > 80,
                "High load factor should cause many collisions, found: " + collisions);
    }

    // ============ Performance Verification Tests ============
    
    @Test
    public void testBadHashIsSignificantlySlower() {
        // BadHash should be much slower than GoodHash due to O(N²) vs O(N)
        HashTable<StudentBadHash, Integer> badTable = new HashTable<>();
        HashTable<StudentGoodHash, Integer> goodTable = new HashTable<>();
        
        int n = 500;  // Use 500 to get measurable time difference
        
        // Warmup
        for (int i = 0; i < n; i++) {
            badTable.put(new StudentBadHash(i, "F", "L"), i);
            goodTable.put(new StudentGoodHash(i, "F", "L"), i);
        }
        badTable.clear();
        goodTable.clear();
        
        // Time BadHash insertions
        long startBad = System.nanoTime();
        for (int i = 0; i < n; i++) {
            badTable.put(new StudentBadHash(1000000 + i, "First", "Last"), i);
        }
        long badTime = System.nanoTime() - startBad;
        
        // Time GoodHash insertions  
        long startGood = System.nanoTime();
        for (int i = 0; i < n; i++) {
            goodTable.put(new StudentGoodHash(1000000 + i, "First", "Last"), i);
        }
        long goodTime = System.nanoTime() - startGood;
        
        // BadHash should be at least 5x slower (conservative threshold)
        double ratio = (double) badTime / goodTime;
        assertTrue(ratio >= 5.0, 
                   "BadHash (O(N²)) should be significantly slower than GoodHash (O(N)), ratio: " + ratio);
    }
    
    // ============ Comparison with Java HashMap ============
    @Test
    public void testPerformanceVsHashMap() {
        HashMap<String, Integer> javaMap = new HashMap<>();
        HashTable<String, Integer> myTable = new HashTable<>();

        int N = 10000;
        String[] keys = new String[N];
        for (int i = 0; i < N; i++) {
            keys[i] = "key" + i;
        }

        // Time Java HashMap PUT
        long startJava = System.nanoTime();
        for (int i = 0; i < N; i++) {
            javaMap.put(keys[i], i);
        }
        long javaTime = System.nanoTime() - startJava;

        // Time My HashTable PUT
        long startMy = System.nanoTime();
        for (int i = 0; i < N; i++) {
            myTable.put(keys[i], i);
        }
        long myTime = System.nanoTime() - startMy;

        double ratio = (double) myTime / javaTime;

        // My implementation should be within 20x of Java's (generous tolerance)
        assertTrue(ratio < 20.0,
                "Implementation is too slow compared to Java HashMap, ratio: " + ratio);

        System.out.println("PUT Performance Ratio (Mine/Java): " + String.format("%.2f", ratio));
    }

    // ============ Rehashing Tests ============
    @Test
    public void testRehashingOccurs() {
        HashTable<String, Integer> smallTable = new HashTable<>(10);
        int initialCapacity = smallTable.getCapacity();

        // Insert enough entries to trigger rehashing (λ > 10)
        for (int i = 0; i < 150; i++) {
            smallTable.put("key" + i, i);
        }

        int newCapacity = smallTable.getCapacity();

        assertTrue(newCapacity > initialCapacity,
                "Capacity should increase after rehashing");
        assertEquals(150, smallTable.size(),
                "All entries should remain after rehashing");
    }

    @Test
    public void testRehashingPreservesEntries() {
        HashTable<String, Integer> smallTable = new HashTable<>(10);

        // Insert 150 entries
        for (int i = 0; i < 150; i++) {
            smallTable.put("key" + i, i);
        }

        // Verify all entries are still accessible
        for (int i = 0; i < 150; i++) {
            assertEquals(i, smallTable.get("key" + i),
                    "Entry should be preserved after rehashing");
        }
    }

    // ============ Edge Cases ============
    @Test
    public void testEmptyTableOperations() {
        assertNull(table.get("nonexistent"));
        assertNull(table.remove("nonexistent"));
        assertFalse(table.containsKey("nonexistent"));
        assertFalse(table.containsValue(999));
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    public void testSingleEntry() {
        table.put("only", 42);

        assertEquals(42, table.get("only"));
        assertEquals(1, table.size());
        assertFalse(table.isEmpty());

        table.remove("only");
        assertTrue(table.isEmpty());
    }

    @Test
    public void testManyCollisions() {
        // Create keys that hash to same value mod capacity
        HashTable<Integer, String> intTable = new HashTable<>(101);

        // Insert keys that collide: 0, 101, 202, 303, ...
        for (int i = 0; i < 10; i++) {
            intTable.put(i * 101, "value" + i);
        }

        // All should be retrievable
        for (int i = 0; i < 10; i++) {
            assertEquals("value" + i, intTable.get(i * 101));
        }
    }

    // ============ Helper Methods ============
    private int countCollisions(HashTable<?, ?> table) {
        int collisions = 0;
        for (int i = 0; i < table.getCapacity(); i++) {
            List<?> bucket = table.getBucket(i);
            if (bucket.size() > 1) {
                collisions += (bucket.size() - 1);
            }
        }
        return collisions;
    }

    private long timePutOperations(int n) {
        HashTable<String, Integer> tempTable = new HashTable<>();

        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            tempTable.put("key" + i, i);
        }
        long end = System.nanoTime();

        return end - start;
    }

    private long timeGetOperations(int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            table.get("key" + i);
        }
        long end = System.nanoTime();

        return end - start;
    }

    private long timeBadHashInsertions(HashTable<StudentBadHash, Integer> table, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            StudentBadHash student = new StudentBadHash(1000000 + i, "First", "Last");
            table.put(student, i);
        }
        long end = System.nanoTime();

        return end - start;
    }

    private String generateRandomName(Random rand, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + rand.nextInt(26)));
        }
        return sb.toString();
    }
}
