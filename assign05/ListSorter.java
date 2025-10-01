package assign05;

import java.util.ArrayList;
import java.util.List;
import assign05.PivotChooser;

public final class ListSorter {

    private ListSorter() {}

    public static <T extends Comparable<? super T>> void mergesort(List<T> list, int threshold) {
        if (list == null || list.size() <= 1) return;
        if (threshold <= 0) throw new IllegalArgumentException();

        int n = list.size();
        ArrayList<T> temp = new ArrayList<>(n);
        for (int i = 0; i < n; i++) temp.add(null);

        mergeSortRecursive(list, temp, 0, n - 1, threshold);
    }

    public static <T extends Comparable<? super T>> void quicksort(List<T> list, PivotChooser<T> chooser) {
        if (list == null || list.size() <= 1) return;
        if (chooser == null) throw new IllegalArgumentException();

        quickSortRecursive(list, 0, list.size() - 1, chooser);
    }

    public static List<Integer> generateAscending(int size) {
        if (size < 0) throw new IllegalArgumentException();
        ArrayList<Integer> out = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) out.add(i);
        return out;
    }

    private static <T extends Comparable<? super T>> void mergeSortRecursive(
            List<T> list, List<T> temp, int left, int right, int threshold) {

        int len = right - left + 1;
        if (len <= 1) return;

        if (len <= threshold) {
            insertionSortRange(list, left, right);
            return;
        }

        int mid = (left + right) >>> 1;
        mergeSortRecursive(list, temp, left, mid, threshold);
        mergeSortRecursive(list, temp, mid + 1, right, threshold);
        merge(list, temp, left, mid, right);

        for (int i = left; i <= right; i++) {
            list.set(i, temp.get(i));
        }
    }

    private static <T extends Comparable<? super T>> void merge(
            List<T> list, List<T> temp, int left, int mid, int right) {

        int i = left, j = mid + 1, k = left;

        while (i <= mid && j <= right) {
            T a = list.get(i);
            T b = list.get(j);
            if (a.compareTo(b) <= 0) {
                temp.set(k++, a);
                i++;
            } else {
                temp.set(k++, b);
                j++;
            }
        }
        while (i <= mid) temp.set(k++, list.get(i++));
        while (j <= right) temp.set(k++, list.get(j++));
    }

    private static <T extends Comparable<? super T>> void insertionSortRange(
            List<T> list, int left, int right) {

        for (int i = left + 1; i <= right; i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= left && list.get(j).compareTo(key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    private static <T extends Comparable<? super T>> void quickSortRecursive(
            List<T> list, int left, int right, PivotChooser<T> chooser) {

        if (left >= right) return;

        int pIdx = chooser.choosePivotIndex(list, left, right);
        swap(list, pIdx, right);
        int p = partitionLomuto(list, left, right);
        quickSortRecursive(list, left, p - 1, chooser);
        quickSortRecursive(list, p + 1, right, chooser);
    }

    private static <T extends Comparable<? super T>> int partitionLomuto(
            List<T> list, int left, int right) {

        T pivot = list.get(right);
        int store = left;

        for (int i = left; i < right; i++) {
            if (list.get(i).compareTo(pivot) < 0) {
                swap(list, i, store);
                store++;
            }
        }
        swap(list, store, right);
        return store;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        if (i == j) return;
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }
}
