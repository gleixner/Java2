package edu.uweo.java2.networking1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.uweo.java2.networking1.ClientGenerator.Message;

public class ClientMasser {

	public ClientMasser() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		//make server settings
		Map<String, String> settings = new HashMap<>();
		settings.put("accept-timeout", "500");
		settings.put("active-clients", "1");
		settings.put("port", "57001");
		settings.put("greeting", "This should be different.");
		//Begin start server
		PlayGroundServer pgs = new PlayGroundServer( settings );
		System.out.println( pgs.getCurrentSettings() );
		
		Thread server = new Thread( pgs );
		server.start();
		//End start server
		
		
		
	}
	
	private static class Client implements Runnable {
		private int serverPort = 57001;
		private Socket sock;
		BufferedReader reader;
		PrintWriter writer;
		private int clientNumber;
		private BlockingQueue<String> messageQ = new LinkedBlockingQueue<>();
		
		private AtomicBoolean shutdown = new AtomicBoolean( false );
		private Command[] cmds;
		
		public Client( int number, Command[] cmds ) {
			clientNumber = number;
			this.cmds = cmds;
		}
		
		public void run() {
			go();
			System.out.println( "Client number " + clientNumber + " has finished");
		}
		
		public void go() {
			for( int i = 0; i < cmds.length && !shutdown.get(); ++i ) {
//				System.out.println( "Running message " + i );
				try {
					sock = new Socket("127.0.0.1", serverPort);
					InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
					reader = new BufferedReader(streamReader);
					writer = new PrintWriter(sock.getOutputStream());
//					System.out.println("networking established");
					Thread readerThread = new Thread( new IncomingReader() );
					readerThread.start();
					Thread.sleep( 500 );
					process( cmds[i] );
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

		private boolean process( Command cmd ) {
			boolean exit = false;
			cmd.execute( reader, writer );
			return exit;
		}

		public class IncomingReader implements Runnable
		{
			public void run()
			{
				String message;
				try {
					while((message = reader.readLine()) !=null && !shutdown.get() ) {
						if( message.equals( "SERVER SHUTTING DOWN" ) ) {
							end();
							break;
						}
						messageQ.add( message );
					}
				} 
				catch (IOException ex) {
//					ex.printStackTrace();
				}
				System.out.println( "Listener is closed" );
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
	
	static abstract class Command {
		
		public boolean execute( BufferedReader reader, PrintWriter writer ) {
			//send message
			//generate expected output
			//check Q for reply
		}
		
	}

}
