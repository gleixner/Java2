package edu.uweo.java2.networking1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Client implements Runnable {
	BlockingQueue<String> mQ = new LinkedBlockingQueue<>();
	PrintWriter writer;
	private BufferedReader reader;
	ClientMaster boss = null;

	//	private int serverPort = 43211;
	//	private String serverIP = "192.168.1.146";
	private String serverIP = "localhost";
	private int serverPort = 57001;
	
	public Client( String serverIP, int serverPort ) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	
	public Client(){}
	
	public Client( String serverIP, int serverPort, ClientMaster boss ) {
		this( serverIP, serverPort );
		this.boss = boss;
	}
	
	public Client( ClientMaster boss ) {
		this.boss = boss;
	}
	
	private void log( String msg ) {
		if( boss != null )
			boss.log( msg );
	}
	
	public void setIP( String IP ) {
		serverIP = IP;
	}
	
	public void setPort( int port ) {
		serverPort = port;
	}

	public void run() {
		go();
		log( "( client )  finished");
	}

	public void go() {
		if( serverIP == null || serverPort == 0 ) {
			throw new RuntimeException( "server or port has not been set");
		}
		try ( Socket sockTmp = new Socket( serverIP, serverPort); 
				InputStreamReader inTmp = new InputStreamReader( sockTmp.getInputStream() );
				BufferedReader readerTmp = new BufferedReader( inTmp );
				PrintWriter writerTmp = new PrintWriter(sockTmp.getOutputStream());
		)
		{
			reader = readerTmp;
			writer = writerTmp;
			listen();
		} 
		catch (IOException e) {} 
	}
	
	private void listen() {
		String message;
		try {
			while( (message = reader.readLine()) != null 
						&& !Thread.currentThread().isInterrupted() ) {
				log("( incoming reader) putting message " + message );
				mQ.put( message );
			}
		}
		catch( SocketException e ) {
			log( "( incoming reader ) SocketException thrown");
		}
		catch (IOException ex) {
			log("( incoming reader ) IOException thrown");
			ex.printStackTrace();
		}
		catch (InterruptedException e) { 
			Thread.currentThread().interrupt();
			log("( incoming reader ) InterruptedException thrown");
		}

		log( "( incoming reader ) Listener is closed" );
	}

	public BlockingQueue<String> getQ(){
		return mQ;
	}
	public PrintWriter getWriter() {
		return writer;
	}
}
