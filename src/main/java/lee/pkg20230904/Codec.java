package lee.pkg20230904;

import lee.adds.TreeNode;

import java.util.ArrayList;
import java.util.List;

//https://leetcode.cn/problems/serialize-and-deserialize-bst/solutions/2426363/jie-ti-si-lu-by-techsheng-uf2l/?envType=daily-question&envId=2023-09-04
public class Codec {
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder("");
        traveTree(root, sb);
        return sb.toString();
    }

    private void traveTree(TreeNode root, StringBuilder sb) {
        if (root == null) return;
        int val = root.val;
        if (!sb.isEmpty()) sb.append(",");
        sb.append(val);
        traveTree(root.left, sb);
        traveTree(root.right, sb);
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if (data.equals("")) return null;
        List<Integer> nums = new ArrayList<>();
        for (String s : data.split(",")) nums.add(Integer.valueOf(s));
        return buildTree(nums, 0, nums.size() - 1);
    }

    private TreeNode buildTree(List<Integer> nums, int start, int end) {
        if (start > end) return null;
        int val = nums.get(start);
        int mid = start + 1;
        while (mid <= end && nums.get(mid) < val) mid++;
        TreeNode node = new TreeNode(val);
        node.left = buildTree(nums, start + 1, mid - 1);
        node.right = buildTree(nums, mid, end);
        return node;
    }
}
