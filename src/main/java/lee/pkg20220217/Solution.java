package lee.pkg20220217;

class Solution {
    //    https://leetcode-cn.com/problems/knight-probability-in-chessboard/submissions/
    int[][] dirs = new int[][]{{-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {-2, 1}, {-2, -1}, {2, 1}, {2, -1}};

    public double knightProbability(int n, int k, int row, int column) {
        double[][][] f = new double[n][n][k + 1];
        // 如果不走一步那这个概率将会是1
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                f[i][j][0] = 1;
            }
        }

//        若下一步的落点 (nx, ny)(nx,ny) 在棋盘内，其剩余可用步数为 p - 1p−1，则最后仍在棋盘的概率为 f[nx][ny][p - 1]f[nx][ny][p−1]，则落点 (nx, ny)(nx,ny)
//        对 f[i][j][p]f[i][j][p] 的贡献为 f[nx][ny][p - 1] \times \frac{1}{8}f[nx][ny][p−1]×

//        f[i][j][p]=∑f[nx][ny][p−1]× (1/8)
        // 如果我走一步
        for (int p = 1; p <= k; p++) {

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    for (int[] dir : dirs) {
                        int nx = dir[0] + i;
                        int ny = dir[1] + j;
//                        3
//                        2
//                        0
//                        0
                        // if nx = 3 and n = 3 ,array out of bounds
                        if (nx < 0 || ny < 0 || nx >= n || ny >= n) continue;
                        // may different way to get there
                        f[i][j][p] += f[nx][ny][p - 1] / 8;
                    }
                }
            }
        }

        return f[row][column][k];
    }
}