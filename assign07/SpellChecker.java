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
 * Dictionary is case-insensitive: words are stored/checked in lowercase.
 * 
 * @author Erin Parker and Tyler Gagliardi
 * 
 * @version March 17, 2021
 */
public class SpellChecker {

	private BinarySearchTree<String> dictionary;

	/** Default constructor--creates empty dictionary. */
	public SpellChecker() {
		dictionary = new BinarySearchTree<String>();
	}

	/** Creates dictionary from a list of words. */
	public SpellChecker(List<String> words) {
		this();
		buildDictionary(words);
	}

	/** Creates dictionary from a file. */
	public SpellChecker(File dictionaryFile) {
		this();
		buildDictionary(readFromFile(dictionaryFile));
	}

	/** Add a word to the dictionary. */
	public void addToDictionary(String word) {
		if (word == null) return;
		String w = word.toLowerCase().trim();
		if (!w.isEmpty()) dictionary.add(w);
	}

	/** Remove a word from the dictionary. */
	public void removeFromDictionary(String word) {
		if (word == null) return;
		String w = word.toLowerCase().trim();
		if (!w.isEmpty()) dictionary.remove(w);
	}

	/**
	 * Spell-checks a document against the dictionary.
	 *
	 * @return a List of misspelled words (unique, in first-appearance order)
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

	/** Fills in the dictionary with the input list of words. */
	private void buildDictionary(List<String> words) {
		if (words == null) return;
		for (String w : words) addToDictionary(w);
	}

	/**
	 * Returns a list of the words contained in the specified file. (Note that
	 * symbols, digits, and spaces are ignored.)
	 */
	private List<String> readFromFile(File file) {
		ArrayList<String> words = new ArrayList<String>();

		try {
			Scanner fileInput = new Scanner(file);
			fileInput.useDelimiter("\\s*[^a-zA-Z]\\s*");

			while (fileInput.hasNext()) {
				String s = fileInput.next();
				if (!s.equals(""))
					words.add(s.toLowerCase());
			}

			fileInput.close();

		} catch (FileNotFoundException e) {
			System.err.println("File " + file + " cannot be found.");
		}

		return words;
	}
}
