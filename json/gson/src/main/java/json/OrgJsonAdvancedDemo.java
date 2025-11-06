package json;

import org.json.*;

import java.io.*;
import java.util.*;

public class OrgJsonAdvancedDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== org.json 全面示例 ===");

        // 1️⃣ 构建基本 JSONObject
        JSONObject user = new JSONObject();
        user.put("id", 101);
        user.put("name", "Alice");
        user.put("active", true);
        user.put("score", 98.5);

        System.out.println("\n[1] 基本 JSONObject：");
        System.out.println(user.toString(2));

        // 2️⃣ 嵌套对象和数组
        JSONArray skills = new JSONArray(Arrays.asList("Java", "Kotlin", "Android"));
        JSONObject profile = new JSONObject();
        profile.put("user", user);
        profile.put("skills", skills);
        profile.put("projects", new JSONArray()
                .put(new JSONObject().put("name", "AppA").put("stars", 500))
                .put(new JSONObject().put("name", "AppB").put("stars", 1200)));

        System.out.println("\n[2] 嵌套 JSON 结构：");
        System.out.println(profile.toString(2));

        // 3️⃣ 安全读取 (optXxx)
        System.out.println("\n[3] 安全读取示例：");
        String nickname = profile.optString("nickname", "未设置");
        System.out.println("nickname = " + nickname);

        // 4️⃣ 动态修改键值
        user.put("active", false);
        user.remove("score");
        user.put("joined", "2023-06-01");
        System.out.println("\n[4] 修改后的 user：");
        System.out.println(user.toString(2));

        // 5️⃣ 合并 JSON
        JSONObject extra = new JSONObject();
        extra.put("role", "engineer");
        extra.put("level", "senior");
        mergeJson(profile.getJSONObject("user"), extra);
        System.out.println("\n[5] 合并后的 user：");
        System.out.println(profile.getJSONObject("user").toString(2));

        // 6️⃣ 从字符串解析 JSON
        String jsonStr = "{\"city\":\"Tokyo\",\"temp\":18.5,\"weather\":\"Sunny\"}";
        JSONObject weather = new JSONObject(jsonStr);
        System.out.println("\n[6] 解析 JSON 字符串：");
        System.out.println(weather.toString(2));

        // 7️⃣ 遍历 JSONObject
        System.out.println("\n[7] 遍历 JSONObject 键值：");
        for (String key : weather.keySet()) {
            System.out.println(key + " -> " + weather.get(key));
        }

        // 8️⃣ JSONArray 操作
        JSONArray arr = new JSONArray();
        arr.put("foo");
        arr.put(123);
        arr.put(true);
        arr.put(JSONObject.NULL);
        System.out.println("\n[8] JSONArray 内容：" + arr.toString());

        // 9️⃣ JSON ↔ Map/List
        Map<String, Object> map = profile.toMap();
        JSONObject fromMap = new JSONObject(map);
        System.out.println("\n[9] 转换为 Map 后再构建 JSON：");
        System.out.println(fromMap.toString(2));

        // 🔟 JSON 与 XML 互转
        String xml = XML.toString(profile, "profile");
        JSONObject fromXml = XML.toJSONObject("<profile><user><id>1</id><name>Bob</name></user></profile>");
        System.out.println("\n[10] XML 转 JSON：");
        System.out.println(fromXml.toString(2));
        System.out.println("\nJSON 转 XML：\n" + xml);

        // 11️⃣ JSON ↔ CSV（CDL）
        JSONArray csvArray = new JSONArray();
        csvArray.put(new JSONObject().put("id", 1).put("name", "Tom"));
        csvArray.put(new JSONObject().put("id", 2).put("name", "Jerry"));
        String csv = CDL.toString(csvArray);
        System.out.println("\n[11] JSON 转 CSV：\n" + csv);
        JSONArray parsedCsv = CDL.toJSONArray(csv);
        System.out.println("CSV 转 JSON：\n" + parsedCsv.toString(2));

        // 12️⃣ JSON 文件读写
        String file = "profile.json";
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(profile.toString(2));
        }
        System.out.println("\n[12] 写入文件成功: " + file);

        String loaded;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            loaded = br.lines().reduce("", (a, b) -> a + b + "\n");
        }
        System.out.println("读取文件内容：\n" + loaded);

        // 13️⃣ 异常处理
        System.out.println("\n[13] 异常捕获示例：");
        try {
            user.getString("non_exist");
        } catch (JSONException e) {
            System.out.println("捕获异常: " + e.getMessage());
        }

        System.out.println("\n=== 示例结束 ===");
    }

    /**
     * 合并两个 JSONObject（浅拷贝）
     */
    public static void mergeJson(JSONObject target, JSONObject source) {
        for (String key : source.keySet()) {
            target.put(key, source.get(key));
        }
    }
}

