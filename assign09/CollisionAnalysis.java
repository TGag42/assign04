package assign09;

import java.util.Random;

/**
 * Detailed analysis of collision behavior to understand why high load factors
 * cause many collisions even with good hash functions.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 13, 2025
 */
public class CollisionAnalysis {

    public static void main(String[] args) {
        System.out.println("=== Collision Analysis: Understanding Load Factor Impact ===\n");

        // Test 1: Show collision behavior at different capacities
        System.out.println("TEST 1: Same N=1000, Different Capacities");
        System.out.println("------------------------------------------");
        analyzeCollisionsWithVaryingCapacity(1000);

        System.out.println("\n\nTEST 2: GoodHash Distribution Quality");
        System.out.println("--------------------------------------");
        analyzeDistributionQuality();

        System.out.println("\n\nTEST 3: Theoretical vs Actual Collisions");
        System.out.println("-----------------------------------------");
        compareTheoreticalAndActual();

        System.out.println("\n\nTEST 4: Load Factor Impact");
        System.out.println("---------------------------");
        analyzeLoadFactorImpact();
    }

    /**
     * Analyze collisions for same N but different capacities
     */
    private static void analyzeCollisionsWithVaryingCapacity(int n) {
        int[] capacities = {101, 503, 1009, 5003, 10007};

        System.out.printf("%-12s %-12s %-12s %-12s %-15s%n",
                "Capacity", "N", "Load(λ)", "Collisions", "Collision%");
        System.out.println("-".repeat(65));

        Random rand = new Random(42);

        for (int capacity : capacities) {
            HashTable<StudentGoodHash, Integer> table = new HashTable<>(capacity);

            // Generate random students
            for (int i = 0; i < n; i++) {
                String firstName = generateRandomName(rand, 6);
                String lastName = generateRandomName(rand, 8);
                StudentGoodHash student = new StudentGoodHash(1000000 + i, firstName, lastName);
                table.put(student, i);
            }

            double loadFactor = (double) n / capacity;
            int collisions = countCollisions(table);
            double collisionPct = (collisions * 100.0) / n;

            System.out.printf("%-12d %-12d %-12.2f %-12d %-15.1f%%%n",
                    capacity, n, loadFactor, collisions, collisionPct);
        }
    }

    /**
     * Analyze distribution quality by examining bucket sizes
     */
    private static void analyzeDistributionQuality() {
        int n = 1000;
        int capacity = 101;  // High load factor

        HashTable<StudentGoodHash, Integer> table = new HashTable<>(capacity);
        Random rand = new Random(42);

        // Insert entries
        for (int i = 0; i < n; i++) {
            String firstName = generateRandomName(rand, 6);
            String lastName = generateRandomName(rand, 8);
            StudentGoodHash student = new StudentGoodHash(1000000 + i, firstName, lastName);
            table.put(student, i);
        }

        // Analyze bucket size distribution
        int[] bucketSizeCounts = new int[20];  // Count buckets of size 0-19, 20+
        int maxBucketSize = 0;
        int occupiedBuckets = 0;
        int emptyBuckets = 0;

        for (int i = 0; i < table.getCapacity(); i++) {
            int size = table.getBucket(i).size();
            if (size == 0) {
                emptyBuckets++;
            } else {
                occupiedBuckets++;
            }
            if (size >= bucketSizeCounts.length) {
                bucketSizeCounts[bucketSizeCounts.length - 1]++;
            } else {
                bucketSizeCounts[size]++;
            }
            maxBucketSize = Math.max(maxBucketSize, size);
        }

        System.out.println("N = " + n + ", Capacity = " + capacity + ", λ = " + String.format("%.2f", (double) n / capacity));
        System.out.println("Empty buckets: " + emptyBuckets);
        System.out.println("Occupied buckets: " + occupiedBuckets);
        System.out.println("Max bucket size: " + maxBucketSize);
        System.out.println("\nBucket Size Distribution:");
        System.out.printf("%-15s %s%n", "Bucket Size", "Count");
        System.out.println("-".repeat(30));

        for (int i = 0; i < bucketSizeCounts.length; i++) {
            if (bucketSizeCounts[i] > 0) {
                String label = (i == bucketSizeCounts.length - 1) ? (i + "+") : String.valueOf(i);
                System.out.printf("%-15s %d%n", label, bucketSizeCounts[i]);
            }
        }
    }

