package assign05;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes that implement this interface provide a method for selecting an
 * element in the given List to serve as the quicksort pivot. Elements in the
 * List must be Comparable.
 *
 * @author CS 2420 course staff, Alex Waldmann && Tyler Gagliardi
 * @version October 2nd, 2025
 */
public class PivotChooser<E extends Comparable<? super E>> {

    /**
     * Selects an element in the given List to serve as the quicksort pivot.
     *
     * @param list - list containing a portion to be sorted
     * @param leftIndex - (optional) position of first item in the sublist to be
     * sorted, if blank 0 is leftIndex
     * @param rightIndex - (optional) position of the last item in the sublist
     * to be sorted, if blank list.size()-1 is rightIndex
     * @return index of the list element selected to serve as the pivot
     * @throws IllegalArgumentException if the list is null or empty, or if
     * leftIndex or rightIndex are out of bounds or invalid
     */
    int choosePivotIndex(List<E> list, int leftIndex, int rightIndex) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        if (leftIndex < 0 || rightIndex >= list.size() || leftIndex > rightIndex) {
            throw new IllegalArgumentException("Invalid left or right index");
        }

        if (list.size() == 1) {
            return 0;
        } else if (list.size() < 5) {
            //If list length is less than 5, return median of first, middle, last
            E farLeftItem = list.get(leftIndex);
            E farRightItem = list.get(rightIndex);
            E middleItem = list.get((leftIndex + rightIndex) >>> 1);

            ArrayList<E> optionsList = new ArrayList<>();
            optionsList.add(farLeftItem);
            optionsList.add(middleItem);
            optionsList.add(farRightItem);

            optionsList.sort(E::compareTo);
            E median = optionsList.get(1);

            return list.indexOf(median);
        } else {
            //If larger than 5, get a larger sample size to find better median
            E farLeftItem = list.get(leftIndex);
            E farRightItem = list.get(rightIndex);
            E middleItem = list.get((leftIndex + rightIndex) >>> 1);

            //Get random index from left half (might have an index out of bounds exception need to test)
            E randomLeftItem = list.get(leftIndex + (int) (Math.random() * (rightIndex - ((leftIndex + rightIndex) >>> 1))));
            //Get random index from right half (bitwise is absolutley disgusting but actually so smart
            //8 is binary 1000, 4 is binary 0100, bitshifting one right will give us the int / 2 but
            //also absolutley disgusting and only works with positive integers, but because index is
            //always positive it works here and avoids overflow)
            E randomRightItem = list.get(((leftIndex + rightIndex) >>> 1) + (int) (Math.random() * (rightIndex - ((leftIndex + rightIndex) >>> 1))));

            //Create a list of options
            ArrayList<E> optionsList = new ArrayList<>();
            optionsList.add(farLeftItem);
            optionsList.add(middleItem);
            optionsList.add(farRightItem);
            optionsList.add(randomLeftItem);
            optionsList.add(randomRightItem);

            //Sort and get object so then we can return index from original list
            optionsList.sort(E::compareTo);
            E median = optionsList.get(2);

            return list.indexOf(median);
        }
    }
}
