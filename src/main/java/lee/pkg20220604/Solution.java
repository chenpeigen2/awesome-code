package lee.pkg20220604;

import java.util.HashSet;
import java.util.Set;

public class Solution {

    //    https://leetcode.cn/problems/unique-email-addresses/
    public int numUniqueEmails(String[] emails) {
        Set<String> emailSet = new HashSet<>();
        for (String email : emails) {
            int i = email.indexOf('@');
            String local = email.substring(0, i).split("\\+")[0]; // 去掉本地名第一个加号之后的部分
            local = local.replace(".", ""); // 去掉本地名中所有的句点
            emailSet.add(local + email.substring(i));
        }
        return emailSet.size();
    }

//    public int numUniqueEmails(String[] emails) {
//        Set<String> hashSet = new HashSet<>();
//        for (String email : emails) {
//            String s = email;
//            s = s.replaceAll("(\\+[a-z]*)|\\.", "");
//            hashSet.add(s);
//        }
//        return hashSet.size();
//    }

    public static void main(String[] args) {
        var s = "test.email+alex@leetcode.com";

        System.out.println(s);
    }
}
