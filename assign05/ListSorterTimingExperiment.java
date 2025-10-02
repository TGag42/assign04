package assign05;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

/**
 * Comprehensive timing experiments for ListSorter mergesort and quicksort
 * implementations. Tests performance across different data patterns and sizes.
 *
 * @author Alex Waldmann && Tyler Gagliardi
 * @version October 2, 2025
 */
public class ListSorterTimingExperiment {

    /**
     * Timing experiment for mergesort with different thresholds
     */
    public static class MergesortTimingExperiment extends TimingExperiment {

        private final Random rng = new Random(42);
        private List<Integer> testList;
        private final int threshold;
        private final DataPattern pattern;

        public enum DataPattern {
            RANDOM, SORTED, REVERSE_SORTED, MOSTLY_SORTED
        }

        public MergesortTimingExperiment(String problemSizeName, List<Integer> problemSizes,
                int iterationCount, int threshold, DataPattern pattern) {
            super(problemSizeName, problemSizes, iterationCount);
            this.threshold = threshold;
            this.pattern = pattern;
        }

        @Override
        protected void setupExperiment(int problemSize) {
            testList = generateList(problemSize, pattern);
        }

        @Override
        protected void runComputation() {
            ListSorter.mergesort(testList, threshold);
        }

        private List<Integer> generateList(int size, DataPattern pattern) {
            List<Integer> list = new ArrayList<>(size);

            switch (pattern) {
                case RANDOM:
                    for (int i = 0; i < size; i++) {
                        list.add(rng.nextInt(size * 2));
                    }
                    break;

                case SORTED:
                    for (int i = 0; i < size; i++) {
                        list.add(i);
                    }
                    break;

                case REVERSE_SORTED:
                    for (int i = size - 1; i >= 0; i--) {
                        list.add(i);
                    }
                    break;

                case MOSTLY_SORTED:
                    for (int i = 0; i < size; i++) {
                        list.add(i);
                    }
                    // Shuffle 10% of elements
                    for (int i = 0; i < size / 10; i++) {
                        int idx1 = rng.nextInt(size);
                        int idx2 = rng.nextInt(size);
                        Collections.swap(list, idx1, idx2);
                    }
                    break;
            }

            return list;
        }
    }

    /**
     * Timing experiment for quicksort with different pivot strategies
     */
    public static class QuicksortTimingExperiment extends TimingExperiment {

        private final Random rng = new Random(42);
        private List<Integer> testList;
        private final PivotChooser<Integer> pivotChooser;
        private final DataPattern pattern;

        public enum DataPattern {
            RANDOM, SORTED, REVERSE_SORTED, MOSTLY_SORTED
        }

        public QuicksortTimingExperiment(String problemSizeName, List<Integer> problemSizes,
                int iterationCount, DataPattern pattern) {
            super(problemSizeName, problemSizes, iterationCount);
            this.pivotChooser = new PivotChooser<>();
            this.pattern = pattern;
        }

        @Override
        protected void setupExperiment(int problemSize) {
            testList = generateList(problemSize, pattern);
        }

        @Override
        protected void runComputation() {
            ListSorter.quicksort(testList, pivotChooser);
        }

        private List<Integer> generateList(int size, DataPattern pattern) {
            List<Integer> list = new ArrayList<>(size);

            switch (pattern) {
                case RANDOM:
                    for (int i = 0; i < size; i++) {
                        list.add(rng.nextInt(size * 2));
                    }
                    break;

                case SORTED:
                    for (int i = 0; i < size; i++) {
                        list.add(i);
                    }
                    break;

                case REVERSE_SORTED:
                    for (int i = size - 1; i >= 0; i--) {
                        list.add(i);
                    }
                    break;

                case MOSTLY_SORTED:
                    for (int i = 0; i < size; i++) {
                        list.add(i);
                    }
                    // Shuffle 10% of elements
                    for (int i = 0; i < size / 10; i++) {
                        int idx1 = rng.nextInt(size);
                        int idx2 = rng.nextInt(size);
                        Collections.swap(list, idx1, idx2);
                    }
                    break;
            }

            return list;
        }
    }

    /**
     * Comparison timing experiment for mergesort vs quicksort
     */
    public static class SortComparisonExperiment extends TimingExperiment {

        private final Random rng = new Random(42);
        private List<Integer> mergeList;
        private List<Integer> quickList;
        private final PivotChooser<Integer> pivotChooser;
        private final boolean testMergesort;

