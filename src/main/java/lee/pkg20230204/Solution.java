package lee.pkg20230204;

import java.util.Arrays;

public class Solution {
    /**
     * <a href="https://pic.leetcode.cn/1675089725-wbrBLD-1798.png">explain</a>
     *
     * @param coins
     * @return
     */
    //    https://leetcode.cn/problems/maximum-number-of-consecutive-values-you-can-make/solution/mei-xiang-ming-bai-yi-zhang-tu-miao-dong-7xlx/
    public int getMaximumConsecutive(int[] coins) {
        Arrays.sort(coins);

        int r = 0;
        for (int coin : coins) {
            if (coin > r + 1) break;
            r += coin;
        }
        return r + 1;
    }

    public static void main(String[] args) {
        var app = new Solution();
        // 0 1 2 4
        // 0 1 2 5 (就算这种情况，想一想)
        var ans = app.getMaximumConsecutive(new int[]{0, 1, 2, 4});
        System.out.println(ans);

    }
}
