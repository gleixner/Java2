package edu.uweo.java2.networking1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ClientBroker implements Runnable {
	
	private final int CLIENT = 4;
	private BlockingQueue cmdQ;
	
	ExecutorService threadPool;
	
	public ClientBroker() {
		threadPool = Executors.newFixedThreadPool( CLIENT );
		cmdQ = test1ClientShutdown();
	}
	
	public BlockingQueue test1ClientShutdown() {
		
	}
	
	public static class Client {
		BlockingQueue mQ;
		
	}
	
	private interface Command {
		void execute( Client cl );
	}
	
	private class ShutdownCommand implements Command {
		public void execute( Client cl ) {
			cl.getWriter().println( "shutdown" );
			cl.getWriter().flush();
			
		}
	}
}
