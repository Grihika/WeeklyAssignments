import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class MultiLevelCache {

    private int L1_SIZE = 10000;
    private int L2_SIZE = 100000;

    // L1 Cache (LRU using LinkedHashMap)
    private LinkedHashMap<String, VideoData> L1 =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > L1_SIZE;
                }
            };

    // L2 Cache
    private LinkedHashMap<String, VideoData> L2 =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > L2_SIZE;
                }
            };

    // L3 Database (simulated)
    private HashMap<String, VideoData> database = new HashMap<>();

    // access counters
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // statistics
    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;

    public MultiLevelCache() {

        // simulate database videos
        for (int i = 1; i <= 200000; i++) {
            String id = "video_" + i;
            database.put(id, new VideoData(id, "Video Content " + i));
        }
    }

    public VideoData getVideo(String videoId) {

        long start = System.nanoTime();

        // L1 check
        if (L1.containsKey(videoId)) {

            L1Hits++;

            System.out.println("L1 Cache HIT (0.5ms)");

            updateAccess(videoId);

            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 check
        if (L2.containsKey(videoId)) {

            L2Hits++;

            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);

            promoteToL1(video);

            updateAccess(videoId);

            return video;
        }

        System.out.println("L2 Cache MISS");

        // L3 database
        if (database.containsKey(videoId)) {

            L3Hits++;

            System.out.println("L3 Database HIT (150ms)");

            VideoData video = database.get(videoId);

            L2.put(videoId, video);

            updateAccess(videoId);

            return video;
        }

        return null;
    }

    private void updateAccess(String videoId) {

        accessCount.put(videoId,
                accessCount.getOrDefault(videoId, 0) + 1);

        // promote if frequently accessed
        if (accessCount.get(videoId) > 3 && L2.containsKey(videoId)) {

            promoteToL1(L2.get(videoId));
        }
    }

    private void promoteToL1(VideoData video) {

        L1.put(video.videoId, video);

        System.out.println("Promoted to L1");
    }

    public void invalidateCache(String videoId) {

        L1.remove(videoId);
        L2.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double L1Rate = total == 0 ? 0 : (L1Hits * 100.0) / total;
        double L2Rate = total == 0 ? 0 : (L2Hits * 100.0) / total;
        double L3Rate = total == 0 ? 0 : (L3Hits * 100.0) / total;

        System.out.println("\nCache Statistics:");

        System.out.println("L1: Hit Rate " + String.format("%.2f", L1Rate) + "%");
        System.out.println("L2: Hit Rate " + String.format("%.2f", L2Rate) + "%");
        System.out.println("L3: Hit Rate " + String.format("%.2f", L3Rate) + "%");

        double overall = ((L1Hits + L2Hits) * 100.0) / total;

        System.out.println("Overall Hit Rate: " + String.format("%.2f", overall) + "%");
    }
}

public class MultiLevelCacheApp {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        System.out.println("\nFirst request:");
        cache.getVideo("video_123");

        System.out.println("\nSecond request:");
        cache.getVideo("video_123");

        System.out.println("\nThird request:");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}
