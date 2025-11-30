package ID1376829.ID1367186;
/**
 * A compressed trie (radix tree) used for efficient storage and lookup
 * of words, along with importance (frequency) information.
 * <p>
 * Each edge in the trie carries a string label rather than a single character,
 * and nodes can represent words (via {@code isEndOfWord}) and store an
 * importance counter. Outgoing edges of each node are stored in a
 * {@link RobinHoodHashing} table inside {@link CompressedTrieNode}.
 * </p>
 *
 * <p>
 * This implementation supports:
 * <ul>
 *   <li>Insertion of words (case-insensitive).</li>
 *   <li>Exact word search.</li>
 *   <li>Retrieval of the node corresponding to a word/prefix.</li>
 *   <li>Top-k suggestions for a prefix (using {@link MinHeap} + {@link HeapSort}).</li>
 *   <li>Average frequency computation for a prefix subtree.</li>
 *   <li>Next-letter prediction based on subtree averages.</li>
 *   <li>Approximate memory estimation for analysis purposes.</li>
 * </ul>
 * </p>
 */
public class CompressedTrie {

    /** Root node of the compressed trie (represents the empty prefix). */
    CompressedTrieNode root;

    /**
     * Constructs an empty {@code CompressedTrie} with a single root node.
     */
    public CompressedTrie() {
        root = new CompressedTrieNode();
    }

    /**
     * Inserts a word into the trie (case-insensitive).
     * <p>
     * If {@code word} is {@code null}, the call is ignored.
     * Internally the word is converted to lowercase.
     * </p>
     *
     * @param word the word to insert
     */
    public void insert(String word) {
        if (word == null) {
            return;
        }
        insertHelper(root, word.toLowerCase());
    }

    /**
     * Recursive helper for insertion.
     * Handles the usual compressed trie cases:
     * <ul>
     *   <li>No outgoing edge with the first char: create a new edge.</li>
     *   <li>Existing edge label fully matches the word.</li>
     *   <li>Existing edge label is a prefix of the word (go deeper).</li>
     *   <li>Word is a prefix of the label (split edge, create mid-node).</li>
     *   <li>Label and word share a common prefix then diverge (split into 2 branches).</li>
     * </ul>
     *
     * @param current the current trie node
     * @param word    the remaining word to insert, in lowercase
     */
    private void insertHelper(CompressedTrieNode current, String word) {

        if (word.isEmpty()) {
            current.isEndOfWord = true;
            return;
        }

        Edge edge = current.getEdgeByFirstChar(word.charAt(0));

        // Case 1: no edge starting with this character
        if (edge == null) {
            CompressedTrieNode child = new CompressedTrieNode();
            child.isEndOfWord = true;
            current.insertEdge(new Edge(word, child));
            return;
        }

        String label = edge.label;
        int prefixLength = commonPrefixLength(label, word);

        // Case 2: label == word
        if (prefixLength == label.length() && prefixLength == word.length()) {
            edge.child.isEndOfWord = true;
            return;
        }

        // Case 3: label is prefix of the word -> go deeper
        if (prefixLength == label.length() && prefixLength < word.length()) {
            insertHelper(edge.child, word.substring(prefixLength));
            return;
        }

        // Case 4: word is prefix of label -> split edge, mid becomes word
        if (prefixLength == word.length() && prefixLength < label.length()) {
            CompressedTrieNode oldChild = edge.child;
            CompressedTrieNode mid = new CompressedTrieNode();
            mid.isEndOfWord = true;

            String remainder = label.substring(prefixLength);

            edge.label = word;     // shorten edge label to the word
            edge.child = mid;      // mid becomes child
            mid.insertEdge(new Edge(remainder, oldChild));
            return;
        }

        // Case 5: word and label diverge after common prefix -> split into 2 branches
        if (prefixLength < label.length() && prefixLength < word.length()) {
            CompressedTrieNode oldChild = edge.child;
            CompressedTrieNode mid = new CompressedTrieNode();

            String prefix = label.substring(0, prefixLength);
            String remainderLabel = label.substring(prefixLength);
            String remainderWord = word.substring(prefixLength);

            edge.label = prefix;
            edge.child = mid;

            // Old label branch
            mid.insertEdge(new Edge(remainderLabel, oldChild));

            // New word branch
            CompressedTrieNode newChild = new CompressedTrieNode();
            newChild.isEndOfWord = true;
            mid.insertEdge(new Edge(remainderWord, newChild));
        }
    }

