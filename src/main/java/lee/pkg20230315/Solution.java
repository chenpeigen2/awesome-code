package lee.pkg20230315;

public class Solution {

    //    https://leetcode.cn/problems/maximal-network-rank/
    // 两座不同城市构成的城市对的网络秩定义为：与这两座城市直接相连的道路总数，这两座城市之间的道路只计算一次
    public int maximalNetworkRank(int n, int[][] roads) {
        boolean[][] connect = new boolean[n][n];
        int[] degree = new int[n];
        for (int[] v : roads) {
            connect[v[0]][v[1]] = true;
            connect[v[1]][v[0]] = true;
            degree[v[0]]++;
            degree[v[1]]++;
        }
        int maxRank = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int rank = degree[i] + degree[j] - (connect[i][j] ? 1 : 0);
                maxRank = Math.max(maxRank, rank);
            }
        }
        return maxRank;
    }
}
