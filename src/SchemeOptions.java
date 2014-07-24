//
//  SchemeOptions.java
//  Operanter
//
//  Created by Robert Lachlan on 5/4/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.text.*;
import java.io.*;
import java.util.*;
import javax.swing.text.*;

public class SchemeOptions extends JPanel implements ActionListener{
	
	Scheme scheme;
	SchemeSimple simpleScheme;
	NumberFormat nf, nfi;
	
	JLabel ledOnL=new JLabel("Time (s) that leds are on for");
	JLabel betweenTrialsL=new JLabel("Time (s) between trials");
	JLabel startUpDelayL=new JLabel("Delay (s) between starting experiment and first trial");
	JLabel startUpDelayL2=new JLabel("Delay (s) between starting experiment and first trial");
	JLabel timeOutsL=new JLabel("Time (s) for time-outs");
	JLabel lightDelayL=new JLabel("Delay (s) between stimulus playing and led's switching on"); 
	JLabel betweenPrefDelayL=new JLabel("Minimum delay (s) between triggering successive playbacks");
	JLabel rewardTL=new JLabel("Time (s) that reward hatch opens for (reward)");
	JLabel punishTL=new JLabel("Time (s) that lights go off for (punishment)");
	
	JLabel failCountL=new JLabel("Number of ignored trials before time-out");
	JLabel bothProbL=new JLabel("Probability that both leds light");
	JLabel greenProbL=new JLabel("Probability that green stimulus is chosen");
	JLabel automProbL=new JLabel("Probability that food hatch opens automatically");
	
	JFormattedTextField ledOn, betweenTrials, startUpDelay, lightDelay, failCount, bothProb, greenProb, automProb, rewardT, punishT, startUpDelay2, betweenPrefDelay;
	JFormattedTextField[] timeOuts;
	
	
	String[] experimentTypes={"Reward/Punish", "Preference test"};
	int experimentType=0;
	JComboBox eType=new JComboBox(experimentTypes);
	
	JPanel rpPanel=new JPanel();
	JPanel prefPanel=new JPanel();
	JPanel typePanel=new JPanel();
	
	public SchemeOptions (Scheme scheme, SchemeSimple simpleScheme){
		this.scheme=scheme;
		this.simpleScheme=simpleScheme;
		
		nf=NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);
		
		nfi=NumberFormat.getInstance();
		nfi.setMaximumFractionDigits(0);
		nfi.setMinimumFractionDigits(0);
		
		this.setLayout(new BorderLayout());
		
		
		
