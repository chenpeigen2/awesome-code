package lee.pkg20210905;

public class Solution {
    public static void main(String[] args) {
        int x = -4;
        System.out.println(x >> 1);

        var app = new Solution();
        app.binaryToInteger(new int[]{1, 0, 1});
    }

    public void binaryToInteger(int n) {
        // 111
        //

    }

    public int binaryToInteger(int[] n) {
        // 1 0 1;
        int result = 0;
        int count = 0;
        System.out.println(n[0] << count);
        for (int i = n.length - 1; i >= 0; i--) {
            int tmp = n[i] << count;
            result = result + tmp;
            count++;
        }

        return result;
    }
}
