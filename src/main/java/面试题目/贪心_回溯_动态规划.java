package 面试题目;

import java.util.*;

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

    // 动态规划


    // 回溯
    // https://assets.leetcode.cn/solution-static/46/fig14.PNG

    /**
     * 生成数组的所有全排列
     *
     * @param nums 输入的整数数组
     * @return 包含所有全排列的二维列表
     */
    public List<List<Integer>> permute(int[] nums) {
        // 创建结果列表，用于存储所有的排列组合
        List<List<Integer>> res = new ArrayList<>();
        // 创建路径列表，用于存储当前正在构建的排列
        List<Integer> path = new ArrayList<>();

        // 将输入数组的所有元素添加到路径列表中
        for (int num : nums) {
            path.add(num);
        }

        // 调用回溯函数开始生成全排列
        backTrack(path, res, 0, nums.length);

        // 返回所有排列的结果
        return res;
    }

    /**
     * 回溯函数，用于递归生成全排列
     *
     * @param path 当前正在构建的排列路径
     * @param res  存储所有排列结果的列表
     * @param step 当前处理的位置（起始索引）
     * @param n    数组的长度
     */
    private void backTrack(List<Integer> path, List<List<Integer>> res, int step, int n) {
        // 如果已经处理完所有位置，说明找到一个完整的排列
        if (step == n) {
            // 将当前路径的一个副本添加到结果列表中
            res.add(new ArrayList<>(path));
            return; // 结束当前递归分支
        }

        // 从当前位置开始，尝试每一个可能的元素
        for (int i = step; i < n; i++) {
            // 交换当前元素和第first个元素，固定当前元素在first位置
            Collections.swap(path, step, i);

            // 递归处理下一个位置
            backTrack(path, res, step + 1, n);

            // 回溯：撤销之前的交换，恢复原始状态，以便尝试其他可能性
            Collections.swap(path, step, i);
        }
    }

    /**
     * 生成数组的所有子集（幂集）
     *
     * @param nums 输入的整数数组
     * @return 包含所有子集的二维列表
     */
    public List<List<Integer>> subsets(int[] nums) {
        // 获取数组长度
        int len = nums.length;
        // 创建结果列表，用于存储所有的子集
        List<List<Integer>> ans = new ArrayList<>();

        // 如果数组为空，直接返回空结果
        if (len == 0) return ans;
        
        // 使用双端队列作为栈来存储当前正在构建的子集
        Deque<Integer> stack = new ArrayDeque<>();
        // 调用回溯函数开始生成所有子集
        backTrack02(0, len, stack, 0, ans, nums);

        // 返回所有子集的结果
        return ans;
    }

    /**
     * 回溯函数，用于递归生成所有子集
     *
     * @param start 当前考虑的起始位置（避免重复选择）
     * @param len   数组的总长度
     * @param stack 用于存储当前子集的栈结构
     * @param depth 当前子集已选择的元素个数
     * @param ans   存储所有子集结果的列表
     * @param nums  输入的整数数组
     */
    // step ， 深度n
    // 结果res ， 变化的东西如 nums 或者 List<Integer> path

    // 新添加的参数：
    // start ， 搜索的起始位置，  一个堆栈
    private void backTrack02(int start, int len, Deque<Integer> stack,
                             int depth, List<List<Integer>> ans, int[] nums) {
        // 将当前栈中的元素作为一个子集添加到结果中
        // 每次递归调用都会执行这一步，确保所有可能的子集都被收集
        ans.add(new ArrayList<>(stack));
        
        // 如果已经达到最大深度（选择了所有元素），结束当前递归分支
        // 这是一个剪枝条件，避免不必要的递归
        if (depth == len) {
            return;
        }
        
        // 从起始位置开始，尝试添加每一个可能的元素
        // 通过控制start参数避免重复选择，确保生成的是子集而非排列
        for (int i = start; i < len; i++) {
            // 将当前元素压入栈中，加入当前子集
            stack.push(nums[i]);
            // 递归处理下一个位置，深度加1
            // i+1确保不会重复选择同一个元素
            backTrack02(i + 1, len, stack, depth + 1, ans, nums);
            // 回溯：弹出刚才添加的元素，恢复栈的状态
            // 这样可以在同一层级尝试其他元素的选择
            stack.pop();
        }
    }

}
