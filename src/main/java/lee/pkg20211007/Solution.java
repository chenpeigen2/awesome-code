package lee.pkg20211007;

public class Solution {
    public int countSegments(String s) {
        int len = s.length();
        int cnt = 0;
        for (int i = 0; i < len; ) {
            if (s.charAt(i) == ' ') {
                i++;
                continue;
            }
            while (i < len && s.charAt(i) != ' ') i++;
            cnt++;
        }
        return cnt;
    }
}
