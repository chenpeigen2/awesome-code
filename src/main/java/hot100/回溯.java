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

    //    https://leetcode.cn/problems/subsets/?envType=study-plan-v2&envId=top-100-liked
    public List<List<Integer>> subsets(int[] nums) {
        return null;
    }

    //    https://leetcode.cn/problems/generate-parentheses/description/?envType=study-plan-v2&envId=top-100-liked
    public List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        backtract(ans, n, sb, 0, 0);
        return ans;
    }

    void backtract(List<String> ans, int n, StringBuilder sb, int left, int right) {
        if (sb.length() == n * 2) {
            ans.add(sb.toString());
            System.out.println(sb.toString());
            return;
        }
        if (left < n) {
            sb.append("(");
            backtract(ans, n, sb, left + 1, right);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (right < left) { // 需要确保括号是有序的
            sb.append(")");
            backtract(ans, n, sb, left, right + 1);
            sb.deleteCharAt(sb.length() - 1);
        }
    }




}
