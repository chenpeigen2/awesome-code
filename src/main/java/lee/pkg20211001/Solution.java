package lee.pkg20211001;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {
    public String destCity(List<List<String>> paths) {
        Set<String> s = new HashSet<>();
//        [["jMgaf WaWA","iinynVdmBz"],[" QCrEFBcAw","wRPRHznLWS"],
//        ["iinynVdmBz","OoLjlLFzjz"],["OoLjlLFzjz"," QCrEFBcAw"],
//        ["IhxjNbDeXk","jMgaf WaWA"],["jmuAYy vgz","IhxjNbDeXk"]]

//        "OoLjlLFzjz" ===output

//        "wRPRHznLWS" ===expected

//        "jMgaf WaWA" -> "iinynVdmBz" : tmp
//        " QCrEFBcAw" -> "wRPRHznLWS" : tmp real
//        "iinynVdmBz" -> "OoLjlLFzjz" : tmp
//        "OoLjlLFzjz" -> " QCrEFBcAw"
//        "IhxjNbDeXk" -> "jMgaf WaWA"
//        "jmuAYy vgz" -> "IhxjNbDeXk"

        for (List<String> l : paths) {
            s.add(l.get(0));
        }

        for (List<String> t : paths) {
            if (!s.contains(t.get(1))) {
                return t.get(1);
            }
        }

        return "";
    }
}