package org.nla.followmytracks.core;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.nla.followmytracks.FollowMyTracksApplication;
import org.nla.followmytracks.FollowMyTracksComponent;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JobManager {

    private static final String CACHE_MANAGER_THREAD_PREFIX = "JobManager #";
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_TIME_SECOND = 60L;
    private static final int INITIAL_CAPACITY_QUEUE = 50;

    private final FollowMyTracksApplication followMyTracksApplication;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    public JobManager(Application followMyTracksApplication) {
        this.followMyTracksApplication = (FollowMyTracksApplication) followMyTracksApplication;
        this.threadPoolExecutor = new PriorityExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME_SECOND,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<>(INITIAL_CAPACITY_QUEUE, new JobEntryComparator()),
                new JobThreadFactory());
    }

    public <T> Future<T> submitCallable(final Callable<T> callable) {
        if (callable instanceof PriorityRunnable) {
            ((PriorityRunnable) callable).inject(followMyTracksApplication.getComponent());
        }
        return threadPoolExecutor.submit(callable);
    }

    public Future submitRunnable(final PriorityRunnable runnable) {
        runnable.inject(followMyTracksApplication.getComponent());
        return threadPoolExecutor.submit(runnable);
    }

    public void execute(final PriorityRunnable runnable) {
        runnable.inject(followMyTracksApplication.getComponent());
        threadPoolExecutor.execute(runnable);
    }

    public void execute(final ChainedPriorityRunnable runnable) {
        runnable.setJobManager(this);
        execute((PriorityRunnable) runnable);
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /**
     * The {@link JobManager} sort those runnable in a priority queue as they are coming.
     */
    public interface Prioritable {
        Priority getPriority();
    }

    /**
     * Runnable that is part of a {@link ChainedPriorityRunnable}.
     */
    public interface QueuedRunnable<T> {
        QueuedRunnable<T> configureRunnable(T object);
    }

    private static class PriorityFutureTask<T> extends FutureTask<T>
            implements Prioritable {

        private volatile Priority priority;

        PriorityFutureTask(Priority priority, Runnable runnable, T result) {
            super(runnable, result);
            this.priority = priority;
        }

        PriorityFutureTask(Priority priority, Callable<T> callable) {
            super(callable);
            this.priority = priority;
        }

        @Override
        public Priority getPriority() {
            return priority;
        }
    }

    public abstract static class PriorityRunnable implements Runnable, Prioritable {

        private final Priority priority;

        protected PriorityRunnable(final Priority priority) {
            this.priority = priority;
        }

        @Override
        public Priority getPriority() {
            return priority;
        }

        public void inject(final FollowMyTracksComponent component) {}
    }

    @SuppressWarnings("unused")
    public static class PriorityException extends Exception {
        public PriorityException() {
        }

        public PriorityException(String detailMessage) {
            super(detailMessage);
        }

        public PriorityException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public PriorityException(Throwable throwable) {
            super(throwable);
        }
    }

    /**
     * {@link PriorityRunnable} that can ask another runnable to run (through the {@link JobManager}).
     *
     * @param <T>
     */
    public abstract static class ChainedPriorityRunnable<T extends PriorityRunnable & QueuedRunnable>
            extends PriorityRunnable {
        private static final String TAG = ChainedPriorityRunnable.class.getSimpleName();

        protected final T nextRunnable;
        private JobManager jobManager;

        protected ChainedPriorityRunnable(final Priority priority, T nextRunnable) {
            super(priority);
            this.nextRunnable = nextRunnable;
        }

        public void setJobManager(JobManager jobManager) {
            this.jobManager = jobManager;
        }

        /**
         * Execute next runnable.
         */
        public void chainCall() {
            if (jobManager != null) {
                jobManager.execute(nextRunnable);
            } else {
                Log.w(TAG, "Attempt to chain runnable, but jobManager has not been set");
            }
        }
    }

    @VisibleForTesting
    static class JobEntryComparator
            implements java.util.Comparator<Runnable>, Serializable {
        @Override
        public int compare(final Runnable lhs, final Runnable rhs) {
            if (lhs instanceof Prioritable && rhs instanceof Prioritable) {
                return ((Prioritable) rhs).getPriority()
                                          .compareTo(((Prioritable) lhs).getPriority());
            } else {
                if (lhs == null) {
                    return -1;
                } else if (rhs == null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    private static class JobThreadFactory implements ThreadFactory {

        private final AtomicInteger count = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable,
                              CACHE_MANAGER_THREAD_PREFIX + count.getAndIncrement());
        }
    }

    private static class PriorityExecutor extends ThreadPoolExecutor {

        public PriorityExecutor(
                int corePoolSize,
                int maximumPoolSize,
                long keepAliveTimeSecond,
                TimeUnit seconds,
                PriorityBlockingQueue<Runnable> runnables,
                JobThreadFactory jobThreadFactory
        ) {
            super(corePoolSize,
                  maximumPoolSize,
                  keepAliveTimeSecond,
                  seconds,
                  runnables,
                  jobThreadFactory);
        }

        private static Priority getPriority(Object object) {
            final Priority priority;
            if (object instanceof Prioritable) {
                priority = ((Prioritable) object).getPriority();
            } else {
                priority = Priority.LOW;
            }
            return priority;
        }

        @NonNull
        @Override
        public <T> Future<T> submit(Callable<T> task) {
            if (task == null) {
                throw new NullPointerException();
            }
            final RunnableFuture<T> ftask = new PriorityFutureTask<>(getPriority(task), task);
            super.execute(ftask);
            return ftask;
        }

        @NonNull
        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            if (task == null) {
                throw new NullPointerException();
            }
            final RunnableFuture<T> ftask = new PriorityFutureTask<>(getPriority(task),
                                                                     task,
                                                                     result);
            super.execute(ftask);
            return ftask;
        }

        @NonNull
        @Override
        public Future<?> submit(Runnable task) {
            if (task == null) {
                throw new NullPointerException();
            }
            final RunnableFuture<Void> ftask = new PriorityFutureTask<>(getPriority(task),
                                                                        task,
                                                                        null);
            super.execute(ftask);
            return ftask;
        }

    }
}
