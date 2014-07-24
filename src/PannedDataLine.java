//
//  PannedDataLine.java
//  Operanter
//
//  Created by Robert Lachlan on 4/24/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import javax.sound.sampled.Mixer;

public class PannedDataLine{

	float pan=0f;
	Mixer mixer;
	
	public PannedDataLine(Mixer mixer, float pan){
		this.mixer=mixer;
		this.pan=pan;
	}
	
	
}
