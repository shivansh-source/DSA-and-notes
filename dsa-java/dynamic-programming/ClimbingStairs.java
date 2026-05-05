/**
 * Problem: Climbing Stairs
 *
 * Description:
 *   You are climbing a staircase with n steps. Each time you can climb
 *   1 or 2 steps. In how many distinct ways can you climb to the top?
 *
 * Approach:
 *   Dynamic programming — this is essentially the Fibonacci sequence.
 *   To reach step i, you either came from step i-1 (1 step) or step i-2 (2 steps).
 *   So: dp[i] = dp[i-1] + dp[i-2]
 *
 *   We optimize space by only keeping the last two values.
 *
 * Time Complexity:  O(n) — single loop from 3 to n
 * Space Complexity: O(1) — only two variables instead of an array
 *
 * Example:
 *   Input:  n = 2
 *   Output: 2  (1+1 or 2)
 *
 *   Input:  n = 3
 *   Output: 3  (1+1+1, 1+2, 2+1)
 */
public class ClimbingStairs {

    public static int climbStairs(int n) {
        if (n <= 2) return n;

        int prev2 = 1; // ways to reach step 1
        int prev1 = 2; // ways to reach step 2

        for (int i = 3; i <= n; i++) {
            int curr = prev1 + prev2;
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }

    public static void main(String[] args) {
        // Test case 1
        System.out.println("Test 1 (n=2): " + climbStairs(2));
        // Expected: 2

        // Test case 2
        System.out.println("Test 2 (n=3): " + climbStairs(3));
        // Expected: 3

        // Test case 3
        System.out.println("Test 3 (n=5): " + climbStairs(5));
        // Expected: 8

        // Test case 4
        System.out.println("Test 4 (n=10): " + climbStairs(10));
        // Expected: 89
    }
}
