package main;

import javax.jws.*;
import javax.xml.ws.*;
import ws.*;

public class Main {
	public static void main(String[] args) {
		try {
		
			Endpoint.publish("http://localhost:9000/ws/SOAPService", new SOAPServiceImpl());
			System.out.println("Server is running....!");
			
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
