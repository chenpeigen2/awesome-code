package lee.pkg20220622;

import lee.adds.TreeNode;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode.cn/problems/find-bottom-left-tree-value/
    public int findBottomLeftValue(TreeNode root) {
        int ans = 0;
        Deque<TreeNode> queue = new ArrayDeque<>();
        if (root != null) queue.addLast(root);

        while (!queue.isEmpty()) {
            int cnt = queue.size();
            ans = queue.peekFirst().val;
            while (cnt > 0) {
                TreeNode tn = queue.removeFirst();
                if (tn.left != null) {
                    queue.addLast(tn.left);
                }
                if (tn.right != null) {
                    queue.addLast(tn.right);
                }
                cnt--;
            }
        }
        return ans;
    }
}
