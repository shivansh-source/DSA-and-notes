# DSA Java

Java solutions for Data Structures and Algorithms problems, organized by topic.

---

## 📂 Organization

Each topic has its own folder. Inside each folder, every `.java` file contains **one problem**.

```
dsa-java/
├── arrays/
├── strings/
├── linkedlist/
├── stack/
├── queue/
├── trees/
├── graphs/
├── dynamic-programming/
├── hashing/
└── bit-manipulation/
```

### File Structure Convention

Each Java file follows this structure:

```java
/**
 * Problem: <Problem Name>
 *
 * Description:
 *   <Brief problem description>
 *
 * Approach:
 *   <Explanation of the approach used>
 *
 * Time Complexity:  O(...)
 * Space Complexity: O(...)
 *
 * Example:
 *   Input:  ...
 *   Output: ...
 */
public class ProblemName {
    public static void main(String[] args) {
        // test the solution
    }

    // solution method(s)
}
```

---

## 🚀 How to Run

### Prerequisites
- Java JDK 11 or higher — download from [Adoptium](https://adoptium.net/)
- Verify installation: `java -version`

### Compile and Run

```bash
# From the repository root:
javac dsa-java/arrays/TwoSum.java
java -cp dsa-java/arrays TwoSum

# Or navigate into the topic folder:
cd dsa-java/arrays
javac TwoSum.java
java TwoSum
```

### Run All Files in a Topic (Linux/macOS)

```bash
cd dsa-java/arrays
for f in *.java; do
  javac "$f" && java "${f%.java}"
  echo "---"
done
```

---

## 📋 Topics and Problems

| Topic | Problems |
|-------|----------|
| arrays | TwoSum, MaxSubarraySum |
| strings | ReverseString, ValidAnagram |
| linkedlist | LinkedListCycle, ReverseLinkedList |
| stack | ValidParentheses |
| queue | ImplementQueueUsingStacks |
| trees | InorderTraversal |
| graphs | BFS |
| dynamic-programming | ClimbingStairs |
| hashing | GroupAnagrams |
| bit-manipulation | SingleNumber |

---

## 💡 Tips

- Read the problem carefully before coding.
- Think about brute force first, then optimize.
- Always analyze time and space complexity.
- Test with edge cases: empty input, single element, negatives.
