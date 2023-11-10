package lee.pkg20231110;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/successful-pairs-of-spells-and-potions/?envType=daily-question&envId=2023-11-10
    public int[] successfulPairs(int[] spells, int[] potions, long success) {
        Arrays.sort(potions);
        int n = spells.length;
        int[] res = new int[n];
        int m = potions.length;
        for (int i = 0; i < n; i++) {
            int target = (int) Math.ceil(success / (double) spells[i]);//向上取整
            int id = binarySearch(potions, target);//二分查找第一个大于等于target的元素
            res[i] = m - id;
        }
        return res;
    }

    public int binarySearch(int[] potions, int target) {
        int left = 0;
        int right = potions.length;
        while (left < right) {
            int mid = (left + right) >> 1;
            if (potions[mid] >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
}