    /**
     * Returns the length of the longest common prefix of the two strings.
     *
     * @param a first string
     * @param b second string
     * @return length of their common prefix
     */
    private static int commonPrefixLength(String a, String b) {
        int n = Math.min(a.length(), b.length());
        int cnt = 0;
        while (cnt < n && a.charAt(cnt) == b.charAt(cnt)) {
            cnt++;
        }
        return cnt;
    }

    /**
     * Checks whether a given word exists in the trie (case-insensitive).
     *
     * @param word the word to search for
     * @return {@code true} if the word is present as a complete word,
     *         {@code false} otherwise
     */
    public boolean search(String word) {
        if (word == null) {
            return false;
        }
        return searchHelper(root, word.toLowerCase());
    }

    /**
     * Recursive helper for {@link #search(String)}.
     *
     * @param current the current node
     * @param word    remaining part of the word to search (lowercase)
     * @return {@code true} if the word is found, {@code false} otherwise
     */
    public boolean searchHelper(CompressedTrieNode current, String word) {

        if (word.isEmpty()) {
            return current.isEndOfWord;
        }

        Edge edge = current.getEdgeByFirstChar(word.charAt(0));

        // word doesn't exist
        if (edge == null) {
            return false;
        }

        String label = edge.label;
        int prefixLength = commonPrefixLength(label, word);

        // The word == label
        if (prefixLength == label.length() && prefixLength == word.length()) {
            return edge.child.isEndOfWord;
        }

        // The label is a PREFIX of the word → go deeper
        if (prefixLength == label.length() && prefixLength < word.length()) {
            return searchHelper(edge.child, word.substring(prefixLength));
        }

        // Any other case -> not a complete word
        return false;
    }

    /**
     * Returns the trie node corresponding to the given word or prefix.
     * <p>
     * This method does not require that the given word is an actual stored word;
     * it can return nodes for prefixes that lie inside an edge label.
     * </p>
     *
     * @param word the word or prefix (case-insensitive)
     * @return the {@link CompressedTrieNode} representing that prefix, or {@code null}
     *         if the path does not exist
     */
    public CompressedTrieNode getNode(String word) {
        if (word == null) {
            return null;
        }
        return getNodeHelper(root, word.toLowerCase());
    }

    /**
     * Recursive helper that returns the node corresponding to a prefix.
     *
     * @param current current node
     * @param word    remaining prefix
     * @return node representing the prefix, or {@code null} if no path exists
     */
    private CompressedTrieNode getNodeHelper(CompressedTrieNode current, String word) {
        if (word.isEmpty()) {
            // Return the node even if it's not end-of-word (we just want the prefix node)
            return current;
        }

        Edge edge = current.getEdgeByFirstChar(word.charAt(0));
        if (edge == null) {
            return null;
        }

        String label = edge.label;
        int prefixLength = commonPrefixLength(label, word);

        // The prefix exactly matches this edge label and consumes the whole word
        if (prefixLength == label.length() && prefixLength == word.length()) {
            // Return the child node regardless of isEndOfWord
            return edge.child;
        }

        // The label is a full prefix of the remaining word -> keep going down
        if (prefixLength == label.length() && prefixLength < word.length()) {
            return getNodeHelper(edge.child, word.substring(prefixLength));
        }

        if (prefixLength == word.length() && prefixLength < label.length()) {
            // The prefix node is INSIDE the edge.
            // According to compressed trie rules:
            // it corresponds to the child node of this edge
            return edge.child;
        }

        // Otherwise, the path for this prefix doesn't exist
        return null;
    }

