package org.peter.mongo;

import com.mongodb.MongoClientSettings;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * 演示入口，<b>完全离线</b>：不连 MongoDB 也能跑。
 * <p>
 * 运行：{@code ./gradlew :database:mongodb:demo}
 * <p>
 * 需要真库的完整示例见 {@code MongoIntegrationTest}（设 {@code MONGO_URI} 后才执行）。
 */
public final class MongoDemo {

    public static void main(String[] args) {
        System.out.print(new MongoDemo().run());
    }

    public String run() {
        StringBuilder out = new StringBuilder();
        out.append("MongoDB driver: queries are plain BSON documents\n");

        out.append("filter: ").append(toJson(MongoQuery.byAuthor("张三"))).append('\n');
        out.append("range:  ").append(toJson(MongoQuery.priceBetween(1000, 5000))).append('\n');
        out.append("update: ").append(toJson(MongoQuery.renameAndReprice("新标题", 2999))).append('\n');
        out.append("sort:   ").append(toJson(MongoQuery.sortByPriceDescending())).append('\n');
        out.append("index:  ").append(toJson(MongoQuery.authorYearIndexKeys())).append('\n');

        CodecRegistry registry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        Book book = new Book("Java 并发编程", "Brian", 2019, 8900);
        Codec<Book> codec = registry.get(Book.class);
        BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());
        codec.encode(writer, book, EncoderContext.builder().build());
        Document stored = Document.parse(writer.getDocument().toJson());
        out.append("pojo encoded: ").append(stored.toJson()).append('\n');

        Book decoded = codec.decode(
                new BsonDocumentReader(stored.toBsonDocument()), DecoderContext.builder().build());
        out.append("pojo decoded: ").append(decoded).append('\n');
        out.append("round trip preserved title: ")
                .append(book.getTitle().equals(decoded.getTitle()));
        return out.toString();
    }

    /** 把任意 Bson 渲染成 JSON，方便肉眼核对；这也正是测试断言的形式。 */
    private static String toJson(Bson bson) {
        return bson.toBsonDocument().toJson();
    }
}
