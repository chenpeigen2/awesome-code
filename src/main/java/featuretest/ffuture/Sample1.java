package featuretest.ffuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Sample1 {
    void testFutureTask() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            CompletableFuture<Integer> fr = new CompletableFuture<>();
            taskList.add(fr);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fr.complete(1);
                }
            }).start();
        }

        int sum = 0;
        for (int i = 0; i < 1000; i++) {
            CompletableFuture<Integer> completableFuture = taskList.get(i);
            int vv = completableFuture.thenApply(res -> {
                System.out.println(res);
                return res + 1;
            }).get();
            sum += vv;
        }

        System.out.println(sum);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var app = new Sample1();
        app.testFutureTask();
    }
}
