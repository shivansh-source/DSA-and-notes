/**
 * Problem: Reverse Linked List
 *
 * Description:
 *   Given the head of a singly linked list, reverse the list and return the
 *   new head.
 *
 * Approach:
 *   Iterative approach using three pointers: prev, current, next.
 *   At each step, reverse the current node's pointer to point to prev,
 *   then advance all three pointers one step forward.
 *
 *   Before: null <- [prev]   [curr] -> [next] -> ...
 *   After:  null <- [prev] <- [curr]   [next] -> ...
 *
 * Time Complexity:  O(n) — single pass through the list
 * Space Complexity: O(1) — only three pointer variables
 *
 * Example:
 *   Input:  1 -> 2 -> 3 -> 4 -> 5 -> null
 *   Output: 5 -> 4 -> 3 -> 2 -> 1 -> null
 */
public class ReverseLinkedList {

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    public static ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next; // save next node
            curr.next = prev;          // reverse the link
            prev = curr;               // advance prev
            curr = next;               // advance curr
        }

        return prev; // prev is now the new head
    }

    // Helper to print a linked list
    public static void printList(ListNode head) {
        StringBuilder sb = new StringBuilder();
        while (head != null) {
            sb.append(head.val);
            if (head.next != null) sb.append(" -> ");
            head = head.next;
        }
        System.out.println(sb.toString());
    }

    public static void main(String[] args) {
        // Test case 1
        ListNode head1 = new ListNode(1);
        head1.next = new ListNode(2);
        head1.next.next = new ListNode(3);
        head1.next.next.next = new ListNode(4);
        head1.next.next.next.next = new ListNode(5);
        System.out.print("Test 1: ");
        printList(reverseList(head1));
        // Expected: 5 -> 4 -> 3 -> 2 -> 1

        // Test case 2 — two nodes
        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(2);
        System.out.print("Test 2: ");
        printList(reverseList(head2));
        // Expected: 2 -> 1

        // Test case 3 — single node
        ListNode head3 = new ListNode(1);
        System.out.print("Test 3: ");
        printList(reverseList(head3));
        // Expected: 1
    }
}
