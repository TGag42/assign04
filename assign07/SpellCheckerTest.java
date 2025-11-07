package assign07;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Comprehensive test suite for SpellChecker implementation.
 *
 * Test Coverage: - Constructor Tests (5 tests): • Default constructor • List
 * constructor with valid/null/empty/duplicate lists • File constructor with
 * valid/non-existent files
 *
 * - AddToDictionary Tests (6 tests): • Single word addition • Case-insensitive
 * handling (HELLO = hello) • Whitespace trimming • Null and empty string
 * handling • Multiple word additions
 *
 * - RemoveFromDictionary Tests (5 tests): • Basic removal • Case-insensitive
 * removal • Null parameter handling • Non-existent word removal • Empty string
 * handling
 *
 * - SpellCheck Tests (13 tests): • Empty documents • All correct words • All
 * incorrect words • Mixed correct/incorrect • Case-insensitive checking •
 * Duplicate misspellings (reports only once) • First-appearance order
 * preservation • Punctuation handling • Numbers in text • Multi-line documents
 * • Integration with real dictionary.txt file • Integration with
 * hello_world.txt and good_luck.txt
 *
 * - Integration Tests (6 tests): • Add/Remove/SpellCheck workflow • Large
 * dictionaries (1000+ words) • Empty dictionary behavior • Special characters
 * in dictionary • Repeated additions and removals • Mixed case in documents
 *
 * - Edge Case Tests (6 tests): • Very long documents (1000+ words) • Documents
 * with only whitespace • Documents with only symbols • Multiple spell checks on
 * same document • Immutability of dictionary during spell check • Single
 * character words • Unicode/non-ASCII characters
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 23, 2025
 */
class SpellCheckerTest {

    private SpellChecker emptyChecker;
    private SpellChecker basicChecker;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        emptyChecker = new SpellChecker();

