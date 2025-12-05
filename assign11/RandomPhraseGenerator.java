package comprehensive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Random phrase generator that reads a grammar file and outputs random phrases.
 *
 * For small grammars, precomputes all possible phrases for fast O(1) selection.
 * For large/recursive grammars, expands symbols one at a time using a stack.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version December 4, 2025
 */
public class RandomPhraseGenerator {

    /**
     * Maximum number of phrases to precompute before falling back to
     * stack-based generation.
     */
    private static final int MAX_PRECOMPUTED = 100000;

    /**
     * Newline character as a byte for direct buffer writing.
     */
    private static final byte NEWLINE = '\n';

    /**
     * Grammar storage using integer encoding for efficiency. Structure:
     * grammar[nonTerminalId][productionIndex][symbolIndex]
     *
     * Symbol encoding: - Non-terminals: id >= 0 (index into grammar array) -
     * Terminals: id < 0 (use ~id to get index into terminals array)
     */
    private static int[][][] grammar;

    /**
     * Terminal symbols pre-encoded as byte arrays for fast output.
     */
    private static byte[][] terminals;

    /**
     * The ID of the &lt;start&gt; non-terminal symbol.
     */
    private static int startSymbol;

    /**
     * Precomputed phrases as byte arrays (includes trailing newline). Only
     * populated if grammar produces ≤ MAX_PRECOMPUTED unique phrases. Array
     * size is padded to power of 2 for fast modulo via bitwise AND.
     */
    private static byte[][] precomputedPhrases;

    /**
     * Bitmask for fast modulo: (index & phraseMask) equals (index %
     * precomputedPhrases.length).
     */
    private static int phraseMask;

    /**
     * Custom output stream for benchmarking (bypasses System.out when set).
     */
    private static OutputStream outputStream;

    // ==================== PUBLIC API ====================
    /**
     * Sets a custom output stream (used for benchmarking with a null stream).
     *
     * @param out the output stream to use, or null to use System.out
     */
    public static void setOutputStream(OutputStream out) {
        outputStream = out;
    }

    /**
     * Main entry point. Parses the grammar file and outputs random phrases.
     *
     * @param args args[0] = grammar file path, args[1] = number of phrases to
     * generate
     */
    public static void main(String[] args) {
        // ---- Argument Validation ----
        if (args.length < 2) {
            System.err.println("Usage: java RandomPhraseGenerator <grammar_file> <num_phrases>");
            return;
        }

        String grammarFile = args[0];
        int numPhrases = Integer.parseInt(args[1]);

        // ---- Grammar Parsing ----
        // parseGrammar() also calls tryPrecompute() to enumerate phrases for small grammars
        try {
            parseGrammar(grammarFile);
        } catch (IOException e) {
            System.err.println("Error reading grammar: " + e.getMessage());
            return;
        }

        // ---- Output Stream Selection ----
        // Use custom stream if set (for benchmarking), otherwise use standard output
        OutputStream out = (outputStream != null) ? outputStream : System.out;

        // ---- Phrase Generation ----
        // Strategy selection based on whether precomputation succeeded
        try {
            if (precomputedPhrases != null) {
                // FAST PATH: O(1) per phrase - random index lookup and byte copy
                generateFromPrecomputed(out, numPhrases);
            } else {
                // FALLBACK PATH: O(phrase_length) per phrase - stack-based expansion
                generateWithStack(out, numPhrases);
            }
        } catch (IOException e) {
            System.err.println("Error writing output: " + e.getMessage());
        }
    }

