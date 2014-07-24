//
//  SoundCollection.java
//  Operanter
//
//  Created by Robert Lachlan on 4/2/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class SoundCollection {

	SoundStimulus[] stimuli;
	Random random=new Random(System.currentTimeMillis());
	int numStimuli=0;
	int position=0;
	PannedDataLine pdl;
	File directory;
	
	
	public SoundCollection(String s){
		
		directory=getFile();
		
		File[] stimFiles=directory.listFiles();
		
		LinkedList applist=new LinkedList();
		for (int i=0; i<stimFiles.length; i++){
			if ((stimFiles[i].getName().endsWith("wav"))&&(stimFiles[i].isHidden()==false)){
				applist.add(stimFiles[i]);
			}
		}
		
		numStimuli=applist.size();;
		stimuli=new SoundStimulus[numStimuli];
		if (numStimuli==0){
			System.out.println("ALERT: NO SOUND FILES FOUND IN FOLDER");
		}
		
		for (int i=0; i<numStimuli; i++){
			File st=(File)applist.get(i);
			stimuli[i]=new SoundStimulus(st);
		}
	}
	
	public void setSoundChannel(PannedDataLine pdl){
		this.pdl=pdl;
		if (stimuli!=null){
			for (int i=0; i<stimuli.length; i++){
				System.out.println(i);
				stimuli[i].setUp(pdl);
			}
		}
	}
	
	
	public File getFile(){
		JFileChooser fc=new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		File file=null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file  = fc.getSelectedFile();
		
		}
		return file;
	}
	
	
	public String playRandom(){
		int a=random.nextInt(numStimuli);
		stimuli[a].playSound();
		position=a+1;
		if (position>=numStimuli){position=0;}
		
		return(stimuli[a].name);
	}
	
	public String playNext(){
		
		
		stimuli[position].playSound();
		
		String s=stimuli[position].name;
		
		position++;
		if (position>=numStimuli){position=0;}
		
		return(s);
	}
			
	
}
