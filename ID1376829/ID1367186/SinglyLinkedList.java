package ID1376829.ID1367186;
public class SinglyLinkedList {
	
	protected class Node{
		Edge edge;
		Node next;
		
		Node(Edge edge){
			this.edge = edge;
			next = null;
		}
	}
	
	protected Node head;

	public SinglyLinkedList() {
		head = null;
	}
	
	public void insert(Edge edge) {
		if (head == null) {
			head = new Node(edge);
			return;
		}
		
		Node last = head;
		while (last.next != null){
			last = last.next;
		}
		
		last.next = new Node(edge);
	}
	
	public Edge getEdge (char c){
		Node currentNode = head;

		while (currentNode != null) {
			if (currentNode.edge.label.charAt(0) == c) {
				return currentNode.edge;
        }
        currentNode = currentNode.next;
		}
		
    return null;
	}

	public static void main(String[] args) {
        // Create a list
        SinglyLinkedList list = new SinglyLinkedList();

        // Create some dummy nodes for edges
        CompressedTrieNode n1 = new CompressedTrieNode();
        CompressedTrieNode n2 = new CompressedTrieNode();
        CompressedTrieNode n3 = new CompressedTrieNode();

        // Create edges with different starting letters
        Edge e1 = new Edge("apple", n1);
        Edge e2 = new Edge("banana", n2);
        Edge e3 = new Edge("cherry", n3);

        // Insert edges into the list
        list.insert(e1);
        list.insert(e2);
        list.insert(e3);

        // Print the edges in order
        System.out.println("=== Edges in list ===");
        SinglyLinkedList.Node current = list.head;
        while (current != null) {
            System.out.println("Edge label: " + current.edge.label);
            current = current.next;
        }

        // Test getEdge method
        System.out.println("\n=== Testing getEdge ===");
        char searchChar = 'b';
        Edge found = list.getEdge(searchChar);
        if (found != null) {
            System.out.println("Found edge starting with '" + searchChar + "': " + found.label);
        } else {
            System.out.println("No edge starting with '" + searchChar + "'");
        }

        // Try a non-existent edge
        searchChar = 'z';
        found = list.getEdge(searchChar);
        if (found != null) {
            System.out.println("Found edge starting with '" + searchChar + "': " + found.label);
        } else {
            System.out.println("No edge starting with '" + searchChar + "'");
        }
    }
}
