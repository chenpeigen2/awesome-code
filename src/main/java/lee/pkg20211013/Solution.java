package lee.pkg20211013;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public List<String> fizzBuzz(int n) {
        List<String> l = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            StringBuilder sb = new StringBuilder();
            if (i % 3 == 0) {
                sb.append("Fizz");
            }
            if (i % 5 == 0) {
                sb.append("Buzz");
            }
            if (sb.length() == 0) {
                sb.append(i);
            }
            l.add(sb.toString());
        }

        return l;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.fizzBuzz(15);
        System.out.println(result);
    }
}
