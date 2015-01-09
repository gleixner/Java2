package edu.uweo.java2.rmi_list.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

public interface RMIFilteredList extends Remote {

	<T extends Comparable<?> > List<T> getList( Collection<T> input, ListFilter<T> filter ) throws RemoteException;
	
}
