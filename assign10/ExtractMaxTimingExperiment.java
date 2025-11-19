package assign10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

public class ExtractMaxTimingExperiment extends TimingExperiment {

    private BinaryMaxHeap<Integer> heap;

    public ExtractMaxTimingExperiment(int count, int nMin, int nMax, double factor, int iterCount) {
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
        while (!heap.isEmpty())
            heap.extractMax();
    }

    public static void main(String[] args) {
        ExtractMaxTimingExperiment exp =
            new ExtractMaxTimingExperiment(10, 10_000, 200_000, 2.0, 5);
        exp.run();
        exp.print();
    }
}
