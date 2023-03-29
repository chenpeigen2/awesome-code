package lee.pkg20210914;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// tmp ---> s
public class Solution {

    // 双指针不仅仅是左右，而且还可以顺序
    public String findLongestWord(String s, List<String> dictionary) {

//        长度不同的字符串，按照字符串长度排倒序；
//        长度相同的，则按照字典序排升序。
        Collections.sort(dictionary, (a, b) -> {
            if (a.length() != b.length()) return b.length() - a.length();
            return a.compareTo(b);
        });

        int n = s.length();

//        dictionary.stream().forEach(System.out::println);

        for (String ss : dictionary) {


            int m = ss.length();

            // i means 最初
            // j means target
//            System.out.println(front);
//            System.out.println(tail);
//            System.out.println("------------");

            int i = 0, j = 0;
            while (i < n && j < m) {
                if (s.charAt(i) == ss.charAt(j)) j++;
                i++;
            }
            if (j == m) return ss;
        }
        return "";
    }

    public static void main(String[] args) {
        var s = "abpcplea";


        List<String> l = new ArrayList<>() {{
            add("a");
            add("b");
            add("c");
        }};
        var app = new Solution();
        var result = app.findLongestWord(s, l);
        System.out.println(result);
    }
}
