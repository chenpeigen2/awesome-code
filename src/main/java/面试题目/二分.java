package 面试题目;

public class 二分 {
    /**
     * 在有序数组中查找目标值的索引，如果目标值不存在，则返回它应该被插入的位置。
     * 使用二分查找算法实现，时间复杂度为 O(log n)。
     *
     * @param nums   有序数组（升序排列，无重复元素）
     * @param target 目标值
     * @return 目标值在数组中的索引，或应插入的位置
     */
    public int searchInsert(int[] nums, int target) {
        int l = 0; // 左边界初始化为数组起始位置
        int r = nums.length - 1; // 右边界初始化为数组末尾位置
        while (l <= r) { // 当左边界小于等于右边界时继续循环
            int mid = l + r >> 1; // 计算中间位置（等价于 (l + r) / 2）
            if (nums[mid] == target) { // 如果中间元素等于目标值
                return mid; // 直接返回中间位置
            } else if (nums[mid] > target) { // 如果中间元素大于目标值
                r = mid - 1; // 调整右边界到中间位置左侧
            } else { // 如果中间元素小于目标值
                l = mid + 1; // 调整左边界到中间位置右侧
            }
        }
        return l; // 循环结束时，左边界即为目标值应插入的位置
    }

    /**
     * 在二维矩阵中搜索目标值是否存在。
     * 矩阵特性：每行从左到右递增，每列从上到下递增。
     * 将二维矩阵视为一维数组进行二分查找，提高搜索效率。
     *
     * @param matrix 二维矩阵，满足每行每列递增特性
     * @param target 目标值
     * @return 如果目标值存在于矩阵中则返回true，否则返回false
     */
    // https://leetcode.cn/problems/search-a-2d-matrix/solutions/688117/sou-suo-er-wei-ju-zhen-by-leetcode-solut-vxui/?envType=study-plan-v2&envId=top-100-liked
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;      // 获取矩阵行数
        int n = matrix[0].length;   // 获取矩阵列数
        int l = 0;                  // 初始化左边界为0（一维数组起始位置）
        int r = m * n - 1;          // 初始化右边界为m*n-1（一维数组末尾位置）

        // 二分查找主循环
        while (l <= r) {
            int mid = l + r >> 1;   // 计算中间位置（位运算等价于(l+r)/2，效率更高）

            // 将一维索引转换为二维矩阵坐标
            // mid / n 计算对应行号（整除得到行索引）
            // mid % n 计算对应列号（取余得到列索引）
            int num = matrix[mid / n][mid % n];

            // 比较中间元素与目标值
            if (num == target) {
                return true;        // 找到目标值，直接返回true
            } else if (num > target) {
                r = mid - 1;        // 中间元素大于目标值，调整右边界到左半部分
            } else {
                l = mid + 1;        // 中间元素小于目标值，调整左边界到右半部分
            }
        }

        // 循环结束未找到目标值，返回false
        return false;
    }

}
