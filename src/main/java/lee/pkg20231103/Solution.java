package lee.pkg20231103;


import java.util.ArrayDeque;
import java.util.Queue;

class Node {
    public int val;
    public Node left;
    public Node right;
    public Node next;

    public Node() {
    }

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _left, Node _right, Node _next) {
        val = _val;
        left = _left;
        right = _right;
        next = _next;
    }
}

public class Solution {
    //    https://leetcode.cn/problems/populating-next-right-pointers-in-each-node-ii/?envType=daily-question&envId=2023-11-03
    public Node connect(Node root) {
        Queue<Node> queue = new ArrayDeque<>();
        if (root == null) return null;
        queue.offer(root);
        while (!queue.isEmpty()) {
            int sz = queue.size();
            Node pre = null;
            Node cur = null;
            for (int i = 0; i < sz; i++) {
                cur = queue.poll();
                if (pre != null) pre.next = cur;
                pre = cur;
                if (cur == null) continue;
                if (cur.left != null) queue.offer(cur.left);
                if (cur.right != null) queue.offer(cur.right);
            }
        }
        return root;
    }
}
