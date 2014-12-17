package edu.uweo.java2.homework.threads;

public class TaskThreaderTest {

	
	public static void main(String[] args) {
		int ThreadCount = 5000;
		
		TaskThreader taskThreader = new TaskThreader( ThreadCount );
		Thread runner = new Thread( taskThreader::process );
		runner.start();
		
		try {
			Thread.sleep(10000);
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
		System.out.println( TaskThreader.count );
	}
}
