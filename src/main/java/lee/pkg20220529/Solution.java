package lee.pkg20220529;

public class Solution {
    //    https://leetcode.cn/problems/validate-ip-address/
    public String validIPAddress(String queryIP) {

        String ipv4Pattern = "^([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";
        String ipv6Pattern = "^[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}$";

        if (queryIP.matches(ipv4Pattern)) {
            return "IPv4";
        }

        if (queryIP.matches(ipv6Pattern)) {
            return "IPv6";
        }

        return "Neither";
    }
}
