package ID1376829.ID1367186;
/**
 * A Robin Hood hashing table used to store outgoing edges of a compressed trie node.
 * <p>
 * Keys are {@link Edge} objects, where the hash is computed from the first character
 * of the edge's label. Collisions are resolved using linear probing with
 * the Robin Hood strategy: when inserting, entries with higher probe length
 * may "steal" the slot from entries with a lower probe length.
 * </p>
 *
 * <p>
 * The table grows over a small array of prime capacities to keep probe lengths
 * relatively small and to preserve good distribution. This implementation is
 * tailored for the compressed trie use-case, where each node usually has a small
 * number of children.
 * </p>
 */
public class RobinHoodHashing {

    /** The underlying hash table storing edges. */
    private Edge[] hashTable;

    /** Current capacity (length) of the hash table array. */
    private int capacity;

    /** Number of occupied slots in the table. */
    private int size;

    /**
     * Maximum probe length observed so far during insertions.
     * This is used as an upper bound during search.
     */
    private int maxProbeLength;

    /** Sequence of prime capacities used for rehashing growth. */
    private static final int[] PRIMES = {3, 7, 11, 17, 23, 29};

    /**
     * Constructs a new {@code RobinHoodHashing} instance with the initial
     * capacity taken from the first element of {@link #PRIMES}.
     */
    public RobinHoodHashing() {
        capacity = PRIMES[0];
        hashTable = new Edge[capacity];
        size = 0;
        maxProbeLength = 0;
    }

    /**
     * Computes a hash index for the given label.
     * <p>
     * Only the first character of the label is used. The caller is expected
     * to pass a <strong>lowercase</strong> string, as this method assumes
     * characters are in the range {@code 'a'..'z'}.
     * </p>
     *
     * @param label the edge label (should be non-null and non-empty)
     * @return an index in the range {@code [0, capacity)}
     */
    private int hashFunction(String label) {
        return (label.charAt(0) - 'a') % capacity;
    }

    /**
     * Inserts the given edge into the hash table, growing the table if necessary.
     * <p>
     * If {@code edge} is {@code null} or its label is {@code null}, the call
     * is ignored. If an edge with the same label already exists in the table,
     * the insertion is also ignored (no duplicates).
     * </p>
     *
     * @param edge the edge to insert
     */
    public void insert(Edge edge) {
        if (edge == null || edge.label == null) {
            return;
        }

        // Grow if load factor would exceed 0.9 after this insertion
        if ((size + 1.0) / capacity > 0.9) {
            rehash();
        }

        insertNoRehash(edge);
    }

    /**
     * Inserts the given edge into the table assuming no rehash is needed.
     * <p>
     * This method uses Robin Hood hashing:
     * <ul>
     *   <li>Compute the "home" bucket for the new edge.</li>
     *   <li>Probe linearly; if we find a slot with an element that has a
     *       smaller probe length, we swap and continue with the displaced element.</li>
     *   <li>Insertion stops when an empty or unoccupied slot is found.</li>
     * </ul>
     *
     * @param edge the edge to insert (non-null, with non-null label)
     */
    private void insertNoRehash(Edge edge) {
        // Labels in the trie are stored case-insensitively;
        // ensure we hash based on a lowercase label.
        int home = hashFunction(edge.label.toLowerCase());
        int idx = home;
        int probeNew = 0;

        while (true) {
            Edge current = hashTable[idx];

            // Empty slot or logically unoccupied (deleted) slot
            if (current == null || !current.occupied) {
                edge.occupied = true;
                hashTable[idx] = edge;
                size++;
                if (probeNew > maxProbeLength) {
                    maxProbeLength = probeNew;
                }
                return;
            }

            // Duplicate label → do nothing
            if (current.label.equals(edge.label)) {
                return;
            }

            // Compute the probe length of the current occupant
            int homeCurrent = hashFunction(current.label.toLowerCase());
            int probeCurrent = (idx - homeCurrent + capacity) % capacity;

            // Robin Hood swap: if new edge has probed further, steal this slot
            if (probeNew > probeCurrent) {
                Edge temp = hashTable[idx];
                hashTable[idx] = edge;
                edge = temp;
                probeNew = probeCurrent;
            }

            // Move to next slot
            idx = (idx + 1) % capacity;
            probeNew++;
        }
    }

