package comprehensive;

/**
 * Performance benchmark for RandomPhraseGenerator.
 *
 * Runs 20 timed iterations of 10M phrase generation (after warmup), using a
 * null output stream to measure pure generation speed. Reports individual run
 * times and average.
 *
 * Usage: java comprehensive.FinalComparisonExperiment [grammar_file]
 * [num_phrases]
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version December 4, 2025
 */
public class FinalComparisonExperiment {

    /**
     * Null output stream that discards all writes (for timing without I/O).
     */
    private static final java.io.OutputStream NULL_OUT = new java.io.OutputStream() {
        @Override
        public void write(int b) {
            // Discard single byte - do nothing
        }

        @Override
        public void write(byte[] b, int off, int len) {
            // Discard byte array - do nothing
        }
    };

    /**
     * Runs warmup then 20 timed measurement runs.
     *
     * @param args [0] = grammar file (optional), [1] = num phrases (optional)
     */
    public static void main(String[] args) {
        // ---- Configuration ----
        String grammarFile = args.length > 0 ? args[0] : "school/src/main/java/comprehensive/poetic_sentence.g";
        int numPhrases = args.length > 1 ? Integer.parseInt(args[1]) : 10000000;

        // ---- Warmup Phase ----
        // Trigger JIT compilation and class loading before measurement
        System.out.println("Warming up...");

        // Save original System.out for restoration after warmup
        java.io.PrintStream originalOut = System.out;

        // Redirect both System.out and generator output to null during warmup
        System.setOut(new java.io.PrintStream(NULL_OUT));
        RandomPhraseGenerator.setOutputStream(NULL_OUT);

        // Run 10 warmup iterations with small phrase counts
        for (int i = 0; i < 10; i++) {
            RandomPhraseGenerator.main(new String[]{grammarFile, "1000"});
            RandomPhraseGenerator.setOutputStream(NULL_OUT); // Re-set after main clears it
        }

        // Restore System.out for measurement output
        System.setOut(originalOut);

        // ---- Measurement Phase ----
        System.out.println("Running experiment...");

        long totalTime = 0;
        int runs = 20;  // Number of measurement runs

        for (int i = 0; i < runs; i++) {
            // Force garbage collection to minimize GC interference during measurement
            System.gc();

            // Redirect generator output to null (pure generation timing)
            RandomPhraseGenerator.setOutputStream(NULL_OUT);

            // Time the generation
            long startTime = System.nanoTime();
            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});
            long endTime = System.nanoTime();

            // Convert nanoseconds to milliseconds and report
            long duration = (endTime - startTime) / 1_000_000;
            System.out.println("Run " + i + ": " + duration + " ms");
            totalTime += duration;
        }

        // ---- Report Results ----
        System.out.println("Average time: " + (totalTime / runs) + " ms");
    }
}
