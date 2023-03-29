package lee.pkg20221207;

public class Solution {

    //    https://leetcode.cn/problems/equal-sum-arrays-with-minimum-number-of-operations/solutions/2009786/mei-xiang-ming-bai-yi-ge-dong-hua-miao-d-ocuu/
    public int minOperations(int[] nums1, int[] nums2) {
        /**
         * 把 nums1\textit{nums}_1nums
         * 1
         * ​
         *   的所有数都改成 666，nums2\textit{nums}_2nums
         * 2
         * ​
         *   的所有数都改成 111，如果 nums1\textit{nums}_1nums
         * 1
         * ​
         *   的元素和仍然小于 nums2\textit{nums}_2nums
         * 2
         * ​
         *   的元素和，则说明无论怎么操作，都无法使这两个数组的元素和相等。
         */
        if (6 * nums1.length < nums2.length || 6 * nums2.length < nums1.length)
            return -1; // 优化
        // int d = Arrays.stream(nums2).sum() - Arrays.stream(nums1).sum();
        int d = 0; // 数组元素和的差，我们要让这个差变为 0
        for (int x : nums2) d += x;
        for (int x : nums1) d -= x;
        if (d < 0) {
            d = -d;
            int[] tmp = nums1;
            nums1 = nums2;
            nums2 = tmp; // 交换，统一让 nums1 的数变大，nums2 的数变小
        }
        // nums1 < nums2
        // 统计变化到6或变化到1所需要的值的个数
        int[] cnt = new int[6]; // 统计每个数的最大变化量
        for (int x : nums1) ++cnt[6 - x]; // nums1 的变成 6
        for (int x : nums2) ++cnt[x - 1]; // nums2 的变成 1
        for (int i = 5, ans = 0; ; --i) { // 从大到小枚举最大变化量 5 4 3 2 1
            if (i * cnt[i] >= d) // 可以让 d 变为 0
                return ans + (d + i - 1) / i; // excellent idea
            ans += cnt[i]; // 需要所有最大变化量为 i 的数
            d -= i * cnt[i];
        }
    }
}
