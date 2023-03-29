package lee.pkg20220112;

public class Solution {

//    接着，我们遍历数组，每遇到一个数字，我们将它和 small 和 mid 相比，若小于等于 small ，
//    则替换 small；否则，若小于等于 mid，则替换 mid；否则，若大于 mid，则说明我们找到了长度为 3 的递增数组！

    //    https://leetcode-cn.com/problems/increasing-triplet-subsequence/
    public boolean increasingTriplet(int[] nums) {
//        int before = nums[0];
//        for (int i = 1, cnt = 0; i < nums.length; i++) {
//            if (nums[i] > before) {
//                cnt++;
//            } else {
//                cnt = 0;
//            }
//            before = nums[i];
//            if (cnt == 2) {
//                return true;
//            }
//        }
//        return false;
        int len = nums.length;
        if (len < 3) {
            return false;
        }
        int small = Integer.MAX_VALUE, middle = Integer.MAX_VALUE;
        for (int v : nums) {
            if (v <= small) {
                small = v;
            } else if (v <= middle) {
                middle = v;
            } else if (v > middle) {
                return true;
            }
        }
        return false;
    }

    public boolean increasingTriplet1(int[] nums) {
        int n = nums.length;
        if (n < 3) {
            return false;
        }
//        创建两个长度为 nn 的数组 \textit{leftMin}leftMin 和 \textit{rightMax}rightMax，对于 0 \le i < n0≤i<n，\textit{leftMin}[i]leftMin[i] 表示 \textit{nums}[0]nums[0]
//    到 \textit{nums}[i]nums[i] 中的最小值，\textit{rightMax}[i]rightMax[i] 表示 \textit{nums}[i]nums[i] 到 \textit{nums}[n - 1]nums[n−1] 中的最大值。
        int[] leftMin = new int[n];
        leftMin[0] = nums[0];
        for (int i = 1; i < n; i++) {
            leftMin[i] = Math.min(leftMin[i - 1], nums[i]);
        }

        int[] rightMax = new int[n];
        rightMax[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], nums[i]);
        }

        for (int i = 1; i < n - 1; i++) {
            if (nums[i] > leftMin[i - 1] && nums[i] < rightMax[i + 1]) {
                return true;
            }
        }
        return false;
    }

    public boolean increasingTriplet2(int[] nums) {
        int n = nums.length;
        if (n < 3) {
            return false;
        }
        int first = nums[0], second = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) {
            int num = nums[i];
            if (num > second) {
                return true;
            } else if (num > first) {
                second = num;
            } else {
                // num < first
                first = num;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3, 4, 5};

        var app = new Solution();
        var ans = app.increasingTriplet(arr);
        System.out.println(ans);
    }
}
