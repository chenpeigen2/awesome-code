package lee.pkg20220704;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/minimum-absolute-difference/
    public List<List<Integer>> minimumAbsDifference(int[] arr) {
        Arrays.sort(arr);
        int min = Integer.MAX_VALUE;
        for (int i = 1; i < arr.length; i++) {
            min = Math.min(min, arr[i] - arr[i - 1]);
        }
        List<List<Integer>> ans = new ArrayList<>();
        for (int i = 1; i < arr.length; i++) {
            int first = arr[i - 1];
            int second = arr[i];
            if (second - first == min) {
                List<Integer> l = new ArrayList<>();
                l.add(first);
                l.add(second);
                ans.add(l);
            }
        }
        return ans;
    }


    public List<List<Integer>> minimumAbsDifference1(int[] arr) {
        Arrays.sort(arr);
        List<List<Integer>> ans = new ArrayList<>();
        int n = arr.length, min = arr[1] - arr[0];
        for (int i = 0; i < n - 1; i++) {
            int cur = arr[i + 1] - arr[i];
            if (cur < min) {
                ans.clear();
                min = cur;
            }
            if (cur == min) {
                List<Integer> temp = new ArrayList<>();
                temp.add(arr[i]);
                temp.add(arr[i + 1]);
                ans.add(temp);
            }
        }
        return ans;
    }
}
