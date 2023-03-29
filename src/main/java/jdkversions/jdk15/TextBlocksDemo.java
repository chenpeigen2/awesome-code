package jdkversions.jdk15;

public class TextBlocksDemo {
    String html = """
            <html>
                <body>
                    <p>Hello, world</p>
                </body>
            </html>
            """;

    public static void main(String[] args) {
        var app = new TextBlocksDemo();
        System.out.println(app.html);
    }
}
