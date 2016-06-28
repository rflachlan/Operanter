package schemes;


import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import op.Defaults;
import devices.*;

public class GoNoGoSchema extends Scheme {
	
	
	public GoNoGoSchema(Defaults defaults, String exptName){
		super(defaults, exptName);
		
		type="GoNoGo";
		
		dos=new DigitalOutput[3];
		mps=new MotorPWMOutput[1];
		dss=new DigitalSwitch[2];
		pls=new PAUSELinker[4];
		als=new ANDLinker[3];
		orls=new ORLinker[1];
		sss=new SoundStimulus[1];
		
		
		Pin pin0=RaspiPin.GPIO_29;
		Pin pin1=RaspiPin.GPIO_28;
		
		dos[0]=new DigitalOutput(pin0, "LED 1", experimentName, type, defaults);
		dos[1]=new DigitalOutput(pin1, "LED 2", experimentName, type, defaults);
		dos[2]=defaultLight;
		
		mps[0]=defaultFoodHatch;

		Pin switchPin0=RaspiPin.GPIO_25;
		Pin switchPin1=RaspiPin.GPIO_24;
		
		dss[0]=new DigitalSwitch(switchPin0, "Switch 1", experimentName, type, defaults);
		dss[1]=new DigitalSwitch(switchPin1, "Switch 2", experimentName, type, defaults);
		
		String[] cn={"GO", "NO-GO", "PROBE"};
		sss[0]=new SoundStimulus("Sounds", experimentName, type, cn, defaults);
		
		//Turns on LED/Switch 1
		DigitalIO[] dout2={dos[0], dss[0], dos[2]}; //added turn lights on
		int[] tc1={3, 3, 3};
		pls[0]=new PAUSELinker("Startup pause", dout2, tc1, defaults);
		
		//Turns on LED/Switch 1
		//Turns off LED/Switch 2
		DigitalIO[] doutn={dos[0], dss[0], dos[1], dss[1]};
		int[] tcn={3,3,4,4};
		pls[1]=new PAUSELinker("Response pause", doutn, tcn, defaults);
		
		//Turns off LED/Switch 1
		//Turns on LED/Switch 2
		//Plays a sound
		//Sets pauselinker[1]
		DigitalIO[] dout1={dos[0], dss[0], dos[1], dss[1], pls[1], sss[0]};
		int[] tc2={4, 4, 3, 3, 0, 0};
		als[0]=new ANDLinker("Switch 1 response", dout1, tc2, defaults);
		dss[0].setLinker(als[0]);
		
		
		DigitalIO[] doutp={dos[0], dss[0]};
		int[] tcp={3, 3};
		
		//This is the list of things to do if the bird gets it correct:
		//Trigger the rewarder (mps)
		//Interrupt the first pause linker
		//Start the new pause linker instead
		//Turn off LED/Switch 1
		
		pls[2]=new PAUSELinker("Correct pause", doutp, tcp, defaults);
		DigitalIO[] doutc={mps[0], pls[1], pls[2], dos[1], dss[1]};
		int[] tcc={2, 1, 0, 4, 4};
		als[1]=new ANDLinker("Correct response", doutc, tcc, defaults);
		
		//This is the list of things to do if the bird gets it wrong:
		//Trigger the punishment (dos[2])
		//Interrupt the first pause linker
		//Start the new pause linker instead
				
		pls[3]=new PAUSELinker("Incorrect pause", doutp, tcp, defaults);
		DigitalIO[] douti={dos[2], pls[1], pls[3], dos[1], dss[1]}; //changed pls2 to 3
		int[] tci={2, 1, 0, 4, 4};
		als[2]=new ANDLinker("Incorrect response", douti, tci, defaults);
		
		//DigitalIO[] dout3={mps[0], dos[2]};
		//int[] tc3={2, 2};
		DigitalIO[] dout3={als[1], als[2]}; //changed als2 and 3 to 1 and 2
		int[] tc3={2, 2};
		orls[0]=new ORLinker("contingent", dout3, tc3, defaults);
		
		//DigitalIO[] dout4={dos[1], dss[1], orls[0]};
		//int[] tc4={4, 4, 0};
		
		//als[1]=new ANDLinker("Switch 2 response", dout4, tc4, defaults);
		//dss[1].setLinker(als[1]);
		
		dss[1].setLinker(orls[0]);
		
		
		dos[0].setStandardType(DigitalOutput.TOGGLE);
		dos[0].setAlternativeType(DigitalOutput.TOGGLE);
		dos[1].setStandardType(DigitalOutput.PULSE_ON);
		dos[1].setLength(10000);
		//dos[0].setStandardType(DigitalOutput.PULSE_ON);
		mps[0].setLength(10000);
		dos[2].setStandardType(DigitalOutput.TOGGLE);
		dos[2].setAlternativeType(DigitalOutput.TOGGLE);
		sss[0].setORLinker(orls[0]);
		
		//dss[0]
		
		
		starter=pls[0];
		
	
	}
	
}


