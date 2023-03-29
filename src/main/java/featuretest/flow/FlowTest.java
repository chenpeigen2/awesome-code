package featuretest.flow;

import java.util.List;
import java.util.regex.Pattern;

public class FlowTest {

    private final Pattern pattern;

    public FlowTest() {
        pattern = Pattern.compile("[0-9]*");
    }

    public void testFlow() {
        List<String> list = List.of("1", "2a", "3", "4b", "5c", "6", "7d", "8", "9");

        FlowHelper.flow(list.spliterator())
                .filter(v -> pattern.matcher(v).matches())
                .mapping(FlowBean::new)
                .accept(FlowBean::print);
    }

    static class FlowBean {

        final String result;

        public FlowBean(String result) {
            this.result = result;
        }

        public void print() {
            System.out.println("zzzzzzzzzzzzz yyyyyyyyy uuuuuuuuuuuuuuuuuuu");
            System.out.println("FlowTest" + result);
        }
    }

    public static void main(String[] args) {
        var app = new FlowTest();
        app.testFlow();
    }
}
