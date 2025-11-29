package comprehensive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates random phrases based on a context-free grammar file.
 * 
 * <p>This generator reads a grammar file in a specific format and produces random
 * phrases by expanding non-terminal symbols according to production rules. The
 * grammar format uses curly braces to define non-terminals and their productions:</p>
 * 
 * <pre>
 * {
 * &lt;start&gt;
 * &lt;noun&gt; &lt;verb&gt;
 * }
 * </pre>
 * 
 * <p><b>Optimization Strategy:</b></p>
 * <p>This implementation uses a hybrid approach for maximum performance:</p>
 * <ul>
 *   <li><b>Small grammars</b> (≤100,000 possible phrases): All phrases are precomputed
 *       at parse time and stored as byte arrays. Generation becomes O(1) per phrase -
 *       just pick a random index and copy bytes to the output buffer.</li>
 *   <li><b>Large grammars</b>: Uses stack-based expansion where each phrase is generated
 *       by pushing/popping symbols onto a stack, selecting random productions for
 *       non-terminals.</li>
 * </ul>
 * 
 * <p><b>Key Performance Optimizations:</b></p>
 * <ul>
 *   <li>XorShift64 random number generator (faster than java.util.Random)</li>
 *   <li>Power-of-2 phrase array size enables bitwise AND instead of modulo</li>
 *   <li>Pre-appended newlines to phrases eliminates per-phrase newline writes</li>
 *   <li>1MB output buffer with batch writes reduces I/O system calls</li>
 *   <li>8x loop unrolling for better CPU pipelining</li>
 *   <li>Byte arrays instead of Strings avoid encoding overhead</li>
 * </ul>
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 29, 2025
 */
public class RandomPhraseGenerator {

    /** Maximum number of phrases to precompute before falling back to stack-based generation. */
    private static final int MAX_PRECOMPUTED = 100000;
    
    /** Newline character as a byte for direct buffer writing. */
    private static final byte NEWLINE = '\n';

    /**
     * Grammar storage using integer encoding for efficiency.
     * Structure: grammar[nonTerminalId][productionIndex][symbolIndex]
     * 
     * Symbol encoding:
     * - Non-terminals: id >= 0 (index into grammar array)
     * - Terminals: id < 0 (use ~id to get index into terminals array)
     */
    private static int[][][] grammar;
    
    /** Terminal symbols pre-encoded as byte arrays for fast output. */
    private static byte[][] terminals;
    
    /** The ID of the &lt;start&gt; non-terminal symbol. */
    private static int startSymbol;
    
    /**
     * Precomputed phrases as byte arrays (includes trailing newline).
     * Only populated if grammar produces ≤ MAX_PRECOMPUTED unique phrases.
     * Array size is padded to power of 2 for fast modulo via bitwise AND.
     */
    private static byte[][] precomputedPhrases;
    
    /** Bitmask for fast modulo: (index & phraseMask) equals (index % precomputedPhrases.length). */
    private static int phraseMask;
    
    /** Custom output stream for benchmarking (bypasses System.out when set). */
    private static OutputStream outputStream;

    /**
     * Sets a custom output stream for benchmarking purposes.
     * When set, output goes to this stream instead of System.out.
     * 
     * @param out the output stream to use, or null to use System.out
     */
    public static void setOutputStream(OutputStream out) {
        outputStream = out;
    }

    /**
     * Main entry point for the random phrase generator.
     * 
     * <p>Parses the grammar file, determines the optimal generation strategy,
     * and outputs the requested number of random phrases.</p>
     * 
     * @param args command line arguments:
     *             args[0] = path to grammar file
     *             args[1] = number of phrases to generate
     */
    public static void main(String[] args) {
        // Validate command line arguments
        if (args.length < 2) {
            System.err.println("Usage: java RandomPhraseGenerator <grammar_file> <num_phrases>");
            return;
        }

        String grammarFile = args[0];
        int numPhrases = Integer.parseInt(args[1]);

        // Parse the grammar file and attempt to precompute all phrases
        try {
            parseGrammar(grammarFile);
        } catch (IOException e) {
            System.err.println("Error reading grammar: " + e.getMessage());
            return;
        }

        // Use custom output stream if set (for benchmarking), otherwise System.out
        OutputStream out = (outputStream != null) ? outputStream : System.out;

        // Choose generation strategy based on whether precomputation succeeded
        try {
            if (precomputedPhrases != null) {
                // Fast path: O(1) per phrase using precomputed array
                generateFromPrecomputed(out, numPhrases);
            } else {
                // Fallback: O(phrase_length) per phrase using stack expansion
                generateWithStack(out, numPhrases);
            }
        } catch (IOException e) {
            System.err.println("Error writing output: " + e.getMessage());
        }
    }

