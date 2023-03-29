package lee.pkg20220102;

public class Solution {
    public int lastRemaining(int n) {
        int a1 = 1;
        int k = 0, cnt = n, step = 1;
        while (cnt > 1) {
            // 分割之后最新的data
            // 比如说 head a1
            // and the cnt
            if ((k & 1) == 0) {
                // forward after
                a1 += step;
            } else {
                // re-forward after
                a1 = (cnt % 2 == 0) ? a1 : a1 + step;
            }
            k++;
            cnt >>= 1;
            //1 2 3 4 5 6 7 8 9
            // 分割 之后最新的step
            step <<= 1;
        }
        return a1;
    }
}
