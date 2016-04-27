package devices;



import op.Defaults;
import db.DatabaseConnection;

public class ANDLinker extends Linker{
	
	//DigitalIO[] outputs;
	
	public ANDLinker(String name, DigitalIO[] outputs, int[] triggerCodes, Defaults defaults){
		super(name, outputs, triggerCodes, defaults);
		//this.outputs=outputs;
	}
	
	public void trigger(int a){
		System.out.println(name+" TRIGGERED");
		for (int i=0; i<outputs.length; i++){
			System.out.println("AND LINKER TRIGGERS: "+outputs[i].name);
			outputs[i].trigger(triggerCodes[i]);
		}
	}

}
