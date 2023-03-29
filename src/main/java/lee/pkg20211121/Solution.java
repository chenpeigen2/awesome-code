package lee.pkg20211121;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    public int maxDepth(Node root) {
        return dfs1(root, 1);
    }

    int dfs1(Node node, int depth) {
        if (node == null) return 0;
        if (node.children.size() == 0) {
            return depth;
        }
        int ans = 1;
        for (int i = 0; i < node.children.size(); i++) {
            int tmp = dfs1(node.children.get(i), depth + 1);
            ans = Math.max(ans, tmp);
        }
        return ans;
    }

    public int maxDepth1(Node root) {
        if (root == null) return 0;
        int ans = 0;
        for (Node n : root.children) {
            ans = Math.max(ans, maxDepth1(n));
        }
        return ans + 1;
    }


    int dfs(Node node) {
        if (node == null) return 0;
        int ans = 1;
        for (int i = 0; node.children != null && i < node.children.size(); i++) {
            ans = Math.max(ans, dfs(node.children.get(i)) + 1);
        }
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        Node d = new Node();
        d.val = 1;
        d.children = new ArrayList<>() {
            {
                add(new Node(2));
                add(new Node(3));
                add(new Node(4));

            }
        };
        d.children.get(0).children = new ArrayList<>() {{
            add(new Node(5));
            add(new Node(6));
        }};
        var ans = app.maxDepth(d);
        System.out.println(ans);
    }
}
