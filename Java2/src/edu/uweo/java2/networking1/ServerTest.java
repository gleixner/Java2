package edu.uweo.java2.networking1;

import java.util.HashMap;
import java.util.Map;

public class ServerTest {

	public static void main( String[] args ) {

		Map<String, String> testInput = new HashMap<>();
		testInput.put("accept-timeout", "100");
		testInput.put("active-clients", "2");
		//		testInput.put("active-connections", "2");
		//		testInput.put("client-timeout", "500");
		testInput.put("port", "57001");
		//		testInput.put("greeting", "This should be different.");

		PlayGroundServer pgs = new PlayGroundServer( testInput );
		System.out.println( pgs.getCurrentSettings() );
		Thread t = new Thread( pgs );
		t.start();
	}

}
