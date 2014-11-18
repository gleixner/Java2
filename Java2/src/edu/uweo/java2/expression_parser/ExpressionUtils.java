package edu.uweo.java2.expression_parser;

import java.util.Arrays;

public class ExpressionUtils {

	final String oExp;
	final String sExp;
	final char[] cExp;

	public ExpressionUtils( String expression) {
		if( expression == null ) {
			throw new IllegalArgumentException("input string must not be null");
		}
		oExp = expression;
		sExp = strip( expression );
		cExp = sExp.toCharArray();
	}
	
	/**
	 * Removes all whitespaces from the given string
	 * 
	 * @author James Gleixner
	 * 
	 * @param str
	 * 	String with whitespaces to be removed
	 * 
	 * @return Input string with the whitespaces removed
	 */
	public static String strip( String str ) {
		return str.replaceAll( "\\s", "");
	}
	/**
	 * Return the string that this object was initialized with
	 * 
	 * @author James Gleixner
	 * 
	 * @return Return the original expression
	 */
	public String getOriginalExpression() {
		return oExp;
	}
	
	/**
	 * Return the string this object was initialized with but with all whitespaces removed
	 * 
	 * @author James Gleixner
	 * 
	 * @return Return the initialization string with all whitespaces removed
	 */
	public String getStringExpression() {
		return sExp;
	}
	
	/**
	 * Returns a character array equivalent to the input string with all whitespaces removed
	 * 
	 * @author James Gleixner
	 * 
	 * @param NA
	 * 
	 * @return character array
	 */
	public char[] getCharacterExpression() {
		return Arrays.copyOf(cExp, cExp.length );
	}
	
	/**
	 * Returns true if the specificied character is a unicode letter, digit, or underscore character.
	 * 
	 * @author James Gleixner
	 * 
	 * @param test 
	 * 			a character to be tested if it is a letter, digit or "_"
	 * 
	 * @return true is the character is a letter, false otherwise
	 */			
	public static boolean isSymbolChar( char test ) {
		return (Character.isLetterOrDigit( test ) || test == '_');
	}
	
	/**
	 * Determines if the specified character is a valid hexidecimal character.
	 * Valid hexidecimal characters are digits 0-9 and letters A-F, case independent.
	 * 
	 * @author James Gleixner
	 * 
	 * @param test
	 * 			Character to be tested
	 * 
	 * @return true if character is a valid hex character, false otherwise
	 */
	public static boolean isHexChar( char test ) {
		String str = String.valueOf( test );
		return str.matches( "[0-9a-fA-F]");
	}
	
	/**
	 * 
	 * @author James Gleixner
	 * 
	 * @param test
	 * 			The character to be tested
	 * @return Return true if character is 1 or 0, false otherwise
	 */
	public static boolean isBinaryChar( char test ) {
		return ( test == '0' || test == '1' );
	}
	
	/**
	 * checks to see if a specified character is a digit 0-9.  If it isn't,
	 * check to see if the specified character is a '.' followed by a 0-9.
	 * If the specified character is '.' at the end of the string, return false.
	 * Return false if specified index is out of range.
	 * 
	 * @author James Gleixner
	 * 
	 * @param next
	 * 			Character to be tested
	 * @return Returns true if specified character is 0-9 or if it is an '.' 
	 *				 followed by a 0-9
	 */
	public boolean isDecimalValue( int next ) {
		boolean result = false;
		char ch = 0;
		
		if( next < cExp.length && next > -1 ) {
			ch = cExp[ next ];
		}
		
		if( ch > 47 && ch < 58 ) {
			result = true;
		} 
		else if( ch == '.' && !( next + 1 > cExp.length - 1 ) ) {
			ch = cExp[ next + 1 ];
			if( ch > 47 && ch < 58 ) {
				result = true;
			}
		}
		return result;
	}
	
	private boolean isStart( int next, String regex ) {
		boolean result = false;
		if( next + 2 < sExp.length() ){
			String str = sExp.substring( next, next + 3 );
			result = str.matches( regex );
		}
		return result;
	}
	
	/**
	 * Returns true if the specified position is a legal start to a binary number. 
	 * Returns false if it is not a legal start, or if the specified position is less than
	 * a distance of 2 from the end of the expression string
	 * 
	 * @author James Gleixner
	 * 
	 * @param next
	 * 				integer denoting the position of the character in the expression
	 * 				string to determine if it is a legal start to a binary number
	 * @return Return true if the given position begins with 0[bB][01]
	 */
	public boolean isBinaryValue( int next ) {
		return isStart( next, "0[bB][01]" );
	}
	
	
	/**
	 * Returns true if the specified position is a legal start to a hexadecimal number. 
	 * Returns false if it is not a legal start, or if the specified position is less than
	 * a distance of 2 from the end of the expression string
	 * 
	 * @author James Gleixner
	 * 
	 * @param next
	 * 				integer denoting the position of the character in the expression
	 * 				string to determine if it is a legal start to a binary number
	 * @return Return true if the given position begins with 0[xX][0-9a-fA-F]
	 */
	public boolean isHexValue( int next ) {
		return isStart( next, "0[xX][0-9a-fA-F]");
	}
	
