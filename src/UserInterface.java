//
//  UserInterface.java
//  Operanter
//
//  Created by Robert Lachlan on 4/1/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//


import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.text.*;
import java.io.*;
import java.util.*;
import javax.swing.text.*;


public class UserInterface extends JPanel implements ActionListener{
	
	
	
	private static String SAVE_COMMAND = "save";
	private static String START_STOP_COMMAND = "start stop";
	private static String ADD_SONG_A_COMMAND = "add a";
	private static String ADD_SONG_B_COMMAND = "add b";
	private static String CHAMBER_COMMAND="chamber";
	private static String SOUND_COMMAND="sound";
	private static String FORMAT_COMMAND="format";
	private static String GLED_COMMAND="green led";
	private static String RLED_COMMAND="red led";
	private static String REWARD_COMMAND="reward";
	private static String OPEN_COMMAND="open hatch";
	private static String CLOSE_COMMAND="close hatch";
	private static String PUNISH_COMMAND="punish led";
	private static String GSTIM_COMMAND="green stim";
	private static String RSTIM_COMMAND="red stim";
	
	JFrame f=new JFrame();
	
	
	JPanel stimPanel=new JPanel(new BorderLayout());
		
	JButton start=new JButton("Start recording");
	JButton save=new JButton("Save data");
	
	JButton addA=new JButton("Choose stimuli for button A");
	JButton addB=new JButton("Choose stimuli for button B");
	
	JButton format=new JButton("Format experiment");
	
	JLabel experimentTypeL=new JLabel("Reward/Punish experiment");
	
	JButton greenLED=new JButton("Green LED");
	JButton redLED=new JButton("Red LED");
	JButton reward=new JButton("Reward");
	JButton openHatch=new JButton("Open feeder");
	JButton closeHatch=new JButton("Close feeder");
	JButton punish=new JButton("Punish");
	JButton gstim=new JButton("Stimulus A");
	JButton rstim=new JButton("Stimulus B");
	
	JTextArea output=new JTextArea(10, 75);
	NumberFormat nf;
	Calendar cal=Calendar.getInstance();
	JScrollPane scrollPane;
	
	
	String[] chamber={"1", "2", "3", "4", "5", "6", "7", "8"};
	
	JComboBox chamberBox=new JComboBox(chamber);
	
	JComboBox soundChannel;
	
	JLabel stimA, stimB, soundLabel, chamberLabel;
	
	boolean isStimASetup=false;
	boolean isStimBSetup=false;
	boolean isSoundSetup=false;
	
	int experimentType=0;
	
	Operanter op;
	Scheme scheme;
	SchemeSimple simpleScheme;
	DatabaseConnection dbc;
	boolean started=false;
	
	public UserInterface(Operanter op, Scheme scheme, SchemeSimple simpleScheme, DatabaseConnection dbc){
		
		this.op=op;
		this.scheme=scheme;
		this.simpleScheme=simpleScheme;
		this.dbc=dbc;
		
		scheme.setUI(this);
		simpleScheme.setUI(this);
		
		this.setLayout(new BorderLayout());
		
		output.setTabSize(12);
		
		nf=NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);
				
		//stop.addActionListener(this);
		//stop.setActionCommand(SHUT_DOWN_COMMAND);
		
		save.addActionListener(this);
		save.setActionCommand(SAVE_COMMAND);
		
		start.addActionListener(this);
		start.setActionCommand(START_STOP_COMMAND);
		
		addA.addActionListener(this);
		addA.setActionCommand(ADD_SONG_A_COMMAND);
		
		addB.addActionListener(this);
		addB.setActionCommand(ADD_SONG_B_COMMAND);
		
		chamberBox.setActionCommand(CHAMBER_COMMAND);
		chamberBox.addActionListener(this);
		
		format.setActionCommand(FORMAT_COMMAND);
		format.addActionListener(this);
		
		String[] chans=op.getSoundConfigList();
		soundChannel=new JComboBox(chans);
		soundChannel.setActionCommand(SOUND_COMMAND);
		soundChannel.addActionListener(this);
		
		
		JPanel topPanel=new JPanel(new GridLayout(1,5));
		
		topPanel.add(start);
		topPanel.add(save);
		topPanel.add(format);
		topPanel.add(experimentTypeL);
		//topPanel.add(soundChannel);
		
		JPanel chamberPanel=new JPanel();
		chamberLabel=new JLabel("Chamber number");
		chamberPanel.add(chamberLabel);
		chamberPanel.add(chamberBox);
		
		topPanel.add(chamberPanel);
		
		
		JPanel stimulusPanel=new JPanel(new BorderLayout());
		
