package lee.pkg20211014;

public class Solution {
    public int peakIndexInMountainArray(int[] arr) {
        int l = 0;
        int r = arr.length - 1;
        while (l < r) {
            int mid = l + r + 1 >> 1;
            if (arr[mid - 1] < arr[mid]) {
                l = mid;
            } else {
                r = mid - 1;
            }
        }
        // 2 1情况
        return r;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{2, 1};
        var app = new Solution();
        var result = app.peakIndexInMountainArray(arr);
        System.out.println(result);
    }
}
