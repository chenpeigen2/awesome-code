package lee.pkg20211226;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    public String[] findOcurrences(String text, String first, String second) {
        String[] strings = text.split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 0; i + 2 < strings.length; i++) {

            boolean flag = (first.equals(strings[i]) ? (second.equals(strings[i + 1]) ? list.add(strings[i + 2]) : false) : false);

//            boolean flag = (first.equals(strings[i]) && (second.equals(strings[i + 1]) && list.add(strings[i + 2])));

            if (strings[i].equals(first)) {
                if (strings[i + 1].equals(second)) {
                    list.add(strings[i + 2]);
                }
            }
        }
        String[] ans = new String[list.size()];
        list.toArray(ans);
        return ans;
    }


    public static void main(String[] args) {
        String text = "alice is a good girl she is a good student", first = "a", second = "good";

        var app = new Solution();
        var ans = app.findOcurrences(text, first, second);
        Arrays.stream(ans).forEach(System.out::println);
    }
}