    // ==================== GENERATION METHODS ====================
    /**
     * Outputs random phrases by picking from the precomputed array (fast path).
     *
     * <p>
     * Since all phrases are already built, we just pick random indices and copy
     * the bytes to an output buffer. Uses XorShift64 for fast random numbers
     * and processes 8 phrases at a time to reduce loop overhead.</p>
     *
     * @param out the output stream to write phrases to
     * @param numPhrases how many phrases to generate
     * @throws IOException if writing fails
     */
    private static void generateFromPrecomputed(OutputStream out, int numPhrases) throws IOException {
        // ---- Buffer Setup ----
        // 1MB buffer provides good balance between memory usage and batch efficiency.
        // Larger buffers show diminishing returns; smaller buffers increase syscalls.
        byte[] buffer = new byte[1024 * 1024];
        int pos = 0;  // Current write position in buffer

        // Local copy of mask for faster access (avoids repeated static field lookup)
        int mask = phraseMask;

        // ---- XorShift64 PRNG Initialization ----
        // System.nanoTime() provides a reasonable seed with good entropy.
        // XorShift64 has a period of 2^64-1, sufficient for any practical use.
        long seed = System.nanoTime();

        // ---- Loop Unrolling (Incredibly optimized using bit operations and
        // previous data coercion to make this REALLY FAST, ms for millions of phrases) ---
        // Divide phrases into groups of 8 for unrolled processing.
        // Using bit operations: >> 3 (2^3, right shift for devision to remove bits)
        // equivalent to / 8, & 7 is equivalent to % 8
        int groups = numPhrases >> 3;       // Number of complete groups of 8
        int remainder = numPhrases & 7;     // Remaining phrases (0-7)

        // ---- Main Generation Loop (8x "Unrolled") ----
        // Very unnecessary, but improves tested speed by ~10%-20%.
        // Basically runs multiple random generations very quickly in one
        // iteration to reduce loop overhead. Tested with sizes 2x-16x operations,
        // 8x was fastest in testing. This could technically be made faster with 
        // multithreading, but for a single process this is basically
        // as good as I could get it.
        for (int g = 0; g < groups; g++) {
            // Generate 8 random phrase references using XorShift64.
            // Each XorShift64 step: 3 XOR operations with specific shift amounts.
            // The shift values (21, 35, 4) are from Marsaglia's recommended parameters.

            // Xorshift64 with validated shift constants (21, 35, 4) chosen to maximize period
            // and ensure good bit diffusion so all bits mix well and avoid RNG bias. (chatgpt.com)
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p0 = precomputedPhrases[(int) (seed) & mask];
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p1 = precomputedPhrases[(int) (seed) & mask];
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p2 = precomputedPhrases[(int) (seed) & mask];
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p3 = precomputedPhrases[(int) (seed) & mask];
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p4 = precomputedPhrases[(int) (seed) & mask];
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p5 = precomputedPhrases[(int) (seed) & mask];
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p6 = precomputedPhrases[(int) (seed) & mask];
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            byte[] p7 = precomputedPhrases[(int) (seed) & mask];

            // Calculate total bytes needed for this group of 8 phrases.
            // This check prevents buffer overflow when phrases are large.
            int needed = p0.length + p1.length + p2.length + p3.length
                    + p4.length + p5.length + p6.length + p7.length;

            // Flush buffer if it cannot hold this group
            if (pos + needed > buffer.length) {
                out.write(buffer, 0, pos);
                pos = 0;
            }

            // Copy all 8 phrases to buffer using System.arraycopy (native memcpy).
            // Newlines are pre-appended to each phrase, so no separate newline writes.
            System.arraycopy(p0, 0, buffer, pos, p0.length);
            pos += p0.length;
            System.arraycopy(p1, 0, buffer, pos, p1.length);
            pos += p1.length;
            System.arraycopy(p2, 0, buffer, pos, p2.length);
            pos += p2.length;
            System.arraycopy(p3, 0, buffer, pos, p3.length);
            pos += p3.length;
            System.arraycopy(p4, 0, buffer, pos, p4.length);
            pos += p4.length;
            System.arraycopy(p5, 0, buffer, pos, p5.length);
            pos += p5.length;
            System.arraycopy(p6, 0, buffer, pos, p6.length);
            pos += p6.length;
            System.arraycopy(p7, 0, buffer, pos, p7.length);
            pos += p7.length;
        }

        // ---- Handle Remaining Phrases ----
        // Process any phrases that don't fit in a complete group of 8
        for (int i = 0; i < remainder; i++) {
            // XorShift64 step (same algorithm as above)
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);

            byte[] phrase = precomputedPhrases[(int) (seed) & mask];

            // Flush buffer if needed before copying
            if (pos + phrase.length > buffer.length) {
                out.write(buffer, 0, pos);
                pos = 0;
            }

            System.arraycopy(phrase, 0, buffer, pos, phrase.length);
            pos += phrase.length;
        }

