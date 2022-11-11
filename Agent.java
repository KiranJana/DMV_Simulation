/*
    Agent
    1)	2 threads created at the beginning.
    2)	Asks customer to take eye exam and photo.
    3)	Provides customer with temporary license
 */

import java.util.concurrent.Semaphore;

public class Agent implements Runnable {
    private int agentId;
    //int custNUM;


    Agent(int id) {
        this.agentId = id;
    }

    @Override
    public void run() {

        System.out.println("Agent " + agentId + " Created");

        while (true) {

            // Wait till a customer is in the agentLine.
            wait(DMV.cInAgent);
            wait(DMV.customer_Mutex_Agent);

            // Then we poll the first customer in the cust to agent queue.
            int custNUM = DMV.custToAgentQueue.poll();

            System.out.println("Agent " + agentId + " is serving customer " + custNUM);

            // Mutex to allow only one agent serves one customer.
            wait(DMV.agent_Mutex_Customer);
            DMV.agentQueue.add(agentId);
            // Agent is ready to serve customer.
            signal(DMV.readyAgent);
            signal(DMV.agent_Mutex_Customer);

            // Customer is being served by the agent.
            signal(DMV.custReady[custNUM]);
            signal(DMV.customer_Mutex_Agent);

            // Wait till customer is at the test. Agent then takes the test.
            wait(DMV.cAtTest);
            System.out.println("Agent " + agentId + " asks customer " + custNUM + " to take a photo and eye exam");
            signal(DMV.cTakingTest);
            signal(DMV.cFinishedTest);
            // Once customer is done taking the test agent gives them their license.
            System.out.println("Agent " + agentId + " gives license to customer " + custNUM);
            signal(DMV.aFinished);
            // Agent is finished, we wait until all customer are done.
            wait(DMV.exit); // gets signal when all customers are done.

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