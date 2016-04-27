package devices;
//
//  DigitalSwitch.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


import op.Defaults;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import db.LogEvent;


public class DigitalOutput extends DigitalIO{
	
	public static int TOGGLE=0;
	public static int PULSE_ON=1;
	public static int PULSE_OFF=2;
	public static int INDEFINITE_ON=3;
	public static int INDEFINITE_OFF=4;
	public static int FLASH=5;
	
	
	public int standardType=0;
	public int alternativeType=0;
	public int standardLength=1000;
	public int altLength=1000;
	public int standardOff=1000;
	public int altOff=1000;
	public int standardFlashReps=10;
	public int altFlashReps=10;
	
	public int onAction=4;
	public int offAction=4;
	
	Linker linker=null;
	
	public DigitalOutput(Pin pinid, String name,  String experimentName, String experimentType, Defaults defaults){
		super(pinid, name, experimentName, experimentType, defaults);
		
		type=false;
		try{
			pin = gpio.provisionDigitalOutputPin(pinid, name, PinState.LOW);
			pin.setShutdownOptions(true, PinState.LOW);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void setStandardType(int x){
		standardType=x;
	}
	
	public void setAlternativeType(int x){
		alternativeType=x;
	}
	
	public void setLength(int length){
		standardLength=length;
	}
	
	public void setLinker(Linker linker){
		this.linker=linker;
	}
	
	public void toggle(){
		try{
			System.out.println(pin.getName()+" "+pinid.getName());
			pin.toggle();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		LogEvent le=new LogEvent(name, "Toggle", experimentName, experimentType, pinid.getName());
		dbc.writeToDatabase(le);
	}
	
	public void pulseOn(int length){	
		if (pin.isHigh()){
			try{
				pin.low();
			}
			catch(Exception e){
			
			}
		}
		else{
			try{
				System.out.println(pin.getName());
				pin.pulse(length, true);
			}
			catch(Exception e){
			
			}
			LogEvent le=new LogEvent(name, "PulseOn", experimentName, experimentType, pinid.getName());
			dbc.writeToDatabase(le);
		}
	}
	
	public void pulseOff(int length){	
		try{
			PinState ps=PinState.getState(false);
			pin.pulse(length, ps);
		}
		catch(Exception e){
		
		}
		LogEvent le=new LogEvent(name, "PulseOn", experimentName, experimentType, pinid.getName());
		dbc.writeToDatabase(le);
	}
	
	public void flash(int lengthOn, int lengthOff, int reps){	
		PinState ps=PinState.getState(false);
		for (int i=0; i<reps; i++){
			pin.pulse(lengthOn, true);
			pin.pulse(lengthOff, ps, true);
		}
		LogEvent le=new LogEvent(name, "Flash", experimentName, experimentType, pinid.getName());
		dbc.writeToDatabase(le);
	}
	
	public void switchOn(){
		pin.high();
		LogEvent le=new LogEvent(name, "SwitchOn", experimentName, experimentType, pinid.getName());
		dbc.writeToDatabase(le);
	}
	
	public void switchOff(){
		pin.low();
		LogEvent le=new LogEvent(name, "SwitchOff", experimentName, experimentType, pinid.getName());
		dbc.writeToDatabase(le);
	}
	
	public void turnOn(){
		trigger(onAction);
	}
	
	public void turnOff(){
		trigger(offAction);
	}
	
	public void trigger(){
		System.out.println("D.O. Triggered: "+standardType+" "+name);
		if (standardType==0){
			toggle();
		}
		else if (standardType==1){
			pulseOn(standardLength);
		}
		else if (standardType==2){
			pulseOff(standardLength);
		}
		else if (standardType==3){
			switchOn();
		}
		else if (standardType==4){
			switchOff();
		}
		else if (standardType==5){
			flash(standardLength, standardOff, standardFlashReps);
		}
		if (linker!=null){
			linker.trigger(0);
		}
	}
	
	public void trigger(int a){
		System.out.println("D.O. Triggered: "+a+" "+name);
		if (a==0){
			toggle();
		}
		else if (a==1){
			pulseOn(standardLength);
		}
		else if (a==2){
			pulseOff(standardLength);
		}
		else if (a==3){
			switchOn();
		}
		else if (a==4){
			switchOff();
		}
		else if (a==5){
			flash(standardLength, standardOff, standardFlashReps);
		}
		if (linker!=null){
			linker.trigger(0);
		}
	}
	//changed all standardType in triggerAlt() method to alternativeType
	public void triggerAlt(){
		System.out.println("D.O. Triggered: "+alternativeType+" "+name);
		if (alternativeType==0){
			toggle();
		}
		else if (alternativeType==1){
			pulseOn(altLength);
		}
		else if (alternativeType==2){
			pulseOff(altLength);
		}
		else if (alternativeType==3){
			switchOn();
		}
		else if (alternativeType==4){
			switchOff();
		}
		else if (alternativeType==5){
			flash(altLength, altOff, altFlashReps);
		}
	}
	
	public void getProperties(String key){
		standardType=defaults.getIntProperty(key+name+"stype");
		alternativeType=defaults.getIntProperty(key+name+"atype");
		standardLength=defaults.getIntProperty(key+name+"slength");
		altLength=defaults.getIntProperty(key+name+"alength");
		standardOff=defaults.getIntProperty(key+name+"soff");
		altOff=defaults.getIntProperty(key+name+"aoff");
		standardFlashReps=defaults.getIntProperty(key+name+"sreps");
		altFlashReps=defaults.getIntProperty(key+name+"areps");
		
	}
	
	public void setProperties(String key){
		defaults.setIntProperty(key+name+"stype", standardType);
		defaults.setIntProperty(key+name+"atype", alternativeType);
		defaults.setIntProperty(key+name+"slength", standardLength);
		defaults.setIntProperty(key+name+"alength", altLength);
		defaults.setIntProperty(key+name+"soff", standardOff);
		defaults.setIntProperty(key+name+"aoff", altOff);
		defaults.setIntProperty(key+name+"sreps", standardFlashReps);
		defaults.setIntProperty(key+name+"areps", altFlashReps);
	}

}
