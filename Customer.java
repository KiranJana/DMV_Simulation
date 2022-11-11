import java.util.concurrent.Semaphore;

public class Customer implements Runnable{

     int custId;
     int cNum;
     int agentId;


    Customer(int id)
    {
        this.custId = id;
    }
    public void setCustomerId(int id)
    {
        this.cNum = id;
    }

    @Override
    public void run()
    {
        // Waits for the mutex to release.
        wait(DMV.CMutex);
        System.out.println("Customer " + custId + " created, enters DMV");
        DMV.customerQueue.add(this); // Adds the customer to the queue using ID.
        signal(DMV.infoDeskWait); // Tells the info desk customer is ready.
        signal(DMV.CMutex); // Releases the mutex.

        // Waits for info desk to signal that the customer got their number and moves to waiting area.
        wait(DMV.go_to_waiting_area);
        System.out.println("Customer " + custId + " gets number " + (cNum + 1) + ", enters waiting room");

        // Customer signals that they are in the waiting area to announcer.
        signal(DMV.CWaitingArea);
        // Waits for annoucer to call their number.
        wait(DMV.custNum[cNum]);
        System.out.println("Customer " + custId + " moves to agent line");
        signal(DMV.cGoAgent); // signal that customer is ready to go to agent line.


        // Mutex to only allow one customer to enter agent line.
        wait(DMV.customer_Mutex_Agent);
        // Add the customer to waiting in the agent line.
        DMV.custToAgentQueue.add(custId);
        // Customer signals agent that they are in agent Line.
        signal(DMV.cInAgent);
        // Release the mutex.
        signal(DMV.customer_Mutex_Agent);

        // Customer waits till agent is ready.
        wait(DMV.readyAgent);
        // Only one agent can serve one customer at a time.
        wait(DMV.agent_Mutex_Customer);
        // polling from the agent queue.
        agentId = DMV.agentQueue.poll();
        signal(DMV.agent_Mutex_Customer);


        // Customer moves to agent and signals that customer is ready to take the test.
        wait(DMV.custReady[custId]);
        System.out.println("Customer " + custId + " is being served by Agent " + agentId );
        signal(DMV.aLine);
        signal(DMV.cAtTest);

        // Wait till agent has taken the customers tests.
        wait(DMV.cTakingTest);
        System.out.println("Customer " + custId + " completes photo and eye exam with Agent" + agentId );
        signal(DMV.cFinishedTest);
        wait(DMV.aFinished);

        // Once customer has completed their test they get their license and depart.
        System.out.println("Customer " + custId + " gets license and departs");
        signal(DMV.exit);
        System.out.println("Customer " + custId + " was joined" );
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
