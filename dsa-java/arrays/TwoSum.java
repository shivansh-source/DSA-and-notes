/**
 * Problem: Two Sum
 *
 * Description:
 *   Given an array of integers and a target sum, return the indices of the
 *   two numbers that add up to the target. Each input has exactly one solution.
 *
 * Approach:
 *   Use a hash map to store each number and its index as we iterate.
 *   For each element, check if (target - element) already exists in the map.
 *   If yes, we found the pair. If no, store the current element in the map.
 *   This avoids the O(n^2) brute-force nested loop.
 *
 * Time Complexity:  O(n) — single pass through the array
 * Space Complexity: O(n) — hash map can store up to n elements
 *
 * Example:
 *   Input:  nums = [2, 7, 11, 15], target = 9
 *   Output: [0, 1]  (because nums[0] + nums[1] = 2 + 7 = 9)
 */
import java.util.Arrays;
import java.util.HashMap;

public class TwoSum {

    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }

        return new int[]{}; // no solution found (per problem, this won't happen)
    }

    public static void main(String[] args) {
        // Test case 1
        int[] nums1 = {2, 7, 11, 15};
        int target1 = 9;
        System.out.println("Test 1: " + Arrays.toString(twoSum(nums1, target1)));
        // Expected: [0, 1]

        // Test case 2
        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        System.out.println("Test 2: " + Arrays.toString(twoSum(nums2, target2)));
        // Expected: [1, 2]

        // Test case 3
        int[] nums3 = {3, 3};
        int target3 = 6;
        System.out.println("Test 3: " + Arrays.toString(twoSum(nums3, target3)));
        // Expected: [0, 1]
    }
}
