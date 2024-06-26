package hot100;

public class 贪心 {
    //    https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/submissions/518165430/?envType=study-plan-v2&envId=top-100-liked
    public int maxProfit(int[] prices) {
        int min = prices[0];
        int ans = 0;
        for (int price : prices) {
            ans = Math.max(ans, price - min);
            min = Math.min(min, price);
        }
        return ans;
    }

    //    https://leetcode.cn/problems/jump-game/?envType=study-plan-v2&envId=top-100-liked
    // 如果某一个作为 起跳点 的格子可以跳跃的距离是 3，那么表示后面 3 个格子都可以作为 起跳点
    // 可以对每一个能作为 起跳点 的格子都尝试跳一次，把 能跳到最远的距离 不断更新
    // 如果可以一直跳到最后，就成功了
    public boolean canJump(int[] nums) {
        int k = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > k) return false;
            k = Math.max(k, i + nums[i]);
        }
        return true;
    }

    //    https://leetcode.cn/problems/jump-game-ii/?envType=study-plan-v2&envId=top-100-liked
    public int jump(int[] nums) {
        int steps = 0;         // 记录所用步数
        int maxPosition = 0;         // 记录在边界范围内，能跳跃的最远位置的下标
        int end = 0;         // 记录当前能跳跃到的位置的边界下标
        for (int i = 0; i < nums.length - 1; i++) {
            // 继续往下遍历，统计边界范围内，哪一格能跳得更远，每走一步就更新一次能跳跃的最远位置下标
            // 其实就是在统计下一步的最优情况
            maxPosition = Math.max(maxPosition, i + nums[i]);
            // 如果到达了边界，那么一定要跳了，下一跳的边界下标就是之前统计的最优情况maxPosition，并且步数加1
            if (i == end) {
                end = maxPosition;
                steps++;
            }
        }

        return steps;
    }
}
