package devices;

import java.util.Random;

import SoundPlayback.PannedDataLine;
import SoundPlayback.SoundCollection;
import SoundPlayback.SoundConfig;
import op.Defaults;

public class SoundStimulus extends DigitalIO{
	
	Random random;
	public SoundCollection[] sc;
	public String[] scNames;
	public double[] transitions;
	double[] cumtrans;
	int ntrans=0;
	ORLinker orl;
	public int numColls;
	Defaults defaults;
	public SoundConfig sconfig;
	
	public int defsc=0;
	public int pdlid=0;
	public PannedDataLine pdl;
	
	public SoundStimulus(String name, String experimentName, String experimentType, SoundCollection[] sc, double[] transitions, Defaults defaults){
		super(name, experimentName, experimentType, defaults);
		this.random=defaults.getRandom();
		this.sc=sc;
		this.numColls=sc.length;
		this.transitions=transitions;
		this.defaults=defaults;
		this.sconfig=defaults.sc;
		calculateCumulativeTransitions();
	}
	
	public SoundStimulus(String name, String experimentName, String experimentType, int numColls, Defaults defaults){
		super(name, experimentName, experimentType, defaults);
		this.random=defaults.getRandom();
		this.numColls=numColls;
		transitions=new double[numColls];
		this.sc=new SoundCollection[numColls];
		this.defaults=defaults;
		this.sconfig=defaults.sc;
		cumtrans=new double[numColls];
		sc=new SoundCollection[numColls];
	}
	
	public SoundStimulus(String name, String experimentName, String experimentType, String[] scnames, Defaults defaults){
		super(name, experimentName, experimentType, defaults);
		this.random=defaults.getRandom();
		this.numColls=scnames.length;
		this.scNames=scnames;
		transitions=new double[numColls];
		this.sc=new SoundCollection[numColls];
		this.defaults=defaults;
		this.sconfig=defaults.sc;
		cumtrans=new double[numColls];
		sc=new SoundCollection[numColls];
		for (int i=0; i<numColls; i++){
			sc[i]=new SoundCollection(scnames[i], experimentName, defaults);
		}
	}
	
	public void calculateCumulativeTransitions(){
		cumtrans=new double[transitions.length];
		int x=0;
		ntrans=transitions.length-1;
		for (int i=0; i<transitions.length; i++){
			cumtrans[i]=cumtrans[x]+transitions[i];
			x=i;
		}
	}
	
	public void setORLinker(ORLinker orl){
		this.orl=orl;
	}
	
	public void setPDL(int a){
		System.out.println("Setting pdl "+a);
		this.pdlid=a;
		this.pdl=(PannedDataLine)sconfig.sourceLines.get(a);
		if (pdl==null){System.out.println("PDL IS NULL");}
		for (int i=0; i<sc.length; i++){
			//sc[i].pdl=pdl;
			sc[i].setSoundChannel(pdl);
		}
	}
	
	public void trigger(int a){
		System.out.println("Sound Stimulus "+name+" triggered");
		double x=random.nextDouble()*cumtrans[ntrans];
		int ch=0;
		for (int i=0; i<=ntrans; i++){
			if (x<=cumtrans[i]){
				ch=i;
				i=ntrans+1;
			}
		}
		
		sc[ch].playSound();
		if (orl!=null){
			System.out.println("OR LINKER SET TO STATE: "+ch);
			orl.setState(ch);
		}
	}
	
	public void playStim(int a){
		sc[a].playSound();
	}
	
	public void getProperties(String key){	
		int x=defaults.getIntProperty(key+name+"sstimchoices");
		System.out.println("TRANSITIONS CHECKED: "+x+" "+key+" "+name);
		transitions=new double[x];
		sc=new SoundCollection[x];
		
		pdlid=defaults.getIntProperty(key+name+"soundcard", 0);
		if (pdlid>=sconfig.sourceLines.size()){
			pdlid=0;
		}	
		
		
		for (int i=0; i<x; i++){
			transitions[i]=defaults.getDoubleProperty(key+name+"sstimprobs"+i, 10000);
		// THIS WORKS	System.out.println(key+name+"sstimprobs"+i);
			String scname=defaults.getStringProperty(key+name+"scname"+i);
			String scfn=defaults.getStringProperty(key+name+"scfn"+i);
			int sctype=defaults.getIntProperty(key+name+"sctype"+i);
			sc[i]=new SoundCollection(scname, experimentName, experimentType, scfn, defaults, sctype);
		}
		
		setPDL(pdlid);
		calculateCumulativeTransitions();
	}
	
	public String[] getSoundCollection(){
		return scNames;
	}
		
	
	public void setProperties(String key){
		//System.out.println("TRANSITIONS LENGTH "+key+" "+name+" "+transitions.length);
		defaults.setIntProperty(key+name+"sstimchoices", transitions.length);
		defaults.setIntProperty(key+name+"soundcard", pdlid);
		for (int i=0; i<transitions.length; i++){
			defaults.setDoubleProperty(key+name+"sstimprobs"+i, transitions[i], 10000);
			if (sc[i]!=null){
				defaults.setStringProperty(key+name+"scname"+i, sc[i].name);
				if (sc[i].fileLoc!=null){
					defaults.setStringProperty(key+name+"scfn"+i, sc[i].fileLoc);
				}
				defaults.getIntProperty(key+name+"sctype"+i, sc[i].playType);
			}
		}
	}
}