    /**
     * Recursively traverses the subtree rooted at {@code node}, collecting
     * the top-k words into a {@link MinHeap}.
     * <p>
     * Each time we reach a node that is end-of-word, we consider inserting its
     * ({@code prefix}, importance) into the heap, possibly ejecting the current minimum.
     * </p>
     *
     * @param node   current trie node
     * @param prefix the word built so far on the path to this node
     * @param heap   min-heap of {@link WordFrequency} of size up to {@code k}
     * @param k      maximum number of suggestions to keep
     */
    private void collectTopK(CompressedTrieNode node, String prefix, MinHeap heap, int k) {
        if (node == null) return;

        if (node.isEndOfWord) {
            int imp = node.importance;
            WordFrequency wf = new WordFrequency(prefix, imp);

            if (heap.size() < k) {
                heap.insert(wf);
            } else {
                WordFrequency min = heap.getMin();
                if (min != null && imp > min.importance) {
                    heap.removeMin();
                    heap.insert(wf);
                }
            }
        }

        Edge[] edges = node.getAllEdges();
        if (edges == null) return;

        for (Edge e : edges) {
            if (e != null && e.occupied) {
                String newPrefix = prefix + e.label;
                collectTopK(e.child, newPrefix, heap, k);
            }
        }
    }

    /**
     * Returns the top-k most important words starting with the given prefix.
     * <p>
     * The result is sorted in descending order by importance (frequency).

     * If {@code k <= 0} or no words match the prefix, an empty array is returned.
     * </p>
     *
     * @param prefix the prefix to search under (case-insensitive)
     * @param k      maximum number of suggestions to return
     * @return an array of {@link WordFrequency} sorted by importance descending
     */
    public WordFrequency[] getTopK(String prefix, int k) {
        if (k <= 0) return new WordFrequency[0];

        prefix = prefix.toLowerCase();
        CompressedTrieNode node = getNode(prefix);
        if (node == null) return new WordFrequency[0];

        // Reconstruct the full word represented by this node (handles inside-edge prefixes)
        String realPrefix = reconstructPath(node);

        MinHeap heap = new MinHeap(k);
        collectTopK(node, realPrefix, heap, k);

        WordFrequency[] result = heap.toArray();
        HeapSort.sort(result);
        return result;
    }

    /**
     * Reconstructs the full string (word/prefix) represented by {@code target}
     * by walking from the root and concatenating edge labels.
     *
     * @param target node whose prefix we want to reconstruct
     * @return the string representing the path to {@code target},
     *         or {@code null} if {@code target} is not reachable from the root
     */
    private String reconstructPath(CompressedTrieNode target) {
        return reconstructPathHelper(root, "", target);
    }

    /**
     * Recursive helper for {@link #reconstructPath(CompressedTrieNode)}.
     *
     * @param current current node in DFS
     * @param prefix  word built so far
     * @param target  target node
     * @return prefix for target, or {@code null} if not found
     */
    private String reconstructPathHelper(CompressedTrieNode current, String prefix, CompressedTrieNode target) {

        if (current == target)
            return prefix;

        Edge[] edges = current.getAllEdges();
        if (edges == null) return null;

        for (Edge e : edges) {
            if (e != null && e.occupied) {
                String newPrefix = prefix + e.label;
                String result = reconstructPathHelper(e.child, newPrefix, target);
                if (result != null) return result;
            }
        }

        return null;
    }

