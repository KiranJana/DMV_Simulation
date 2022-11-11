import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class InformationDesk implements Runnable{

    @Override
    public void run() {
        int count = 0;
        // Creating the Info Desk at the start.
        System.out.println("Information Desk Created");

        while(true)
        {
            // Waits for signal from customer and then assigns a number to the customer.
            wait(DMV.infoDeskWait);
            // Only one customer at a time can get a number.
            wait(DMV.CMutex);
            Customer nextCustomer = DMV.customerQueue.poll();
            nextCustomer.setCustomerId(count);
            count++;
            signal(DMV.CMutex);
            // Releases the mutex

            // signals that the customer can go to the waiting room.
            signal(DMV.go_to_waiting_area);
        }
    }

    // These are functions I created to simplify my code.
    // acquires my semaphore and also employs a try catch exception.
    public static void wait(Semaphore sem) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // releases my semaphore.
    public static void signal(Semaphore sem) {
        sem.release();

    }
}
