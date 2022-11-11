import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class DMV {

    /*1)	20 customers (1 thread each) all created at the start of the simulation.
           2)	Waits in line at Information Desk to get a number.
           3)	Waits in waiting area until number is called.
           4)	Waits in line for agent.
           5)	Works with agent to complete driver’s license application.
           6)	Exits
*/
    // 20 Customers and Threads
     Thread[] custThread = new Thread[20];

    // 2 Agents
    Agent agent0 = new Agent(0);
    Agent agent1 = new Agent(1);

    Thread a0Thread = new Thread(agent0);
    Thread a1Thread = new Thread(agent1);

    // Announcer
    Thread announcerThread = new Thread(new Announcer());

    // Info Desk being created.
    Thread infoThread = new Thread(new InformationDesk());

    // Customer Semaphores
    public static Semaphore CMutex = new Semaphore(1,true);
    public static Semaphore customer_Mutex_Agent = new Semaphore(1,true);
    public static Semaphore agent_Mutex_Customer = new Semaphore(1,true);

    public static Semaphore cAtTest = new Semaphore(0, true);
    public static Semaphore cFinishedTest = new Semaphore(0, true);
    public static Semaphore exit = new Semaphore(0, true);



    // Info desk Mutex
    public static Semaphore infoDeskWait = new Semaphore(0, true); //checks if infoDesk is available.
    public static Semaphore go_to_waiting_area = new Semaphore(0, true); //checks if infoDesk is available

    // Announcer Semaphore
    public static Semaphore CWaitingArea = new Semaphore(0, true);
    public static Semaphore cInAgent = new Semaphore(0, true);
    public static Semaphore cGoAgent = new Semaphore(0, true);

    // Agent Semaphores
    public static Semaphore readyAgent = new Semaphore(0, true);
    public static Semaphore aLine = new Semaphore(4, true);
    public static Semaphore aFinished = new Semaphore(0, true);
    public static Semaphore cTakingTest = new Semaphore(0, true);

    // Creating my Queues.
    public static Queue<Customer> customerQueue = new LinkedList<Customer>();
    public static Queue<Integer> agentQueue = new LinkedList<Integer>();
    public static Queue<Integer> custToAgentQueue = new LinkedList<Integer>();

    //Semaphore Arrays
    public static Semaphore[] custNum = new Semaphore[20];
    public static Semaphore[] custReady = new Semaphore[20];



    // Method that creates my threads and then runs them.
    public void runDMV() throws InterruptedException
    {

        for (int i = 0; i < 20; i++) {
            custNum[i] = new Semaphore(0);
            custReady[i] = new Semaphore(0);
        }

        // Starting my threads.
        infoThread.setDaemon(true);
        infoThread.start();

        announcerThread.setDaemon(true);
        announcerThread.start();

        a0Thread.setDaemon(true);
        a0Thread.start();

        a1Thread.setDaemon(true);
        a1Thread.start();

        // Creating my customer threads.
        for (int i = 0; i < 20; i++) {
            custThread[i] = new Thread(new Customer(i));
            custThread[i].start();
        }

        // Joining the threads in the end and check for exceptions.
        for (int i = 0; i < 20; i++) {
            try{
                custThread[i].join();
            }
            catch (InterruptedException e) {}
        }
        System.out.println("Done");

    }

}
