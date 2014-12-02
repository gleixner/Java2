package edu.uweo.java2.homework.threads;

public class TaskThreaderTest {

	public static void main(String[] args) {
		int ThreadCount = 10;
		TaskThreader threader = new TaskThreader( ThreadCount );
		threader.Process();
		Thread[] threadList = threader.getThreads();
		Thread interrupter = threadList[ ThreadCount - 2 ];
		interrupter.interrupt();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		TaskGenerator.initiateShutdown();
	}

}
