package op;
//
//Operanter.java
//Operanter
//
//Created by Robert Lachlan on 3/31/10.
//Copyright (c) 2010 __MyCompanyName__. All rights reserved.
//

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import devices.MotorPWMOutput;


public class SchedulePane extends JPanel implements ActionListener, ChangeListener{
	
	Dimension dim=new Dimension(150, 75);
	
	OButton On=new OButton("Lights On", dim);
	OButton Off=new OButton("Lights Off", dim);
	OButton Start=new OButton("Start", dim);
	OButton Stop=new OButton("Stop", dim);
	OButton setTimers=new OButton("<html><center>" + "Set Timers" + "<br>" + "(Opens Food Hatch)" + "</center></html>", dim);
	OButton setTime=new OButton("Set Sys Time", dim);
	OButton dumpData=new OButton("<html><center>" + "Set Data Dump" + "</center></html>", dim);
	JSpinner lightsOn, lightsOff, eStart, eStop, maxHatchClosed, openDuration;
	JLabel opVersion=new JLabel("1.3.0 Date: 27/6/16");

	long zeroTime=0l;
	Operanter op;
	Defaults defaults;
	MotorPWMOutput mpo;
	
	/**
	 * Initialises the SchedulePane with all the JSpinners and buttons.
	 * NB: Timezone settings are important or else the timers will be off
	 * by an hour in summer.
	 * @param defaults the desired defaults setup
	 * @param op the desired operanter setup
	 */
	public SchedulePane (Defaults defaults, Operanter op) {
		this.setPreferredSize(new Dimension(700, 400));
		this.defaults=defaults;
		this.op=op;
		
		long timeOn=defaults.getIntProperty("lightson", 28800000);
		long timeOff=defaults.getIntProperty("lightsoff", 68400000);
		long exptStart=defaults.getIntProperty("exptstart", 28800000);
		long exptStop=defaults.getIntProperty("exptstop", 68400000);	
		
		long timeOut=defaults.getIntProperty("timeout", 10800000);
		long openTime=defaults.getIntProperty("opentime", 10800000);
		try{
		DateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
		Date date=new Date();
		Date todayWithZeroTime;	
		todayWithZeroTime = formatter.parse(formatter.format(date));
		zeroTime=todayWithZeroTime.getTime();
		//24 hours in ms: 86400000
		
		
		Start.addActionListener(this);
		Stop.addActionListener(this);
		On.addActionListener(this);
		Off.addActionListener(this);
		setTimers.addActionListener(this);
		setTime.addActionListener(this);
		dumpData.addActionListener(this);
		
		setTimers.setEnabled(false);
		
		SpinnerDateModel model = new SpinnerDateModel();
		model.setCalendarField(Calendar.MINUTE);

		lightsOn= new JSpinner();
		lightsOn.setModel(model);

		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(lightsOn, "HH:mm:ss");
		timeEditor.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		lightsOn.setEditor(timeEditor);

		lightsOn.setValue(new Date(timeOn));
		
		lightsOn.addChangeListener(this);
		
		lightsOff = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor2 = new JSpinner.DateEditor(lightsOff, "HH:mm:ss");
		timeEditor2.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		lightsOff.setEditor(timeEditor2);
		lightsOff.setValue(new Date(timeOff));
		lightsOff.addChangeListener(this);
		
		eStart = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor3 = new JSpinner.DateEditor(eStart, "HH:mm:ss");
		timeEditor3.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		eStart.setEditor(timeEditor3);
		eStart.setValue(new Date(exptStart));
		eStart.addChangeListener(this);
		
		eStop = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor4 = new JSpinner.DateEditor(eStop, "HH:mm:ss");
		timeEditor4.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		eStop.setEditor(timeEditor4);
		eStop.setValue(new Date(exptStop));
		eStop.addChangeListener(this);

		maxHatchClosed = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor5 = new JSpinner.DateEditor(maxHatchClosed, "HH:mm:ss");
		timeEditor5.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		maxHatchClosed.setEditor(timeEditor5);
		maxHatchClosed.setValue(new Date(timeOut));
		maxHatchClosed.addChangeListener(this);
		
		openDuration = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor7 = new JSpinner.DateEditor(openDuration, "HH:mm:ss");
		timeEditor7.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		openDuration.setEditor(timeEditor7);
		openDuration.setValue(new Date(openTime));
		openDuration.addChangeListener(this);
		

		
		
		
		setJSpinnerButtonSize(lightsOn);
		setJSpinnerButtonSize(lightsOff);
		setJSpinnerButtonSize(eStart);
		setJSpinnerButtonSize(eStop);
		setJSpinnerButtonSize(maxHatchClosed);
		setJSpinnerButtonSize(openDuration);
		
		JLabel maxHatchLabel=new JLabel("<html>" + "Max Time" + "<br>" + "Hatch Closed"+"</html>", SwingConstants.RIGHT);
		JLabel openDurationLabel=new JLabel("<html>" + "Hatch Open" + "<br>" + "Duration"+"</html>", SwingConstants.RIGHT);
		
		setJLabelSize(maxHatchLabel);
		setJLabelSize(openDurationLabel);

		this.setLayout(new FlowLayout());
		
		this.add(On);
		this.add(lightsOn);
		this.add(Off);
		this.add(lightsOff);
		this.add(Start);
		this.add(eStart);
		this.add(Stop);
		this.add(eStop);
		this.add(maxHatchLabel);
		this.add(maxHatchClosed);
		this.add(openDurationLabel);
		this.add(openDuration);
		this.add(dumpData);
	
		this.add(setTimers);
		this.add(setTime);
		this.add(opVersion);
		}
		catch(Exception e){}
	
	}
	
