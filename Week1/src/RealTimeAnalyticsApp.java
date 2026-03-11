import java.util.*;

class AnalyticsDashboard {

    private HashMap<String, Integer> pageViews = new HashMap<>();

    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    private HashMap<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(String url, String userId, String source) {

        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();

        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            topPages.add(pq.poll());
            count++;
        }

        return topPages;
    }

    public void getDashboard() {

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String url = entry.getKey();
            int visits = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url +
                    " - " + visits +
                    " views (" + unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }
}

public class RealTimeAnalyticsApp {

    public static void main(String[] args) throws InterruptedException {

        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/article/breaking-news", "user_456", "facebook");
        dashboard.processEvent("/sports/championship", "user_777", "direct");
        dashboard.processEvent("/sports/championship", "user_888", "google");
        dashboard.processEvent("/sports/championship", "user_777", "google");

        for (int i = 0; i < 2; i++) {

            Thread.sleep(5000);

            dashboard.getDashboard();
        }
    }
}