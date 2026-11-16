package org.peter.mongo;

/**
 * 被持久化的普通 Java 对象（POJO）。
 * <p>
 * 没有注解、没有父类——{@code PojoCodecProvider.automatic(true)} 靠
 * getter/setter 命名约定自动映射到 BSON 字段。字段名来自
 * {@code getTitle()} → {@code title}，所以 <b>getter 名字就是 schema</b>，
 * 改名等于改数据库字段，要慎重。
 * <p>
 * 必须有无参构造器（驱动反射创建实例时需要）。
 */
public class Book {

    private String title;
    private String author;
    private int publishedYear;
    private long priceCents;

    public Book() {
    }

    public Book(String title, String author, int publishedYear, long priceCents) {
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.priceCents = priceCents;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public long getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(long priceCents) {
        this.priceCents = priceCents;
    }

    @Override
    public String toString() {
        return "Book{title=" + title + ", author=" + author
                + ", publishedYear=" + publishedYear + ", priceCents=" + priceCents + "}";
    }
}
