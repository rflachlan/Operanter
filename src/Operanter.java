//
//  Operanter.java
//  Operanter
//
//  Created by Robert Lachlan on 3/31/10.
//  Copyright (c) 2010 __MyCompanyName__. All rights reserved.
//
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import javax.swing.JOptionPane;


public class Operanter {

	//Scheme scheme;
	
	InputStream in;
	OutputStream out;
	SerialPort serialPort;
	SerialReader serialReader;
	DatabaseConnection dbc=new DatabaseConnection();
	SoundConfig sc=new SoundConfig();
	
	LinkedList schemes=new LinkedList();
	LinkedList simpleSchemes=new LinkedList();
	
	DigitalSwitch[] switchArray=new DigitalSwitch[50];
	Defaults defaults=new Defaults();
	
    public Operanter(){
		super();			
	}
	
	public void setUp(){
		makeConnection();
		dbc.doConnect();
		MainPanel mp=new MainPanel(this);
		
		

	}
	
	void makeConnection(){
		try{
			connect();
		}
		catch (Exception e) {
			String s="Arduino controller not detected!";
			JOptionPane.showMessageDialog(null,s,"Alert!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	void connect () throws Exception{
		String s="tty.usb";
		String os=System.getProperty("os.name");
		if (os.startsWith("Win")){
			s="COM3";
		}
		
		CharSequence cs=s.subSequence(0, s.length());
		
		
		CommPortIdentifier portIdentifier = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		
		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().contains(cs)) {
				portIdentifier = currPortId;
				break;
			}
		}
		if (portIdentifier==null){
			String sw="Arduino controller not detected!";
			JOptionPane.showMessageDialog(null,sw,"Alert!", JOptionPane.ERROR_MESSAGE);
		}
		//CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		else if (portIdentifier.isCurrentlyOwned()){
			String sw="Error: Port is currently in use";
			JOptionPane.showMessageDialog(null,sw,"Alert!", JOptionPane.ERROR_MESSAGE);
		}
		else{
			
			CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
			
			if ( commPort instanceof SerialPort ){
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
				
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();

				try{
					Thread.sleep(1500);
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				serialReader=new SerialReader(in, this);
				serialPort.addEventListener(serialReader);
				serialPort.notifyOnDataAvailable(true);
				
				
				if (out==null){System.out.println("HELP OUT NOT GOT");}
				//System.out.println("set up worked?");
				
				
				/*
				for (int i=0; i<schemes.size(); i++){
					Scheme scheme=(Scheme)schemes.get(i);
					scheme.setConnection(out);
				}
				*/
				
				//UserInterface ui=new UserInterface(this, scheme, dbc);
				//dbc.doWriteText(ui);
				
			}
			else{
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}     
	}
	
	public void registerSwitch(SwitchWithLEDRewardPunish slrw){
		
		switchArray[slrw.channel]=slrw;
	}
	
	public void registerSwitch(DigitalSwitch slrw){
		System.out.println(slrw.channel);
		switchArray[slrw.channel]=slrw;
	}
		
		
	
	public void switchTriggered(int a){
		System.out.println("switch triggered");
		switchArray[a].switchTriggered();
		
		
	}
	
	
	public UserInterface makeNewUI(){
		if (out==null){System.out.println("out is null");}
		Scheme scheme=new Scheme(out, dbc, this);
		SchemeSimple simpleScheme=new SchemeSimple(out, dbc, this);
		//scheme.makeScheme();
		
		
		schemes.add(scheme);
		simpleSchemes.add(simpleScheme);
		UserInterface ui=new UserInterface(this, scheme, simpleScheme, dbc);
		dbc.doWriteText(ui);
		return ui;
	}
	
	public void removeScheme(UserInterface ui){
		schemes.remove(ui.scheme);
		simpleSchemes.remove(ui.simpleScheme);
	}
	
	
	public void checkChamberAllocation(int a){
		
		for (int i=0; i<schemes.size(); i++){
			Scheme scheme=(Scheme)schemes.get(i);
			if (scheme.chamber==a){
				scheme.setUpChamber(0);
			}
		}
	}
				
	
	public void disconnect(){
		
		if (serialPort != null) {
			try {
				out.close();
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			serialPort.close();
		}
	}
	
	public String[] getSoundConfigList(){
		String[] results=new String[sc.mixerNames.size()];
		for (int i=0; i<sc.mixerNames.size(); i++){
			results[i]=(String)sc.mixerNames.get(i);
		}
		
		return results;
	}
		
	
	public static void main (String[] args ){
		
		try{
			
			//(new Operanter()).connect();
			(new Operanter()).setUp();
		}
		catch (Exception e ){
			e.printStackTrace();
		}
	}
}