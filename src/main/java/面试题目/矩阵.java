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

    /**
     * 将矩阵顺时针旋转90度
     * 实现思路：
     * 1. 先将矩阵上下翻转（第一行与最后一行交换，第二行与倒数第二行交换...）
     * 2. 再沿主对角线进行镜像翻转（交换matrix[i][j]和matrix[j][i]）
     * 这样就能实现矩阵的90度顺时针旋转
     */
    // https://leetcode.cn/problems/rotate-image/solutions/526980/xuan-zhuan-tu-xiang-by-leetcode-solution-vu3m/?envType=study-plan-v2&envId=top-100-liked
    public void rotate(int[][] matrix) {
        int row = matrix.length;
        int col = matrix[0].length;

        // 步骤1: 上下翻转矩阵
        for (int i = 0, j = row - 1; i < j; i++, j--) {
            for (int co = 0; co < col; co++) {
                int temp = matrix[i][co];
                matrix[i][co] = matrix[j][co];
                matrix[j][co] = temp;
            }
        }

        // 步骤2: 沿主对角线镜像翻转（转置矩阵）
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < i; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
    }


    public boolean searchMatrix(int[][] matrix, int target) {
        // 从右上角开始搜索，利用矩阵的有序性质
        // 矩阵每行从左到右升序，每列从上到下升序
        int row = matrix.length;
        int col = matrix[0].length;
        int i = 0;           // 起始行：第0行
        int j = col - 1;     // 起始列：最后一列

        while (i < row && j >= 0) {
            if (matrix[i][j] == target) {
                return true;  // 找到目标值
            } else if (matrix[i][j] > target) {
                j--;          // 当前值大于目标值，向左移动（排除当前列）
            } else {
                i++;          // 当前值小于目标值，向下移动（排除当前行）
            }
        }
        return false;  // 未找到目标值
    }


    public static void main(String[] args) {
        矩阵 matrix = new 矩阵();
        int[][] matrix1 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        List<Integer> result = matrix.spiralOrder(matrix1);
        System.out.println(result); // 输出: [1, 2, 3, 6, 9, 8, 7, 4, 5]


        int[][] matrix2 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        matrix.rotate(matrix2);
        for (int[] row : matrix2) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

}
