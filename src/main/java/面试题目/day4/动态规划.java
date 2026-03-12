package 面试题目.day4;

import 面试题目.NeedDeepLearn;

import java.util.*;

// 动态规划 4个题目
// 多纬动态规划 3个题目 ，两层for循环
public class 动态规划 {

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
                    continue;
                }
                // 中间元素等于上一行相邻两个元素的和
                // 即：当前行第j个元素 = 上一行第(j-1)个元素 + 上一行第j个元素
                row.add(ret.get(i - 1).get(j - 1) + ret.get(i - 1).get(j));
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
    @NeedDeepLearn
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

    // https://leetcode.cn/problems/coin-change/description/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 计算凑成指定金额所需的最少硬币个数
     * <p>
     * 问题描述：给定不同面额的硬币 coins 和一个总金额 amount，
     * 计算可以凑成总金额所需的最少的硬币个数。如果没有任何一种硬币组合能组成总金额，返回 -1。
     * <p>
     * 解题思路：
     * 这是一个典型的动态规划问题，属于"完全背包"问题的变种。
     * <p>
     * 状态定义：
     * dp[i] 表示凑成金额 i 所需的最少硬币个数
     * <p>
     * 状态转移方程：
     * dp[i] = min(dp[i], dp[i - coins[j]] + 1)
     * 其中 coins[j] 是当前考虑的硬币面额，且 coins[j] <= i
     * <p>
     * 初始条件：
     * dp[0] = 0（凑成金额0需要0个硬币）
     * 其他位置初始化为一个较大值（amount + 1），表示不可达状态
     * <p>
     * 计算顺序：
     * 从小到大计算每个 dp[i]，确保在计算 dp[i] 时，所有 dp[i-coins[j]] 都已计算完成
     *
     * @param coins  硬币面额数组
     * @param amount 目标金额
     * @return 凑成目标金额所需的最少硬币个数，无法凑成则返回-1
     */
    @NeedDeepLearn
    public int coinChange(int[] coins, int amount) {
        // 设置一个比最大可能值还大的数，用来标记无法达到的状态
        int max = amount + 1;

        // 创建dp数组，dp[i]表示凑成金额i所需的最少硬币个数
        int[] dp = new int[amount + 1];

        // 初始化dp数组，将所有位置设为max（不可达状态）
        Arrays.fill(dp, max);

        // 初始条件：凑成金额0需要0个硬币
        dp[0] = 0;

        // 从小到大计算每个金额的最优解
        for (int i = 1; i <= amount; i++) {
            // 尝试使用每一种硬币面额
            for (int j = 0; j < coins.length; j++) {
                // 只有当硬币面额不超过当前金额时才考虑使用
                if (coins[j] <= i) {
                    // 状态转移方程：
                    // dp[i] = min(当前值, 使用coins[j]硬币后的最优解)
                    // dp[i - coins[j]] + 1 表示使用一枚coins[j]硬币后，剩余金额的最优解加1
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }

        // 如果dp[amount]仍然大于amount，说明无法凑成目标金额，返回-1
        // 否则返回计算得到的最少硬币个数
        return dp[amount] > amount ? -1 : dp[amount];
    }

    /**
     * 判断字符串是否可以被字典中的单词拼接而成
     * <p>
     * 问题描述：给定一个字符串 s 和一个字符串列表 wordDict 作为字典，
     * 如果可以利用字典中出现的一个或多个单词拼接出 s，则返回 true。
     * 注意：不要求字典中出现的单词全部都使用，并且字典中的单词可以重复使用。
     * <p>
     * 解题思路：
     * 这是一个动态规划问题，类似于"单词拆分"问题。
     * <p>
     * 状态定义：
     * dp[i] 表示字符串 s 的前 i 个字符是否可以被字典中的单词拼接而成
     * <p>
     * 状态转移方程：
     * dp[i] = dp[j] && wordDictSet.contains(s.substring(j, i))
     * 其中 j < i，表示检查是否存在一个 j，使得 s 的前 j 个字符可以拼接，
     * 并且 s 的第 j 到 i 个字符是一个字典中的单词。
     * <p>
     * 初始条件：
     * dp[0] = true（空字符串可以被拼接）
     * <p>
     * 计算顺序：
     * 从小到大计算每个 dp[i]，确保在计算 dp[i] 时，所有 dp[j] 都已计算完成
     *
     * @param s        待判断的字符串
     * @param wordDict 字典中的单词列表
     * @return 如果字符串可以被字典中的单词拼接而成，返回 true；否则返回 false
     */
    // https://leetcode.cn/problems/word-break/submissions/700786437/?envType=study-plan-v2&envId=top-100-liked
    public boolean wordBreak(String s, List<String> wordDict) {
        // 将字典转换为 HashSet，提高查找效率
        Set<String> wordDictSet = new HashSet<>(wordDict);
        // 创建 dp 数组，dp[i] 表示 s 的前 i 个字符是否可以被拼接
        boolean[] dp = new boolean[s.length() + 1];
        // 初始条件：空字符串可以被拼接
        dp[0] = true;
        // 从小到大计算每个 dp[i]
        for (int i = 1; i <= s.length(); i++) {
            // 遍历所有可能的 j 值，检查是否存在一个 j 使得 dp[j] 为 true
            // 并且 s.substring(j, i) 是字典中的单词
            for (int j = 0; j < i; j++) {
                // 如果 dp[j] 为 true 且 s.substring(j, i) 在字典中
                if (dp[j] && wordDictSet.contains(s.substring(j, i))) {
                    // 则 dp[i] 为 true
                    dp[i] = true;
                    // 找到一个满足条件的 j 后即可跳出内层循环
                    break;
                }
            }
        }
        // 返回 dp[s.length()]，即整个字符串是否可以被拼接
        return dp[s.length()];
    }

    // 多纬动态规划

    /**
     * 计算机器人从左上角到右下角的不同路径数
     * <p>
     * 问题描述：一个机器人位于一个 m x n 网格的左上角，机器人每次只能向下或向右移动一步，
     * 问总共有多少条不同的路径可以到达右下角。
     * <p>
     * 解题思路：
     * 这是一个经典的动态规划问题。我们可以使用二维数组来表示每个位置的路径数。
     * <p>
     * 状态定义：
     * grid[i][j] 表示从起点 (0,0) 到达位置 (i,j) 的不同路径数
     * <p>
     * 状态转移方程：
     * grid[i][j] = grid[i-1][j] + grid[i][j-1]
     * 即当前位置的路径数等于上方位置和左方位置路径数之和
     * <p>
     * 边界条件：
     * 第一行和第一列的所有位置路径数都为1，因为只能一直向右或一直向下
     * grid[0][0] = 1（起点）
     *
     * @param m 网格的行数
     * @param n 网格的列数
     * @return 从左上角到右下角的不同路径总数
     */
    // https://leetcode.cn/problems/unique-paths/?envType=study-plan-v2&envId=top-100-liked
    public int uniquePaths(int m, int n) {
        // 创建二维数组存储每个位置的路径数
        int[][] grid = new int[m][n];
        // 初始化起点路径数为1
        grid[0][0] = 1;
        // 遍历网格中的每个位置
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // 跳过起点位置
                if (i == 0 && j == 0) continue;
                // 处理第一行：只能从左边来
                if (i == 0) {
                    grid[i][j] = grid[i][j - 1];
                }
                // 处理第一列：只能从上面来
                else if (j == 0) {
                    grid[i][j] = grid[i - 1][j];
                }
                // 处理其他位置：可以从上面或左边来
                else {
                    grid[i][j] = grid[i - 1][j] + grid[i][j - 1];
                }
            }
        }
        // 返回右下角位置的路径数
        return grid[m - 1][n - 1];
    }


    /**
     * 计算从左上角到右下角的最小路径和
     * <p>
     * 问题描述：给定一个包含非负整数的 m x n 网格 grid，
     * 找出一条从左上角到右下角的路径，使得路径上的数字总和为最小。
     * 机器人每次只能向下或向右移动一步。
     * <p>
     * 解题思路：
     * 使用动态规划方法，在原地修改网格数组来存储到达每个位置的最小路径和。
     * <p>
     * 状态定义：
     * grid[i][j] 表示从起点 (0,0) 到达位置 (i,j) 的最小路径和
     * <p>
     * 状态转移方程：
     * - 如果在第一行 (i == 0 且 j != 0)：只能从左边来
     * grid[i][j] = grid[i][j] + grid[i][j-1]
     * - 如果在第一列 (j == 0 且 i != 0)：只能从上面来
     * grid[i][j] = grid[i][j] + grid[i-1][j]
     * - 其他位置：可以选择从上面或左边来，取较小值
     * grid[i][j] = grid[i][j] + min(grid[i-1][j], grid[i][j-1])
     * <p>
     * 边界条件：
     * 起点 grid[0][0] 保持不变，因为这是路径的起始点
     * <p>
     * 时间复杂度：O(m*n)，需要遍历整个网格
     * 空间复杂度：O(1)，原地修改数组，不使用额外空间
     *
     * @param grid 包含非负整数的二维网格数组
     * @return 从左上角到右下角的最小路径和
     */
    // https://leetcode.cn/problems/minimum-path-sum/?envType=study-plan-v2&envId=top-100-liked
    public int minPathSum(int[][] grid) {
        // 遍历网格中的每个位置
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                // 跳过起点位置，因为起点的路径和就是它本身的值
                if (i == 0 && j == 0) continue;
                // 处理第一行：只能从左边来，累加左边位置的路径和
                if (i == 0) {
                    grid[i][j] += grid[i][j - 1];
                }
                // 处理第一列：只能从上面来，累加上面位置的路径和
                else if (j == 0) {
                    grid[i][j] += grid[i - 1][j];
                }
                // 处理其他位置：可以选择从上面或左边来，取路径和较小的方向
                else {
                    grid[i][j] += Math.min(grid[i - 1][j], grid[i][j - 1]);
                }
            }
        }
        // 返回右下角位置存储的最小路径和
        return grid[grid.length - 1][grid[0].length - 1];
    }


    /**
     * 计算两个字符串的最长公共子序列长度
     * <p>
     * 问题描述：给定两个字符串 text1 和 text2，返回这两个字符串的最长公共子序列的长度。
     * 如果不存在公共子序列，返回 0。
     * <p>
     * 解题思路：
     * 这是一个经典的动态规划问题。我们需要找到两个字符串中字符相同的最长子序列。
     * 子序列是指在不改变字符相对顺序的前提下，删除某些字符后形成的序列。
     * <p>
     * 状态定义：
     * dp[i][j] 表示 text1 的前 i 个字符和 text2 的前 j 个字符的最长公共子序列长度
     * <p>
     * 状态转移方程：
     * - 如果 text1[i-1] == text2[j-1]：
     * dp[i][j] = dp[i-1][j-1] + 1
     * （当前字符相同，公共子序列长度加1）
     * - 如果 text1[i-1] != text2[j-1]：
     * dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1])
     * （当前字符不同，取两种情况的最大值）
     * <p>
     * 边界条件：
     * dp[0][j] = 0 （text1为空时，公共子序列长度为0）
     * dp[i][0] = 0 （text2为空时，公共子序列长度为0）
     * <p>
     * 时间复杂度：O(m*n)，其中m和n分别是两个字符串的长度
     * 空间复杂度：O(m*n)，需要创建二维dp数组
     *
     * @param text1 第一个字符串
     * @param text2 第二个字符串
     * @return 两个字符串的最长公共子序列长度
     */
    // dp[i][j] 表示 text1 的前 i 个字符和 text2 的前 j 个字符的最长公共子序列长度
    public int longestCommonSubsequence(String text1, String text2) {
        // 获取两个字符串的长度
        int m = text1.length(), n = text2.length();
        // 创建二维dp数组，dp[i][j]表示text1前i个字符和text2前j个字符的LCS长度
        int[][] dp = new int[m + 1][n + 1];

        // 填充dp数组
        for (int i = 1; i <= m; i++) {
            // 获取text1的第i个字符（注意索引从0开始）
            char c1 = text1.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                // 获取text2的第j个字符（注意索引从0开始）
                char c2 = text2.charAt(j - 1);
                // 如果当前字符相同
                if (c1 == c2) {
                    // LCS长度等于左上角的值加1
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    // 如果当前字符不同，取上方和左方的最大值
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        // 返回右下角的值，即两个完整字符串的LCS长度
        return dp[m][n];
    }


}
