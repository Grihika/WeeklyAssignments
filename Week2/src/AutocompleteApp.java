import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfQuery = false;
    String query = "";
}

class AutocompleteSystem {

    private TrieNode root = new TrieNode();

    // query -> frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // Insert query
    public void insert(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEndOfQuery = true;
        node.query = query;

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);
    }

    // Getter for frequency (fix for private access)
    public int getFrequency(String query) {
        return frequencyMap.getOrDefault(query, 0);
    }

    // Search suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        dfs(node, results);

        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(a) - frequencyMap.get(b));

        for (String q : results) {

            pq.offer(q);

            if (pq.size() > 10)
                pq.poll();
        }

        List<String> suggestions = new ArrayList<>();

        while (!pq.isEmpty())
            suggestions.add(pq.poll());

        Collections.reverse(suggestions);

        return suggestions;
    }

    // DFS traversal
    private void dfs(TrieNode node, List<String> results) {

        if (node.isEndOfQuery)
            results.add(node.query);

        for (TrieNode child : node.children.values())
            dfs(child, results);
    }

    // Update frequency
    public void updateFrequency(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);
    }
}

public class AutocompleteApp {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        // Insert queries
        system.insert("java tutorial");
        system.insert("javascript");
        system.insert("java download");
        system.insert("java tutorial");
        system.insert("java tutorial");

        System.out.println("Search results for 'jav':");

        List<String> suggestions = system.search("jav");

        for (String s : suggestions) {
            System.out.println(
                    s + " (" + system.getFrequency(s) + " searches)");
        }

        // Update frequency example
        system.updateFrequency("java 21 features");
    }
}