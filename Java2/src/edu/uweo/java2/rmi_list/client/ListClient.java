package edu.uweo.java2.rmi_list.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uweo.java2.rmi_list.interfaces.ListFilter;
import edu.uweo.java2.rmi_list.interfaces.RMIFilteredList;

public class ListClient {

	public ListClient() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main ( String[] args ) {
		try {
//			String name = "localhost";
//			int port = 1099;
			Registry registry = LocateRegistry.getRegistry();
			RMIFilteredList rmiServe = (RMIFilteredList) registry.lookup("rmi:rmilist");
			ListFilter<String> filter = new ListFilterImpl();
			
			Collection<String> input = new ArrayList<>();
			input.add( "Abcd" );
			input.add( "Bcde" );
			input.add( "Alpha" );
			
			List<String> result = rmiServe.getList( input, filter );
			System.out.println( result );
		}
		catch( RemoteException | NotBoundException e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		}
	}

}