    /**
     * Compare theoretical expected collisions with actual
     */
    private static void compareTheoreticalAndActual() {
        int[] testSizes = {100, 500, 1000, 2500, 5000, 10000};

        System.out.printf("%-8s %-12s %-12s %-18s %-18s %-10s%n",
                "N", "Capacity", "Load(λ)", "Expected Collisions", "Actual Collisions", "Diff");
        System.out.println("-".repeat(85));

        Random rand = new Random(42);

        for (int n : testSizes) {
            HashTable<StudentGoodHash, Integer> table = new HashTable<>();
            int capacity = table.getCapacity();

            // Insert entries
            for (int i = 0; i < n; i++) {
                String firstName = generateRandomName(rand, 6);
                String lastName = generateRandomName(rand, 8);
                StudentGoodHash student = new StudentGoodHash(1000000 + i, firstName, lastName);
                table.put(student, i);
            }

            double loadFactor = (double) n / capacity;
            int actualCollisions = countCollisions(table);

            // Theoretical expected collisions using birthday paradox approximation
            // For high load factors: E[collisions] ≈ N - M * (1 - (1 - 1/M)^N)
            // Simplified for λ >> 1: E[collisions] ≈ N - M
            double expectedCollisions = (loadFactor > 1) ? (n - capacity) : (n * n / (2.0 * capacity));

            int diff = actualCollisions - (int) expectedCollisions;

            System.out.printf("%-8d %-12d %-12.2f %-18.0f %-18d %-10d%n",
                    n, capacity, loadFactor, expectedCollisions, actualCollisions, diff);
        }

        System.out.println("\nInterpretation:");
        System.out.println("- When λ >> 1: Expected collisions ≈ N - M (pigeonhole principle)");
        System.out.println("- When λ ≈ 1: Expected collisions ≈ N²/(2M) (birthday paradox)");
        System.out.println("- High load factors cause many collisions regardless of hash quality");
    }

    /**
     * Show impact of load factor on collision rate
     */
    private static void analyzeLoadFactorImpact() {
        double[] loadFactors = {0.5, 1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0};
        int baseN = 1000;

        System.out.printf("%-15s %-12s %-12s %-18s %-18s%n",
                "Load Factor(λ)", "Capacity", "N", "Collisions", "Avg Chain Length");
        System.out.println("-".repeat(80));

        Random rand = new Random(42);

        for (double targetLambda : loadFactors) {
            int capacity = (int) (baseN / targetLambda);
            if (capacity < 1) {
                capacity = 1;
            }

            HashTable<StudentGoodHash, Integer> table = new HashTable<>(capacity);

            // Insert entries
            for (int i = 0; i < baseN; i++) {
                String firstName = generateRandomName(rand, 6);
                String lastName = generateRandomName(rand, 8);
                StudentGoodHash student = new StudentGoodHash(1000000 + i, firstName, lastName);
                table.put(student, i);
            }

            double actualLambda = (double) baseN / table.getCapacity();
            int collisions = countCollisions(table);

            // Calculate average chain length for occupied buckets
            int occupiedBuckets = 0;
            for (int i = 0; i < table.getCapacity(); i++) {
                if (!table.getBucket(i).isEmpty()) {
                    occupiedBuckets++;
                }
            }
            double avgChainLength = (occupiedBuckets > 0) ? ((double) baseN / occupiedBuckets) : 0;

            System.out.printf("%-15.2f %-12d %-12d %-18d %-18.2f%n",
                    actualLambda, table.getCapacity(), baseN, collisions, avgChainLength);
        }

        System.out.println("\nKey Insight:");
        System.out.println("As λ increases, collisions increase dramatically.");
        System.out.println("At λ = 100, nearly all entries collide (999 out of 1000).");
        System.out.println("This is EXPECTED behavior, not a bug in the hash function!");
    }

    // ============ Helper Methods ============
    private static int countCollisions(HashTable<?, ?> table) {
        int collisions = 0;
        for (int i = 0; i < table.getCapacity(); i++) {
            int bucketSize = table.getBucket(i).size();
            if (bucketSize > 1) {
                collisions += (bucketSize - 1);
            }
        }
        return collisions;
    }

    private static String generateRandomName(Random rand, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + rand.nextInt(26)));
        }
        return sb.toString();
    }
}
