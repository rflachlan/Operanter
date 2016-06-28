package schemes;


import op.Defaults;
import devices.*;

public class SoundTester extends Scheme {
	
	
	public SoundTester(Defaults defaults, String exptName){
		super(defaults, exptName);
		
		type="SoundTester";
		
		dos=new DigitalOutput[0];
		dss=new DigitalSwitch[0];
		mps=new MotorPWMOutput[0];
		pls=new PAUSELinker[1];
		als=new ANDLinker[1];
		sss=new SoundStimulus[1];
		orls=new ORLinker[0];
		

		String[] cn={"PROBE", "GO", "NOGO"};
		sss[0]=new SoundStimulus("Sounds", experimentName, type, cn, defaults);
		
		
		DigitalIO[] dout1={sss[0], pls[0]};
		int[] tc1={0,0};
		
		pls[0]=new PAUSELinker("Pause", dout1, tc1, defaults);
		


		starter=pls[0];
		
	
	}
	
}
