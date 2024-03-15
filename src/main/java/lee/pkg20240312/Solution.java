package lee.pkg20240312;

import hot100.TreeNode;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    // https://leetcode.cn/problems/find-elements-in-a-contaminated-binary-tree/solutions/2681672/liang-chong-fang-fa-ha-xi-biao-wei-yun-s-6m7w/?envType=daily-question&envId=2024-03-12
}

class FindElements {

    public FindElements(TreeNode root) {
        dfs(root, 0);
    }

    public boolean find(int target) {
        return s.contains(target);
    }

    private final Set<Integer> s = new HashSet<>();


    private void dfs(TreeNode node, int val) {
        if (node == null) return;
        s.add(val);
        dfs(node.left, val * 2 + 1);
        dfs(node.right, val * 2 + 2);
    }
}
