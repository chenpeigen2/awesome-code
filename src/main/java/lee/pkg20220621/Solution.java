package lee.pkg20220621;

public class Solution {
//    https://leetcode.cn/problems/defanging-an-ip-address/
    public String defangIPaddr(String address) {
        return address.replace(".", "[.]");
    }
}
