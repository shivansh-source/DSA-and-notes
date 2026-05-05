/**
 * Problem: Valid Anagram
 *
 * Description:
 *   Given two strings s and t, return true if t is an anagram of s.
 *   An anagram uses the same characters in the same frequencies, just reordered.
 *
 * Approach:
 *   Use a frequency count array of size 26 (for lowercase English letters).
 *   Increment count for each character in s, decrement for each in t.
 *   If all counts are zero at the end, the strings are anagrams.
 *
 * Time Complexity:  O(n) — two passes through the strings
 * Space Complexity: O(1) — fixed-size array of 26 integers
 *
 * Example:
 *   Input:  s = "anagram", t = "nagaram"
 *   Output: true
 *
 *   Input:  s = "rat", t = "car"
 *   Output: false
 */
public class ValidAnagram {

    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] count = new int[26];

        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }
        for (char c : t.toCharArray()) {
            count[c - 'a']--;
        }

        for (int freq : count) {
            if (freq != 0) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        // Test case 1
        System.out.println("Test 1: " + isAnagram("anagram", "nagaram"));
        // Expected: true

        // Test case 2
        System.out.println("Test 2: " + isAnagram("rat", "car"));
        // Expected: false

        // Test case 3 — empty strings
        System.out.println("Test 3: " + isAnagram("", ""));
        // Expected: true
    }
}
