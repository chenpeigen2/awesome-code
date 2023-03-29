package lee.pkg20220905;

import lee.adds.TreeNode;

import java.util.*;

public class Solution {

    //    https://leetcode.cn/problems/find-duplicate-subtrees/
    Map<String, TreeNode> seen = new HashMap<>();
    Set<TreeNode> repeat = new HashSet<>();

    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        dfs(root);
        return new ArrayList<>(repeat);
    }

    private String dfs(TreeNode node) {
        if (node == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(node.val);
        sb.append("(");
        sb.append(dfs(node.left));
        sb.append(")(");
        sb.append(dfs(node.right));
        sb.append(")");
        String serial = sb.toString();
        if (seen.containsKey(serial)) {
            // repeat same node value hash can be diffetent, so can't add node ;
            // it will cause duplicate
            // repeat.add(node);
            repeat.add(seen.get(serial));
        } else {
            seen.put(serial, node);
        }
        return serial;
    }

}
