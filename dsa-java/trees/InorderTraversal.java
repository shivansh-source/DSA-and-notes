/**
 * Problem: Binary Tree Inorder Traversal
 *
 * Description:
 *   Given the root of a binary tree, return the inorder traversal
 *   of its node values (left -> root -> right).
 *
 * Approach:
 *   Iterative approach using a stack (avoids recursion overhead).
 *   Push nodes to the stack as we go left. When we can't go further left,
 *   pop a node, record its value, then explore its right subtree.
 *
 * Time Complexity:  O(n) — every node is visited once
 * Space Complexity: O(h) where h is the height of the tree (stack depth)
 *                   O(n) in the worst case (skewed tree)
 *
 * Example:
 *       1
 *        \
 *         2
 *        /
 *       3
 *
 *   Input:  root = [1, null, 2, 3]
 *   Output: [1, 3, 2]
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InorderTraversal {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    public static List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            // Go as far left as possible
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }

            // Process the node
            curr = stack.pop();
            result.add(curr.val);

            // Now explore the right subtree
            curr = curr.right;
        }

        return result;
    }

    public static void main(String[] args) {
        // Test case 1:  1 -> right -> 2, 2 -> left -> 3
        TreeNode root1 = new TreeNode(1);
        root1.right = new TreeNode(2);
        root1.right.left = new TreeNode(3);
        System.out.println("Test 1: " + inorderTraversal(root1));
        // Expected: [1, 3, 2]

        // Test case 2 — empty tree
        System.out.println("Test 2: " + inorderTraversal(null));
        // Expected: []

        // Test case 3 — single node
        TreeNode root3 = new TreeNode(1);
        System.out.println("Test 3: " + inorderTraversal(root3));
        // Expected: [1]
    }
}
