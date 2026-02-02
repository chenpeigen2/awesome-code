package 面试题目;

import java.util.*;
import java.util.function.IntFunction;

public class 普通数组 {


    // https://leetcode.cn/problems/maximum-subarray/solutions/228009/zui-da-zi-xu-he-by-leetcode-solution/?envType=study-plan-v2&envId=top-100-liked
    // 我们用 f(i) 代表以第 i 个数结尾的「连续子数组的最大和」，那么很显然我们要求的答案就是：
    public int maxSubArray(int[] nums) {
        int cur = 0, maxAns = nums[0];
        for (int x : nums) {
//            f(i)=max{f(i−1)+nums[i],nums[i]}
            cur = Math.max(x, cur + x);
            maxAns = Math.max(cur, maxAns);
        }
        return maxAns;
    }

    public int[][] merge(int[][] intervals) {
        if (intervals.length == 0) {
            return new int[0][2];
        }
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });

        List<int[]> merged = new ArrayList<>();

        for (int i = 0; i < intervals.length; i++) {
            int L = intervals[i][0], R = intervals[i][1];
            if (merged.size() == 0 || merged.get(merged.size() - 1)[1] < L) {
                merged.add(new int[]{L, R});
            } else {
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], R);
            }
        }

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
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] sum = new int[n];
        // 初始化结果数组为1，这样后续可以直接相乘
        Arrays.fill(sum, 1);

        // beforeSum 存储当前位置左边所有元素的乘积
        int beforeSum = 1;
        // afterSum 存储当前位置右边所有元素的乘积  
        int afterSum = 1;

        // 双指针同时从两端向中间遍历
        for (int i = 0, j = n - 1; i < n; i++, j--) {
            // sum[i] 乘以左侧所有元素的乘积
            sum[i] *= beforeSum;
            // sum[j] 乘以右侧所有元素的乘积
            sum[j] *= afterSum;

            // 更新左侧乘积（包含当前元素）
            beforeSum *= nums[i];
            // 更新右侧乘积（包含当前元素）
            afterSum *= nums[j];
        }
        return sum;
    }

    // https://leetcode.cn/problems/first-missing-positive/description/?envType=study-plan-v2&envId=top-100-liked
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
