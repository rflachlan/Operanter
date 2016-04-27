package schemes;


import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import op.Defaults;
import devices.*;

public class FirstSchema extends Scheme {
	
	
	public FirstSchema(Defaults defaults, String exptName){
		super(defaults, exptName);
		
		type="FirstTrainingDay";
		
		dos=new DigitalOutput[2];
		dss=new DigitalSwitch[2];
		mps=new MotorPWMOutput[1];
		pls=new PAUSELinker[1];
		als=new ANDLinker[1];
		sss=new SoundStimulus[0];
		orls=new ORLinker[0];
		
		
		
		Pin pin0=RaspiPin.GPIO_29;
		Pin pin1=RaspiPin.GPIO_28;
		//Pin motorPin=RaspiPin.GPIO_01;
		//Pin[] rewardPins={RaspiPin.GPIO_02, RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_00, RaspiPin.GPIO_05, RaspiPin.GPIO_06};
		//int outputSocket=1;
		
		dos[0]=new DigitalOutput(pin0, "LED 1", experimentName, type, defaults);
		dos[1]=new DigitalOutput(pin1, "LED 2", experimentName, type, defaults);
		//dos[2]=new DigitalOutput(motorPin, "rewarder", experimentName, defaults);
		//dos[2]=new EnergeniePiMote(rewardPins, outputSocket, "punisher", experimentName, defaults);
		//mps[0]=new MotorPWMOutput(motorPin, "rewarder", experimentName, defaults);
		mps[0]=defaultFoodHatch;

		Pin switchPin0=RaspiPin.GPIO_25;
		Pin switchPin1=RaspiPin.GPIO_24;
		
		dss[0]=new DigitalSwitch(switchPin0, "Switch 1", experimentName, type, defaults);
		dss[1]=new DigitalSwitch(switchPin1, "Switch 2", experimentName, type, defaults);
		
		DigitalIO[] dout1={dos[0], dss[0], dos[1], dss[1]};
		int[] tc1={3, 3, 3, 3};
		
		pls[0]=new PAUSELinker("Reset pause", dout1, tc1, defaults);
		

		DigitalIO[] dout2={mps[0], dos[0], dss[0], dos[1], dss[1], pls[0]};
		int[] tc2={2, 4, 4, 4, 4, 0};
		als[0]=new ANDLinker("Switch response", dout2, tc2, defaults);
		dss[0].setLinker(als[0]);
		dss[1].setLinker(als[0]);
	
		dos[0].setStandardType(DigitalOutput.TOGGLE);
		dos[0].setAlternativeType(DigitalOutput.TOGGLE);
		dos[1].setStandardType(DigitalOutput.PULSE_ON);
		dos[1].setLength(10000);
		//dos[0].setStandardType(DigitalOutput.PULSE_ON);
		mps[0].setLength(10000);

		
		//dss[0]
		
		
		starter=pls[0];
		
	
	}
	
}
