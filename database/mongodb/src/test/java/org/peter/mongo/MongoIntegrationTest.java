package org.peter.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 真库集成测试：只有设置了 {@code MONGO_URI} 才会跑。
 * <p>
 * 本机默认没有 mongod，所以无条件跳过——但这不代表这个测试没用：
 * 它把「怎么连、怎么建索引、怎么读写」这套流程固化下来，
 * 需要时用 {@code MONGO_URI=mongodb://localhost:27017 ./gradlew :database:mongodb:test} 打开即可。
 * <p>
 * 用环境变量而不是硬编码地址，是为了不让测试依赖某台机器的状态。
 */
@EnabledIfEnvironmentVariable(named = "MONGO_URI", matches = ".+")
class MongoIntegrationTest {

    private static final String DATABASE = "awesome_code_test";
    private static final String COLLECTION = "books";

    private static MongoClient client;
    private static MongoCollection<Book> books;

    @BeforeAll
    static void connect() {
        client = MongoClients.create(new ConnectionString(System.getenv("MONGO_URI")));
        MongoDatabase database = client.getDatabase(DATABASE);
        books = database.getCollection(COLLECTION, Book.class);
        // 每个用例从干净状态开始，避免残留数据影响断言
        books.drop();
    }

    @AfterAll
    static void disconnect() {
        if (client != null) {
            client.getDatabase(DATABASE).drop();
            client.close();
        }
    }

    @Test
    void insertsAndReadsBackThroughPojoCodec() {
        books.insertOne(new Book("Java 并发编程", "Brian", 2019, 8900));

        Book found = books.find(Filters.eq("title", "Java 并发编程")).first();

        assertNotNull(found);
        assertEquals("Brian", found.getAuthor());
        assertEquals(8900, found.getPriceCents());
    }

    @Test
    void rangeQueryReturnsOnlyMatchingDocuments() {
        books.insertMany(List.of(
                new Book("便宜的书", "A", 2020, 500),
                new Book("中间价", "B", 2021, 3000),
                new Book("贵的书", "C", 2022, 9000)));

        List<String> titles = new ArrayList<>();
        books.find(MongoQuery.priceBetween(1000, 5000))
                .forEach(book -> titles.add(book.getTitle()));

        assertEquals(List.of("中间价"), titles);
    }

    @Test
    void indexIsCreatedAndUsable() {
        books.createIndex(Indexes.compoundIndex(
                Indexes.ascending("author"),
                Indexes.descending("publishedYear")));

        for (Document index : books.listIndexes()) {
            if (index.containsKey("key") && index.get("key", Document.class)
                    .containsKey("author")) {
                assertNotNull(index.getString("name"));
                return;
            }
        }
        throw new AssertionError("compound index on author was not created");
    }

    @Test
    void updateWithSetOperatorOnlyTouchesGivenFields() {
        books.insertOne(new Book("原标题", "作者", 2020, 1000));

        books.updateOne(Filters.eq("title", "原标题"), MongoQuery.renameAndReprice("新标题", 2000));

        Book updated = books.find(Filters.eq("title", "新标题")).first();
        assertNotNull(updated, "标题应该被 $set 改掉");
        assertEquals("作者", updated.getAuthor(), "未在 $set 里的字段必须保持不变");
        assertEquals(2000, updated.getPriceCents());
    }
}
