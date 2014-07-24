//
//  Scheme.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.io.OutputStream;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer.*;
import javax.swing.event.*;

import java.awt.event.*;

public class Scheme implements ActionListener {
	
	
	
	public static final int GREEN_STIM_COMMAND=0;
	public static final int RED_STIM_COMMAND=1;
	public static final int GREEN_LED_COMMAND=2;
	public static final int RED_LED_COMMAND=3;
	public static final int FOOD_REWARD_COMMAND=4;
	public static final int OPEN_HATCH_COMMAND=6;
	public static final int CLOSE_HATCH_COMMAND=7;
	public static final int PUNISHMENT_COMMAND=5;
	
	OutputStream out;
	DatabaseConnection dbc;
	UserInterface ui;
	Operanter op;
	
	Defaults defaults;
	DigitalIO rewarder, punisher, greenLED, redLED;
	javax.swing.Timer timer;
	SwitchWithLEDRewardPunish greenSwitch, redSwitch;
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
		
	int pause1=3000;		//pause1 sets how many ms the lights are switched on for
	int pause2=25000;		//pause2 sets how many ms lights are off between trials
	int pause3=5000;		//pause3 is an initial delay after the program is started
	int pause5=60000;		//currently a fudge for when only one light is to be switched on. Should be >> than pause 6
	int pause6=1500;		//pause6 sets the delay between the (beginning of the) stimulus being played and lights coming on
	int timeUnit=100;
	int failCount=6;
	double bothProb=0.0;
	double greenChosen=0.5;
	double autoProb=1.0;
	int rewardTime=2000;
	int punishTime=4000;
	int[] pause={120000, 120000, 120000, 120000, 120000};
	int timeOutCounter=0;
	int timeOutCounter2=0; 
	
	//The following variables set the channel ID's for Arduino to route decisions to.

	
	int rewarderID=0;
	int punisherID=1;
	int greenLEDID=2;
	int redLEDID=3;
	int greenSwitchID=0;
	int redSwitchID=1;
	boolean chamberAllocated=false;
	boolean collection1ready=false;
	boolean collection2ready=false;

	
	public Scheme(OutputStream out, DatabaseConnection dbc, Operanter op){
		this.out=out;
		if (out==null){System.out.println("ALERT: out is null");}
		this.dbc=dbc;
		this.op=op;
		timer=new javax.swing.Timer(timeUnit, this);
		defaults=new Defaults();
	}
	
	public Scheme(OutputStream out, DatabaseConnection dbc, int chamber, Operanter op){
		this.out=out;
		this.dbc=dbc;
		this.chamber=chamber;
		this.op=op;
		setUpChamber(chamber-1);			//-1 because chamber index starts at 1!!
		timer=new javax.swing.Timer(timeUnit, this);
		defaults=new Defaults();
	}
	 
	
	public Scheme(OutputStream out, DatabaseConnection dbc, UserInterface ui, Operanter op){
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
		
			rewarderID=count+4;
			punisherID=count+1;//NOTE: THIS IS FOR THE TWO CABLE CONFIGURATION!
			greenLEDID=count+2;
			redLEDID=count+3;
		
			count=chamber*2;//NOTE: THIS STILL NEEDS TO BE ADJUSTED FOR > 2 CHAMBERS!
		
			redSwitchID=count+1;
			greenSwitchID=count;
			
			chamberAllocated=true;
			makeScheme();
		}
		else{
			punisherID=-1;
			rewarderID=-1;
			greenLEDID=-1;
			redLEDID=-1;
			redSwitchID=-1;
			greenSwitchID=-1;
			
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
		rewarder.outStream=out;
		punisher.outStream=out;
		greenLED.outStream=out;
		redLED.outStream=out;
		greenSwitch.outStream=out;
		redSwitch.outStream=out;
	}
		
	
	public void makeScheme(){
		
		System.out.println("making scheme: "+greenSwitchID+" "+redSwitchID);
		
		rewarder=new DigitalIO(rewarderID, chamber, out, "Food delivered", experimentName, dbc, ui, true);
		punisher=new DigitalIO(punisherID, chamber, out, "Lights out", experimentName, dbc, ui, false);
		greenLED=new DigitalIO(greenLEDID, chamber, out, "Green LED", experimentName, dbc, ui, false);		
		redLED=new DigitalIO(redLEDID, chamber, out, "Red LED", experimentName, dbc, ui, false);
	
		
		greenSwitch=new SwitchWithLEDRewardPunish(greenSwitchID, chamber, out, this, greenLED, rewarder, punisher, "Green switch", experimentName, dbc, ui);
		redSwitch=new SwitchWithLEDRewardPunish(redSwitchID, chamber, out, this, redLED, rewarder, punisher, "Red switch", experimentName, dbc, ui);
		
		greenSwitch.setRewardTime(rewardTime);
		greenSwitch.setPunishmentTime(punishTime);
		redSwitch.setRewardTime(rewardTime);
		redSwitch.setPunishmentTime(punishTime);
		
		op.registerSwitch(greenSwitch);
		op.registerSwitch(redSwitch);
		
		//test();
		//startUp();
		
	}
	
	public void test(){
		
		rewarder.switchOn(2000);
		try{
		Thread.sleep(1000);
		}
		catch(Exception e){}
		punisher.switchOn(2000);
		
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
		greenLED.switchOn(2000);
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		redLED.switchOn(2000);
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
	}
	
