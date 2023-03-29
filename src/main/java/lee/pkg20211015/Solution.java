package lee.pkg20211015;

public class Solution {
    public String countAndSay(int n) {
        String result = "1";
        for (int i = 2; i <= n; i++) {
            StringBuilder sb = new StringBuilder();
            int j = 0, k = 0;
            int len = result.length();
//            j   k
            while (k < len) {
                if (result.charAt(j) == result.charAt(k)) {
                    k++;
                } else {
                    int cnt = k - j;
                    sb.append(cnt).append(result.charAt(j));
                    j = k;
                }
            }
            int cnt = k - j;
            sb.append(cnt).append(result.charAt(j));
            result = sb.toString();
        }
        return result;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.countAndSay(4);
        System.out.println(result);
    }
}
