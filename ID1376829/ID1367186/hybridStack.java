package ID1376829.ID1367186;
public class hybridStack {
	
	private class hybridStackNode {
        CompressedTrieNode[] nodes;
		String[] words;
		
		int top;
		hybridStackNode next;
		
		hybridStackNode (int capacity){
            nodes = new CompressedTrieNode[capacity];
            words = new String[capacity];
            top = -1;
        }
		
		boolean isEmpty() {
			return top == -1;
		}
		
		boolean isFull() {
			return top == words.length - 1;
		}
		
		void push(CompressedTrieNode node, String word) {
			top++;
			nodes[top] = node;
            words[top] = word;
		}
		
		void pop() {
            nodes[top] = null;
            words[top] = null;
			top--;
		}
		
		CompressedTrieNode topNode() {
			return nodes[top];
		}

        String topWord() {
            return words[top];
        }
		
        @Override
		public String toString(){
			if (this.isEmpty()) {
				return "";
			}
			
			String res = "";
			for (int i = 0; i <= top; i++) {
				res += words[i] + " ";
			}
			
			return res;
		}
	}
	
	private hybridStackNode head;
	private final int capacity;
	private int totalSize;
	
	public hybridStack(int capacity) {
		this.capacity = capacity;
		totalSize = 0;
		head = new hybridStackNode(this.capacity);
	}
	
	public void push(CompressedTrieNode node, String word) {
		if (head.isFull()) {
			hybridStackNode newHead = new hybridStackNode(capacity);
			newHead.next = head;
			head = newHead;
		}
		
		head.push(node, word);
		totalSize++;
	}
	
	public void pop() throws Exception {
		if (head.isEmpty()) {
			throw new Exception("Empty stack");
		}
		
		head.pop();
		
		if (head.isEmpty()) {
			if (head.next == null) {
                // If it's the last block, just reset it
				head = new hybridStackNode(capacity);
			} else {
                // Otherwise move to the next block
				head = head.next;
			}
		}
		
		totalSize--;
	}
	
	public CompressedTrieNode topNode() throws Exception {
		if (head.isEmpty()) {
			throw new Exception("Empty stack");
		}
		return head.topNode();
	}

    public String topWord() throws Exception {
		if (head.isEmpty()) {
			throw new Exception("Empty stack");
		}
		return head.topWord();
	}
	
	public boolean isEmpty() {
		return (totalSize == 0);
	}
	
	public int size() {
		return totalSize;
	}
	
    @Override
	public String toString() {
		String stc = "";
		hybridStackNode temp = head;
		while(temp != null) {
			stc += temp.toString();
			temp = temp.next;
		}
		
		return stc;
	}
	
	// ====================================================================
    // --- ULTIMATE MAIN FUNCTION FOR TESTING ALL METHODS AND SCENARIOS ---
    // ====================================================================
    
    private static void testResult(String operationName, String expected, String actual) {
        boolean passed = expected.equals(actual);
        String status = passed ? "PASS" : "FAIL";
        System.out.println("--- " + status + " --- " + operationName);
        if (!passed) {
            System.out.println("    Expected: " + expected);
            System.out.println("    Actual:   " + actual);
        }
    }
    
    private static void safeOperation(hybridStack stack, String methodName, String expectedError) {
        try {
            if ("top".equals(methodName)) {
                // Testing topWord() for string verification
                String value = stack.topWord();
                if (expectedError.isEmpty()) {
                     testResult("Test " + methodName + " - Exception Check", "No Exception Thrown", "No Exception Thrown");
                     System.out.println("    Actual Word Read: " + value); 
                } else {
                     testResult("Test " + methodName + " - Expected Exception", expectedError, "Error was not thrown");
                }
            } else if ("pop".equals(methodName)) {
                stack.pop();
                if (expectedError.isEmpty()) {
                    testResult("Test " + methodName + " - Exception Check", "No Exception Thrown", "No Exception Thrown");
                } else {
                    testResult("Test " + methodName + " - Expected Exception", expectedError, "Error was not thrown");
                }
            }
        } catch (Exception e) {
            String expected = expectedError;
            String actual = e.getMessage();
            testResult("Test " + methodName + " - Expected Exception", expected, actual);
        }
    }

