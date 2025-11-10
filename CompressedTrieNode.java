public class CompressedTrieNode{
    private SinglyLinkedList edgeList;
    public boolean isEndOfWord;

    public CompressedTrieNode(){
        edgeList = new SinglyLinkedList();
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
}
