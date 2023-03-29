package lee.pkg20220430;

import java.util.Arrays;

public class Solution {

    //    https://leetcode-cn.com/problems/smallest-range-i/
    public int smallestRangeI(int[] nums, int k) {
        Arrays.sort(nums);
        if (nums[0] + k >= nums[nums.length - 1] - k) return 0;
        else return nums[nums.length - 1] - nums[0] - 2 * k;
    }

    public int smallestRangeI1(int[] nums, int k) {
        int max = nums[0], min = nums[0];
        for (int i : nums) {
            max = Math.max(max, i);
            min = Math.min(min, i);
        }
        return Math.max(0, max - min - 2 * k);
    }

    //    https://leetcode-cn.com/problems/shu-zu-zhong-de-ni-xu-dui-lcof/

    public int reversePairs(int[] nums) {
        int len = nums.length;
        if (len < 2) return 0;
        int[] temp = new int[len];
        return reversePairs(nums, 0, len - 1, temp);
    }

    private int reversePairs(int[] nums, int left, int right, int[] temp) {
        if (left == right) return 0;
        int mid = (right - left) / 2 + left;
        int leftCnt = reversePairs(nums, left, mid, temp);
        int rightCnt = reversePairs(nums, mid + 1, right, temp);
        if (nums[mid] <= nums[mid + 1]) {
            return leftCnt + rightCnt;
        }
        return leftCnt + rightCnt + mergeAndCount(nums, left, mid, right, temp);
    }

    private int mergeAndCount(int[] nums, int left, int mid, int right, int[] temp) {
        for (int i = left; i <= right; i++) {
            temp[i] = nums[i];
        }
        int i = left;
        int j = mid + 1;
        int cnt = 0;
        for (int k = left; k <= right; k++) {
            if (i == mid + 1) {
                nums[k] = temp[j++];
            } else if (j == right + 1) {
                nums[k] = temp[i++];
            } else if (temp[i] <= temp[j]) {
                nums[k] = temp[i++];
            } else {
                nums[k] = temp[j++];
                cnt += (mid - i + 1);
            }
        }
        return cnt;
    }

    // add some attempts
    public static int[] qsort(int arr[], int start, int end) {
        int pivot = arr[start];
        int mid = sortArrayByParity(arr, pivot, start, end);
        if (start < mid) {
            qsort(arr, start, mid);
        }

        if (mid + 1 < end) {
            qsort(arr, mid + 1, end);
        }
        return arr;
    }

    public static int[] qsort1(int arr[], int start, int end) {
        int pivot = arr[start];
        int i = start;
        int j = end;
        while (i < j) {
            while ((i < j) && (arr[j] > pivot)) {
                j--;
            }
            while ((i < j) && (arr[i] < pivot)) {
                i++;
            }
            if ((arr[i] == arr[j]) && (i < j)) {
                i++;
            } else {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        if (i - 1 > start) arr = qsort1(arr, start, i - 1);
        if (j + 1 < end) arr = qsort1(arr, j + 1, end);
        return (arr);
    }

    public static int sortArrayByParity(int[] nums, int pivot, int start, int end) {
        int i = start;
        for (int j = end; i < j; i++) {
            if (nums[i] >= pivot) {
                int c = nums[j];
                nums[j--] = nums[i];
                nums[i--] = c;
            }
        }
        return i;
    }

    public static void main(String[] args) {


        var nums = new int[]{
                1, 2, 3
        };

        var ans = qsort(nums, 0, nums.length - 1);

        Arrays.stream(ans).forEach(System.out::println);

//        var app = new Solution();
//        var ans = app.smallestRangeI(nums, 3);
//        System.out.println(ans);
    }
}
