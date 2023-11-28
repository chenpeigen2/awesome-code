package featuretest.juc;

/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * Implementation of {@link Executor} which executes on a provided looper.
 */
public class LooperExecutor implements Executor {


    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = new FutureTask<T>(task);
        execute(ftask);
        return ftask;
    }

    @Override
    public void execute(@NotNull Runnable command) {
        command.run();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LooperExecutor executor = new LooperExecutor();
        var task = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 123;
            }
        });

        System.out.println(task.get());
    }
}
