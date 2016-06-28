package devices;

import java.util.Timer;
import java.util.TimerTask;

import op.Defaults;

import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

import db.LogEvent;

public class MotorPWMOutput extends DigitalIO {
	
	int off=0;
	int neutral=15;
	int back=22;
	int forward=8;
	public int standardLength=2000; //duration motor takes to open/close
	public int pauseLength=10000;
	int pinnum=1;

	int maxTimeOut=10800000;
	
	Timer timer, timerC, timerD;
	
	public int onAction=1;
	public int offAction=0;
	
	public MotorPWMOutput(Pin pinid, String name,  String experimentName, String experimentType, Defaults defaults){
		super(null, name, experimentName, experimentType, defaults);
		this.pinnum=pinid.getAddress();
		
		try{
			Gpio.wiringPiSetup();
			SoftPwm.softPwmCreate(pinid.getAddress(), 0, 200);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void setLength(int length){
		pauseLength=length;
	}
	
	public void turnOn(){
		trigger(onAction);
	}
	
	public void turnOff(){
		trigger(offAction);
	}

	public void setMaxTimeOut(int a){
		maxTimeOut=a;
		try{
			timerC.cancel();
			timerD.cancel();
		}
		catch(Exception e){}

	}

	
	/* (non-Javadoc)
	 * @see devices.DigitalIO#trigger(int)
	 * @param a integer indicating which action is called
	 */
	public void trigger(int a){
		//OPEN. Cancels securityTask timers.
		if (a==0){
			runMotor(forward, standardLength);
			try{
				timerC.cancel();
				timerD.cancel();
			}
			catch(Exception e){}
			System.out.println("a==0");
			LogEvent le=new LogEvent(name, "Open", experimentName, experimentType, "unknownpin");
			dbc.writeToDatabase(le);
		}
		//CLOSE. Cancels previous securityTask timers and resets securityTaskOpen timer.
		else if (a==1){
			runMotor(back, standardLength);
			LogEvent le=new LogEvent(name, "Close", experimentName, experimentType, "unknownpin");
			dbc.writeToDatabase(le);
			try{
				timerC.cancel();
				timerD.cancel();
			}
			catch(Exception e){}
			timerC=new Timer();
			timerC.schedule(new SecurityTaskOpen(), maxTimeOut);
			System.out.println("a==1");
		}
		//REWARDER. Cancels previous securityTask timers and resets securityTaskOpen timer for 
		//the maxTimeOut (set in SchedulePane) + pauseLength (rewarder open duration) +
		//standardLength*2 (the time it takes for the hopper to physically open and close again).
		else if (a==2){
			runMotor(forward, standardLength, back, pauseLength);
		//	System.out.println("pauseLength is: " + pauseLength);
			LogEvent le=new LogEvent(name, "Rewarder", experimentName, experimentType, "unknownpin");
			dbc.writeToDatabase(le);
			try{
				timerC.cancel();
				timerD.cancel();
			}
			catch(Exception e){}
			timerC=new Timer();
			timerC.schedule(new SecurityTaskOpen(), maxTimeOut + pauseLength + (standardLength*2));
			System.out.println("a==2");
		}
		//OPEN SECURITY TASK. Opens hopper and sets timer for SecurityTaskClose.
		else if (a==3){
			runMotor(forward, standardLength);
			LogEvent le=new LogEvent(name, "SecurityTaskOpen", experimentName, experimentType, "unknownpin");
			dbc.writeToDatabase(le);
			try{
				timerC.cancel();
				timerD.cancel();
			}
			catch(Exception e){}
			timerD=new Timer();
			timerD.schedule(new SecurityTaskClose(), defaults.getIntProperty("opentime"));
			System.out.println("a==3");
		}
		//CLOSE SECURITY TASK. Closes hopper and resets SecurityTaskOpen timer.
		else if (a==4){
			runMotor(back, standardLength);
			LogEvent le=new LogEvent(name, "SecurityTaskClose", experimentName, experimentType, "unknownpin");
			dbc.writeToDatabase(le);
			try{
				timerC.cancel();
				timerD.cancel();
			}
			catch(Exception e){}
			timerC=new Timer();
			timerC.schedule(new SecurityTaskOpen(), maxTimeOut);
			System.out.println("a==4");
		}
	}
	
	public void runMotor(int x, int y){
		timer = new Timer();
		SoftPwm.softPwmWrite(pinnum, x);
		timer.schedule(new FireTask(off), y);
	}
	
	public void runMotor(int x, int y, int z, int w){
		timer = new Timer();
		SoftPwm.softPwmWrite(pinnum, x);
		timer.schedule(new FireTask(off), y);
		timer.schedule(new FireTask(z), y+w);
		timer.schedule(new FireTask(off), 2*y+w);

	}

	
	public void getProperties(String key){
		pauseLength=defaults.getIntProperty(key+name+"mpolength");
	}
	
	public void setProperties(String key){
		defaults.setIntProperty(key+name+"mpolength", pauseLength);
	}
		
	class FireTask extends TimerTask{
		int x;

		public FireTask(int x){
			this.x=x;
		}
			
		public void run(){
			SoftPwm.softPwmWrite(pinnum, x);
		}	
	}

	class SecurityTaskOpen extends TimerTask{
		
		public void run(){
			trigger(3);
		}
	}
	
	class SecurityTaskClose extends TimerTask{
		
		public void run(){
			trigger(4);
		}
	}
	
	
}
//
