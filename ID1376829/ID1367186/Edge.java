package ID1376829.ID1367186;
public class Edge {
    public String label;
    public CompressedTrieNode child;
    public boolean occupied;

    public Edge(String label, CompressedTrieNode child){
        this.label = label;
        this.child = child;
        occupied = true;
    }
}
