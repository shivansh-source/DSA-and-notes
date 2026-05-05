/**
 * Problem: Single Number
 *
 * Description:
 *   Given a non-empty array of integers where every element appears twice
 *   except for one, find that single element.
 *
 * Approach:
 *   XOR (exclusive OR) — XOR has two key properties:
 *   1. a XOR a = 0  (same values cancel out)
 *   2. a XOR 0 = a  (XOR with 0 returns the value)
 *
 *   XOR all elements together. Pairs will cancel out, leaving only the
 *   single number.
 *
 * Time Complexity:  O(n) — single pass
 * Space Complexity: O(1) — only one variable needed
 *
 * Example:
 *   Input:  [4, 1, 2, 1, 2]
 *   Output: 4
 *
 *   Explanation: 4 ^ 1 ^ 2 ^ 1 ^ 2 = 4 ^ (1^1) ^ (2^2) = 4 ^ 0 ^ 0 = 4
 */
public class SingleNumber {

    public static int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num; // XOR accumulates; pairs cancel out
        }
        return result;
    }

    public static void main(String[] args) {
        // Test case 1
        int[] nums1 = {4, 1, 2, 1, 2};
        System.out.println("Test 1: " + singleNumber(nums1));
        // Expected: 4

        // Test case 2
        int[] nums2 = {2, 2, 1};
        System.out.println("Test 2: " + singleNumber(nums2));
        // Expected: 1

        // Test case 3 — single element array
        int[] nums3 = {7};
        System.out.println("Test 3: " + singleNumber(nums3));
        // Expected: 7
    }
}
