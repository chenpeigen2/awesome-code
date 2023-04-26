package lee.pkg20230426;

public class Solution {

    //    https://leetcode.cn/problems/maximum-sum-of-two-non-overlapping-subarrays/
    public int maxSumTwoNoOverlap(int[] nums, int firstLen, int secondLen) {
        return Math.max(help(nums, firstLen, secondLen), help(nums, secondLen, firstLen));
    }


    private int help(int[] nums, int firstLen, int secondLen) {
        int suml = 0;
        for (int i = 0; i < firstLen; ++i) {
            suml += nums[i];
        }
        int maxSuml = suml;
        int sumr = 0;
        for (int i = firstLen; i < firstLen + secondLen; i++) {
            sumr += nums[i];
        }
        int res = maxSuml + sumr;

        for (int i = firstLen + secondLen, j = firstLen; i < nums.length; i++, j++) {
            suml += nums[j] - nums[j - firstLen];
            maxSuml = Math.max(suml, maxSuml);
            sumr += nums[i] - nums[i - secondLen];
            res = Math.max(res, maxSuml + sumr);
        }

        return res;
    }
}
