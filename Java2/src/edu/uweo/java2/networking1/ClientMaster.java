package edu.uweo.java2.networking1;

import java.util.LinkedList;
import java.util.Queue;

public class ClientMaster implements Runnable {

	Queue<Command> cQ;
	Queue<String> log = new LinkedList<>();
	private String name;
	private volatile boolean end = false;

	public ClientMaster( Queue<Command> job, int name ) {
		cQ = job;
		this.name = "ClientMaster " + name + ": ";
	}

	@Override
	public void run() {
		//creates client
		Client cl = new Client( this );
		Command sc = cQ.poll();
		sc.setLogger( this );
		
		//First command must always be a start up command that checks the Q to
		if( !(sc instanceof StartCommand ) ) {
			log.add( "Command Queue did not begin with a start Command" );
			end = true;
		}
		StartCommand startC = (StartCommand) sc;
		if( !end && !startC.isValid() ) {
			log( "server configuration has not been set for the start command");
			end = true;
		}
		
		//make sure a startup message was sent
		try {
			if( !end && !startC.execute( cl ) ) {
				log( "Client failed on startup" );
			}
		}
		catch( Exception e ) {
			end = true;
			log( "Client threw exception on startup " );
			e.printStackTrace();
		}
		
		
		//loops
		//invokes command on client
		//checks to make sure everything works as expected
		if( !end ) {
			log( sc.getName() + " Success:" + sc.success + " Exception:" + false );
			for( Command c : cQ ) {
				if( end )
					break;
				c.setLogger( this );
				boolean exception = false;
				try {
					c.execute( cl );
				}
				catch( Exception e ) {
					exception = true;
					end = true;
					e.printStackTrace();
				}
				log( c.getName() + " Success:" + c.success + " Exception:" + exception );
			}
		}
		//die gracefully
		if( !end ) {
			try {
				startC.join();
			} catch (InterruptedException e) {
				log("client thread experienced InterruptedExceptionn");
				e.printStackTrace();
			}
		}
	}
	
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		for( String msg : log ) {
			sb.append( msg + "\n" );
		}
		return sb.toString();
	}
	
	void log( String msg ) {
		log.add( name + msg );
	}
}