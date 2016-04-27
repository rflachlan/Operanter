package devices;

import op.Defaults;
import db.DatabaseConnection;

public class Linker extends DigitalIO{

	DigitalIO[] outputs;
	int[] triggerCodes;
	
	public Linker(String name, DigitalIO[] outputs, int[] triggerCodes, Defaults defaults){
		super(name, defaults);	
		this.outputs=outputs;
		this.triggerCodes=triggerCodes;
	}	
	
	public void trigger(int a){
		
	}
	

}
