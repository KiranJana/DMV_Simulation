import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Announcer implements Runnable{

    @Override
    // method to run Announcer.
    public void run() {
        int count = 0;
        System.out.println("Announcer Created");

        while(true)
        {
            // Waits till a customer is in the waiting area.
            wait(DMV.CWaitingArea);
            // Waits till Customer is ready to go into the agent line.
            wait(DMV.aLine);
            System.out.println("Announcer calls number " + (count+1));
            // Signals that this customer has been called.
            signal(DMV.custNum[count]);
            count++;
            // waits for customer to go into the agent Line.
            wait(DMV.cGoAgent);
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
