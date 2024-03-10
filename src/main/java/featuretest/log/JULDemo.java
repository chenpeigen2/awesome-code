package featuretest.log;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class JULDemo {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public static void main(String[] args) throws IOException {
        // java.util.logging
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JULDemo.class.getName());
        FileHandler fileHandler = new FileHandler("status.log");
        fileHandler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        });
        logger.addHandler(fileHandler);

        logger.info("This is an info message");
        logger.severe("This is an error message"); // == ERROR
        logger.fine("Here is a debug message"); // == DEBUG


    }
}
