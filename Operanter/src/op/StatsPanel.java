package op;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import javax.swing.plaf.basic.BasicSpinnerUI;

import db.LogEvent;
import db.DatabaseConnection;
import devices.DigitalOutput;
import schemes.Scheme;

public class StatsPanel extends JPanel implements ActionListener{

	Dimension dim=new Dimension(150, 75);

	OButton calc=new OButton("Calculate", dim);
	JLabel calcLabel;
	JSpinner calcTime;
	Operanter op;
	Defaults defaults;
	String[] componentList;
	Scheme scheme;
	DigitalOutput d;
	JLabel[] componentLabels;
	JPanel labels, topPanel;
	DatabaseConnection dbc;
	LogEvent[] le;
	
	Font font=new Font("Sans-Serif", Font.PLAIN, 10);


	public StatsPanel(Defaults defaults, Scheme scheme, DatabaseConnection dbc){
		
		long timeCalcFrom=defaults.getIntProperty("timecalcfrom", 68400000);
		
		this.setPreferredSize(new Dimension(700, 400));
		this.defaults=defaults;
		this.scheme=scheme;
		this.dbc=dbc;
		this.setFont(font);
		
		this.setLayout(new BorderLayout());
		JPanel topPanel=new JPanel(new FlowLayout());
		
		calcLabel = new JLabel("Calc from this far back:");
		topPanel.add(calcLabel);

		calcTime = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(calcTime, "HH:mm:ss");
		timeEditor.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		calcTime.setEditor(timeEditor);
		calcTime.setValue(new Date(timeCalcFrom));
		setJSpinnerButtonSize(calcTime);
		topPanel.add(calcTime);

		calc.addActionListener(this);
		topPanel.add(calc);
		
		this.add(topPanel, BorderLayout.PAGE_START);
		
		createComponentLabels();
		changeFont(font, this);
		



	}
	
	public void createComponentLabels(){
		JPanel labels = new JPanel();
		labels.setLayout(new GridLayout(0,1));
		String[] componentList = new String[scheme.getComponentNames().length];
		componentList = scheme.getComponentNames();
		JLabel[] componentLabels = new JLabel[componentList.length];
		for (int i=0; i<componentList.length; i++){
			componentLabels[i]=new JLabel(componentList[i]);
			labels.add(componentLabels[i]);
		}
		this.add(labels);
	}


	public void calculateStats(long timeCalcFrom){
		//TODO Figure out how to read through a LogEvent array to "find" certain things.
		//At least not getting any errors...
		
		Calendar cal = Calendar.getInstance();
		long d = cal.getTimeInMillis();
		timeCalcFrom= d - timeCalcFrom;

		le = defaults.dbc.readFromDatabase(timeCalcFrom);
		
		String[] componentList = new String[scheme.getComponentNames().length];
		for (int i=0; i<scheme.getComponentNames().length; i++);{
			//TODO Compare the names to the actionName in LogEvent array. AAAAAAAAAAH. 
		}

	}



	public void actionPerformed(ActionEvent e) {
		Object object=e.getSource();
		if (object.equals(calc)){
			Date d = (Date)calcTime.getValue();
			long timeCalcFrom=d.getTime();
			System.out.println("calcTime: "+timeCalcFrom);
			defaults.setIntProperty("timecalcfrom", (int)timeCalcFrom);

			defaults.writeProperties();

			calculateStats(timeCalcFrom);
		}



	}


	public void setJSpinnerButtonSize(JSpinner spinner){
		spinner.setPreferredSize(new Dimension(150, 75));

		spinner.setUI(new BasicSpinnerUI(){
			protected Component createPreviousButton(){
				Component b=super.createPreviousButton();
				JPanel wrap=new JPanel(new BorderLayout());
				wrap.add(b);
				wrap.setPreferredSize(new Dimension(50, 50));
				return wrap;
			}
		});
	}
	
	public static void changeFont (Font font, JPanel pane){
		
	    for (Component child : pane.getComponents()){
	            child.setFont(font);
	    }
	}

}