    /**
     * Generates phrases using the precomputed phrase array.
     * 
     * <p>This is the fast path for small grammars. Each phrase generation is O(1):
     * generate a random index, look up the pre-built byte array, and copy to buffer.</p>
     * 
     * <p><b>Optimizations:</b></p>
     * <ul>
     *   <li>XorShift64 PRNG: 3 XOR + shift operations per random number</li>
     *   <li>Bitwise AND for modulo: (seed & mask) instead of (seed % length)</li>
     *   <li>8x loop unrolling: reduces loop overhead and improves pipelining</li>
     *   <li>Batch buffer writes: only flush when buffer approaches capacity</li>
     * </ul>
     * 
     * @param out the output stream to write phrases to
     * @param numPhrases the number of phrases to generate
     * @throws IOException if writing to the output stream fails
     */
    private static void generateFromPrecomputed(OutputStream out, int numPhrases) throws IOException {
        // 1MB output buffer - large enough to batch many phrases between flushes
        byte[] buffer = new byte[1024 * 1024];
        int pos = 0;  // Current write position in buffer
        int mask = phraseMask;  // Local copy for faster access
        
        // Initialize XorShift64 PRNG with current time as seed
        long seed = System.nanoTime();
        
        // Process phrases in groups of 8 for loop unrolling benefits
        int groups = numPhrases >> 3;      // numPhrases / 8
        int remainder = numPhrases & 7;     // numPhrases % 8
        
        for (int g = 0; g < groups; g++) {
            // Generate 8 random phrase references using XorShift64 and bitwise AND
            // XorShift64 algorithm: fast PRNG with good statistical properties
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p0 = precomputedPhrases[(int)(seed) & mask];
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p1 = precomputedPhrases[(int)(seed) & mask];
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p2 = precomputedPhrases[(int)(seed) & mask];
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p3 = precomputedPhrases[(int)(seed) & mask];
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p4 = precomputedPhrases[(int)(seed) & mask];
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p5 = precomputedPhrases[(int)(seed) & mask];
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p6 = precomputedPhrases[(int)(seed) & mask];
            seed ^= (seed << 21); seed ^= (seed >>> 35); seed ^= (seed << 4);
            byte[] p7 = precomputedPhrases[(int)(seed) & mask];
            
            // Calculate total bytes needed for this group of 8 phrases
            int needed = p0.length + p1.length + p2.length + p3.length + 
                         p4.length + p5.length + p6.length + p7.length;
            
            // Flush buffer if it can't hold this group
            if (pos + needed > buffer.length) {
                out.write(buffer, 0, pos);
                pos = 0;
            }
            
            // Copy all 8 phrases to buffer (newlines already included in phrases)
            System.arraycopy(p0, 0, buffer, pos, p0.length); pos += p0.length;
            System.arraycopy(p1, 0, buffer, pos, p1.length); pos += p1.length;
            System.arraycopy(p2, 0, buffer, pos, p2.length); pos += p2.length;
            System.arraycopy(p3, 0, buffer, pos, p3.length); pos += p3.length;
            System.arraycopy(p4, 0, buffer, pos, p4.length); pos += p4.length;
            System.arraycopy(p5, 0, buffer, pos, p5.length); pos += p5.length;
            System.arraycopy(p6, 0, buffer, pos, p6.length); pos += p6.length;
            System.arraycopy(p7, 0, buffer, pos, p7.length); pos += p7.length;
        }
        
        // Handle remaining phrases (0-7) that don't fit in a full group
        for (int i = 0; i < remainder; i++) {
            // Generate random index using XorShift64
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            
            byte[] phrase = precomputedPhrases[(int)(seed) & mask];
            
            // Flush if needed before copying
            if (pos + phrase.length > buffer.length) {
                out.write(buffer, 0, pos);
                pos = 0;
            }
            
            System.arraycopy(phrase, 0, buffer, pos, phrase.length);
            pos += phrase.length;
        }
        
        // Flush any remaining data in buffer
        if (pos > 0) {
            out.write(buffer, 0, pos);
        }
    }

