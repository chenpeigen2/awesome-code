package lee.pkg20211020;

import java.util.Arrays;

public class Solution {

    //    给你一个长度为 n 的整数数组，每次操作将会使 n - 1 个元素增加 1 。返回让数组所有元素相等的最小操作次数。\


//    输入：nums = [1,2,3]
//    输出：3
//    解释：
//    只需要3次操作（注意每次操作会增加两个元素的值）：
//            [1,2,3]  =>  [2,3,3]  =>  [3,4,3]  =>  [4,4,4]


//    方法、反向思考
//    题目要求：每次操作将会使 n - 1 个元素增加 1 ，让我们返回使所有数字相等的操作次数。
//
//    但是，并没有说一定要求出最后全部相等的数字是多少，所以，我们可以反向思考，每次操作使 1 个元素减少 1 ，这样的话我们就比较好计算了，只需要计算出来最小值，每一个元素都需要减少到最小值，求出这个操作次数即可。
//
//    我们这里使用Java中的IntStream来简化代码，你也可以使用双层循环、单层循环来求解。
//
//    作者：tong-zhu
//    链接：https://leetcode-cn.com/problems/minimum-moves-to-equal-array-elements/solution/mei-ri-yi-ti-fan-xiang-si-kao-liang-xing-uaqx/
//    来源：力扣（LeetCode）
//    著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

    public int minMoves(int[] nums) {

        int sum = 0, min = nums[0];
        for (int val : nums) {
            min = Math.min(min, val);
            sum += val;
        }

        return sum - nums.length * min;
    }

    public static void main(String[] args) {
        new Solution().minMoves(new int[]{1, 2, 3, 4, 5});
    }
}
