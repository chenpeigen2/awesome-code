package lee.pkg20220708;

public class Solution {
    //    https://leetcode.cn/problems/minimum-cost-to-move-chips-to-the-same-position/

    /**
     * 相当于8⃣️奇偶数全部聚集到一起 跳2的话 ，跳1就是min了
     */
    public int minCostToMoveChips(int[] position) {
        int odd = 0, even = 0;
        for (int i : position) {
            if ((i & 1) != 0) {
                odd++;
            } else {
                even++;
            }
        }
        return Math.min(odd, even);
    }
}
