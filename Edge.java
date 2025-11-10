public class Edge {
    public String label;
    public CompressedTrieNode child;

    public Edge(String label, CompressedTrieNode child){
        this.label = label;
        this.child = child;
    }
}
