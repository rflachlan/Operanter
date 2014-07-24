//
//  SwitchWithSound.java
//  Operanter
//
//  Created by Robert Lachlan on 7/11/11.
//  Copyright 2011 __MyCompanyName__. All rights reserved.
//

import java.io.OutputStream;
import java.io.IOException;

public class SwitchWithSound extends DigitalSwitch{

	SchemeSimple simpleScheme;
	
	public SwitchWithSound(int channel, int chamber, OutputStream outStream, SchemeSimple simpleScheme, String name, String experimentName, DatabaseConnection dbc, UserInterface ui){
		super(channel, chamber, outStream, name, experimentName, dbc, ui);
		this.simpleScheme=simpleScheme;
		System.out.println("Switch with sound: "+channel+" "+chamber);
		//son=(byte)3;
		//soff=(byte)4;
		//motorc=(byte)5;
		
	}
	
	
	public void switchTriggered(){
		
		simpleScheme.choiceMade(this);
		System.out.println("Here");
		
		
	}
	
	
	
}
