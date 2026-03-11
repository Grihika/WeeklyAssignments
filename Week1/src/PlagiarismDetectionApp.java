import java.util.*;

class PlagiarismDetector {

    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    private int N = 5;

    public void addDocument(String docId, String text) {

        List<String> words = preprocess(text);
        List<String> ngrams = generateNGrams(words);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    public void analyzeDocument(String docId, String text) {

        List<String> words = preprocess(text);
        List<String> ngrams = generateNGrams(words);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String existingDoc : ngramIndex.get(gram)) {
                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            double similarity =
                    (entry.getValue() * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + entry.getValue() +
                            " matching n-grams with \"" +
                            entry.getKey() + "\"");

            System.out.println(
                    "Similarity: " +
                            String.format("%.2f", similarity) + "%");

            if (similarity > 60) {
                System.out.println("PLAGIARISM DETECTED");
            }
        }
    }

    private List<String> generateNGrams(List<String> words) {

        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.size() - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words.get(i + j)).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    private List<String> preprocess(String text) {

        text = text.toLowerCase()
                .replaceAll("[^a-z ]", "");

        String[] words = text.split("\\s+");

        return Arrays.asList(words);
    }
}

public class PlagiarismDetectionApp {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        detector.addDocument("essay_089.txt",
                "machine learning is a field of artificial intelligence that focuses on data");

        detector.addDocument("essay_092.txt",
                "machine learning is a field of artificial intelligence that focuses on data analysis and prediction models");

        detector.analyzeDocument("essay_123.txt",
                "machine learning is a field of artificial intelligence that focuses on data analysis and prediction");
    }
}
