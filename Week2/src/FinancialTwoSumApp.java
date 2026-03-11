import java.util.*;

class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long timestamp;

    public Transaction(int id, int amount, String merchant, String account, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.timestamp = timestamp;
    }

    public String toString() {
        return "(id:" + id + ", amount:" + amount + ")";
    }
}

class TransactionAnalyzer {

    List<Transaction> transactions;

    public TransactionAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Classic Two-Sum
    public List<String> findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction t2 = map.get(complement);

                result.add("(" + t2.id + "," + t.id + ")");
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum within time window (1 hour)
    public List<String> findTwoSumWithinHour(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                if (Math.abs(t.timestamp - prev.timestamp) <= 3600000) {

                    result.add("(" + prev.id + "," + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // K-Sum (recursive)
    public void findKSum(int k, int target) {

        kSumHelper(new ArrayList<>(), 0, k, target);
    }

    private void kSumHelper(List<Transaction> current, int index, int k, int target) {

        if (current.size() == k) {

            int sum = 0;

            for (Transaction t : current)
                sum += t.amount;

            if (sum == target)
                System.out.println(current);

            return;
        }

        for (int i = index; i < transactions.size(); i++) {

            current.add(transactions.get(i));

            kSumHelper(current, i + 1, k, target);

            current.remove(current.size() - 1);
        }
    }

    // Duplicate detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());

            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.println("Duplicate transactions:");

                for (Transaction t : list)
                    System.out.println(t);
            }
        }
    }
}

public class FinancialTwoSumApp {

    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1, 500, "Store A", "acc1", System.currentTimeMillis()));
        transactions.add(new Transaction(2, 300, "Store B", "acc2", System.currentTimeMillis()));
        transactions.add(new Transaction(3, 200, "Store C", "acc3", System.currentTimeMillis()));

        TransactionAnalyzer analyzer = new TransactionAnalyzer(transactions);

        System.out.println("Two-Sum pairs:");
        System.out.println(analyzer.findTwoSum(500));

        System.out.println("\nTwo-Sum within 1 hour:");
        System.out.println(analyzer.findTwoSumWithinHour(500));

        System.out.println("\nK-Sum (k=3, target=1000):");
        analyzer.findKSum(3, 1000);

        System.out.println("\nDuplicate detection:");
        analyzer.detectDuplicates();
    }
}
