package lee.pkg20211028;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    static Set<Integer> s = new HashSet<>();

    static {
        for (int i = 1; i < (int) 1e9 + 10; i *= 2) s.add(i);
    }

    public boolean reorderedPowerOf2(int n) {
        int[] cnts = new int[10];
        while (n != 0) {
            cnts[n % 10]++;
            n /= 10;
        }

        int[] cur = new int[10];
        for (int x : s) {
            Arrays.fill(cur, 0);
            while (x != 0) {
                cur[x % 10]++;
                x /= 10;
            }
            boolean flag = Arrays.equals(cnts, cur);
            if (!flag) continue;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        var result = new Solution().reorderedPowerOf2(1);

        System.out.println(result);
    }
}
