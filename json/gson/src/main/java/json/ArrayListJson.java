package json;

import org.json.JSONObject;

import java.util.ArrayList;

public class ArrayListJson {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");

        JSONObject obj = new JSONObject();
        obj.put("data", list);
        System.out.println(obj.toString());

    }
}
