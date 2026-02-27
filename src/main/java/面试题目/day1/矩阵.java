package 面试题目.day1;

import java.util.ArrayList;
import java.util.List;

public class 矩阵 {

    // https://leetcode.cn/problems/set-matrix-zeroes/?envType=study-plan-v2&envId=top-100-liked
    // 不能用一个set去做状态记录
    /**
     * 将矩阵中所有包含0的行和列都设置为0
     * 
     * 解题思路：
     * 1. 使用两个布尔数组分别记录哪些行和列需要被置零
     * 2. 第一次遍历矩阵，标记所有包含0的行和列
     * 3. 第二次遍历矩阵，根据标记将对应的行列全部置零
     * 
     * 时间复杂度：O(m*n)，其中m是行数，n是列数
     * 空间复杂度：O(m+n)，用于存储行和列的标记数组
     * 
     * @param matrix 输入的二维整数矩阵
     */
    public void setZeroes(int[][] matrix) {
        int rows = matrix.length;           // 获取矩阵行数
        int cols = matrix[0].length;        // 获取矩阵列数
        
        // 创建行和列的标记数组，初始值都是false
        boolean[] row = new boolean[rows];  // 标记需要置零的行
        boolean[] col = new boolean[cols];  // 标记需要置零的列
        
        // 第一遍遍历：找出所有包含0的行列位置并标记
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 0) {    // 发现0元素
                    row[i] = true;          // 标记该行需要置零
                    col[j] = true;          // 标记该列需要置零
                }
            }
        }
        
        // 第二遍遍历：根据标记将对应行列的所有元素置零
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // 如果当前行或当前列被标记为需要置零
                if (row[i] || col[j]) {
                    matrix[i][j] = 0;       // 将该位置置零
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
            if (nextRow < 0 || nextRow >= m || nextCol < 0 || nextCol >= n
                    || visited[nextRow][nextCol]) { // 触发 visit检查的是nextRow, nextCol
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

        // 步骤1: 上下翻转矩阵， j = row -1 （记住这个）
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

    // https://leetcode.cn/problems/search-a-2d-matrix-ii/description/?envType=study-plan-v2&envId=top-100-liked
    /**
     * 在排序矩阵中搜索目标值
     * 
     * 解题思路：
     * 利用矩阵的特性：每行从左到右递增，每列从上到下递增
     * 从矩阵的右上角开始搜索（第0行，最后一列）
     * - 如果当前元素等于目标值，返回true
     * - 如果当前元素大于目标值，说明目标值不可能在当前列，向左移动一列
     * - 如果当前元素小于目标值，说明目标值不可能在当前行，向下移动一行
     * 
     * 时间复杂度：O(m+n)，其中m是行数，n是列数
     * 空间复杂度：O(1)，只使用了常数级别的额外空间
     * 
     * @param matrix 排序矩阵，满足每行从左到右递增，每列从上到下递增
     * @param target 要搜索的目标值
     * @return 如果找到目标值返回true，否则返回false
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;              // 获取矩阵行数
        int n = matrix[0].length;           // 获取矩阵列数
        
        // 从右上角开始搜索：第0行，最后一列
        for (int row = 0, col = n - 1; row >= 0 && row < m && col >= 0 && col < n;) {
            if (matrix[row][col] == target) {
                // 找到目标值，直接返回true
                return true;
            } else if (matrix[row][col] > target) {
                // 当前值大于目标值，由于列是递增的，目标值不可能在当前列
                // 向左移动一列继续搜索
                col--;
            } else {
                // 当前值小于目标值，由于行是递增的，目标值不可能在当前行
                // 向下移动一行继续搜索
                row++;
            }
        }
        // 遍历完所有可能位置都没找到目标值，返回false
        return false;
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
