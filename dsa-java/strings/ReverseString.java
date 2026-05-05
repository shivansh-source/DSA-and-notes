/**
 * Problem: Reverse String
 *
 * Description:
 *   Reverse a string in-place (reverse the character array).
 *
 * Approach:
 *   Two-pointer technique — place one pointer at the start and one at the end.
 *   Swap the characters at both pointers, then move them toward the center.
 *   Stop when the pointers meet in the middle.
 *
 * Time Complexity:  O(n) — each character is visited once
 * Space Complexity: O(1) — in-place, no extra space needed
 *
 * Example:
 *   Input:  ['h','e','l','l','o']
 *   Output: ['o','l','l','e','h']
 */
import java.util.Arrays;

public class ReverseString {

    public static void reverseString(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while (left < right) {
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            left++;
            right--;
        }
    }

    public static void main(String[] args) {
        // Test case 1
        char[] s1 = {'h', 'e', 'l', 'l', 'o'};
        reverseString(s1);
        System.out.println("Test 1: " + Arrays.toString(s1));
        // Expected: [o, l, l, e, h]

        // Test case 2
        char[] s2 = {'H', 'a', 'n', 'n', 'a', 'h'};
        reverseString(s2);
        System.out.println("Test 2: " + Arrays.toString(s2));
        // Expected: [h, a, n, n, a, H]

        // Test case 3 — single character
        char[] s3 = {'a'};
        reverseString(s3);
        System.out.println("Test 3: " + Arrays.toString(s3));
        // Expected: [a]
    }
}
