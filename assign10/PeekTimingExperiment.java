package assign10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

public class PeekTimingExperiment extends TimingExperiment {

    private BinaryMaxHeap<Integer> heap;

    public PeekTimingExperiment(int count, int nMin, int nMax, double factor, int iterCount) {
        super("N", makeProblemSizes(count, nMin, factor), iterCount);
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
        Random rand = new Random();
        List<Integer> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            list.add(rand.nextInt());
        heap = new BinaryMaxHeap<>(list);
    }

    @Override
    protected void runComputation() {
        for (int i = 0; i < 1_000_000; i++)
            heap.peek();
    }

    public static void main(String[] args) {
        PeekTimingExperiment exp =
            new PeekTimingExperiment(10, 10_000, 200_000, 2.0, 5);
        exp.run();
        exp.print();
    }
}
