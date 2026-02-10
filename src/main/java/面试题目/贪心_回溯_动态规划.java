package 面试题目;

import java.util.ArrayList;
import java.util.List;

public class 贪心_回溯_动态规划 {

    // https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/?envType=study-plan-v2&envId=top-100-liked
    public int maxProfit(int[] prices) {
        // 初始化最低价格为第一天的价格
        int min = prices[0];
        // 初始化最大利润为0
        int ans = 0;
        // 从第二天开始遍历价格数组
        for (int i = 1; i < prices.length; i++) {
            // 计算当前价格与最低价格的差值，并更新最大利润
            ans = Math.max(ans, prices[i] - min);
            // 更新最低价格
            min = Math.min(min, prices[i]);
        }
        // 返回最大利润
        return ans;
    }

    /**
     * 判断是否能够跳跃到最后一个下标
     *
     * @param nums 非负整数数组，每个元素表示在该位置可以跳跃的最大长度
     * @return 如果可以到达最后一个下标返回true，否则返回false
     */
    // https://leetcode.cn/problems/jump-game/?envType=study-plan-v2&envId=top-100-liked
    public boolean canJump(int[] nums) {
        // k表示当前能够到达的最远位置
        int k = 0;

        // 遍历数组中的每一个位置
        for (int i = 0; i < nums.length; i++) {
            // 如果当前位置i超过了当前能够到达的最远位置k，
            // 说明无法继续向前跳跃，直接返回false
            if (i > k) {
                return false;
            }

            // 更新能够到达的最远位置：
            // 比较当前记录的最远位置k和从位置i能跳到的最远位置(i + nums[i])
            k = Math.max(k, i + nums[i]);
        }

        // 如果遍历完成都没有提前返回false，说明可以到达最后位置
        return true;
    }

    /**
     * 计算跳跃游戏所需的最少跳跃次数
     *
     * @param nums 非负整数数组，每个元素表示在该位置可以跳跃的最大长度
     * @return 到达最后一个位置所需的最少跳跃次数
     */
    public int jump(int[] nums) {
        // 获取数组长度
        int n = nums.length;
        // 记录跳跃次数
        int step = 0;
        // 记录当前能够到达的最远位置
        int maxPosition = 0;
        // end表示上一次跳跃能够到达的边界位置
        // 当遍历到end位置时，说明需要进行下一次跳跃
        int end = 0;

        // 遍历数组，不需要遍历到最后一个元素（因为到达最后一个位置后不需要再跳跃）
        for (int i = 0; i < n - 1; i++) {
            // 更新当前能够到达的最远位置
            maxPosition = Math.max(maxPosition, i + nums[i]);

            // 如果遍历到了上一次跳跃的边界位置
            if (i == end) {
                // 增加跳跃次数
                step++;
                // 更新跳跃边界为当前能够到达的最远位置
                end = maxPosition;
            }
        }

        // 返回最少跳跃次数
        return step;
    }

    // https://leetcode.cn/problems/partition-labels/description/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 将字符串分割成尽可能多的片段，使得每个字母只出现在一个片段中
     *
     * @param s 输入的字符串
     * @return 每个片段的长度组成的列表
     */
    public List<Integer> partitionLabels(String s) {
        // 创建结果列表，用于存储每个片段的长度
        List<Integer> result = new ArrayList<>();

        // 创建数组记录每个字符最后出现的位置
        // 数组大小为26，对应26个小写字母
        int[] lastSee = new int[26];

        // 遍历字符串，记录每个字符最后出现的位置
        for (int i = 0; i < s.length(); i++) {
            // 将字符转换为数组索引(0-25)，并记录其最后出现的位置
            lastSee[s.charAt(i) - 'a'] = i;
        }

        // start表示当前片段的起始位置
        // end表示当前片段的结束位置（即该片段内所有字符最后出现位置的最大值）
        int start = 0, end = 0;

        // 遍历字符串的每个字符
        for (int i = 0; i < s.length(); i++) {
            // 更新当前片段的结束位置为当前字符最后出现位置和原结束位置的最大值
            end = Math.max(end, lastSee[s.charAt(i) - 'a']);

            // 如果当前位置等于结束位置，说明找到了一个完整的片段
            if (i == end) {
                // 将当前片段的长度添加到结果列表中
                result.add(end - start + 1);
                // 更新下一个片段的起始位置
                start = i + 1;
            }
        }

        // 返回包含所有片段长度的列表
        return result;
    }

}
