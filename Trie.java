public class Trie {

    public final static int alphabetSize = 26;

    private class TrieNode {

        private TrieNode[] children;
        private boolean isEndOfWord;
        public TrieNode() { 
            children = new TrieNode[alphabetSize];
            isEndOfWord = false;
        }
    }

    private TrieNode root;

    public Trie() { 
        root = new TrieNode();
    }
    public boolean search(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++){
            int pos = word.charAt(i) - 'a';

            if (current.children[pos] == null){
                return false;
            }

            current = current.children[pos];
        }

        return current.isEndOfWord;
    }

    public void insert(String word) {
        TrieNode current = root;
        int pos;
        
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (c < 'a' || c > 'z') {
                System.out.println("Wrong input to insert: " + word);
                return;
            }

            pos = c - 'a';
            if (current.children[pos] == null) {
                current.children[pos] = new TrieNode();
            }
            current = current.children[pos];
        }

        current.isEndOfWord = true;
    }

    public static void displayHelper(TrieNode node, String word){
        if (node == null){
            return;
        }

        if (node.isEndOfWord){
            System.out.println(word);
        }

        for(int i = 0; i < alphabetSize; i++){
            if(node.children[i] != null){
                char nextChar = (char) ('a' +  i);
                displayHelper(node.children[i], word + nextChar);
            }
        }
        
    }

    public void display(){
        if (root != null){
            displayHelper(root, "");
        }
    }

    public boolean delete(String key){
        if(key.equals("")){
            return false;
        }
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c < 'a' || c > 'z') return false;
        }

        if(!search(key)){
            return false;
        }

        deleteHelper(root, key, 0);
        return true;
    }

    private static boolean hasNoChildren(TrieNode node){
        for(int i = 0; i < alphabetSize; i++){
            if(node.children[i] != null){
                return false;
            }
        }
        return true;
    }

    public static boolean deleteHelper(TrieNode node, String key, int depth){
        if (node == null){
            return false;
        }

        if(depth == key.length()){
            node.isEndOfWord = false;
            return hasNoChildren(node);
        }

        int index = key.charAt(depth) - 'a';
        TrieNode child = node.children[index];

        // Not necessary but safe because of search(key)
        if(child == null){
            return false;
        }
        
        boolean shouldDeleteChild = deleteHelper(child, key, depth + 1);
        if(shouldDeleteChild){
            node.children[index] = null;
        }

        return !node.isEndOfWord && hasNoChildren(node);
    }

    // Test program
    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("app");
        trie.insert("apple");
        trie.insert("bat");
        trie.insert("band");
        trie.insert("banana");

        System.out.println("Initial words:");
        trie.display();

        // 1) Δεν υπάρχει στο δέντρο
        System.out.println("\nDelete 'cat' (not present): " + trie.delete("cat"));
        trie.display();

        // 2) Το στοιχείο αποτελεί πρόθεμα άλλου (app -> apple)
        System.out.println("\nDelete 'app' (prefix of 'apple'): " + trie.delete("app"));
        System.out.println("Search 'app' after delete: " + trie.search("app"));       // false
        System.out.println("Search 'apple' after delete: " + trie.search("apple"));   // true
        trie.display();

        // 3) Αυτόνομο στοιχείο (bat)
        System.out.println("\nDelete 'bat' (autonomous): " + trie.delete("bat"));
        System.out.println("Search 'bat' after delete: " + trie.search("bat"));       // false
        trie.display();

        // 4) Το στοιχείο έχει άλλα ως πρόθεμα: διαγραφή από το τέρμα προς τα πάνω μέχρι prefix
        // π.χ. διαγράφουμε 'banana' ενώ υπάρχει 'band' (μοιράζονται "ban")
        System.out.println("\nDelete 'banana' (shares prefix 'ban' with 'band'): " + trie.delete("banana"));
        System.out.println("Search 'band' after delete: " + trie.search("band"));     // true
        System.out.println("Search 'banana' after delete: " + trie.search("banana")); // false
        trie.display();
    }
}