		makePanel();
		
		
		
	}
	
	public void makePanel(){
		makeTypePanel();
		makeRPExpPanel();
		makePrefPanel();
		this.add(typePanel, BorderLayout.NORTH);
		this.add(rpPanel, BorderLayout.CENTER);
	}
	
	public void makeTypePanel(){
		
		typePanel.setLayout(new BorderLayout());
		
		JLabel desc=new JLabel("Experiment type: ");
		
		eType.addActionListener(this);
		
		typePanel.add(desc, BorderLayout.WEST);
		typePanel.add(eType, BorderLayout.CENTER);
	}
	
	public void makeRPExpPanel(){
		rpPanel.setLayout(new GridLayout(11,2));
		
		rpPanel.add(ledOnL);
		ledOn=new JFormattedTextField(nf);
		ledOn.setValue(new Double(0.001*scheme.pause1));
		rpPanel.add(ledOn);
		rpPanel.add(betweenTrialsL);
		betweenTrials=new JFormattedTextField(nf);
		betweenTrials.setValue(new Double(0.001*scheme.pause2));
		rpPanel.add(betweenTrials);
		rpPanel.add(startUpDelayL);
		startUpDelay=new JFormattedTextField(nf);
		startUpDelay.setValue(new Double(0.001*scheme.pause3));
		rpPanel.add(startUpDelay);
		rpPanel.add(timeOutsL);
		JPanel timeOutsPanel=new JPanel();
		
		int q=scheme.pause.length;
		
		timeOutsPanel.setLayout(new GridLayout(1, q));
		timeOuts=new JFormattedTextField[q];
		
		for (int i=0; i<q; i++){
			timeOuts[i]=new JFormattedTextField(nf);
			timeOuts[i].setValue(new Double(0.001*scheme.pause[i]));
			timeOutsPanel.add(timeOuts[i]);
		}
		rpPanel.add(timeOutsPanel);

		rpPanel.add(rewardTL);
		rewardT=new JFormattedTextField(nf);
		rewardT.setValue(new Double(0.001*scheme.rewardTime));
		rpPanel.add(rewardT);
		
		rpPanel.add(punishTL);
		punishT=new JFormattedTextField(nf);
		punishT.setValue(new Double(0.001*scheme.punishTime));
		rpPanel.add(punishT);
		
		
		rpPanel.add(lightDelayL);
		lightDelay=new JFormattedTextField(nf);
		lightDelay.setValue(new Double(0.001*scheme.pause6));
		rpPanel.add(lightDelay);
		rpPanel.add(failCountL);
		failCount=new JFormattedTextField(nfi);
		failCount.setValue(new Integer(scheme.failCount));
		rpPanel.add(failCount);
		rpPanel.add(bothProbL);
		bothProb=new JFormattedTextField(nf);
		bothProb.setValue(new Double(scheme.bothProb));
		rpPanel.add(bothProb);
		rpPanel.add(greenProbL);
		greenProb=new JFormattedTextField(nf);
		greenProb.setValue(new Double(scheme.greenChosen));
		rpPanel.add(greenProb);
		rpPanel.add(automProbL);
		automProb=new JFormattedTextField(nf);
		automProb.setValue(new Double(scheme.autoProb));
		rpPanel.add(automProb);
	}
	
	public void makePrefPanel(){
		prefPanel.setLayout(new GridLayout(2,2));
		startUpDelay2=new JFormattedTextField(nf);
		startUpDelay2.setValue(new Double(0.001*simpleScheme.pause1));
		betweenPrefDelay=new JFormattedTextField(nf);
		betweenPrefDelay.setValue(new Double(0.001*scheme.pause2));
		prefPanel.add(startUpDelayL2);
		prefPanel.add(startUpDelay2);
		prefPanel.add(betweenPrefDelayL);
		prefPanel.add(betweenPrefDelay);
	
	}
	
	public int getIntFromJFTF(JFormattedTextField tf, int multiplier){
		double q=(double)((Number)tf.getValue()).doubleValue();
		int p=(int)Math.round(q*multiplier);
		return p;
	}
	
	public double getDoubleFromJFTF(JFormattedTextField tf){
		double q=(double)((Number)tf.getValue()).doubleValue();
		return q;
	}
	
	
	public void setParameters(){

		
		scheme.pause1 = getIntFromJFTF(ledOn, 1000);
		scheme.pause2= getIntFromJFTF(betweenTrials, 1000);
		scheme.pause3=getIntFromJFTF(startUpDelay, 1000);
		scheme.pause6=getIntFromJFTF(lightDelay, 1000);
		scheme.failCount=getIntFromJFTF(failCount, 1);
		scheme.bothProb=getDoubleFromJFTF(bothProb);
		scheme.greenChosen=getDoubleFromJFTF(greenProb);
		scheme.autoProb=getDoubleFromJFTF(automProb);
		scheme.rewardTime=getIntFromJFTF(rewardT, 1000);
		scheme.punishTime=getIntFromJFTF(punishT, 1000);
		int q=timeOuts.length;
		
		for (int i=0; i<q; i++){
			scheme.pause[i]=getIntFromJFTF(timeOuts[i], 1000);
		}
		scheme.makeScheme();
		
		simpleScheme.pause1=getIntFromJFTF(startUpDelay2, 1000);
		simpleScheme.pause2=getIntFromJFTF(betweenPrefDelay, 1000);
		
		simpleScheme.makeScheme();
		
	}
		
	public void actionPerformed(ActionEvent e) {
		experimentType=eType.getSelectedIndex();
		if (experimentType==0){
			this.remove(prefPanel);
			this.add(rpPanel, BorderLayout.CENTER);
		}
		if (experimentType==1){
			this.remove(rpPanel);
			this.add(prefPanel, BorderLayout.CENTER);
		}
		this.revalidate();
	}

}
