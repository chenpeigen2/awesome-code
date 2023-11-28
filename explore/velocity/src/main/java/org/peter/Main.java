package org.peter;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class Main {
    public static void main(final String[] args) {
        try {
            VelocityEngine velocityEngine = new VelocityEngine();

            velocityEngine.init();

            Velocity.init();


            /* lets make a Context and put data into it */
            VelocityContext context = new VelocityContext();
            context.put("name", "Velocity");
            context.put("project", "Jakarta");
            context.put("now", new Date());

            /* lets make our own string to render */
            String str = "We are using $project $name to render this. 中文测试  $!dateFormatUtils.format($!now,'yyyy-MM-dd')";
            StringWriter stringWriter = new StringWriter();
            System.out.println(Velocity.resourceExists("project"));
            Velocity.evaluate(context, stringWriter, "mystring", str);
            System.out.println(" string : " + stringWriter);

        } catch (final Exception e) {
            System.out.print("系统异常：" + e.getMessage());
            e.printStackTrace();
        }

        testStringVelocity();
    }

    private static Properties props = new Properties();

    static {
        props.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        props.setProperty(Velocity.RESOURCE_LOADER, "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    }

    /**
     * 测试字符串模板替换
     */
    private static void testStringVelocity() {
        // 初始化并取得Velocity引擎
        VelocityEngine engine = new VelocityEngine(props);
        // 字符串模版
        String template = "${owner}：您的${type} : ${bill} 在  ${date} 日已支付成功";
        System.out.println(template.contains("${"));
        // 取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        // 把数据填入上下文
        context.put("owner", "nassir");
        context.put("bill", "201203221000029763");
        context.put("type", "订单");
        context.put("date",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        StringWriter writer = new StringWriter();
        engine.evaluate(context, writer, "", template);
        System.out.println(writer.toString().contains("${"));
        System.out.println(writer.toString());

    }
}