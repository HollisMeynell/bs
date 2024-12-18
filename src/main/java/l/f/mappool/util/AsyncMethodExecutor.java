package l.f.mappool.util;

import l.f.mappool.exception.HttpTipException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Slf4j
@SuppressWarnings("unused")
public class AsyncMethodExecutor {
    public interface Supplier<T>{
        T get() throws Exception;
    }
    public interface Runnable{
        void run() throws Exception;
    }

    @SuppressWarnings("unchecked")
    private static <T> T waitForResult(Condition lock, Object key, T defaultValue) throws Exception {
        CountDownLatch countDownLock = null;
        try {
            reentrantLock.lock();
            Util.add(lock);
            lock.await();
            reentrantLock.unlock();
            countDownLock = countDownLocks.get(key);
            Object result = results.get(key);
            if (result instanceof Exception e) {
                throw e;
            }
            return (T) result;
        } catch (InterruptedException ignore) {
            return defaultValue;
        } finally {
            if (countDownLock != null) countDownLock.countDown();
        }
    }
    private static final ReentrantLock reentrantLock = new ReentrantLock();
    private static final ConcurrentHashMap<Object, CountDownLatch> countDownLocks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Object, Condition> locks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Object, Object> results = new ConcurrentHashMap<>();

    @SuppressWarnings({"unused", "DuplicatedCode"})
    public static<T> T execute(Supplier<T> supplier, Object key, T defaultValue) throws Exception {
        boolean hasLock;
        Condition lock;
        reentrantLock.lock();
        hasLock = locks.containsKey(key);
        lock = locks.computeIfAbsent(key, s -> reentrantLock.newCondition());
        reentrantLock.unlock();
        if (hasLock) {
            return waitForResult(lock, key, defaultValue);
        } else {
            return getResult(lock,key,supplier);
        }
    }
    @SuppressWarnings("DuplicatedCode")
    public static<T> T execute(Supplier<T> supplier, Object key, Supplier<T> getDefault) throws Exception {
        boolean hasLock;
        Condition lock;
        reentrantLock.lock();
        hasLock = locks.containsKey(key);
        lock = locks.computeIfAbsent(key, s -> reentrantLock.newCondition());
        reentrantLock.unlock();
        if (hasLock) {
            return waitForResult(lock, key, getDefault);
        } else {
            return getResult(lock,key,supplier);
        }
    }

    @SuppressWarnings("unchecked")
    private static<T> T waitForResult(Condition lock, Object key, Supplier<T> getDefault) throws Exception {
        CountDownLatch countDownLock = null;
        try {
            reentrantLock.lock();
            Util.add(lock);
            lock.await();
            reentrantLock.unlock();
            countDownLock = countDownLocks.get(key);
            Object result = results.get(key);
            if (result instanceof Exception e) {
                throw e;
            }
            return (T) result;
        } catch (InterruptedException ignore) {
            return getDefault.get();
        } finally {
            if (countDownLock != null) countDownLock.countDown();
        }
    }

    private static<T> T getResult(Condition lock, Object key, Supplier<T> supplier) throws Exception {

        try {
            T result = supplier.get();
            results.put(key, result);
            return result;
        } catch (Exception e) {
            results.put(key, e);
            throw e;
        } finally {
            reentrantLock.lock();
            int locksSum = Util.getAndRemove(lock);
            log.debug("sum: {}", locksSum);
            CountDownLatch count = countDownLocks.computeIfAbsent(key, k -> new CountDownLatch(locksSum));
            lock.signalAll();
            reentrantLock.unlock();
            if (!count.await(5, TimeUnit.SECONDS)) {
                if (locksSum > 0) log.warn("wait to long");
            }
            results.remove(key);
            locks.remove(key);
            countDownLocks.remove(key);
        }

    }

    private static class Util {
        static final ConcurrentHashMap<Condition, Integer> conditionCount = new ConcurrentHashMap<>();

        static void add(Condition lock) {
            conditionCount.putIfAbsent(lock, 0);
            conditionCount.computeIfPresent(lock, (k, v) -> v + 1);
        }

        static int getAndRemove(Condition lock) {
            Integer count = conditionCount.remove(lock);
            return Objects.nonNull(count) ? count : 0;
        }
    }

    @SuppressWarnings("unused")
    public static void execute(Runnable work, Object key) throws Exception {
        boolean hasLock;
        Condition lock;
        reentrantLock.lock();
        hasLock = locks.containsKey(key);
        lock = locks.computeIfAbsent(key, s -> reentrantLock.newCondition());
        reentrantLock.unlock();

        if (hasLock) {
            try {
                reentrantLock.lock();
                lock.await();
                reentrantLock.unlock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            work.run();
        } finally {
            reentrantLock.lock();
            lock.signalAll();
            reentrantLock.unlock();
            locks.remove(key);
        }
    }

    /**
     * 同时进行多个任务
     */
    public static void asyncRunnable(Collection<Runnable> works) {
        asyncRunnable(works, e -> log.error("Async error", e));
    }

    public static void asyncRunnable(Collection<Runnable> works, java.util.function.Consumer<Exception> errHandler) {
        works.stream()
                .<java.lang.Runnable>map(w -> (() -> {
                    try {
                        w.run();
                    } catch (Exception e) {
                        errHandler.accept(e);
                    }
                }))
                .forEach(Thread::startVirtualThread);
    }

    /**
     * 同时进行多个带返回值的任务
     * 结果不固定
     */
    public static <T> List<T> asyncSupplier(Collection<Supplier<T>> works, Function<Exception, T> errHandler) {
        int size = works.size();
        var lock = new CountDownLatch(size);
        final List<T> results = new LinkedList<>();
        works.stream()
                .<java.lang.Runnable>map(w -> () -> {
                    try {
                        T result = w.get();
                        results.add(result);
                    } catch (Exception e) {
                        results.add(errHandler.apply(e));
                    } finally {
                        lock.countDown();
                    }
                })
                .forEach(Thread::startVirtualThread);
        try {
            //noinspection ResultOfMethodCallIgnored
            lock.await(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("lock error", e);
        }
        return results;
    }

    public static void sleep(int time) throws HttpTipException {
        if (time < 0) time = 10;
        sleep((long)time);
    }

    public static void sleep(long time) throws HttpTipException {
        if (time < 0) time = 10L;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new HttpTipException(500, "服务处理遭到中断");
        }
    }
/*
    public static <T> Optional<T> anyWork(Collection<Callable<T>> works){
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<T>()) {
            works.forEach(scope::fork);
            var result = scope.join().result();
            return Optional.ofNullable(result);
        } catch (Exception e) {
            log.error("work err: ", e);
        }
        return Optional.empty();
    }
*/
}
