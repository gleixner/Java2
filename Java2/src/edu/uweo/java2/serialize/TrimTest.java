package edu.uweo.java2.serialize;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;

import edu.uweo.java2.homework.serialize.SerializableObject;

public class TrimTest {

	private static Object[] deserializeObject( String input ) throws IOException, ClassNotFoundException {
		Object[] list = new Object[2];
		try(ObjectInputStream in = new ObjectInputStream( new FileInputStream( input )) ) {
			list[0] = in.readObject();
			list[1] = in.readObject();
		} catch ( EOFException ex ) {}
		return list;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String inputCheck = "resources/trimmed.ser";
		String inputOriginal = "resources/to_trim.ser";
		Object[] check = deserializeObject( inputCheck );
		Object [] original = deserializeObject( inputOriginal );
		
		 List<SerializableObject> testList = (  List<SerializableObject> ) check[0];
		 List<Integer> originalIntegers = (List<Integer>) original[1];
		 Collections.sort( originalIntegers );
		 
		 System.out.println(originalIntegers);
		 for( SerializableObject item : testList ) {
			 System.out.println( item );
		 }
	}

}
