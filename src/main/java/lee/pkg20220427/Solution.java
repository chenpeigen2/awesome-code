package lee.pkg20220427;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Solution {

    /**
     * 使用 BFS 进行求解：目的是构造出两个答案矩阵 res_1res
     * 1
     * ​
     * 和 res_2res
     * 2
     * ​
     * ，res_k[i][j] = trueres
     * k
     * ​
     * [i][j]=true 代表格子 (i, j)(i,j) 能够流向海域，起始将所有与海域相连的格子放入队列，然后跑一遍 BFS ，所有能够进入队列的格子均能够与海域联通。
     * <p>
     * 最后统计所有满足 res_1[i][j] = res_2[i][j] = trueres
     * 1
     * ​
     * [i][j]=res
     * 2
     * ​
     * [i][j]=true 的格子即是答案。
     * <p>
     * 作者：AC_OIer
     * 链接：<a href="https://leetcode-cn.com/problems/pacific-atlantic-water-flow/solution/by-ac_oier-do7d/">https://leetcode-cn.com/problems/pacific-atlantic-water-flow/solution/by-ac_oier-do7d/</a>
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * Interface Deque<E>
     * All Known Implementing Classes:
     * ArrayDeque, ConcurrentLinkedDeque, LinkedBlockingDeque, LinkedList
     */

    int n, m;
    int[][] g;

    //    https://leetcode-cn.com/problems/pacific-atlantic-water-flow/
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        g = heights;
        m = g.length;
        n = g[0].length;

        Deque<int[]> d1 = new ArrayDeque<>(), d2 = new ArrayDeque<>();
        boolean[][] res1 = new boolean[m][n], res2 = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || j == 0) {
                    res1[i][j] = true; // edge 边缘的陆地（front） for pacific ocean
                    d1.addLast(new int[]{i, j});
                }
                if (i == m - 1 || j == n - 1) {
                    res2[i][j] = true; // edge的边缘的陆地（tail） for atlantic ocean
                    d2.addLast(new int[]{i, j});
                }
            }
        }

        bfs(d1, res1);
        bfs(d2, res2);

        List<List<Integer>> ans = new ArrayList<>();

        // If it goes to [pacific ocean] and [atlantic ocean]
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (res1[i][j] && res2[i][j]) {
                    List<Integer> list = new ArrayList<>();
                    list.add(i);
                    list.add(j);
                    ans.add(list);
                }
            }
        }
        return ans;
    }

    int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    void bfs(Deque<int[]> d, boolean[][] res) {
        while (!d.isEmpty()) {
            int[] info = d.pollFirst();
            int x = info[0], y = info[1], t = g[x][y]; // The value We are going to evaluate
            for (int[] di : dirs) {
                int nx = x + di[0], ny = y + di[1];
                if (nx < 0 || nx >= m || ny < 0 || ny >= n) continue; // 无意义的超越边界的数据
                if (res[nx][ny] || g[nx][ny] < t) continue; // 已经是true 或者是 【从海域出发的格子是从低到高】
                d.addLast(new int[]{nx, ny});
                res[nx][ny] = true;
            }
        }
    }
}
