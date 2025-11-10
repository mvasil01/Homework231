public class CompressedTrie {
    CompressedTrieNode root;

    public CompressedTrie(){
        root = new CompressedTrieNode();
    }

    public void insert(String word){
        if(edgeList == null){
            edgeList.insert(edge);
            return;
        }
        char c = edge.label.charAt(0);
        Edge current = edgeList.getEdge(c);
        if(current == null){
            edgeList.insert(edge);
            return;
        }
        
        int cnt = 0;
        String rest;
        for(int i = 0; i < current.label.length(); i++){
            if(current.label.charAt(i) != edge.label.charAt(i)){
                break;
            }
            cnt++;
        }
        rest = current.label.substring(cnt - 1);
        CompressedTrieNode newChild = new CompressedTrieNode();
        newChild.edgeList.insert(new Edge(rest, current.child));
        rest = edge.label.substring()
        current.label = current.label.substring(0, cnt - 1);
        current.child = newChild;
    }
}
