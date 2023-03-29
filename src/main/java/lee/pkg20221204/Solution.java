package lee.pkg20221204;

public class Solution {

    int ans = 0x3f3f3f3f;

    public int closestCost(int[] base, int[] top, int target) {
        for (int x : base) dfs(0, x, target, top);
        return ans;
    }

    // sum 为目前料的总和
    // target 为目标值
    // ans为记录的最终值
    private void dfs(int x, int sum, int target, int[] top) {
        int a = Math.abs(target - sum);
        int b = Math.abs(target - ans);
        if (a < b) ans = sum; // 相距离最小
        if (a == b && ans > sum) ans = sum;
        if (sum > target) return;
        for (int i = x; i < top.length; i++) {
//            dfs(i + 1, sum, target, top); opt
            dfs(i + 1, sum + top[i], target, top);
            dfs(i + 1, sum + 2 * top[i], target, top);
        }
    }

}