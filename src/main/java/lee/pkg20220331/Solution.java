package lee.pkg20220331;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode-cn.com/problems/self-dividing-numbers/
    public List<Integer> selfDividingNumbers(int left, int right) {
        List<Integer> l = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            if (isSelfDividing(i)) {
                l.add(i);
            }
        }
        return l;
    }

    private boolean isSelfDividing(int num) {
        int tmp = num;
        while (tmp > 0) {
            int idx = tmp % 10;
            if (idx == 0 || num % idx != 0) {
                return false;
            }
            tmp /= 10;
        }
        return true;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.selfDividingNumbers(47, 85);
        ans.forEach(System.out::println);
    }
}
