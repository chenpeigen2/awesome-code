package org.peter.couchdb;

import com.google.gson.JsonObject;

/**
 * 演示入口：建库、插文档、查文档。
 * <p>
 * 运行：{@code ./gradlew :database:couchdb:demo}（需要真实 CouchDB）
 * <p>
 * 连接参数从环境变量读：{@code COUCHDB_URL} / {@code COUCHDB_USER} /
 * {@code COUCHDB_PASSWORD}。没有真实实例时，{@link CouchDbClientTest} 里的
 * 假服务器覆盖了同样的代码路径，所以 demo 不会被「跑不起来」掩盖问题。
 */
public final class CouchDbDemo {

    private final CouchDbClient client;

    public CouchDbDemo(CouchDbClient client) {
        this.client = client;
    }

    public static void main(String[] args) {
        System.out.print(new CouchDbDemo(CouchDbClient.fromEnvironment()).run());
    }

    public String run() {
        JsonObject created = client.createDatabase("demo");
        JsonObject inserted = client.insertDocument("demo", """
                {"title":"CouchDB 入门","servings":4}
                """);
        JsonObject found = client.findDocument("demo", inserted.get("id").getAsString());

        return "CouchDB demo\n"
                + "create: " + (created.get("ok").getAsBoolean() ? "ok" : created) + "\n"
                + "insert: id=" + inserted.get("id").getAsString() + "\n"
                + "find: " + found + "\n";
    }
}
