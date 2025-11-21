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
    
    private void insertHelper(CompressedTrieNode current, String word){
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

    public CompressedTrieNode getNode(String word){
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

    public static void main(String[] args) {

        CompressedTrie trie = new CompressedTrie();

        System.out.println("=== Test 1: Basic Insert & Search ===");
        trie.insert("apple");
        trie.insert("application");
        trie.insert("app");
        trie.insert("banana");
        trie.insert("band");

        check(trie.search("apple"), true, "search(apple)");
        check(trie.search("application"), true, "search(application)");
        check(trie.search("app"), true, "search(app)");
        check(trie.search("banana"), true, "search(banana)");
        check(trie.search("band"), true, "search(band)");
        check(trie.search("ba"), false, "search(ba)");
        check(trie.search("appl"), false, "search(appl)");

        System.out.println("\n=== Test 2: Case Insensitivity ===");
        trie.insert("JaVa");
        check(trie.search("java"), true, "search(java)");
        check(trie.search("JAVA"), true, "search(JAVA)");

        System.out.println("\n=== Test 3: Edge Splitting ===");
        CompressedTrie trie2 = new CompressedTrie();
        trie2.insert("carton");
        trie2.insert("car");
        trie2.insert("cart");
        trie2.insert("cat");

        check(trie2.search("car"), true, "search(car)");
        check(trie2.search("cart"), true, "search(cart)");
        check(trie2.search("carton"), true, "search(carton)");
        check(trie2.search("cat"), true, "search(cat)");
        check(trie2.search("ca"), false, "search(ca)");
        check(trie2.search("carts"), false, "search(carts)");

        System.out.println("\n=== Test 4: Test getNode(word) ===");
        CompressedTrieNode node = trie.getNode("apple");
        check(node != null && node.isEndOfWord, true, "getNode(apple)");
        
        trie.insert("hello");
        CompressedTrieNode node2 = trie.getNode("hello");
        check(node2 != null && node2.isEndOfWord, true, "getNode(hello)");

        check(trie.getNode("hell") == null, true, "getNode(hell)");

        System.out.println("\n=== Test 5: Importance Counter ===");
        CompressedTrie trie3 = new CompressedTrie();
        trie3.insert("apple");
        trie3.insert("banana");

        // simulate "apple apple banana apple"
        increment(trie3, "apple");
        increment(trie3, "apple");
        increment(trie3, "banana");
        increment(trie3, "apple");

        check(getImportance(trie3, "apple") == 3, true, "importance(apple)");
        check(getImportance(trie3, "banana") == 1, true, "importance(banana)");

        System.out.println("\nAll tests finished.\n");
    }

    // Helper: increment importance
    private static void increment(CompressedTrie trie, String w) {
        CompressedTrieNode n = trie.getNode(w);
        if (n != null) n.importance++;
    }

    // Helper: get importance
    private static int getImportance(CompressedTrie trie, String w) {
        CompressedTrieNode n = trie.getNode(w);
        return (n == null) ? -1 : n.importance;
    }

    // Helper: testing output
    private static void check(boolean actual, boolean expected, String message) {
        if (actual == expected) {
            System.out.println("  [PASS] " + message);
        } else {
            System.out.println("  [FAIL] " + message + 
                               " (expected " + expected + ", got " + actual + ")");
        }
    }
}
