package op;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
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
	Scheme scheme;
	DigitalOutput d;
	JPanel labels, topPanel;
	DatabaseConnection dbc;
	LogEvent[] le;
	JTextArea output=new JTextArea();


	public StatsPanel(Defaults defaults, Scheme scheme, DatabaseConnection dbc){


		this.setPreferredSize(new Dimension(700, 400));
		this.defaults=defaults;
		this.scheme=scheme;
		this.dbc=dbc;
		
		this.setLayout(new BorderLayout());
		JPanel topPanel=new JPanel(new FlowLayout());
		
		calcLabel = new JLabel("Calc from:");
		topPanel.add(calcLabel);

		calcTime = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(calcTime, "yyyy-MM-dd HH:mm:ss");
		timeEditor.getFormat().setTimeZone(TimeZone.getTimeZone("UTC"));
		calcTime.setEditor(timeEditor);
	//	calcTime.setValue(datenow);
		setJSpinnerButtonSize(calcTime);
		topPanel.add(calcTime);

		calc.addActionListener(this);
		topPanel.add(calc);
		
		
		this.add(topPanel, BorderLayout.PAGE_START);
		this.add(output);

	}


	public LogEvent[] getLogEventList(){
		
		Date d = (Date)calcTime.getValue();
		long timeCalcFrom=d.getTime();
	//	System.out.println("calcTime: "+timeCalcFrom);

		//Calendar cal = Calendar.getInstance();
		//long d2 = cal.getTimeInMillis();
		//timeCalcFrom = d2 - timeCalcFrom;
		
		//defaults.setIntProperty("timecalcfrom", (int)timeCalcFrom);
		//defaults.writeProperties();

		le = defaults.dbc.readFromDatabase(timeCalcFrom);
		
		return le;

	}
	
	
	public void countOccurrence(LogEvent[] le){
		String[] objectColumn = new String[le.length];
		
		
		for (int i=0; i<le.length; i++){
			objectColumn[i] = le[i].objectName + " (" + le[i].actionName + ")";
		}
		
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();
		for (String string : objectColumn){
			if (!countMap.containsKey(string)){
				countMap.put(string, 1);
			}
			else{
				Integer count = countMap.get(string);
				count = count + 1;
				countMap.put(string, count);
			}
		}
		output.setText(null);
		printCount(countMap);
	}
	
	public void printCount(HashMap<String, Integer> countMap){
		
		Set<String> keySet = countMap.keySet();
		for (String string : keySet){

			output.append(string + " : " + countMap.get(string) + "\n");
		//	System.out.println(string + " : " + countMap.get(string));

		}

	}
	


	public void actionPerformed(ActionEvent e) {
		Object object=e.getSource();
		if (object.equals(calc)){
			LogEvent[] list = getLogEventList();
			countOccurrence(list);
		}
	}


	public void setJSpinnerButtonSize(JSpinner spinner){
		spinner.setPreferredSize(new Dimension(200, 75));

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
	
}


