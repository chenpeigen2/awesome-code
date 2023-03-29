package lee.pkg20220304;

public class Solution {
    //    https://leetcode-cn.com/problems/sum-of-subarray-ranges/
    public long subArrayRanges(int[] nums) {
        long ans = 0;
        for (int i = 0; i < nums.length; i++) {
            int min = nums[i], max = nums[i];
            for (int j = i + 1; j < nums.length; j++) {
                min = Math.min(min, nums[j]);
                max = Math.max(max, nums[j]);

                ans += (max - min);
            }
        }
        return ans;
    }

//    f[l][r][0]=min(f[l][r−1][0],nums[r])
//
//    f[l][r][1] = \max(f[l][r - 1][1], nums[r])
//    f[l][r][1]=max(f[l][r−1][1],nums[r])


    public long subArrayRanges1(int[] nums) {
        int n = nums.length;
        int[][][] f = new int[n][n][2];
        for (int i = 0; i < n; i++) f[i][i][0] = f[i][i][1] = nums[i];
        for (int len = 2; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                f[l][r][0] = Math.min(nums[r], f[l][r - 1][0]);
                f[l][r][1] = Math.max(nums[r], f[l][r - 1][1]);
            }
        }
        long ans = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                ans += f[i][j][1] - f[i][j][0];
            }
        }
        return ans;
    }
}
