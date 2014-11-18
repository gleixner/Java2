package edu.uweo.java2.expression_parser;

import java.util.HashMap;
import java.util.Map;

public class Symbols {

	private static Map<HashKey, Double> UserSymbolMap_ = new HashMap<>();
	
	private static Map<HashKey, Double> predefinedSymbolMap_ = new HashMap<>();
	static {
		predefinedSymbolMap_.put( new HashKey( "PI" ), java.lang.Math.PI);
		predefinedSymbolMap_.put( new HashKey( "E" ), java.lang.Math.E);
	}
	
	private Symbols() {}
	
	
	/**
	 * Stores a symbol and its associated value.  The symbols are case 
	 * insensitive( c3p0 is the same as C3P0), so if you attempt to store 
	 * multiple symbols that differ only in the case of letters, the last added
	 *  symbol will override all the others.
	 * 
	 * @author James Gleixner
	 * 
	 * @param name
	 * 			The symbol to be added
	 * 			
	 * @param value
	 * 			The value associated with the symbol
	 */
	public static void addSymbol( String name, double value ) {
		HashKey hk = new HashKey( name );
		UserSymbolMap_.put( hk,  value );
	}
	
	/**
	 * Returns the value associated with a specified symbol.
	 * Symbols are case insensitive so c3p0 is the same as C3P0.
	 * 
	 * @author James Gleixner
	 * 
	 * @param name
	 * 			Specified symbol
	 * @return
	 * 			Return value associated with specified symbol
	 */
	public static double getSymbol( String name ) {
		HashKey hk = new HashKey( name );
		Double result = UserSymbolMap_.get( hk );
		if( result == null ) 
			result = predefinedSymbolMap_.get( hk );
		if( result == null ) {
			throw new IllegalArgumentException( "Symbol " + name + " is not defined");
		}
		return result;
	}

	private static class HashKey {
		private final String key;
		private final int hash;

		private HashKey( String str ) {
			key = str;
			hash = key.toLowerCase().hashCode();
		}

		/**
		 * Returns the hash code for this object.  HashKey's with initialized with the
		 * same string, or with strings that differe only by cases, will have the same
		 * hash code.
		 * 
		 * @author James Gleixner
		 * 
		 * @return Returns the hash code for this object
		 */
		public final int hashCode() {
			return hash;
		}
		
		/**
		 *  Returns true if the specified object is a HashKey, and if it 
		 * was initialized with the same string (case independent).
		 * 
		 * @author James Gleixner
		 * 
		 * @param obj
		 * 			Object specified
		 * 
		 * @return Returns true if the specified object is a HashKey, and if it 
		 * was initialized with the same string (case independent).
		 */
		public final boolean equals( Object  obj ) {
			boolean result = false;
			if( obj instanceof HashKey ) {
				HashKey tmp = (HashKey) obj;
				result = tmp.key.equalsIgnoreCase( key );
			}
			return result;
		}
	}
}
