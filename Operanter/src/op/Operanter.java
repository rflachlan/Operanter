package op;
//
//  Operanter.java
//  Operanter
//
//  Created by Robert Lachlan on 3/31/10.
//  Copyright (c) 2010 __MyCompanyName__. All rights reserved.
//

import SoundPlayback.SoundConfig;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.Pin;

import db.DatabaseConnection;
import devices.EnergeniePiMote;
import devices.MotorPWMOutput;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.ImageIcon;

import schemes.*;


public class Operanter {
	
	DatabaseConnection dbc=new DatabaseConnection();
	SoundConfig sc=new SoundConfig();
	Random random=new Random(System.currentTimeMillis());
	Defaults defaults=new Defaults();
		
	Scheme scheme=null;
	
	Timer onTimer=new Timer();
	Timer offTimer=new Timer();
	Timer startTimer=new Timer();
	Timer stopTimer=new Timer();
	Timer dumpTimer=new Timer();
	
	EnergeniePiMote epm;
	MotorPWMOutput mpo;
	UserInterface ui; 

	
    public void start(){
    	//try {
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		//} catch (ClassNotFoundException | InstantiationException
			//	| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
    	UIManager.put("Spinner.arrowButtonSize", new Dimension(200, 5));
  //  	System.out.println("SETTING UP");
    	setUp();
   // 	System.out.println("SCHEDULING TIMERS");
    	scheduleTimers();
   // 	System.out.println("MAKING UI");
    	ui=makeNewUI();
    	
    	JFrame f=new JFrame("Operanter");
		f.getContentPane().add(ui);
		f.pack();
		f.setVisible(true);
		try{
			Image img = new ImageIcon("/home/comet/Desktop/Operanter/Icon/operanter.png").getImage();
			f.setIconImage(img);
		}
		catch(Exception e){
			System.out.println("Icon not found");
		}
		
		mpo.trigger(0);
	
    }
	
	public void setUp(){
		System.getProperty("java.version");
		System.getProperty("java.runtime.version");
		System.getProperties().list(System.out);
		
		dbc.doConnect(defaults);
		

		try{
			final GpioController gpio = GpioFactory.getInstance();
			defaults.gpio=gpio;
			defaults.virtualMode=false;
			//System.out.println("Here ok");
		}
		catch(Exception e){
			e.printStackTrace();
			defaults.virtualMode=true;
		}
		
		
		
		defaults.getSchemeIDs();
		defaults.random=random;
		defaults.dbc=dbc;
		defaults.sc=sc;
		
		Pin[] pins={RaspiPin.GPIO_02, RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_00, RaspiPin.GPIO_05, RaspiPin.GPIO_06};
		int outputSocket=1;
		epm=new EnergeniePiMote(pins, outputSocket, "lights", " ", " ", defaults);
		
		defaults.setPiMote(epm);
		
		Pin motorPin=RaspiPin.GPIO_01;
		mpo=new MotorPWMOutput(motorPin, "rewarder", " ", " ", defaults);
		defaults.setFoodHatch(mpo);		


		LinkedList<String>schemes=defaults.getSchemeIDs();
		String s=schemes.get(0);
		loadScheme(s);
		System.out.println("CHECK NAME: "+scheme.getExperimentName());
	

	}
	
	public void loadScheme(String s){
		if (scheme!=null){
			scheme.unload();
		}
		String qt=defaults.getStringProperty(s+"type");
		System.out.println("This is qt: " + qt);
		if (qt.equals("GoNoGo")){
			scheme=new GoNoGoSchema(defaults, s);
		}
		else if (qt.equals("FirstTrainingDay")){
		//	System.out.println("THIS IS A FIRST SCHEME");
			scheme=new FirstSchema(defaults, s);
		}
		scheme.setExperimentName(s);
		scheme.loadFromDefaults();
	//	System.out.println("We will load this scheme: " + s);
	}
	
	public void unloadScheme(){
		//GpioPin[] ps;
		Collection<GpioPin> ps2=defaults.gpio.getProvisionedPins();
		GpioPin[] ps=(GpioPin[])ps2.toArray(new GpioPin[ps2.size()]);
		for (int i=0; i<ps.length; i++){
			GpioPin gp=ps[i];
			defaults.gpio.unprovisionPin(gp);
		}
		
	}

	public void updateUI(){
		ui.update();
	}
	
