package lee.pkg20211110;

public class Solution {
    public int findPoisonedDuration(int[] timeSeries, int duration) {

        int sum = 0;
        int index = -1;
        for (int x : timeSeries) {
            if (index >= x) {
                int overlap = index - x + 1;
                sum = sum - overlap;
            }
            sum += duration;
            index = x + duration - 1;
        }
        return sum;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.findPoisonedDuration(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, 1);
        System.out.println(result);
    }
}
