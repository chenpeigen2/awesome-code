package 面试题目.day1;

import 面试题目.DoubleCheck;
import 面试题目.NeedDeepLearn;

import java.util.*;

public class 普通数组 {


    // https://leetcode.cn/problems/maximum-subarray/solutions/228009/zui-da-zi-xu-he-by-leetcode-solution/?envType=study-plan-v2&envId=top-100-liked
    // 我们用 f(i) 代表以第 i 个数结尾的「连续子数组的最大和」，那么很显然我们要求的答案就是：
    /**
     * 寻找数组中连续子数组的最大和
     * 
     * 算法思路：
     * 使用动态规划的思想，定义 f(i) 表示以第 i 个元素结尾的连续子数组的最大和
     * 状态转移方程：f(i) = max(f(i-1) + nums[i], nums[i])
     * 即：要么将当前元素加入之前的子数组，要么重新开始一个新的子数组
     * 
     * 实现细节：
     * 1. cur 变量记录当前连续子数组的和
     * 2. maxAns 记录全局最大值，初始化为数组第一个元素
     * 3. 遍历数组，对每个元素执行状态转移
     * 4. 每次更新后比较并保存最大值
     * 
     * 时间复杂度：O(n) - 只需遍历数组一次
     * 空间复杂度：O(1) - 只使用常数额外空间
     * 
     * @param nums 输入的整数数组
     * @return 连续子数组的最大和
     */
    public int maxSubArray(int[] nums) {
        // 初始化当前子数组和为0，最大答案为第一个元素
        int cur = 0, maxAns = nums[0];
        // 遍历数组中的每个元素
        for (int x : nums) {
            // 状态转移：选择继续扩展子数组或重新开始
            // f(i) = max{f(i-1) + nums[i], nums[i]}
            cur = Math.max(x, cur + x); // please remember it
            // 更新全局最大值
            maxAns = Math.max(cur, maxAns);
        }
        // 返回连续子数组的最大和
        return maxAns;
    }

    /**
     * 合并重叠区间
     * 
     * 算法思路：
     * 1. 首先对区间数组按照起始位置进行排序，确保区间有序
     * 2. 遍历排序后的区间，维护一个合并后的区间列表
     * 3. 对于每个区间，判断是否与上一个合并区间重叠：
     *    - 如果不重叠，则直接添加新区间
     *    - 如果重叠，则更新上一个区间的结束位置为两者的较大值
     * 
     * 实现细节：
     * - 使用 ArrayList 存储合并后的区间，便于动态添加
     * - 排序使用自定义比较器，按区间起始位置升序排列
     * - 时间复杂度主要由排序决定：O(n log n)
     * - 空间复杂度：O(n) - 存储合并后的区间
     * 
     * 示例：
     * 输入：[[1,3],[2,6],[8,10],[15,18]]
     * 输出：[[1,6],[8,10],[15,18]]
     * 解释：区间 [1,3] 和 [2,6] 重叠，合并为 [1,6]
     * 
     * @param intervals 包含多个区间的二维数组，每个区间为 [start, end]
     * @return 合并后的不重叠区间数组
     */
    public int[][] merge(int[][] intervals) {
        // 边界条件：空数组直接返回空结果
        if (intervals.length == 0) {
            return new int[0][2];
        }
        // 按照区间起始位置排序，确保区间有序处理
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];  // 按起始位置升序排序
            }
        });
        // 存储合并后的区间结果
        List<int[]> merged = new ArrayList<>();
        // 遍历所有区间
        for (int[] interval : intervals) {
            // 提取当前区间的左右边界
            int L = interval[0], R = interval[1];
            // 判断是否需要新建区间：
            // 1. 合并列表为空时
            // 2. 当前区间起始位置大于上一个区间结束位置（无重叠）
            if (merged.size() == 0 || merged.get(merged.size() - 1)[1] < L) {
                merged.add(new int[]{L, R});  // 添加新区间
            } else {
                // 存在重叠，更新上一个区间的结束位置为较大值
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], R);
            }
        }
        // 将 List 转换为二维数组返回
        return merged.toArray(new int[0][]);
    }

    // https://leetcode.cn/problems/rotate-array/description/?envType=study-plan-v2&envId=top-100-liked
    public void rotate(int[] nums, int k) {
        int len = nums.length; // 获取数组长度
        k = k % len; // 处理 k 大于数组长度的情况
        int[] newArray = new int[len];
        for (int i = 0; i < len; i++) {
            newArray[(i + k) % len] = nums[i];
        }
        // 使用System.arraycopy将新数组的内容复制回原数组
        // 参数说明：
        // src: 源数组 (newArray) - 要复制数据的源数组
        // srcPos: 源数组起始位置 (0) - 从源数组的哪个索引开始复制
        // dest: 目标数组 (nums) - 要将数据复制到的目标数组
        // destPos: 目标数组起始位置 (0) - 在目标数组的哪个索引开始放置数据
        // length: 复制元素的数量 (len) - 要复制多少个元素
        System.arraycopy(newArray, 0, nums, 0, len);
    }

    /**
     * 计算数组中每个位置的乘积（除了自身）
     * <p>
     * 算法思路：
     * 1. 使用两个变量 beforeSum 和 afterSum 分别记录当前位置左侧所有数字的乘积和右侧所有数字的乘积
     * 2. 使用双指针技术，从数组两端同时遍历，i 从左向右，j 从右向左
     * 3. 在每次迭代中：
     * - sum[i] 乘以 beforeSum（i 左侧的累积乘积）
     * - sum[j] 乘以 afterSum（j 右侧的累积乘积）
     * - 更新 beforeSum 和 afterSum
     * 4. 最终 sum[i] 就包含了除 nums[i] 外其他所有元素的乘积
     * <p>
     * 时间复杂度：O(n)，只需要一次遍历
     * 空间复杂度：O(1)，除了输出数组外没有额外空间开销
     */
