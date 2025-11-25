public class CompressedTrie {
    CompressedTrieNode root;

    public CompressedTrie(){
        root = new CompressedTrieNode();
    }

    public void insert(String word){
        if(word == null){
            return;
        }
        insertHelper(root, word.toLowerCase());
    }
    
    /*private void insertHelper(CompressedTrieNode current, String word){
        if(word.isEmpty()){
            current.isEndOfWord = true;
            return;
        }

        Edge edge = current.getEdgeByFirstChar(word.charAt(0));
        
        if(edge == null){
            CompressedTrieNode child = new CompressedTrieNode();
            child.isEndOfWord = true;
            current.insertEdge(new Edge(word, child));
            return;
        }

        String label = edge.label;
        int prefixLength = commonPrefixLength(word, label);

        // word == label
        if(prefixLength == label.length() && prefixLength == word.length()){
            edge.child.isEndOfWord = true;
        }
        // the label is a prefix of the word
        else if(prefixLength == label.length() && prefixLength < word.length()){
            insertHelper(edge.child, word.substring(prefixLength));
        }
        // The word is a prefix
        else if(prefixLength < label.length() && prefixLength == word.length()){
            CompressedTrieNode oldChild = edge.child;
            CompressedTrieNode mid = new CompressedTrieNode();
            mid.isEndOfWord = true;
            String remainder = label.substring(prefixLength);
            edge.label = word;
            edge.child = mid;
            mid.insertEdge(new Edge(remainder, oldChild));
        }
        // The word and label have a common prefix
        else if (prefixLength < word.length() && prefixLength < label.length()){
            CompressedTrieNode oldChild = edge.child;
            CompressedTrieNode mid = new CompressedTrieNode();
            String prefix = label.substring(0, prefixLength);
            String remainderWord = word.substring(prefixLength);
            String remainderLabel = label.substring(prefixLength);
            edge.label = prefix;
            edge.child = mid;
            mid.insertEdge(new Edge(remainderLabel, oldChild));
            CompressedTrieNode newChild = new CompressedTrieNode();
            newChild.isEndOfWord = true;
            mid.insertEdge(new Edge(remainderWord, newChild));   
        }
        else{
            CompressedTrieNode child = new CompressedTrieNode();
            child.isEndOfWord = true;
            current.insertEdge(new Edge(word, child));
        }
    }*/

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

        if (prefixLength == label.length() && prefixLength < word.length()) {
            insertHelper(edge.child, word.substring(prefixLength));
            return;
        }

        if (prefixLength == word.length() && prefixLength < label.length()) {
            CompressedTrieNode oldChild = edge.child;
            CompressedTrieNode mid = new CompressedTrieNode();
            mid.isEndOfWord = true;

            String remainder = label.substring(prefixLength);

            edge.label = word;     // shorten edge label to word
            edge.child = mid;      // mid becomes child
            mid.insertEdge(new Edge(remainder, oldChild));
            return;
        }

        // Case 5: word and label diverge -> split into 2 branches
        if (prefixLength < label.length() && prefixLength < word.length()) {
            CompressedTrieNode oldChild = edge.child;
            CompressedTrieNode mid = new CompressedTrieNode();

            String prefix = label.substring(0, prefixLength);
            String remainderLabel = label.substring(prefixLength);
            String remainderWord = word.substring(prefixLength);

            edge.label = prefix;
            edge.child = mid;

            mid.insertEdge(new Edge(remainderLabel, oldChild));

            CompressedTrieNode newChild = new CompressedTrieNode();
            newChild.isEndOfWord = true;
            mid.insertEdge(new Edge(remainderWord, newChild));
            return;
        }
    }


    private static int commonPrefixLength(String a, String b){
        int n = Math.min(a.length(), b.length());
        int cnt = 0;
        while(cnt < n && a.charAt(cnt) == b.charAt(cnt)){
            cnt++;
        }

        return cnt;
    }

    public boolean search(String word){
        if(word == null){
            return false;
        }
        return searchHelper(root, word.toLowerCase());
    }
    
    public boolean searchHelper(CompressedTrieNode current, String word){
        
        if(word.isEmpty()){
            return current.isEndOfWord;
        }
        
        Edge edge = current.getEdgeByFirstChar(word.charAt(0));
        
        // word doesn't exist
        if(edge == null){
            return false;
        }

        String label = edge.label;
        int prefixLength = commonPrefixLength(label, word);
        
        // The word == label
        if(prefixLength == label.length() && prefixLength == word.length()){
            return edge.child.isEndOfWord;
        }
        
        // The label is a PREFIX of the word
        if (prefixLength == label.length() && prefixLength < word.length()) {
            return searchHelper(edge.child, word.substring(prefixLength));
        }
        
        // not a word
        return false;
    }

    /*public CompressedTrieNode getNode(String word){
        if(word == null){
            return null;
        }
        return getNodeHelper(root, word);
    }

    public CompressedTrieNode getNodeHelper(CompressedTrieNode current, String word){
        if(word.isEmpty()){
            return current.isEndOfWord ? current : null;
        }

        Edge edge = current.getEdgeByFirstChar(word.charAt(0));

        if(edge == null){
            return null;
        }

        String label = edge.label;
        int prefixLength = commonPrefixLength(label, word);

        if(prefixLength == label.length() && prefixLength == word.length()){
            return edge.child.isEndOfWord ? edge.child : null;
        }

        if(prefixLength < word.length() && prefixLength == label.length()){
            return getNodeHelper(edge.child, word.substring(prefixLength));
        }

        return null;
    }
    */
    public CompressedTrieNode getNode(String word){
        if (word == null) {
            return null;
        }
        return getNodeHelper(root, word.toLowerCase());
    }

    private CompressedTrieNode getNodeHelper(CompressedTrieNode current, String word){
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


    private void collectTopK(CompressedTrieNode node, String prefix, MinHeap heap, int k){
        if(node == null) return;

        if(node.isEndOfWord){
            int imp = node.importance;
            WordFrequency wf = new WordFrequency(prefix, imp);

            if(heap.size() < k){
                heap.insert(wf);
            }
            else{
                WordFrequency min = heap.getMin();
                if(min != null && imp > min.importance){
                    heap.removeMin();
                    heap.insert(wf);
                }
            }
        }

        Edge[] edges = node.getAllEdges();
        if(edges == null) return;

        for(Edge e : edges){
            if(e != null && e.occupied){
                String newPrefix = prefix + e.label;
                collectTopK(e.child, newPrefix, heap, k);
            }
        }
    }

    
    public WordFrequency[] getTopK(String prefix, int k) {
        if (k <= 0) return new WordFrequency[0];

        prefix = prefix.toLowerCase();
        CompressedTrieNode node = getNode(prefix);
        if (node == null) return new WordFrequency[0];

        // FIX: Get the REAL prefix based on actual trie structure
        String realPrefix = reconstructPath(node);

        MinHeap heap = new MinHeap(k);
        collectTopK(node, realPrefix, heap, k);

        WordFrequency[] result = heap.toArray();
        HeapSort.sort(result);
        return result;
    }

    private String reconstructPath(CompressedTrieNode target) {
        return reconstructPathHelper(root, "", target);
    }

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

    private double getSubtreeAverage(CompressedTrieNode startNode) {
        if (startNode == null) return 0.0;

        long totalImp = 0;
        int count = 0;

        // dfs
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

    public double getAverageFrequency(String prefix) {
        if(prefix == null) return -1;
        prefix = prefix.toLowerCase();
        CompressedTrieNode node = getNode(prefix.toLowerCase());
        if (node == null) return 0.0;
        return getSubtreeAverage(node);
    }

    public char predictNextLetter(String prefix) {
        CompressedTrieNode rootOfPrefix = getNode(prefix);
        prefix = prefix.toLowerCase();

        if (rootOfPrefix == null) {
            System.out.println("No words start with \"" + prefix + "\".");
            return '\0';
        }

        Edge[] children = rootOfPrefix.getAllEdges();
        if (children == null) {
            System.out.println("No next letter can be predicted (no children).");
            return '\0';
        }

        char bestChar = '\0';
        double maxAverage = -1.0;

        for (Edge e : children) {
            
            if (e != null && e.occupied) {
                double subAvg = getSubtreeAverage(e.child);
                
                char nextChar = e.label.charAt(0);

                if (subAvg > maxAverage) {
                    maxAverage = subAvg;
                    bestChar = nextChar;
                }
            }
        }
        if (maxAverage == -1.0 || bestChar == '\0') {
            System.out.println("No next letter can be predicted.");
        }
        return bestChar;
    }

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
