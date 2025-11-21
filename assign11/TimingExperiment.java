package comprehensive;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TimingExperiment {

    public static void main(String[] args) throws IOException {
        runPhrasesExperiment();
        runNonTerminalsExperiment();
    }

    private static void runPhrasesExperiment() {
        System.out.println("Experiment 1: Time vs Number of Phrases (N)");
        System.out.println("N,Time(ns)");

        String grammarFile = "assign11/poetic_sentence.g";
        // Warmup
        try {
            RandomPhraseGenerator.main(new String[]{grammarFile, "100"});
        } catch (Exception e) {
        }

        for (int n = 1000; n <= 20000; n += 1000) {
            long startTime = System.nanoTime();
            // Suppress output for timing
            // Actually, printing to stdout is part of the assignment "The output... is simply the random phrase(s)"
            // But printing to console is very slow and dominates the time. 
            // Usually timing experiments exclude I/O if possible, but here the main method prints.
            // I will redirect stdout to null in the real run, or just accept it includes I/O.
            // Given the "median running time" requirement, I/O is likely part of it.
            // However, for the graph, we want to see algorithm behavior.
            // I'll modify RandomPhraseGenerator temporarily to NOT print if a flag is set? 
            // Or just run it as is. The user asked to "Design and conduct an experiment".
            // I will run it as is, but maybe redirect output to a file to avoid console scrolling lag.

            // Actually, I can't easily redirect stdout from inside Java for the called main method without changing System.out.
            // I'll just run it.
            // Wait, calling main() prints to System.out. I should probably capture it or silence it.
            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(n)});

            long endTime = System.nanoTime();
            System.setOut(originalOut);

            System.out.println(n + "," + (endTime - startTime));
        }
        System.out.println();
    }

    private static void runNonTerminalsExperiment() throws IOException {
        System.out.println("Experiment 2: Time vs Number of Non-Terminals (N)");
        System.out.println("N,Time(ns)");

        int numPhrases = 100; // Constant number of phrases

        for (int n = 100; n <= 2000; n += 100) {
            String grammarFile = "comprehensive/generated_" + n + ".g";
            generateChainedGrammar(grammarFile, n);

            long startTime = System.nanoTime();

            java.io.PrintStream originalOut = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});

            long endTime = System.nanoTime();
            System.setOut(originalOut);

            System.out.println(n + "," + (endTime - startTime));

            // Cleanup
            new java.io.File(grammarFile).delete();
        }
    }

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
