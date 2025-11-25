/* ai generated, en gia to peo en doulefki sosta */
package exp;

import java.lang.reflect.Field;

public class MemoryController {

    private static final int OBJ_HEADER = 16;
    private static final int ARR_HEADER = 16;
    private static final int REF_SIZE = 8;
    private static final int INT_SIZE = 4;
    private static final int BOOL_SIZE = 1;
    private static final int CHAR_SIZE = 2;

    public static String bytesToMB(long bytes) {
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    public static long estimateClassicTrie(Object trie) {
        if (trie == null) return 0;
        long size = OBJ_HEADER + REF_SIZE;

        try {
            Field rootField = trie.getClass().getDeclaredField("root");
            rootField.setAccessible(true);
            Object rootNode = rootField.get(trie);

            if (rootNode != null) {
                size += estimateClassicNode(rootNode);
            }
        } catch (Exception e) {
            System.err.println("Error calculating Classic Trie memory: " + e.getMessage());
        }
        return size;
    }

    private static long estimateClassicNode(Object node) throws Exception {
        long size = OBJ_HEADER + REF_SIZE + BOOL_SIZE;

        Field childrenField = node.getClass().getDeclaredField("children");
        childrenField.setAccessible(true);
        Object[] children = (Object[]) childrenField.get(node);

        if (children != null) {
            size += ARR_HEADER + (children.length * REF_SIZE);

            for (Object child : children) {
                if (child != null) {
                    size += estimateClassicNode(child);
                }
            }
        }
        return size;
    }

    public static long estimateCompressedTrie(Object trie) {
        if (trie == null) return 0;
        long size = OBJ_HEADER + REF_SIZE;

        try {
            Field rootField = trie.getClass().getDeclaredField("root");
            rootField.setAccessible(true);
            Object rootNode = rootField.get(trie);

            if (rootNode != null) {
                size += estimateCompressedNode(rootNode);
            }
        } catch (Exception e) {
            System.err.println("Error calculating Compressed Trie memory: " + e.getMessage());
        }
        return size;
    }

    private static long estimateCompressedNode(Object node) throws Exception {
        long size = OBJ_HEADER + REF_SIZE + BOOL_SIZE + INT_SIZE;

        Field edgeListField = node.getClass().getDeclaredField("edgeList");
        edgeListField.setAccessible(true);
        Object robinHoodObj = edgeListField.get(node);

        if (robinHoodObj != null) {
            size += OBJ_HEADER + REF_SIZE + (5 * INT_SIZE);

            Field hashTableField = robinHoodObj.getClass().getDeclaredField("hashTable");
            hashTableField.setAccessible(true);
            Object[] hashTable = (Object[]) hashTableField.get(robinHoodObj);

            if (hashTable != null) {
                size += ARR_HEADER + (hashTable.length * REF_SIZE);

                for (Object edge : hashTable) {
                    if (edge != null) {
                        size += estimateEdge(edge);
                    }
                }
            }
        }
        return size;
    }

    private static long estimateEdge(Object edge) throws Exception {
        long size = OBJ_HEADER + (2 * REF_SIZE) + BOOL_SIZE;

        Field childField = edge.getClass().getDeclaredField("child");
        childField.setAccessible(true);
        Object childNode = childField.get(edge);
        if (childNode != null) {
            size += estimateCompressedNode(childNode);
        }

        Field labelField = edge.getClass().getDeclaredField("label");
        labelField.setAccessible(true);
        String label = (String) labelField.get(edge);
        
        if (label != null) {
            size += OBJ_HEADER + REF_SIZE + INT_SIZE;
            size += ARR_HEADER + (label.length() * CHAR_SIZE);
        }

        return size;
    }
}