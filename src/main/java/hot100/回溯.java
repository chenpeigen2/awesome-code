package hot100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class 回溯 {
    // O(n×n!)
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ret = new ArrayList<>();
        int len = nums.length;
        List<Integer> arr = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            arr.add(nums[i]);
        }
        backTrack(arr, ret, 0, len);
        return ret;
    }

    void backTrack(List<Integer> arr, List<List<Integer>> ret, int first, int n) {
        if (first == n) {
            ret.add(new ArrayList<>(arr));
        }
        for (int i = first; i < n; i++) {
            Collections.swap(arr, first, i);
            backTrack(arr, ret, first + 1, n);
            Collections.swap(arr, first, i);
        }
    }
}
