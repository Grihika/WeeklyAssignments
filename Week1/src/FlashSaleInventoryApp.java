import java.util.*;

class FlashSaleInventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> stock = new HashMap<>();

    // productId -> waiting list (FIFO)
    private HashMap<String, Queue<Integer>> waitingList = new HashMap<>();

    // Add product with stock
    public void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    // Purchase item (synchronized for thread safety)
    public synchronized String purchaseItem(String productId, int userId) {

        int currentStock = stock.getOrDefault(productId, 0);

        if (currentStock > 0) {
            stock.put(productId, currentStock - 1);
            return "Success, " + (currentStock - 1) + " units remaining";
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Added to waiting list, position #" + queue.size();
        }
    }

    // Show waiting list
    public void showWaitingList(String productId) {
        System.out.println("Waiting List: " + waitingList.get(productId));
    }
}

public class FlashSaleInventoryApp {

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println("Stock Available: " + manager.checkStock("IPHONE15_256GB"));

        // Simulated purchases
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // Simulate stock finishing quickly
        for (int i = 0; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));

        manager.showWaitingList("IPHONE15_256GB");
    }
}
