package lee.pkg20220618;


class Node {
    public int val;
    public Node next;

    public Node() {
    }

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _next) {
        val = _val;
        next = _next;
    }
}

public class Solution {
    //    https://leetcode.cn/problems/4ueAj6/
    public Node insert(Node h, int x) {
        Node t = new Node(x);
        t.next = t;
        if (h == null) return t;
        Node ans = h;
        int min = h.val, max = h.val;

        // find the max val and min val of the chain
        while (h.next != ans) {
            h = h.next;
            min = Math.min(min, h.val);
            max = Math.max(max, h.val);
        }
        // only one node
        if (min == max) {
            t.next = ans.next;
            ans.next = t;
        } else {
            // find the max-node
            while (!(h.val == max && h.next.val == min)) h = h.next;
            while (!(x <= min || x >= max) && !(h.val <= x && x <= h.next.val)) h = h.next;
            t.next = h.next;
            h.next = t;
        }

        return ans;
    }
}
