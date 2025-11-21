package comprehensive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class generates random phrases based on a context-free grammar provided
 * in a file.
 *
 * @author GitHub Copilot
 * @version November 21, 2025
 */
public class RandomPhraseGenerator {

    // Maps non-terminal names to unique integer IDs (0, 1, 2...)
    private static final Map<String, Integer> nonTerminalMap = new HashMap<>();

    // Maps terminal strings to unique negative integer IDs (-1, -2, -3...)
    private static final ArrayList<String> terminals = new ArrayList<>();
    private static final Map<String, Integer> terminalMap = new HashMap<>();
    // Optimized terminal storage for generation
    private static String[] terminalArray;

    // Stores the grammar: grammar[nonTerminalID][productionIndex][symbolIndex]
    private static int[][][] grammar;

    // Temporary storage during parsing
    // Optimized to store int[] directly to avoid Integer boxing and extra ArrayList overhead
    private static final ArrayList<ArrayList<int[]>> tempGrammar = new ArrayList<>();

    // Reusable buffer for parsing productions to avoid repeated allocation
    private static int[] productionBuffer = new int[100];

    /**
     * Main entry point for the RandomPhraseGenerator.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java comprehensive.RandomPhraseGenerator <grammar_file> <number_of_phrases>");
            return;
        }

        // Reset static state for repeated runs in experiments
        nonTerminalMap.clear();
        terminals.clear();
        terminalMap.clear();
        tempGrammar.clear();
        grammar = null;
        terminalArray = null;

        String grammarFile = args[0];
        int numPhrases;
        try {
            numPhrases = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Number of phrases must be an integer.");
            return;
        }

        try {
            parseGrammar(grammarFile);
        } catch (IOException e) {
            System.err.println("Error reading grammar file: " + e.getMessage());
            return;
        }

        Integer startId = nonTerminalMap.get("<start>");
        if (startId == null) {
            System.err.println("Error: Grammar does not contain <start> non-terminal.");
            return;
        }

        // Convert tempGrammar to int[][][] for speed
        finalizeGrammar();

        // Use BufferedWriter for fast I/O
        // Reusing StringBuilder to reduce allocation
        // Initial capacity 256 to avoid resizing for typical phrases
        StringBuilder sb = new StringBuilder(256);

        // Custom stack for generation to avoid allocation in loop
        int[] stack = new int[128];

        try (java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.OutputStreamWriter(System.out), 65536)) {
            for (int i = 0; i < numPhrases; i++) {
                sb.setLength(0);
                // Pass stack to avoid allocation
                stack = generateIterative(startId, sb, stack);
                out.write(sb.toString());
                out.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing output: " + e.getMessage());
        }
    }

    private static void parseGrammar(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("{")) {
                    String nonTerminalName = br.readLine();
                    if (nonTerminalName == null) {
                        break;
                    }

                    int id = getOrCreateNonTerminalIndex(nonTerminalName);

                    ArrayList<int[]> productions = tempGrammar.get(id);

                    while ((line = br.readLine()) != null && !line.equals("}")) {
                        productions.add(parseProduction(line));
                    }
                }
            }
        }
    }

    private static void finalizeGrammar() {
        int numNonTerminals = tempGrammar.size();
        grammar = new int[numNonTerminals][][];

        for (int i = 0; i < numNonTerminals; i++) {
            ArrayList<int[]> productionsList = tempGrammar.get(i);
            // Convert ArrayList<int[]> to int[][]
            grammar[i] = productionsList.toArray(new int[0][]);
        }

        // Convert terminals ArrayList to array for faster access
        terminalArray = terminals.toArray(new String[0]);
    }

    private static int getOrCreateNonTerminalIndex(String name) {
        if (!nonTerminalMap.containsKey(name)) {
            int id = nonTerminalMap.size();
            nonTerminalMap.put(name, id);
            tempGrammar.add(new ArrayList<>());
        }
        return nonTerminalMap.get(name);
    }

    private static int getOrCreateTerminalIndex(String value) {
        if (!terminalMap.containsKey(value)) {
            int index = terminals.size();
            terminals.add(value);
            // Store as negative ID: -1, -2, -3...
            // ID = -(index + 1)
            terminalMap.put(value, -(index + 1));
        }
        return terminalMap.get(value);
    }

    private static int[] parseProduction(String line) {
        int count = 0;
        int index = 0;
        int length = line.length();

        while (index < length) {
            int open = line.indexOf('<', index);

            if (open == -1) {
                addSymbolToBuffer(getOrCreateTerminalIndex(line.substring(index)), count++);
                break;
            }

            if (open > index) {
                addSymbolToBuffer(getOrCreateTerminalIndex(line.substring(index, open)), count++);
            }

            int close = line.indexOf('>', open);
            if (close == -1) {
                addSymbolToBuffer(getOrCreateTerminalIndex(line.substring(index)), count++);
                break;
            }

            String nonTerminalName = line.substring(open, close + 1);
            addSymbolToBuffer(getOrCreateNonTerminalIndex(nonTerminalName), count++);
            index = close + 1;
        }
        return Arrays.copyOf(productionBuffer, count);
    }

    private static void addSymbolToBuffer(int symbol, int index) {
        if (index >= productionBuffer.length) {
            productionBuffer = Arrays.copyOf(productionBuffer, productionBuffer.length * 2);
        }
        productionBuffer[index] = symbol;
    }

    private static int[] generateIterative(int startId, StringBuilder sb, int[] stack) {
        int top = 0;
        stack[top++] = startId;

        while (top > 0) {
            int symbol = stack[--top];

            if (symbol >= 0) {
                // Non-terminal
                int[][] productions = grammar[symbol];
                if (productions.length == 0) {
                    continue;
                }

                // Use ThreadLocalRandom for better performance than Random (which is synchronized)
                int[] production = productions[java.util.concurrent.ThreadLocalRandom.current().nextInt(productions.length)];

                // Ensure stack capacity
                if (top + production.length > stack.length) {
                    stack = Arrays.copyOf(stack, Math.max(stack.length * 2, top + production.length));
                }

                // Push in reverse order
                for (int i = production.length - 1; i >= 0; i--) {
                    stack[top++] = production[i];
                }
            } else {
                // Terminal
                // Index is -(symbol + 1) -> -symbol - 1
                sb.append(terminalArray[-symbol - 1]);
            }
        }
        return stack;
    }
}
