package 面试题目;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
