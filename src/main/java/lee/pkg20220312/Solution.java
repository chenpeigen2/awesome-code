package lee.pkg20220312;

import lee.pkg20220310.Node;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode-cn.com/problems/n-ary-tree-postorder-traversal/
    List<Integer> l = new ArrayList<>();

    public List<Integer> postorder(Node root) {
        postOrder(root);
        return l;
    }

    void postOrder(Node root) {
        if (root == null) return;
        root.children.forEach(this::postOrder);
        l.add(root.val);
    }
}
