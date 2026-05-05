/**
 * Problem: Linked List Cycle Detection
 *
 * Description:
 *   Given the head of a linked list, determine if it contains a cycle.
 *   A cycle exists if some node's next pointer points back to a previous node.
 *
 * Approach:
 *   Floyd's Cycle Detection (Tortoise and Hare algorithm).
 *   Use two pointers: slow moves one step at a time, fast moves two steps.
 *   If there is a cycle, fast will eventually catch up to slow inside the cycle.
 *   If there is no cycle, fast will reach the end (null).
 *
 * Time Complexity:  O(n) — fast pointer traverses at most 2n steps
 * Space Complexity: O(1) — only two pointers used
 *
 * Example:
 *   Input:  3 -> 2 -> 0 -> -4 -> (back to node with value 2)
 *   Output: true
 *
 *   Input:  1 -> 2 -> null
 *   Output: false
 */
public class LinkedListCycle {

    // Singly linked list node definition
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
            this.next = null;
        }
    }

    public static boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true; // cycle detected
            }
        }

        return false; // fast reached the end — no cycle
    }

    public static void main(String[] args) {
        // Test case 1 — list with a cycle
        ListNode head1 = new ListNode(3);
        head1.next = new ListNode(2);
        head1.next.next = new ListNode(0);
        head1.next.next.next = new ListNode(-4);
        head1.next.next.next.next = head1.next; // cycle back to node 2
        System.out.println("Test 1 (cycle): " + hasCycle(head1));
        // Expected: true

        // Test case 2 — list without a cycle
        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(2);
        System.out.println("Test 2 (no cycle): " + hasCycle(head2));
        // Expected: false

        // Test case 3 — single node, no cycle
        ListNode head3 = new ListNode(1);
        System.out.println("Test 3 (single node): " + hasCycle(head3));
        // Expected: false
    }
}