        // ---- Final Flush ----
        // Write any remaining data in the buffer
        if (pos > 0) {
            out.write(buffer, 0, pos);
        }
    }

    /**
     * Generates phrases by expanding the grammar one symbol at a time (fallback
     * path).
     *
     * <p>
     * Used when precomputation isn't possible (grammar too large or recursive).
     * Uses a stack to expand non-terminals: pop a symbol, if it's a
     * non-terminal pick a random production and push its symbols, if it's a
     * terminal write it out.</p>
     *
     * @param out the output stream to write phrases to
     * @param numPhrases how many phrases to generate
     * @throws IOException if writing fails
     */
    private static void generateWithStack(OutputStream out, int numPhrases) throws IOException {
        // ---- Buffer Setup ----
        // Same 1MB buffer strategy as the precomputed path
        byte[] buffer = new byte[1024 * 1024];
        int pos = 0;

        // ---- Stack Setup ----
        // 256 should be sufficient for most grammars. Deep recursion would have
        // been caught during precomputation attempt (depth > 50 throws exception).
        int[] stack = new int[256];

        // ---- XorShift64 "RNG" ----
        long seed = System.nanoTime();

        // ---- Main Generation Loop ----
        for (int i = 0; i < numPhrases; i++) {
            // Initialize stack with start symbol
            int top = 0;

            stack[top++] = startSymbol;

            // Expand symbols until stack is empty (phrase complete)
            while (top > 0) {
                int symbol = stack[--top];

                if (symbol < 0) {
                    // ---- Terminal Symbol ----
                    // Negative IDs indicate terminals. Use bitwise NOT to get array index.
                    byte[] term = terminals[~symbol];
                    int len = term.length;

                    // Flush buffer if needed
                    if (pos + len > buffer.length) {
                        out.write(buffer, 0, pos);
                        pos = 0;
                    }

                    // Copy terminal bytes to output buffer
                    System.arraycopy(term, 0, buffer, pos, len);
                    pos += len;
                } else {
                    // ---- Non-Terminal Symbol ----
                    // Get all productions for this non-terminal
                    int[][] productions = grammar[symbol];

                    // Generate random production index using XorShift64
                    seed ^= (seed << 21);
                    seed ^= (seed >>> 35);
                    seed ^= (seed << 4);

                    // Select random production, pseudo rng for speed. Using 
                    // (seed & 0x7FFFFFFFL) ensures positive value for modulo. 
                    // Basically masks off sign bit. (Thanks AI).
                    int[] production = productions[(int) ((seed & 0x7FFFFFFFL) % productions.length)];

                    // Push symbols in REVERSE order so they pop in correct (left-to-right) order
                    for (int j = production.length - 1; j >= 0; j--) {
                        stack[top++] = production[j];
                    }
                }
            }

            // ---- Append Newline ----
            // Unlike precomputed phrases, stack-generated phrases don't include newlines
            if (pos >= buffer.length - 1) {
                out.write(buffer, 0, pos);
                pos = 0;
            }
            buffer[pos++] = NEWLINE;
        }

        // ---- Final Flush ----
        if (pos > 0) {
            out.write(buffer, 0, pos);
        }
    }

    // ==================== PRECOMPUTATION METHODS ====================
    /**
     * Tries to build a list of all possible phrases from the grammar.
     *
     * <p>
     * If the grammar is small enough (≤100,000 phrases and not deeply
     * recursive), we store all phrases in an array for fast random selection.
     * The array is padded to a power-of-2 size so we can use bitwise AND for
     * speed instead of modulous for the indexing.</p>
     *
     * <p>
     * If the grammar is too large or recursive, this sets precomputedPhrases to
     * null and we fall back to generating phrases one at a time.</p>
     */
    private static void tryPrecompute() {
        try {
            // Recursively enumerate all possible phrases starting from <start>
            ArrayList<byte[]> phrases = expand(startSymbol, 0);

            if (phrases.size() <= MAX_PRECOMPUTED) {
                int size = phrases.size();

                // ---- Power-of-2 Array Sizing ----
                // Find smallest power of 2 >= size
                // Example: size=21 -> highestOneBit=16 -> powerOf2=32
                int powerOf2 = Integer.highestOneBit(size);
                if (powerOf2 < size) {
                    powerOf2 <<= 1;
                }

                precomputedPhrases = new byte[powerOf2][];
                phraseMask = powerOf2 - 1;  // Bitmask for fast modulo

                // ---- Append Newlines ----
                // Pre-append newline to each phrase to eliminate per-phrase newline writes
                for (int i = 0; i < size; i++) {
                    byte[] orig = phrases.get(i);
                    byte[] withNewline = new byte[orig.length + 1];
                    System.arraycopy(orig, 0, withNewline, 0, orig.length);
                    withNewline[orig.length] = NEWLINE;
                    precomputedPhrases[i] = withNewline;
                }

                // ---- Fill Padding Slots ----
                // Cycle through existing phrases to fill remaining slots.
                // This ensures all array indices are valid and maintains
                // mostly uniform distribution (a few items may repeat).
                for (int i = size; i < powerOf2; i++) {
                    precomputedPhrases[i] = precomputedPhrases[i % size];
                }
            }
        } catch (RuntimeException e) {
            // Grammar too large or too deeply recursive - fall back to stack generation
            precomputedPhrases = null;
        }
    }

    /**
     * Builds a list of all possible phrases that can be generated from a
     * symbol.
     *
     * <p>
     * For terminals, just returns that terminal. For non-terminals, tries every
     * production and every combination of expansions within each
     * production.</p>
     *
     * <p>
     * <b>Example:</b> If {@code <A>} can produce "X" followed by {@code <B>},
     * and {@code <B>} can be "Y" or "Z", then expanding {@code <A>} gives
     * ["XY", "XZ"].</p>
     *
     * @param symbol the symbol to expand (non-negative = non-terminal, negative
     * = terminal)
     * @param depth current recursion depth (stops at 50 to prevent infinite
     * loops)
     * @return list of all possible phrases from this symbol
     * @throws RuntimeException if too deep or too many phrases
     */
    private static ArrayList<byte[]> expand(int symbol, int depth) {
        // Recursion depth limit prevents infinite loops from cyclic grammars
        // and bounds the expansion time for deeply nested grammars
        if (depth > 50) {
            throw new RuntimeException("Too deep");
        }

        ArrayList<byte[]> results = new ArrayList<>();

        if (symbol < 0) {
            // ---- Terminal Symbol ----
            // Return single-element list with the terminal's byte representation
            results.add(terminals[~symbol]);
            return results;
        }

        // ---- Non-Terminal Symbol ----
        // Enumerate all phrases for each production
        for (int[] production : grammar[symbol]) {
            // Start with empty prefix (will accumulate symbols left-to-right)
            ArrayList<byte[]> current = new ArrayList<>();
            current.add(new byte[0]);

            // For each symbol in production, compute all possible expansions
            for (int s : production) {
                ArrayList<byte[]> expansions = expand(s, depth + 1);
                ArrayList<byte[]> next = new ArrayList<>();

                // All phrases: combine each current prefix with each expansion
                for (byte[] prefix : current) {
                    for (byte[] suffix : expansions) {
                        // Concatenate prefix and suffix into new byte array
                        byte[] combined = new byte[prefix.length + suffix.length];
                        System.arraycopy(prefix, 0, combined, 0, prefix.length);
                        System.arraycopy(suffix, 0, combined, prefix.length, suffix.length);
                        next.add(combined);

                        // Early termination if too many phrases
                        if (next.size() > MAX_PRECOMPUTED) {
                            throw new RuntimeException("Too many phrases");
                        }
                    }
                }
                current = next;
            }

            // Add all phrases from this production to results
            results.addAll(current);
            if (results.size() > MAX_PRECOMPUTED) {
                throw new RuntimeException("Too many phrases");
            }
        }

        return results;
    }

    // ==================== GRAMMAR PARSING METHODS ====================
    /**
     * Reads a grammar file and sets up all data structures.
     *
     * <p>
     * After parsing, tries to precompute all phrases if the grammar is small
     * enough.
     * </p>
     *
     * @param filePath path to the grammar file
     * @throws IOException if the file cannot be read
     */
    private static void parseGrammar(String filePath) throws IOException {
        // ---- Symbol Mapping Structures ----
        // Maps symbol names to integer IDs for efficient encoding
        Map<String, Integer> nonTerminalMap = new HashMap<>();
        ArrayList<byte[]> terminalList = new ArrayList<>();
        Map<String, Integer> terminalMap = new HashMap<>();

        // Builder for grammar - will be converted to primitive array after parsing
        ArrayList<ArrayList<int[]>> grammarBuilder = new ArrayList<>();

        // ---- File Parsing ----
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Look for start of non-terminal definition (line starting with '{')
                if (line.isEmpty() || line.charAt(0) != '{') {
                    continue;
                }

                // Next line contains the non-terminal name
                String ntName = br.readLine();
                if (ntName == null) {
                    break;
                }

                // Get or create ID for this non-terminal
                int ntId = getNonTerminalId(ntName, nonTerminalMap, grammarBuilder);
                ArrayList<int[]> productions = grammarBuilder.get(ntId);

                // Read all productions until closing brace
                while ((line = br.readLine()) != null && !line.startsWith("}")) {
                    if (!line.isEmpty()) {
                        // Parse production and add to this non-terminal's production list
                        productions.add(parseProduction(line, nonTerminalMap,
                                terminalList, terminalMap, grammarBuilder));
                    }
                }
            }
        }

        // ---- Convert to Primitive Arrays ----
        // ArrayLists have object overhead; primitive arrays are faster to traverse
        grammar = new int[grammarBuilder.size()][][];
        for (int i = 0; i < grammarBuilder.size(); i++) {
            ArrayList<int[]> prods = grammarBuilder.get(i);
            grammar[i] = prods.toArray(new int[prods.size()][]);
        }

        terminals = terminalList.toArray(new byte[0][]);

        // Look up the start symbol (grammar must define <start>)
        startSymbol = nonTerminalMap.get("<start>");

        // ---- Attempt Precomputation ----
        // If successful, enables O(1) generation; otherwise falls back to stack-based
        tryPrecompute();
    }

    /**
     * Gets or creates an ID for a non-terminal (like {@code <start>}). IDs are
     * non-negative and used as indices into the grammar array.
     */
    private static int getNonTerminalId(String name, Map<String, Integer> map,
            ArrayList<ArrayList<int[]>> builder) {
        Integer id = map.get(name);
        if (id != null) {
            return id;
        }

        // Assign new ID (next available index) and create empty production list
        id = map.size();
        map.put(name, id);
        builder.add(new ArrayList<>());
        return id;
    }

    /**
     * Gets or creates an ID for a terminal (literal text like "hello").
     * Terminal IDs are negative (encoded with ~) to distinguish from
     * non-terminals.
     */
    private static int getTerminalId(String value, ArrayList<byte[]> list, Map<String, Integer> map) {
        Integer id = map.get(value);
        if (id != null) {
            return id;
        }

        // Assign new index and store byte array representation
        int index = list.size();
        list.add(value.getBytes());  // Pre-encode as bytes for fast output
        id = ~index;  // Encode as negative using bitwise NOT
        map.put(value, id);
        return id;
    }

    /**
     * Parses a production line into an array of symbol IDs. Splits on angle
     * brackets: text in {@code <...>} becomes non-terminals, everything else
     * becomes terminals.
     */
    private static int[] parseProduction(String line, Map<String, Integer> ntMap,
            ArrayList<byte[]> termList, Map<String, Integer> termMap,
            ArrayList<ArrayList<int[]>> builder) {
        ArrayList<Integer> symbols = new ArrayList<>();
        int i = 0;

        while (i < line.length()) {
            // Find next non-terminal (starts with '<')
            int open = line.indexOf('<', i);

            if (open == -1) {
                // No more non-terminals; rest of line is terminal text
                if (i < line.length()) {
                    symbols.add(getTerminalId(line.substring(i), termList, termMap));
                }
                break;
            }

            // Text before '<' is terminal
            if (open > i) {
                symbols.add(getTerminalId(line.substring(i, open), termList, termMap));
            }

            // Extract non-terminal name (including angle brackets)
            int close = line.indexOf('>', open);
            String ntName = line.substring(open, close + 1);
            symbols.add(getNonTerminalId(ntName, ntMap, builder));
            i = close + 1;
        }

        // Convert ArrayList<Integer> to int[] for performance
        int[] result = new int[symbols.size()];
        for (int j = 0; j < symbols.size(); j++) {
            result[j] = symbols.get(j);
        }
        return result;
    }
}
