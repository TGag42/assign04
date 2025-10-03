package assign05;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

/**
 * Comprehensive optimization experiments to determine the best threshold values
 * for mergesort and the best pivot selection strategy for quicksort.
 *
 * This class contains timing experiments that systematically test: 1. Different
 * mergesort thresholds (1, 5, 10, 15, 20, 25, 30, 50) 2. All pivot selection
 * strategies (First, Random, MedianOfThree, MedianOfFive) 3. Different data
 * patterns (Random, Already Sorted, Reverse Sorted)
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version October 2, 2025
 */
public class SortingOptimizationExperiment {

    /**
     * Data patterns for testing sorting algorithms
     */
    public enum DataPattern {
        RANDOM, SORTED, REVERSE_SORTED
    }

    /**
     * Timing experiment for mergesort threshold optimization
     */
    public static class MergesortThresholdExperiment extends TimingExperiment {

        private final Random rng = new Random(42);
        private List<Integer> testList;
        private final int threshold;
        private final DataPattern pattern;

        public MergesortThresholdExperiment(String problemSizeName, List<Integer> problemSizes,
                int iterationCount, int threshold, DataPattern pattern) {
            super(problemSizeName, problemSizes, iterationCount);
            this.threshold = threshold;
            this.pattern = pattern;
        }

        @Override
        protected void setupExperiment(int problemSize) {
            testList = generateList(problemSize, pattern, rng);
        }

        @Override
        protected void runComputation() {
            ListSorter.mergesort(testList, threshold);
        }

        private static List<Integer> generateList(int size, DataPattern pattern, Random rng) {
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
                case REVERSE_SORTED -> {
                    for (int i = size - 1; i >= 0; i--) {
                        list.add(i);
                    }
                }
            }

