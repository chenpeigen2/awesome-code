package org.peter;

import java.io.File;
import java.io.IOException;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        File file = new File("./a.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File zipFile = new File("./a.zip");
        try {
            ZipUtils.zip(file, zipFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
