import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private final int MAX_CACHE_SIZE = 5;

    // LRU cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };

    private int hits = 0;
    private int misses = 0;

    // Resolve domain
    public synchronized String resolve(String domain) {

        long startTime = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null) {

            if (!entry.isExpired()) {
                hits++;
                long time = System.nanoTime() - startTime;
                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (" + time / 1_000_000.0 + " ms)");
                return entry.ipAddress;
            }

            // expired
            cache.remove(domain);
            System.out.println("Cache EXPIRED → querying upstream...");
        }

        misses++;

        // simulate upstream DNS lookup
        String newIP = queryUpstreamDNS(domain);

        // store with TTL
        cache.put(domain, new DNSEntry(domain, newIP, 10));

        long time = System.nanoTime() - startTime;

        System.out.println("Cache MISS → " + newIP +
                " (" + time / 1_000_000.0 + " ms)");

        return newIP;
    }

    // simulate upstream DNS
    private String queryUpstreamDNS(String domain) {
        try {
            Thread.sleep(100); // simulate network delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random r = new Random();
        return "172.217." + r.nextInt(255) + "." + r.nextInt(255);
    }

    // cache statistics
    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;

        System.out.println("\nCache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }
}

public class DNSCacheApp {

    public static void main(String[] args) throws InterruptedException {

        DNSCache cache = new DNSCache();

        cache.resolve("google.com");
        cache.resolve("google.com");

        Thread.sleep(11000); // wait for TTL expiry

        cache.resolve("google.com");

        cache.getCacheStats();
    }
}