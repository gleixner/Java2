package edu.uweo.java2.homework.threads;

/**
 * Put Task object in a class that can be put in threads and run.
 * @author chq-jamesgl
 *
 */

public class TaskThreader {
	
	private static Thread[] Threads;
	private Thread dispatch;
	private static volatile boolean terminate = false;

	/**
	 * Creates and array of threads where each one contains a TaskRapper
	 * @param numThreads
	 */
	public TaskThreader( int numThreads ) {
		Threads = new Thread[ numThreads ];
		
		for( int i = 0; i < Threads.length; ++i ) {
			Threads[i] = new Thread(new TaskRapper() );
		}
	}
	
	public void Process() {
		dispatch = new Thread( new TaskDispatcher() );
		dispatch.start();
	}
	
	public Thread[] getThreads() {
		return Threads;
	}
	
	/**
	 * Asynchronously gets a task and executes it if the thread has not been executed.
	 * @author chq-jamesgl
	 *
	 */
	private static class TaskRapper implements Runnable {
		
		private Task cTask;

		public void run() {
			while( !terminate ) {
				System.out.println( "***Getting Task***" );
				cTask = TaskGenerator.nextTask();

				if( terminate ) {
					System.out.println( "thread" + cTask.getIdent() + " has been interrupted" );
					break;
				}

				try {
					System.out.println( "EXECUTING " + cTask );
					cTask.execute();
					System.out.println( "FINISHED " + cTask );
				} catch (IllegalTaskStateException e) {
					e.printStackTrace();
				}
			}
			System.out.println( "***Getting Task***" );
			cTask = TaskGenerator.nextTask();

			try {
				System.out.println( "EXECUTING " + cTask );
				cTask.execute();
				System.out.println( "FINISHED " + cTask );
			} catch (IllegalTaskStateException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Dispatch TaskRappers from this thread, that way this thread can block
	 * @author chq-jamesgl
	 *
	 */
	private static class TaskDispatcher implements Runnable {
		
		public void run() {
			for( Thread thr : Threads ) {
				thr.start();
			}
			 TaskGenerator.waitForShutdown();
			 terminate = true;
			 System.out.println( "###SHUTTING DOWN###" );
		}
		
	}
}
