package lee.pkg20230218;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/find-positive-integer-solution-for-a-given-equation/
    public List<List<Integer>> findSolution(CustomFunction customfunction, int z) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        for (int x = 1; x <= 1000; x++) {
            for (int y = 1; y <= 1000; y++) {
                if (customfunction.f(x, y) == z) {
                    List<Integer> pair = new ArrayList<Integer>();
                    pair.add(x);
                    pair.add(y);
                    res.add(pair);
                }
            }
        }
        return res;
    }

    public List<List<Integer>> findSolution1(CustomFunction customfunction, int z) {
        List<List<Integer>> res = new ArrayList<>();
        for (int x = 1; x <= 1000; x++) {
            int yleft = 1, yright = 1000;
            while (yleft <= yright) {
                int ymiddle = (yleft + yright) / 2;
                if (customfunction.f(x, ymiddle) == z) {
                    List<Integer> pair = new ArrayList<Integer>();
                    pair.add(x);
                    pair.add(ymiddle);
                    res.add(pair);
                    break;
                }
                if (customfunction.f(x, ymiddle) > z) {
                    yright = ymiddle - 1;
                } else {
                    yleft = ymiddle + 1;
                }
            }
        }
        return res;
    }
}

class CustomFunction {
    public int f(int x, int y) {
        return -1;
    }
};
