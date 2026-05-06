/**
 * Problem: Maximum Subarray Sum (Kadane's Algorithm)
 *
 * Description:
 *   Given an integer array, find the contiguous subarray with the largest sum
 *   and return that sum.
 *
 * Approach:
 *   Kadane's Algorithm — keep a running sum and reset it to the current element
 *   whenever the running sum drops below the current element. Track the max seen.
 *
 *   At each step:
 *     currentSum = max(nums[i], currentSum + nums[i])
 *     maxSum     = max(maxSum, currentSum)
 *
 * Time Complexity:  O(n) — single pass
 * Space Complexity: O(1) — only two variables needed
 *
 * Example:
 *   Input:  [-2, 1, -3, 4, -1, 2, 1, -5, 4]
 *   Output: 6  (subarray [4, -1, 2, 1])
 */
public class MaxSubarraySum {

    public static int maxSubArray(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        // Test case 1 — mixed positive and negative
        int[] nums1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Test 1: " + maxSubArray(nums1));
        // Expected: 6

        // Test case 2 — all negative
        int[] nums2 = {-3, -1, -2};
        System.out.println("Test 2: " + maxSubArray(nums2));
        // Expected: -1

        // Test case 3 — single element
        int[] nums3 = {5};
        System.out.println("Test 3: " + maxSubArray(nums3));
        // Expected: 5
    }
}
