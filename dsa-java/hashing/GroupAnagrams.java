/**
 * Problem: Group Anagrams
 *
 * Description:
 *   Given an array of strings, group the anagrams together.
 *   The order of groups and the order within each group does not matter.
 *
 * Approach:
 *   For each word, compute a canonical key by sorting its characters.
 *   Anagrams will produce the same sorted key.
 *   Use a hash map: key = sorted word, value = list of words with that key.
 *
 * Time Complexity:  O(n * k log k) where n = number of words, k = max word length
 * Space Complexity: O(n * k) for the hash map
 *
 * Example:
 *   Input:  ["eat", "tea", "tan", "ate", "nat", "bat"]
 *   Output: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
 */
import java.util.*;

public class GroupAnagrams {

    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();

        for (String word : strs) {
            // Sort the characters to get a canonical key
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }

        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        // Test case 1
        String[] strs1 = {"eat", "tea", "tan", "ate", "nat", "bat"};
        System.out.println("Test 1: " + groupAnagrams(strs1));
        // Expected groups: [eat, tea, ate], [tan, nat], [bat]

        // Test case 2 — single word
        String[] strs2 = {"a"};
        System.out.println("Test 2: " + groupAnagrams(strs2));
        // Expected: [[a]]

        // Test case 3 — all empty strings
        String[] strs3 = {"", ""};
        System.out.println("Test 3: " + groupAnagrams(strs3));
        // Expected: [["", ""]]
    }
}
