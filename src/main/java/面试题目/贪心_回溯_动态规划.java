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

    /**
     * 寻找所有组合使得它们的和等于目标值target
     *
     * @param candidates 候选数字数组（无重复元素）
     * @param target     目标和
     * @return 所有满足条件的组合列表
     */
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        // 获取候选数组的长度
        int len = candidates.length;
        // 创建结果列表，用于存储所有满足条件的组合
        List<List<Integer>> res = new ArrayList<>();

        // 如果候选数组为空，直接返回空结果
        if (len == 0) return res;

        // 创建双端队列作为路径栈，用于存储当前正在构建的组合
        Deque<Integer> path = new ArrayDeque<>();

        // 调用深度优先搜索函数开始寻找组合
        dfs(candidates, 0, len, target, path, res);

        // 返回所有满足条件的组合
        return res;
    }

    /**
     * 深度优先搜索函数，用于递归寻找所有满足条件的组合
     *
     * @param candidates 候选数字数组
     * @param depth      当前搜索的起始位置（避免重复组合）
     * @param len        候选数组的总长度
     * @param target     剩余需要达到的目标值
     * @param path       当前正在构建的组合路径
     * @param res        存储所有满足条件组合的结果列表
     */
    // candidate target 需要转换的东西
    // len 总长度
    // depth 当前深度
    // res 结果的hold
    private void dfs(int[] candidates, int depth, int len, int target,
                     Deque<Integer> path, List<List<Integer>> res) {
        // 如果剩余目标值小于0，说明当前路径不满足条件，直接返回
        if (target < 0) return;

        // 如果剩余目标值等于0，说明找到了一个满足条件的组合
        if (target == 0) {
            // 将当前路径的一个副本添加到结果列表中
            res.add(new ArrayList<>(path));
            return; // 结束当前递归分支
        }

        // 从当前起始位置开始，尝试每一个候选数字
        for (int i = depth; i < len; i++) {
            // 将当前候选数字加入路径
            path.push(candidates[i]);

            // 递归搜索，注意起始位置仍为i（允许重复使用同一元素）
            // 目标值减去当前选择的数字
            dfs(candidates, i, len, target - candidates[i], path, res);

            // 回溯：移除刚才添加的数字，恢复路径状态
            // 以便在同一层级尝试其他候选数字
            path.pop();
        }
    }

    // 动态规划

    // https://leetcode.cn/problems/climbing-stairs/description/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 使用动态规划计算爬楼梯的不同方法数
     * <p>
     * 问题描述：每次可以爬1或2个台阶，求爬到第n阶楼梯共有多少种不同的方法
     * 解题思路：这是一个经典的斐波那契数列问题
     * - 爬到第n阶的方法数 = 爬到第(n-1)阶的方法数 + 爬到第(n-2)阶的方法数
     * - 因为可以从第(n-1)阶爬1步到达，或从第(n-2)阶爬2步到达
     *
     * @param n 楼梯的总阶数
     * @return 爬到第n阶的不同方法数
     */
    public int climbStairs(int n) {
        // 创建dp数组，dp[i]表示爬到第i阶楼梯的方法数
        int[] dp = new int[n + 1];

        // 初始条件：
        // 爬到第0阶有1种方法（不爬）
        dp[0] = 1;
        // 爬到第1阶有1种方法（爬1步）
        dp[1] = 1;

        // 状态转移方程：dp[i] = dp[i-1] + dp[i-2]
        // 从第2阶开始计算每一阶的方法数
        for (int i = 2; i <= n; i++) {
            // 当前阶数的方法数 = 前一阶的方法数 + 前两阶的方法数
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        // 返回爬到第n阶楼梯的方法数
        return dp[n];
    }

    // https://leetcode.cn/problems/pascals-triangle/submissions/628154915/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 生成杨辉三角的前numRows行
     * <p>
     * 杨辉三角的特点：
     * 1. 每一行的第一个和最后一个元素都是1
     * 2. 中间的元素等于上一行相邻两个元素的和
     * 3. 第n行有n个元素
     * <p>
     * 算法思路：
     * 1. 外层循环控制行数，从第0行到第(numRows-1)行
     * 2. 内层循环控制每行的列数，第i行有(i+1)个元素
     * 3. 对于每行的元素：
     * - 首尾元素直接设为1
     * - 中间元素通过上一行的相邻元素相加得到
     * <p>
     * 时间复杂度：O(n²)，其中n是行数
     * 空间复杂度：O(n²)，需要存储整个三角形
     *
     * @param numRows 需要生成的行数（非负整数）
     * @return 包含杨辉三角前numRows行的二维列表
     */
    public List<List<Integer>> generate(int numRows) {
        // 创建结果列表，用于存储杨辉三角的所有行
        List<List<Integer>> ret = new ArrayList<>();

        // 外层循环：逐行生成杨辉三角
        for (int i = 0; i < numRows; i++) {
            // 创建当前行的列表
            List<Integer> row = new ArrayList<>();

            // 内层循环：生成当前行的所有元素
            for (int j = 0; j <= i; j++) {
                // 判断是否为首尾元素
                if (j == 0 || j == i) {
                    // 首尾元素都为1
                    row.add(1);
                } else {
                    // 中间元素等于上一行相邻两个元素的和
                    // 即：当前行第j个元素 = 上一行第(j-1)个元素 + 上一行第j个元素
                    row.add(ret.get(i - 1).get(j - 1) + ret.get(i - 1).get(j));
                }
            }
            // 将生成的行添加到结果列表中
            ret.add(row);
        }

        // 返回完整的杨辉三角
        return ret;
    }

    // https://leetcode.cn/problems/house-robber/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 计算在不触发警报的情况下能够偷窃到的最高金额
     * <p>
     * 问题描述：你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，
     * 影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋
     * 在同一晚上被小偷闯入，系统会自动报警。
     * <p>
     * 解题思路：
     * 这是一个经典的动态规划问题。对于每个房屋，我们有两个选择：
     * 1. 偷窃当前房屋：那么就不能偷窃前一个房屋，最大金额为 当前房屋金额 + 前前个房屋的最大金额
     * 2. 不偷窃当前房屋：那么最大金额就是前一个房屋的最大金额
     * <p>
     * 状态定义：
     * f[i] 表示偷窃前 i+1 间房屋能够获得的最高金额
     * <p>
     * 状态转移方程：
     * f[i] = max(nums[i] + f[i-2], f[i-1])
     * <p>
     * 边界条件：
     * f[0] = nums[0] （只有一间房屋时，只能偷这一间）
     * f[1] = max(nums[0], nums[1]) （只有两间房屋时，选择金额较大的那一间）
     *
     * @param nums 每个房屋存放的金额数组
     * @return 能够偷窃到的最高金额
     */
    public int rob(int[] nums) {
        // 创建dp数组，f[i]表示偷窃前i+1间房屋能获得的最高金额
        int[] f = new int[nums.length];

        // 特殊情况处理：如果只有一间房屋，直接返回该房屋的金额
        if (f.length == 1) {
            return nums[0];
        }

        // 初始化前两个状态
        f[0] = nums[0];                           // 第一间房屋的最大金额就是它本身
        f[1] = Math.max(nums[0], nums[1]);        // 前两间房屋选择金额较大的那一间

        // 从第三间房屋开始，按照状态转移方程计算每个位置的最优解
        for (int i = 2; i < nums.length; i++) {
            // 对于第i间房屋，选择偷或不偷的最大值：
            // 偷：当前房屋金额 + 前前间房屋的最大金额
            // 不偷：前一间房屋的最大金额
            f[i] = Math.max(nums[i] + f[i - 2], f[i - 1]);
        }

        // 返回偷窃所有房屋能获得的最高金额
        return f[nums.length - 1];
    }

    // https://leetcode.cn/problems/perfect-squares/description/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 计算和为 n 的完全平方数的最少数量
     * <p>
     * 解题思路：
     * 这是一个典型的动态规划问题，类似于"零钱兑换"问题。
     * <p>
     * 状态定义：
     * f[i] 表示和为 i 的完全平方数的最少数量
     * <p>
     * 状态转移方程：
     * f[i] = min(f[i - j*j]) + 1，其中 j*j <= i
     * <p>
     * 初始条件：
     * f[0] = 0（和为0需要0个完全平方数）
     * <p>
     * 计算顺序：
     * 从小到大计算每个 f[i]，确保在计算 f[i] 时，所有 f[i-j*j] 都已计算完成
     *
     * @param n 目标和
     * @return 和为 n 的完全平方数的最少数量
     */
    public int numSquares(int n) {
        // 创建dp数组，f[i]表示和为i的完全平方数的最少数量
        int[] f = new int[n + 1];

        // 初始化：f[0] = 0，和为0需要0个完全平方数
        f[0] = 0;

        // 从1到n依次计算每个状态值
        for (int i = 1; i <= n; i++) {
            // 初始化当前状态的最小值为最大整数值
            int minCount = Integer.MAX_VALUE;

            // 遍历所有可能的完全平方数 j*j，其中 j*j <= i
            for (int j = 1; j * j <= i; j++) {
                // 更新最小值：f[i-j*j] + 1 表示使用一个 j*j 后，剩余部分的最优解
                minCount = Math.min(minCount, f[i - j * j]);
            }

            // 当前状态的最优解 = 最小值 + 1（加上当前使用的这个完全平方数）
            f[i] = minCount + 1;
        }

        // 返回和为n的完全平方数的最少数量
        return f[n];
    }

}
