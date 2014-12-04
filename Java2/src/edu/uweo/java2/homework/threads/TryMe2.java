package edu.uweo.java2.homework.threads;

/**
 * Demonstrates that your thread does NOT get an interrupt exception just
 * because it's interrupted; to get an interrupted exception you have to
 * be waiting on something when you are interrupted.
 * 
 * By the way, Thread.sleep( 0 ) in Runner.run() is just to keep the compiler
 * happy; without it, the compiler complains that InterruptedException is
 * never thrown (oh, another clue!)..s
 * 
 * @author jack
 *
 */
public class TryMe2
{
    public static void main(String[] args)
    {
        Thread thr = new Thread( new Runner(), "Runner" );
        thr.start();
        try
        {
            Thread.sleep( 1 );
            thr.interrupt();
            thr.join();
        }
        catch ( InterruptedException exc )
        {
        }
        System.out.println( "exiting" );
    }

    private static class Runner implements Runnable
    {
        int counter = 0;
        
        public void run()
        {
            int counter = 0;
            try
            {
                Thread.sleep( 0 );
                for ( int inx = 0 ; inx < 1000000000 ; ++inx )
                    for ( int jnx = 0 ; jnx < 10000000 ; ++jnx )
                        counter += count();
            }
            catch ( InterruptedException exc )
            {
                System.out.println( "Interrupted" );
            }
            System.out.println( "done" );
        }
        
        private int count()
        {
            return 1;
        }
    }
}

