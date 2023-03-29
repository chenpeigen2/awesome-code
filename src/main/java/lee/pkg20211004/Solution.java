package lee.pkg20211004;

public class Solution {

    public String licenseKeyFormatting(String s, int k) {
        int len = s.length();

        StringBuilder sb = new StringBuilder();

        int cnt = 0;

        for (int i = len - 1; i >= 0; i--) {
            if (s.charAt(i) == '-') continue;

            if (cnt == k) {
                cnt = 0;
                sb.append("-");
            }
            sb.append(Character.toUpperCase(s.charAt(i)));
            cnt++;
        }
        return sb.reverse().toString();
    }

    private void hello() {
        System.out.println("hello world");
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.licenseKeyFormatting("2-5g-3-j", 2);
        System.out.println(result);
    }
}