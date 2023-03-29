package lee.pkg20220729;

import java.util.HashSet;
import java.util.Set;

public class Solution {

    //    https://leetcode.cn/problems/valid-square/
    public boolean validSquare(int[] p1, int[] p2, int[] p3, int[] p4) {
        Set<Integer> s = new HashSet<>();
        s.add(distance(p1, p2));
        s.add(distance(p1, p3));
        s.add(distance(p1, p4));
        s.add(distance(p2, p3));
        s.add(distance(p2, p4));
        s.add(distance(p3, p4));

        if (s.size() != 2 || s.contains(0)) return false;
        Integer[] array = s.toArray(new Integer[2]);
        if (array[0] > array[1]) {
            return array[0] == 2 * array[1];
        } else {
            return array[1] == 2 * array[0];
        }
    }


    public int distance(int[] p1, int[] p2) {
        int edge1 = p1[0] - p2[0];
        int edge2 = p1[1] - p2[1];
        return edge1 * edge1 + edge2 * edge2;
    }

    /**
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return [2, 2]
     * [2,1]
     * [1,2]
     * [1,2]
     * <p>
     * avoid case
     */
    public boolean validSquare1(int[] p1, int[] p2, int[] p3, int[] p4) {
        return isIsoscelesRightTriangle(p1, p2, p3) && isIsoscelesRightTriangle(p1, p2, p4) && isIsoscelesRightTriangle(p2, p3, p4) && isIsoscelesRightTriangle(p1, p3, p4);
    }

    public static boolean isIsoscelesRightTriangle(int[] p1, int[] p2, int[] p3) {
        int d1 = (p1[0] - p2[0]) * (p1[0] - p2[0]) + (p1[1] - p2[1]) * (p1[1] - p2[1]);
        int d2 = (p1[0] - p3[0]) * (p1[0] - p3[0]) + (p1[1] - p3[1]) * (p1[1] - p3[1]);
        int d3 = (p3[0] - p2[0]) * (p3[0] - p2[0]) + (p3[1] - p2[1]) * (p3[1] - p2[1]);
        if (d1 == 0 || d2 == 0 || d3 == 0) return false;
        return (d2 == d3) && d1 == (d2 + d3) || (d1 == d2) && d3 == (d2 + d1) || (d1 == d3) && d2 == (d3 + d1);
    }
}
