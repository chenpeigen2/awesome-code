package lee.pkg20211113;

public class Solution {
    public boolean detectCapitalUse(String word) {
        char s = word.charAt(0);
        char e = word.charAt(word.length() - 1);

        if (Character.isUpperCase(s) && Character.isUpperCase(e)) {
            for (int i = word.length() - 2; i > 0; i--) {
                if (!Character.isUpperCase(word.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        if (Character.isLowerCase(e)) {
            for (int i = word.length() - 2; i > 0; i--) {
                if (!Character.isLowerCase(word.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public boolean detectCapitalUse1(String word) {
        if (word.toUpperCase().equals(word)) return true;
        if (word.toLowerCase().equals(word)) return true;
        int n = word.length(), idx = 1;
        if (Character.isUpperCase(word.charAt(0))) {
            while (idx < n && Character.isLowerCase(word.charAt(idx))) idx++;
        }
        return idx == n;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.detectCapitalUse("FlaG");
        System.out.println(result);
    }
}
