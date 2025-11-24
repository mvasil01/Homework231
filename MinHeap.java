public class MinHeap {
    private WordFrequency[] heap;
    private int size;

    public MinHeap(int capacity){
        heap = new WordFrequency[capacity + 1];
        size = 0;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public WordFrequency getMin(){
        if(isEmpty()) return null;
        return heap[1];
    }

    public void insert(WordFrequency item) {
        if (item == null) return;

        // If heap is full, double its size
        if (size == heap.length - 1) {
            resize(heap.length * 2);
        }

        // Insert at the end
        size++;
        heap[size] = item;

        // Swim up
        swim(size);
    }

    public WordFrequency removeMin() {
        if (isEmpty()) return null;

        WordFrequency min = heap[1];
        swap(1, size);
        heap[size] = null;
        size--;

        // Sink down
        sink(1);

        return min;
    }

    // For later use: get all elements in an array (positions 1..size)
    public WordFrequency[] toArray() {
        WordFrequency[] result = new WordFrequency[size];
        for (int i = 1; i <= size; i++) {
            result[i - 1] = heap[i];
        }
        return result;
    }

    // ---------- private helpers ----------

    private void resize(int newCapacity) {
        WordFrequency[] newHeap = new WordFrequency[newCapacity];
        for (int i = 1; i <= size; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }

    // move element at index k up while it's smaller than its parent
    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            swap(k, k / 2);
            k = k / 2;
        }
    }

    // move element at index k down while it's larger than its children
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

    // return true if heap[i] > heap[j] (we want min-heap)
    private boolean greater(int i, int j) {
        if (heap[i] == null || heap[j] == null) return false;

        // smaller importance = "smaller" in heap
        return heap[i].importance > heap[j].importance;
    }

    private void swap(int i, int j) {
        WordFrequency temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public static void main(String[] args) {
        MinHeap heap = new MinHeap(4);

        heap.insert(new WordFrequency("apple", 50));
        heap.insert(new WordFrequency("banana", 20));
        heap.insert(new WordFrequency("cat", 5));
        heap.insert(new WordFrequency("dog", 30));

        System.out.println("Heap size: " + heap.size());
        System.out.println("Min: " + heap.getMin()); // should be cat (5)

        System.out.println("Removing elements in order:");
        while (!heap.isEmpty()) {
            System.out.println(heap.removeMin());
        }
    }
}
