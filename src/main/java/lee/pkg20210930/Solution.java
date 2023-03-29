package lee.pkg20210930;

public class Solution {
    public int computeArea(int ax1, int ay1, int ax2, int ay2, int bx1, int by1, int bx2, int by2) {
        int area1 = (ax2 - ax1) * (ay2 - ay1);
        int area2 = (bx2 - bx1) * (by2 - by1);

        int overlap = Math.max(0, Math.min(ax2, bx2) - Math.max(bx1, ax1)) * Math.max(0, Math.min(ay2, by2) - Math.max(by1, ay1));


        System.out.println(overlap);

        return area1 + area2 - overlap;
    }


//    public int computeArea(int ax1, int ay1, int ax2, int ay2, int bx1, int by1, int bx2, int by2) {
//        int area1 = (ax2 - ax1) * (ay2 - ay1);
//        int area2 = (bx2 - bx1) * (by2 - by1);
//
//        int overlap = (Math.min(ax2, bx2) - Math.max(bx1, ax1)) * (Math.min(ay2, by2) - Math.max(by1, ay1));
//        System.out.println(overlap);
//
//        if (overlap < 0){
//            overlap = 0;
//        }
//
//        return area1 + area2 - overlap;
//    }


    // case 不重叠的 ,avoid -1 * -1  = 1
//        -2
//        -2
//        2
//        2
//        3
//        3
//        4
//        4


//    -2
//            -2
//            2
//            2
//            3
//            3
//            4
//            4


    public static void main(String[] args) {
        var app = new Solution();
        var result = app.computeArea(-2, -2, 2, 2, 3, 3, 4, 4);
        System.out.println(result);
    }
}
