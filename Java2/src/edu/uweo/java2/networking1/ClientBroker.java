package edu.uweo.java2.networking1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientBroker {

	public static void main( String[] args ) {
		for( int i = 0; i < 2; ++i ) {

			System.out.println("#########STARTING TEST " + i  + " ##############");
			//make server settings
			Map<String, String> settings = new HashMap<>();
			settings.put("accept-timeout", "500");
			settings.put("active-clients", "1");
			settings.put("port", "57001");
			//start server
			PlayGroundServer pgs = new PlayGroundServer( settings );
			//			System.out.println( pgs.getCurrentSettings() );

			Thread server = new Thread( pgs );
			server.start();

			ClientBroker cb = new ClientBroker( true );
			cb.setJobList( ClientBroker.make5ClientJobs() );
			cb.run();

			try {
				server.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("#########ENDING TEST " + i  + " ##############\n");
		}
	}
	//#############################
	
	
	private final int CLIENTS = 4;
	List< Queue<Command> > jobList;
	boolean logging = false;

	ExecutorService threadPool;

	public ClientBroker( boolean logging ) {
		threadPool = Executors.newFixedThreadPool( CLIENTS );
		this.logging = logging;
	}

	public void run() {
		List<ClientMaster> logHistory = new ArrayList<>();

		if( jobList == null ) {
			throw new IllegalStateException( "Job List must be set" );
		}
		for( int i = 0; i < jobList.size(); ++i ) {
			ClientMaster cm = new ClientMaster( jobList.get( i ), i );
			if( logging )
				logHistory.add( cm );
			threadPool.execute( cm );
		}

		threadPool.shutdown();
		while( !threadPool.isTerminated() ) {
			try {
				Thread.sleep( 500 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if( logging ) {
			System.out.println("CLIENT_BROKER: printing log");
			for(ClientMaster cm : logHistory ) {
				System.out.println( cm.getLog() );
			}
		}
		System.out.println( "CLIENT_BROKER: Test Finished");
	}

	public void setJobList( List< Queue<Command> > jobList ) {
		this.jobList = jobList;
	}

	public static List< Queue<Command> > make1ClientShutdownJobs() {
		List< Queue<Command> > result = new ArrayList<>();
		LinkedList<Command> job1 = new LinkedList<>();
		StartCommand start = new BasicStartupCommand();
		start.setServerIP( "localhost" );
		start.setServerPort( 57001 );
		job1.add( start );
		job1.add( new WaitCommand( 1000 ) );
		job1.add( new ShutdownCommand() );
		result.add( job1 );
		return result;
	}
	
	public static List< Queue<Command> > make5ClientJobs() {
		List< Queue<Command> > result = new ArrayList<>();
		for( int i = 0; i < 4; ++ i ) {
			LinkedList<Command> job = new LinkedList<>();
			StartCommand start = new BasicStartupCommand();
			start.setServerIP( "localhost" );
			start.setServerPort( 57001 );
			job.add( start );
			job.add( new WaitCommand( 1000 ) );
			job.add( new ShutdownCommand() );
			result.add( job );
		}
		return result;
	}

}
