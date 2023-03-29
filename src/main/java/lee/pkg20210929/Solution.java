package lee.pkg20210929;

public class Solution {
    public int findMinMoves(int[] ms) {
        int n = ms.length;
        int sum = 0;
        for (int i : ms) {
            sum += i;
        }
        if (sum % n != 0) {
            return -1;
        }
        int t = sum / n;
        int ls = 0, rs = sum;
        int ans = 0;


        for (int i = 0; i < n; i++) {
            rs -= ms[i];

            // to left sum should deliver t * i[we all need]
            int a = Math.max(t * i - ls, 0);

            // to right sum should deliver (n - i - 1) * t [we all need]
            int b = Math.max((n - 1 - i) * t - rs, 0);

            System.out.println(a + "   " + b);

            ans = Math.max(ans, a + b);

            ls += ms[i];
        }
        return ans;
    }


    public static void main(String[] args) {
        int[] arr = new int[]{0, 3, 0};
        var app = new Solution();
        app.findMinMoves(arr);
    }


}
