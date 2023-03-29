package lee.pkg20220717;

public class Solution {
    //    https://leetcode.cn/problems/array-nesting/
    public int arrayNesting(int[] nums) {
        int ans = 0;
        for (int i = 0; i < nums.length; i++) {
            int cur = i;
            int cnt = 0;
            while (nums[cur] != -1) {
                cnt++;
                int c = cur;
                cur = nums[cur];
                nums[c] = -1; // remembering array manipulate
            }
            ans = Math.max(ans, cnt);
        }
        return ans;
    }

    public int arrayNesting1(int[] nums) {
        int ans = 0, n = nums.length;
        boolean[] vis = new boolean[n];
        for (int i = 0; i < n; ++i) {
            int cnt = 0;
            while (!vis[i]) {
                vis[i] = true;
                i = nums[i];
                ++cnt;
            }
            ans = Math.max(ans, cnt);
        }
        return ans;
    }
}
