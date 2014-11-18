package edu.uweo.java2.expression_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionUtilTest {

	public static void main(String[] args) {

		testStrip();
		testGetCharacterExpression();
		testIsSymbolChar();
		testIsHexChar(); 
		testIsBinaryChar();  
		testIsDecimalValue(); 
		testIsBinaryValue(); 
		testIsHexValue();  
		testIsSymbolValue(); 
		testFindClosingParenthesisSuccess();  
		testFindClosingParenthesisFailures();  
		testFindLastSymbolCharSuccess();   
		testFindLastSymbolCharFailures(); 
		testIsFunctionValue();
		testFindLastDecimalDigitSuccess();
		testFindLastDecimalDigitFailures(); 
		testFindLastHexDigitSuccess();  
		testFindLastHexDigitFailures();  
		testFindLastBinaryDigitSuccess();
		testFindLastBinaryDigitFailures();
		
	}

	public static void testStrip() {
		String[] cases = {"this  is a test", "  thisis a test   ", "abc		d"};
		String[] expected = {"thisisatest", "thisisatest", "abcd"};

		int failures = 0;
		List<String> failCases = new ArrayList<>();

		for( int i = 0; i < cases.length; ++i ) {
			String result = ExpressionUtils.strip( cases[i] );
			if( !result.equals( expected[i] ) ) {
				++failures;
				failCases.add( cases[i] );
			}
		}
		if( failures > 0 ) {
			System.out.println("Test failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testStrip passed with no errors%n%n");
		}

	}

	public static void testGetCharacterExpression() {
		String testCase = " a test";
		char[] expected = {'a', 't', 'e', 's', 't'};

		ExpressionUtils eU = new ExpressionUtils( testCase );
		char[] result = eU.getCharacterExpression();

		if( Arrays.equals(result,  expected) ) {
			System.out.printf( "testGetCharacterExpression passed%n%n");
		} else {
			System.out.printf( "testGetCharacterExpression failed%n%n");
		}
	}

	public static void testIsSymbolChar() {
		char[] testCases = {'a', 'B', '0', '_', '7', '%'};
		boolean[] testResults = {true, true, true, true, true, false};

		int failures = 0;
		List<Character> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length; ++i ) {
			boolean result = ExpressionUtils.isSymbolChar( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( testCases[i] );
			}
		}
		if( failures > 0 ) {
			System.out.println("Test failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testIsSymbolCharacter passed with no errors%n%n");
		}
	}

	public static void testIsHexChar() {
		char[] testCases = {'1', '9', '0', 'a', 'A', 'f', 'F', 'Z', ' ', 'g', 'G' };
		boolean[] testResults = {true, true, true, true, true, true, true, false, false, false, false};

		int failures = 0;
		List<Character> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			boolean hexCase = ExpressionUtils.isHexChar( testCases[i] );
			if( hexCase != testResults[i] ) {
				++failures;
				failCases.add( testCases[i] );
			}
		}
		if( failures > 0 ) {
			System.out.println("testIsHexChar failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.println( "testIsHexChar has passed with no failures");
		}
	}

	public static void testIsBinaryChar() {
		char[] testCases = {'1', '0', '7' };
		boolean[] testResults = {true, true, false};

		int failures = 0;
		List<Character> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			boolean hexCase = ExpressionUtils.isBinaryChar( testCases[i] );
			if( hexCase != testResults[i] ) {
				++failures;
				failCases.add( testCases[i] );
			}
		}
		if( failures > 0 ) {
			System.out.println("testIsBinaryChar failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.println( "testIsBinaryChar has passed with no failures");
		}
	}

	public static void testIsDecimalValue() {
		String[] testStrings = {"0.1+.4*6", "0.1+.4*6", "0.1+.4*6.", 
				"0.1+.4*6", "0.1+.4*.", "0.1+.4*."};
		int[] testCases = { 0, 4, 7, 3, 7, 8 };
		boolean[] testResults = {true, true, true, false, false, false};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			boolean result = eU.isDecimalValue( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( i );
			}
		}
		if( failures > 0 ) {
			System.out.println("testIsDecimalValue failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.println( "testIsDecimalValue has passed with no failures");
		}
	}

	public static void testIsBinaryValue() {
		String[] testStrings = {"0b1+0b" , "0b1+0b1", "0b1101", "0B11",};
		int[] testCases = { 4, 4, 0, 0,};
		boolean[] testResults = {false, true, true, true};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testStrings.length; ++i ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			boolean result = eU.isBinaryValue( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( i );
			}			
		}
		if( failures > 0 ) {
			System.out.println("testIsDecimalValue failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.println( "testIsDecimalValue has passed with no failures");
		}		
	}

	public static void testIsHexValue() {
		String[] testStrings = {"0x5",
											  "0X55", 
											  "0X55", 
											  "0xa1F+0x1", 
											  "0xa1F+0x1",
											  "0x2F*0X",
											  "0x0x1",
											  "0x0x1",
											  };
		int[] testCases = {0, 1, 0, 0, 6, 5, 0, 2  };
		boolean[] testResults = {true, false, true, true, true, false, true, true };

		int failures = 0;
		List<String> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			boolean hexCase = eU.isHexValue( testCases[i] );
			if( hexCase != testResults[i] ) {
				++failures;
				failCases.add( testStrings[i] );
			}
		}
		if( failures > 0 ) {
			System.out.println("Test failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testIsHexValue passed with no errors%n%n");
		}
	}

	public static void testIsSymbolValue() {
		String[] testStrings = {"x", "X", "_", "6", "a+b9", "a+b9" };
		int[] testCases = {0, 0, 0, 0, 1, 2 };
		boolean[] testResults = {true, true, true, false, false, true};

		int failures = 0;
		List<String> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			boolean result = eU.isSymbolValue( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( testStrings[i] );
			}
		}
		if( failures > 0 ) {
			System.out.println("testIsSymbolValue failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testIsSymbolValue passed with no errors%n%n");
		}
	}
	
	public static void testIsFunctionValue() {
		String[] testStrings = {"date()",
											  "pow( 2, 4 )",
											  "a()+4",
											  "a()+4",
											  "a()+b(",
											};
		int[] testCases = {0, 0, 3, 0, 4 };
		boolean[] testResults = {true, true, false, true, true };

		int failures = 0;
		List<String> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			boolean result = eU.isFunctionValue( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( testStrings[i] );
			}
		}
		if( failures > 0 ) {
			System.out.println("testIsFunctionValue failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testIsFunctionValue passed with no errors%n%n");
		}
	}

	public static void testFindClosingParenthesisSuccess() {
		String[] testStrings = {"(123", "123(", "5*((3+1)/4)", "5*((3+1)/4)", };
		int[] testCases = {0, 3, 2, 3, };
		int[] testResults = { 4, 4, 10, 7,};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			int result = eU.findClosingParenthesis( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( i );
			}
		}
		if( failures > 0 ) {
			System.out.println("testFindClosingParenthesisSuccess failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testFindClosingParenthesisSuccess passed with no errors%n%n");
		}
	}

	public static void testFindClosingParenthesisFailures() {
		String[] testStrings = {"(123", "123(", "5*((3+1)/4)", "5*((3+1)/4)", "123" };
		int[] testCases = {1, 2, 7, 10, -2 };

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			try {
				eU.findClosingParenthesis(  testCases[i] );
				++failures;
				failCases.add( i );
			} catch( IllegalArgumentException ex) {}
			catch( ArrayIndexOutOfBoundsException x){}
		}

		if( failures > 0 ) {
			System.out.println("testFindClosingParenthesisFailures failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testFindClosingParenthesisFailures passed with no errors%n%n");
		}
	}

	public static void testFindLastSymbolCharSuccess() {
		String[] testStrings = {"PI*radius*radius+may9*10",
											  "PI*radius*radius+may9*10",
											  "PI*radius*radius+may9*10",
											  "PI*radius*radius+may",
											  "4+c3p0",
											  "p",
											  "abc1",
		};
		int[] testCases = {0, 3, 17, 19, 2, 0, 0};
		int[] testResults = { 1, 8, 20, 19, 5, 0, 3};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();
		List<Integer> failResults = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			int result = eU.findLastSymbolChar( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( i );
				failResults.add( result );
			}
		}
		if( failures > 0 ) {
			System.out.println("testFindLastSymbolCharSuccess failed " + failures + " times on cases:");
			System.out.println( failCases );
			System.out.println( failResults );
		} else {
			System.out.printf( "testFindLastSymbolCharSuccess passed with no errors%n%n");
		}
	}

	public static void testFindLastSymbolCharFailures() {
		String[] testStrings = {"PI*radius*radius+may9*10",
				"PI*radius*radius+may9*10",
				"",
		};
		int[] testCases = {-1, 2, 0,};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			try {
				eU.findLastSymbolChar(  testCases[i] );
				++failures;
				failCases.add( i );
			} catch( IllegalArgumentException ex) {}
			catch( ArrayIndexOutOfBoundsException x ) {}
		}

		if( failures > 0 ) {
			System.out.println("testFindLastSymbolCharFailures failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testFindLastSymbolCharFailures passed with no errors%n%n");
		}
	}

	public static void testFindLastDecimalDigitSuccess() {
		String[] testStrings = {"3.19",
											  "3.1+4",
											  "3.1+4",
											  "12+3*33.4*pow(2,4)",
											  "12+3*33.4*pow(2,4)",
											  "12+3*33.4*pow(2,4)",
											  "0x123",
											  "0B101",
											  "pow(2,4)",
											  "0.1.2",
											  "0.1.2",
											  "0.1.2",
											};
		int[] testCases = {1, 0, 4, 0, 3, 5, 0, 0, 4, 0, 1, 2 };
		int[] testResults = { 3, 2, 4, 1, 3, 8, 0, 0, 4, 2, 2, 4 };

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();
		List<Integer> failResults = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			int result = eU.findLastDecimalDigit( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( i );
				failResults.add( result );
			}
		}
		if( failures > 0 ) {
			System.out.println("testFindLastDecimalDigitSuccess failed " + failures + " times on cases:");
			System.out.println( failCases );
			System.out.println( failResults );
		} else {
			System.out.printf( "testFindLastDecimalDigitSuccess passed with no errors%n%n");
		}
	}

	public static void testFindLastDecimalDigitFailures() {
		String[] testStrings = {"12+3*33.4*pow(2,4)",
											  "12+3*33.4*pow(2,4)",
											  "p",
											  "",
											};
		int[] testCases = {11, 2, 1, 0,};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			try {
				eU.findLastDecimalDigit(  testCases[i] );
				++failures;
				failCases.add( i );
			} catch( IllegalArgumentException ex) {}
			catch( IndexOutOfBoundsException x ) {}
		}

		if( failures > 0 ) {
			System.out.println("testFindLastDecimalDigitFailures failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testFindLastDecimalDigitFailures passed with no errors%n%n");
		}
	}
	
	public static void testFindLastHexDigitSuccess() {
		String[] testCases = {"0x2F*0Xc",
											"0x2F*0Xc",
											};
		int[] testInput = {0, 5, };
		int[] expOut = {3, 7, };

		int failures = 0;
		List<String> failCases = new ArrayList<>();
		List<String> failResult = new ArrayList<>();

		for( int i = 0; i < testCases.length; i++) {
			ExpressionUtils util = new ExpressionUtils( testCases[i] );
			int result = util.findLastHexDigit( testInput[i] );
			if( result != expOut[i] ) {
				++failures;
				failCases.add( testCases[i] );
				failResult.add( Integer.toString( result ) );
			}

		}
		if( failures > 0 ) {
			System.out.println( failures + " failed cases in testFindLastHexDigit");
			System.out.println( "Failure cases: " + failCases );
			System.out.printf( "Actual outputs: " + failResult + "%n%n" );
		} else {
			System.out.printf( "testFindLastHexDigit passed with no errors%n%n");
		}
	}
	
	public static void testFindLastHexDigitFailures() {
		String[] testStrings = {"0x2F*0Xc",
											  "p",
											  "",
											  "0x2F*0X",
											};
		int[] testCases = {11, 1, 0, 5};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			try {
				eU.findLastHexDigit(  testCases[i] );
				++failures;
				failCases.add( i );
			} catch( IllegalArgumentException ex) {}
			catch( IndexOutOfBoundsException x) {}
		}

		if( failures > 0 ) {
			System.out.println("testFindLastDecimalDigitFailures failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testFindLastDecimalDigitFailures passed with no errors%n%n");
		}
	}
	
	public static void testFindLastBinaryDigitSuccess() {
		String[] testStrings = {"0b11",
											  "0B12"

											};
		int[] testCases = {0, 0};
		int[] testResults = { 3, 2};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();
		List<Integer> failResults = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			int result = eU.findLastBinaryDigit( testCases[i] );
			if( result != testResults[i] ) {
				++failures;
				failCases.add( i );
				failResults.add( result );
			}
		}
		if( failures > 0 ) {
			System.out.println("testFindLastDecimalDigit failed " + failures + " times on cases:");
			System.out.println( failCases );
			System.out.println( failResults );
		} else {
			System.out.printf( "testFindLastDecimalDigit passed with no errors%n%n");
		}
	}
	
	public static void testFindLastBinaryDigitFailures() {
		String[] testStrings = {"0x2F",
											  "",
											  "0b",
											  "0.123:,"
											};
		int[] testCases = {0, 0, 0, 0};

		int failures = 0;
		List<Integer> failCases = new ArrayList<>();

		for( int i = 0; i < testCases.length ; i++ ) {
			ExpressionUtils eU = new ExpressionUtils( testStrings[i] );
			try {
				eU.findLastBinaryDigit(  testCases[i] );
				++failures;
				failCases.add( i );
			} catch( IllegalArgumentException ex) {}
			catch( IndexOutOfBoundsException x) {}

		}

		if( failures > 0 ) {
			System.out.println("testFindLastBinaryDigitFailures failed " + failures + " times on cases:");
			System.out.println( failCases );
		} else {
			System.out.printf( "testFindLastBinaryDigitFailures passed with no errors%n%n");
		}
	}

		public static void testAll() {
			testIsHexChar();
			testIsHexValue();
			testFindLastHexDigit();
		}
		
		public static void testFindLastHexDigit() {
			testFindLastHexDigitSuccess();
			testFindLastHexDigitFailures();
		}

}
