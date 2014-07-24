//
//  LED.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


import java.io.OutputStream;
import java.io.IOException;

public class LED {
	
	
	byte channel=0;
	OutputStream outStream;
	int state=0;
	long t=0;
	
	public LED(byte channel, OutputStream outStream){
		
		this.channel=channel;
		this.outStream=outStream;
	}
	
	public void switchOn(int millis){
		try{
			byte onTime=(byte)Math.round(millis*0.01);
			outStream.write(channel);
			outStream.write(onTime);
			t=System.currentTimeMillis();
			state=1;
			/*
			try{
				this.sleep(millis);
			}
			catch( Exception e){}
			*/
			//outStream.switchOff();
		}
		catch (IOException e){
			System.out.println("LED couldn't switch on");
			e.printStackTrace();
			System.exit(-1);
		}    
		
	}
	
	
	public void switchOn(){
		try{
			outStream.write(channel);
			outStream.write((byte)0);
			state=2;
		}
		catch (IOException e){
			System.out.println("LED couldn't switch o");
			e.printStackTrace();
			System.exit(-1);
		}    
		
	}
	
	
	public void switchOff(){
		try{
			outStream.write((byte)(channel+30));
			state=0;
		}
		catch (IOException e){
			System.out.println("LED couldn't switch off");
			e.printStackTrace();
			System.exit(-1);
		}    
		
	}
	
	public boolean getState(){
		boolean r=false;
		switch(state){
			case 0: r=false; break;
			case 1:
				if (System.currentTimeMillis()>t){
					r=false;
				}
				else{
					r=true;;
				}
				break;
			case 2: r=true; break;
		}
		return r;
	}
	
}
