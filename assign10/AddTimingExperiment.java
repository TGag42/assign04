package assign10;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timing.TimingExperiment;

public class AddTimingExperiment extends TimingExperiment {

    private int n;
    private BinaryMaxHeap<Integer> heap;
    private Random rand;

    public AddTimingExperiment(int count, int nMin, int nMax, double factor, int iterCount) {
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
        this.n = n;
        heap = new BinaryMaxHeap<>();
        rand = new Random();
    }

    @Override
    protected void runComputation() {
        for (int i = 0; i < n; i++)
            heap.add(rand.nextInt());
    }

    public static void main(String[] args) {
        AddTimingExperiment exp =
            new AddTimingExperiment(10, 10_000, 200_000, 2.0, 5);

        exp.run();
        exp.print();
    }

}
