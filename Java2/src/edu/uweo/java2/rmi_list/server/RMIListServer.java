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
	
	private String regHost = "localhost";
	private int report = 1099;
	private static final String NAME = "rmi:rmilist";

	public RMIListServer() {}

	public RMIListServer( String regHost, int report ) {
		this.regHost = regHost;
		this.report = report;
	}

	@Override
	public void run() {
		Registry registry = null;
		
		try {
			registry = LocateRegistry.getRegistry( regHost, report );
			RMIFilteredList stub = (RMIFilteredList) UnicastRemoteObject.exportObject( this, 0 );
			registry.rebind( NAME, stub );
		}
		catch( RemoteException e ) {
			e.printStackTrace();
			System.exit(1);
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
}
