package comprehensive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Timing experiments to analyze RandomPhraseGenerator performance.
 *
 * Runs two experiments: 1. Time vs. number of phrases (expects O(N) growth) 2.
 * Time vs. number of non-terminals (expects O(N) growth)
 *
 * Outputs results in CSV format: N,Time(ns)
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version December 4, 2025
 */
public class TimingExperiment {

    /**
     * Main entry point. Runs both timing experiments sequentially.
     *
     * @param args unused
     * @throws IOException if grammar file generation fails
     */
    public static void main(String[] args) throws IOException {
        runPhrasesExperiment();
        runNonTerminalsExperiment();
    }

    /**
     * Measures time vs. number of phrases (1,000 to 20,000). Uses
     * poetic_sentence.g with precomputed phrases.
     */
    private static void runPhrasesExperiment() {
        System.out.println("Experiment 1: Time vs Number of Phrases (N)");
        System.out.println("N,Time(ns)");

        String grammarFile = "./school/src/main/java/comprehensive/poetic_sentence.g";

        for (int n = 1000; n <= 20000; n += 1000) {
            // Force garbage collection to minimize interference
            System.gc();

            // Redirect output to null during timing as we are testing algorithm
            // scaling, any overhead should be minimized
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            // Warmup run to ensure JIT compilation
            try {
                RandomPhraseGenerator.main(new String[]{grammarFile, "1000"});
            } catch (Exception e) {
                // Ignore warmup errors
            }

            // Timed measurement
            long startTime = System.nanoTime();
            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(n)});
            long endTime = System.nanoTime();

            // Restore output and report
            System.setOut(originalOut);
            System.out.println(n + "," + (endTime - startTime));
        }
        System.out.println();
    }

    /**
     * Measures time vs. number of non-terminals (100 to 2,000). Generates
     * synthetic chained grammars: <start> -> <1> -> <2> -> ... -> terminal
     *
     * @throws IOException if temporary grammar file cannot be written
     */
    private static void runNonTerminalsExperiment() throws IOException {
        System.out.println("Experiment 2: Time vs Number of Non-Terminals (N)");
        System.out.println("N,Time(ns)");

        int numPhrases = 100; // Fixed phrase count for this experiment

        for (int n = 100; n <= 2000; n += 100) {
            // Force garbage collection
            System.gc();

            // Generate synthetic grammar with N non-terminals
            String grammarFile = "./generated_" + n + ".g";
            generateChainedGrammar(grammarFile, n);

            // Redirect output to null during timing
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            // Timed measurement
            long startTime = System.nanoTime();
            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});
            long endTime = System.nanoTime();

            // Restore output and report
            System.setOut(originalOut);
            System.out.println(n + "," + (endTime - startTime));

            // Cleanup temporary grammar file
            new java.io.File(grammarFile).delete();
        }
    }

    /**
     * Generates a chained grammar file: <start> -> <1> -> <2> -> ... -> <N> ->
     * "terminal"
     *
     * @param filename output file path
     * @param n number of non-terminals in the chain
     * @throws IOException if file cannot be written
     */
    private static void generateChainedGrammar(String filename, int n) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Start symbol definition
            writer.write("{\n<start>\n<1>\n}\n");

            // Chain of non-terminals: <1> -> <2> -> ... -> <N>
            for (int i = 1; i < n; i++) {
                writer.write("{\n<" + i + ">\n<" + (i + 1) + ">\n}\n");
            }

            // Final non-terminal produces terminal
            writer.write("{\n<" + n + ">\nterminal\n}\n");
        }
    }
}
