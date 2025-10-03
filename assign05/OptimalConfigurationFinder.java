package assign05;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

/**
 * Focused timing experiments to determine optimal configurations. Tests all
 * pivot choosers and threshold values systematically.
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version October 2, 2025
 */
public class OptimalConfigurationFinder {

    public enum DataPattern {
        RANDOM, SORTED
    }

    /**
     * Mergesort threshold testing experiment
     */
    public static class ThresholdTest extends TimingExperiment {

        private final Random rng = new Random(42);
        private List<Integer> testList;
        private final int threshold;
        private final DataPattern pattern;

        public ThresholdTest(int threshold, DataPattern pattern, List<Integer> problemSizes, int iterationCount) {
            super("Mergesort_Threshold_" + threshold + "_" + pattern, problemSizes, iterationCount);
            this.threshold = threshold;
            this.pattern = pattern;
        }

        @Override
        protected void setupExperiment(int problemSize) {
            testList = generateTestData(problemSize, pattern, rng);
        }

        @Override
        protected void runComputation() {
            ListSorter.mergesort(testList, threshold);
        }
    }

    /**
     * Pivot chooser testing experiment
     */
    public static class PivotTest extends TimingExperiment {

        private final Random rng = new Random(42);
        private List<Integer> testList;
        private final PivotChooser<Integer> chooser;
        private final DataPattern pattern;

        public PivotTest(String chooserName, PivotChooser<Integer> chooser,
                DataPattern pattern, List<Integer> problemSizes, int iterationCount) {
            super("Quicksort_" + chooserName + "_" + pattern, problemSizes, iterationCount);
            this.chooser = chooser;
            this.pattern = pattern;
        }

        @Override
        protected void setupExperiment(int problemSize) {
            testList = generateTestData(problemSize, pattern, rng);
        }

        @Override
        protected void runComputation() {
            ListSorter.quicksort(testList, chooser);
        }
    }

    /**
     * Generate test data based on pattern
     */
    public static List<Integer> generateTestData(int size, DataPattern pattern, Random rng) {
        List<Integer> list = new ArrayList<>(size);

        switch (pattern) {
            case RANDOM -> {
                for (int i = 0; i < size; i++) {
                    list.add(rng.nextInt(size * 2));
                }
            }
            case SORTED -> {
                for (int i = 0; i < size; i++) {
                    list.add(i);
                }
            }
        }

        return list;
    }

    /**
     * Helper method to build problem sizes (mirroring
     * ListSorterTimingExperiment)
     */
    public static List<Integer> buildProblemSizes(int sizeMin, int sizeStep, int sizeCount) {
        List<Integer> problemSizes = new ArrayList<>();
        for (int i = 0; i < sizeCount; i++) {
            problemSizes.add(sizeMin);
            sizeMin += sizeStep;
        }
        return problemSizes;
    }

    /**
     * Calculate average time from median times list
     */
    public static double calculateAverageTime(List<Long> medianTimes) {
        return medianTimes.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1_000_000.0; // Convert to milliseconds
    }

