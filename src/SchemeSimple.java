//
//  SchemeSimple.java
//  Operanter
//
//  Created by Robert Lachlan on 7/10/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

import java.io.OutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer.*;
import javax.swing.event.*;

import java.awt.event.*;

public class SchemeSimple extends Scheme implements ActionListener {
	
	
	
	public static final int LEFT_STIM_COMMAND=0;
	public static final int RIGHT_STIM_COMMAND=1;
	
	OutputStream out;
	DatabaseConnection dbc;
	UserInterface ui;
	Operanter op;
	
	Defaults defaults;
	SwitchWithSound leftSwitch, rightSwitch;
	DigitalIO greenLED, redLED;
	javax.swing.Timer timer;
	Random random=new Random(System.currentTimeMillis());
	SoundCollection collection1, collection2;
	
	PannedDataLine pdl;
	
	boolean waitingOff=true;
	boolean recording=false;
	boolean lightDelay=false;
	boolean polarity=false;
	
	long nextOnTime=System.currentTimeMillis();
	long turnOnRed=0;
	long turnOnGreen=0;
	
	//VARIABLES
	
	String experimentName="Training 2";	//experimentName is the name given to the Experiment. Use this to differentiate different training stages etc.
	int chamber=1;						//chamber is the identity of the chamber that the experiment is being run in.
	
	int pause1=5000;		//pause3 is an initial delay after the program is started
	int pause2=3000;		//pause6 sets the delay between the (beginning of the) stimulus being played and lights coming on
	
	int timeUnit=100;

	int[] pause={120000, 120000, 120000, 120000, 120000};

	//The following variables set the channel ID's for Arduino to route decisions to.
	
	int leftSwitchID=0;
	int rightSwitchID=1;
	boolean chamberAllocated=false;
	boolean collection1ready=false;
	
	boolean collection2ready=false;
	
	
	public SchemeSimple(OutputStream out, DatabaseConnection dbc, Operanter op){
		super(out, dbc, op);
		this.out=out;
		this.dbc=dbc;
		this.op=op;
		timer=new javax.swing.Timer(timeUnit, this);
		defaults=new Defaults();
	}
	
	public SchemeSimple(OutputStream out, DatabaseConnection dbc, int chamber, Operanter op){
		super(out, dbc, chamber, op);
		this.out=out;
		this.dbc=dbc;
		this.chamber=chamber;
		this.op=op;
		setUpChamber(chamber-1);			//-1 because chamber index starts at 1!!
		timer=new javax.swing.Timer(timeUnit, this);
		defaults=new Defaults();
	}
	
	
	public SchemeSimple(OutputStream out, DatabaseConnection dbc, UserInterface ui, Operanter op){
		super(out, dbc, ui, op);
		this.out=out;
		this.dbc=dbc;
		this.ui=ui;
		this.op=op;
		//setUpChamber(chamber-1);			//-1 because chamber index starts at 1!!
		timer=new javax.swing.Timer(timeUnit, this);
		defaults=new Defaults();
	}
	
	public void setUI(UserInterface ui){
		this.ui=ui;
	}
	
	public void setUpChamber(int p){
		
		chamber=p-1;
		System.out.println(" "+p);
		if (p>=0){
			int count=chamber*8;
			greenLEDID=count+2;
			redLEDID=count+3;
			
			count=chamber*2;//NOTE: THIS STILL NEEDS TO BE ADJUSTED FOR > 2 CHAMBERS!
			
			leftSwitchID=count+1;
			rightSwitchID=count;
			
			chamberAllocated=true;
			makeScheme();
			
		}
		else{
			leftSwitchID=-1;
			rightSwitchID=-1;
			
			chamberAllocated=false;
		}	
	}
	
	public void setSoundChannel(PannedDataLine p){
		pdl=p;
		
		if (collection1!=null){
			collection1.setSoundChannel(pdl);
		}
		if (collection2!=null){
			collection2.setSoundChannel(pdl);
		}
		
	}
	
	public String makeNewCollection(int a, String s){
		String t;
		if (a==1){
			collection1=new SoundCollection(s);
			t=collection1.directory.getName();
			if (pdl!=null){
				collection1.setSoundChannel(pdl);
			}
			collection1ready=true;
		}
		else{
			collection2=new SoundCollection(s);
			t=collection2.directory.getName();
			if (pdl!=null){
				collection2.setSoundChannel(pdl);
			}
			collection1ready=true;
		}	
		return t;
	}
	