	/**
	 * Sets the preferred JSpinner size to dimensions selected.
	 * @param spinner the JSpinner you want to edit
	 */
	public void setJSpinnerButtonSize(JSpinner spinner){
		spinner.setPreferredSize(new Dimension(150, 90));
		
		spinner.setUI(new BasicSpinnerUI(){
			protected Component createPreviousButton(){
				Component b=super.createPreviousButton();
				JPanel wrap=new JPanel(new BorderLayout());
				wrap.add(b);
				wrap.setPreferredSize(new Dimension(45, 45));
				return wrap;
			}
		});
	}
		
		

	/**
	 * Sets the JLabel to the dimensions selected.
	 * @param jLabel the JLabel you want to edit
	 */
	public void setJLabelSize(JLabel jLabel){
		jLabel.setPreferredSize(new Dimension(150,100));
	}
	
	
	public void actionPerformed(ActionEvent e) {
		Object object=e.getSource();
		if (object.equals(Start)){
			op.startRecording();	
		}
		else if (object.equals(Stop)){
			op.stopRecording();
		}
		else if (object.equals(On)){ //Manual lights on button
			op.switchOn();
		}
		else if (object.equals(Off)){ //Manual lights off button
			op.switchOff();
		}
		else if (object.equals(setTimers)){ //Set timers button. Also triggers motor
			updateDefaults();
			
			Pin motorPin=RaspiPin.GPIO_01;
			mpo=new MotorPWMOutput(motorPin, "rewarder", " ", " ", defaults);
			mpo.trigger(0);
			
			op.scheduleTimers();
			setTimers.setEnabled(false);
		}
		else if (object.equals(setTime)){ //Set time button. Also updates timers because changing system time changes the reference.
			updateTime();
			updateDefaults();
			op.scheduleTimers();
			setTimers.setEnabled(false);
		}
		
		else if (object.equals(dumpData)){ //Dump data button. Updates timers.
			setDumpData();
			updateDefaults();
			op.scheduleTimers();
			setTimers.setEnabled(false);
		}
	
	}
	