        public SortComparisonExperiment(String problemSizeName, List<Integer> problemSizes,
                int iterationCount, boolean testMergesort) {
            super(problemSizeName, problemSizes, iterationCount);
            this.pivotChooser = new PivotChooser<>();
            this.testMergesort = testMergesort;
        }

        @Override
        protected void setupExperiment(int problemSize) {
            List<Integer> originalList = new ArrayList<>(problemSize);
            for (int i = 0; i < problemSize; i++) {
                originalList.add(rng.nextInt(problemSize * 2));
            }

            mergeList = new ArrayList<>(originalList);
            quickList = new ArrayList<>(originalList);
        }

        @Override
        protected void runComputation() {
            if (testMergesort) {
                ListSorter.mergesort(mergeList, 10);
            } else {
                ListSorter.quicksort(quickList, pivotChooser);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== ListSorter Timing Experiments ===\n");

        // Common parameters
        List<Integer> problemSizes = buildProblemSizes(0, 20000, 21);
        int iterationCount = 25;
        int warmupIterations = 5;

        // Test 1: Mergesort with different thresholds on random data
        System.out.println("1. Mergesort Threshold Comparison (Random Data):");

        TimingExperiment mergeThreshold5 = new MergesortTimingExperiment(
                "Mergesort (threshold=5)", problemSizes, iterationCount, 5,
                MergesortTimingExperiment.DataPattern.RANDOM);
        mergeThreshold5.warmup(warmupIterations);
        mergeThreshold5.run();
        mergeThreshold5.print();

        System.out.println();

        TimingExperiment mergeThreshold20 = new MergesortTimingExperiment(
                "Mergesort (threshold=20)", problemSizes, iterationCount, 20,
                MergesortTimingExperiment.DataPattern.RANDOM);
        mergeThreshold20.warmup(warmupIterations);
        mergeThreshold20.run();
        mergeThreshold20.print();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Test 2: Quicksort on different data patterns
        System.out.println("2. Quicksort Data Pattern Comparison:");

        TimingExperiment quickRandom = new QuicksortTimingExperiment(
                "Quicksort (Random)", problemSizes, iterationCount,
                QuicksortTimingExperiment.DataPattern.RANDOM);
        quickRandom.warmup(warmupIterations);
        quickRandom.run();
        quickRandom.print();

        System.out.println();

        TimingExperiment quickSorted = new QuicksortTimingExperiment(
                "Quicksort (Already Sorted)", problemSizes, iterationCount,
                QuicksortTimingExperiment.DataPattern.SORTED);
        quickSorted.warmup(warmupIterations);
        quickSorted.run();
        quickSorted.print();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Test 3: Direct comparison between mergesort and quicksort
        System.out.println("3. Mergesort vs Quicksort Direct Comparison:");

        TimingExperiment mergeComparison = new SortComparisonExperiment(
                "Mergesort (Random)", problemSizes, iterationCount, true);
        mergeComparison.warmup(warmupIterations);
        mergeComparison.run();
        mergeComparison.print();

        System.out.println();

        TimingExperiment quickComparison = new SortComparisonExperiment(
                "Quicksort (Random)", problemSizes, iterationCount, false);
        quickComparison.warmup(warmupIterations);
        quickComparison.run();
        quickComparison.print();

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Test 4: Best vs Worst case scenarios
        System.out.println("4. Best vs Worst Case Analysis:");

        // Mergesort on mostly sorted (best case for hybrid approach)
        TimingExperiment mergeBest = new MergesortTimingExperiment(
                "Mergesort (Mostly Sorted)", problemSizes, iterationCount, 10,
                MergesortTimingExperiment.DataPattern.MOSTLY_SORTED);
        mergeBest.warmup(warmupIterations);
        mergeBest.run();
        mergeBest.print();

        System.out.println();

        // Quicksort on reverse sorted (potential worst case)
        TimingExperiment quickWorst = new QuicksortTimingExperiment(
                "Quicksort (Reverse Sorted)", problemSizes, iterationCount,
                QuicksortTimingExperiment.DataPattern.REVERSE_SORTED);
        quickWorst.warmup(warmupIterations);
        quickWorst.run();
        quickWorst.print();

        System.out.println("\n=== Timing Experiments Complete ===");
    }

    /**
     * Helper method to build problem sizes
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
