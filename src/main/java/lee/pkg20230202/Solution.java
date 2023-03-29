package lee.pkg20230202;

import java.util.*;

public class Solution {
    //    https://leetcode.cn/problems/shortest-path-with-alternating-colors/

    /**
     * <a href="https://leetcode.cn/problems/shortest-path-with-alternating-colors/solution/java-99-jian-dan-yi-dong-bfs-by-joneli-3ybb/">...</a>
     *
     * @param n
     * @param redEdges
     * @param blueEdges
     * @return
     */
    public int[] shortestAlternatingPaths(int n, int[][] redEdges, int[][] blueEdges) {

        List<Integer>[] redList = new List[n];
        List<Integer>[] blueList = new List[n];
        for (int i = 0; i < n; ++i) {
            redList[i] = new ArrayList<>();
            blueList[i] = new ArrayList<>();
        }
        for (int[] e : redEdges) {
            redList[e[0]].add(e[1]);
        }
        for (int[] e : blueEdges) {
            blueList[e[0]].add(e[1]);
        }

        int[] redAns = new int[n];// `最后一步为` [红色] 时到达点i的 [最小] 步数
        int[] blueAns = new int[n];// 最后一步为 [蓝色] 时到达点i的 [最小] 步数
        for (int i = 1; i < n; i++) {// 初始化 所有 [最小] 步数全部设置成 [MAX]
            // 从 [1] 开始因为从 [点0] 到 [点0] 需要 [0] 步
            redAns[i] = Integer.MAX_VALUE;
            blueAns[i] = Integer.MAX_VALUE;
        }

        Queue<int[]> queue = new ArrayDeque<>();// 由长度为 [2] 的数组表示每个点
        // idx-0 表示到当前node，`后面要去走`的是红色/绿色
        queue.add(new int[]{0, 0});// [0] 表示下一次要走 [红色]
        queue.add(new int[]{0, 1});// [1] 表示下一次要走 [蓝色]

        int level = 0;
        while (!queue.isEmpty()) {
            level++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] curArr = queue.poll();

                int cur = curArr[0]; // 当前节点

                if (curArr[1] == 0) { // 如果这个点下一步要走 [红色]
                    for (int next : redList[cur]) {
                        if (level < redAns[next]) {
                            redAns[next] = level;
                            queue.offer(new int[]{next, 1});
                        }
                    }

                } else { // 绿色
                    for (int next : blueList[cur]) {
                        if (level < blueAns[next]) {
                            blueAns[next] = level;
                            queue.offer(new int[]{next, 0});
                        }
                    }
                }
            }

        }

        for (int i = 0; i < n; i++) {
            if (blueAns[i] < redAns[i]) {
                redAns[i] = blueAns[i];
            } else if (redAns[i] == Integer.MAX_VALUE) {
                redAns[i] = -1;
            }
        }

        return redAns;

    }
}
