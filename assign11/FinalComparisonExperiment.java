package comprehensive;

public class FinalComparisonExperiment {

    public static void main(String[] args) {
        String grammarFile = "src/main/java/assign11/poetic_sentence.g";
        int numPhrases = 1000000; // Increase to 1 million

        // Warmup
        System.out.println("Warming up...");
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            public void write(int b) {
            }
        }));

        for (int i = 0; i < 10; i++) {
            RandomPhraseGenerator.main(new String[]{grammarFile, "1000"});
        }

        System.setOut(originalOut);
        System.out.println("Running experiment...");

        long totalTime = 0;
        int runs = 20;

        for (int i = 0; i < runs; i++) {
            System.gc();
            long startTime = System.nanoTime();

            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                public void write(int b) {
                }
            }));

            RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});

            long endTime = System.nanoTime();
            System.setOut(originalOut);

            long duration = (endTime - startTime) / 1_000_000; // ms
            System.out.println("Run " + i + ": " + duration + " ms");
            totalTime += duration;
        }

        System.out.println("Average time: " + (totalTime / runs) + " ms");
    }
}