	/**
	 * Examines
	 * 
	 * @author James Gleixner
	 * 
	 * @param next
	 * 			Character to be checked
	 * @return Return true if Character.isLetter is true, or if specified character
	 * 				is an underscore
	 */
	public boolean isSymbolValue( int next ) {
		char ch = cExp[ next ];
		boolean result = Character.isLetter(ch) || ch == '_';
		return result;
	}
	
	/**
	 * Returns true if the character specified at index next is part of a function value.
	 * A symbol is part of a function value if the character following the end of the symbol
	 * is "(".
	 * If the given index is out of bounds, this method returns false.  If the specified
	 * index is less than zero or greater than the expression length, return false.
	 * @param next
	 * 				Index specifying a character in a symbol
	 * @return
	 * 			True if index specifies a symbol that is followed "(", returns false otherwise.
	 */
	public boolean isFunctionValue( int next ) {
		
		boolean result = false;
		try{
			int symEnd = findLastSymbolChar( next );
			if( symEnd + 1 != cExp.length ) {
				result = cExp[ symEnd + 1 ] == '(';
			}
		} catch( IllegalArgumentException ex ) {}
		
		return result;
	}
	
	/**
	 * Finds the parenthesis that matches the specified parenthesis.  If a character is
	 * specified that is not a parenthesis, an error is thrown.  If the provided integer is
	 * less than 0 or greater than the length of the expression stripped of spaces, 
	 * an error is thrown.
	 * 
	 * @author James Gleixner
	 * 
	 * @param next
	 * 			Character to be checked
	 * 
	 * @return  Return the location of the parenthesis that matches the provided
	 * 			parenthesis
	 */
	public int findClosingParenthesis( int next ) {
		if( cExp[ next ] != '(' ) {
			throw new IllegalArgumentException( "value at " + next + " must be"
					+ "a left parenthesis");
		}
		
		int pCount = 0;
		char ch;
		int result = next + 1;

		for(; result < cExp.length; ++result ) {
			ch = cExp[result];
			if( ch == ')' && pCount == 0 ) {
				break;
			} 
			else if( ch == ')' ) --pCount;
			else if( ch == '(' ) ++pCount;
		}
		
		return result;
	}
	
	/**
	 * Given an index that specifies the start of a symbol, this method returns the index
	 * that specifies the last character of that symbol.  Throws an error if the specified index 
	 * does not correspond to a symbol character.  Throws an error if the specified index
	 * is longer than the stripped expression string or is less than 0.  
	 * 
	 * @author James Gleixner
	 * 
	 * @param from
	 * 			Integer that specifies the start of a symbol
	 * @return
	 * 			Index of last character in symbol
	 */
	public int findLastSymbolChar( int from ) {
		if( !isSymbolValue(  from ) ) 
			throw new IllegalArgumentException( "Value at " + from + " must be"
					+ "a valid symbol value");
		
		int result = from;
		while( result < cExp.length && isSymbolChar( cExp[result] ) ){
			++result;
		}
		return result - 1;
	}
	
	/**
	 * Given an index that specifies a character that is a valid digit, returns the index 
	 * of the last digit that is part of the decimal number.  Throws an error if the specified index 
	 * does not correspond to a digitcharacter.  Throws an error if the specified index
	 * is longer than the stripped expression string or is less than 0.
	 * 
	 * @author James Gleixner
	 * 
	 * @param from
	 * 			index of character to be examined
	 * @return
	 * 		index of last digit in decimal number
	 */
	public int findLastDecimalDigit( int from ) {
		if( !isDecimalValue(  from ) ) 
			throw new IllegalArgumentException( "Value at " + from + " must be"
					+ "a valid symbol value");
		
		int result = from;
		boolean p = false;
		for(; result < cExp.length; ++result ) {
			char ch = cExp[result];
			if( ch == '.' && p )
				break;
			else if( ch == '.' ) 
				p = true;
			else if( !Character.isDigit( ch  ) )
				break;
		}
		return result - 1;
	}

	/**
	 * Given an index that is an allowable start to a hexadecimal number, 
	 * this method finds the last index of that number.  Throws an error if the specified index 
	 * does not correspond to a hexadecimal character.  Throws an error if the specified index
	 * is longer than the stripped expression string or is less than 0.
	 * 
	 * @author James Gleixner
	 * 
	 * @param start
	 * 		  The position that the string in question starts at
	 * 
	 * @return The index for the last position of the hex value
	 */
	public int findLastHexDigit( int start ) {
		if( !isHexValue( start ) )
			throw new IllegalArgumentException( "Value at " + start + " must be"
					+ " a valid hexadecimal value");
		
		int result = start + 2;
		while( result < cExp.length && isHexChar( cExp[result] ) ) 
			++result;
		return result - 1;
	}
	
	/**
	 * Given an index that points to a legal start to a binary digit, return the index pointing
	 * to the last digit of that binary number.  Throws an IllegalArgumentException
	 * if the specified index is out of bounds for the character array, or if the specified
	 * index does not point to a valid start of a binary number.
	 * 
	 * @author James Gleixner
	 * 
	 * @param from
	 * 			index of the start of the putative binary digit
	 * @return index of the last digit in the specified binary number
	 */
	public int findLastBinaryDigit( int from ) {
		if( !isBinaryValue( from ) )
			throw new IllegalArgumentException( "Value at " + from + " must be"
					+ "a valid binary value");
		
		int result = from + 2;
		while( result < cExp.length && isBinaryChar( cExp[result] ) )
			++result;

		return result - 1;
	}
}