            return list;
        }
    }

    /**
     * Timing experiment for pivot chooser comparison
     */
    public static class PivotChooserExperiment extends TimingExperiment {

        private final Random rng = new Random(42);
        private List<Integer> testList;
        private final PivotChooser<Integer> pivotChooser;
        private final DataPattern pattern;
        private final String chooserName;

        public PivotChooserExperiment(String problemSizeName, List<Integer> problemSizes,
                int iterationCount, PivotChooser<Integer> pivotChooser,
                String chooserName, DataPattern pattern) {
            super(problemSizeName, problemSizes, iterationCount);
            this.pivotChooser = pivotChooser;
            this.chooserName = chooserName;
            this.pattern = pattern;
        }

        @Override
        protected void setupExperiment(int problemSize) {
            testList = MergesortThresholdExperiment.generateList(problemSize, pattern, rng);
        }

        @Override
        protected void runComputation() {
            ListSorter.quicksort(testList, pivotChooser);
        }

        public String getChooserName() {
            return chooserName;
        }
    }

    /**
     * Main method to run all optimization experiments
     */
    public static void main(String[] args) {
        System.out.println("=== Sorting Algorithm Optimization Experiments ===\n");

        // Common parameters
        List<Integer> problemSizes = buildProblemSizes(1000, 1000, 15); // 1k to 15k
        int iterationCount = 20;
        int warmupIterations = 3;

        System.out.println("Problem sizes: " + problemSizes);
        System.out.println("Iterations per size: " + iterationCount);
        System.out.println("Warmup iterations: " + warmupIterations + "\n");

        // ==========================================
        // PART 1: MERGESORT THRESHOLD OPTIMIZATION
        // ==========================================
        System.out.println("PART 1: MERGESORT THRESHOLD OPTIMIZATION");
        System.out.println("=" + "=".repeat(50));

        int[] thresholds = {1, 5, 10, 15, 20, 25, 30, 50};
        DataPattern[] patterns = {DataPattern.RANDOM, DataPattern.SORTED};

        for (DataPattern pattern : patterns) {
            System.out.println("\n--- Testing " + pattern + " data ---");

            for (int threshold : thresholds) {
                System.out.println("Testing threshold: " + threshold);

                MergesortThresholdExperiment experiment = new MergesortThresholdExperiment(
                        "Mergesort (threshold=" + threshold + ", " + pattern + ")",
                        problemSizes, iterationCount, threshold, pattern);

                experiment.warmup(warmupIterations);
                experiment.run();
                experiment.print();
                System.out.println();
            }
        }

        // ==========================================
        // PART 2: PIVOT CHOOSER OPTIMIZATION
        // ==========================================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 2: PIVOT CHOOSER OPTIMIZATION");
        System.out.println("=" + "=".repeat(50));

        // Create all pivot choosers
        List<PivotChooser<Integer>> pivotChoosers = new ArrayList<>();
        pivotChoosers.add(new FirstPivotChooser<>());
        pivotChoosers.add(new RandomPivotChooser<>(new Random(42))); // Seeded for consistency
        pivotChoosers.add(new MedianOfThreePivotChooser<>());
        pivotChoosers.add(new MedianOfFivePivotChooser<>());

        String[] chooserNames = {
            "First", "Random", "MedianOfThree", "MedianOfFive"
        };

        for (DataPattern pattern : patterns) {
            System.out.println("\n--- Testing " + pattern + " data ---");

            for (int i = 0; i < pivotChoosers.size(); i++) {
                System.out.println("Testing pivot chooser: " + chooserNames[i]);

                PivotChooserExperiment experiment = new PivotChooserExperiment(
                        "Quicksort (" + chooserNames[i] + ", " + pattern + ")",
                        problemSizes, iterationCount, pivotChoosers.get(i),
                        chooserNames[i], pattern);

                experiment.warmup(warmupIterations);
                experiment.run();
                experiment.print();
                System.out.println();
            }
        }

        // ==========================================
        // PART 3: HEAD-TO-HEAD COMPARISON
        // ==========================================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 3: HEAD-TO-HEAD COMPARISON (Best vs Best)");
        System.out.println("=" + "=".repeat(50));

        // Use findings from above (you can modify these based on results)
        int bestThreshold = 15; // Typically good starting point
        PivotChooser<Integer> bestPivotChooser = new MedianOfFivePivotChooser<>();

        System.out.println("\nComparing optimal configurations:");
        System.out.println("- Mergesort with threshold=" + bestThreshold);
        System.out.println("- Quicksort with MedianOfFive pivot chooser\n");

        for (DataPattern pattern : patterns) {
            System.out.println("--- " + pattern + " data comparison ---");

            // Test mergesort
            MergesortThresholdExperiment mergeExperiment = new MergesortThresholdExperiment(
                    "Mergesort (optimized, " + pattern + ")",
                    problemSizes, iterationCount, bestThreshold, pattern);

            mergeExperiment.warmup(warmupIterations);
            mergeExperiment.run();
            mergeExperiment.print();
            System.out.println();

            // Test quicksort
            PivotChooserExperiment quickExperiment = new PivotChooserExperiment(
                    "Quicksort (optimized, " + pattern + ")",
                    problemSizes, iterationCount, bestPivotChooser,
                    "MedianOfFive", pattern);

            quickExperiment.warmup(warmupIterations);
            quickExperiment.run();
            quickExperiment.print();
            System.out.println();
        }

        // ==========================================
        // PART 4: STRESS TEST ON LARGE DATA
        // ==========================================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 4: STRESS TEST ON LARGE DATA");
        System.out.println("=" + "=".repeat(50));

        List<Integer> largeProblemSizes = buildProblemSizes(10000, 10000, 6); // 10k to 60k
        int stressIterations = 10;

        System.out.println("\nStress testing with larger datasets:");
        System.out.println("Problem sizes: " + largeProblemSizes);
        System.out.println("Iterations: " + stressIterations + "\n");

        for (DataPattern pattern : new DataPattern[]{DataPattern.RANDOM}) {
            System.out.println("--- " + pattern + " stress test ---");

            // Test best mergesort
            MergesortThresholdExperiment stressMerge = new MergesortThresholdExperiment(
                    "Mergesort (stress test)",
                    largeProblemSizes, stressIterations, bestThreshold, pattern);

            stressMerge.warmup(2);
            stressMerge.run();
            stressMerge.print();
            System.out.println();

            // Test best quicksort
            PivotChooserExperiment stressQuick = new PivotChooserExperiment(
                    "Quicksort (stress test)",
                    largeProblemSizes, stressIterations, bestPivotChooser,
                    "MedianOfFive", pattern);

            stressQuick.warmup(2);
            stressQuick.run();
            stressQuick.print();
            System.out.println();
        }

        System.out.println("=== All Optimization Experiments Complete ===");
        System.out.println("\nRecommendations based on results:");
        System.out.println("1. Examine the mergesort threshold results to find the optimal value");
        System.out.println("2. Compare pivot chooser performance across different data patterns");
        System.out.println("3. Consider the trade-offs between consistency and peak performance");
        System.out.println("4. Note any significant differences between random and sorted data");
    }

    /**
     * Helper method to build problem sizes for experiments
     *
     * @param sizeMin starting size
     * @param sizeStep increment between sizes
     * @param sizeCount number of sizes to generate
     * @return list of problem sizes
     */
    public static List<Integer> buildProblemSizes(int sizeMin, int sizeStep, int sizeCount) {
        List<Integer> problemSizes = new ArrayList<>();
        for (int i = 0; i < sizeCount; i++) {
            problemSizes.add(sizeMin);
            sizeMin += sizeStep;
        }
        return problemSizes;
    }
}
