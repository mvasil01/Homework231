/**
 * A node in the {@link CompressedTrie}.
 * <p>
 * Each node stores:
 * <ul>
 *   <li>A {@link RobinHoodHashing} table of outgoing {@link Edge} objects.</li>
 *   <li>A flag {@link #isEndOfWord} indicating whether this node terminates a word.</li>
 *   <li>An integer {@link #importance} which counts how many times the word
 *       represented by this node has appeared in the training text.</li>
 * </ul>
 * </p>
 */
public class CompressedTrieNode {

    /**
     * Hash table of outgoing edges from this node.
     * Each edge label represents a compressed path segment to a child node.
     */
    private RobinHoodHashing edgeList;

    /**
     * Indicates whether this node corresponds to the end of a valid word.
     */
    public boolean isEndOfWord;

    /**
     * The importance/frequency counter for the word that ends at this node.
     * This is typically incremented when the word appears in the training text.
     */
    public int importance;

    /**
     * Constructs a new {@code CompressedTrieNode} with an empty edge list
     * and {@link #isEndOfWord} set to {@code false}.
     */
    public CompressedTrieNode() {
        edgeList = new RobinHoodHashing();
        isEndOfWord = false;
        importance = 0;
    }

    /**
     * Inserts an outgoing edge from this node.
     * <p>
     * The edge is stored in the underlying {@link RobinHoodHashing} table.
     * If the {@code edge} argument is {@code null}, a {@link RuntimeException}
     * is thrown.
     * </p>
     *
     * @param edge the edge to insert (must not be {@code null})
     * @throws RuntimeException if {@code edge} is {@code null}
     */
    public void insertEdge(Edge edge) {
        if (edge == null) {
            throw new RuntimeException("The edge given to insert is null");
        }
        edgeList.insert(edge);
    }

    /**
     * Returns the outgoing edge whose label starts with the given character,
     * or {@code null} if no such edge exists.
     * <p>
     * This is a convenience method used by the trie logic to select the next
     * edge based on the first character of the remaining word segment.
     * </p>
     *
     * @param c the first character of the desired edge label
     * @return the matching {@link Edge}, or {@code null} if none is found
     */
    public Edge getEdgeByFirstChar(char c) {
        return edgeList.getEdge(c);
    }

    /**
     * Returns all outgoing edges from this node as an array.
     * <p>
     * The returned array is the internal table from {@link RobinHoodHashing},
     * so it may contain {@code null} entries or unoccupied slots. Callers
     * should check for {@code null} and {@code e.occupied} when iterating.
     * </p>
     *
     * @return the internal {@link Edge} array, possibly containing {@code null}s
     */
    public Edge[] getAllEdges() {
        return edgeList.getTable();
    }
}
