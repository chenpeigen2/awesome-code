package lee.pkg20220223;

public class Solution {
    public String reverseOnlyLetters(String s) {
        char[] ans = s.toCharArray();
        int l = 0, r = ans.length - 1;
        while (l < r) {
            if (!Character.isLetter(ans[l])) {
                l++;
                continue;
            }
            if (!Character.isLetter(ans[r])) {
                r--;
                continue;
            }
            char tmp = ans[l];
            ans[l] = ans[r];
            ans[r] = tmp;
            l++;
            r--;
        }
        return new String(ans);
    }

    public static void main(String[] args) {
        var app = new Solution();
        var str = "Test1ng-Leet=code-Q!";
        var ans = app.reverseOnlyLetters(str);
        System.out.println(ans);
    }
}
