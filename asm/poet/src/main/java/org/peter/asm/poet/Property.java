package org.peter.asm.poet;

/**
 * 生成源码时的一个属性（字段）。
 *
 * @param name 字段名，遵循 Java 命名习惯（小驼峰）
 * @param type 字段的 Java 类型
 */
public record Property(String name, Class<?> type) {

    public Property {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("property name must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("property type must not be null: " + name);
        }
    }

    public static Property of(String name, Class<?> type) {
        return new Property(name, type);
    }

    /** {@code name} → {@code Name}，用于拼 getter 名。 */
    public String capitalizedName() {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