    /**
     * Computes the average importance of all words in the subtree rooted at
     * {@code startNode}.
     * <p>
     * This uses an explicit stack ({@link hybridStack}) to perform DFS and avoid
     * recursion limits. Each end-of-word node contributes its {@code importance}
     * to the total.
     * </p>
     *
     * @param startNode root of the subtree
     * @return average importance value, or 0.0 if there are no words in the subtree
     */
    private double getSubtreeAverage(CompressedTrieNode startNode) {
        if (startNode == null) return 0.0;

        long totalImp = 0;
        int count = 0;

        // DFS using the custom stack
        hybridStack stack = new hybridStack(50);
        stack.push(startNode, "");

        while (!stack.isEmpty()) {
            try {
                CompressedTrieNode curr = stack.topNode();
                stack.pop();

                if (curr.isEndOfWord) {
                    totalImp += curr.importance;
                    count++;
                }

                Edge[] edges = curr.getAllEdges();
                if (edges != null) {
                    for (Edge e : edges) {
                        if (e != null && e.occupied) {
                            stack.push(e.child, "");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to pop from the stack! ");
            }
        }

        if (count == 0) return 0.0;
        return (double) totalImp / count;
    }

    /**
     * Computes the average frequency (importance) of all words that start with
     * the given prefix.
     * <p>
     * If the prefix does not exist in the trie, the method returns 0.0. If
     * {@code prefix} is {@code null}, it returns -1 as an error code.
     * </p>
     *
     * @param prefix the prefix (case-insensitive)
     * @return average importance of words under that prefix,
     *         or 0.0 if no words match, or -1 if {@code prefix} is null
     */
    public double getAverageFrequency(String prefix) {
        if (prefix == null) return -1;
        prefix = prefix.toLowerCase();
        CompressedTrieNode node = getNode(prefix);
        if (node == null) return 0.0;
        return getSubtreeAverage(node);
    }

    /**
     * Predicts the most likely next character after the given prefix.
     * <p>
     * The next character is chosen as the one whose corresponding child subtree
     * has the highest average importance. If the prefix does not exist or no
     * child subtrees are available, {@code '\0'} is returned.
     * </p>
     *
     * @param prefix the prefix (case-insensitive)
     * @return the predicted next character, or {@code '\0'} if none can be predicted
     */
    public char predictNextLetter(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return '\0';
        }

        return predictNextLetterHelper(root, prefix.toLowerCase());
    }

    /**
     * Recursive helper for {@link #predictNextLetter(String)}.
     *
     * @param current   current node
     * @param remaining the remaining part of the prefix to match
     * @return predicted next character or {@code '\0'} if none
     */
    private char predictNextLetterHelper(CompressedTrieNode current, String remaining) {
        if (remaining.isEmpty()) {
            // We are exactly at the node for the prefix.
            Edge[] children = current.getAllEdges();
            if (children == null) {
                return '\0';
            }

            char bestChar = '\0';
            double bestAvg = -1.0;

            for (Edge e : children) {
                if (e != null && e.occupied) {
                    double subAvg = getSubtreeAverage(e.child);
                    char nextChar = e.label.charAt(0);

                    if (subAvg > bestAvg) {
                        bestAvg = subAvg;
                        bestChar = nextChar;
                    }
                }
            }

            return bestChar;
        }

        char c = remaining.charAt(0);
        Edge edge = current.getEdgeByFirstChar(c);
        if (edge == null) {
            return '\0';
        }

        String label = edge.label;
        int common = commonPrefixLength(label, remaining);

        if (common == 0) {
            return '\0';
        }

        // Case 1: label is a full prefix of remaining -> go deeper
        if (common == label.length() && common < remaining.length()) {
            String rest = remaining.substring(common);
            return predictNextLetterHelper(edge.child, rest);
        }

        // Case 2: prefix ends inside label (remaining shorter than label, but matches)
        if (common == remaining.length() && common < label.length()) {
            // Next character after the prefix is just the next char in the label
            return label.charAt(common);
        }

        // Case 3: prefix exactly matches label -> we are at edge.child node
        if (common == remaining.length() && common == label.length()) {
            return predictNextLetterHelper(edge.child, "");
        }

        // Any other case means no valid continuation for this prefix
        return '\0';
    }

    // ==========================================
    // ===  MEMORY ESTIMATION (Theoretical)   ===
    // ==========================================

    /**
     * Estimates the memory usage of the entire trie in bytes.
     * <p>
     * This is a theoretical approximation based on typical JVM object layout.
     * It includes:
     * <ul>
     *   <li>The {@code CompressedTrie} object itself</li>
     *   <li>All {@link CompressedTrieNode} objects</li>
     *   <li>Underlying Robin Hood hash tables and {@link Edge} objects</li>
     *   <li>Strings used as edge labels</li>
     * </ul>
     * </p>
     *
     * @return estimated memory usage in bytes
     */
    public long estimateMemory() {
        long size = 16 + 8; // CompressedTrie Object(16) + root Ref(8)
        if (root != null) {
            size += measureNode(root);
        }
        return size;
    }

    /**
     * Recursively measures the memory usage of a single node and its descendants.
     *
     * @param node node to measure
     * @return memory usage in bytes
     */
    private long measureNode(CompressedTrieNode node) {
        // 1. Node Object: Header(16) + edgeList Ref(8) + boolean(1) + int(4)
        long size = 29;

        size += estimateRobinHood(node);

        // 3. Recursive Children (Traverse edges to find children)
        Edge[] edges = node.getAllEdges();
        if (edges != null) {
            for (Edge e : edges) {
                if (e != null && e.occupied && e.child != null) {
                    size += measureNode(e.child);
                }
            }
        }
        return size;
    }

    /**
     * Estimates the memory used by the Robin Hood hash table attached to a node.
     *
     * @param node node whose edge table we want to estimate
     * @return estimated size in bytes
     */
    private long estimateRobinHood(CompressedTrieNode node) {
        Edge[] table = node.getAllEdges(); // table.length == capacity
        int capacity = (table != null) ? table.length : 0;

        // RobinHood object (48) + PRIMES Array (40)
        long size = 48 + 40;

        // HashTable Array
        size += 16 + (capacity * 8L);

        // Edges
        if (table != null) {
            for (Edge e : table) {
                if (e != null) {
                    // Edge Object (33)
                    size += 33;
                    // String Label (28 + 16 + chars)
                    if (e.label != null) {
                        size += 44 + (e.label.length() * 2L);
                    }
                }
            }
        }
        return size;
    }

    // ======================================================
    // ================  TEST SUITE  ========================
    // ======================================================

    /**
     * Runs a full internal test suite for the compressed trie implementation.
     * <p>
     * This includes:
     * <ul>
     *   <li>Basic insert & search.</li>
     *   <li>Case insensitivity.</li>
     *   <li>Edge splitting scenarios.</li>
     *   <li>{@code getNode} behavior on words and prefixes.</li>
     *   <li>Importance tracking & average frequency.</li>
     *   <li>Top-k suggestions.</li>
     *   <li>Next-letter prediction.</li>
     * </ul>
     * </p>
     *
     * @param args ignored
     */
    public static void main(String[] args) {

        System.out.println("======= COMPRESSED TRIE FULL TEST SUITE =======\n");

        testBasicInsertSearch();
        testCaseInsensitivity();
        testEdgeSplitting();
        testGetNode();
        testImportanceAndAverage();
        testTopK();
        testPredictNextLetter();

        System.out.println("\n======= ALL TESTS FINISHED =======");
    }

    // ---------------------------------------------------------
    // Test 1: Basic insertion and search (PDF-like example)
    // ---------------------------------------------------------
    private static void testBasicInsertSearch() {
        System.out.println("=== Test 1: Basic Insert & Search ===");
        CompressedTrie trie = new CompressedTrie();

        String[] words = {"bear", "bell", "bid", "bull", "stock", "stop", "be"};
        for (String w : words) trie.insert(w);

        // Should be found
        check(trie.search("bear"), true,  "search(bear)");
        check(trie.search("bell"), true,  "search(bell)");
        check(trie.search("bid"), true,   "search(bid)");
        check(trie.search("bull"), true,  "search(bull)");
        check(trie.search("stock"), true, "search(stock)");
        check(trie.search("stop"), true,  "search(stop)");
        check(trie.search("be"), true,    "search(be)");

        // Should NOT be found
        check(trie.search("b"),      false, "search(b)");
        check(trie.search("bel"),    false, "search(bel)");
        check(trie.search("sto"),    false, "search(sto)");
        check(trie.search("bears"),  false, "search(bears)");
        check(trie.search("belli"),  false, "search(belli)");
        check(trie.search("billing"),false, "search(billing)");

        System.out.println();
    }

    // ---------------------------------------------------------
    // Test 2: Case insensitivity
    // ---------------------------------------------------------
    private static void testCaseInsensitivity() {
        System.out.println("=== Test 2: Case Insensitivity ===");
        CompressedTrie trie = new CompressedTrie();

        trie.insert("JaVa");
        check(trie.search("java"), true,  "search(java)");
        check(trie.search("JAVA"), true,  "search(JAVA)");
        check(trie.search("jAvA"), true,  "search(jAvA)");
        check(trie.search("python"), false, "search(python)");

        System.out.println();
    }

    // ---------------------------------------------------------
    // Test 3: Edge splitting (carton, car, cart, cat)
    // ---------------------------------------------------------
    private static void testEdgeSplitting() {
        System.out.println("=== Test 3: Edge Splitting ===");
        CompressedTrie trie = new CompressedTrie();

        trie.insert("carton");
        trie.insert("car");
        trie.insert("cart");
        trie.insert("cat");

        // should exist
        check(trie.search("car"),    true, "search(car)");
        check(trie.search("cart"),   true, "search(cart)");
        check(trie.search("carton"), true, "search(carton)");
        check(trie.search("cat"),    true, "search(cat)");

        // should NOT exist
        check(trie.search("ca"),     false, "search(ca)");
        check(trie.search("carts"),  false, "search(carts)");
        check(trie.search("carbon"), false, "search(carbon)");

        System.out.println();
    }

    // ---------------------------------------------------------
    // Test 4: getNode(word / prefix) behavior
    // ---------------------------------------------------------
    private static void testGetNode() {
        System.out.println("=== Test 4: getNode(word / prefix) ===");
        CompressedTrie trie = new CompressedTrie();

        trie.insert("apple");
        trie.insert("application");
        trie.insert("banana");

        // Full word
        CompressedTrieNode appleNode = trie.getNode("apple");
        check(appleNode != null && appleNode.isEndOfWord,
              true, "getNode(apple) != null && endOfWord");

        // Prefix that ends INSIDE an edge: "ap" (prefix of "apple","application")
        CompressedTrieNode apNode = trie.getNode("ap");
        check(apNode != null,
              true, "getNode(ap) != null (prefix inside label)");

        // Prefix of another branch: "ban" (prefix of "banana")
        CompressedTrieNode banNode = trie.getNode("ban");
        check(banNode != null,
              true, "getNode(ban) != null (prefix of banana)");

        // Non-existing prefix
        CompressedTrieNode xyzNode = trie.getNode("xyz");
        check(xyzNode == null,
              true, "getNode(xyz) == null");

        System.out.println();
    }

    // ---------------------------------------------------------
    // Test 5: Importance & getAverageFrequency(prefix)
    // ---------------------------------------------------------
    private static void testImportanceAndAverage() {
        System.out.println("=== Test 5: Importance & Average Frequency ===");
        CompressedTrie trie = new CompressedTrie();

        // words under 'app' prefix
        trie.insert("apple");
        trie.insert("application");

        // words under 'ban' prefix
        trie.insert("banana");
        trie.insert("band");

        // Simulate frequencies:
        // apple: 3, application: 1, banana: 2, band: 0
        increment(trie, "apple", 3);
        increment(trie, "application", 1);
        increment(trie, "banana", 2);

        // average under "app" = (3 + 1) / 2 = 2.0
        double avgApp = trie.getAverageFrequency("app");
        checkDouble(avgApp, 2.0, 1e-6, "avgFreq(app)");

        // average under "ban" = (2 + 0) / 2 = 1.0
        double avgBan = trie.getAverageFrequency("ban");
        checkDouble(avgBan, 1.0, 1e-6, "avgFreq(ban)");

        // No words under "xyz" -> 0.0
        double avgXyz = trie.getAverageFrequency("xyz");
        checkDouble(avgXyz, 0.0, 1e-6, "avgFreq(xyz)");

        System.out.println();
    }

    // ---------------------------------------------------------
    // Test 6: getTopK(prefix, k) using MinHeap + HeapSort
    // ---------------------------------------------------------
    private static void testTopK() {
        System.out.println("=== Test 6: getTopK(prefix, k) ===");
        CompressedTrie trie = new CompressedTrie();

        trie.insert("apple");
        trie.insert("application");
        trie.insert("appetite");
        trie.insert("banana");
        trie.insert("band");

        System.out.println("excpected true " + trie.search("band"));
        System.out.println("excpected true " + trie.search("banana"));

        // Set some frequencies:
        // apple: 5, application: 2, appetite: 7, banana: 1, band: 3
        increment(trie, "apple", 5);
        increment(trie, "application", 2);
        increment(trie, "appetite", 7);
        increment(trie, "banana", 1);
        increment(trie, "band", 3);

        // Top 2 for "app" should be: appetite (7), apple (5)
        WordFrequency[] topApp2 = trie.getTopK("app", 2);
        System.out.println("Top 2 for prefix 'app':");
        for (WordFrequency wf : topApp2) {
            System.out.println("  " + wf.word + " (" + wf.importance + ")");
        }

        check(topApp2.length == 2, true, "topApp2 length == 2");
        if (topApp2.length == 2) {
            check(topApp2[0].word.equals("appetite"), true, "topApp2[0] == appetite");
            check(topApp2[1].word.equals("apple"),     true, "topApp2[1] == apple");
        }

        // Top 3 for "b" should include band (3) and banana(1) in that order
        WordFrequency[] topB3 = trie.getTopK("b", 3);
        System.out.println("Top 3 for prefix 'b':");
        for (WordFrequency wf : topB3) {
            System.out.println("  " + wf.word + " (" + wf.importance + ")");
        }

        // We know only banana(1) and band(3) exist under 'b'
        check(topB3.length == 2, true, "topB3 length == 2");
        if (topB3.length == 2) {
            check(topB3[0].word.equals("band"),   true, "topB3[0] == band");
            check(topB3[1].word.equals("banana"),true, "topB3[1] == banana");
        }

        // No words under "xyz" -> empty array
        WordFrequency[] topXyz = trie.getTopK("xyz", 5);
        check(topXyz.length == 0, true, "topK(xyz,5) is empty");

        System.out.println();
    }

    // ---------------------------------------------------------
    // Test 7: predictNextLetter(prefix)
    // ---------------------------------------------------------
    private static void testPredictNextLetter() {
        System.out.println("=== Test 7: predictNextLetter(prefix) ===");
        CompressedTrie trie = new CompressedTrie();

        // Build a small controlled example:
        // Words: "ab", "ac", "ad"
        // We'll set frequencies so that:
        //  ab: 1, ac: 5, ad: 2
        trie.insert("ab");
        trie.insert("ac");
        trie.insert("ad");

        increment(trie, "ab", 1);
        increment(trie, "ac", 5);
        increment(trie, "ad", 2);

        // For prefix "a": there should be children 'b','c','d'
        // average under 'b' = 1
        // under 'c' = 5
        // under 'd' = 2
        // So best next letter = 'c'
        char next = trie.predictNextLetter("a");
        System.out.println("Predicted next letter after 'a': " + next);
        check(next == 'c', true, "predictNextLetter('a') == 'c'");

        // For a prefix not in trie
        char nextX = trie.predictNextLetter("xyz");
        check(nextX == '\0', true, "predictNextLetter('xyz') == '\\0'");

        System.out.println();
    }

    // ---------------------------------------------------------
    // Helper: increment importance multiple times
    // ---------------------------------------------------------
    private static void increment(CompressedTrie trie, String w, int times) {
        for (int i = 0; i < times; i++) {
            CompressedTrieNode n = trie.getNode(w);
            if (n != null) {
                n.importance++;
            }
        }
    }

    // ---------------------------------------------------------
    // Generic assertion helpers
    // ---------------------------------------------------------
    private static void check(boolean actual, boolean expected, String message) {
        if (actual == expected) {
            System.out.println("  [PASS] " + message);
        } else {
            System.out.println("  [FAIL] " + message +
                               " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void checkDouble(double actual, double expected, double eps, String message) {
        if (Math.abs(actual - expected) <= eps) {
            System.out.println("  [PASS] " + message + " = " + actual);
        } else {
            System.out.println("  [FAIL] " + message +
                               " (expected " + expected + ", got " + actual + ")");
        }
    }
}
