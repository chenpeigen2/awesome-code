import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.json.CDL;
import org.json.JSONObject;

import java.io.*;

public class GsonTest {

    static String s1 = """
            {
                "store": {
            		"book": [
            		    {
            		        "name":"dasd",
            		        "category": "reference",
            		        "author": "Nigel Rees",
            		        "title": "Sayings of the Century",
            		        "isbn": "0-395-19395-8",
            		        "price": 8.95,
            		        "marks" : [3,99,89]
            		    },
            		    {
            		        "category": "fiction",
            		        "author": "Evelyn Waugh",
            		        "title": "Sword of Honour",
            		        "price": 12.99,
            		        "marks" : [3,99,89,34,67567]
            		    },
            		    {
            		        "category": "fiction",
            		        "author": "Herman Melville",
            		        "title": "Moby Dick",
            		        "isbn": "0-553-21311-3",
            		        "price": 8.99,
            		        "marks" : [3,99,89]
            		    },
            		    {
            		        "category": "fiction",
            		        "author": "J. R. R. Tolkien",
            		        "title": "The Lord of the Rings",
            		        "isbn": "0-395-19395-8",
            		        "price": 22.99,
            		        "marks" : []
            		    }
            		]
            	}
            }
            """;

    public static void main(String[] args) throws IOException {

        // gson
        var books_obj = JsonParser.parseString(s1).getAsJsonObject().get("store").getAsJsonObject();

        String s = books_obj.toString();
        System.out.println(s);

        System.out.println();
        // org
        JSONObject jsonObject = new JSONObject(s);
        var jarr = jsonObject.getJSONArray("book");

        var result = CDL.toString(jarr);


        System.out.println(result);

        File f = new File("book.csv");
        if (!f.exists()) {
            f.createNewFile();
        }
        Writer w = new OutputStreamWriter(new FileOutputStream(f));
        BufferedWriter br = new BufferedWriter(w);

        br.write(result);

        br.flush();
        br.close();


        A a = new A();
        Gson g = new Gson();
        String xx = g.toJson(a);

        System.out.println(xx);

        A b = g.fromJson(xx, A.class);
        System.out.println();
    }

    static class A {
        private int a;
        private String b;

        public A() {
            a = 12;
            b = "xff";
        }
    }
}
