package lee.pkg20220324;

public class Solution {
    //    https://leetcode-cn.com/problems/image-smoother/
    public int[][] imageSmoother(int[][] img) {
        int m = img.length, n = img[0].length;
        int[][] ans = new int[m][n];
        int[][] dirs = new int[][]{{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int tot = 0, cnt = 0;
                for (int[] di : dirs) {
                    // every direction
                    int nx = i + di[0], ny = j + di[1];
                    if (nx < 0 || nx >= m || ny < 0 || ny >= n) continue;
                    tot += img[nx][ny];
                    cnt++;
                }
                ans[i][j] = tot / cnt;
            }
        }

        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        int[][] arr = new int[][]{{100, 200, 100}, {200, 50, 200}, {100, 200, 100}};
        var ans = app.imageSmoother(arr);

        for (var an : ans) {
            for (var a : an) {
                System.out.print(a + " ");
            }
            System.out.println();
        }
    }
}
