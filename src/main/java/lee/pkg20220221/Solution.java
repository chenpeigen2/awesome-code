package lee.pkg20220221;

import java.util.*;

public class Solution {
    //    https://leetcode-cn.com/problems/push-dominoes/
    public String pushDominoes(String dominoes) {
        int Ridx = -1;
        char[] ans = dominoes.toCharArray();
        for (int i = 0; i < ans.length; i++) {
            if (ans[i] == 'L') {
                if (Ridx == -1) {
                    int j = i - 1;
                    while (j >= 0 && ans[j] == '.') {
                        ans[j] = 'L';
                        j--;
                    }
                } else {
                    int l = Ridx + 1, r = i - 1;
                    while (l < r) {
                        ans[l] = 'R';
                        ans[r] = 'L';
                        l++;
                        r--;
                    }
                    // reset
                    Ridx = -1;
                }
            } else if (ans[i] == 'R') {
                //"R.R.L"
                if (Ridx != -1) {
                    for (int j = Ridx + 1; j < i; j++) {
                        ans[j] = 'R';
                    }
                }
                Ridx = i;
            }
        }
        if (Ridx != -1) {
            for (int i = Ridx + 1; i < ans.length; i++) {
                ans[i] = 'R';
            }
        }
        return new String(ans);
    }

    public String pushDominoes1(String dominoes) {
//        我们用一个队列 qq 模拟搜索的顺序；数组 \textit{time}time 记录骨牌翻倒或者确定不翻倒的时间，翻倒的骨牌不会对正在翻倒或者已经翻倒的骨牌施加力；
//        数组 \textit{force}force 记录骨牌受到的力，骨牌仅在受到单侧的力时会翻倒

        int n = dominoes.length();
        Deque<Integer> queue = new ArrayDeque<Integer>();
        // 倒的时间
        int[] time = new int[n];
        Arrays.fill(time, -1);
        List<Character>[] force = new List[n];
        // 受力情况
        for (int i = 0; i < n; i++) {
            force[i] = new ArrayList<Character>();
        }

        // init
        for (int i = 0; i < n; i++) {
            char f = dominoes.charAt(i);
            if (f != '.') {
                queue.offer(i);

                time[i] = 0;
                force[i].add(f);
            }
        }

        char[] res = new char[n];
        Arrays.fill(res, '.');

        while (!queue.isEmpty()) {
            int i = queue.poll();
            // 通过判断是否受了两个力
            if (force[i].size() == 1) {
                // 返回force power
                char f = force[i].get(0);
                // 赋值
                res[i] = f;
                // 给下一代赋值force
                int ni = f == 'L' ? i - 1 : i + 1;

                if (ni >= 0 && ni < n) {
                    // 这是第几次倒的
                    int t = time[i];
                    // time[ni] 倒的时间
                    if (time[ni] == -1) {
                        queue.offer(ni);
                        time[ni] = t + 1;
                        force[ni].add(f);
                    } else if (time[ni] == t + 1) {
                        // 两边同时 fall
                        force[ni].add(f);
                    }else{
                        // L.L
                        // already fall
                    }
                }
            }
        }
        return new String(res);
    }

    public static void main(String[] args) {
        var str = "L.L";
        var app = new Solution();
        var ans = app.pushDominoes1(str);
        System.out.println(ans);
    }
}
