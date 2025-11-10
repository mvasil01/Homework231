public class CompressedTrie {
    CompressedTrieNode root;

    public CompressedTrie(){
        root = new CompressedTrieNode();
    }

    public void insert(String word){
        if(word == null){
            return;
        }
        insertHelper(root, word);
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
        return searchHelper(root, word);
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
}
