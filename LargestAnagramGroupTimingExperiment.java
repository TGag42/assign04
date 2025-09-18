
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import assign04.AnagramChecker;
import timing.TimingExperiment;

public class LargestAnagramGroupTimingExperiment extends TimingExperiment {

    private static final int WORD_LEN = 10; // fixed word length
    private static final int BASE_WORDS = 25; // pool size to create repeats
    private final Random rng = new Random(0);

    private String[] words;
    private ArrayList<String> pool;

    public static void main(String[] args) {
        String sizeName = "Number of words (N)";
        List<Integer> sizes = buildProblemSizes(1_000, 1_000, 20);
        int iterations = 50;

        TimingExperiment exp = new LargestAnagramGroupTimingExperiment(sizeName, sizes, iterations);
        exp.warmup(5);
        exp.run();
        exp.print();
    }

    public LargestAnagramGroupTimingExperiment(String name, List<Integer> sizes, int iterations) {
        super(name, sizes, iterations);
    }

    @Override
    protected void setupExperiment(int problemSize) {
        // Make a small pool of random words
        pool = new ArrayList<>(BASE_WORDS);
        for (int i = 0; i < BASE_WORDS; i++) {
            pool.add(randomWord(WORD_LEN));
        }
        // Fill the input array by sampling from the pool
        words = new String[problemSize];
        for (int i = 0; i < problemSize; i++) {
            words[i] = pool.get(rng.nextInt(BASE_WORDS));
        }
    }

    @Override
    protected void runComputation() {
        AnagramChecker.getLargestAnagramGroup(words);
    }

    private String randomWord(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char) ('a' + rng.nextInt(26)));
        }
        return sb.toString();
    }
}