/*package schemes;


import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import op.Defaults;
import devices.*;

public class GoNoGoSchema extends Scheme {
	
	
	public GoNoGoSchema(Defaults defaults, String exptName){
		super(defaults, exptName);
		
		type="GoNoGo";
		
		dos=new DigitalOutput[3];
		mps=new MotorPWMOutput[1];
		dss=new DigitalSwitch[2];
		pls=new PAUSELinker[2];
		als=new ANDLinker[2];
		orls=new ORLinker[1];
		sss=new SoundStimulus[1];
		
		
		Pin pin0=RaspiPin.GPIO_29;
		Pin pin1=RaspiPin.GPIO_28;
		//Pin motorPin=RaspiPin.GPIO_01;
		//Pin[] rewardPins={RaspiPin.GPIO_02, RaspiPin.GPIO_04, RaspiPin.GPIO_03, RaspiPin.GPIO_00, RaspiPin.GPIO_05, RaspiPin.GPIO_06};
		//int outputSocket=1;
		
		dos[0]=new DigitalOutput(pin0, "LED 1", experimentName, type, defaults);
		dos[1]=new DigitalOutput(pin1, "LED 2", experimentName, type, defaults);
		//dos[2]=new DigitalOutput(motorPin, "rewarder", experimentName, defaults);
		//dos[2]=new EnergeniePiMote(rewardPins, outputSocket, "punisher", experimentName, defaults);
		dos[2]=defaultPiMote;
		//mps[0]=new MotorPWMOutput(motorPin, "rewarder", experimentName, defaults);
		
		mps[0]=defaultFoodHatch;

		Pin switchPin0=RaspiPin.GPIO_25;
		Pin switchPin1=RaspiPin.GPIO_24;
		
		dss[0]=new DigitalSwitch(switchPin0, "Switch 1", experimentName, type, defaults);
		dss[1]=new DigitalSwitch(switchPin1, "Switch 2", experimentName, type, defaults);
		
		String[] cn={"GO", "NO-GO", "PROBE"};
		sss[0]=new SoundStimulus("Sounds", experimentName, type, cn, defaults);
		
		
		DigitalIO[] dout2={dos[0], dss[0]};
		int[] tc1={3, 3};
		
		pls[0]=new PAUSELinker("Reset pause", dout2, tc1, defaults);
		
		DigitalIO[] doutn={dos[0], dss[0], dos[1], dss[1]};
		int[] tcn={3,3,4,4};
		pls[1]=new PAUSELinker("Stimulus pause", doutn, tcn, defaults); // if nothing happens this is okay
		
		DigitalIO[] dout1={dos[0], dss[0], dos[1], dss[1], pls[1], sss[0]};
		int[] tc2={4, 4, 3, 3, 0, 0};
		als[0]=new ANDLinker("Switch 1 response", dout1, tc2, defaults);
		dss[0].setLinker(als[0]);
		
		DigitalIO[] dout3={mps[0], dos[2]};
		int[] tc3={2, 2};
		orls[0]=new ORLinker("contingent", dout3, tc3, defaults);
		
		DigitalIO[] dout4={dos[1], dss[1], orls[0]}; //
		int[] tc4={4, 4, 0};
		
		als[1]=new ANDLinker("Switch 2 response", dout4, tc4, defaults);
		dss[1].setLinker(als[1]);	
		
		
		dos[0].setStandardType(DigitalOutput.TOGGLE);
		dos[0].setAlternativeType(DigitalOutput.TOGGLE);
		dos[1].setStandardType(DigitalOutput.PULSE_ON);
		dos[1].setLength(10000);
		//dos[0].setStandardType(DigitalOutput.PULSE_ON);
		mps[0].setLength(10000);
		dos[2].setStandardType(DigitalOutput.TOGGLE);
		dos[2].setAlternativeType(DigitalOutput.TOGGLE);
		sss[0].setORLinker(orls[0]);
		
		//dss[0]
		
		
		starter=pls[0];
		
	
	}
	
}
*/