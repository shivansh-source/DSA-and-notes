/**
 * Problem: Valid Parentheses
 *
 * Description:
 *   Given a string containing only '(', ')', '{', '}', '[', ']', determine if
 *   the input string is valid. A string is valid if:
 *   - Open brackets are closed by the same type of bracket.
 *   - Open brackets are closed in the correct order.
 *
 * Approach:
 *   Use a stack. For every opening bracket, push it onto the stack.
 *   For every closing bracket, check if the top of the stack is the matching
 *   opening bracket. If not, the string is invalid.
 *   At the end, the stack must be empty for the string to be valid.
 *
 * Time Complexity:  O(n) — single pass through the string
 * Space Complexity: O(n) — stack can hold up to n/2 elements
 *
 * Example:
 *   Input:  "()[]{}"
 *   Output: true
 *
 *   Input:  "([)]"
 *   Output: false
 */
import java.util.Stack;

public class ValidParentheses {

    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            // Push opening brackets onto the stack
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else {
                // If stack is empty, no matching opening bracket
                if (stack.isEmpty()) return false;

                char top = stack.pop();
                if (c == ')' && top != '(') return false;
                if (c == '}' && top != '{') return false;
                if (c == ']' && top != '[') return false;
            }
        }

        return stack.isEmpty(); // valid only if all brackets are matched
    }

    public static void main(String[] args) {
        // Test case 1
        System.out.println("Test 1: " + isValid("()[]{}"));
        // Expected: true

        // Test case 2
        System.out.println("Test 2: " + isValid("([)]"));
        // Expected: false

        // Test case 3
        System.out.println("Test 3: " + isValid("{[]}"));
        // Expected: true

        // Test case 4 — single opening bracket
        System.out.println("Test 4: " + isValid("("));
        // Expected: false
    }
}
