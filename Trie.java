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

    // Test program
    public static void main(String[] args) {
        Trie trie = new Trie();

        // Insert some words
        trie.insert("apple");
        trie.insert("app");
        trie.insert("banana");

        // Test searches
        System.out.println("Search 'apple': " + trie.search("apple"));   // true
        System.out.println("Search 'app': " + trie.search("app"));       // true
        System.out.println("Search 'banana': " + trie.search("banana")); // true
        System.out.println("Search 'ban': " + trie.search("ban"));       // false
        System.out.println("Search 'orange': " + trie.search("orange")); // false
    }
}

