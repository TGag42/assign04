package assign04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to check for anagrams and find the largest group of anagrams
 * in a list of words or in a text file.
 */
public class AnagramChecker {

    /**
     * Main method that reads words from the default file and prints the largest
     * group of anagrams found.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        String filepath = "./assign04/sample_word_list.txt";
        String[] largestAnagramGroup = getLargestAnagramGroup(filepath);

        if (largestAnagramGroup != null && largestAnagramGroup.length > 0) {
            System.out.println("Largest anagram group:");
            for (String word : largestAnagramGroup) {
                System.out.println(word);
            }
        } else {
            System.out.println("No anagrams found.");
        }
    }

    /**
     * Returns a new string with the letters of the input string arranged in
     * alphabetical order.
     *
     * @param s the string to sort
     * @return the sorted string
     */
    public static String sort(String s) {
        Character[] chars = new Character[s.length()];
        for (int i = 0; i < s.length(); i++) {
            chars[i] = s.toLowerCase().charAt(i);
        }

        insertionSort(chars, Comparator.naturalOrder());

        StringBuilder sb = new StringBuilder(chars.length);
        for (Character c : chars) {
            sb.append(c.charValue());
        }
        return sb.toString();
    }

    /**
     * Sorts the given array in place using insertion sort and the provided
     * comparator.
     *
     * @param arr the array to sort
     * @param cmp the comparator to decide order
     * @param <T> the type of elements in the array
     */
    public static <T> void insertionSort(T[] arr, Comparator<? super T> cmp) {
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && cmp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Checks whether two strings are anagrams of each other.
     *
     * @param str1 the first string
     * @param str2 the second string
     * @return true if the strings are anagrams, false otherwise
     */
    public static boolean areAnagrams(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        }

        return sort(str1).equals(sort(str2));
    }

    /**
     * Finds the largest group of words that are anagrams from a list of words.
     *
     * @param words list of words to check
     * @return the largest anagram group as a string array, or [] if none found
     */
    private static String[] findLargestAnagramGroup(List<String> words) {
        Map<String, List<String>> anagramGroups = new HashMap<>();

        for (String str : words) {
            String sorted = sort(str);
            anagramGroups.computeIfAbsent(sorted, k -> new ArrayList<>()).add(str);
        }

        List<String> largestGroup = null;
        for (List<String> group : anagramGroups.values()) {
            if (largestGroup == null || group.size() > largestGroup.size()) {
                largestGroup = group;
            }
        }

        return largestGroup != null ? largestGroup.toArray(String[]::new) : null;
    }

    /**
     * Finds the largest anagram group in an array of words.
     *
     * @param inputArr array of words
     * @return the largest anagram group in a string array, or [] if none found
     */
    public static String[] getLargestAnagramGroup(String[] inputArr) {
        List<String> words = List.of(inputArr);
        return findLargestAnagramGroup(words);
    }

    /**
     * Reads a file and returns the largest group of anagrams found in the file.
     * Words in the file should be separated by commas and spaces.
     *
     * @param filepath the path to the input file
     * @return the largest anagram group in a string array, or [] if none found
     * or an error occurs
     */
    public static String[] getLargestAnagramGroup(String filepath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] wordsOnLine = line.split(", ");

                for (String word : wordsOnLine) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    words.add(word);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return findLargestAnagramGroup(words);
    }
}