    public static void main(String[] args) {
        System.out.println("=== FINDING OPTIMAL CONFIGURATIONS ===\n");

        // Test parameters (mirroring ListSorterTimingExperiment)
        List<Integer> sizes = buildProblemSizes(0, 20000, 21); // 0 to 400k in steps of 20k
        DataPattern[] patterns = {DataPattern.RANDOM, DataPattern.SORTED};
        int iterationCount = 25; // Match ListSorterTimingExperiment
        int warmupIterations = 5; // Match ListSorterTimingExperiment

        System.out.println("Testing with sizes: " + sizes.size() + " sizes from " + sizes.get(0) + " to " + sizes.get(sizes.size() - 1));
        System.out.println("Data patterns: RANDOM, SORTED");
        System.out.println("Iterations per test: " + iterationCount);
        System.out.println("Warmup iterations: " + warmupIterations + "\n");

        // ===================================
        // 1. MERGESORT THRESHOLD OPTIMIZATION
        // ===================================
        System.out.println("1. MERGESORT THRESHOLD OPTIMIZATION");
        System.out.println("=" + "=".repeat(40));

        int[] thresholds = {1, 5, 10, 15, 20, 25, 30};

        // Store results for analysis
        double bestRandomThresholdTime = Double.MAX_VALUE;
        int bestRandomThreshold = -1;
        double bestSortedThresholdTime = Double.MAX_VALUE;
        int bestSortedThreshold = -1;

        for (DataPattern pattern : patterns) {
            System.out.println("\n--- " + pattern + " Data ---");

            for (int threshold : thresholds) {
                System.out.println("Testing threshold: " + threshold);
                ThresholdTest test = new ThresholdTest(threshold, pattern, sizes, iterationCount);
                test.warmup(warmupIterations);
                test.run();
                test.print();

                // Calculate average time and track best
                double avgTime = calculateAverageTime(test.getMedianTimes());
                System.out.printf("Average time: %.2f ms\n", avgTime);

                if (pattern == DataPattern.RANDOM && avgTime < bestRandomThresholdTime) {
                    bestRandomThresholdTime = avgTime;
                    bestRandomThreshold = threshold;
                }
                if (pattern == DataPattern.SORTED && avgTime < bestSortedThresholdTime) {
                    bestSortedThresholdTime = avgTime;
                    bestSortedThreshold = threshold;
                }
                System.out.println();
            }
        }

        // Report best thresholds found
        System.out.println("\n🏆 BEST MERGESORT THRESHOLDS FOUND:");
        System.out.printf("Random data: Threshold %d (%.2f ms average)\n", bestRandomThreshold, bestRandomThresholdTime);
        System.out.printf("Sorted data: Threshold %d (%.2f ms average)\n", bestSortedThreshold, bestSortedThresholdTime);

        // ===================================
        // 2. PIVOT CHOOSER OPTIMIZATION  
        // ===================================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("2. PIVOT CHOOSER OPTIMIZATION");
        System.out.println("=" + "=".repeat(40));

        // Create all pivot choosers to test
        List<PivotChooser<Integer>> choosers = new ArrayList<>();
        choosers.add(new FirstPivotChooser<>());
        choosers.add(new RandomPivotChooser<>(new Random(42)));
        choosers.add(new MedianOfThreePivotChooser<>());
        choosers.add(new MedianOfFivePivotChooser<>());

        String[] chooserNames = {"First", "Random", "MedianOfThree", "MedianOfFive"};

        // Store results for analysis
        double bestRandomChooserTime = Double.MAX_VALUE;
        String bestRandomChooser = "";
        double bestSortedChooserTime = Double.MAX_VALUE;
        String bestSortedChooser = "";

        for (DataPattern pattern : patterns) {
            System.out.println("\n--- " + pattern + " Data ---");

            for (int i = 0; i < choosers.size(); i++) {
                System.out.println("Testing pivot chooser: " + chooserNames[i]);
                PivotTest test = new PivotTest(chooserNames[i], choosers.get(i), pattern, sizes, iterationCount);
                test.warmup(warmupIterations);
                test.run();
                test.print();

                // Calculate average time and track best
                double avgTime = calculateAverageTime(test.getMedianTimes());
                System.out.printf("Average time: %.2f ms\n", avgTime);

                if (pattern == DataPattern.RANDOM && avgTime < bestRandomChooserTime) {
                    bestRandomChooserTime = avgTime;
                    bestRandomChooser = chooserNames[i];
                }
                if (pattern == DataPattern.SORTED && avgTime < bestSortedChooserTime) {
                    bestSortedChooserTime = avgTime;
                    bestSortedChooser = chooserNames[i];
                }
                System.out.println();
            }
        }

        // Report best pivot choosers found
        System.out.println("\n🏆 BEST PIVOT CHOOSERS FOUND:");
        System.out.printf("Random data: %s (%.2f ms average)\n", bestRandomChooser, bestRandomChooserTime);
        System.out.printf("Sorted data: %s (%.2f ms average)\n", bestSortedChooser, bestSortedChooserTime);

        // ===================================
        // 3. OPTIMAL ALGORITHM COMPARISON
        // ===================================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("3. OPTIMAL ALGORITHM HEAD-TO-HEAD COMPARISON");
        System.out.println("=" + "=".repeat(40));

        // Use the best configurations found above
        System.out.println("Comparing optimal configurations found:");
        System.out.printf("- Mergesort with best thresholds (Random: %d, Sorted: %d)\n", bestRandomThreshold, bestSortedThreshold);
        System.out.printf("- Quicksort with best choosers (Random: %s, Sorted: %s)\n", bestRandomChooser, bestSortedChooser);
        System.out.println();

        for (DataPattern pattern : patterns) {
            System.out.println("--- " + pattern + " Head-to-Head ---");

            // Use best threshold for this pattern
            int optimalThreshold = (pattern == DataPattern.RANDOM) ? bestRandomThreshold : bestSortedThreshold;

            // Use best chooser for this pattern  
            PivotChooser<Integer> optimalChooser;
            String optimalChooserName = (pattern == DataPattern.RANDOM) ? bestRandomChooser : bestSortedChooser;

            switch (optimalChooserName) {
                case "First" ->
                    optimalChooser = new FirstPivotChooser<>();
                case "Random" ->
                    optimalChooser = new RandomPivotChooser<>(new Random(42));
                case "MedianOfThree" ->
                    optimalChooser = new MedianOfThreePivotChooser<>();
                default ->
                    optimalChooser = new MedianOfFivePivotChooser<>();
            }

            // Test optimal mergesort
            ThresholdTest mergeTest = new ThresholdTest(optimalThreshold, pattern, sizes, iterationCount);
            mergeTest.warmup(warmupIterations);
            mergeTest.run();
            double mergeTime = calculateAverageTime(mergeTest.getMedianTimes());
            System.out.printf("Optimal Mergesort (threshold %d): %.2f ms average\n", optimalThreshold, mergeTime);

            // Test optimal quicksort  
            PivotTest quickTest = new PivotTest(optimalChooserName, optimalChooser, pattern, sizes, iterationCount);
            quickTest.warmup(warmupIterations);
            quickTest.run();
            double quickTime = calculateAverageTime(quickTest.getMedianTimes());
            System.out.printf("Optimal Quicksort (%s): %.2f ms average\n", optimalChooserName, quickTime);

            // Declare winner
            if (mergeTime < quickTime) {
                double improvement = ((quickTime - mergeTime) / quickTime) * 100;
                System.out.printf("🏆 WINNER for %s data: Mergesort (%.1f%% faster)\n", pattern, improvement);
            } else {
                double improvement = ((mergeTime - quickTime) / mergeTime) * 100;
                System.out.printf("🏆 WINNER for %s data: Quicksort (%.1f%% faster)\n", pattern, improvement);
            }
            System.out.println();
        }

        System.out.println("=== FINAL OPTIMIZATION RESULTS ===");
        System.out.println("\n📊 SUMMARY OF BEST CONFIGURATIONS:");
        System.out.println("Mergesort Thresholds:");
        System.out.printf("  • Random data: %d (%.2f ms avg)\n", bestRandomThreshold, bestRandomThresholdTime);
        System.out.printf("  • Sorted data: %d (%.2f ms avg)\n", bestSortedThreshold, bestSortedThresholdTime);
        System.out.println("\nQuicksort Pivot Choosers:");
        System.out.printf("  • Random data: %s (%.2f ms avg)\n", bestRandomChooser, bestRandomChooserTime);
        System.out.printf("  • Sorted data: %s (%.2f ms avg)\n", bestSortedChooser, bestSortedChooserTime);

        System.out.println("\n💡 RECOMMENDATIONS:");
        System.out.println("1. Use the threshold/chooser combinations shown above for best performance");
        System.out.println("2. Consider data characteristics when choosing algorithms");
        System.out.println("3. Test with your specific data patterns for final optimization");
    }
}
