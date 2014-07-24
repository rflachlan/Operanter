//
//  SwitchWithLED.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


import java.io.OutputStream;
import java.io.IOException;

public class SwitchWithLED extends DigitalSwitch{

	
	DigitalIO LED;
	Scheme scheme;
	
	public SwitchWithLED(int channel, int chamber, OutputStream outStream, DigitalIO LED, Scheme scheme, String name, String experimentName, DatabaseConnection dbc, UserInterface ui){
		super(channel, chamber, outStream, name, experimentName, dbc, ui);
		this.LED=LED;
		this.scheme=scheme;
	}
	
	
}
