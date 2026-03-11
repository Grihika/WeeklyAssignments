import java.util.*;

class TokenBucket {

    int tokens;
    int maxTokens;
    double refillRate; // tokens per second
    long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // refill tokens based on elapsed time
    private void refill() {

        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        int tokensToAdd = (int)(seconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    // attempt request
    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        refill();
        return tokens;
    }
}

class RateLimiter {

    // clientId → token bucket
    private HashMap<String, TokenBucket> clients = new HashMap<>();

    private int LIMIT = 1000;
    private double REFILL_RATE = 1000.0 / 3600.0; // tokens per second

    public synchronized String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(LIMIT, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" +
                    bucket.getRemainingTokens() +
                    " requests remaining)";
        }

        return "Denied (0 requests remaining, retry later)";
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int used = LIMIT - bucket.getRemainingTokens();

        System.out.println(
                "{used: " + used +
                        ", limit: " + LIMIT +
                        ", remaining: " +
                        bucket.getRemainingTokens() + "}"
        );
    }
}

public class DistributedRateLimiterApp {

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {
            System.out.println(
                    limiter.checkRateLimit(client));
        }

        limiter.getRateLimitStatus(client);
    }
}