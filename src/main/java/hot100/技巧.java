package hot100;

import java.util.Arrays;

public class 技巧 {


    // 同0异1
    //    https://leetcode.cn/problems/single-number/?envType=study-plan-v2&envId=top-100-liked
    public int singleNumber(int[] nums) {
        int ans = 0;
        for (int num : nums) {
            ans ^= num;
        }
        return ans;
    }

    //    https://leetcode.cn/problems/majority-element/?envType=study-plan-v2&envId=top-100-liked
    public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length / 2];
    }
}
