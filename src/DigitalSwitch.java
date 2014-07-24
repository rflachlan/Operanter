//
//  DigitalSwitch.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.io.OutputStream;
import java.io.IOException;

public class DigitalSwitch extends DigitalIO{
	
	boolean polarity=false;
	
	public DigitalSwitch(int channel, int chamber, OutputStream outStream, String name,  String experimentName, DatabaseConnection dbc, UserInterface ui){
		super(channel, chamber, outStream, name, experimentName, dbc, ui, false);
		son=(byte)4;
		soff=(byte)3;
		motorc=(byte)5;

	}
	
	public void switchTriggered(){
		
		
	} 
	
	
	
	
}
