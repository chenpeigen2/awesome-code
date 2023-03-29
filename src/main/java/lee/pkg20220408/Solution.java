package lee.pkg20220408;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Node {
    public int val;
    public List<Node> children;

    public Node() {
    }

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
}

public class Solution {
    //    https://leetcode-cn.com/problems/n-ary-tree-level-order-traversal/
    public List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) return ans;
        Deque<Node> q = new ArrayDeque<>();
        q.addLast(root);
        while (!q.isEmpty()) {
            int len = q.size();
            List<Integer> l = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                Node node = q.removeFirst();
                node.children.forEach(q::addLast);
                l.add(node.val);
            }
            ans.add(l);
        }
        return ans;
    }
}
