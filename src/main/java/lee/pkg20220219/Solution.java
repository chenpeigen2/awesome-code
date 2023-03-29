package lee.pkg20220219;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    //https://leetcode-cn.com/problems/pancake-sorting/
    public List<Integer> pancakeSort(int[] arr) {
        int n = arr.length;
        // 位置location的记录
        int[] idxs = new int[n + 1];
        for (int i = 0; i < n; i++) {
            idxs[arr[i]] = i;
        }

        List<Integer> ans = new ArrayList<>();

        for (int i = n; i >= 1; i--) {
            // 9 8 7 6 5 4 3 2 1 应该的下标是
            // 8 7 6 5 4 3 2 1 0
            // find the value = i - - key = idx in arr
            int idx = idxs[i];
            if (idx == i - 1) continue;
            if (idx != 0) {
                ans.add(idx + 1);
                reverse(arr, 0, idx, idxs);
            }
            ans.add(i);
            reverse(arr, 0, i - 1, idxs);
        }
        return ans;
    }

    void reverse(int[] arr, int i, int j, int[] idxs) {
        // 4 3 2 1
        // 0 1 2 3
        while (i < j) {
            // swap idx
            idxs[arr[i]] = j;
            idxs[arr[j]] = i;
            // swap arr values
            int c = arr[j];
            arr[j--] = arr[i];
            arr[i++] = c;
        }
    }
}
