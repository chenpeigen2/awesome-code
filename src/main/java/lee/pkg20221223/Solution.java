package lee.pkg20221223;

public class Solution {
    //    https://leetcode.cn/problems/final-value-of-variable-after-performing-operations/
    public int finalValueAfterOperations(String[] operations) {
        int ans = 0;
        for (String operation : operations) {
            if (operation.contains("--")) {
                ans--;
            } else ans++;
        }
        return ans;
    }
}
