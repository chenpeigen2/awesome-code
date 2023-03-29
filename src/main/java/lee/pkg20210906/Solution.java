package lee.pkg20210906;

//       lee 528
//        int l = 1, r = n - 1;
//                while (l < r) {
//        int mid = l + r >> 1;
//        if (sum[mid] >= t) r = mid;
//        else l = mid + 1;
//        }
//        return r - 1;

public class Solution {
    public int search(int[] nums, int target) {
        int l = 0;
        int r = nums.length - 1;
        for (; l < r; ) {
            int mid = l + r >> 1;
            if (nums[mid] >= target) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        if (nums[l] != target) {
            return -1;
        }
        return l;
    }

    public static void main(String[] args) {
        var app = new Solution();
        int[] arr = new int[]{-1, 0, 3, 5, 9, 12};
        var result = app.search(arr, 9);
        System.out.println(result);
    }
}
