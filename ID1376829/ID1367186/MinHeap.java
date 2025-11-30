package ID1376829.ID1367186;
/**
 * A simple binary min-heap for {@link WordFrequency} objects.
 * <p>
 * This heap is 1-indexed internally (the root is at index 1) and orders
 * elements by {@link WordFrequency#importance} in ascending order:
 * the word with the smallest importance is at the root.
 * </p>
 *
 * <p>
 * It is used in the autocomplete system to efficiently maintain the
 * top-k most important words, by storing at most k elements and
 * evicting the current minimum when a more important word arrives.
 * </p>
 */
public class MinHeap {

    /**
     * Internal array storing heap elements.
     * <p>
     * This array is 1-based: index 1 is the root, index 2 and 3 are its children, etc.
     * Index 0 is unused.
     * </p>
     */
    private WordFrequency[] heap;

    /** Current number of elements in the heap. */
    private int size;

    /**
     * Creates a new {@code MinHeap} with the given initial capacity.
     *
     * @param capacity the initial maximum number of elements before resizing
     */
    public MinHeap(int capacity) {
        heap = new WordFrequency[capacity + 1]; // 1-based indexing
        size = 0;
    }

    /**
     * Returns whether the heap is empty.
     *
     * @return {@code true} if the heap contains no elements, otherwise {@code false}
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently stored in the heap.
     *
     * @return the current heap size
     */
    public int size() {
        return size;
    }

    /**
     * Returns (but does not remove) the minimum element of the heap.
     * <p>
     * If the heap is empty, {@code null} is returned.
     * </p>
     *
     * @return the root {@link WordFrequency}, or {@code null} if the heap is empty
     */
    public WordFrequency getMin() {
        if (isEmpty()) return null;
        return heap[1];
    }

    /**
     * Inserts a new {@link WordFrequency} into the heap.
     * <p>
     * If {@code item} is {@code null}, the call is ignored.
     * If the internal array is full, it is resized to double its previous length.
     * </p>
     *
     * @param item the word-frequency pair to insert
     */
    public void insert(WordFrequency item) {
        if (item == null) return;

        // If heap is full, double its size
        if (size == heap.length - 1) {
            resize(heap.length * 2);
        }

        // Insert at the end
        size++;
        heap[size] = item;

        // Restore heap order upwards
        swim(size);
    }

    /**
     * Removes and returns the minimum element of the heap.
     * <p>
     * If the heap is empty, {@code null} is returned.
     * </p>
     *
     * @return the minimum {@link WordFrequency} or {@code null} if the heap is empty
     */
    public WordFrequency removeMin() {
        if (isEmpty()) return null;

        WordFrequency min = heap[1];
        swap(1, size);
        heap[size] = null;
        size--;

        // Restore heap order downwards
        sink(1);

        return min;
    }

    /**
     * Returns a copy of the heap contents as an array.
     * <p>
     * The returned array contains elements from index 0 to {@code size - 1},
     * corresponding to heap positions 1..size. The order is the internal
     * heap order, <strong>not</strong> sorted by importance.
     * </p>
     *
     * @return an array containing all elements currently in the heap
     */
    public WordFrequency[] toArray() {
        WordFrequency[] result = new WordFrequency[size];
        for (int i = 1; i <= size; i++) {
            result[i - 1] = heap[i];
        }
        return result;
    }

    // ---------- private helpers ----------

    /**
     * Resizes the internal array to the given new capacity.
     *
     * @param newCapacity the new array length
     */
    private void resize(int newCapacity) {
        WordFrequency[] newHeap = new WordFrequency[newCapacity];
        for (int i = 1; i <= size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }

    /**
     * Moves the element at index {@code k} up the heap while it is
     * smaller than its parent, restoring the min-heap property.
     *
     * @param k the index of the element to move up
     */
    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            swap(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * Moves the element at index {@code k} down the heap while it is
     * larger than its smaller child, restoring the min-heap property.
     *
     * @param k the index of the element to move down
     */
    private void sink(int k) {
        while (2 * k <= size) {
            int j = 2 * k; // left child
            if (j < size && greater(j, j + 1)) {
                j++; // right child is smaller
            }
            if (!greater(k, j)) break;
            swap(k, j);
            k = j;
        }
    }

    /**
     * Returns {@code true} if {@code heap[i]} is considered greater than {@code heap[j]}
     * according to importance.
     * <p>
     * This method defines the ordering for the min-heap:
     * a smaller importance value means a "smaller" element.
     * </p>
     *
     * @param i index of first element
     * @param j index of second element
     * @return {@code true} if {@code heap[i].importance > heap[j].importance}, otherwise {@code false}
     */
    private boolean greater(int i, int j) {
        if (heap[i] == null || heap[j] == null) return false;

        // smaller importance = "smaller" in heap
        return heap[i].importance > heap[j].importance;
    }

    /**
     * Swaps the elements at indices {@code i} and {@code j} in the heap array.
     *
     * @param i first index
     * @param j second index
     */
    private void swap(int i, int j) {
        WordFrequency temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}
