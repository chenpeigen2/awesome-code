package featuretest.ios;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class TStringReader {
    public static void main(String[] args) throws IOException {
        StringReader reader = new StringReader("hello world");
        char c = (char) reader.read();
        System.out.println(c);
    }
}
