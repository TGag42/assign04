package assign07;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Represents a "dictionary" of strings using a binary search tree and offers
 * methods for spell-checking documents.
 *
 * The dictionary is case-insensitive: all words are stored and checked in
 * lowercase. For example, "Tree", "tree", and "TREE" are all considered the
 * same word.
 *
 * Non-alphabetic characters (digits, punctuation, symbols) are ignored when
 * reading words from files.
 *
 * @author Erin Parker
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 23, 2025
 */
public class SpellChecker {

    private BinarySearchTree<String> dictionary;

    /**
     * Default constructor--creates an empty dictionary.
     */
    public SpellChecker() {
        dictionary = new BinarySearchTree<>();
    }

    /**
     * Creates a dictionary from a list of words. All words are converted to
     * lowercase and added to the dictionary.
     *
     * @param words the list of words to add to the dictionary
     */
    public SpellChecker(List<String> words) {
        this();
        buildDictionary(words);
    }

    /**
     * Creates a dictionary from a file. Reads words from the file, converts
     * them to lowercase, and adds them to the dictionary. Non-alphabetic
     * characters are ignored.
     *
     * @param dictionaryFile the file containing dictionary words
     */
    public SpellChecker(File dictionaryFile) {
        this();
        buildDictionary(readFromFile(dictionaryFile));
    }

    /**
     * Adds a word to the dictionary. The word is converted to lowercase and
     * whitespace is trimmed. Null and empty strings are ignored.
     *
     * @param word the word to add to the dictionary
     */
    public void addToDictionary(String word) {
        if (word == null) {
            return;
        }
        String w = word.toLowerCase().trim();
        if (!w.isEmpty()) {
            dictionary.add(w);
        }
    }

    /**
     * Removes a word from the dictionary. The word is converted to lowercase
     * and whitespace is trimmed. Null and empty strings are ignored.
     *
     * @param word the word to remove from the dictionary
     */
    public void removeFromDictionary(String word) {
        if (word == null) {
            return;
        }
        String w = word.toLowerCase().trim();
        if (!w.isEmpty()) {
            dictionary.remove(w);
        }
    }

    /**
     * Spell-checks a document against the dictionary. Returns a list of
     * misspelled words (words not found in the dictionary). The list preserves
     * first-appearance order and contains no duplicates. All checking is
     * case-insensitive.
     *
     * @param documentFile the file to spell-check
     * @return a list of misspelled words (unique, in first-appearance order)
     */
    public List<String> spellCheck(File documentFile) {
        List<String> wordsToCheck = readFromFile(documentFile);

        // preserve order, avoid duplicates
        Set<String> misspelled = new LinkedHashSet<>();
        for (String w : wordsToCheck) {
            if (!dictionary.contains(w)) {
                misspelled.add(w);
            }
        }
        return new ArrayList<>(misspelled);
    }

    /**
     * Private helper method to fill in the dictionary with the input list of
     * words. Each word is processed through addToDictionary (lowercase,
     * trimmed).
     *
     * @param words the list of words to add to the dictionary
     */
    private void buildDictionary(List<String> words) {
        if (words == null) {
            return;
        }
        for (String w : words) {
            addToDictionary(w);
        }
    }

    /**
     * Returns a list of the words contained in the specified file. Uses a
     * delimiter that splits on non-alphabetic characters, so symbols, digits,
     * punctuation, and spaces are ignored. All words are converted to
     * lowercase.
     *
     * @param file the file to read words from
     * @return a list of words from the file (all lowercase)
     */
    private List<String> readFromFile(File file) {
        ArrayList<String> words = new ArrayList<>();

        try {
            Scanner fileInput = new Scanner(file);
            fileInput.useDelimiter("\\s*[^a-zA-Z]\\s*");

            while (fileInput.hasNext()) {
                String s = fileInput.next();
                if (!s.equals("")) {
                    words.add(s.toLowerCase());
                }
            }

            fileInput.close();

        } catch (FileNotFoundException e) {
            System.err.println("File " + file + " cannot be found.");
        }

        return words;
    }
}
