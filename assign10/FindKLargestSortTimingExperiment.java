package assign10;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import timing.TimingExperiment;

public class FindKLargestSortTimingExperiment extends TimingExperiment {

    private List<Integer> list;
    private int k;

    public FindKLargestSortTimingExperiment(int count, int kMin, double factor, int iterCount) {
        super("k", makeProblemSizes(count, kMin, factor), iterCount);
        k = kMin;
    }

    private static List<Integer> makeProblemSizes(int count, int min, double factor) {
        List<Integer> sizes = new ArrayList<>();
        int current = min;
        for (int i = 0; i < count; i++) {
            sizes.add(current);
            current = (int) Math.round(current * factor);
        }
        return sizes;
    }

    @Override
    protected void setupExperiment(int n) {
        list = IntStream.rangeClosed(1, n)
                .boxed().collect(Collectors.toList());
        java.util.Collections.shuffle(list);
        k = n;
    }

    @Override
    protected void runComputation() {
        //Get first 10 largest elements (consistent measurement no matter size,
        //larger list will not affect time significantly for heap as no matter
        //the size of the list, we are only extracting 10 elements not the entire list
        //list O(1) time (or should be))
        FindKLargest.findKLargestSort(list, k);
    }

    public static void main(String[] args) {
        FindKLargestSortTimingExperiment exp
                = new FindKLargestSortTimingExperiment(15, 10_000, 2.0, 5);
        exp.warmup(1000);
        exp.run();
        exp.print();
    }
}

