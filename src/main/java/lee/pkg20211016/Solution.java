package lee.pkg20211016;

import java.util.ArrayList;
import java.util.List;

//七沢みあ
public class Solution {
    List<String> ans = new ArrayList<>();
    String s;
    int n, t;

    public List<String> addOperators(String num, int target) {
        s = num;
        n = s.length();
        t = target;
        dfs(0, 0, 0, "");
        return ans;
    }
    // 0  1【here】? 2 ?3

    void dfs(int u, long prev, long cur, String ss) {
        if (u == n) {
            if (cur == t) ans.add(ss);
            return;
        }
        // 考虑1 12 123 1234 没有操作符的情况才需要循环
        for (int i = u; i < n; i++) {
            //01 is not allowd means 1 无意义
            if (i != u && s.charAt(u) == '0') break;
            long next = Long.parseLong(s.substring(u, i + 1));
            // 如果是第一个 1 12 123 1234 都直接进去下一场
            if (u == 0) {
                // prev means previndexnumber; cur means cur sum value
                dfs(i + 1, next, next, "" + next);
            } else {
                dfs(i + 1, next, cur + next, ss + "+" + next);
                dfs(i + 1, -next, cur - next, ss + "-" + next);
                long x = prev * next;
                // 4+3*2 -> 7-3+6 = 10
                dfs(i + 1, x, cur - prev + x, ss + "*" + next);
            }
        }
    }

    public static void main(String[] args) {
        var l = new Solution().addOperators("0123", 6);
        l.forEach(System.out::println);
    }
}
