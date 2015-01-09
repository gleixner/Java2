package edu.uweo.java2.rmi_list.interfaces;

public interface ListFilter<T extends Comparable<?> >{

	boolean test( T test );
	
}
