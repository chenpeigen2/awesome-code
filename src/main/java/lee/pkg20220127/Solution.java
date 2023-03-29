package lee.pkg20220127;

public class Solution {
    public int countValidWords(String sentence) {
        int ans = 0;

        // flag this str is the start or the middle
        boolean flag = true;
        // flag the str is meaningful
        boolean shouldContinue = false;

        // flag has1before
        boolean has1before = false;
        // flag has2before
        boolean has2before = false;

        for (int i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if (ch == ' ') {
                if (!flag && !shouldContinue) {
                    ans++;
                }

                flag = true;
                shouldContinue = false;

                has1before = false;
                has2before = false;

                continue;
            }

            if (shouldContinue) {
                continue;
            }

            // ! .  0 , 0 -
            // flag = true means the start
            if (flag) {
                if (ch >= 'a' && ch <= 'z') {
//                    flag = false;
                } else if (ch == '!' || ch == ',' || ch == '.') {
//                    flag = false;
                    has1before = true;
                } else {
                    shouldContinue = true;
                }
                flag = false;
            } else {
                if (ch >= '0' && ch <= '9') {
                    shouldContinue = true;
                } else if (ch == '!' || ch == ',' || ch == '.') {

                    if (has1before) {
                        shouldContinue = true;
                        continue;
                    }

                    has1before = true;

                    if (i + 1 == sentence.length()) {
//                        shouldContinue = false;
                    } else if (i + 1 < sentence.length() && sentence.charAt(i + 1) == ' ') {
                        //                        shouldContinue = false;
                    } else {
                        shouldContinue = true;
                    }
                } else if (ch == '-') {
                    if (has2before) {
                        shouldContinue = true;
                        continue;
                    }
                    has2before = true;
                    if (i + 1 < sentence.length() && sentence.charAt(i + 1) <= 'z' && sentence.charAt(i + 1) >= 'a') {
                        //                        shouldContinue = false;
                    } else {
                        shouldContinue = true;
                    }
                } else {
                    // 'a' - 'z'
                    if (has1before) {
                        shouldContinue = true;
                    }
                }
            }
        }

        if (!flag && !shouldContinue) {
            ans++;
        }

        return ans;
    }


    public int countValidWords1(String sentence) {
        int n = sentence.length();
        int l = 0, r = 0;
        int ret = 0;
        while (true) {
            while (l < n && sentence.charAt(l) == ' ') {
                l++;
            }
            if (l >= n) {
                break;
            }
            r = l + 1;
            while (r < n && sentence.charAt(r) != ' ') {
                r++;
            }
            if (isValid(sentence.substring(l, r))) { // 判断根据空格分解出来的 token 是否有效
                ret++;
            }
            l = r + 1;
        }
        return ret;
    }

    public boolean isValid(String word) {
        int n = word.length();
        boolean hasHyphens = false;
        for (int i = 0; i < n; i++) {
            if (Character.isDigit(word.charAt(i))) {
                return false;
            } else if (word.charAt(i) == '-') {
                if (hasHyphens == true || i == 0 || i == n - 1 || !Character.isLetter(word.charAt(i - 1)) || !Character.isLetter(word.charAt(i + 1))) {
                    return false;
                }
                hasHyphens = true;
            } else if (word.charAt(i) == '!' || word.charAt(i) == '.' || word.charAt(i) == ',') {
                if (i != n - 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        var sentence = "he bought 2 pencils, 3 erasers, and 1  pencil-sharpener.";
        var app = new Solution();
        var ans = app.countValidWords(sentence);
        System.out.println(ans);
    }
}
