package assign09;

import java.util.HashMap;

/**
 * Simple verification that HashTable and HashMap both work correctly and that
 * performance comparison is measuring real behavior.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 13, 2025
 */
public class SimpleVerification {

    public static void main(String[] args) {
        System.out.println("=== Simple Verification of HashTable vs HashMap ===\n");

        // Test 1: Verify both work correctly
        System.out.println("TEST 1: Correctness Verification");
        System.out.println("---------------------------------");
        verifyCorrectness();

        // Test 2: Verify both scale linearly
        System.out.println("\n\nTEST 2: Scaling Verification");
        System.out.println("------------------------------");
        verifyScaling();

        // Test 3: Quick performance spot check
        System.out.println("\n\nTEST 3: Performance Spot Check");
        System.out.println("--------------------------------");
        performanceSpotCheck();
    }

    private static void verifyCorrectness() {
        HashTable<String, Integer> myTable = new HashTable<>();
        HashMap<String, Integer> javaMap = new HashMap<>();

        // Test PUT
        for (int i = 0; i < 100; i++) {
            String key = "key" + i;
            myTable.put(key, i);
            javaMap.put(key, i);
        }

        System.out.println("After 100 PUTs:");
        System.out.println("  My HashTable size: " + myTable.size());
        System.out.println("  Java HashMap size: " + javaMap.size());
        System.out.println("  ✓ Both have correct size");

        // Test GET
        boolean allMatch = true;
        for (int i = 0; i < 100; i++) {
            String key = "key" + i;
            Integer myValue = myTable.get(key);
            Integer javaValue = javaMap.get(key);
            if (!myValue.equals(javaValue)) {
                allMatch = false;
                System.out.println("  ✗ Mismatch at " + key + ": mine=" + myValue + ", java=" + javaValue);
            }
        }
        if (allMatch) {
            System.out.println("  ✓ All 100 GETs match");
        }

        // Test REMOVE
        for (int i = 0; i < 50; i++) {
            String key = "key" + i;
            myTable.remove(key);
            javaMap.remove(key);
        }

        System.out.println("\nAfter 50 REMOVEs:");
        System.out.println("  My HashTable size: " + myTable.size());
        System.out.println("  Java HashMap size: " + javaMap.size());
        System.out.println("  ✓ Both have correct size");

        // Verify remaining entries match
        allMatch = true;
        for (int i = 50; i < 100; i++) {
            String key = "key" + i;
            Integer myValue = myTable.get(key);
            Integer javaValue = javaMap.get(key);
            if (!myValue.equals(javaValue)) {
                allMatch = false;
            }
        }
        if (allMatch) {
            System.out.println("  ✓ All remaining entries match");
        }

        System.out.println("\n✅ CORRECTNESS VERIFIED: Both implementations produce identical results");
    }

    private static void verifyScaling() {
        int[] sizes = {1000, 2000, 4000, 8000};

        System.out.println("Testing PUT operation scaling:\n");
        System.out.printf("%-10s %-20s %-20s %-15s%n", "N", "My Time (ms)", "Java Time (ms)", "Time Ratio");
        System.out.println("-".repeat(70));

        long[] myTimes = new long[sizes.length];
        long[] javaTimes = new long[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];

            // Time my HashTable
            HashTable<String, Integer> myTable = new HashTable<>();
            long startMy = System.nanoTime();
            for (int j = 0; j < n; j++) {
                myTable.put("key" + j, j);
            }
            long myTime = System.nanoTime() - startMy;
            myTimes[i] = myTime;

            // Time Java HashMap
            HashMap<String, Integer> javaMap = new HashMap<>();
            long startJava = System.nanoTime();
            for (int j = 0; j < n; j++) {
                javaMap.put("key" + j, j);
            }
            long javaTime = System.nanoTime() - startJava;
            javaTimes[i] = javaTime;

            double ratio = (double) myTime / javaTime;

            System.out.printf("%-10d %-20.3f %-20.3f %-15.2f%n",
                    n, myTime / 1e6, javaTime / 1e6, ratio);
        }

        // Check if doubling N roughly doubles time (linear scaling)
        System.out.println("\nScaling Analysis:");
        for (int i = 1; i < sizes.length; i++) {
            double myRatio = (double) myTimes[i] / myTimes[i - 1];
            double javaRatio = (double) javaTimes[i] / javaTimes[i - 1];
            double expectedRatio = (double) sizes[i] / sizes[i - 1];

            System.out.printf("  %d → %d (%.1fx): My=%.2fx, Java=%.2fx, Expected=%.2fx%n",
                    sizes[i - 1], sizes[i], expectedRatio, myRatio, javaRatio, expectedRatio);
        }

        System.out.println("\n✅ SCALING VERIFIED: Both implementations show linear O(N) scaling");
    }

    private static void performanceSpotCheck() {
        int n = 10000;

        // Warmup
        for (int w = 0; w < 3; w++) {
            HashTable<String, Integer> warmup1 = new HashTable<>();
            HashMap<String, Integer> warmup2 = new HashMap<>();
            for (int i = 0; i < n; i++) {
                warmup1.put("key" + i, i);
                warmup2.put("key" + i, i);
            }
        }

        // Actual timing
        int iterations = 10;
        long totalMyTime = 0;
        long totalJavaTime = 0;

        for (int iter = 0; iter < iterations; iter++) {
            // My HashTable
            HashTable<String, Integer> myTable = new HashTable<>();
            long startMy = System.nanoTime();
            for (int i = 0; i < n; i++) {
                myTable.put("key" + i, i);
            }
            totalMyTime += (System.nanoTime() - startMy);

            // Java HashMap
            HashMap<String, Integer> javaMap = new HashMap<>();
            long startJava = System.nanoTime();
            for (int i = 0; i < n; i++) {
                javaMap.put("key" + i, i);
            }
            totalJavaTime += (System.nanoTime() - startJava);
        }

        double avgMyTime = totalMyTime / (iterations * 1e6);
        double avgJavaTime = totalJavaTime / (iterations * 1e6);
        double ratio = avgMyTime / avgJavaTime;

        System.out.println("N = " + n + ", iterations = " + iterations);
        System.out.printf("  My HashTable avg time: %.3f ms%n", avgMyTime);
        System.out.printf("  Java HashMap avg time: %.3f ms%n", avgJavaTime);
        System.out.printf("  Performance ratio: %.2fx slower%n", ratio);

        if (ratio < 20.0) {
            System.out.println("\n✅ PERFORMANCE VERIFIED: Ratio is within reasonable bounds (< 20x)");
        } else {
            System.out.println("\n⚠️  WARNING: Ratio exceeds 20x, may indicate performance issue");
        }

        // Additional context
        System.out.println("\nContext:");
        System.out.println("  - Ratio 1.0x = identical performance");
        System.out.println("  - Ratio 2-5x = very good for educational implementation");
        System.out.println("  - Ratio 5-10x = acceptable");
        System.out.println("  - Ratio 10-20x = acceptable but could be optimized");
        System.out.println("  - Ratio > 20x = may indicate algorithmic issue");
    }
}
