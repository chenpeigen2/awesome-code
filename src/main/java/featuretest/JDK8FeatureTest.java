package featuretest;


class Something {

    // constructor methods
    Something() {
    }

    Something(String something) {
        System.out.println(something);
    }

    // static methods
    static String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }

    // object methods
    String endWith(String s) {
        return String.valueOf(s.charAt(s.length() - 1));
    }

    void endWith() {
    }
}

interface IConvert<F, T> {
    T convert(F form);
}

public class JDK8FeatureTest {
    // static methods
    IConvert<String, String> convert = Something::startsWith;

    IConvert<String, String> convert1 = new IConvert<String, String>() {
        @Override
        public String convert(String form) {
            return Something.startsWith(form);
        }
    };

    IConvert<String, String> convert2 = form -> Something.startsWith(form);


    // object methods
    Something something = new Something();
    IConvert<String, String> converter = something::endWith;
    String converted = converter.convert("Java");


    // constructor methods
    IConvert<String, Something> convert3 = Something::new;
    String something1 = convert.convert("constructors");

//    String converted = convert.convert("123");
}
