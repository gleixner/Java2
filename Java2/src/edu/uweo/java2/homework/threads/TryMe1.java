package edu.uweo.java2.homework.threads;

import edu.uweo.java2.homework.threads.TaskGenerator;

/**
 * This program demonstrates a common problem. It starts a thread that
 * wants to wait for five seconds, but then it let's main() exit. This 
 * will cause the program to terminate while the Waiter thread is still
 * waiting.
 * 
 * @author jack
 *
 */
public class TryMe1
{

    public static void main(String[] args)
    {
        maker();
        TaskGenerator.initiateShutdown();
        System.out.println( "done" );
    }
    
    private static void maker()
    {
        new Thread( new Waiter(), "Waiter"  ).start();
    }
    
    private static class Waiter implements Runnable
    {
        public void run()
        {
            try
            {
                Thread.sleep( 2000 );
            }
            catch ( InterruptedException exc )
            {
            }
            System.out.println( "thread has finished" );
        }
    }
}

