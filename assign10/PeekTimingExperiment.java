package assign10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import timing.TimingExperiment;

public class PeekTimingExperiment extends TimingExperiment {

    private BinaryMaxHeap<Integer> heap;
    private Random rand;

    public PeekTimingExperiment(int count, int nMin, double factor, int iterCount) {
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
        List<Integer> list = IntStream.rangeClosed(1, n)
                .boxed().collect(Collectors.toList());

        heap = new BinaryMaxHeap<>(list);
        rand = new Random();
    }

    @Override
    protected void runComputation() {
        heap.peek();
    }

    public static void main(String[] args) {
        PeekTimingExperiment exp
                = new PeekTimingExperiment(10, 10_000, 2.0, 5);

        exp.warmup(10);
        exp.run();
        exp.print();
    }

}
