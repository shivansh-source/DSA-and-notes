/**
 * Problem: Implement Queue Using Stacks
 *
 * Description:
 *   Implement a first-in-first-out (FIFO) queue using only two stacks.
 *   The queue should support: push, pop, peek, and empty operations.
 *
 * Approach:
 *   Use two stacks: inbox and outbox.
 *   - push: always add to inbox.
 *   - pop/peek: if outbox is empty, move all elements from inbox to outbox
 *     (this reverses the order, making the oldest element on top).
 *     Then pop/peek from outbox.
 *   This gives amortized O(1) time for all operations.
 *
 * Time Complexity:
 *   push: O(1)
 *   pop:  O(1) amortized (each element is moved at most once)
 *   peek: O(1) amortized
 *   empty: O(1)
 * Space Complexity: O(n) — total elements across both stacks
 *
 * Example:
 *   push(1), push(2), peek() -> 1, pop() -> 1, empty() -> false
 */
import java.util.Stack;

public class ImplementQueueUsingStacks {

    static class MyQueue {
        private Stack<Integer> inbox;  // for push
        private Stack<Integer> outbox; // for pop/peek

        public MyQueue() {
            inbox = new Stack<>();
            outbox = new Stack<>();
        }

        public void push(int x) {
            inbox.push(x);
        }

        public int pop() {
            refill();
            return outbox.pop();
        }

        public int peek() {
            refill();
            return outbox.peek();
        }

        public boolean empty() {
            return inbox.isEmpty() && outbox.isEmpty();
        }

        // Move all items from inbox to outbox if outbox is empty
        private void refill() {
            if (outbox.isEmpty()) {
                while (!inbox.isEmpty()) {
                    outbox.push(inbox.pop());
                }
            }
        }
    }

    public static void main(String[] args) {
        MyQueue queue = new MyQueue();

        queue.push(1);
        queue.push(2);
        System.out.println("Peek: " + queue.peek());   // Expected: 1
        System.out.println("Pop:  " + queue.pop());    // Expected: 1
        System.out.println("Empty: " + queue.empty()); // Expected: false

        queue.pop();
        System.out.println("Empty: " + queue.empty()); // Expected: true
    }
}