	public void setConnection(OutputStream out){
		this.out=out;
		leftSwitch.outStream=out;
		rightSwitch.outStream=out;
		greenLED.outStream=out;
		redLED.outStream=out;
	}
	
	
	public void makeScheme(){
		
		System.out.println("making scheme: "+leftSwitchID+" "+rightSwitchID);
		leftSwitch=new SwitchWithSound(leftSwitchID, chamber, out, this, "Left Switch", experimentName, dbc, ui);		
		rightSwitch=new SwitchWithSound(rightSwitchID, chamber, out, this, "Right Switch", experimentName, dbc, ui);
		
		greenLED=new DigitalIO(greenLEDID, chamber, out, "Green LED", experimentName, dbc, ui, false);		
		redLED=new DigitalIO(redLEDID, chamber, out, "Red LED", experimentName, dbc, ui, false);
	
		
		op.registerSwitch(leftSwitch);
		op.registerSwitch(rightSwitch);
		
		//test();
		//startUp();
		
	}
	
	
	public void startUp(){
		timer.setInitialDelay(pause1);
		timer.start();
		
	}
	
	
	public void actionPerformed(ActionEvent e) {}
	
	public void choiceMade(SwitchWithSound sws){
		if (sws==leftSwitch){leftChoice();}
		else{rightChoice();}
		
	}
		
	public void switchOff(){
		
		leftSwitch.polarity=false;
		rightSwitch.polarity=false;
	}
	
	public void startRecording(){
		startUp();
		leftSwitch.switchOn();
		rightSwitch.switchOn();
		recording=true;
		
		LogEvent le=new LogEvent("experiment", "started", experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);
	}
	
	public void stopRecording(){
		timer.stop();
		leftSwitch.switchOff();
		rightSwitch.switchOff();
		recording=false;
		nextOnTime=System.currentTimeMillis();
		LogEvent le=new LogEvent("experiment", "stopped", experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);
	}
	
	public void leftChoice(){
		String s=collection1.playRandom();
		long p=System.currentTimeMillis();
		String t="playback left";
		LogEvent le=new LogEvent(t, s, experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);	
	}
	
	public void rightChoice(){
		String s=collection2.playRandom();
		long p=System.currentTimeMillis();
		String t="playback right";
		LogEvent le=new LogEvent(t, s, experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);	
	}
	
	
	public void manualTrigger(int p){
		
		String s=" ";
		switch(p){
				
			case LEFT_STIM_COMMAND:
				collection1.playRandom();
				s= "stimulus 1";
				break;
			case RIGHT_STIM_COMMAND:
				collection2.playRandom();
				s= "stimulus 2";
				break;
			case GREEN_LED_COMMAND:
				greenLED.switchOn(pause1);
				s=  "green LED on "+ pause1;
				break;
			case RED_LED_COMMAND:
				redLED.switchOn(pause1);
				s=  "red LED on "+ pause1;
				break;

		}		
		
		
		LogEvent le=new LogEvent("manual trigger", s, experimentName, -2, chamber);
		dbc.writeToDatabase(le, ui);
	}
	
	
	
	/*
	 public void triggerSwitch(int a){
	 
	 System.out.println("Trigger: "+a);
	 
	 timeOutCounter=0;
	 timeOutCounter2=0;
	 nextOnTime=System.currentTimeMillis()+pause2;
	 if ((punishWhenBothLightsOff)||(greenSwitch.polarity==true)||(redSwitch.polarity==true)){
	 if (a==0){
	 greenSwitch.switchTriggered();
	 }
	 else if (a==1){
	 redSwitch.switchTriggered();
	 }
	 }
	 
	 }
	 */
	
	public void saveResults(JPanel ui){
		
		DocumentSave ds=new DocumentSave(ui, defaults);
		
		
		boolean readyToWrite=ds.makeFile();
		if (readyToWrite){
			
			
			
			String[] header={"year", "month", "date", "hour", "minute", "second", "experiment", "object", "action", "channel", "chamber"};
			
			for (int i=0; i<header.length; i++){
				ds.writeString(header[i]);
			}
			ds.writeLine();
			
			
			LogEvent[] le1=dbc.readFromDatabase(0);
			for (int i=0; i<le1.length; i++){
				le1[i].writeDoc(ds);
			}
			
			ds.finishWriting();
		}
	}
	
}
