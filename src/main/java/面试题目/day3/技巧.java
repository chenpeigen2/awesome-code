package 面试题目.day3;

import 面试题目.NeedDeepLearn;

import java.util.Arrays;

public class 技巧 {

    // 1. 异或运算
    // 同0异1
    // https://leetcode.cn/problems/single-number/description/?envType=study-plan-v2&envId=top-100-liked
    public int singleNumber(int[] nums) {
        int res = 0;
        for (int num : nums) {
            res ^= num;
        }
        return res;
    }

    // https://leetcode.cn/problems/majority-element/submissions/697562212/?envType=study-plan-v2&envId=top-100-liked
    public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length / 2];
    }


    /**
     * 对数组进行原地排序，使得相同颜色的元素相邻，并按照红色（0）、白色（1）、蓝色（2）顺序排列。
     * 使用双指针方法，先将所有0移动到数组前面，再将所有1移动到0之后。
     *
     * @param nums 输入的整数数组，元素值为0、1或2，分别代表红色、白色和蓝色
     */
    // https://leetcode.cn/problems/sort-colors/description/?envType=study-plan-v2&envId=top-100-liked
    public void sortColors(int[] nums) {
        int n = nums.length;
        int ptr = 0; // 指向下一个0应该放置的位置

        // 第一次遍历：将所有0移动到数组前面
        for (int i = 0; i < n; i++) {
            if (nums[i] == 0) {
                // 交换当前元素和ptr位置的元素
                int temp = nums[i];
                nums[i] = nums[ptr];
                nums[ptr] = temp;
                ++ptr; // 移动ptr指针，指向下一个0应该放置的位置
            }
        }

        // 第二次遍历：将所有1移动到0之后
        for (int i = ptr; i < n; i++) {
            if (nums[i] == 1) {
                // 交换当前元素和ptr位置的元素
                int temp = nums[i];
                nums[i] = nums[ptr];
                nums[ptr] = temp;
                ++ptr; // 移动ptr指针，指向下一个1应该放置的位置
            }
        }
    }

    // https://leetcode.cn/problems/next-permutation/description/?envType=study-plan-v2&envId=top-100-liked
    @NeedDeepLearn
    public void nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }
        if (i >= 0) {
            int j = nums.length - 1;
            while (j > i && nums[j] <= nums[i]) {
                j--;
            }
            swap(nums, i, j);
        }
        reverse(nums, i + 1, nums.length - 1);
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            swap(nums, start, end);
            start++;
            end--;
        }
    }


    /**
     * 寻找数组中的重复数字。
     * 使用Floyd判圈算法（龟兔赛跑算法）来检测环并找到入口点。
     * <p>
     * 算法思路：
     * 1. 将数组看作一个链表，每个元素的值指向下一个节点的索引。
     * 2. 由于存在重复数字，必然形成环。
     * 3. 使用快慢指针找到环的相遇点。
     * 4. 将其中一个指针重置为起点，两个指针以相同速度前进，再次相遇的点即为环的入口（重复数字）。
     *
     * @param nums 输入的整数数组，包含n+1个整数，每个整数都在[1,n]范围内
     * @return 返回数组中的重复数字
     */
    // https://leetcode.cn/problems/find-the-duplicate-number/submissions/697546491/?envType=study-plan-v2&envId=top-100-liked
    public int findDuplicate(int[] nums) {
        // 初始化快慢指针
        int slow = 0;
        int fast = 0;

        // 第一阶段：寻找环中的相遇点
        do {
            slow = nums[slow];           // 慢指针每次移动一步
            fast = nums[nums[fast]];     // 快指针每次移动两步
        } while (slow != fast);          // 当两者相遇时停止

        // 第二阶段：寻找环的入口点（重复数字）
        slow = 0;                        // 将慢指针重置为起点
        while (slow != fast) {           // 两个指针以相同速度前进
            slow = nums[slow];           // 慢指针移动一步
            fast = nums[fast];           // 快指针移动一步
        }

        // 相遇点即为重复数字
        return slow;
    }

}
