package y2k.joyreactor.images;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 12/10/15.
 */
class TaskExecutor {

    private ThreadPoolExecutor executor;

    public TaskExecutor(int threadCount) {
        executor = new ThreadPoolExecutor(threadCount, threadCount, 5, TimeUnit.SECONDS, new LifoBlockingDeque());
        executor.allowCoreThreadTimeOut(true);
    }

    void execute(UnsafeRunnable task) {
        executor.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Executor asExecutor() {
        return executor;
    }

    interface UnsafeRunnable {
        void run() throws Exception;
    }

    static class LifoBlockingDeque extends LinkedBlockingDeque<Runnable> {

        private static final long serialVersionUID = -4854985351588039351L;

        LifoBlockingDeque() {
            super(128);
        }

        @Override
        public boolean offer(Runnable e) {
            return super.offerFirst(e);
        }

        @Override
        public boolean offer(Runnable e, long timeout, TimeUnit unit) throws InterruptedException {
            return super.offerFirst(e, timeout, unit);
        }

        @Override
        public boolean add(Runnable e) {
            return super.offerFirst(e);
        }

        @Override
        public void put(Runnable e) throws InterruptedException {
            super.putFirst(e);
        }
    }
}
