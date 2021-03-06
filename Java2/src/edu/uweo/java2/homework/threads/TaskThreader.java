package edu.uweo.java2.homework.threads;

/**
 * Put Task object in a class that can be put in threads and run.
 * @author chq-jamesgl
 *
 */

public class TaskThreader {
	
	private static Thread[] Threads;
	private static volatile boolean terminate = false;
	public static volatile long count = 0;

	/**
	 * Creates an array of threads where each one contains a TaskRapper
	 * @param numThreads
	 */
	public TaskThreader( int numThreads ) {
		Threads = new Thread[ numThreads ];
		
		TaskRapper TR = new TaskRapper();
		
		for( int i = 0; i < Threads.length; ++i ) {
			Threads[i] = new Thread(TR );
		}
	}
	
	public void process() {
		for( Thread thr : Threads ) {
			thr.start();
		}
		 TaskGenerator.waitForShutdown();
		 terminate = true;
		 for(Thread thr : Threads ) {
			 thr.interrupt(); 
		 }
//		 System.out.println( "###SHUTTING DOWN###" );
		 
		 for( Thread thr: Threads ) {
			 try {
				thr.join(); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
	}
	
	/**
	 * Asynchronously gets a task and executes it if the thread has not been executed.
	 * @author chq-jamesgl
	 *
	 */
	private static class TaskRapper implements Runnable {
		
		public void run() {
			while( !terminate ) {
//				System.out.println( "***Getting Task***" );
				Task cTask = TaskGenerator.nextTask();
				
				if( !terminate && !Thread.currentThread().isInterrupted() ) {
					try {
//						System.out.println( "EXECUTING " + cTask );
						cTask.execute();
//						System.out.println( "FINISHED " + cTask );
						count += 1;
					} catch (IllegalTaskStateException e) {
						e.printStackTrace();
//						System.exit(0);
					}
				} else {
//					System.out.println( "thread" + cTask.getIdent() + " has been interrupted" );
					break;
				}
			}
		}
	}
}