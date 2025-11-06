package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonDemo {

    public static void main(String[] args) {
        System.out.println("=== org.json 使用示例 ===\n");

        // 1️⃣ 创建 JSONObject
        JSONObject user = new JSONObject();
        user.put("name", "Alice");
        user.put("age", 25);
        user.put("city", "Shanghai");

        System.out.println("创建的 JSONObject:");
        System.out.println(user.toString(2)); // 格式化输出

        // 2️⃣ 从 Map 创建 JSONObject
        Map<String, Object> map = new HashMap<>();
        map.put("language", "Java");
        map.put("version", 17);
        JSONObject fromMap = new JSONObject(map);

        System.out.println("\n从 Map 创建的 JSONObject:");
        System.out.println(fromMap.toString(2));

        // 3️⃣ 嵌套结构
        user.put("skills", new JSONArray()
                .put("Android")
                .put("Spring Boot")
                .put("Kotlin"));

        JSONObject profile = new JSONObject();
        profile.put("user", user);
        profile.put("active", true);

        System.out.println("\n嵌套结构示例:");
        System.out.println(profile.toString(2));

        // 4️⃣ 从字符串解析
        String jsonStr = "{\"name\":\"Bob\",\"age\":30,\"married\":false}";
        JSONObject parsed = new JSONObject(jsonStr);
        System.out.println("\n解析 JSON 字符串:");
        System.out.println("name = " + parsed.getString("name"));
        System.out.println("age = " + parsed.getInt("age"));
        System.out.println("married = " + parsed.getBoolean("married"));

        // 5️⃣ JSONArray 示例
        JSONArray arr = new JSONArray();
        arr.put(new JSONObject().put("id", 1).put("value", "A"));
        arr.put(new JSONObject().put("id", 2).put("value", "B"));

        System.out.println("\nJSONArray 内容:");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            System.out.println("id=" + obj.getInt("id") + ", value=" + obj.getString("value"));
        }

        // 6️⃣ 异常处理
        try {
            System.out.println("\n尝试获取不存在字段:");
            System.out.println(parsed.getString("non_exist")); // 会抛异常
        } catch (JSONException e) {
            System.out.println("捕获 JSONException: " + e.getMessage());
        }

        // 7️⃣ optXxx 使用
        String fallback = parsed.optString("nickname", "无昵称");
        System.out.println("\n使用 optString 安全读取: nickname = " + fallback);

        System.out.println("\n=== 示例结束 ===");
    }
}

