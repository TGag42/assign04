package assign09;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

/**
 * Timing experiment to compare the performance of our HashTable implementation
 * with Java's HashMap for put, get, and remove operations.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 13, 2025
 */
public class HashMapComparisonExperiment {

    private static final int WARMUP_ITERATIONS = 5;
    private static final int TIMING_ITERATIONS = 20;
    private static final int[] PROBLEM_SIZES = {1000, 5000, 10000, 50000, 100000};
    private static final Random rand = new Random(42); // Fixed seed for reproducibility

    public static void main(String[] args) {
        System.out.println("=== HashTable vs HashMap Performance Comparison ===\n");

        try (PrintWriter putWriter = new PrintWriter(new FileWriter("hashmap_put_times.csv")); PrintWriter getWriter = new PrintWriter(new FileWriter("hashmap_get_times.csv")); PrintWriter removeWriter = new PrintWriter(new FileWriter("hashmap_remove_times.csv"))) {

            // Write headers
            putWriter.println("ProblemSize,MyHashTable,JavaHashMap,Ratio");
            getWriter.println("ProblemSize,MyHashTable,JavaHashMap,Ratio");
            removeWriter.println("ProblemSize,MyHashTable,JavaHashMap,Ratio");

            for (int n : PROBLEM_SIZES) {
                System.out.println("Testing with N = " + n + " entries...");

                // Generate test data
                String[] keys = generateRandomKeys(n);
                Integer[] values = generateRandomValues(n);

                // Warmup
                for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                    testMyHashTablePut(keys, values);
                    testJavaHashMapPut(keys, values);
                    testMyHashTableGet(keys, values);
                    testJavaHashMapGet(keys, values);
                    testMyHashTableRemove(keys, values);
                    testJavaHashMapRemove(keys, values);
                }

                // Time PUT operations
                long myPutTime = timeMyHashTablePut(keys, values);
                long javaPutTime = timeJavaHashMapPut(keys, values);
                double putRatio = (double) myPutTime / javaPutTime;

                // Time GET operations
                long myGetTime = timeMyHashTableGet(keys, values);
                long javaGetTime = timeJavaHashMapGet(keys, values);
                double getRatio = (double) myGetTime / javaGetTime;

                // Time REMOVE operations
                long myRemoveTime = timeMyHashTableRemove(keys, values);
                long javaRemoveTime = timeJavaHashMapRemove(keys, values);
                double removeRatio = (double) myRemoveTime / javaRemoveTime;

                // Write results (convert to milliseconds)
                putWriter.printf("%d,%.3f,%.3f,%.2f%n", n, myPutTime / 1_000_000.0, javaPutTime / 1_000_000.0, putRatio);
                getWriter.printf("%d,%.3f,%.3f,%.2f%n", n, myGetTime / 1_000_000.0, javaGetTime / 1_000_000.0, getRatio);
                removeWriter.printf("%d,%.3f,%.3f,%.2f%n", n, myRemoveTime / 1_000_000.0, javaRemoveTime / 1_000_000.0, removeRatio);

                // Display results
                System.out.printf("  PUT:    My HashTable = %6.2f ms, Java HashMap = %6.2f ms, Ratio = %.2fx%n",
                        myPutTime / 1_000_000.0, javaPutTime / 1_000_000.0, putRatio);
                System.out.printf("  GET:    My HashTable = %6.2f ms, Java HashMap = %6.2f ms, Ratio = %.2fx%n",
                        myGetTime / 1_000_000.0, javaGetTime / 1_000_000.0, getRatio);
                System.out.printf("  REMOVE: My HashTable = %6.2f ms, Java HashMap = %6.2f ms, Ratio = %.2fx%n",
                        myRemoveTime / 1_000_000.0, javaRemoveTime / 1_000_000.0, removeRatio);
                System.out.println();
            }

            System.out.println("Results written to hashmap_put_times.csv, hashmap_get_times.csv, hashmap_remove_times.csv");

        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }

    // ============ Timing Methods ============
    private static long timeMyHashTablePut(String[] keys, Integer[] values) {
        long totalTime = 0;
        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            totalTime += testMyHashTablePut(keys, values);
        }
        return totalTime / TIMING_ITERATIONS;
    }

    private static long timeJavaHashMapPut(String[] keys, Integer[] values) {
        long totalTime = 0;
        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            totalTime += testJavaHashMapPut(keys, values);
        }
        return totalTime / TIMING_ITERATIONS;
    }

    private static long timeMyHashTableGet(String[] keys, Integer[] values) {
        long totalTime = 0;
        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            totalTime += testMyHashTableGet(keys, values);
        }
        return totalTime / TIMING_ITERATIONS;
    }

    private static long timeJavaHashMapGet(String[] keys, Integer[] values) {
        long totalTime = 0;
        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            totalTime += testJavaHashMapGet(keys, values);
        }
        return totalTime / TIMING_ITERATIONS;
    }

    private static long timeMyHashTableRemove(String[] keys, Integer[] values) {
        long totalTime = 0;
        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            totalTime += testMyHashTableRemove(keys, values);
        }
        return totalTime / TIMING_ITERATIONS;
    }

    private static long timeJavaHashMapRemove(String[] keys, Integer[] values) {
        long totalTime = 0;
        for (int i = 0; i < TIMING_ITERATIONS; i++) {
            totalTime += testJavaHashMapRemove(keys, values);
        }
        return totalTime / TIMING_ITERATIONS;
    }

    // ============ Test Methods ============
    private static long testMyHashTablePut(String[] keys, Integer[] values) {
        HashTable<String, Integer> table = new HashTable<>();

        long startTime = System.nanoTime();
        for (int i = 0; i < keys.length; i++) {
            table.put(keys[i], values[i]);
        }
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    private static long testJavaHashMapPut(String[] keys, Integer[] values) {
        HashMap<String, Integer> map = new HashMap<>();

        long startTime = System.nanoTime();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    private static long testMyHashTableGet(String[] keys, Integer[] values) {
        HashTable<String, Integer> table = new HashTable<>();
        for (int i = 0; i < keys.length; i++) {
            table.put(keys[i], values[i]);
        }

        long startTime = System.nanoTime();
        for (String key : keys) {
            table.get(key);
        }
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    private static long testJavaHashMapGet(String[] keys, Integer[] values) {
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        long startTime = System.nanoTime();
        for (String key : keys) {
            map.get(key);
        }
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    private static long testMyHashTableRemove(String[] keys, Integer[] values) {
        HashTable<String, Integer> table = new HashTable<>();
        for (int i = 0; i < keys.length; i++) {
            table.put(keys[i], values[i]);
        }

        long startTime = System.nanoTime();
        for (String key : keys) {
            table.remove(key);
        }
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    private static long testJavaHashMapRemove(String[] keys, Integer[] values) {
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        long startTime = System.nanoTime();
        for (String key : keys) {
            map.remove(key);
        }
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    // ============ Data Generation ============
    /**
     * Generate an array of random String keys.
     */
    private static String[] generateRandomKeys(int n) {
        String[] keys = new String[n];
        for (int i = 0; i < n; i++) {
            keys[i] = "key_" + i + "_" + generateRandomString(10);
        }
        return keys;
    }

    /**
     * Generate an array of random Integer values.
     */
    private static Integer[] generateRandomValues(int n) {
        Integer[] values = new Integer[n];
        for (int i = 0; i < n; i++) {
            values[i] = rand.nextInt(1000000);
        }
        return values;
    }

    /**
     * Generate a random string of specified length.
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + rand.nextInt(26)));
        }
        return sb.toString();
    }
}
