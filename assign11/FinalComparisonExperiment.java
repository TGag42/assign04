package comprehensive;

/**
 * This class runs a comprehensive performance benchmark of the
 * RandomPhraseGenerator. It generates a large number of phrases (10 million by
 * default) and measures the average execution time across multiple runs to
 * ensure consistent results.
 *
 * <p>
 * The experiment includes JVM warmup runs and garbage collection between test
 * runs to minimize variance.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 28, 2025
 */
public class FinalComparisonExperiment {
    
    // Null output stream that discards all writes
    private static final java.io.OutputStream NULL_OUT = new java.io.OutputStream() {
        @Override public void write(int b) {}
        @Override public void write(byte[] b, int off, int len) {}
    };

    /**
     * Main entry point. Runs the performance benchmark.
     *
     * @param args optional:
     * <ul>
     * <li>args[0] - alternate grammar</li>
     * <li>args[1] - number of phrases to generate (default 10,000,000)</li>
     * </ul>
     *
     */
    public static void main(String[] args) {
        String grammarFile = args.length > 0 ? args[0] : "school/src/main/java/comprehensive/poetic_sentence.g";
        int numPhrases = args.length > 1 ? Integer.parseInt(args[1]) : 10000000; // 10 million

        //Warmup - redirect both stdout and the generator's output
        System.out.println("Warming up...");
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(NULL_OUT));
        RandomPhraseGenerator.setOutputStream(NULL_OUT);
        for (int i = 0; i < 10; i++) {
            RandomPhraseGenerator.main(new String[]{grammarFile, "1000"});
            RandomPhraseGenerator.setOutputStream(NULL_OUT); // Re-set after main clears it
        }
        System.setOut(originalOut);

        //Begin experiment
        System.out.println("Running experiment...");

        long totalTime = 0;
        int runs = 20;

        for (int i = 0; i < runs; i++) {
            System.gc();
            
            RandomPhraseGenerator.setOutputStream(NULL_OUT);
            
            long startTime = System.nanoTime();
            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1_000_000; // ms
            System.out.println("Run " + i + ": " + duration + " ms");
            totalTime += duration;
        }

        System.out.println("Average time: " + (totalTime / runs) + " ms");
    }
}
