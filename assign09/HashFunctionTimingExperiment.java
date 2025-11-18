package assign09;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Timing experiment to compare the performance of StudentBadHash,
 * StudentMediumHash, and StudentGoodHash by measuring insertion time and
 * collision rates.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 13, 2025
 */
public class HashFunctionTimingExperiment {

    private static final int WARMUP_ITERATIONS = 5;
    private static final int TIMING_ITERATIONS = 20;
    private static final int[] PROBLEM_SIZES = {100, 500, 1000, 2500, 5000, 10000};
    private static final Random rand = new Random(42); // Fixed seed for reproducibility

    public static void main(String[] args) {
        System.out.println("=== Hash Function Quality Timing Experiment ===\n");

        try {
            // Create CSV files for results
            PrintWriter timeWriter = new PrintWriter(new FileWriter("hash_function_times.csv"));
            PrintWriter collisionWriter = new PrintWriter(new FileWriter("hash_function_collisions.csv"));

            // Write headers
            timeWriter.println("ProblemSize,BadHashTime,MediumHashTime,GoodHashTime");
            collisionWriter.println("ProblemSize,BadHashCollisions,MediumHashCollisions,GoodHashCollisions");

            for (int n : PROBLEM_SIZES) {
                System.out.println("Testing with N = " + n + " students...");

                // Warmup
                for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                    testBadHash(n);
                    testMediumHash(n);
                    testGoodHash(n);
                }

                // Time BadHash
                long badTime = 0;
                int badCollisions = 0;
                for (int i = 0; i < TIMING_ITERATIONS; i++) {
                    Result result = testBadHash(n);
                    badTime += result.time;
                    badCollisions += result.collisions;
                }
                badTime /= TIMING_ITERATIONS;
                badCollisions /= TIMING_ITERATIONS;

                // Time MediumHash
                long mediumTime = 0;
                int mediumCollisions = 0;
                for (int i = 0; i < TIMING_ITERATIONS; i++) {
                    Result result = testMediumHash(n);
                    mediumTime += result.time;
                    mediumCollisions += result.collisions;
                }
                mediumTime /= TIMING_ITERATIONS;
                mediumCollisions /= TIMING_ITERATIONS;

                // Time GoodHash
                long goodTime = 0;
                int goodCollisions = 0;
                for (int i = 0; i < TIMING_ITERATIONS; i++) {
                    Result result = testGoodHash(n);
                    goodTime += result.time;
                    goodCollisions += result.collisions;
                }
                goodTime /= TIMING_ITERATIONS;
                goodCollisions /= TIMING_ITERATIONS;

                // Write results
                timeWriter.println(n + "," + badTime + "," + mediumTime + "," + goodTime);
                collisionWriter.println(n + "," + badCollisions + "," + mediumCollisions + "," + goodCollisions);

                System.out.printf("  BadHash:    %6d ms, %5d collisions%n", badTime / 1_000_000, badCollisions);
                System.out.printf("  MediumHash: %6d ms, %5d collisions%n", mediumTime / 1_000_000, mediumCollisions);
                System.out.printf("  GoodHash:   %6d ms, %5d collisions%n", goodTime / 1_000_000, goodCollisions);
                System.out.println();
            }

            timeWriter.close();
            collisionWriter.close();

            System.out.println("Results written to hash_function_times.csv and hash_function_collisions.csv");

        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }

    /**
     * Test StudentBadHash insertion performance.
     */
    private static Result testBadHash(int n) {
        HashTable<StudentBadHash, Integer> table = new HashTable<>();
        StudentBadHash[] students = generateBadHashStudents(n);

        long startTime = System.nanoTime();
        for (int i = 0; i < n; i++) {
            table.put(students[i], i);
        }
        long endTime = System.nanoTime();

        int collisions = countCollisions(table);

        return new Result(endTime - startTime, collisions);
    }

    /**
     * Test StudentMediumHash insertion performance.
     */
    private static Result testMediumHash(int n) {
        HashTable<StudentMediumHash, Integer> table = new HashTable<>();
        StudentMediumHash[] students = generateMediumHashStudents(n);

        long startTime = System.nanoTime();
        for (int i = 0; i < n; i++) {
            table.put(students[i], i);
        }
        long endTime = System.nanoTime();

        int collisions = countCollisions(table);

        return new Result(endTime - startTime, collisions);
    }

    /**
     * Test StudentGoodHash insertion performance.
     */
    private static Result testGoodHash(int n) {
        HashTable<StudentGoodHash, Integer> table = new HashTable<>();
        StudentGoodHash[] students = generateGoodHashStudents(n);

        long startTime = System.nanoTime();
        for (int i = 0; i < n; i++) {
            table.put(students[i], i);
        }
        long endTime = System.nanoTime();

        int collisions = countCollisions(table);

        return new Result(endTime - startTime, collisions);
    }

    /**
     * Generate random StudentBadHash objects.
     */
    private static StudentBadHash[] generateBadHashStudents(int n) {
        StudentBadHash[] students = new StudentBadHash[n];
        for (int i = 0; i < n; i++) {
            int uid = 1000000 + rand.nextInt(9000000);
            String firstName = generateRandomName();
            String lastName = generateRandomName();
            students[i] = new StudentBadHash(uid, firstName, lastName);
        }
        return students;
    }

    /**
     * Generate random StudentMediumHash objects.
     */
    private static StudentMediumHash[] generateMediumHashStudents(int n) {
        StudentMediumHash[] students = new StudentMediumHash[n];
        for (int i = 0; i < n; i++) {
            int uid = 1000000 + rand.nextInt(9000000);
            String firstName = generateRandomName();
            String lastName = generateRandomName();
            students[i] = new StudentMediumHash(uid, firstName, lastName);
        }
        return students;
    }

    /**
     * Generate random StudentGoodHash objects.
     */
    private static StudentGoodHash[] generateGoodHashStudents(int n) {
        StudentGoodHash[] students = new StudentGoodHash[n];
        for (int i = 0; i < n; i++) {
            int uid = 1000000 + rand.nextInt(9000000);
            String firstName = generateRandomName();
            String lastName = generateRandomName();
            students[i] = new StudentGoodHash(uid, firstName, lastName);
        }
        return students;
    }

    /**
     * Generate a random name (5-10 characters).
     */
    private static String generateRandomName() {
        int length = 5 + rand.nextInt(6);
        StringBuilder sb = new StringBuilder(length);
        sb.append((char) ('A' + rand.nextInt(26))); // First letter uppercase
        for (int i = 1; i < length; i++) {
            sb.append((char) ('a' + rand.nextInt(26)));
        }
        return sb.toString();
    }

    /**
     * Count the number of collisions in a hash table. A collision occurs when a
     * bucket contains more than one entry.
     */
    private static <K, V> int countCollisions(HashTable<K, V> table) {
        int collisions = 0;
        for (int i = 0; i < table.getCapacity(); i++) {
            int bucketSize = table.getBucket(i).size();
            if (bucketSize > 1) {
                collisions += (bucketSize - 1);
            }
        }
        return collisions;
    }

    /**
     * Helper class to store timing and collision results.
     */
    private static class Result {

        long time;
        int collisions;

        Result(long time, int collisions) {
            this.time = time;
            this.collisions = collisions;
        }
    }
}
