package lee.pkg20220731;

import lee.adds.TreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

public class Solution {
    public int maxLevelSum(TreeNode root) {

        Queue<TreeNode> q = new ArrayDeque<>();
        q.offer(root);

        int ans = -1;
        int level = 1;
        long max = Long.MIN_VALUE;

        while (!q.isEmpty()) {
            int size = q.size();
            int sum = 0;
            while (size-- > 0) {
                TreeNode n = q.poll();
                sum += n.val;
                if (n.left != null) q.add(n.left);
                if (n.right != null) q.add(n.right);
            }
            if (sum > max) {
                ans = level;
                max = sum;
            }
            level++;
        }

        return ans;
    }
}
