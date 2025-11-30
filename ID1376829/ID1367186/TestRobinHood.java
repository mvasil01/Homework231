package ID1376829.ID1367186;
public class TestRobinHood {

    public static void main(String[] args) {

        RobinHoodHashing table = new RobinHoodHashing();

        // Small helper: insert a label and print the table
        java.util.function.Consumer<String> insertAndPrint = label -> {
            System.out.println("\n=== INSERT \"" + label + "\" ===");
            table.insert(new Edge(label, null));   // child is null for testing
            printTable(table);
        };

        // Insert some keys that will collide on first letter (a/b)
        insertAndPrint.accept("apple");
        insertAndPrint.accept("banana");
        insertAndPrint.accept("apricot");
        insertAndPrint.accept("blue");
        insertAndPrint.accept("avocado");
        insertAndPrint.accept("berry");

        // Test search
        System.out.println("\n=== SEARCH TESTS ===");
        System.out.println("Contains 'apple'?   " + table.search("apple"));
        System.out.println("Contains 'banana'?  " + table.search("banana"));
        System.out.println("Contains 'avocado'? " + table.search("avocado"));
        System.out.println("Contains 'mango'?   " + table.search("mango"));

        System.out.println("\n=== FINAL TABLE ===");
        printTable(table);
    }

    // --- helper to print internal state of RobinHoodHashing ---
    private static void printTable(RobinHoodHashing table) {
        int capacity = (int) getField(table, "capacity");
        int size = (int) getField(table, "size");
        int maxProbe = (int) getField(table, "maxProbeLength");
        Edge[] arr = (Edge[]) getField(table, "hashTable");

        System.out.println("Capacity:   " + capacity);
        System.out.println("Size:       " + size);
        System.out.println("MaxProbe:   " + maxProbe);
        System.out.println("Hash table:");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) {
                System.out.println("  [" + i + "] null");
            } else {
                System.out.println("  [" + i + "] "
                        + arr[i].label
                        + " (occupied=" + arr[i].occupied + ")");
            }
        }
    }

    // --- reflection helper for accessing private fields ---
    private static Object getField(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field f =
                    obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            System.out.println("Error reading field " + fieldName + ": " + e);
            return null;
        }
    }
}
