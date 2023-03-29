package featuretest.ffuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class Sample {

    void testFutureTask() throws ExecutionException, InterruptedException {
        List<RunnableFuture<Integer>> taskList = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            // make a callable pass it into
            RunnableFuture<Integer> rf = new FutureTask<>(this::task);
            taskList.add(rf);
            new Thread(rf).start();
        }

        // now wait for all tasks
        int sum = 0;
        for (Future<Integer> future : taskList) {
            sum += future.get();
        }

        System.out.println(sum);

    }

    public Integer task() {
        System.out.println(Thread.currentThread().getId());
        return 1;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var app = new Sample();
        app.testFutureTask();
    }
}
