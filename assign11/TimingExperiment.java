package comprehensive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class conducts timing experiments to analyze the performance of the
 * RandomPhraseGenerator. It runs two experiments:
 * <ol>
 * <li>Time vs. Number of Phrases: Measures how generation time scales with the
 * number of phrases generated.</li>
 * <li>Time vs. Number of Non-Terminals: Measures how generation time scales
 * with the complexity (number of non-terminals) of the grammar.</li>
 * </ol>
 *
 * <p>
 * Results are printed in CSV format for easy graphing.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 28, 2025
 */
public class TimingExperiment {

    /**
     * Main entry point. Runs both timing experiments.
     *
     * @param args unused
     * @throws IOException if grammar file generation fails
     */
    public static void main(String[] args) throws IOException {
        runPhrasesExperiment();
        runNonTerminalsExperiment();
    }

    /**
     * Runs Experiment 1: measures time vs. number of phrases generated. Uses a
     * fixed grammar and varies the number of phrases.
     */
    private static void runPhrasesExperiment() {
        System.out.println("Experiment 1: Time vs Number of Phrases (N)");
        System.out.println("N,Time(ns)");

        String grammarFile = "./school/src/main/java/comprehensive/poetic_sentence.g";
        for (int n = 1000; n <= 20000; n += 1000) {
            //Remove old unused objects
            System.gc();

            //Disable output during timing
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            //Warmup
            try {
                RandomPhraseGenerator.main(new String[]{grammarFile, "1000"});
            } catch (Exception e) {
            }

            //Begin timing
            long startTime = System.nanoTime();

            //Generate phrases in size of n
            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(n)});

            long endTime = System.nanoTime();

            System.setOut(originalOut);
            System.out.println(n + "," + (endTime - startTime));
        }
        System.out.println();
    }

    /**
     * Runs Experiment 2: measures time vs. number of non-terminals. Generates
     * synthetic chained grammars of increasing size.
     *
     * @throws IOException if grammar file generation fails
     */
    private static void runNonTerminalsExperiment() throws IOException {
        System.out.println("Experiment 2: Time vs Number of Non-Terminals (N)");
        System.out.println("N,Time(ns)");

        int numPhrases = 100; // Constant number of phrases

        for (int n = 100; n <= 2000; n += 100) {
            System.gc();
            String grammarFile = "./generated_" + n + ".g";
            generateChainedGrammar(grammarFile, n);

            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            long startTime = System.nanoTime();

            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});

            long endTime = System.nanoTime();
            System.setOut(originalOut);

            System.out.println(n + "," + (endTime - startTime));

            // Cleanup
            new java.io.File(grammarFile).delete();
        }
    }

    /**
     * Generates a synthetic grammar file with N chained non-terminals. The
     * grammar forms a chain: &lt;start&gt; -&gt; &lt;1&gt; -&gt; &lt;2&gt;
     * -&gt; ... -&gt; &lt;N&gt; -&gt; "terminal"
     *
     * @param filename the output file path
     * @param n the number of non-terminals in the chain
     * @throws IOException if the file cannot be written
     */
    private static void generateChainedGrammar(String filename, int n) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("{\n<start>\n<1>\n}\n");
            for (int i = 1; i < n; i++) {
                writer.write("{\n<" + i + ">\n<" + (i + 1) + ">\n}\n");
            }
            writer.write("{\n<" + n + ">\nterminal\n}\n");
        }
    }
}
