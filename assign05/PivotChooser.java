package assign05;

import java.util.List;

/**
 * Classes that implement this interface provide a method for selecting an
 * element in the given List to serve as the quicksort pivot. Elements in
 * the List are Comparable.
 * 
 * @author CS 2420 course staff
 * @version February 6, 2025
 */
public interface PivotChooser<E extends Comparable<? super E>> {

	/**
	 * Selects an element in the given List to serve as the quicksort pivot.
	 * 
	 * @param list - list containing a portion to be sorted
	 * @param leftIndex - position of first item in the sublist to be sorted
	 * @param rightIndex - position of the last item in the sublist to be sorted
	 * @return index of the list element selected to serve as the pivot
	 */
	int getPivotIndex(List<E> list, int leftIndex, int rightIndex);
}