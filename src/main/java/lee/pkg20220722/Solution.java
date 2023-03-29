package lee.pkg20220722;

import java.util.*;

public class Solution {
    //    https://leetcode.cn/problems/set-intersection-size-at-least-two/
    public int intersectionSizeTwo(int[][] ins) {
        Arrays.sort(ins, (a, b) -> {
            // 前面升序列 ，后面降序
            return a[1] != b[1] ? a[1] - b[1] : b[0] - a[0];
        });
        int a = -1, b = -1, ans = 0;
        for (int[] i : ins) {
            if (i[0] > b) {
                a = i[1] - 1;
                b = i[1];
                ans += 2;
            } else if (i[0] > a) {
                a = b;
                b = i[1];
                ans++;
            }
        }
        return ans;
    }

//    public static void main(String[] args) {
//        List<Integer> l = new ArrayList<>() {{
//            add(1);
//            add(2);
//            add(3);
//        }};
//
//        Collections.sort(l, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                System.out.println(o1 + "  " + o2);
//                return o2 - o1;
//            }
//        });
//    }
}