	/**
	 * Updates the system time by sending terminal command.
	 */
	public void updateTime(){
		
		SpinnerDateModel model=new SpinnerDateModel();
		model.setCalendarField(Calendar.MINUTE);
		
		JSpinner spinner=new JSpinner();
		setJSpinnerButtonSize(spinner);
		spinner.setModel(model);
		spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd HH:mm" ));
		
		int n=JOptionPane.showConfirmDialog(this, spinner, "Set Time", JOptionPane.OK_CANCEL_OPTION);
		if (n==JOptionPane.OK_OPTION){
			Date date=(Date)spinner.getValue();
			
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			
			int yy=cal.get(Calendar.YEAR);
			int mo=cal.get(Calendar.MONTH)+1; //Jan=0
			System.out.println("The current month is: " + mo);
			int da=cal.get(Calendar.DAY_OF_MONTH);
			int hh=cal.get(Calendar.HOUR_OF_DAY);
			int mm=cal.get(Calendar.MINUTE);
			System.out.println(System.currentTimeMillis());
			String s="echo debian | sudo -S date -s \""+yy+"-"+mo+"-"+da+" "+hh+":"+mm+"\"";
			try{ 
				Process p=Runtime.getRuntime().exec(new String[]{"bash", "-c", s});
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Updates all defaults set on the Schedule tab.
	 */
	public void updateDefaults(){

		Date d = (Date)lightsOn.getValue();

		long t=d.getTime();

		Calendar c=Calendar.getInstance();
		c.setTime(d); 
		long t2=c.get(Calendar.MILLISECOND);

		defaults.setIntProperty("lightson", (int)t);

		d = (Date)lightsOff.getValue();
		t=d.getTime();
		defaults.setIntProperty("lightsoff", (int)t);

		d = (Date)eStart.getValue();
		t=d.getTime();
		defaults.setIntProperty("exptstart", (int)t);

		d = (Date)eStop.getValue();
		t=d.getTime();
		defaults.setIntProperty("exptstop", (int)t);

		d = (Date)maxHatchClosed.getValue();
		t=d.getTime();
		defaults.setIntProperty("timeout", (int)t);

		d = (Date)openDuration.getValue();
		t=d.getTime();
		defaults.setIntProperty("opentime", (int)t);

		defaults.writeProperties();
	}
	
	/**
	 * Brings up dump data interface, which allows user to select
	 * as many data dump timers as wanted, and then allows them to
	 * set these timers.
	 * TODO: Fix layout so timers can't fall off RPi touchscreen.
	 */
	public void setDumpData(){

		
		SpinnerModel model = new SpinnerNumberModel(1,0,24,1);
		JSpinner numDumps=new JSpinner(model);
		numDumps.setPreferredSize(new Dimension(50,50));
		setJSpinnerButtonSize(numDumps);
		JComponent editor = numDumps.getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		tf.setColumns(3);
		numDumps.setValue(new Integer(defaults.getIntProperty("numdumps")));
		this.add(numDumps);
		
		int n=JOptionPane.showConfirmDialog(this, numDumps, "How many dumps per day?", JOptionPane.OK_CANCEL_OPTION);
		if (n==JOptionPane.OK_OPTION){
			
			Integer dumps = (Integer)numDumps.getValue();
			JSpinner[] dumpDataDate = new JSpinner[dumps];
			JPanel wrap=new JPanel(new BorderLayout());
			
			for (int i=0; i<dumps; i++){
				String string=new String(i+"dumptime");
				long dumpTimes=defaults.getIntProperty(string, 70000000);
				System.out.println(dumps);
				dumpDataDate[i] = new JSpinner(new SpinnerDateModel());
				JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(dumpDataDate[i], "HH:mm:ss");
				timeEditor.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
				dumpDataDate[i].setEditor(timeEditor);
				dumpDataDate[i].setValue(new Date(dumpTimes));
				setJSpinnerButtonSize(dumpDataDate[i]);
				
				wrap.add(dumpDataDate[i]);

			}

			int n2=JOptionPane.showConfirmDialog(this, dumpDataDate, "What times?", JOptionPane.OK_CANCEL_OPTION);
			if (n2==JOptionPane.OK_OPTION){
				
				this.add(wrap);
				for (int i=0; i<dumps; i++){
					Date d = (Date)dumpDataDate[i].getValue();
					long t=d.getTime();
					System.out.println("t is: "+t);

					defaults.setIntProperty(i+"dumptime", (int)t);
					defaults.setIntProperty("numdumps", dumps);
					System.out.println("dumpTimeSetTo: " + t);
					defaults.writeProperties();
				}			
			}
		}
	}
	
	public void stateChanged(ChangeEvent e){
		
		setTimers.setEnabled(true);
		
	}

}
