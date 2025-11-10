// Tester.java AI generated

public class Tester {

    // Counter for failed tests
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("--- Starting CompressedTrie Extreme Test Suite ---");

        // Test 1: PDF Example
        // Tests the words from the presentation example
        System.out.println("\n--- Test 1: PDF Example (bear, bell, bid, be, bull, stock, stop) ---");
        CompressedTrie pdfTrie = new CompressedTrie();
        String[] pdfWords = {"bear", "bell", "bid", "be", "bull", "stock", "stop"};
        for (String word : pdfWords) {
            pdfTrie.insert(word);
        }

        // Check inserted words
        for (String word : pdfWords) {
            check(pdfTrie, word, true);
        }
        
        // Check non-existent words from the same stems
        check(pdfTrie, "b", false);
        check(pdfTrie, "bel", false);
        check(pdfTrie, "sto", false);
        check(pdfTrie, "bears", false);
        check(pdfTrie, "belli", false);
        check(pdfTrie, "billing", false);

        // Test 2: All 4 Insertion Cases (Slides 5-6)
        System.out.println("\n--- Test 2: PDF Insertion Cases ---");
        CompressedTrie caseTrie = new CompressedTrie();

        // Base inserts
        caseTrie.insert("carton");
        
        // Case 1: Word is prefix of label ("carton" -> "car" + "ton")
        caseTrie.insert("car"); 
        System.out.println("Inserted 'carton', then 'car' (Case 1)");
        check(caseTrie, "car", true);
        check(caseTrie, "carton", true);

        // Case 2: Label is prefix of word (at node "car", insert "t")
        caseTrie.insert("cart"); 
        System.out.println("Inserted 'cart' (Case 2)");
        check(caseTrie, "cart", true);

        // Case 3: Partial match (at root, "ca" split from "car")
        caseTrie.insert("cat"); 
        System.out.println("Inserted 'cat' (Case 3)");
        check(caseTrie, "cat", true);
        check(caseTrie, "car", true); // Ensure old words still exist

        // Case 4: No common prefix
        caseTrie.insert("dog"); 
        System.out.println("Inserted 'dog' (Case 4)");
        check(caseTrie, "dog", true);

        // Test 3: Null, Empty, and Duplicates
        System.out.println("\n--- Test 3: Edge Cases (Null, Empty, Duplicates) ---");
        CompressedTrie edgeCaseTrie = new CompressedTrie();
        
        edgeCaseTrie.insert(null); // Should not crash
        check(edgeCaseTrie, null, false); // Search for null should be false

        // Test insertion of the empty string
        edgeCaseTrie.insert(""); 
        check(edgeCaseTrie, "", true); // Search for empty string

        edgeCaseTrie.insert("hello");
        edgeCaseTrie.insert("hello"); // Insert duplicate
        edgeCaseTrie.insert("hello"); // Insert duplicate
        check(edgeCaseTrie, "hello", true);
        
        // Check that a prefix of an inserted word is not a word
        check(edgeCaseTrie, "hell", false);

        // Test 4: Complex Chaining and Splitting
        System.out.println("\n--- Test 4: Complex Chaining & Splitting ---");
        CompressedTrie complexTrie = new CompressedTrie();
        complexTrie.insert("testing");
        complexTrie.insert("test");    // Case 1 split
        complexTrie.insert("tester");  // Case 2 split from "test"
        complexTrie.insert("team");    // Case 3 split from "te"
        
        check(complexTrie, "test", true);
        check(complexTrie, "testing", true);
        check(complexTrie, "tester", true);
        check(complexTrie, "team", true);

        // Check prefixes that are not words
        check(complexTrie, "t", false);
        check(complexTrie, "te", false);
        check(complexTrie, "tes", false);

        // Test 5: Case Sensitivity (Assuming default Java String comparison)
        System.out.println("\n--- Test 5: Case Sensitivity ---");
        CompressedTrie caseSenseTrie = new CompressedTrie();
        caseSenseTrie.insert("Java");
        check(caseSenseTrie, "Java", true);
        check(caseSenseTrie, "java", false); // Should be false
        caseSenseTrie.insert("java");
        check(caseSenseTrie, "java", true); // Should now be true
        
        // --- Test Suite Summary ---
        System.out.println("\n-------------------------------------------");
        if (failedTests == 0) {
            System.out.println("✅ SUCCESS: All tests passed!");
        } else {
            System.out.println("❌ FAILED: " + failedTests + " test(s) failed.");
        }
        System.out.println("-------------------------------------------");
    }

    /**
     * Helper method to check search results and print pass/fail status.
     * @param trie The trie to search in.
     * @param word The word to search for.
     * @param expected The expected result (true or false).
     */
    private static void check(CompressedTrie trie, String word, boolean expected) {
        boolean actual = trie.search(word);
        if (actual == expected) {
            System.out.println("  [PASS] search(\"" + word + "\") == " + expected);
        } else {
            System.out.println("  [FAIL] search(\"" + word + "\") == " + actual + " (Expected: " + expected + ")");
            failedTests++;
        }
    }
}