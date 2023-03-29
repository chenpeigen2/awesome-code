package lee.pkg20220405;


public class Solution {

    //https://leetcode-cn.com/problems/prime-number-of-set-bits-in-binary-representation/
    public int countPrimeSetBits(int left, int right) {
        int ans = 0;
        for (int i = left; i <= right; i++) {
            if (hash[getCnt(i)]) ans++;
        }
        return ans;
    }

    //    https://leetcode-cn.com/problems/number-of-1-bits/solution/yi-ti-san-jie-wei-shu-jian-cha-you-yi-to-av1r/
    int getCnt(int x) {
        int cnt = 0;
        while (x != 0) {
            cnt++;
            x -= (x & -x);
        }
        return cnt;
    }


    static boolean[] hash = new boolean[40];

    static {
        int[] nums = new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
        for (int x : nums) hash[x] = true;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.countPrimeSetBits(10, 15);
        System.out.println(ans);
    }

}
