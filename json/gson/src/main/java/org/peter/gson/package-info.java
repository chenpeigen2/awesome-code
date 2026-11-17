/**
 * Gson 用法示例。
 * <p>
 * 这个包里的类原来都放在<b>默认包</b>（没有 package 声明），后果是：
 * <ul>
 *   <li>任何其它包的代码都无法 import 它们，只能靠反射或复制粘贴；</li>
 *   <li>JDK 9+ 的模块系统直接禁止从具名模块引用默认包；</li>
 *   <li>IDE 的「移动类」重构会拒绝操作默认包的类。</li>
 * </ul>
 * 一个 JSON 库的示例代码放在默认包里，等于把自己隔离起来。
 * <p>
 * 相关文档：<a href="https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/module-summary.html">Gson Javadoc</a>
 */
package org.peter.gson;
