package devices;
//
//  DigitalSwitch.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//



import op.Defaults;

import com.pi4j.io.gpio.PinPullResistance;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.Pin;

import db.LogEvent;


public class DigitalSwitch extends DigitalIO implements GpioPinListenerDigital{
	
	public static int TOGGLE=0;
	public static int PULSE_ON=1;
	public static int PULSE_OFF=2;
	public static int INDEFINITE_ON=3;
	public static int INDEFINITE_OFF=4;

	public int standardType=0;
	public int standardLength=1000;
	
	boolean activated=false;
	Linker linker=null;
	long otime=0;
	
	int onAction=4;
	int offAction=4;
	
	public DigitalSwitch(Pin pinid, String name,  String experimentName, String experimentType, Defaults defaults){
		super(pinid, name, experimentName, experimentType, defaults);
		type=true;
		try{
			myButton = gpio.provisionDigitalInputPin(pinid, PinPullResistance.PULL_DOWN);
			myButton.setPullResistance(PinPullResistance.PULL_UP);
			myButton.setDebounce(100);
			myButton.addListener(this);
		}
		catch(Exception e){}
	}
	
	public void setLinker(Linker linker){
		this.linker=linker;
	}
	
	public void switchTriggered(){
		LogEvent le=new LogEvent(name, "Switch Triggered", experimentName, experimentType, pinid.getName());
		dbc.writeToDatabase(le);
	//	System.out.println("SWITCH TRIGGERED AND REGISTERED");
		if (linker!=null){
			System.out.println("Linker triggered");
			linker.trigger(0);
		}
	} 
	
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		//System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
		long ttime=System.currentTimeMillis();
		//System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState()+" "+ttime+" "+otime);
		//if ((event.getState()==PinState.HIGH)&&(activated)){
		if (activated){
			if (ttime-otime>2000){
				switchTriggered();
			}
			otime=ttime;
		}
	}
	
	public void turnOn(){
		trigger(onAction);
	}
	
	public void turnOff(){
		trigger(offAction);
	}
	
	public void trigger(){
		trigger(standardType);
	}
	
	public void trigger(int a){
		if (a==TOGGLE){
			activated=!activated;
		}
		else if (a==1){
			activated=true;
			MakePause mp=new MakePause(standardLength, 4);
			mp.run();
		}
		else if (a==2){
			activated=false;
			MakePause mp=new MakePause(standardLength, 3);
			mp.run();
		}
		else if (a==3){
			activated=true;
		}
		else if (a==4){
			activated=false;
		}
	}
	
	class MakePause extends Thread{
		
		int t;
		int u;
		
		public MakePause(int t, int u){
			this.t=t;
			this.u=u;
		}

		public void run(){
			try{
				Thread.sleep(t);
				trigger(u);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getProperties(String key){
		standardType=defaults.getIntProperty(key+name+"swstype");
		standardLength=defaults.getIntProperty(key+name+"swslength");
	}
	
	public void setProperties(String key){
		defaults.setIntProperty(key+name+"swstype", standardType);
		defaults.setIntProperty(key+name+"swslength", standardLength);
	}
	
}
