package devices;

import op.Defaults;
import db.DatabaseConnection;

public class ORLinker extends Linker{

	//DigitalIO[] outputs;
	int state=0;
	boolean onOff=true;
	
	public ORLinker(String name, DigitalIO[] outputs, int[] triggerCodes,  Defaults defaults){
		super(name, outputs, triggerCodes, defaults);
		//this.outputs=outputs;
	}
	
	public void setState(int c){
		state=c;
	}
	
	public void trigger(int a){
		System.out.println("OR LINKER TRIGGERED, STATE: "+a);
		if (outputs[state]!=null){
			outputs[state].trigger(triggerCodes[state]);
		}
	}

}
