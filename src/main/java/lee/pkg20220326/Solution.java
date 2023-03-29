package lee.pkg20220326;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode-cn.com/problems/baseball-game/
    public int calPoints(String[] ops) {
        Deque<Integer> list = new ArrayDeque<>();
        for (String op : ops) {
            switch (op) {
                case "+" -> {
                    int before = list.removeLast();
                    int add = list.getLast() + before;
                    list.addLast(before);
                    list.addLast(add);
                }
                case "D" -> list.addLast(list.getLast() * 2);
                case "C" -> list.removeLast();
                default -> list.add(Integer.parseInt(op));
            }
        }

        int ans = 0;
        while (list.peek() != null) {
            ans += list.poll();
        }

        return ans;
    }

    public static void main(String[] args) {
        String[] ops = new String[]{"5", "-2", "4", "C", "D", "9", "+", "+"};
        var app = new Solution();
        var ans = app.calPoints(ops);
        System.out.println(ans);
    }
}
