package edu.uweo.java2.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;

import edu.uweo.java2.homework.serialize.SerializableObject;

public class Trim {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		JFileChooser fc = new JFileChooser( new File( System.getProperty( "user.dir") ) );
		int info = fc.showOpenDialog( null );
		String input = null;
		if( info == JFileChooser.APPROVE_OPTION ) {
			input = fc.getSelectedFile().getAbsolutePath();
		} else {
			throw new IllegalArgumentException( "incorrect file chosen" );
		}
		String output = fc.getSelectedFile().getParent() + "/trimmed.ser";
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

	@SuppressWarnings("unchecked")
	private void deserializeObjects() throws IOException, ClassNotFoundException {
		try(ObjectInputStream in = new ObjectInputStream( new FileInputStream( input )) ) {
			Object tmp = in.readObject();
			if( tmp instanceof List<?> ) {
				List<?> tmp2 = ( List<?> ) tmp;
				if( !(tmp2.length() > 0) ) {
					throw new IOException( " Serialized object does not contain data" );
				}
				list  = ( List<SerializableObject> ) in.readObject();
				toTrim = ( List<Integer> ) in.readObject();
				Collections.sort( toTrim );
			}
		} catch ( IOException ex ) {
			throw new IOException( "Error opening file" );
		} catch ( ClassNotFoundException ex ) {
			throw new ClassNotFoundException( "Extracted class does not match cast" );
		}

	}

	private void serializeObject() throws IOException {
		try( ObjectOutputStream out = 
				new ObjectOutputStream( new FileOutputStream( output ) ) ) {
			out.writeObject( outputList );
		} catch ( IOException ex ) {
			throw new IOException( "Error opening file" );
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