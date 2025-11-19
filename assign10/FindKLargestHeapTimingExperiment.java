package assign10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

public class FindKLargestHeapTimingExperiment extends TimingExperiment {

    private List<Integer> list;
    private int k;

    public FindKLargestHeapTimingExperiment(int count, int kMin, int kMax, double factor, int iterCount) {
        super("k", makeProblemSizes(count, kMin, factor), iterCount);
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
    protected void setupExperiment(int k) {
        this.k = k;

        int N = 200_000;
        Random rand = new Random();
        list = new ArrayList<>(N);
        for (int i = 0; i < N; i++)
            list.add(rand.nextInt());
    }

    @Override
    protected void runComputation() {
        FindKLargest.findKLargestHeap(list, k);
    }

    public static void main(String[] args) {
        FindKLargestHeapTimingExperiment exp =
            new FindKLargestHeapTimingExperiment(6, 1_000, 32_000, 2.0, 5);
        exp.run();
        exp.print();
    }
}