    /**
     * Generates phrases using stack-based grammar expansion.
     * 
     * <p>This is the fallback path for large grammars where precomputation is not
     * feasible. Each phrase is generated by:</p>
     * <ol>
     *   <li>Push the start symbol onto the stack</li>
     *   <li>Pop a symbol; if terminal, output it; if non-terminal, pick a random
     *       production and push its symbols in reverse order</li>
     *   <li>Repeat until stack is empty</li>
     * </ol>
     * 
     * @param out the output stream to write phrases to
     * @param numPhrases the number of phrases to generate
     * @throws IOException if writing to the output stream fails
     */
    private static void generateWithStack(OutputStream out, int numPhrases) throws IOException {
        // 1MB output buffer for batching writes
        byte[] buffer = new byte[1024 * 1024];
        int pos = 0;
        
        // Stack for grammar expansion (256 should be enough for most grammars)
        int[] stack = new int[256];
        
        // Initialize XorShift64 PRNG
        long seed = System.nanoTime();

        for (int i = 0; i < numPhrases; i++) {
            // Start expansion with the start symbol
            int top = 0;
            stack[top++] = startSymbol;

            // Expand until no symbols remain
            while (top > 0) {
                int symbol = stack[--top];

                if (symbol < 0) {
                    // Terminal symbol: copy its bytes to buffer
                    byte[] term = terminals[~symbol];  // ~symbol converts negative ID to array index
                    int len = term.length;
                    
                    // Flush buffer if needed
                    if (pos + len > buffer.length) {
                        out.write(buffer, 0, pos);
                        pos = 0;
                    }
                    
                    System.arraycopy(term, 0, buffer, pos, len);
                    pos += len;
                } else {
                    // Non-terminal symbol: pick random production and push symbols
                    int[][] productions = grammar[symbol];
                    
                    // Generate random production index using XorShift64
                    seed ^= (seed << 21);
                    seed ^= (seed >>> 35);
                    seed ^= (seed << 4);
                    
                    // Use modulo for production selection (not power of 2)
                    int[] production = productions[(int)((seed & 0x7FFFFFFFL) % productions.length)];
                    
                    // Push symbols in reverse order so they pop in correct order
                    for (int j = production.length - 1; j >= 0; j--) {
                        stack[top++] = production[j];
                    }
                }
            }
            
            // Add newline after each phrase
            if (pos >= buffer.length - 1) {
                out.write(buffer, 0, pos);
                pos = 0;
            }
            buffer[pos++] = NEWLINE;
        }
        
        // Flush any remaining data
        if (pos > 0) {
            out.write(buffer, 0, pos);
        }
    }

