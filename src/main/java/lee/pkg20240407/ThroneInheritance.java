package lee.pkg20240407;

import java.util.*;

// https://leetcode.cn/problems/throne-inheritance/solutions/2726886/python3javacgotypescript-yi-ti-yi-jie-du-bm16/?envType=daily-question&envId=2024-04-07
public class ThroneInheritance {

    private String king;
    private Set<String> dead = new HashSet<>();
    private Map<String, List<String>> g = new HashMap<>();

    public ThroneInheritance(String kingName) {
        king = kingName;
    }

    public void birth(String parentName, String childName) {
        g.computeIfAbsent(parentName, k -> new ArrayList<>()).add(childName);
    }

    public void death(String name) {
        dead.add(name);
    }

    public List<String> getInheritanceOrder() {
        List<String> ans = new ArrayList<>();
        dfs(ans, king);
        return ans;
    }

    private void dfs(List<String> ans, String x) {
        if (!dead.contains(x)) {
            ans.add(x);
        }
        for (String y : g.getOrDefault(x, List.of())) {
            dfs(ans, y);
        }
    }
}