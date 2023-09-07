package lee.pkg20230907;

public class Solution {
    //    https://leetcode.cn/problems/minimum-time-to-repair-cars/?envType=daily-question&envId=2023-09-07
    public long repairCars(int[] ranks, int cars) {
        long l = 1, r = 1l * ranks[0] * cars * cars;
        while (l < r) {
            long m = l + r >> 1;
            if (check(ranks, cars, m)) {
                r = m;
            } else {
                l = m + 1;
            }
        }
        return l;
    }

    private boolean check(int[] ranks, int cars, long m) {
        long cnt = 0;
        for (int x : ranks) {
//            我们枚举一个时间 ttt，那么能力值为 rrr 的工人可以修完 ⌊tr⌋\lfloor \sqrt{\frac{t}{r}} \rfloor⌊
            cnt += (long) Math.sqrt(m / x);
        }
        return cnt >= cars;
    }
}