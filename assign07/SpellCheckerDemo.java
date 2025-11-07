package assign07;

import java.io.File;
import java.util.List;

/**
 * A small demonstration of the SpellChecker class. Demonstrates spell-checking
 * functionality on sample documents using a dictionary file.
 *
 * @author Erin Parker
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version October 23, 2025
 */
public class SpellCheckerDemo {

    /**
     * Main method to demonstrate the SpellChecker functionality. Creates a
     * spell checker with dictionary.txt and tests it on hello_world.txt and
     * good_luck.txt, printing misspelled words to the console.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        SpellChecker mySC = new SpellChecker(new File("./school/src/main/java/assign07/dictionary.txt"));

        runSpellCheck(mySC, "./school/src/main/java/assign07/hello_world.txt");
        runSpellCheck(mySC, "./school/src/main/java/assign07/good_luck.txt");
    }

    /**
     * Runs the given spell checker (with dictionary already added) on the
     * specified file.
     *
     * @param sc - the given spell checker
     * @param documentFilename - name of the file to be spell checked
     */
    private static void runSpellCheck(SpellChecker sc, String documentFilename) {

        File doc = new File(documentFilename);
        List<String> misspelledWords = sc.spellCheck(doc);
        if (misspelledWords.size() == 0) {
            System.out.println("There are no misspelled words in file " + doc + ".");
        } else {
            System.out.println("The misspelled words in file " + doc + " are:");
            for (String w : misspelledWords) {
                System.out.println("\t" + w);
            }
        }
    }
}