//    原数组：       [1       2       3       4]
//    左部分的乘积：   1       1      1*2    1*2*3
//    右部分的乘积： 2*3*4    3*4      4      1
//    结果：        1*2*3*4  1*3*4   1*2*4  1*2*3*1
    /**
     * 计算数组中每个位置的乘积（除了自身）
     * 
     * 算法思路：
     * 1. 使用两个变量 beforeSum 和 afterSum 分别记录当前位置左侧所有数字的乘积和右侧所有数字的乘积
     * 2. 使用双指针技术，从数组两端同时遍历，i 从左向右，j 从右向左
     * 3. 在每次迭代中：
     *    - result[i] 乘以 beforeSum（i 左侧的累积乘积）
     *    - result[j] 乘以 afterSum（j 右侧的累积乘积）
     *    - 更新 beforeSum 和 afterSum
     * 4. 最终 result[i] 就包含了除 nums[i] 外其他所有元素的乘积
     * 
     * 时间复杂度：O(n)，只需要两次遍历数组
     * 空间复杂度：O(1)，除了输出数组外没有额外空间开销
     * 
     * 示例：
     * 输入：[1, 2, 3, 4]
     * 输出：[24, 12, 8, 6]
     * 解释：
     * - 索引0：2*3*4 = 24
     * - 索引1：1*3*4 = 12
     * - 索引2：1*2*4 = 8
     * - 索引3：1*2*3 = 6
     * 
     * @param nums 输入的整数数组
     * @return 每个位置对应除自身外其他元素乘积的数组
     */
    public int[] productExceptSelf(int[] nums) {
        // 初始化结果数组，所有元素初始值为1
        int[] result = new int[nums.length];
        Arrays.fill(result, 1);
        
        // beforeSum 记录当前位置左侧所有元素的乘积
        int beforeSum = 1;
        // afterSum 记录当前位置右侧所有元素的乘积
        int afterSum = 1;
        
        // 第一次遍历：从左到右计算每个位置左侧元素的乘积
        for (int i = 0; i < nums.length; i++) {
            // 将当前位置的左侧乘积累积到结果中
            result[i] *= beforeSum;
            // 更新左侧乘积（包含当前元素）
            beforeSum *= nums[i];
        }
        
        // 第二次遍历：从右到左计算每个位置右侧元素的乘积
        for (int i = nums.length - 1; i >= 0; i--) {
            // 将当前位置的右侧乘积累积到结果中
            result[i] *= afterSum;
            // 更新右侧乘积（包含当前元素）
            afterSum *= nums[i];
        }
        
        // 返回计算结果
        return result;
    }

    // https://leetcode.cn/problems/first-missing-positive/description/?envType=study-plan-v2&envId=top-100-liked
    @DoubleCheck
    public int firstMissingPositive(int[] nums) {
        int len = nums.length;

        // 检查数组中是否包含数字1
        boolean hasOne = false;
        for (int num : nums) {
            if (num == 1) {
                hasOne = true;
                break;
            }
        }
        // 如果数组中不包含数字1，则返回1
        if (!hasOne) {
            return 1;
        }

        for (int i = 0; i < len; i++) {
            // 如果当前数字小于等于0或大于数组长度，则将其置为1
            if (nums[i] <= 0 || nums[i] > len) {
                nums[i] = 1;
            }
        }

        for (int i = 0; i < len; i++) {
            int idx = Math.abs(nums[i]) - 1; // 获取当前数字对应的索引
            nums[idx] = -Math.abs(nums[idx]); // 处置 为negative number
        }

        // 遍历数组，找到第一个正数，其索引加1即为缺失的最小正整数
        for (int i = 0; i < len; i++) {
            if (nums[i] > 0) {
                return i + 1;
            }
        }

        return len + 1;
    }

    public static void main(String[] args) {
        List<String> list = Arrays.asList("A", "B", "C");

// ❌ 无参版本 - 需要强制转换，不安全
        Object[] objArray = list.toArray();  // 返回 Object[]
// String[] strArray = (String[]) objArray;  // 运行时可能出错

// ✅ 有参版本 - 类型安全
        String[] strArray1 = list.toArray(new String[100]);  // 空数组方式
        String[] strArray2 = list.toArray(new String[list.size()]);  // 精确大小

//        list.stream().toArray(new IntFunction<? extends Object[]>() {
//            @Override
//            public Object[] apply(int value) {
//                return new Object[0];
//            }
//        });


// Lambda 表达式
        list.sort((o1, o2) -> 0);

// 任意对象的实例方法引用
        list.sort(String::compareTo);

        System.out.println();

//        // Lambda 表达式
//        (args) -> Class.method(args)
//
//// 等价的方法引用
//        Class::method
    }
}
