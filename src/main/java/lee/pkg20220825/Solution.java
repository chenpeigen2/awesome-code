package lee.pkg20220825;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Solution {


//    https://leetcode.cn/problems/find-k-closest-elements/
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        int idx = arr.length; // ken1
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= x) {
                idx = i;
                break;
            }
        }
        int i = idx - 1;
        int j = idx;

        List<Integer> ans = new ArrayList<>();

        while (k > 0) {
            if (i < 0) {
                i = -1;
                j += k;
                k = 0;
                continue;
            }

            // ken2
            if (j >= arr.length) {
                j = arr.length;
                i -= k;
                k = 0;
                continue;
            }

            int a = arr[i], b = arr[j];
            if (Math.abs(a - x) <= Math.abs(b - x)) {
                i--;
            } else {
                j++;
            }
            k--;
        }

        // ken3
        for (int l = i + 1; l < j; l++) {
            ans.add(arr[l]);
        }

        return ans;
    }


    // 反向思考
    public List<Integer> findClosestElements1(int[] arr, int k, int x) {
        int size = arr.length;
        int left = 0;
        int right = size - 1;
        int removeNums = size - k;
        while (removeNums > 0) {
            if (x - arr[left] <= arr[right] - x) {
                right--;
            } else {
                left++;
            }
            removeNums--;
        }

        List<Integer> res = new ArrayList<>();
        for (int i = left; i < left + k; i++) {
            res.add(arr[i]);
        }
        return res;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{3, 5, 8, 10};
        int k = 2;
        int x = 15;
        var app = new Solution();
        var ans = app.findClosestElements(arr, k, x);
        ans.stream().forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer);
            }
        });
    }
}
