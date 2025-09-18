package assign04;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for {@link AnagramChecker}.
 *
 * @author Tyler Gagliardi & Alex Waldmann
 */
public class AnagramCheckerTest {

    // Helper: compare arrays ignoring order and case
    private static void assertArrayEqualsIgnoreOrderAndCase(String[] actual, String[] expected) {
        assertNotNull(actual, "actual array is null");
        assertEquals(expected.length, actual.length, "lengths differ");
        Set<String> actualSet = new HashSet<>();
        for (String s : actual) {
            actualSet.add(s.toLowerCase(Locale.ROOT));
        }
        Set<String> expectedSet = new HashSet<>();
        for (String s : expected) {
            expectedSet.add(s.toLowerCase(Locale.ROOT));
        }
        assertEquals(expectedSet, actualSet);
    }

    private static Set<String> toLowerSet(String... items) {
        Set<String> set = new HashSet<>();
        for (String s : items) {
            set.add(s.toLowerCase(Locale.ROOT));
        }
        return set;
    }

    @Nested
    class SortTests {

        @Test
        void mixedCapitalization() {
            assertEquals("FIal", AnagramChecker.sort("aFIl"));
        }

        @Test
        void sortEmptyString() {
            assertEquals("", AnagramChecker.sort(""));
        }

        @Test
        void sortNullThrowsNPE() {
            assertThrows(NullPointerException.class, () -> AnagramChecker.sort(null));
        }
    }

    @Nested
    class AreAnagramsTests {

        @Test
        void detectsAnagramsIgnoringCase() {
            assertTrue(AnagramChecker.areAnagrams("Begin", "being"));
        }

        @Test
        void differentLengthsAreNotAnagrams() {
            assertFalse(AnagramChecker.areAnagrams("abc", "ab"));
        }

        @Test
        void differentLettersSameLengthAreNotAnagrams() {
            assertFalse(AnagramChecker.areAnagrams("abcd", "abce"));
        }

        @Test
        void emptyStringsAreAnagrams() {
            assertTrue(AnagramChecker.areAnagrams("", ""));
        }

        @Test
        void nullParamsThrowNPE() {
            assertThrows(NullPointerException.class, () -> AnagramChecker.areAnagrams(null, "abc"));
            assertThrows(NullPointerException.class, () -> AnagramChecker.areAnagrams("abc", null));
        }
    }

    @Nested
    class LargestGroupFromArrayTests {

        @Test
        void findsLargestGroupHappyPath() {
            String[] input = new String[]{
                "carets", "Caters", "caster", "crates", "Reacts", "recast", "traces",
                "orange", "purple"
            };
            String[] result = AnagramChecker.getLargestAnagramGroup(input);
            assertNotNull(result);
            assertEquals(7, result.length);
            String[] expected = {
                "carets", "Caters", "caster", "crates", "Reacts", "recast", "traces"
            };
            assertArrayEqualsIgnoreOrderAndCase(expected, result);
        }

        @Test
        void tieBetweenGroupsReturnsEither() {
            String[] groupA = {"care", "race", "acre"};
            String[] groupB = {"angel", "Angle", "glean"};
            String[] input = new String[]{"zzz", groupA[0], groupA[1], groupA[2], groupB[0], groupB[1], groupB[2], "yyy"};

            String[] result = AnagramChecker.getLargestAnagramGroup(input);
            assertNotNull(result);
            assertEquals(3, result.length);

            Set<String> got = new HashSet<>();
            Arrays.stream(result).forEach(s -> got.add(s.toLowerCase(Locale.ROOT)));

            Set<String> a = toLowerSet(groupA);
            Set<String> b = toLowerSet(groupB);
            assertTrue(got.equals(a) || got.equals(b), "Result should be one of the tied groups");
        }

        @Test
        void noAnagramsYieldsEmptyArray() {
            String[] input = {"apple", "banana", "cherry"};
            String[] result = AnagramChecker.getLargestAnagramGroup(input);
            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        void nullArrayThrowsNPE() {
            assertThrows(NullPointerException.class, () -> AnagramChecker.getLargestAnagramGroup((String[]) null));
        }
    }

    @Nested
    class LargestGroupFromFileTests {

        @Test
        void findsLargestGroupFromSampleFile() {
            String[] result = AnagramChecker.getLargestAnagramGroup("./src/main/java/assign04/sample_word_list.txt");
            assertNotNull(result);
            String[] expected = {
                "carets", "Caters", "caster", "crates", "Reacts", "recast", "traces"
            };
            assertArrayEquals(expected, result);
        }

        @Test
        void nonExistentFileYieldsEmptyArray() {
            assertArrayEquals(new String[0], AnagramChecker.getLargestAnagramGroup("assign04/does_not_exist.txt"));
        }

        @Test
        void nullFilePathThrowsNPE() {
            assertThrows(NullPointerException.class, () -> AnagramChecker.getLargestAnagramGroup((String) null));
        }
    }

    @Nested
    class InsertionSortSmokeTest {

        @Test
        void sortsIntegersWithComparator() {
            Integer[] arr = {3, 1, 2};
            AnagramChecker.insertionSort(arr, Integer::compareTo);
            assertArrayEquals(new Integer[]{1, 2, 3}, arr);
        }
    }
}
