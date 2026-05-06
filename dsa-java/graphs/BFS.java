/**
 * Problem: Breadth-First Search (BFS) on a Graph
 *
 * Description:
 *   Given a graph represented as an adjacency list and a starting node,
 *   perform a BFS traversal and return the order in which nodes are visited.
 *
 * Approach:
 *   Use a queue and a visited set.
 *   1. Enqueue the start node and mark it visited.
 *   2. While the queue is not empty:
 *      - Dequeue a node and add it to the result.
 *      - Enqueue all unvisited neighbors and mark them visited.
 *   This guarantees shortest path order (by number of edges) from the source.
 *
 * Time Complexity:  O(V + E) where V = vertices, E = edges
 * Space Complexity: O(V) for the queue and visited set
 *
 * Example:
 *   Graph:
 *     0 -- 1
 *     |    |
 *     2 -- 3
 *
 *   Input:  start = 0
 *   Output: [0, 1, 2, 3]
 */
import java.util.*;

public class BFS {

    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            order.add(node);

            for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }

        return order;
    }

    public static void main(String[] args) {
        // Build adjacency list for a simple undirected graph
        //   0 -- 1
        //   |    |
        //   2 -- 3
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(0, 3));
        graph.put(2, Arrays.asList(0, 3));
        graph.put(3, Arrays.asList(1, 2));

        System.out.println("BFS from 0: " + bfs(graph, 0));
        // Expected: [0, 1, 2, 3]  (order may vary for same-level nodes)

        System.out.println("BFS from 3: " + bfs(graph, 3));
        // Expected: [3, 1, 2, 0]
    }
}
