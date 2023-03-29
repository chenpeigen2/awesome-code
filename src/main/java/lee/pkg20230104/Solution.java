package lee.pkg20230104;

public class Solution {

    //    https://leetcode.cn/problems/maximum-value-at-a-given-index-in-a-bounded-array/solutions/
    public int maxValue(int n, int index, int maxSum) {
        // 1 2 3 4
        // mid = 3

        // 1 2 3
        // mid = 2

        // 往大缩用这种case
        // 往小缩normal
        int left = 1, right = maxSum;
        while (left < right) {
            int mid = (left + right+1) / 2;
            if (valid(mid, n, index, maxSum)) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return left;
    }

    private boolean valid(int mid, int n, int index, int maxSum) {
        int left = index;
        int right = n - index - 1;
        return mid + cal(mid, left) + cal(mid, right) <= maxSum;
    }

    private long cal(int big, int length) {
        // 4 3 2
        if (length + 1 < big) {
            int small = big - length;
            return (long) (big - 1 + small) * length / 2;
        } else {
            // 4 3 2 1
            int ones = length - (big
                    - 1);
            return (long) big * (big - 1) / 2 + ones;
        }
    }
}