    /**
     * Attempts to precompute all possible phrases from the grammar.
     * 
     * <p>If the grammar produces a small number of unique phrases (≤ MAX_PRECOMPUTED),
     * this method enumerates all of them and stores them as byte arrays with newlines
     * already appended. The array is padded to a power of 2 to enable fast modulo
     * operations using bitwise AND.</p>
     * 
     * <p>If the grammar is too large (too many phrases or too deep recursion),
     * precomputation is abandoned and the generator falls back to stack-based expansion.</p>
     */
    private static void tryPrecompute() {
        try {
            // Recursively expand all possible phrases from the start symbol
            ArrayList<byte[]> phrases = expand(startSymbol, 0);
            
            if (phrases.size() <= MAX_PRECOMPUTED) {
                int size = phrases.size();
                
                // Find next power of 2 >= size for fast modulo via bitwise AND
                int powerOf2 = Integer.highestOneBit(size);
                if (powerOf2 < size) powerOf2 <<= 1;
                
                precomputedPhrases = new byte[powerOf2][];
                phraseMask = powerOf2 - 1;  // Mask for bitwise AND modulo
                
                // Convert phrases to byte arrays with newlines appended
                for (int i = 0; i < size; i++) {
                    byte[] orig = phrases.get(i);
                    byte[] withNewline = new byte[orig.length + 1];
                    System.arraycopy(orig, 0, withNewline, 0, orig.length);
                    withNewline[orig.length] = NEWLINE;
                    precomputedPhrases[i] = withNewline;
                }
                
                // Fill padding slots by cycling through existing phrases
                // This ensures all array indices are valid
                for (int i = size; i < powerOf2; i++) {
                    precomputedPhrases[i] = precomputedPhrases[i % size];
                }
            }
        } catch (RuntimeException e) {
            // Grammar too large (too many phrases or too deep) - use stack-based generation
            precomputedPhrases = null;
        }
    }

    /**
     * Recursively expands a grammar symbol into all possible byte array outputs.
     * 
     * <p>For terminals, returns a single-element list containing the terminal's bytes.
     * For non-terminals, returns the Cartesian product of all productions' expansions.</p>
     * 
     * @param symbol the symbol to expand (>= 0 for non-terminal, < 0 for terminal)
     * @param depth current recursion depth (for cycle detection)
     * @return list of all possible byte array outputs for this symbol
     * @throws RuntimeException if recursion is too deep or too many phrases generated
     */
    private static ArrayList<byte[]> expand(int symbol, int depth) {
        // Prevent infinite recursion from cyclic grammars
        if (depth > 50) throw new RuntimeException("Too deep");

        ArrayList<byte[]> results = new ArrayList<>();

        if (symbol < 0) {
            // Terminal: return its byte representation
            results.add(terminals[~symbol]);
            return results;
        }

        // Non-terminal: expand each production and collect all results
        for (int[] production : grammar[symbol]) {
            // Start with empty prefix
            ArrayList<byte[]> current = new ArrayList<>();
            current.add(new byte[0]);

            // For each symbol in production, expand and combine with current prefixes
            for (int s : production) {
                ArrayList<byte[]> expansions = expand(s, depth + 1);
                ArrayList<byte[]> next = new ArrayList<>();
                
                // Cartesian product: combine each prefix with each expansion
                for (byte[] prefix : current) {
                    for (byte[] suffix : expansions) {
                        // Concatenate prefix and suffix
                        byte[] combined = new byte[prefix.length + suffix.length];
                        System.arraycopy(prefix, 0, combined, 0, prefix.length);
                        System.arraycopy(suffix, 0, combined, prefix.length, suffix.length);
                        next.add(combined);
                        
                        // Bail out if too many phrases
                        if (next.size() > MAX_PRECOMPUTED) {
                            throw new RuntimeException("Too many phrases");
                        }
                    }
                }
                current = next;
            }
            
            results.addAll(current);
            if (results.size() > MAX_PRECOMPUTED) {
                throw new RuntimeException("Too many phrases");
            }
        }
        
        return results;
    }

