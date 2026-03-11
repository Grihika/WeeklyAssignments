class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        this.licensePlate = null;
        this.entryTime = 0;
        this.occupied = false;
    }
}

class ParkingLot {

    private int capacity = 500;
    private ParkingSpot[] table;

    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int totalParks = 0;

    public ParkingLot() {

        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++)
            table[i] = new ParkingSpot();
    }

    // Hash function
    private int hash(String licensePlate) {

        int hash = 0;

        for (char c : licensePlate.toCharArray())
            hash += c;

        return Math.abs(hash) % capacity;
    }

    // Park vehicle using linear probing
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        occupiedCount++;
        totalProbes += probes;
        totalParks++;

        System.out.println("parkVehicle(\"" + licensePlate + "\") → Assigned spot #" +
                index + " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (table[index].occupied) {

            if (table[index].licensePlate.equals(licensePlate)) {

                long exitTime = System.currentTimeMillis();
                long durationMillis = exitTime - table[index].entryTime;

                double hours = durationMillis / 3600000.0;
                double fee = hours * 5; // $5 per hour

                table[index].occupied = false;
                table[index].licensePlate = null;

                occupiedCount--;

                System.out.printf(
                        "exitVehicle(\"%s\") → Spot #%d freed, Duration: %.2f hours, Fee: $%.2f\n",
                        licensePlate, index, hours, fee);

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found.");
    }

    // Find nearest spot
    public void findNearestSpot() {

        for (int i = 0; i < capacity; i++) {

            if (!table[i].occupied) {
                System.out.println("Nearest available spot: #" + i);
                return;
            }
        }

        System.out.println("Parking lot full.");
    }

    // Statistics
    public void getStatistics() {

        double occupancy = (occupiedCount * 100.0) / capacity;

        double avgProbes = totalParks == 0 ? 0 : (double) totalProbes / totalParks;

        System.out.println("\nParking Statistics:");
        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
    }
}

public class ParkingLotApp {

    public static void main(String[] args) throws InterruptedException {

        ParkingLot lot = new ParkingLot();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.findNearestSpot();

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}
