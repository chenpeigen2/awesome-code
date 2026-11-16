package org.peter.mongo;

import org.bson.BsonDocument;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证查询构造产出的 BSON 文档。
 * <p>
 * 为什么不连真库：BSON 构造是纯函数，断言它产出的文档比断言「查到了几条记录」
 * 更精确——后者一旦数据集变了就假失败。真库的集成测试单独放在
 * {@code MongoIntegrationTest}，没有 mongod 时自动跳过。
 */
class MongoQueryTest {

    /**
     * 按 JSON 文本比较，而不是按 BsonDocument.equals。
     * <p>
     * BsonDocument.equals 区分具体实现类（BsonDocumentWrapper vs BsonDocument）
     * 和数值宽度（int32 vs int64），明明语义相同的文档会判不等。
     * 比较 JSON 才是「看起来一样就是一样」。
     */
    private static void assertBsonEquals(String expectedJson, Bson actual) {
        assertEquals(BsonDocument.parse(expectedJson).toJson(),
                actual.toBsonDocument().toJson());
    }

    @Test
    void equalityFilterProducesExpectedDocument() {
        assertBsonEquals("{\"author\": \"张三\"}", MongoQuery.byAuthor("张三"));
    }

    @Test
    void rangeFilterCombinesTwoBounds() {
        // 价格区间最容易写错：$gte/$lte 必须落在同一个字段的子文档里
        assertBsonEquals("{\"priceCents\": {\"$gte\": 1000, \"$lte\": 5000}}",
                MongoQuery.priceBetween(1000, 5000));
    }

    @Test
    void setUpdateWrapsFieldsUnderSetOperator() {
        // 少了 $set 会把整个文档替换掉，是线上事故的常见来源
        assertBsonEquals("{\"$set\": {\"title\": \"新标题\", \"priceCents\": 2999}}",
                MongoQuery.renameAndReprice("新标题", 2999));
    }

    @Test
    void sortAndProjectionAreBuiltIndependently() {
        assertBsonEquals("{\"priceCents\": -1}", MongoQuery.sortByPriceDescending());
        assertBsonEquals("{\"title\": 1, \"_id\": 0}", MongoQuery.titleOnlyProjection());
    }

    @Test
    void indexDefinitionUsesAscendingOrder() {
        assertBsonEquals("{\"author\": 1, \"publishedYear\": -1}",
                MongoQuery.authorYearIndexKeys());
    }

    @Test
    void filtersComposeThroughFiltersAnd() {
        // 复合查询：两个条件都要满足。Bson 只是标记接口，没有 .and()，
        // 组合要靠 Filters.and —— 这里验证的是两个构造器的输出能被正确嵌套。
        Bson filter = Filters.and(
                MongoQuery.byAuthor("张三"),
                MongoQuery.priceBetween(1000, 5000));

        assertBsonEquals("""
                {"$and": [{"author": "张三"}, {"priceCents": {"$gte": 1000, "$lte": 5000}}]}
                """, filter);
    }
}
