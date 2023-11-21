package lee.pkg20231121;

public class Solution {
    //    https://leetcode.cn/problems/minimum-deletions-to-make-array-beautiful/?envType=daily-question&envId=2023-11-21
    public int minDeletion(int[] nums) {
        int n = nums.length, cnt = 0;
        for (int i = 0; i < n; i++) {
//            由于每次删除元素，剩余元素都会往前移动，因此当前下标为 i−cnti - cnti−cnt。
            if ((i - cnt) % 2 == 0 // 下标为 0 2 4 6 8
                    && i + 1 < n // 处理 nums 过程中，若当前下标为偶数，
                    // 且与下一位置元素相同，那么当前元素需被删除，令 cnt 自增。
                    && nums[i] == nums[i + 1]) {
                cnt++;
            }
        }
        return (n - cnt) % 2 != 0 ? cnt + 1 : cnt;
    }

    public int minDeletion1(int[] nums) {
        int n = nums.length, cnt = 0;
        for (int i = 0; i < n - 1; i++) {
            if ((i - cnt) % 2 == 0 && nums[i] == nums[i + 1]) cnt++;
        }
        return (n - cnt) % 2 != 0 ? cnt + 1 : cnt;
    }
}
