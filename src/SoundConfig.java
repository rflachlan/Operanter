//
//  SoundConfig.java
//  Operanter
//
//  Created by Robert Lachlan on 4/23/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


//goals for this class:
// produce an array/list of lines, representing the sound configuration for each chamber in the experiment.
// present the sound outputs of the computer as however many mono's, and use PAN control to route mono signals to stereo outs
// provide a UI for people to match chambers to outs: three drop-down lists (with drop-down models...): sound-card, channel, L/R....
// ... or two-dd lists: sound-card and channel. This might match up better with how sound cards are labeled!
// be ready to implement other controls, especially volume. 

import javax.sound.sampled.*;
import java.util.*;

public class SoundConfig {
	
	LinkedList sourceLines=new LinkedList();
	LinkedList mixerNames=new LinkedList();
	
	public SoundConfig(){
		getMixerInfo();
	}
		
	private void getMixerInfo(){
		System.out.println("starting sound probe");
		Line.Info sdlInfo = new Line.Info(SourceDataLine.class);
		
		Mixer.Info[] m_aMixerInfos = AudioSystem.getMixerInfo();
		Mixer[] m_aMixers = new Mixer[m_aMixerInfos.length];
		System.out.println(m_aMixers.length+" mixers "+m_aMixerInfos.length+" mixer.infos");
		for (int i=0; i<m_aMixerInfos.length; i++){
			m_aMixers[i] = AudioSystem.getMixer(m_aMixerInfos[i]);
			Line.Info[] inf=m_aMixers[i].getSourceLineInfo();
			Line[] sdls=m_aMixers[i].getSourceLines();
			System.out.println(i+" "+inf.length+" Line.Infos "+sdls.length+" Lines");
			for (int j=0; j<inf.length; j++){
				
				
				if (sdlInfo.matches(inf[j])){
					try{
						Line sdl=m_aMixers[i].getLine(inf[j]);
					
						int channelCount=2;
						
						/*
						Control[] con=sdl.getControls();
						System.out.println(con.length+" controls");
						
						for (int k=0; k<con.length; k++){
							if (con[k].getType().toString()=="Pan"){
								channelCount=2;
								k=con.length;
							}
							if (con[k].getType().toString()=="Balance"){
								channelCount=2;
								k=con.length;
							}
						}
						*/ 
						
						for (int k=0; k<channelCount; k++){
					
							float pan=-1.0f;
							if (k==1){
								pan=1.0f;
							}
					
							PannedDataLine pdl=new PannedDataLine(m_aMixers[i], pan);
					
							sourceLines.add(pdl);
							String s=m_aMixerInfos[i].getName()+" "+(k+1);
							mixerNames.add(s);
						}
					}
					catch(Exception e){}
				}
			}			
		}
	}
	
	public PannedDataLine getPannedDataLine(int a){
		PannedDataLine sdl=(PannedDataLine)sourceLines.get(a);
		return sdl;
	}
	
	
	
	
	
	

}
