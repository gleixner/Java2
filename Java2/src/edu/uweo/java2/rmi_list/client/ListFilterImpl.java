package edu.uweo.java2.rmi_list.client;

import java.io.Serializable;

import edu.uweo.java2.rmi_list.interfaces.ListFilter;

public class ListFilterImpl implements ListFilter<String>, Serializable {

	private static final long serialVersionUID = 227L;
	
	@Override
	public boolean test(String test) {
		return test.startsWith( "A" );
	}
}
