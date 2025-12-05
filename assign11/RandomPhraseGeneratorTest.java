package comprehensive;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Test cases for RandomPhraseGenerator to ensure correctness after
 * optimization.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version Dec 4, 2025
 */
public class RandomPhraseGeneratorTest {

    private static final String SUPER_SIMPLE_GRAMMAR = "src/main/java/comprehensive/super_simple.g";
    private static final String POETIC_GRAMMAR = "src/main/java/comprehensive/poetic_sentence.g";

    /**
     * Captures output from RandomPhraseGenerator.main()
     */
    private String captureOutput(String grammarFile, int numPhrases) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        RandomPhraseGenerator.main(new String[]{grammarFile, String.valueOf(numPhrases)});

        System.setOut(originalOut);
        return baos.toString();
    }

    @Test
    public void testSuperSimpleGrammarProducesOutput() {
        String output = captureOutput(SUPER_SIMPLE_GRAMMAR, 10);
        assertFalse(output.isEmpty(), "Output should not be empty");

        String[] lines = output.trim().split("\n");
        assertEquals(10, lines.length, "Should produce exactly 10 phrases");
    }

    @Test
    public void testPoeticGrammarProducesOutput() {
        String output = captureOutput(POETIC_GRAMMAR, 5);
        assertFalse(output.isEmpty(), "Output should not be empty");

        String[] lines = output.trim().split("\n");
        assertEquals(5, lines.length, "Should produce exactly 5 phrases");
    }

    @Test
    public void testPoeticGrammarStructure() {
        // Generate many phrases and verify they all match expected structure
        String output = captureOutput(POETIC_GRAMMAR, 100);
        String[] lines = output.trim().split("\n");

        for (String line : lines) {
            assertTrue(line.startsWith("The "), "Each phrase should start with 'The '");
            assertTrue(line.endsWith(" tonight."), "Each phrase should end with ' tonight.'");
        }
    }

    @Test
    public void testPoeticGrammarContainsValidObjects() {
        Set<String> validObjects = new HashSet<>(Arrays.asList("waves", "big yellow flowers", "slugs"));
        String output = captureOutput(POETIC_GRAMMAR, 100);
        String[] lines = output.trim().split("\n");

        for (String line : lines) {
            // Extract the object (after "The " and before the verb)
            boolean foundValidObject = false;
            for (String obj : validObjects) {
                if (line.contains(obj)) {
                    foundValidObject = true;
                    break;
                }
            }
            assertTrue(foundValidObject, "Line should contain a valid object: " + line);
        }
    }

    @Test
    public void testPoeticGrammarContainsValidVerbs() {
        // Valid verb patterns
        Set<String> verbPatterns = new HashSet<>(Arrays.asList(
                "sigh warily", "sigh grumpily",
                "die warily", "die grumpily",
                "portend like waves", "portend like big yellow flowers", "portend like slugs"
        ));

        String output = captureOutput(POETIC_GRAMMAR, 200);
        String[] lines = output.trim().split("\n");

        for (String line : lines) {
            boolean foundValidVerb = false;
            for (String verb : verbPatterns) {
                if (line.contains(verb)) {
                    foundValidVerb = true;
                    break;
                }
            }
            assertTrue(foundValidVerb, "Line should contain a valid verb pattern: " + line);
        }
    }

    @Test
    public void testZeroPhrases() {
        String output = captureOutput(POETIC_GRAMMAR, 0);
        assertTrue(output.isEmpty() || output.trim().isEmpty(), "Zero phrases should produce empty output");
    }

    @Test
    public void testLargeBatchProducesCorrectCount() {
        String output = captureOutput(POETIC_GRAMMAR, 1000);
        String[] lines = output.trim().split("\n");
        assertEquals(1000, lines.length, "Should produce exactly 1000 phrases");
    }

    @Test
    public void testDeterministicWithSameSeed() {
        // This tests that multiple runs produce varied output (randomness works)
        String output1 = captureOutput(POETIC_GRAMMAR, 50);
        String output2 = captureOutput(POETIC_GRAMMAR, 50);

        // With 50 phrases, it's extremely unlikely both outputs are identical
        // This verifies randomness is working
        // Note: There's a tiny chance this fails due to extreme coincidence
        assertNotEquals(output1, output2, "Two runs should produce different outputs (randomness check)");
    }

    @Test
    public void testRepeatedCallsWork() {
        // Verify the generator can be called multiple times (static state resets properly)
        for (int i = 0; i < 5; i++) {
            String output = captureOutput(POETIC_GRAMMAR, 10);
            String[] lines = output.trim().split("\n");
            assertEquals(10, lines.length, "Run " + i + " should produce 10 phrases");
        }
    }
}
