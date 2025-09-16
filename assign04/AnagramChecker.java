package assign04;

import java.util.Comparator;

public class AnagramChecker {
	
	
	public static String sort(String s) {
		 Character[] chars = new Character[s.length()];
	        for (int i = 0; i < s.length(); i++) {
	            chars[i] = s.charAt(i);
	        }
	      
	     insertionSort(chars, Comparator.naturalOrder());

	     StringBuilder sb = new StringBuilder(chars.length);
	        for (Character c : chars) sb.append(c.charValue());
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
		// TODO
		return false;
	}
	
	public static String[] getLargestAnagramGroup(String[] inputArr) {
		// TODO
		return null;
	}
	
	public static String[] getLargestAnagramGroup(String filename) {
		// TODO
		return null;
	}
}
