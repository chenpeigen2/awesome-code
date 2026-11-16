package org.peter.mongo;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * 常用查询/更新构造的集中处。
 * <p>
 * 单独抽出来的理由：这些 Bson 构造是纯函数，可以完全离线断言。
 * 如果散落在 main 方法里，就只能靠连真库来「看打印对不对」，
 * 而那种验证方式连字段名拼错都发现不了。
 */
public final class MongoQuery {

    private MongoQuery() {
    }

    /** 精确匹配作者。 */
    public static Bson byAuthor(String author) {
        return Filters.eq("author", author);
    }

    /**
     * 价格区间。
     * <p>
     * 用 {@code Filters.and(gte, lte)} 会得到 {@code {$and:[{priceCents:{$gte}},{priceCents:{$lte}}]}}，
     * 语义正确但不是惯用写法；直接构造单字段子文档得到
     * {@code {priceCents:{$gte:..,$lte:..}}}，更短、更易读，也是 explain 里
     * 索引边界解析最直接的形态。
     */
    public static Bson priceBetween(long minCents, long maxCents) {
        return new Document("priceCents",
                new Document("$gte", minCents).append("$lte", maxCents));
    }

    /**
     * 同时改标题和价格。
     * <p>
     * 必须用 {@code $set}：直接传一个裸文档给 {@code updateOne} 会整条替换，
     * 未列出的字段全部丢失——这是 MongoDB 最经典的数据事故。
     */
    public static Bson renameAndReprice(String title, long priceCents) {
        return Updates.combine(
                Updates.set("title", title),
                Updates.set("priceCents", priceCents));
    }

    public static Bson sortByPriceDescending() {
        return Sorts.descending("priceCents");
    }

    /** 只取标题，显式排除 _id（默认会带上）。 */
    public static Bson titleOnlyProjection() {
        return Projections.fields(
                Projections.include("title"),
                Projections.excludeId());
    }

    /** 复合索引：作者升序 + 出版年降序，匹配「按作者查、按年份倒序」的查询。 */
    public static Bson authorYearIndexKeys() {
        return Indexes.compoundIndex(
                Indexes.ascending("author"),
                Indexes.descending("publishedYear"));
    }
}
