package assign04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnagramChecker {

    public static void main(String[] args) {
        String filepath = "./anagramFile.txt";
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

    public static String sort(String s) {
        Character[] chars = new Character[s.length()];
        for (int i = 0; i < s.length(); i++) {
            chars[i] = s.charAt(i);
        }

        insertionSort(chars, Comparator.naturalOrder());

        StringBuilder sb = new StringBuilder(chars.length);
        for (Character c : chars) {
            sb.append(c.charValue());
        }
        return sb.toString();
    }

    public static <T> void insertionSort(T[] arr, Comparator<? super T> cmp) {
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            // Shift larger elements to the right
            while (j >= 0 && cmp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static boolean areAnagrams(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        }

        return sort(str1).equals(sort(str2));
    }

    private static String[] findLargestAnagramGroup(List<String> words) {
        Map<String, List<String>> anagramGroups = new HashMap<>();

        for (String str : words) {
            String sorted = sort(str);
            anagramGroups.computeIfAbsent(sorted, k -> new ArrayList<>()).add(str);
        }

        // Find the largest anagram group
        List<String> largestGroup = null;
        for (List<String> group : anagramGroups.values()) {
            if (largestGroup == null || group.size() > largestGroup.size()) {
                largestGroup = group;
            }
        }

        return largestGroup != null ? largestGroup.toArray(String[]::new) : null;
    }

    public static String[] getLargestAnagramGroup(String[] inputArr) {
        List<String> words = List.of(inputArr);
        return findLargestAnagramGroup(words);
    }

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