    public static void main(String[] args) {
        // Dummy node for testing purposes
        CompressedTrieNode dummy = new CompressedTrieNode();

        // Test 1: Capacity 3 - Standard Operations and Overflow
        final int CAPACITY = 3;
        System.out.println("====================================================================");
        System.out.println("TEST SUITE 1: Capacity " + CAPACITY + " - Basic and Overflow Testing");
        System.out.println("====================================================================");
        
        hybridStack stack = new hybridStack(CAPACITY);

        // A. Initialization
        System.out.println("\n[A] Initial Checks");
        testResult("Size (Initial)", "0", String.valueOf(stack.size()));
        testResult("isEmpty (Initial)", "true", String.valueOf(stack.isEmpty()));
        testResult("toString (Initial)", "", stack.toString());
        safeOperation(stack, "top", "Empty stack");
        safeOperation(stack, "pop", "Empty stack");

        // B. Filling First Node
        System.out.println("\n[B] Filling First Node (Push 'a', 'b', 'c')");
        stack.push(dummy, "a"); 
        stack.push(dummy, "b"); 
        stack.push(dummy, "c"); 
        
        testResult("Size (After 3 pushes)", "3", String.valueOf(stack.size()));
        safeOperation(stack, "top", ""); // Expected: c
        testResult("toString (Filled Node 1)", "a b c ", stack.toString());

        // C. Node Overflow
        System.out.println("\n[C] Node Overflow (Push 'd', 'e', 'f')");
        stack.push(dummy, "d"); 
        stack.push(dummy, "e"); 
        stack.push(dummy, "f"); 
        
        testResult("Size (After 6 pushes)", "6", String.valueOf(stack.size()));
        safeOperation(stack, "top", ""); // Expected: f
        testResult("toString (After overflow)", "d e f a b c ", stack.toString());

        // D. Pop and Head Replacement
        System.out.println("\n[D] Pop and Node Deletion");

        safeOperation(stack, "pop", ""); // Pop f
        testResult("Size (After pop f)", "5", String.valueOf(stack.size()));
        safeOperation(stack, "top", ""); // Expected: e
        testResult("toString (Pop f)", "d e a b c ", stack.toString());

        safeOperation(stack, "pop", ""); // Pop e
        testResult("Size (After pop e)", "4", String.valueOf(stack.size()));
        safeOperation(stack, "top", ""); // Expected: d
        testResult("toString (Pop e)", "d a b c ", stack.toString());

        safeOperation(stack, "pop", ""); // Pop d (Head empties, moves next)
        testResult("Size (After pop d)", "3", String.valueOf(stack.size()));
        safeOperation(stack, "top", ""); // Expected: c (from Node 1)
        testResult("toString (Pop d)", "a b c ", stack.toString());

        // E. Critical Pop Edge Case
        System.out.println("\n[E] Critical Pop Edge Case (Emptying last node)");
        
        safeOperation(stack, "pop", ""); // Pop c
        safeOperation(stack, "pop", ""); // Pop b
        testResult("Size (After pop c, b)", "1", String.valueOf(stack.size()));
        safeOperation(stack, "top", ""); // Expected: a
        testResult("toString (Size 1)", "a ", stack.toString());

        safeOperation(stack, "pop", ""); // Pop a
        testResult("Size (After final pop)", "0", String.valueOf(stack.size()));
        testResult("isEmpty (After final pop)", "true", String.valueOf(stack.isEmpty()));
        testResult("toString (Empty stack)", "", stack.toString());

        // F. Re-test Empty
        System.out.println("\n[F] Re-test Empty Stack Operations");
        safeOperation(stack, "top", "Empty stack");
        safeOperation(stack, "pop", "Empty stack");
        
        // G. Push after Full Empty
        System.out.println("\n[G] Push after Full Empty");
        stack.push(dummy, "new"); 
        testResult("Size (Push new)", "1", String.valueOf(stack.size()));
        safeOperation(stack, "top", ""); // Expected: new
        testResult("toString (Push new)", "new ", stack.toString());

        // Test 2: Capacity 1
        System.out.println("\n====================================================================");
        System.out.println("TEST SUITE 2: Capacity 1 - Extreme Node Creation Test");
        System.out.println("====================================================================");
        
        hybridStack stackCap1 = new hybridStack(1);

        System.out.println("\n[H] Pushing 3 elements with capacity 1");
        stackCap1.push(dummy, "x"); 
        stackCap1.push(dummy, "y"); 
        stackCap1.push(dummy, "z"); 
        
        testResult("Size (Capacity 1, 3 pushes)", "3", String.valueOf(stackCap1.size()));
        safeOperation(stackCap1, "top", ""); // Expected: z
        testResult("toString (Capacity 1)", "z y x ", stackCap1.toString());

        System.out.println("\n[I] Popping all 3 elements with capacity 1");
        
        safeOperation(stackCap1, "pop", ""); // Pop z
        testResult("Size (Pop z)", "2", String.valueOf(stackCap1.size()));
        safeOperation(stackCap1, "top", ""); // Expected: y

        safeOperation(stackCap1, "pop", ""); // Pop y
        testResult("Size (Pop y)", "1", String.valueOf(stackCap1.size()));
        safeOperation(stackCap1, "top", ""); // Expected: x

        safeOperation(stackCap1, "pop", ""); // Pop x
        testResult("Size (Pop x, Empty)", "0", String.valueOf(stackCap1.size()));
        testResult("isEmpty (Final check)", "true", String.valueOf(stackCap1.isEmpty()));
        safeOperation(stackCap1, "pop", "Empty stack");

        System.out.println("\n====================================================================");
        System.out.println("TESTING COMPLETE.");
        System.out.println("====================================================================");
    }
}