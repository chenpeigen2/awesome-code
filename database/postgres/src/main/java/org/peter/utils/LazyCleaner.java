package org.peter.utils;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.time.Duration;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LazyCleaner is a utility class that allows to register objects for deferred cleanup.
 * <p>Note: this is a driver-internal class</p>
 */
public class LazyCleaner {
    private static final Logger LOGGER = Logger.getLogger(
            LazyCleaner.class.getName());
    private static final LazyCleaner instance =
            new LazyCleaner(
                    Duration.ofMillis(3000),
                    "PostgreSQL-JDBC-Cleaner"
            );

    public interface Cleanable<T extends Throwable> {
        void clean() throws T;
    }

    public interface CleaningAction<T extends Throwable> {
        void onClean(boolean leak) throws T;
    }

    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final long threadTtl;
    private final ThreadFactory threadFactory;
    private boolean threadRunning = false;
    private int watchedCount = 0;
    private LazyCleaner.Node<?> first;

    /**
     * Returns a default cleaner instance.
     * <p>Note: this is driver-internal API.</p>
     *
     * @return the instance of LazyCleaner
     */
    public static LazyCleaner getInstance() {
        return instance;
    }

    public LazyCleaner(Duration threadTtl, final String threadName) {
        this(threadTtl, runnable -> {
            Thread thread = new Thread(runnable, threadName);
            thread.setDaemon(true);
            return thread;
        });
    }

    private LazyCleaner(Duration threadTtl, ThreadFactory threadFactory) {
        this.threadTtl = threadTtl.toMillis();
        this.threadFactory = threadFactory;
    }

    public <T extends Throwable> Cleanable<T> register(Object obj, CleaningAction<T> action) {
        assert obj != action : "object handle should not be the same as cleaning action, otherwise"
                + " the object will never become phantom reachable, so the action will never trigger";
        return add(new Node<T>(obj, action));
    }

    public synchronized int getWatchedCount() {
        return watchedCount;
    }

    public synchronized boolean isThreadRunning() {
        return threadRunning;
    }

    private synchronized boolean checkEmpty() {
        if (first == null) {
            threadRunning = false;
            return true;
        }
        return false;
    }

    private synchronized <T extends Throwable> Node<T> add(Node<T> node) {
        if (first != null) {
            node.next = first;
            first.prev = node;
        }
        first = node;
        watchedCount++;

        if (!threadRunning) {
            threadRunning = startThread();
        }
        return node;
    }

    private boolean startThread() {
        Thread thread = threadFactory.newThread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        // Clear setContextClassLoader to avoid leaking the classloader
                        Thread.currentThread().setContextClassLoader(null);
                        Thread.currentThread().setUncaughtExceptionHandler(null);
                        // Node extends PhantomReference, so this cast is safe
                        Node<?> ref = (Node<?>) queue.remove(threadTtl);
                        if (ref == null) {
                            if (checkEmpty()) {
                                break;
                            }
                            continue;
                        }
                        try {
                            ref.onClean(true);
                        } catch (Throwable e) {
                            if (e instanceof InterruptedException) {
                                // This could happen if onClean uses sneaky-throws
                                LOGGER.log(Level.WARNING, "Unexpected interrupt while executing onClean", e);
                                throw e;
                            }
                            // Should not happen if cleaners are well-behaved
                            LOGGER.log(Level.WARNING, "Unexpected exception while executing onClean", e);
                        }
                    } catch (InterruptedException e) {
                        if (LazyCleaner.this.checkEmpty()) {
                            LOGGER.log(
                                    Level.FINE,
                                    "Cleanup queue is empty, and got interrupt, will terminate the cleanup thread"
                            );
                            break;
                        }
                        LOGGER.log(Level.FINE, "Ignoring interrupt since the cleanup queue is non-empty");
                    } catch (Throwable e) {
                        // Ignore exceptions from the cleanup action
                        LOGGER.log(Level.WARNING, "Unexpected exception in cleaner thread main loop", e);
                    }
                }
            }
        });
        if (thread != null) {
            thread.start();
            return true;
        }
        LOGGER.log(Level.WARNING, "Unable to create cleanup thread");
        return false;
    }

    private synchronized boolean remove(Node<?> node) {
        // If already removed, do nothing
        if (node.next == node) {
            return false;
        }

        // Update list
        if (first == node) {
            first = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }

        // Indicate removal by pointing the cleaner to itself
        node.next = node;
        node.prev = node;

        watchedCount--;
        return true;
    }

    private class Node<T extends Throwable> extends PhantomReference<Object> implements Cleanable<T>,
            CleaningAction<T> {
        private final CleaningAction<T> action;
        private Node<?> prev;
        private Node<?> next;

        Node(Object referent, CleaningAction<T> action) {
            super(referent, queue);
            this.action = action;
            //Objects.requireNonNull(referent); // poor man`s reachabilityFence
        }

        @Override
        public void clean() throws T {
            onClean(false);
        }

        @Override
        public void onClean(boolean leak) throws T {
            if (!remove(this)) {
                return;
            }
            if (action != null) {
                action.onClean(leak);
            }
        }
    }
}
