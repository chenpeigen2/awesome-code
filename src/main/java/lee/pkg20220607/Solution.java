package lee.pkg20220607;

public class Solution {
    //    https://leetcode.cn/problems/koko-eating-bananas/
    public int minEatingSpeed(int[] piles, int h) {
        int l = 0, r = (int) 1e9;
        while (l < r) {
            int mid = (l + r) >> 1;
            if (check(piles, mid, h)) r = mid;
            else l = mid + 1;
        }
        return r;
    }

    private boolean check(int[] piles, int mid, int h) {
        int ans = 0;
        for (int x : piles) ans += Math.ceil(x * 1.0 / mid);
        return ans <= h;
    }
}
