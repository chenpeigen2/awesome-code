package featuretest.interact;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandUtils {
    public static void runCommand(String cmd) {
        System.out.println("cmd: " + cmd);
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
            pb.redirectInput(new File("./input.txt"));
            pb.redirectOutput(new File("./output.txt"));
            pb.redirectErrorStream(true);
            pb.start();
            final Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("cmd failed" + e);
        }
    }

    public static void exeCase02() throws IOException {
        var process = Runtime.getRuntime().exec(new String[]{"pwd"}); // 底层就是process-builder

        System.out.println(Runtime.getRuntime().totalMemory());
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) throws IOException {
        runCommand("pwd");
    }
}
