package comprehensive;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Timing harness for RandomPhraseGenerator experiments.
 * Runs generation internally and reports only the generation time (no JVM startup).
 */
public class TimingHarness {

    // Null output stream to discard generated phrases
    private static final OutputStream NULL_OUT = new OutputStream() {
        @Override public void write(int b) {}
        @Override public void write(byte[] b) {}
        @Override public void write(byte[] b, int off, int len) {}
    };

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println("Usage: java TimingHarness <grammar_file> <num_phrases> <warmup_runs> <timed_runs>");
            return;
        }

        String grammarFile = args[0];
        int numPhrases = Integer.parseInt(args[1]);
        int warmupRuns = Integer.parseInt(args[2]);
        int timedRuns = Integer.parseInt(args[3]);

        // Redirect output to null stream (we only want timing, not output)
        RandomPhraseGenerator.setOutputStream(NULL_OUT);

        // Warmup runs (JIT compilation, cache warming)
        for (int i = 0; i < warmupRuns; i++) {
            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});
        }

        // Timed runs
        double totalTime = 0;
        for (int i = 0; i < timedRuns; i++) {
            long start = System.nanoTime();
            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});
            long end = System.nanoTime();
            totalTime += (end - start) / 1_000_000.0;
        }

        double avgTime = totalTime / timedRuns;
        
        // Output just the average time in ms (for Python to parse)
        System.out.printf("%.2f%n", avgTime);
    }
}
