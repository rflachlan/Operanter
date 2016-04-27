package SoundPlayback;
//
//  SoundStimulus.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.FloatControl;


public class Sound {

	String name;
	File file;
	//Clip clip;
	boolean chill=false;
	PannedDataLine pdl;
	
	byte[] data;
	AudioFormat af;
	
	public Sound(File file){
		
		//loadFile(fileName, LR);
		this.file=file;
		name=file.getName();
	}
	
	public void setUp(PannedDataLine pdl){
		
		try {
			this.pdl=pdl;
			System.out.println("Setting up sound");
			AudioInputStream sound = AudioSystem.getAudioInputStream(file);
			af=sound.getFormat();
			
			long q=sound.getFrameLength();
			int r=af.getFrameSize();
			
			int length=(int)q*r;
			
			data=new byte[length];
			sound.read(data); 
			System.out.println("Finished setting up sound "+data.length+" "+af.getChannels());
		} 
		catch (Exception e){
			e.printStackTrace();
		}
		finally { 
			
		}
	}
	
	public void playSound (){
		System.out.println("Sound started");
		pdl.playSound(data, af);
		System.out.println("Sound Played");
	}
	
}
