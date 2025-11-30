package ID1376829.ID1367186;
public class HeapSort {

    public static void sort(WordFrequency[] arr) {
        int n = arr.length;

        // Build max-heap
        for (int i = n/2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Extract elements one by one
        for (int i = n-1; i >= 0; i--) {
            swap(arr, 0, i);     
            heapify(arr, i, 0);  
        }

        // Reverse to get descending importance
        reverse(arr);
    }

    private static void heapify(WordFrequency[] arr, int heapSize, int rootIndex) {
        int largest = rootIndex;
        int left = 2 * rootIndex + 1;
        int right = 2 * rootIndex + 2;

        if (left < heapSize && greater(arr[left], arr[largest])) {
            largest = left;
        }

        if (right < heapSize && greater(arr[right], arr[largest])) {
            largest = right;
        }

        if (largest != rootIndex) {
            swap(arr, rootIndex, largest);
            heapify(arr, heapSize, largest);
        }
    }

    // return true if a > b based on rules
    private static boolean greater(WordFrequency a, WordFrequency b) {
        if (a.importance > b.importance) return true;
        if (a.importance < b.importance) return false;
        return a.word.compareTo(b.word) < 0;
    }

    private static void swap(WordFrequency[] arr, int i, int j) {
        WordFrequency temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static void reverse(WordFrequency[] arr) {
        int i = 0, j = arr.length - 1;
        while (i < j) {
            swap(arr, i, j);
            i++;
            j--;
        }
    }
}