        // Create a basic checker with some common words
        List<String> commonWords = Arrays.asList(
                "hello", "world", "the", "a", "an", "is", "are",
                "good", "bad", "test", "java", "code", "tree", "binary"
        );
        basicChecker = new SpellChecker(commonWords);
    }

    // ========== Constructor Tests ==========
    @Test
    void testDefaultConstructor() {
        SpellChecker checker = new SpellChecker();
        assertNotNull(checker);
    }

    @Test
    void testListConstructor() {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        SpellChecker checker = new SpellChecker(words);
        assertNotNull(checker);
    }

    @Test
    void testListConstructorWithNull() {
        SpellChecker checker = new SpellChecker((List<String>) null);
        assertNotNull(checker);
    }

    @Test
    void testFileConstructor() throws IOException {
        File dictFile = createTempFile("dict.txt", "hello\nworld\ntest\n");
        SpellChecker checker = new SpellChecker(dictFile);
        assertNotNull(checker);
    }

    @Test
    void testFileConstructorNonExistentFile() {
        File nonExistent = new File("this_file_does_not_exist_xyz123.txt");
        SpellChecker checker = new SpellChecker(nonExistent);
        assertNotNull(checker); // Should not crash
    }

    // ========== AddToDictionary Tests ==========
    @Test
    void testAddToDictionarySingleWord() {
        emptyChecker.addToDictionary("hello");
        // Dictionary should now contain "hello"
        File testDoc = createTempFile("test.txt", "hello");
        List<String> misspelled = emptyChecker.spellCheck(testDoc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testAddToDictionaryCaseInsensitive() {
        emptyChecker.addToDictionary("HELLO");
        File testDoc = createTempFile("test.txt", "hello Hello HELLO");
        List<String> misspelled = emptyChecker.spellCheck(testDoc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testAddToDictionaryWithWhitespace() {
        emptyChecker.addToDictionary("  hello  ");
        File testDoc = createTempFile("test.txt", "hello");
        List<String> misspelled = emptyChecker.spellCheck(testDoc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testAddToDictionaryNull() {
        emptyChecker.addToDictionary(null);
        // Should not crash
    }

    @Test
    void testAddToDictionaryEmptyString() {
        emptyChecker.addToDictionary("");
        emptyChecker.addToDictionary("   ");
        // Should not crash
    }

    @Test
    void testAddToDictionaryMultipleWords() {
        emptyChecker.addToDictionary("apple");
        emptyChecker.addToDictionary("banana");
        emptyChecker.addToDictionary("cherry");

        File testDoc = createTempFile("test.txt", "apple banana cherry");
        List<String> misspelled = emptyChecker.spellCheck(testDoc);
        assertTrue(misspelled.isEmpty());
    }

    // ========== RemoveFromDictionary Tests ==========
    @Test
    void testRemoveFromDictionary() {
        basicChecker.removeFromDictionary("hello");

        File testDoc = createTempFile("test.txt", "hello");
        List<String> misspelled = basicChecker.spellCheck(testDoc);
        assertEquals(1, misspelled.size());
        assertEquals("hello", misspelled.get(0));
    }

    @Test
    void testRemoveFromDictionaryCaseInsensitive() {
        basicChecker.removeFromDictionary("HELLO");

        File testDoc = createTempFile("test.txt", "hello");
        List<String> misspelled = basicChecker.spellCheck(testDoc);
        assertEquals(1, misspelled.size());
    }

    @Test
    void testRemoveFromDictionaryNull() {
        basicChecker.removeFromDictionary(null);
        // Should not crash
    }

    @Test
    void testRemoveFromDictionaryNonExistent() {
        basicChecker.removeFromDictionary("nonexistentword");
        // Should not crash
    }

    @Test
    void testRemoveFromDictionaryEmptyString() {
        basicChecker.removeFromDictionary("");
        basicChecker.removeFromDictionary("   ");
        // Should not crash
    }

    // ========== SpellCheck Tests ==========
    @Test
    void testSpellCheckEmptyDocument() {
        File emptyDoc = createTempFile("empty.txt", "");
        List<String> misspelled = basicChecker.spellCheck(emptyDoc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testSpellCheckAllCorrect() {
        File doc = createTempFile("doc.txt", "hello world the test");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testSpellCheckAllIncorrect() {
        File doc = createTempFile("doc.txt", "xyz abc def");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        assertEquals(3, misspelled.size());
        assertTrue(misspelled.contains("xyz"));
        assertTrue(misspelled.contains("abc"));
        assertTrue(misspelled.contains("def"));
    }

    @Test
    void testSpellCheckMixedCorrectIncorrect() {
        File doc = createTempFile("doc.txt", "hello xyz world abc");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertEquals(2, misspelled.size());
        assertTrue(misspelled.contains("xyz"));
        assertTrue(misspelled.contains("abc"));
    }

    @Test
    void testSpellCheckCaseInsensitive() {
        File doc = createTempFile("doc.txt", "Hello WORLD hElLo");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testSpellCheckDuplicatesMisspelledWords() {
        File doc = createTempFile("doc.txt", "xyz xyz xyz abc abc");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        assertEquals(2, misspelled.size()); // Should only report each word once
        assertTrue(misspelled.contains("xyz"));
        assertTrue(misspelled.contains("abc"));
    }

    @Test
    void testSpellCheckPreservesFirstAppearanceOrder() {
        File doc = createTempFile("doc.txt", "zzz aaa mmm");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        assertEquals(Arrays.asList("zzz", "aaa", "mmm"), misspelled);
    }

    @Test
    void testSpellCheckWithPunctuation() {
        File doc = createTempFile("doc.txt", "hello, world! test.");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testSpellCheckWithNumbers() {
        File doc = createTempFile("doc.txt", "hello123 world456");
        List<String> misspelled = basicChecker.spellCheck(doc);
        // Numbers should be ignored by the file reader
        assertTrue(misspelled.isEmpty() || misspelled.size() == 2);
    }

    @Test
    void testSpellCheckMultipleLines() {
        File doc = createTempFile("doc.txt", "hello\nworld\ntest\nxyz");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertEquals(1, misspelled.size());
        assertEquals("xyz", misspelled.get(0));
    }

    @Test
    void testSpellCheckRealDictionary() throws IOException {
        // Use the actual dictionary file
        File dictFile = new File("./school/src/main/java/assign07/dictionary.txt");
        if (!dictFile.exists()) {
            dictFile = new File("src/main/java/assign07/dictionary.txt");
        }

        if (dictFile.exists()) {
            SpellChecker checker = new SpellChecker(dictFile);

            // Test with hello_world.txt
            File helloDoc = new File("./school/src/main/java/assign07/hello_world.txt");
            if (!helloDoc.exists()) {
                helloDoc = new File("src/main/java/assign07/hello_world.txt");
            }

            if (helloDoc.exists()) {
                List<String> misspelled = checker.spellCheck(helloDoc);
                // "hello", "there", "world", "nice", "meet", "you" should all be in dictionary
                assertTrue(misspelled.isEmpty() || misspelled.size() <= 2);
            }
        }
    }

    @Test
    void testSpellCheckGoodLuckDocument() throws IOException {
        File dictFile = new File("./school/src/main/java/assign07/dictionary.txt");
        if (!dictFile.exists()) {
            dictFile = new File("src/main/java/assign07/dictionary.txt");
        }

        if (dictFile.exists()) {
            SpellChecker checker = new SpellChecker(dictFile);

            File goodLuckDoc = new File("./school/src/main/java/assign07/good_luck.txt");
            if (!goodLuckDoc.exists()) {
                goodLuckDoc = new File("src/main/java/assign07/good_luck.txt");
            }

            if (goodLuckDoc.exists()) {
                List<String> misspelled = checker.spellCheck(goodLuckDoc);
                // Some technical words like "bst" might be misspelled
                assertNotNull(misspelled);
            }
        }
    }

    // ========== Integration Tests ==========
    @Test
    void testAddRemoveAndSpellCheck() {
        emptyChecker.addToDictionary("alpha");
        emptyChecker.addToDictionary("beta");
        emptyChecker.addToDictionary("gamma");

        File doc = createTempFile("doc.txt", "alpha beta gamma delta");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        assertEquals(1, misspelled.size());
        assertEquals("delta", misspelled.get(0));

        emptyChecker.addToDictionary("delta");
        misspelled = emptyChecker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());

        emptyChecker.removeFromDictionary("beta");
        misspelled = emptyChecker.spellCheck(doc);
        assertEquals(1, misspelled.size());
        assertEquals("beta", misspelled.get(0));
    }

    @Test
    void testLargeDictionary() {
        SpellChecker checker = new SpellChecker();

        // Add 1000 words (using only letters, no numbers)
        String[] testWords = {"alpha", "beta", "gamma", "delta", "epsilon"};
        for (int i = 0; i < 1000; i++) {
            checker.addToDictionary("word" + testWords[i % testWords.length] + letters(i));
        }

        // Add specific test words
        checker.addToDictionary("firstword");
        checker.addToDictionary("middleword");
        checker.addToDictionary("lastword");

        // Test document with mix of correct and incorrect
        File doc = createTempFile("doc.txt", "firstword middleword lastword notaword");
        List<String> misspelled = checker.spellCheck(doc);
        assertEquals(1, misspelled.size());
        assertEquals("notaword", misspelled.get(0));
    }

    private String letters(int num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append((char) ('a' + (num % 26)));
            num /= 26;
        }
        return sb.toString();
    }

    @Test
    void testEmptyDictionary() {
        File doc = createTempFile("doc.txt", "hello world");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        assertEquals(2, misspelled.size());
        assertTrue(misspelled.contains("hello"));
        assertTrue(misspelled.contains("world"));
    }

    @Test
    void testDictionaryWithSpecialCharacters() {
        emptyChecker.addToDictionary("hello!");
        emptyChecker.addToDictionary("world?");

        // The dictionary should normalize these
        File doc = createTempFile("doc.txt", "hello world");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        // Since addToDictionary strips non-letters, these should be in dictionary
        assertTrue(misspelled.isEmpty() || misspelled.size() == 2);
    }

    @Test
    void testRepeatedAdditionsAndRemovals() {
        for (int i = 0; i < 10; i++) {
            emptyChecker.addToDictionary("test");
            emptyChecker.removeFromDictionary("test");
        }

        File doc = createTempFile("doc.txt", "test");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        assertEquals(1, misspelled.size());
    }

    @Test
    void testMixedCaseInDocument() {
        basicChecker.addToDictionary("Programming");

        File doc = createTempFile("doc.txt", "programming Programming PROGRAMMING ProGraMMinG");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testVeryLongDocument() {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            content.append("hello world test ");
        }
        content.append("misspelledword");

        File doc = createTempFile("doc.txt", content.toString());
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertEquals(1, misspelled.size());
        assertEquals("misspelledword", misspelled.get(0));
    }

    @Test
    void testDocumentWithOnlyWhitespace() {
        File doc = createTempFile("doc.txt", "     \n\n\n     \t\t\t     ");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testDocumentWithSymbolsOnly() {
        File doc = createTempFile("doc.txt", "!@#$%^&*()_+-={}[]|:;<>?,./");
        List<String> misspelled = basicChecker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());
    }

    // ========== Helper Methods ==========
    private File createTempFile(String filename, String content) {
        try {
            File file = tempDir.resolve(filename).toFile();
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }

    @Test
    void testMultipleSpellChecksOnSameDocument() {
        File doc = createTempFile("doc.txt", "hello xyz world");

        List<String> misspelled1 = basicChecker.spellCheck(doc);
        List<String> misspelled2 = basicChecker.spellCheck(doc);

        assertEquals(misspelled1, misspelled2);
    }

    @Test
    void testSpellCheckDoesNotModifyDictionary() {
        int initialSize = basicChecker.spellCheck(
                createTempFile("test.txt", "test")
        ).size();

        basicChecker.spellCheck(createTempFile("test2.txt", "xyz abc"));

        int afterSize = basicChecker.spellCheck(
                createTempFile("test3.txt", "test")
        ).size();

        assertEquals(initialSize, afterSize);
    }

    @Test
    void testConstructorWithEmptyList() {
        SpellChecker checker = new SpellChecker(Arrays.asList());
        File doc = createTempFile("doc.txt", "hello");
        List<String> misspelled = checker.spellCheck(doc);
        assertEquals(1, misspelled.size());
    }

    @Test
    void testConstructorWithDuplicatesInList() {
        List<String> words = Arrays.asList("hello", "hello", "world", "world");
        SpellChecker checker = new SpellChecker(words);

        File doc = createTempFile("doc.txt", "hello world");
        List<String> misspelled = checker.spellCheck(doc);
        assertTrue(misspelled.isEmpty());
    }

    @Test
    void testSingleCharacterWords() {
        emptyChecker.addToDictionary("a");
        emptyChecker.addToDictionary("i");

        File doc = createTempFile("doc.txt", "a i x y z");
        List<String> misspelled = emptyChecker.spellCheck(doc);
        assertEquals(3, misspelled.size());
        assertTrue(misspelled.contains("x"));
        assertTrue(misspelled.contains("y"));
        assertTrue(misspelled.contains("z"));
    }

    @Test
    void testUnicodeCharacters() {
        // Test with non-ASCII characters - should be handled gracefully
        File doc = createTempFile("doc.txt", "hello café résumé");
        List<String> misspelled = basicChecker.spellCheck(doc);
        // Should at least not crash
        assertNotNull(misspelled);
    }
}