		JPanel aPanel=new JPanel();
		aPanel.add(addA);
		stimA=new JLabel("No stimuli chosen yet for A");
		aPanel.add(stimA);
		
		JPanel bPanel=new JPanel();
		bPanel.add(addB);
		stimB=new JLabel("No stimuli chosen yet for B");
		bPanel.add(stimB);
		
		stimulusPanel.add(aPanel, BorderLayout.WEST);
		stimulusPanel.add(bPanel, BorderLayout.EAST);
		
		JPanel soundPanel=new JPanel();
		soundPanel.add(soundChannel);
		soundLabel=new JLabel("Sound not yet configured");
		soundPanel.add(soundLabel);
		
		
		JPanel cPanel=new JPanel(new BorderLayout());
		
		cPanel.add(stimulusPanel, BorderLayout.WEST);
		cPanel.add(soundPanel, BorderLayout.EAST);
		
		JPanel manualPanel=new JPanel(new GridLayout(1,6));
		
		greenLED.setActionCommand(GLED_COMMAND);
		greenLED.addActionListener(this);
		redLED.setActionCommand(RLED_COMMAND);
		redLED.addActionListener(this);
		reward.setActionCommand(REWARD_COMMAND);
		reward.addActionListener(this);
		openHatch.setActionCommand(OPEN_COMMAND);
		openHatch.addActionListener(this);
		closeHatch.setActionCommand(CLOSE_COMMAND);
		closeHatch.addActionListener(this);
		punish.setActionCommand(PUNISH_COMMAND);
		punish.addActionListener(this);
		gstim.setActionCommand(GSTIM_COMMAND);
		gstim.addActionListener(this);
		rstim.setActionCommand(RSTIM_COMMAND);
		rstim.addActionListener(this);
		
		manualPanel.add(greenLED);
		manualPanel.add(redLED);
		manualPanel.add(gstim);
		manualPanel.add(rstim);
		manualPanel.add(reward);
		manualPanel.add(openHatch);
		manualPanel.add(closeHatch);
		manualPanel.add(punish);
		
		cPanel.add(manualPanel, BorderLayout.SOUTH);
		
		output.setLineWrap(true);
		scrollPane=new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
				
