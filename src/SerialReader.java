//
//  SerialReader.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;


public class SerialReader implements SerialPortEventListener {
	private InputStream in;
	private int[] buffer = new int[1024];
	Operanter op;
	
	
	public SerialReader ( InputStream in, Operanter op){
		this.in = in;
		this.op=op;
	}
	
	public void serialEvent(SerialPortEvent arg0) {
		int data;
		try{
			int len = 0;
			while ( ( data = in.read()) > -1 ){
				if ( data == '\n' ) {
					break;
				}
				buffer[len++] = data;
				System.out.println("Something read "+len+" "+data);
			}
			//System.out.println("here "+len);
			for (int i=0; i<len; i++){
				//System.out.println(i+" "+buffer[i]);
				if (buffer[i]!=13){
					System.out.println("TEST "+i+" "+buffer[i]);
					op.switchTriggered(buffer[i]-48);
				}
				//System.out.println("R: "+buffer[i]);
			}
			
		}
		catch ( IOException e ){
			e.printStackTrace();
			System.exit(-1);
		}             
	}
}