    /**
     * Searches for an edge with the given label in the table.
     * <p>
     * The search uses linear probing but stops after {@code maxProbeLength}
     * steps from the computed home position. This is safe because
     * {@code maxProbeLength} stores the longest probe used so far in insertions.
     * </p>
     *
     * @param label the label to search for (case-insensitive)
     * @return {@code true} if an edge with the given label is found, otherwise {@code false}
     */
    public boolean search(String label) {
        if (label == null || label.isEmpty()) {
            return false;
        }

        String normalized = label.toLowerCase();
        int pos = hashFunction(normalized);
        int index;

        for (int offset = 0; offset <= maxProbeLength; offset++) {
            index = (pos + offset) % capacity;
            Edge e = hashTable[index];

            if (e == null) {
                // Cannot have further matches beyond this empty slot
                return false;
            }

            if (e.occupied && e.label.equals(normalized)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the edge whose label starts with the given character.
     * <p>
     * This is a convenience method used by the compressed trie to find an
     * outgoing edge based on the first character of the remaining word.
     * It performs a linear scan over the table and returns the first match.
     * </p>
     *
     * @param c the first character of the desired label
     * @return the matching {@link Edge}, or {@code null} if none exists
     */
    public Edge getEdge(char c) {
        for (int i = 0; i < capacity; i++) {
            if (hashTable[i] != null
                    && hashTable[i].occupied
                    && !hashTable[i].label.isEmpty()
                    && hashTable[i].label.charAt(0) == c) {
                return hashTable[i];
            }
        }
        return null;
    }

    /**
     * Rehashes the table into the next larger prime capacity, if available.
     * <p>
     * All currently occupied edges are re-inserted using {@link #insertNoRehash(Edge)}.
     * If the table already uses the largest prime in {@link #PRIMES}, no
     * rehash is performed.
     * </p>
     */
    private void rehash() {
        // Find current capacity index in PRIMES
        int index = 0;
        while (index < PRIMES.length && PRIMES[index] != capacity) {
            index++;
        }

        // No larger prime available
        if (index + 1 >= PRIMES.length) {
            return;
        }

        int newCapacity = PRIMES[index + 1];
        Edge[] oldTable = hashTable;

        hashTable = new Edge[newCapacity];
        capacity = newCapacity;
        size = 0;
        maxProbeLength = 0;

        for (Edge e : oldTable) {
            if (e != null && e.occupied) {
                insertNoRehash(e);
            }
        }
    }

    /**
     * Returns the underlying hash table array.
     * <p>
     * This is mainly intended for debugging, testing or size estimation;
     * callers should not modify the returned array directly.
     * </p>
     *
     * @return the internal {@link Edge} array
     */
    public Edge[] getTable() {
        return hashTable;
    }

    /**
     * Estimates the memory footprint of this hash table instance in bytes.
     * <p>
     * The calculation is approximate and based on typical 64-bit JVM object
     * layout assumptions (headers, references, primitive sizes). It includes:
     * </p>
     * <ul>
     *   <li>The {@code RobinHoodHashing} object itself</li>
     *   <li>The {@code PRIMES} array</li>
     *   <li>The hash table array</li>
     *   <li>Each non-null {@link Edge} object and its label string</li>
     * </ul>
     *
     * @return approximate memory usage in bytes
     */
    public long estimateMemory() {
        // 1. RobinHoodHashing object: header (16) + refs/ints (approx 32)
        long size = 48;

        // 2. PRIMES array: header (16) + 6 ints (24)
        size += 40;

        // 3. Hash table array: header (16) + capacity * reference (8)
        size += 16 + (capacity * 8L);

        // 4. Edges (only occupied ones)
        for (Edge e : hashTable) {
            if (e != null && e.occupied) {
                // Edge object: header(16) + 2 refs(16) + boolean(1)
                size += 33;

                // String label: header(16) + value ref(8) + hash(4) = 28
                // char[]: header(16) + length * 2 bytes
                if (e.label != null) {
                    size += 28 + 16 + (e.label.length() * 2L);
                }
            }
        }

        return size;
    }
}
