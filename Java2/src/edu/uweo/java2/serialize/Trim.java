package edu.uweo.java2.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import serialization.Employee;
import edu.uweo.java2.homework.serialize.SerializableObject;

public class Trim {

	public static void main(String[] args) {
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

	public void process() {
		deserializeObjects();
		trim();
		serializeObject();
	}

	private void deserializeObjects() {
		try(ObjectInputStream in = new ObjectInputStream( new FileInputStream( input )) ) {
			list  = ( List<SerializableObject> ) in.readObject();
			toTrim = ( List<Integer> ) in.readObject();
		}
	}

	private void serializeObject() {
		try( ObjectOutputStream out = 
				new ObjectOutputStream( new FileOutputStream( output ) ) ) {
			
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