package lee.pkg20240326;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Solution {
}

//https://leetcode.cn/problems/design-graph-with-shortest-path-calculator/?envType=daily-question&envId=2024-03-26
class Graph {


    private List<int[]>[] graph;


    public Graph(int n, int[][] edges) {
        graph = new List[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        for (int[] edge : edges) {
            graph[edge[0]].add(new int[]{edge[1], edge[2]});
        }
    }

    public void addEdge(int[] edge) {
        graph[edge[0]].add(new int[]{edge[1], edge[2]});
    }

    public int shortestPath(int node1, int node2) {
        PriorityQueue<int[]> pq = new PriorityQueue<int[]>((a, b) -> a[0] - b[0]);
        int[] dist = new int[graph.length];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[node1] = 0;
        pq.offer(new int[]{0, node1});
        while (!pq.isEmpty()) {
            int[] polled = pq.poll();
            int cost = polled[0];
            int cur = polled[1];
            if (cur == node2) return cost;
            for (int[] edge : graph[cur]) {
                int nCost = edge[1];
                int nNode = edge[0];
                if (dist[nNode] > nCost + cost) {
                    dist[nNode] = nCost + cost;
                    pq.offer(new int[]{dist[nNode], nNode});
                }
            }
        }
        return -1;
    }
}