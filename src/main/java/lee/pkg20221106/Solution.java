package lee.pkg20221106;

public class Solution {
    //    https://leetcode.cn/problems/goal-parser-interpretation/description/
    public String interpret(String command) {
        return command.replaceAll("\\(\\)", "o").replaceAll("(al)", "al");
    }
}
