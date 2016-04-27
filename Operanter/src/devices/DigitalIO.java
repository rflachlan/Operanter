package devices;
//
//  DigitalIO.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


import op.Defaults;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import db.DatabaseConnection;
import db.LogEvent;

public class DigitalIO {
	
	
	String name="Light";
	String experimentName=" ";
	String experimentType=" ";
	Pin pinid;
	boolean type=false;
	
	GpioController gpio;
	GpioPinDigitalOutput pin;
	GpioPinDigitalInput myButton;
	DatabaseConnection dbc;
	Defaults defaults;
	
	public DigitalIO(String name, Defaults defaults){
		this.name=name;
		this.defaults=defaults;
		this.dbc=defaults.getDBC();
	}
	
	public DigitalIO(String name, String experimentName, String experimentType, Defaults defaults){
		this.name=name;
		this.experimentName=experimentName;
		this.experimentType=experimentType;
		this.defaults=defaults;
		this.dbc=defaults.getDBC();
	}
	
	public DigitalIO(Pin pinid, GpioController gpio, Defaults defaults){
		
		this.pinid=pinid;
		this.gpio=gpio;
		this.defaults=defaults;
		this.dbc=defaults.getDBC();
	}
	
	public DigitalIO(Pin pinid, String name, String experimentName, String experimentType, Defaults defaults){
		
		this.pinid=pinid;
		this.name=name;
		this.experimentName=experimentName;
		this.experimentType=experimentType;
		this.defaults=defaults;
		this.gpio=defaults.getGPIO();
		this.dbc=defaults.getDBC();
		
	}
	
	public void trigger(int a){
		
	}	
	
	public String getName(){
		return name;
	}
	
	public void getProperties(String key){}
	
	public void setProperties(String key){}
	
	public void shutdown(){
		try{
			gpio.unprovisionPin(pin);
		}
		catch(Exception e){}
		try{
			gpio.unprovisionPin(myButton);
		}
		catch(Exception e){}
	}
	
	
}
