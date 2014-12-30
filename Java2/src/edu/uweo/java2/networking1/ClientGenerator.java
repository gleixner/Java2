package edu.uweo.java2.networking1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientGenerator {
	private static ArrayList<Thread> threads = new ArrayList<>();

	public static void main( String arg[] ) {
		//make server settings
		Map<String, String> settings = new HashMap<>();
		settings.put("accept-timeout", "500");
		settings.put("active-clients", "1");
		settings.put("port", "57001");
		//start server
		PlayGroundServer pgs = new PlayGroundServer( settings );
		System.out.println( pgs.getCurrentSettings() );
		
		Thread server = new Thread( pgs );
		server.start();
		
		//create messages
		Message[][][] batch= { //client level
				{
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "goodbye", 1, 1) },
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "goodbye", 1, 1) },
					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
				},
				{
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
				},
				{
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
				},
				{
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
				},
				{
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
				},
				{
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
//					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
					{new Message( "ping", 5, 100), new Message( "time", 1, 50 ), new Message( "shutdown", 1, 1) },
				},
		};
		
		//Generate a bunch of clients
		for( int i = 0 ; i < batch.length; ++i ) {
			Client cl = new Client( i, batch[i] );
			Thread thr = new Thread( cl );
			threads.add( thr );
			thr.start();
		}
		
		//Clients talk to server and print out any message they get
		//shutdown server
		for( Thread t : threads ) {
			try {
				t.join();
			} catch (InterruptedException e) {}
		}
//		pgs.shutdown();
		System.out.println( "Client Generator has finished" );
	}
	
	private static class Client implements Runnable {
//		private int serverPort = 43211;
//		private String serverIP = "192.168.1.146";
		private String serverIP = "localhost";
		private int serverPort = 57001;


		private Socket sock;
		BufferedReader reader;
		PrintWriter writer;
		private int clientNumber;
		
		private AtomicBoolean shutdown = new AtomicBoolean( false );
		private Message[][] messages;
		
		public Client( int number, Message[][] messages ) {
			clientNumber = number;
			this.messages = messages;
		}
		
		public void run() {
			go();
			System.out.println( "Client number " + clientNumber + " has finished");
		}
		
		public void go() {
			for( int i = 0; i < messages.length && !shutdown.get(); ++i ) {
//				System.out.println( "Running message " + i );
				try {
					sock = new Socket( serverIP, serverPort);
					InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
					reader = new BufferedReader(streamReader);
					writer = new PrintWriter(sock.getOutputStream());
//					System.out.println("networking established");
					Thread readerThread = new Thread( new IncomingReader() );
					readerThread.start();
					Thread.sleep( 500 );
					process( messages[i] );
				} 
				catch (IOException e) {} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					try{
						if( reader != null ) {
							reader.close();
						}
						if( writer != null ) {
							writer.close();
						}
						if( sock != null && !sock.isClosed() ) {
							sock.close();
						}
					}
					catch( Exception e) {}
				}
				
			}
		}

		private boolean process( Message[] instructions ) {
			boolean exit = false;
//			System.out.println( "Processing instructions ");
//			System.out.println( Arrays.toString( instructions ) );
//			System.out.println( instructions[0]);
			try{
				for( int i = 0 ; i < instructions.length && !shutdown.get() ; ++i ) {
					Message m = instructions[i];
					System.out.println( "Client " + clientNumber + " sending message " + m.command );
					for( int j = 0; j < m.repeat && !shutdown.get() ; ++ j ) {
						if( m.command.equals( "goodbye" ) )
							exit = true;
						writer.println( m.command );
						writer.flush();
						System.out.println( "Client " + clientNumber + " message sent " + m.command);
						Thread.sleep( m.delay );
					}
				}
			}
			catch( Exception e ) {
				System.out.println("Caught an exception in process");
			}
			return exit;
		}

		public class IncomingReader implements Runnable
		{
			public void run()
			{
				String message;
				try {
					while((message = reader.readLine()) !=null) {
						System.out.println("Client#" + clientNumber + ": " + message );
//						if( message.equals( "SERVER SHUTTING DOWN" ) ) {
//							end();
//							break;
//						} 
//						else
//							messageQ.put( message );
					}
				} 
				catch (IOException ex) {
//					ex.printStackTrace();
					System.out.println("Client#" + clientNumber + ": IOException thrown" );
				}
				System.out.println( "Client#" + clientNumber + ": Listener is closed" );
			}

			private synchronized void end() {
				shutdown.set( true );
				try {
					sock.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static class Message {
		String command;
		int repeat;
		int delay;
		
		public Message( String command, int repeat, int delay ) {
			this.command = command;
			this.repeat = repeat;
			this.delay = delay;
		}
		
		public String toString() {
			return command + " " + repeat + " " + delay;
		}
	}
}
