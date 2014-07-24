//
//  DigitalIO.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


import java.io.OutputStream;
import java.io.IOException;

public class DigitalIO {
	
	
	String name="Light";
	String experimentName=" ";
	byte channel=0;
	byte motorc=(byte)2;
	byte motorb=(byte)6;
	byte motord=(byte)7;
	byte motore=(byte)8;
	byte son=(byte)1;
	byte soff=(byte)0;
	int chamber=0;
	boolean feeder=false;
	
	OutputStream outStream;
	int state=0;
	long t=0;
	
	DatabaseConnection dbc;
	UserInterface ui;
	
	public DigitalIO(int channel, OutputStream outStream){
		
		this.channel=(byte)channel;
		this.outStream=outStream;
		
		
	}
	
	public DigitalIO(int channel, int chamber, OutputStream outStream, String name, String experimentName, DatabaseConnection dbc, UserInterface ui, boolean feeder){
		
		this.channel=(byte)channel;
		this.chamber=chamber;
		this.name=name;
		this.experimentName=experimentName;
		this.ui=ui;
		this.outStream=outStream;
		this.dbc=dbc;
		this.feeder=feeder;
	}
	
	public void openHatch(boolean open){
		
		int millis=4000;
		LogEvent le=new LogEvent(name, "HATCHOPEN"+millis+" ms", experimentName, channel, chamber);
		try{
			byte onTime=(byte)Math.round(millis*0.01);
			if (open){
				outStream.write(motord);
			}
			else{
				outStream.write(motore);
			}
			outStream.write(channel);
			outStream.write(onTime);
			
			
			System.out.println(name+" "+motorc+" "+channel+" "+onTime);			
			
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
		catch (Exception e){
			
			le=new LogEvent(name, " HATCHOPEN "+millis+" ms failed!", experimentName, channel, chamber);
			
			
			e.printStackTrace();
			//System.exit(-1);
		}
		dbc.writeToDatabase(le, ui);
	}
	
	public void openSwitch(int millis){
		LogEvent le=new LogEvent(name, " OPEN "+millis+" ms", experimentName, channel, chamber);
		try{
			byte onTime=(byte)Math.round(millis*0.01);
			outStream.write(motorb);
			outStream.write(channel);
			outStream.write(onTime);
			
			
			System.out.println(name+" "+motorc+" "+channel+" "+onTime);			
			
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
		catch (Exception e){
			
			le=new LogEvent(name, " On "+millis+" ms failed!", experimentName, channel, chamber);
			
			
			e.printStackTrace();
			//System.exit(-1);
		}
		dbc.writeToDatabase(le, ui);
	}
	
	public void switchOn(int millis){
		LogEvent le=new LogEvent(name, " On "+millis+" ms", experimentName, channel, chamber);
		try{
			byte onTime=(byte)Math.round(millis*0.01);
			outStream.write(motorc);
			outStream.write(channel);
			outStream.write(onTime);
			
			
			System.out.println(name+" "+motorc+" "+channel+" "+onTime);			
			
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
		catch (Exception e){
			
			le=new LogEvent(name, " On "+millis+" ms failed!", experimentName, channel, chamber);
			
			
			e.printStackTrace();
			//System.exit(-1);
		}
		dbc.writeToDatabase(le, ui);
		
	}
	
	public void switchOn(){
		LogEvent le=new LogEvent(name, " On", experimentName, channel, chamber);
		try{
			outStream.write(son);
			outStream.write(channel);
			outStream.write((byte)0);
			
			System.out.println("Switch on "+channel+" switch "+0+" "+son);	
			
			state=2;
		}
		catch (Exception e){
			System.out.println("couldn't switch on");
			
			le=new LogEvent(name," On Failed!", experimentName, channel, chamber);
			
			//e.printStackTrace();
			//System.exit(-1);
		}   
		dbc.writeToDatabase(le, ui);
		
	}
	
	
	public void switchOff(){
		LogEvent le=new LogEvent(name, " Off",experimentName, channel, chamber);
		try{
			outStream.write(soff);
			outStream.write(channel);
			outStream.write((byte)0);
			
			
			
			
			state=0;
		}
		catch (Exception e){
			le=new LogEvent(name, " Off Failed!", experimentName, channel, chamber);
			
			//e.printStackTrace();
			//System.exit(-1);
		}    
		dbc.writeToDatabase(le, ui);
		
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
	
	
	public LogEvent[] getLogs(){
		
		long l=0;
		LogEvent[] results=dbc.readFromDatabase(l);
		
		
		return results;
	}
		
		
	
}
