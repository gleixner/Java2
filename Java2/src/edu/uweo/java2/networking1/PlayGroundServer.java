package edu.uweo.java2.networking1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayGroundServer implements Runnable {

	private int SO_TIMEOUT;
	private int ACTIVE_CLIENTS;
	private int CLIENT_TIMEOUT;
	private int PORT;
	private String GREETING;

	private volatile boolean shutdown = false;
	private List<Thread> threadPool = new ArrayList<>();
	private List<ClientHandler> handlerPool = new ArrayList<>();
	private BlockingQueue<Socket> socketQueue = new LinkedBlockingQueue<>();
	private ServerSocket serverSock;

	private long startTime;
	private AtomicInteger connectionCount = new AtomicInteger( 0 );

	//BEGIN CONFIGURATION MODULE
	private Map<String, String> currentSettings = new HashMap<>();
	{
		currentSettings.put("accept-timeout", "500");
		currentSettings.put("active-clients", "4");
		currentSettings.put("client-timeout", "500");
		currentSettings.put("port", "57001");
		currentSettings.put("greeting", "Welcome to the playground.");
	}

	private final Map<String, Config> CONFIGURATIONS = new HashMap<>();
	{
		CONFIGURATIONS
		.put("accept-timeout", (a) -> SO_TIMEOUT = Integer.parseInt( a ) );
		CONFIGURATIONS
		.put("active-clients",  (a) -> ACTIVE_CLIENTS = Integer.parseInt( a ) );
		CONFIGURATIONS
		.put("client-timeout", (a) -> CLIENT_TIMEOUT = Integer.parseInt( a ) );
		CONFIGURATIONS
		.put("port", (a) -> PORT = Integer.parseInt( a ) );
		CONFIGURATIONS
		.put("greeting", (a) -> GREETING = a);
	}

	public PlayGroundServer( Map<String, String> settings ) {
		if( settings != null && !settings.isEmpty() ){
			for( String key : settings.keySet() ) {
				currentSettings.put( key, settings.get( key ) );
			}
		}

		for( String key : currentSettings.keySet() ) {
			Config func = CONFIGURATIONS.get( key );
			//make sure that there is a function for a given key
			if( func != null ) {
				func.execute( currentSettings.get( key ) );
			}
		}
	}
	//END CONFIGURATION MODULE

	//BEGIN SERVER OPERATION MODULE
	/**
	 * This method "turns on" the server.  Once invoked, the server will hang, waiting 
	 * for client connections.
	 * @throws IOException 
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		//Create client handlers and their threads and start them running
		startClientHandlers();
		System.out.println( "SERVER: now accepting connections");

		//Listen for client connections, put them in the Queue as they come in
		try( ServerSocket tmpServer = new ServerSocket( PORT ) ) {
			serverSock = tmpServer;
			serverSock.setSoTimeout( SO_TIMEOUT );

			while( !shutdown && !Thread.currentThread().isInterrupted() )
			{

				try {
					Socket clientSocket = serverSock.accept();
					socketQueue.add( clientSocket );
					connectionCount.incrementAndGet();
					System.out.println( "SERVER: client added to queue");
				}
				catch( SocketTimeoutException e ) {}

			}
		} 
		catch( IOException e ) {
			e.printStackTrace();
			System.exit(1);
		}
		catch (Exception ex) {} 

		//make sure that every client processing thread has finished before exiting.
		shutdown();
		joinAllThreads();
	}

	//make sure that every client processing thread has finished before exiting.
	private void joinAllThreads() {
		for( Thread thr : threadPool ) {
			try {
				thr.join();
			} catch (InterruptedException e) { Thread.currentThread().interrupt(); }
		}
	}

	private void startClientHandlers() {
		for( int i = 0; i < ACTIVE_CLIENTS; ++i ) {
			ClientHandler client = new ClientHandler();
			Thread t = new Thread( client );
			handlerPool.add( client );
			threadPool.add( t );
			t.start();
		}
	}

	public synchronized void shutdown() {
		if( !shutdown ) {
			System.out.println( "SERVER: Shutdown command starting" );
			shutdown = true;
			for( ClientHandler c : handlerPool ) {
				c.end();
			}
			for( Thread t : threadPool ) {
				t.interrupt();
			}
		}
	}

	@Override
	public void run() {
		start();
		System.out.println( "SERVER: Server has exited" );
	}
	//END SERVER OPERATION MODULE

	//BEGIN CLIENT OPERATION MODULE
	//Create client handlers and their threads and start them running

	/**
	 * The class manages communication with clients.
	 * It picks up client sockets
	 * @author chq-jamesgl
	 *
	 */
	private class ClientHandler implements Runnable {
		Socket clientSocket;
		PrintWriter writer;
		BufferedReader reader;

		/**
		 * Get a socket, set up the reader and writer, then handle client communications
		 * Continue doing this until told to stop
		 */
		public void run() {
			while( !shutdown && !Thread.currentThread().isInterrupted() ) {
				try {
					clientSocket = socketQueue.poll( CLIENT_TIMEOUT, TimeUnit.MILLISECONDS );
					if( clientSocket == null ) { continue; }
					writer = new PrintWriter( clientSocket.getOutputStream(), true);
					reader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream() ) );
					System.out.println("SERVER( CLIENT_HANDLER ): client accepted");
					handleClient();
				} 
				catch (IOException e) {} 
				catch (InterruptedException e1) { Thread.currentThread().interrupt();}
				
				connectionCount.decrementAndGet();
				try{
					if( clientSocket != null ){
						writer.close();
						clientSocket.close();
						reader.close();
					}
				}
				catch( IOException e ) {}
			}

			cleanSocketQueue();
		}

		//Wait for enquires from client and respond to them
		private void handleClient() throws IOException{
			//Issue greeting
			writer.println( GREETING );
			String message;
			while( !shutdown
					&& !Thread.currentThread().isInterrupted()
					&&( message = reader.readLine() ) != null ) {
				System.out.println( "SERVER: received message " + message );
				if( process( message ) )
					break;
			}
		}

		private boolean process( String message ) {
			boolean exit = false;
			String[] msg = message.split( " " );
			String cmd = msg[0];
			switch (cmd) {
			case "time":
				writer.println( System.currentTimeMillis() );
				break;
			case "echo":
				writer.println( message.substring( 5) );
				break;
			case "ping":
				writer.println( "ack" );
				break;
			case "clients":
				writer.println( connectionCount.get() );
				break;
			case "run-time":
				Long time = System.currentTimeMillis() - startTime;
				writer.println( time );
				break;
			case "goodbye":
				writer.println( "ack" );
				exit = true;
				break;
			case "shutdown":
				writer.println( "ack" );
				shutdown();
				System.out.println("SERVER: shutdown command finished");
				break;
			default:
				writer.println( "Nuh uh uh, you didn't say the magic word!" );
				break;
			}
			return exit;
		}

		//Look at every socket in the socketQueue and close them
		private void cleanSocketQueue() {
			while( socketQueue.size() > 0 ) {
				try { 
					clientSocket = socketQueue.poll();
					writer = new PrintWriter( clientSocket.getOutputStream(), true );
					end();
				} 
				catch (IOException e) {}
			}
		}

		public synchronized void end() {
			if( clientSocket != null ) {
				writer.println( "SERVER SHUTTING DOWN" );
				try {
//					Thread.sleep(200);
					clientSocket.close();
				} 
				catch (IOException e) {} 
//				catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				
			}
		}
	}

	//END CLIENT OPERATION MODULE

	/**
	 * A functional interface to allow convenient configuration of settings
	 * @author chq-jamesgl
	 *
	 */
	private interface Config {
		public void execute( String setting );
	}

	//An overly complicated method to print out the server's settings
	//A little too complicated but a little self reflection every now and then is good
	public String getCurrentSettings() {
		StringBuilder sb = new StringBuilder();
		for( Field fd : this.getClass().getDeclaredFields() ) {
			if( fd.getModifiers() == Modifier.PRIVATE &&
					(fd.getType().equals( Integer.TYPE ) || fd.getType().equals( String.class ) ) ) {
				fd.setAccessible( true );
				Object value = null;
				try {
					value = fd.get( this );
				} catch (Exception e) {}
				sb.append( value );
				sb.append( "\n" );
			}
		}
		return sb.toString();
	}
}
