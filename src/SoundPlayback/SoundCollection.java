package SoundPlayback;
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

import db.DatabaseConnection;
import db.LogEvent;
import op.Defaults;

public class SoundCollection {

	static int PLAY_RANDOM=0;
	static int PLAY_SEQUENTIAL=1;
	
	public int playType=0;
	
	Sound[] stimuli;
	int numStimuli=0;
	int position=0;
	public PannedDataLine pdl;
	File directory;
	Defaults defaults;
	public String name="unnamed";
	Random random;
	DatabaseConnection dbc;
	public String experimentName;
	public String experimentType;
	public String fileLoc;
	
	public SoundCollection(String name, String experimentName, String experimentType, String fileLoc, Defaults defaults, int playType){
		this.defaults=defaults;
		this.name=name;
		this.fileLoc=fileLoc;
		this.experimentName=experimentName;
		this.experimentType=experimentType;
		this.playType=playType;
		this.random=defaults.getRandom();
		this.dbc=defaults.getDBC();
		System.out.println("SOUND DIR LOC: "+fileLoc);
		//directory=getFile();
		if (fileLoc!=null){
			//directory=new File(fileLoc);
			setUp();
		}
	}
	
	public SoundCollection(String name, String experimentName, Defaults defaults){
		this.defaults=defaults;
		this.name=name;
		this.experimentName=experimentName;
		this.random=defaults.getRandom();
		this.dbc=defaults.getDBC();
	}

	
	public void setUp(){
		directory=new File(fileLoc);
		File[] stimFiles=directory.listFiles();
		System.out.println("LOADING SOUND FILES FROM "+directory.getPath());
		LinkedList<File> applist=new LinkedList<File>();
		for (int i=0; i<stimFiles.length; i++){
			if ((stimFiles[i].getName().endsWith("wav"))&&(stimFiles[i].isHidden()==false)){
				applist.add(stimFiles[i]);
			}
		}
		
		numStimuli=applist.size();;
		stimuli=new Sound[numStimuli];
		if (numStimuli==0){
			System.out.println("ALERT: NO SOUND FILES FOUND IN FOLDER");
		}
		
		for (int i=0; i<numStimuli; i++){
			File st=(File)applist.get(i);
			stimuli[i]=new Sound(st);
		}
	}
	
	public void setSoundChannel(PannedDataLine pdl){
		this.pdl=pdl;
		if (stimuli!=null){
			for (int i=0; i<stimuli.length; i++){
				stimuli[i].setUp(pdl);
			}
		}
	}
	
	/*
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
	*/
	
	public void playSound(){
		if (playType==PLAY_RANDOM){
			playRandom();
		}
		else{
			playNext();
		}
	}
	
	
	public void playRandom(){
		if (numStimuli==0){
			System.out.println("ERROR NO SOUNDS LOADED");
		}
		else{
			int a=random.nextInt(numStimuli);
			stimuli[a].playSound();
			position=a+1;
			if (position>=numStimuli){position=0;}
		
			LogEvent le=new LogEvent(name, stimuli[a].name, experimentName, experimentType, " ");
			dbc.writeToDatabase(le);
		}
	}
	
	public void playNext(){
		
		
		stimuli[position].playSound();
		
		String s=stimuli[position].name;
		
		position++;
		if (position>=numStimuli){position=0;}
		
		LogEvent le=new LogEvent(name, s, experimentName, experimentType, " ");
		dbc.writeToDatabase(le);
	}
			
	
}
