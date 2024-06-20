package l.f.mappool.util;

import java.util.Map;
import java.util.concurrent.*;

public class TokenBucketUtil {
    private static final long OVER_TIME = 1000 * 60 * 60;
    private static final Map<String, Token> tokens = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ScheduledFuture<?> f;

    static {
        f = scheduler.scheduleAtFixedRate(TokenBucketUtil::refillTokens, 0, 1, TimeUnit.SECONDS);
    }
    static class Token{
        double capacity;
        double refillRate;
        long lastTime;
        double tokens;
    }

    static void refillTokens() {
        long d = System.currentTimeMillis();
        tokens.entrySet().removeIf(e -> d - e.getValue().lastTime > OVER_TIME);
        tokens.forEach((key, v) -> v.tokens = Math.min(v.capacity, v.tokens + v.refillRate));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean getToken(String ip, int max, double refillRate) {
        var token = tokens.computeIfAbsent(ip, (i) -> {
            var t = new Token();
            t.tokens = max / 2f;
            t.capacity = max;
            t.refillRate = refillRate;
            t.lastTime = System.currentTimeMillis();
            return t;
        });
        double availableTokens = token.tokens;

        if (availableTokens >= 1) {
            token.tokens -= 1;
            token.lastTime = System.currentTimeMillis();
            tokens.put(ip, token); // 消耗一个令牌
            return true;
        }
        return false;
    }

    public static void closeTask() {
        f.cancel(true);
    }
}