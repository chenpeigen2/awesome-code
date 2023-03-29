package lee.pkg20211025;

public class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        for (int i = 0; i < m; i++) {
            int l = 0;
            int r = n - 1;
            while (l < r) {
                int mid = l + r + 1 >> 1;
                if (matrix[i][mid] <= target) {
                    l = mid;
                } else {
                    r = mid - 1;
                }
            }
            if (matrix[i][l] == target) return true;
        }
        return false;
    }

    public boolean searchMatrix1(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int i = 0;
        int j = n - 1;
        while (i < m && j >= 0) {
            if (matrix[i][j] == target) {
                return true;
            }
            if (matrix[i][j] > target) {
                // 去左子树
                j--;
            } else {
//                去右子树
                i++;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] arr = new int[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 25}};
        var result = new Solution().searchMatrix1(arr, 15);
        System.out.println(result);
    }
}
