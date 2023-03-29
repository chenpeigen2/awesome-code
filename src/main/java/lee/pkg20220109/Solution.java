package lee.pkg20220109;

public class Solution {
    public char slowestKey(int[] releaseTimes, String keysPressed) {
        int max = releaseTimes[0];
        char ans = keysPressed.charAt(0);
        for (int i = 1; i < keysPressed.length(); i++) {
            if ((releaseTimes[i] - releaseTimes[i - 1]) > max) {
                ans = keysPressed.charAt(i);
                max = releaseTimes[i] - releaseTimes[i - 1];
            }
            if ((releaseTimes[i] - releaseTimes[i - 1]) == max) {
                if (ans < keysPressed.charAt(i)) {
                    ans = keysPressed.charAt(i);
                }
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{1, 2};
        var str = "ba";
        var app = new Solution();
        var result = app.slowestKey(arr, str);
        System.out.println(result);
    }
}
