package featuretest.text.format;

import java.text.MessageFormat;

public class Message {

    public static void main(String[] args) {
        String pattern = "The answer to {0} is {1}";
        Object[] arguments = {"everything", 42};
        String formattedString = MessageFormat.format(pattern, arguments);
        System.out.println(formattedString); // The answer to everything is 42


        int fileCount = 1273;
        String diskName = "MyDisk";
        Object[] testArgs = {Long.valueOf(fileCount), diskName};

        MessageFormat form = new MessageFormat(
                "The disk \"{1}\" contains {0} file(s).");

        System.out.println(form.format(testArgs));

    }
}
