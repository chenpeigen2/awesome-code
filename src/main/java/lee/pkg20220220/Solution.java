package lee.pkg20220220;

public class Solution {
//    https://leetcode-cn.com/problems/1-bit-and-2-bit-characters/
    public boolean isOneBitCharacter(int[] bits) {
        int len = bits.length;
        if (bits[len - 1] != 0) return false;
        for (int i = 0; i < len - 1; i++) {
            int bit = bits[i];
            if (bit == 1) {
                if (i + 1 < len - 1) {
                    i++;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    // 最后的一个bit的位置的判断
    public boolean isOneBitCharacter1(int[] bits) {
        int n = bits.length, idx = 0;
        while (idx < n - 1) {
            if (bits[idx] == 0) idx++;
            else idx += 2;
        }
        return idx == n - 1;
    }

    //    Input 'bits' must have last integer 0
    public static void main(String[] args) {
        var app = new Solution();
        // wrong input
        int[] bits = new int[]{1, 0, 1};
        var ans = app.isOneBitCharacter1(bits);
        System.out.println(ans);
    }
}
