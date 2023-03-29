package org.peter;

import lombok.SneakyThrows;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//https://square.github.io/okhttp/4.x/okhttp/okhttp3/

public class DbUtils {

    public static String Cookie;

    private final static OkHttpClient client;

    private final static Map<String, String> m;

    private static volatile boolean authed = false;

    private static final String HOST = "http://localhost:5984/";

    static {
        client = new OkHttpClient();
        m = new HashMap<>() {{
            put("Accept", "application/json");
            put("Content-Type", "application/json");
        }};
    }

    /**
     * below is couchdb api
     */
    @SneakyThrows
    public static void auth() {
        RequestBody body = new FormBody.Builder()
                .add("name", "admin")
                .add("password", "123456").build();
        var r = new Request.Builder().url(HOST + "_session")
                .headers(Headers.of(m))
                .method("POST", body)
                .build();

        var res = client.newCall(r).execute();
        var cookie_list = res.headers().values("Set-Cookie");
        Cookie = cookie_list.get(0);
        authed = true;
        m.put("Cookie", Cookie);

    }

    /**
     * below is the database api
     */
    public static void createDataBase(String db_name) {
        if (!authed) auth();
        var headers = Headers.of(m);
        RequestBody body = new FormBody.Builder().build();
        var r = new Request.Builder().url(HOST + db_name).headers(headers)
                .method("PUT", body)
                .build();
        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            authed = false;
            e.printStackTrace();
        }
    }

    public static void getDataBaseInfo(String db_name) {
        if (!authed) auth();
        var headers = Headers.of(m);
        var r = new Request.Builder().url(HOST + db_name).headers(headers).build();
        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            authed = false;
            e.printStackTrace();
        }
    }

    public static void deleteDataBase(String db_name) {
        if (!authed) auth();
        var headers = Headers.of(m);
        var r = new Request.Builder().url(HOST + db_name).headers(headers)
                .delete()
                .build();
        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            authed = false;
            e.printStackTrace();
        }
    }

    public static <T> void insertDocumentToDataBase(String db_name, T data) {
        if (!authed) auth();
        var header = Headers.of(m);

        RequestBody body = null;

        if (data instanceof String s) {
            body = RequestBody.create(s, MediaType.get("application/json"));
        }

        if (data instanceof File f) {
            body = RequestBody.create(f, MediaType.get("application/json"));
        }

        if (body == null) throw new IllegalArgumentException("Type error");

        var r = new Request.Builder().url(HOST + db_name).headers(header)
                .method("POST", body)
                .build();

        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());

        } catch (IOException e) {
            e.printStackTrace();
            authed = false;
        }

    }

    public static void findAllDocs(String db_name) {
        if (!authed) auth();
        var header = Headers.of(m);

        var r = new Request.Builder().url(HOST + db_name + "/_all_docs").headers(header)
                .build();

        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            authed = false;
        }
    }

    public static void findAllDocs(String db_name, String condition) {
        if (!authed) auth();
        var header = Headers.of(m);

        var body = RequestBody.create(condition, MediaType.get("application/json"));

        var r = new Request.Builder().url(HOST + db_name + "/_all_docs").headers(header)
                .method("POST", body)
                .build();

        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            authed = false;
        }
    }

    /**
     * @param db_name db,name we are going to use
     */
    public static void filterDocs(String db_name) {
        String filter = """
                     {
                         "selector": {
                             "servings": {"$gt": 1}
                         },
                         "fields": ["_id", "_rev", "servings", "title"],
                         "sort": [{"servings": "asc"}],
                         "limit": 2,
                         "skip": 0,
                         "execution_stats": true
                     }
                """;
        filterDocs(db_name, filter);
    }

    public static void filterDocs(String db_name, String filter) {
        if (!authed) auth();
        var header = Headers.of(m);
        var body = RequestBody.create(filter, MediaType.get("application/json"));
        var r = new Request.Builder().url(HOST + db_name + "/_find").headers(header)
                .method("POST", body)
                .build();
        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param db_name db-name we are about to use to create the idx
     */
    public static void createIndex(String db_name) {
        var info = """
                {"index": {"fields": ["servings"]}, "name": "servings-index", "type": "json"}
                """;
        createIndex(db_name, info);
    }

    public static void createIndex(String db_name, String info) {
        if (!authed) auth();
        var header = Headers.of(m);
        var body = RequestBody.create("""
                {"index": {"fields": ["servings"]}, "name": "servings-index", "type": "json"}
                """, MediaType.get("application/json"));
        var r = new Request.Builder().url(HOST + db_name + "/_index").headers(header)
                .method("POST", body)
                .build();
        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * below is the document api
     */
    public static void findSpecDoc(String db_name, String docId) {
        if (!authed) auth();
        var header = Headers.of(m);
        var r = new Request.Builder().url(HOST + db_name + "/" + docId).headers(header)
                .get()
                .build();
        try {
            var res = client.newCall(r).execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        var data = """
                {
                    "servings": 4,
                    "subtitle": "Delicious with fresh bread",
                    "title": "Fish Stew"
                }
                """;

        var keys = """
                {
                     "keys" : [
                         "19be9942f7136548f7dfb284cc006f75",
                         "19be9942f7136548f7dfb284cc006354"
                     ]
                 }
                 """;

//        createDataBase("helloworld");
//        getDataBaseInfo("helloworld");
//        insertDocumentToDataBase("helloworld", data);
        findAllDocs("helloworld");

//        createIndex("helloworld");

//        filterDocs("helloworld");

//        findAllDocs("helloworld", keys);

//        deleteDataBase("helloworld");

//        deleteDataBase("helloworld");

        findSpecDoc("helloworld", "19be9942f7136548f7dfb284cc006354");
    }
}
