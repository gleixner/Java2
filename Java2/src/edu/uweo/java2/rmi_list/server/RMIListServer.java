package edu.uweo.java2.rmi_list.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uweo.java2.rmi_list.interfaces.ListFilter;
import edu.uweo.java2.rmi_list.interfaces.RMIFilteredList;

public class RMIListServer implements RMIFilteredList, Runnable {
	
	boolean debug = true;
	String regHost = "localhost";
	int report = 0;
	String name = "rmi:rmilist";

	public RMIListServer() {}

	public RMIListServer( String regHost, int report ) {
		this.regHost = regHost;
		this.report = report;
	}

	@Override
	public void run() {
		Registry registry = null;
		
		try {
			if( debug ) {
				report = 1099;
				registry = LocateRegistry.createRegistry( report );
				System.out.println( "Registry open");
			} else {
				registry = LocateRegistry.getRegistry( regHost, report );
			}
			RMIFilteredList stub = (RMIFilteredList) UnicastRemoteObject.exportObject( this, report);
			registry.rebind( name, stub );
			System.out.println( "Server bound and open for connection");
			
		}
		catch( RemoteException e ) {
			e.printStackTrace();
		}

	}

	public <T extends Comparable<?>> List<T> getList(Collection<T> input,
																					   ListFilter<T> filter) 
																				   throws RemoteException {
		List<T> result = new ArrayList<>();
		for( T item : input ) {
			if( filter.test( item ) )
				result.add( item );
		}
		return result;
	}
	
	public static void main( String[] args ) {
		RMIListServer server = new RMIListServer();
		server.run();
	}

}
