package lee.pkg20211018;

public class Solution {
    public int findComplement(int num) {
        String s = Integer.toBinaryString(num);
        int len = s.length();
        int val = (1 << len) - 1;
        return num ^ val;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.findComplement(5);
        System.out.println(result);
    }
}
