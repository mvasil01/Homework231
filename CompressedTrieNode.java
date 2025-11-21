public class CompressedTrieNode{
    private RobinHoodHashing edgeList;
    public boolean isEndOfWord;

    public int importance;

    public CompressedTrieNode(){
        edgeList = new RobinHoodHashing();
        isEndOfWord = false;
    }

    public void insertEdge(Edge edge){
        if(edge == null){
            throw new RuntimeException("The edge given to insert is null");
        }
        edgeList.insert(edge);
    }


    public Edge getEdgeByFirstChar(char c){
        return edgeList.getEdge(c);
    }

    public static void main(String[] args) {

        // Create a CompressedTrieNode (this will be our parent node)
        CompressedTrieNode parent = new CompressedTrieNode();

        // Create some child nodes to attach via edges
        CompressedTrieNode child1 = new CompressedTrieNode();
        CompressedTrieNode child2 = new CompressedTrieNode();
        CompressedTrieNode child3 = new CompressedTrieNode();

        // Create edges with labels
        Edge e1 = new Edge("apple", child1);
        Edge e2 = new Edge("banana", child2);
        Edge e3 = new Edge("cherry", child3);

        // Insert edges into the parent node
        parent.insertEdge(e1);
        parent.insertEdge(e2);
        parent.insertEdge(e3);

        // Print confirmation
        System.out.println("=== Edges inserted into parent node ===");
        for (char c : new char[] {'a', 'b', 'c'}) {
            Edge found = parent.getEdgeByFirstChar(c);
            if (found != null) {
                System.out.println("Found edge starting with '" + c + "': " + found.label);
            } else {
                System.out.println("No edge starting with '" + c + "'");
            }
        }

        // Try to search for a non-existent edge
        char missing = 'z';
        Edge notFound = parent.getEdgeByFirstChar(missing);
        System.out.println("\nSearching for '" + missing + "'...");
        if (notFound == null) {
            System.out.println("No edge found starting with '" + missing + "' (as expected).");
        } else {
            System.out.println("Unexpectedly found: " + notFound.label);
        }

        // Test the isEndOfWord flag
        System.out.println("\n=== Testing isEndOfWord flag ===");
        System.out.println("Initially: " + parent.isEndOfWord);
        parent.isEndOfWord = true;
        System.out.println("After setting: " + parent.isEndOfWord);
    }
}
