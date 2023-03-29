package lee.pkg20220822;

import lee.adds.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    int h, m, n;
    List<List<String>> ans;

    public List<List<String>> printTree(TreeNode root) {
        dfs1(root, 0); // 数层级

        m = h + 1;
        n = (1 << m) - 1;

        ans = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                row.add("");
            }
            ans.add(row);
        }

        dfs2(root, 0, (n - 1) / 2);

        return ans;
    }

    // -1 -2 -4 -8
    // 1 << [0 , 1 , 2 , 3] 层级越多，所需要gap的东西越多
    private void dfs2(TreeNode root, int x, int y) {
        if (root == null) return;
        ans.get(x).set(y, String.valueOf(root.val));
        dfs2(root.left, x + 1, y - (1 << (m - x - 2)));
        dfs2(root.right, x + 1, y + (1 << (m - x - 2)));
    }

    // calculate the max height
    private void dfs1(TreeNode root, int depth) {
        if (root == null) return;
        h = Math.max(h, depth);
        dfs1(root.left, depth + 1);
        dfs1(root.right, depth + 1);
    }
}
