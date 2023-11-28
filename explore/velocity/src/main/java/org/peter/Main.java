package org.peter;

import java.io.StringWriter;
import java.util.Date;

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
            Velocity.evaluate(context, stringWriter, "mystring", str);
            System.out.println(" string : " + stringWriter);

        } catch (final Exception e) {
            System.out.print("系统异常：" + e.getMessage());
            e.printStackTrace();
        }
    }
}