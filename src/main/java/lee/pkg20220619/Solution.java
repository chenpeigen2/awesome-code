package lee.pkg20220619;

import lee.adds.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    //    https://leetcode.cn/problems/most-frequent-subtree-sum/
    Map<Integer, Integer> map = new HashMap<>();

    int max = 0;

    public int[] findFrequentTreeSum(TreeNode root) {
        dfs(root);
        List<Integer> list = new ArrayList<>();
        for (int k : map.keySet()) {
            if (map.get(k) == max) list.add(k);
        }
        int n = list.size();
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) ans[i] = list.get(i);
        return ans;
    }

    private int dfs(TreeNode root) {
        if (root == null) return 0;
        int cur = root.val + dfs(root.left) + dfs(root.right);
        map.put(cur, map.getOrDefault(cur, 0) + 1);
        max = Math.max(max, map.get(cur));
        return cur;
    }
}
