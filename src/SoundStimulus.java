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
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.FloatControl;


public class SoundStimulus {

	String name;
	File file;
	//Clip clip;
	boolean chill=false;
	PannedDataLine pdl;
	
	
	
	byte[] data;
	int length=10;
	SourceDataLine sdl;
	
	public SoundStimulus(File file){
		
		//loadFile(fileName, LR);
		this.file=file;
		name=file.getName();
	}
	
	/*
	
	
	public void loadFile(File file, boolean LR){
				
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(fileName);

			DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			clip = (Clip) AudioSystem.getLine(info);
			//FloatControl.Type.PAN;
			FloatControl panControl=(FloatControl) clip.getControl(FloatControl.Type.PAN);
			if (LR){
				panControl.setValue(-1f);
			}
			else{
				panControl.setValue(1f);
			}
			clip.addLineListener(this);
			clip.open(sound);


		} 
		catch (Exception e){
			e.printStackTrace();
		}
		finally { 

		}
	}
	
	
	*/
	
	public void setUp(PannedDataLine pdl){
		
		this.pdl=pdl;
		try {
			
			if ((sdl!=null)&&(sdl.isOpen())){closeDown();}
			System.out.println("clip opening");
			AudioInputStream sound = AudioSystem.getAudioInputStream(file);
			
			AudioFormat af=sound.getFormat();
			
			long q=sound.getFrameLength();
			int r=af.getFrameSize();
			
			length=(int)q*r;
			
			byte[] datam=new byte[length];
			sound.read(datam); 
			
			
			length*=2;
			data=new byte[length];
			
			int k=0;
			if (pdl.pan>0){
				k=r;
			}
			
			
			for (int i=0; i<q; i++){
				for (int j=0; j<r; j++){
					data[i*2*r+j+k]=datam[i*r+j];
				}
			}
			
			//af.channels=2;
			
			AudioFormat saf=new AudioFormat(af.getEncoding(), af.getSampleRate(), af.getSampleSizeInBits(), 2, af.getFrameSize()*2, af.getFrameRate(), af.isBigEndian());
			
			DataLine.Info info=new DataLine.Info(SourceDataLine.class, saf);
			
			sdl=(SourceDataLine)pdl.mixer.getLine(info);
			sdl.open(saf);
			
			
		} 
		catch (Exception e){
			e.printStackTrace();
		}
		finally { 
			
		}
	}
	
	public void playSound (){
		try{
			if ((sdl==null)||(!sdl.isOpen())){setUp(pdl);}
			
			sdl.start();
			
			Thread play=new Thread(new PlaySound(0, length));
			play.setPriority(Thread.MIN_PRIORITY);
			play.start();
		} 
		catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}
	
	class PlaySound extends Thread{
		
		int start=0;
		int end=0;
		
		public PlaySound(int start, int end){
			this.start=start;
			this.end=end;
		}
		
		public void run(){
			
			try{
				int a=sdl.write(data, start, (end-start));
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("OWWW");
			}
		}
	}
	
	
	
		
	public void closeDown(){
		try{
			sdl.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally { 
			
		}
	}
		
		
		
		
	
	
	
}
