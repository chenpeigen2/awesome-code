package lee.pkg20211023;

import java.util.Arrays;

public class Solution {

//    1. 你设计的矩形页面必须等于给定的目标面积。
//
//    2. 宽度 W 不应大于长度 L，换言之，要求 L >= W 。
//
//    3. 长度 L 和宽度 W 之间的差距应当尽可能小。

//    输入: 4
//    输出: [2, 2]
//    解释: 目标面积是 4， 所有可能的构造方案有 [1,4], [2,2], [4,1]。
//    但是根据要求2，[1,4] 不符合要求; 根据要求3，[2,2] 比 [4,1] 更能符合要求. 所以输出长度 L 为 2， 宽度 W 为 2。

    public int[] constructRectangle(int area) {
//        i >= 0
//        12
        for (int i = (int) Math.sqrt(area); ; i--) {
            if (area % i == 0) {
                // return: l length ; w width
                return new int[]{area / i, i};
            }
        }
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.constructRectangle(12);
        Arrays.stream(result).forEach(System.out::println);
        System.out.println(Math.sqrt(12));
    }
}