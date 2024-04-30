package lee.pkg20240430;

import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class Solution {
    //    https://leetcode.cn/problems/number-of-employees-who-met-the-target/?envType=daily-question&envId=2024-04-30
    public int numberOfEmployeesWhoMetTarget(int[] hours, int target) {
        return (int) IntStream.of(hours).filter(new IntPredicate() {
            @Override
            public boolean test(int i) {
                return i >= target;
            }
        }).count();
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.numberOfEmployeesWhoMetTarget(new int[]{0, 1, 2, 3, 4}, 2);
        System.out.println(ans);
    }
}