		this.add(topPanel, BorderLayout.NORTH);
		this.add(cPanel, BorderLayout.CENTER);
		
		
		this.add(scrollPane, BorderLayout.SOUTH);

	}
	
	public void writeLogToTextArea(LogEvent le, int counter){
		cal.setTimeInMillis(le.time);
		java.util.Date date=cal.getTime();
		DateFormat df=DateFormat.getInstance();
		String s=df.format(date);
		//System.out.println("trying to write stuff on the textarea");
		String t=Integer.toString(counter);
		
		String x="\t";
		
		int sec=cal.get(Calendar.SECOND);
		int msec=cal.get(Calendar.MILLISECOND);
		
		double q=sec+(0.001*msec);
		String y=nf.format(q);
		
		
		output.append(t+"  ");
		output.append(s+x);
		output.append(y+x);
		output.append(le.objectName+x);
		output.append(le.actionName+x);
		output.append(le.experimentName+x);
		String u=Integer.toString(le.chamber);
		output.append(u+x);
		String v=Integer.toString(le.channel);
		output.append(v);
		
		//DefaultEditorKit dek=new DefaultEditorKit();
		//output.append(dek.insertBreakAction);
		output.append("\n");
		
		JViewport vp=scrollPane.getViewport();
		
		Dimension dim1=scrollPane.getSize();
		Dimension dim2=output.getSize();
		Point p=new Point(0, (int)(dim2.getHeight()-dim1.getHeight()));
		
		vp.setViewPosition(p);
		
	}
	
	public boolean shutDown(){
		boolean close=true;
		
		int n = JOptionPane.showConfirmDialog(this,"Do you really want to stop and close down this experiment?","Confirm shut down", JOptionPane.YES_NO_OPTION);
		if (n==0){
			close=true;
			if (started){
				scheme.stopRecording();
			}
			op.disconnect();
		}
		else{
			close=false;
		}
		
		return close;
	}

	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		//if (SHUT_DOWN_COMMAND.equals(command)) {
		//	op.disconnect();
		//	System.exit(2);
		//}
		if (START_STOP_COMMAND.equals(command)){
			if (started){
				start.setText("Start recording");
				if (experimentType==0){
					scheme.stopRecording();
				}
				else{
					simpleScheme.stopRecording();
				}
				save.setEnabled(true);
				format.setEnabled(true);
				started=false;
			}
			
			else{
				start.setText("Stop recording");
				if (experimentType==0){
					scheme.startRecording();
				}
				else{
					simpleScheme.startRecording();
				}
				save.setEnabled(false);
				format.setEnabled(true);
				started=true;
			}
		}
		if(SAVE_COMMAND.equals(command)){
			scheme.saveResults(this);
		}
		if (ADD_SONG_A_COMMAND.equals(command)){
			if (experimentType==0){
				String s=scheme.makeNewCollection(1, "greenStim");
				stimA.setText(s);
			}
			else{
				String s=simpleScheme.makeNewCollection(1, "leftStim");
				stimA.setText(s);
			}
		}
		if (ADD_SONG_B_COMMAND.equals(command)){
			if (experimentType==0){
				String s=scheme.makeNewCollection(2, "redStim");
				stimB.setText(s);
			}
			else{
				String s=simpleScheme.makeNewCollection(2, "rightStim");
				stimB.setText(s);
			}
		}
		if (SOUND_COMMAND.equals(command)){
			int a=soundChannel.getSelectedIndex();
			
			PannedDataLine pdl=op.sc.getPannedDataLine(a);
			
			
			if (experimentType==1){
				System.out.println("here");
				simpleScheme.setSoundChannel(pdl);
			}
			else{
				scheme.setSoundChannel(pdl);
			}
		}
		if (CHAMBER_COMMAND.equals(command)){
			int a=chamberBox.getSelectedIndex();
			System.out.println(a);
			//op.checkChamberAllocation(a);
			scheme.setUpChamber(a);
		}
		if (FORMAT_COMMAND.equals(command)){
			SchemeOptions so=new SchemeOptions(scheme, simpleScheme);
			int co=JOptionPane.showConfirmDialog(this, so, "Format experiment", JOptionPane.OK_CANCEL_OPTION);
			if (co==0){
				so.setParameters();
				if (so.experimentType!=experimentType){
					if (so.experimentType==0){
						scheme.collection1=simpleScheme.collection1;
						scheme.collection2=simpleScheme.collection2;
						scheme.pdl=simpleScheme.pdl;
					}
					else{
						simpleScheme.collection1=scheme.collection1;
						simpleScheme.collection2=scheme.collection2;
						simpleScheme.pdl=scheme.pdl;
					}
				}
				experimentType=so.experimentType;
				if (experimentType==1){
					greenLED.setEnabled(true);
					redLED.setEnabled(true);
					reward.setEnabled(false);
					punish.setEnabled(false);
					openHatch.setEnabled(false);
					closeHatch.setEnabled(false);
					experimentTypeL.setText("Preference experiment");
					simpleScheme.makeScheme();
				}
				else{
					greenLED.setEnabled(true);
					redLED.setEnabled(true);
					reward.setEnabled(true);
					punish.setEnabled(true);
					experimentTypeL.setText("Reward/Punish experiment");
					scheme.makeScheme();
				}	
			}
		}
		if (GLED_COMMAND.equals(command)){
			if (experimentType==0){
				scheme.manualTrigger(scheme.GREEN_LED_COMMAND);
			}
			else{
				simpleScheme.manualTrigger(simpleScheme.GREEN_LED_COMMAND);
			}
		}
		if (RLED_COMMAND.equals(command)){
			if (experimentType==0){
				scheme.manualTrigger(scheme.RED_LED_COMMAND);
			}
			else{
				simpleScheme.manualTrigger(simpleScheme.RED_LED_COMMAND);
			}
		}
		if (GSTIM_COMMAND.equals(command)){
			if (experimentType==0){
				scheme.manualTrigger(scheme.GREEN_STIM_COMMAND);
			}
			else{
				System.out.println("trigger");
				simpleScheme.manualTrigger(simpleScheme.GREEN_STIM_COMMAND);
			}
		}
		if (RSTIM_COMMAND.equals(command)){
			if (experimentType==0){
				scheme.manualTrigger(scheme.RED_STIM_COMMAND);
			}
			else{
				simpleScheme.manualTrigger(simpleScheme.RED_STIM_COMMAND);
			}
		}
		if (REWARD_COMMAND.equals(command)){
			scheme.manualTrigger(scheme.FOOD_REWARD_COMMAND);
		}
		if (OPEN_COMMAND.equals(command)){
			scheme.manualTrigger(scheme.OPEN_HATCH_COMMAND);
		}
		if (CLOSE_COMMAND.equals(command)){
			scheme.manualTrigger(scheme.CLOSE_HATCH_COMMAND);
		}
		if (PUNISH_COMMAND.equals(command)){
			scheme.manualTrigger(scheme.PUNISHMENT_COMMAND);
		}
						
		
		
		
		
	}
		

}