	public void scheduleTimers(){
		try{
			onTimer.cancel();
			offTimer.cancel();
			startTimer.cancel();
			stopTimer.cancel();
			dumpTimer.cancel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			
			onTimer=new Timer();
			offTimer=new Timer();
			startTimer=new Timer();
			stopTimer=new Timer();
			dumpTimer=new Timer();
			long timeOn=defaults.getIntProperty("lightson", 28800000);
			long timeOff=defaults.getIntProperty("lightsoff", 68400000);
			long exptStart=defaults.getIntProperty("exptstart", 28800000);
			long exptStop=defaults.getIntProperty("exptstop", 68400000);
				
			int timeOut=defaults.getIntProperty("timeout", 10800000);
		//	int openTime=defaults.getIntProperty("opentime", 100000);
		//	long dumpTime=defaults.getIntProperty("dumptime", 70000000);
			long numDumps=defaults.getIntProperty("numdumps", 1);

			long dayPeriod=86400000;
			//24 hours in ms: 86400000

			//System.out.println("precalculation timeOn: " + timeOn);
			
			
			mpo.setMaxTimeOut(timeOut);
				
			DateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
			Date date=new Date();
			Date todayWithZeroTime;	
			todayWithZeroTime = formatter.parse(formatter.format(date));
			//DateFormat formatter2=new SimpleDateFormat("ms/ss/mm/hh/dd/MM/yyyy");
			//Date todayNow=formatter2.parse(formatter2.format(date));
			long nt=date.getTime()-todayWithZeroTime.getTime();
			
			//long nt2=todayNow.getTime()-todayWithZeroTime.getTime();
			
			//System.out.println(nt+" "+date.getTime()+" "+todayWithZeroTime.getTime());
			
			
			
			timeOn-=nt;
			if (timeOn<=0){
		//		System.out.println("Turning on later: "+timeOn);
				onTimer.schedule(new OnTask(), dayPeriod+timeOn, dayPeriod);
			}
			else{
		//		System.out.println("Turning on today: "+timeOn);
				onTimer.schedule(new OnTask(), timeOn, dayPeriod);
			}
			
			timeOff-=nt;
			if (timeOff<=0){
		//		System.out.println("Turning off later: "+timeOff);
				offTimer.schedule(new OffTask(), dayPeriod+timeOff, dayPeriod);
			}
			else{
		//		System.out.println("Turning off today: "+timeOff);
				offTimer.schedule(new OffTask(), timeOff, dayPeriod);
			}
			
			exptStart-=nt;
			if (exptStart<=0){
				startTimer.schedule(new StartTask(), dayPeriod+exptStart, dayPeriod);
			}
			else{
				startTimer.schedule(new StartTask(), exptStart, dayPeriod);
			}
			
			exptStop-=nt;
			if (exptStop<=0){
				stopTimer.schedule(new StopTask(), dayPeriod+exptStop, dayPeriod);
			}
			else{
				stopTimer.schedule(new StopTask(), exptStop, dayPeriod);
			}
			//THIS FOR LOOP IS NEW
			for (int i=0; i<numDumps; i++){
				String string=new String(i+"dumptime");
				long dumpTimes=defaults.getIntProperty(string, 70000000);
				dumpTimes-=nt;
				if (dumpTimes<=0){
					dumpTimer.schedule(new DumpTask(), dayPeriod+dumpTimes, dayPeriod);
				}
				else{
					dumpTimer.schedule(new DumpTask(), dumpTimes, dayPeriod);
				System.out.println("dumptime for: " + i + " is "+ dumpTimes);
				}
			//	System.out.println("datadump!");
			}
			
//			dumpTime-=nt;
//			if (dumpTime<=0){
//				dumpTimer.schedule(new DumpTask(), dayPeriod+dumpTime, dayPeriod);
//			}
//			else{
//				dumpTimer.schedule(new DumpTask(), dumpTime, dayPeriod);
//		//	System.out.println("dumpTime: " + dumpTime);
//			}
			
		//	System.out.println("nt=" + nt + " post calculation timeOn=" + timeOn);
		//  System.out.println("nt=" + nt + " post calculation timeOff=" + timeOff);
			
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	Pin motorPin=RaspiPin.GPIO_01;
	//	mpo=new MotorPWMOutput(motorPin,"rewarder", " ", " ", defaults);
	//	mpo.trigger(0);
	}
	
	class OnTask extends TimerTask{
		public void run(){
			switchOn();
		}	
	}
	
	class OffTask extends TimerTask{
		public void run(){
			switchOff();
		}	
	}
	
	class StartTask extends TimerTask{
		public void run(){
			startRecording();
		}	
	}
	
	class StopTask extends TimerTask{
		public void run(){
			stopRecording();
		}	
	}
	
	class DumpTask extends TimerTask{
		public void run(){
			dbc.dumpData();
		System.out.println("Executed op.DumpTask");	
		}
	}
	
	public void switchOn(){
		epm.switchOn();
	}
	
	public void switchOff(){
		epm.switchOff();
	}
	
	
	public void setScheme(Scheme scheme){
		this.scheme=scheme;
	}
	
	public Scheme getScheme(){
		return scheme;
	}
	
	public void startRecording(){
		System.out.println("Starting experiment");
		scheme.startRecording();
	}
	
	public void stopRecording(){
		System.out.println("Stopping experiment");
		scheme.stopRecording();
	}
	
//	public void saveResults(){
//		
//	}
	
	
	
	public UserInterface makeNewUI(){
		UserInterface ui=new UserInterface(this, defaults);
		dbc.doWriteText(ui);
		return ui;
	}
	
	public static void main (String[] args ){
		try{			
			(new Operanter()).start();
		}
		catch (Exception e ){
			e.printStackTrace();
		}
		
		
	}
}