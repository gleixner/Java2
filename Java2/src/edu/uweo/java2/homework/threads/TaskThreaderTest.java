package edu.uweo.java2.homework.threads;

public class TaskThreaderTest {

	private static int ThreadCount;
	
	public static void main(String[] args) {
		ThreadCount = 3;
		
		Thread runner = new Thread( new Runnable() {
			public void run() {
				TaskThreader taskThreader = new TaskThreader( ThreadCount );
				 taskThreader.process();
			}
			
		});
		runner.start();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		TaskGenerator.initiateShutdown();
		
		try {
			runner.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println( "Main has finished" );
	}
}
