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
}
