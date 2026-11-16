package org.peter.mongo;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonDocumentReader;
import org.junit.jupiter.api.Test;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 验证 POJO codec：普通 Java 对象 ↔ BSON 的双向转换。
 * <p>
 * 这是驱动里最省事也最容易踩坑的部分：字段名映射错了不会报错，
 * 只会静默丢数据。所以这里断言编解码后的<b>实际字段名</b>。
 */
class PojoCodecTest {

    private static CodecRegistry registry() {
        return fromRegistries(
                com.mongodb.MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    private static <T> Document encode(T value, Class<T> type) {
        Codec<T> codec = registry().get(type);
        BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());
        codec.encode(writer, value, EncoderContext.builder().build());
        return Document.parse(writer.getDocument().toJson());
    }

    private static <T> T decode(Document document, Class<T> type) {
        Codec<T> codec = registry().get(type);
        return codec.decode(new BsonDocumentReader(document.toBsonDocument()), DecoderContext.builder().build());
    }

    @Test
    void encodesPojoToBsonDocument() {
        Book book = new Book("Java 并发编程", "Brian", 2019, 8900);

        Document encoded = encode(book, Book.class);

        assertEquals("Java 并发编程", encoded.getString("title"));
        assertEquals("Brian", encoded.getString("author"));
        // 整数字段会被存成 int32/int64，用 get 取值而不是强转
        assertEquals(2019, encoded.get("publishedYear"));
        assertEquals(8900, encoded.get("priceCents"));
    }

    @Test
    void decodesBsonDocumentBackToPojo() {
        Document document = new Document("title", "重构")
                .append("author", "Martin Fowler")
                .append("publishedYear", 2018)
                .append("priceCents", 6500);

        Book book = decode(document, Book.class);

        assertEquals("重构", book.getTitle());
        assertEquals("Martin Fowler", book.getAuthor());
        assertEquals(2018, book.getPublishedYear());
        assertEquals(6500, book.getPriceCents());
    }

    @Test
    void missingOptionalFieldDecodesToNull() {
        // 老数据里没有这个字段时不能炸，只能是 null —— 这是 schema 演进的前提
        Document document = new Document("title", "无作者的书")
                .append("publishedYear", 2020)
                .append("priceCents", 100);

        Book book = decode(document, Book.class);

        assertNull(book.getAuthor());
    }

    @Test
    void demoRunsOffline() {
        String report = new MongoDemo().run();

        assertEquals(true, report.contains("filter: {\"author\": \"张三\"}"), report);
    }
}
