//
//  SwitchWithLEDRewardPunish.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.io.OutputStream;
import java.io.IOException;

public class SwitchWithLEDRewardPunish extends SwitchWithLED{
	
	DigitalIO rewarder, punisher;
	int punishmentTime=8000;
	int rewardTime=4000;
	
	
	public SwitchWithLEDRewardPunish(int channel, int chamber, OutputStream outStream, Scheme scheme, DigitalIO led, DigitalIO rewarder, DigitalIO punisher, String name, String experimentName, DatabaseConnection dbc, UserInterface ui){
		super(channel, chamber, outStream, led, scheme, name, experimentName, dbc, ui);
		this.rewarder=rewarder;
		this.punisher=punisher;
			
	}
	
	
	public void switchTriggered(){
		System.out.println("Switch triggered "+channel);
		//boolean ledState=LED.getState();
		int p=0;
		if (polarity){
			p=reward();
			
			LogEvent le=new LogEvent(name, "Correct switch choice", experimentName, channel, chamber);
			dbc.writeToDatabase(le);
			
		}
		else{
			p=punish();
			
			LogEvent le=new LogEvent(name, "Incorrect switch choice", experimentName, channel, chamber);
			dbc.writeToDatabase(le);
			
		}
		scheme.choiceMade(p);
	}
	
	public void setRewardTime(int rt){
		rewardTime=rt;
	}
	
	public void setPunishmentTime(int pt){
		punishmentTime=pt;
	}
	
	public int reward(){
		//LED.switchOff();
		rewarder.openSwitch(rewardTime);
		System.out.println("rewarded");
		return rewardTime;
	}
	
	public int punish(){
		//LED.switchOn();
		punisher.switchOn(punishmentTime);
		System.out.println("punished");
		return punishmentTime;
	}
								 

}
