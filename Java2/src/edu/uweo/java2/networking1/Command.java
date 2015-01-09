package edu.uweo.java2.networking1;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class Command {
	PrintWriter writer;
	private BlockingQueue<String> mQ;
	protected volatile boolean success = false;
	ClientMaster boss = null;
	
	abstract boolean execute( Client cl );
	abstract String getName();

	protected void setup( Client cl ) {
		writer = cl.getWriter();
		mQ = cl.getQ();
	}

	void setLogger( ClientMaster boss ) {
		this.boss = boss;
	}
	
	protected void log( String msg ) {
		if( boss != null )
			boss.log( msg );
	}
	
	/**
	 * Gets a single message from the client.  If there is no message within 
	 * the given time, returns null.
	 * 
	 * @param timeout how long should you want to wait for a message
	 * @return
	 */
	protected String getMessage( int timeout ) {
		String msg = null;
		Long tm = System.currentTimeMillis();
		log("( command ) mQ IS: " + mQ);
		try {
			msg = mQ.poll( timeout, TimeUnit.MILLISECONDS );
		} catch (InterruptedException e) { 
			Thread.currentThread().interrupt();
		}
		log("( command) Retrieved Message: " + msg);
		log("( getMessage ) time to run " + (System.currentTimeMillis() - tm ) );
		return msg;
	}

	protected String getMessage() {
		return getMessage( 1000 );
	}

	public boolean success() {
		return success;
	}
}

class ShutdownCommand extends Command {

	public String getName() {
		return "ShutdownCommand";
	}

	public boolean execute( Client cl ) {
		setup( cl );
		writer.println( "shutdown" );
		writer.flush();
		String msg1 = getMessage();
		String msg2 = getMessage();

		log("( command ) has messages " + msg1 + " " + msg2);
		if( msg1 != null && msg2 != null 
				&& msg1.contains( "ack" ) && msg2.contains("SERVER SHUTTING DOWN") ) {
			success = true;
//			System.out.println( "in the if statement");
		}
		return success;
	}
}

class WaitCommand extends Command {
	private int time;

	public WaitCommand( int millis ) {
		time = millis;
	}

	@Override
	boolean execute(Client cl) {
		success = true;
		try {
			Thread.sleep( time );
		} catch (InterruptedException e) { 
			Thread.currentThread().interrupt();
			success = false;
		}
		return success;
	}

	@Override
	String getName() {
		return "WaitCommand";
	}
}

class BasicStartupCommand extends StartCommand {
	private int timeout;

	public BasicStartupCommand( int timeout ) {
		this.timeout = timeout;
	}

	public BasicStartupCommand() {
		this( 8000 );
	}

	@Override
	boolean execute(Client cl) {
		initialize( cl );
		try {
			Thread.sleep( 50 );
		} catch (InterruptedException e) {}
		setup( cl );
		String msg = getMessage( timeout );
		if( msg != null ) {
			success = true;
		}
		return success;
	}

	@Override
	String getName() {
		return "BasicStartupCommand";
	}
}

class MessageStartupCommand extends StartCommand {
	private int timeout;
	private String expectedMsg;

	public MessageStartupCommand( String expectedMsg, int timeout ) {
		this.expectedMsg = expectedMsg;
		this.timeout = timeout;
	}

	public MessageStartupCommand( String expectedMsg ) {
		this( expectedMsg, 8000 );
	}

	boolean execute(Client cl) {
		initialize( cl );
		setup( cl );
		String msg = getMessage( timeout );
		success = ( msg != null && msg.equals( expectedMsg ) );
		return success;
	}

	@Override
	String getName() {
		return "MessageStartupCommand";
	}
	
}

/**
 * This class denotes a command that is legal to be at the start of a job list
 * The Client Master ensures that the first command in the commandQ inherits
 * this class.
 * @author chq-jamesgl
 */
abstract class StartCommand extends Command{
	String IP;
	int port;
	private Thread t;
	
	/**
	 * Returns true if the IP and port have been set, returns  false otherwise
	 * @return
	 */
	public boolean isValid() {
		return port != 0 && IP != null;
	}
	
	public void initialize( Client cl ) {
		cl.setIP( IP );
		cl.setPort( port );
		t = new Thread( cl );
		t.start();
	}
	
	void setServerIP( String serverIP ) {
		IP = serverIP;
	}
	void setServerPort( int port ) {
		this.port = port;
	}
	String getServerIP() {
		return IP;
	}
	int getServerPort() {
		return port;
	}
	
	public void join() throws InterruptedException {
		if( t != null ) {
			t.join();
		}
	}
}