import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

/**
 * Represents a single node in the M-ary tree.
 * It contains locking information and pointers to its parent and children.
 * Crucially, it also maintains a count of its locked descendants for optimization.
 */
class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    List<Node> children;
    int lockedDescendantCount;

    Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
        this.isLocked = false;
        this.lockedBy = -1; // -1 indicates unlocked
        this.children = new ArrayList<>();
        this.lockedDescendantCount = 0;
    }
}

public class TestClass {

    // A map for O(1) access to any node by its name
    private static Map<String, Node> nodeMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // Using a fast I/O reader
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        // 1. Read initial parameters
        int n = Integer.parseInt(br.readLine());
        int m = Integer.parseInt(br.readLine());
        int q = Integer.parseInt(br.readLine());

        // 2. Read node names and build the tree
        String[] nodeNames = new String[n];
        for (int i = 0; i < n; i++) {
            nodeNames[i] = br.readLine();
        }
        buildTree(nodeNames, m);

        // 3. Process each query
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < q; i++) {
            String[] queryParts = br.readLine().split(" ");
            int opType = Integer.parseInt(queryParts[0]);
            String nodeName = queryParts[1];
            int userId = Integer.parseInt(queryParts[2]);

            Node targetNode = nodeMap.get(nodeName);
            boolean result = false;

            switch (opType) {
                case 1:
                    result = lock(targetNode, userId);
                    break;
                case 2:
                    result = unlock(targetNode, userId);
                    break;
                case 3:
                    result = upgradeLock(targetNode, userId);
                    break;
            }
            output.append(result).append("\n");
        }
        System.out.print(output);
    }

    /**
     * Builds the M-ary tree from a level-order list of node names.
     */
    private static void buildTree(String[] names, int m) {
        if (names.length == 0) return;
        
        Node root = new Node(names[0], null);
        nodeMap.put(names[0], root);

        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        int currentIndex = 1;
        while (!queue.isEmpty() && currentIndex < names.length) {
            Node parent = queue.poll();
            for (int i = 0; i < m && currentIndex < names.length; i++) {
                Node child = new Node(names[currentIndex], parent);
                parent.children.add(child);
                nodeMap.put(names[currentIndex], child);
                queue.add(child);
                currentIndex++;
            }
        }
    }
    
    /**
     * Operation 1: Attempts to lock a node.
     * Time Complexity: O(log N)
     */
    private static boolean lock(Node node, int uid) {
        if (node.isLocked || node.lockedDescendantCount > 0) return false;

        for (Node p = node.parent; p != null; p = p.parent) {
            if (p.isLocked) return false;
        }

        node.isLocked = true;
        node.lockedBy = uid;
        for (Node p = node.parent; p != null; p = p.parent) {
            p.lockedDescendantCount++;
        }
        return true;
    }

    /**
     * Operation 2: Attempts to unlock a node.
     * Time Complexity: O(log N)
     */
    private static boolean unlock(Node node, int uid) {
        if (!node.isLocked || node.lockedBy != uid) return false;

        node.isLocked = false;
        node.lockedBy = -1;
        for (Node p = node.parent; p != null; p = p.parent) {
            p.lockedDescendantCount--;
        }
        return true;
    }

    /**
     * Operation 3: OPTIMIZED upgradeLock function.
     * This version avoids redundant traversals to the root.
     */
    private static boolean upgradeLock(Node node, int uid) {
        if (node.isLocked || node.lockedDescendantCount == 0) {
            return false;
        }

        List<Node> descendantsToUnlock = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>(node.children);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.isLocked) {
                if (current.lockedBy != uid) return false; // Fail: descendant locked by another user
                descendantsToUnlock.add(current);
            }
            // Optimization: If we've found all known locked descendants, we can stop searching deeper.
            if (descendantsToUnlock.size() == node.lockedDescendantCount) break;
            queue.addAll(current.children);
        }

        // Fail if the locked descendants are not owned by the user, or none were found
        if (descendantsToUnlock.size() != node.lockedDescendantCount) {
            return false;
        }
        
        // --- Perform the efficient update ---
        
        // 1. Unlock descendants and update counts for ancestors UP TO the current node
        for(Node descendant : descendantsToUnlock) {
            descendant.isLocked = false;
            descendant.lockedBy = -1;
            for(Node p = descendant.parent; p != node; p = p.parent) {
                p.lockedDescendantCount--;
            }
        }
        
        // 2. Lock the current node and update its own count and its ancestors' counts
        node.isLocked = true;
        node.lockedBy = uid;
        int unlockedCount = node.lockedDescendantCount;
        node.lockedDescendantCount = 0; // It now has 0 locked descendants

        for(Node p = node.parent; p != null; p = p.parent) {
            p.lockedDescendantCount -= (unlockedCount - 1); // Net change
        }

        return true;
    }
}