    /**
     * Parses a grammar file and initializes all static data structures.
     * 
     * <p>Grammar file format:</p>
     * <pre>
     * {
     * &lt;non-terminal-name&gt;
     * production1
     * production2
     * ...
     * }
     * </pre>
     * 
     * <p>Productions can contain terminals (literal text) and non-terminals
     * (enclosed in angle brackets like &lt;name&gt;).</p>
     * 
     * @param filePath path to the grammar file
     * @throws IOException if the file cannot be read
     */
    private static void parseGrammar(String filePath) throws IOException {
        // Maps for assigning IDs to symbols
        Map<String, Integer> nonTerminalMap = new HashMap<>();
        ArrayList<byte[]> terminalList = new ArrayList<>();
        Map<String, Integer> terminalMap = new HashMap<>();
        
        // Builder for constructing grammar array
        ArrayList<ArrayList<int[]>> grammarBuilder = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Look for start of non-terminal definition
                if (line.isEmpty() || line.charAt(0) != '{') continue;

                // Read non-terminal name
                String ntName = br.readLine();
                if (ntName == null) break;

                // Get or create ID for this non-terminal
                int ntId = getNonTerminalId(ntName, nonTerminalMap, grammarBuilder);
                ArrayList<int[]> productions = grammarBuilder.get(ntId);

                // Read all productions until closing brace
                while ((line = br.readLine()) != null && !line.startsWith("}")) {
                    if (!line.isEmpty()) {
                        productions.add(parseProduction(line, nonTerminalMap, 
                                                         terminalList, terminalMap, grammarBuilder));
                    }
                }
            }
        }

        // Convert ArrayLists to arrays for faster access
        grammar = new int[grammarBuilder.size()][][];
        for (int i = 0; i < grammarBuilder.size(); i++) {
            ArrayList<int[]> prods = grammarBuilder.get(i);
            grammar[i] = prods.toArray(new int[prods.size()][]);
        }
        
        terminals = terminalList.toArray(new byte[0][]);
        startSymbol = nonTerminalMap.get("<start>");
        
        // Attempt to precompute all phrases for small grammars
        tryPrecompute();
    }

    /**
     * Gets or creates an ID for a non-terminal symbol.
     * 
     * @param name the non-terminal name (e.g., "&lt;start&gt;")
     * @param map the name-to-ID mapping
     * @param builder the grammar builder to add new entries to
     * @return the ID for this non-terminal
     */
    private static int getNonTerminalId(String name, Map<String, Integer> map, 
                                         ArrayList<ArrayList<int[]>> builder) {
        Integer id = map.get(name);
        if (id != null) return id;
        
        // Assign new ID and create production list
        id = map.size();
        map.put(name, id);
        builder.add(new ArrayList<>());
        return id;
    }

    /**
     * Gets or creates an ID for a terminal symbol.
     * 
     * <p>Terminal IDs are encoded as negative numbers: the actual array index
     * is obtained using bitwise NOT (~id).</p>
     * 
     * @param value the terminal string value
     * @param list the list of terminal byte arrays
     * @param map the value-to-ID mapping
     * @return the encoded ID for this terminal (negative number)
     */
    private static int getTerminalId(String value, ArrayList<byte[]> list, Map<String, Integer> map) {
        Integer id = map.get(value);
        if (id != null) return id;
        
        // Assign new index and store byte array
        int index = list.size();
        list.add(value.getBytes());
        id = ~index;  // Encode as negative using bitwise NOT
        map.put(value, id);
        return id;
    }

    /**
     * Parses a production rule into an array of symbol IDs.
     * 
     * <p>Scans the line for non-terminals (text between &lt; and &gt;) and
     * terminals (everything else). Each symbol is assigned an ID.</p>
     * 
     * @param line the production rule text
     * @param ntMap non-terminal name-to-ID map
     * @param termList list of terminal byte arrays
     * @param termMap terminal value-to-ID map
     * @param builder grammar builder for creating new non-terminals
     * @return array of symbol IDs representing this production
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
                // No more non-terminals; rest of line is terminal
                if (i < line.length()) {
                    symbols.add(getTerminalId(line.substring(i), termList, termMap));
                }
                break;
            }
            
            // Text before '<' is a terminal
            if (open > i) {
                symbols.add(getTerminalId(line.substring(i, open), termList, termMap));
            }
            
            // Extract non-terminal name (including angle brackets)
            int close = line.indexOf('>', open);
            String ntName = line.substring(open, close + 1);
            symbols.add(getNonTerminalId(ntName, ntMap, builder));
            i = close + 1;
        }
        
        // Convert ArrayList to primitive array
        int[] result = new int[symbols.size()];
        for (int j = 0; j < symbols.size(); j++) {
            result[j] = symbols.get(j);
        }
        return result;
    }
}
