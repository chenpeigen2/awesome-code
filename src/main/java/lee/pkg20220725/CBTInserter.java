package lee.pkg20220725;

import lee.adds.TreeNode;

import java.util.ArrayList;
import java.util.List;

//https://leetcode.cn/problems/complete-binary-tree-inserter/
public class CBTInserter {

    List<TreeNode> list = new ArrayList<>();
    int idx = 0; // 模拟出队情况

    public CBTInserter(TreeNode root) {
        list.add(root);
        int cur = 0; // 模拟出情况
        // 由于仍要保留层序遍历顺序，因此使用下标指针 cur 来模拟出队位置）
        while (cur < list.size()) {
            TreeNode node = list.get(cur);
            if (node.left != null) list.add(node.left);
            if (node.right != null) list.add(node.right);
            cur++;
        }
    }

    public int insert(int val) {
        TreeNode node = new TreeNode(val);
        while (list.get(idx).left != null && list.get(idx).right != null) idx++;
        TreeNode fa = list.get(idx);
        if (fa.left == null) fa.left = node;
        else fa.right = node;
        list.add(node);
        return fa.val;
    }

    public TreeNode get_root() {
        return list.get(0);
    }
}
