package 面试题目;

import java.util.ArrayList;
import java.util.List;

public class 矩阵 {

    // https://leetcode.cn/problems/set-matrix-zeroes/?envType=study-plan-v2&envId=top-100-liked
    public void setZeroes(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[] row = new boolean[rows];
        boolean[] col = new boolean[cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 0) {
                    row[i] = true;
                    col[j] = true;
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (row[i] || col[j]) {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    // https://leetcode.cn/problems/spiral-matrix/?envType=study-plan-v2&envId=top-100-liked
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> ans = new ArrayList<>();

        // 边界检查：如果矩阵为空或没有元素，则直接返回空列表
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return ans;

        int m = matrix.length;      // 行数
        int n = matrix[0].length;   // 列数

        boolean[][] visited = new boolean[m][n];  // 记录访问过的元素位置

        int total = m * n;          // 总元素个数

        int row = 0;                // 当前行位置
        int col = 0;                // 当前列位置
        int[][] directions = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};  // 右、下、左、上四个方向

        int directionIndex = 0;     // 当前移动方向索引
        
        for (int i = 0; i < total; i++) {
            ans.add(matrix[row][col]);              // 添加当前位置元素到结果列表
            visited[row][col] = true;               // 标记当前位置为已访问

            // 计算下一个位置坐标
            int nextRow = row + directions[directionIndex][0];
            int nextCol = col + directions[directionIndex][1];

            // 检查是否越界或已访问过，如果是则改变方向
            if (nextRow < 0 || nextRow >= m || nextCol < 0 || nextCol >= n || visited[nextRow][nextCol]) {
                directionIndex = (directionIndex + 1) % 4;  // 转向下一个方向
                // 重新计算新方向下的下一个位置
                nextRow = row + directions[directionIndex][0];
                nextCol = col + directions[directionIndex][1];
            }
            
            row = nextRow;  // 更新当前行位置
            col = nextCol;  // 更新当前列位置
        }
        return ans;
    }

    public static void main(String[] args) {
        矩阵 matrix = new 矩阵();
        int[][] matrix1 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        List<Integer> result = matrix.spiralOrder(matrix1);
        System.out.println(result); // 输出: [1, 2, 3, 6, 9, 8, 7, 4, 5]
    }

}
