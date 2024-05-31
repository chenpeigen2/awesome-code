package lee.acontext;

public class Solution {
    public String compressedString(String word) {
        StringBuilder sb = new StringBuilder();
        int len = word.length();
        int cnt = 0;
        int idx = 0;
        for (int i = 0; i < len; i++) {
            if (cnt == 9) {
                sb.append(cnt).append(word.charAt(idx));
                idx = i;
                cnt = 1;
                continue;
            }
            if (word.charAt(i) != word.charAt(idx)) {
                sb.append(cnt).append(word.charAt(idx));
                idx = i;
                cnt = 0;
            }
            if (word.charAt(i) == word.charAt(idx)) {
                cnt++;
            }
        }
        sb.append(cnt).append(word.charAt(idx));
        return sb.toString();
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.compressedString("abcde");
        System.out.println(ans);
    }
}
