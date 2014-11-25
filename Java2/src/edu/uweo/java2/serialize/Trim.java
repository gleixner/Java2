package edu.uweo.java2.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uweo.java2.homework.serialize.SerializableObject;

public class Trim {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String input = "C:\\Users\\chq-jamesgl\\git\\Java2\\Java2\\resources\\to_trim.ser";
		String output = "C:\\Users\\chq-jamesgl\\git\\Java2\\Java2\\resources\\to_trimmed.ser";
		Trim trm = new Trim( input, output );
		trm.process();
	}

	private String input;
	private String output;
	private List<SerializableObject> list;
	private List<Integer> toTrim;

	private List<SerializableObject> outputList;


	public Trim( String input, String output) {
		this.input = input;
		this.output = output;
		outputList = new ArrayList<>();
	}

	public void process() throws IOException, ClassNotFoundException {
		deserializeObjects();
		trim();
		serializeObject();
	}

	private void deserializeObjects() throws IOException, ClassNotFoundException {
		try(ObjectInputStream in = new ObjectInputStream( new FileInputStream( input )) ) {
			list  = ( List<SerializableObject> ) in.readObject();
			toTrim = ( List<Integer> ) in.readObject();
		}
	}

	private void serializeObject() throws IOException, ClassNotFoundException {
		try( ObjectOutputStream out = 
				new ObjectOutputStream( new FileOutputStream( output ) ) ) {
			out.writeObject( outputList );
		}
	}

	private void trim() {
		for( SerializableObject ser : list ) {
			int hsh = ser.hashCode();
			if( Collections.binarySearch( toTrim, hsh ) < 0 ) {
				outputList.add( ser );
			}
		}
	}
}