	public void startUp(){
		
		timer.setInitialDelay(pause3);
		timer.start();
		
	}

	
	public void choiceMade(int p){
		switchOff();
		waitingOff=true;
		nextOnTime=System.currentTimeMillis()+pause2+p;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (recording){
					
			if (lightDelay){
				if (System.currentTimeMillis()>turnOnGreen){
					turnGreenOn();
				}
				if (System.currentTimeMillis()>turnOnRed){
					turnRedOn();
				}
			}

			if (waitingOff){
				if (System.currentTimeMillis()>nextOnTime){
					selectNext();
				}
			}
			if (!waitingOff){
				if (System.currentTimeMillis()>nextOnTime){
					waitingOff=true;
					switchOff();
					
					
					timeOutCounter++;
					if (timeOutCounter==failCount){
						
						timeOutCounter=0;
						timeOutCounter2++;
						if (timeOutCounter2>=pause.length){
							timeOutCounter2=pause.length-1;
						}
						LogEvent le=new LogEvent("experiment", "Timed out "+pause[timeOutCounter2], experimentName, -1, chamber);
						dbc.writeToDatabase(le, ui);
						nextOnTime=System.currentTimeMillis()+pause[timeOutCounter2];
					}
					else{
						LogEvent le=new LogEvent("experiment", "Timed out "+pause2, experimentName, -1, chamber);
						dbc.writeToDatabase(le, ui);
						nextOnTime=System.currentTimeMillis()+pause2;
					}
				}
			}
		}
	}
	
	
	public void greenChoice(){
		String s=" ";
		String t="playback, both lit";
		try{
			s=collection1.playRandom();
			long p=System.currentTimeMillis();
			turnOnGreen=p+pause6;
			
			if (random.nextDouble()<bothProb){
				turnOnRed=p+pause6;
			}
			else{
				turnOnRed=p+pause5;
				t="playback, only green";
			}
		}
		catch(Exception e){}
		
		LogEvent le=new LogEvent(t, s, experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);	
	}
	
	public void redChoice(){
		String s=" ";
		String t="playback, both lit";
		try{
			s=collection2.playRandom();
			long p=System.currentTimeMillis();
			turnOnRed=p+pause6;
			
			if (random.nextDouble()<bothProb){
				turnOnGreen=p+pause6;
			}
			else{
				turnOnGreen=p+pause5;
				t="playback, only red";
			}
		}
		catch(Exception e){}
		LogEvent le=new LogEvent(t, s, experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);
	}
	
	public void turnGreenOn(){
		greenLED.switchOn();
		if (random.nextDouble()<autoProb){
			greenSwitch.reward();
		}
		lightDelay=false;
		if (polarity){
			greenSwitch.polarity=true;
			redSwitch.polarity=false;
		}
		else{
			greenSwitch.polarity=false;
			redSwitch.polarity=true;
		}
	}
	
	public void turnRedOn(){
		redLED.switchOn();
		if (random.nextDouble()<autoProb){
			redSwitch.reward();
		}
		lightDelay=false;
		if (polarity){
			greenSwitch.polarity=true;
			redSwitch.polarity=false;
		}
		else{
			greenSwitch.polarity=false;
			redSwitch.polarity=true;
		}
	}
		
	
	public void switchOff(){
		greenLED.switchOff();
		redLED.switchOff();
		greenSwitch.polarity=false;
		redSwitch.polarity=false;
	}
	
	
	public void selectNext(){
		System.out.println("choosing next");
		nextOnTime=System.currentTimeMillis()+pause1+pause6;

		if (random.nextDouble()<greenChosen){
			greenChoice();
			polarity=true;
		}
		else{
			redChoice();
			polarity=false;
		}
		waitingOff=false;
		lightDelay=true;
	}
	
	
	
	public void startRecording(){
		startUp();
		greenSwitch.switchOn();
		redSwitch.switchOn();
		recording=true;
		
		LogEvent le=new LogEvent("experiment", "started", experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);
	}
	
	public void stopRecording(){
		timer.stop();
		greenLED.switchOff();
		redLED.switchOff();
		greenSwitch.switchOff();
		redSwitch.switchOff();
		recording=false;
		nextOnTime=System.currentTimeMillis();
		LogEvent le=new LogEvent("experiment", "stopped", experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);
	}
	
	public void manualTrigger(int p){
		
		String s=" ";
		LogEvent le=new LogEvent("manual trigger", s, experimentName, -1, chamber);
		dbc.writeToDatabase(le, ui);
		switch(p){
				
			case GREEN_STIM_COMMAND:
				collection1.playRandom();
				s= "stimulus 1";
				break;
			case RED_STIM_COMMAND:
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
			case FOOD_REWARD_COMMAND:
				rewarder.openSwitch(rewardTime);
				s=  "food reward "+rewardTime;
				break;
			case OPEN_HATCH_COMMAND:
				rewarder.openHatch(true);
				s=  "food reward "+rewardTime;
				break;
			case CLOSE_HATCH_COMMAND:
				rewarder.openHatch(false);
				s=  "food reward "+rewardTime;
				break;
			case PUNISHMENT_COMMAND:
				punisher.switchOn(punishTime);	
				s="punishment "+punishTime;
				break;
		}		
		
		
		
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
