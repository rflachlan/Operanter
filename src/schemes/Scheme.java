package schemes;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import op.Defaults;
import op.OButton;
import devices.*;


public class Scheme {
	public DigitalOutput[] dos;
	public MotorPWMOutput[] mps;
	public DigitalSwitch[] dss;	
	public ANDLinker[] als;
	public PAUSELinker[] pls;
	public ORLinker[] orls;
	public SoundStimulus[] sss;
	String experimentName;
	String type="Scheme";
	Defaults defaults;
	String scList[];
	
	DigitalIO starter;
	int starterCode=0;
	
	EnergeniePiMote defaultPiMote;
	MotorPWMOutput defaultFoodHatch;
	
	Timer timer;
	
	public Scheme(Defaults defaults, String exptName){
		this.defaults=defaults;
		this.defaultPiMote=defaults.getPiMote();
		this.defaultFoodHatch=defaults.getFoodHatch();
		this.experimentName=exptName;
	}
	
	public String getExperimentName(){
		return experimentName;
	}
	
	public void setExperimentName(String s){
		experimentName=s;
	}
	
	public void startRecording(){
	//	System.out.println("Expt started "+starter.getName());
		for (int i=0; i<pls.length; i++){
			pls[i].turnOn();
		}
		for (int i=0; i<dos.length; i++){
			if (dos[i]!=defaultPiMote){
				dos[i].turnOn();
			}
			else{
				//dos[i].trigger(3);
			}
		}
		for (int i=0; i<dss.length; i++){
			dss[i].turnOn();
		}
		for (int i=0; i<mps.length; i++){
			mps[i].turnOn();
		}
		
		starter.trigger(starterCode);
	}
	
	public void stopRecording(){
		for (int i=0; i<pls.length; i++){
			pls[i].turnOff();
		}
		for (int i=0; i<dos.length; i++){
			if (dos[i]!=defaultPiMote){
				dos[i].turnOff();
			}
			else{
				dos[i].trigger(3);
			}
		}
		for (int i=0; i<dss.length; i++){
			dss[i].turnOff();
		}
		for (int i=0; i<mps.length; i++){
			mps[i].turnOff();
			//added wait a minute then try to open again in case first signal was interfered with
			try{
				timer.cancel();
			}
			catch(Exception e){}
			timer=new Timer();
			timer.schedule(new SecondOpen(), 30000);
			}
		
	}
	
	public void unload(){
		for (int i=0; i<dos.length; i++){
			if (dos[i]!=defaultPiMote){
				dos[i].shutdown();
			}
		}
		for (int i=0; i<dss.length; i++){
			dss[i].shutdown();
		}
		
	}
	
	public void loadFromDefaults(){
	//	System.out.println("LOADING: "+experimentName);
		type=defaults.getStringProperty(experimentName+"type");
	//	System.out.println("(expect to see Maeve1) experimentName: " + experimentName);
	//	System.out.println("type getStringProperty: " + defaults.getStringProperty(experimentName+"type"));
		for (int i=0; i<dos.length; i++){
			dos[i].getProperties(experimentName);
		}
		for (int i=0; i<mps.length; i++){
			mps[i].getProperties(experimentName);
		}
		for (int i=0; i<dss.length; i++){
			dss[i].getProperties(experimentName);
		}
		for (int i=0; i<pls.length; i++){
			pls[i].getProperties(experimentName);
		}
		for (int i=0; i<sss.length; i++){
			sss[i].getProperties(experimentName);
		}
		
	}
	
	public void writeToDefaults(){
		defaults.setStringProperty(experimentName+"type", type);
		for (int i=0; i<dos.length; i++){
			dos[i].setProperties(experimentName);
		}
		for (int i=0; i<mps.length; i++){
			mps[i].setProperties(experimentName);
		}
		for (int i=0; i<dss.length; i++){
			dss[i].setProperties(experimentName);
		}
		for (int i=0; i<pls.length; i++){
			pls[i].setProperties(experimentName);
		}
		for (int i=0; i<sss.length; i++){
			sss[i].setProperties(experimentName);
		}	
	}
	
	public String[] getComponentNames(){
		int ssLength = 0;
		for (int i=0; i<sss.length; i++){
			ssLength=sss[i].getSoundCollection().length + ssLength;
		}
		
		String[] names = new String[dos.length+mps.length+dss.length+pls.length+ssLength];

		for (int i=0; i<dos.length; i++){
			names[i]=dos[i].getName();
		}
		for (int i=0; i<mps.length; i++){
			names[i+dos.length]=mps[i].getName();
		}
		for (int i=0; i<dss.length; i++){
			names[i+dos.length+mps.length]=dss[i].getName();
		}
		for (int i=0; i<pls.length; i++){
			names[i+dos.length+mps.length+dss.length]=pls[i].getName();
		}
		for (int i=0; i<sss.length; i++){
			names[i+dos.length+mps.length+dss.length+pls.length]=sss[i].getName();
		}	
		
		for (int i=0; i<sss.length; i++){
			String[] scList = new String[sss[i].getSoundCollection().length];
			for (int j=0; j<sss[i].getSoundCollection().length; j++){
				scList[j]=sss[i].getSoundCollection()[j];
				names[j+dos.length+mps.length+dss.length+pls.length]=scList[j];
			}
			
		}
		return names;
	}
	
class SecondOpen extends TimerTask{
		
		public void run(){
			for (int i=0; i<mps.length; i++){
				mps[i].turnOff();
			}	
		}
	}
	
}
