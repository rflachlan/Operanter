package SoundPlayback;
//
//  PannedDataLine.java
//  Operanter
//
//  Created by Robert Lachlan on 4/24/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class PannedDataLine{

	float pan=0f;
	Mixer mixer;
	SourceDataLine sdl;
	//byte[] data2;
	//PlaySound ps;
	
	public PannedDataLine(Mixer mixer, float pan){
		this.mixer=mixer;
		this.pan=pan;
	}
	
public void setUp(AudioFormat af){
		
		try {
			
			if ((sdl!=null)&&(sdl.isOpen())){closeDown();}
			System.out.println("clip opening ");
			DataLine.Info info=new DataLine.Info(SourceDataLine.class, af);
			sdl=(SourceDataLine)mixer.getLine(info);
			sdl.open(af);	
			//data2=new byte[64];
			//PlaySound2 ps2=new PlaySound2(t, af);
			//ps=new PlaySound(data2, af);
			sdl.start();
			//ps.start();
			
		} 
		catch (Exception e){
			e.printStackTrace();
		}
		finally { 
			
		}
	}

	public void playSound(byte[] data, AudioFormat af){
		if ((sdl==null)||(!sdl.isOpen())){
			System.out.println("AUDIO RESET1");
			setUp(af);
		}
		//if (af!=sdl.getFormat()){
			//System.out.println("AUDIO RESET2");
			//closeDown();
			//setUp(af);
		//}
		PlaySound ps=new PlaySound(data, af);
		ps.setPriority(Thread.MAX_PRIORITY);
		//sdl.start();
		ps.start();
		//ps.playAlt=true;
		//ps.data2=data;
	}
	
	class PlaySound extends Thread{
		byte[] data;
	//byte[] data2;
		AudioFormat format;
		//boolean x=true;
		//boolean playAlt=false;
		
		public PlaySound(byte[] data, AudioFormat format){
			this.data=data;
			this.format=format;
			
		}
		
		public void run(){
			
			try{	
				//while (x){
				//	if (playAlt){
				//		int b=data2.length;
				//		int a=sdl.write(data2, 0, b);
				//	}
				//	else{
				//		int b=data.length;
				//		int a=sdl.write(data, 0, b);
				//	}
				//	playAlt=false;
					//ata=data2;
				//}
				while (!sdl.isOpen()){this.sleep(50);}
				int b=data.length;
				//System.out.println("Sound ready to start");
				int a=sdl.write(data, 0, b);
				//System.out.println("Sound finished");
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("OWWW");
			}
		}
	}
	
	/*
	class PlaySound2 extends Thread{
		byte[] data;
		AudioFormat format;
		
		public PlaySound2(byte[] data, AudioFormat format){
			this.data=data;
			this.format=format;
			
		}
		
		public void run(){
			boolean x=true;
			try{		
				while(x==true){
					int b=data.length;
					int a=sdl.write(data, 0, b);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("OWWW");
			}
		}
	}
	
	*/
	
		
	public void closeDown(){
		try{
			sdl.drain();
			sdl.stop();
			sdl.close();
			sdl=null;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally { 
			
		}
	}	
	
	
}
