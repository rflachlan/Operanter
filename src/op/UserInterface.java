package op;



import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import db.LogEvent;
import db.DatabaseConnection;


public class UserInterface extends JPanel{
	
	
	Operanter op;
	Defaults defaults;
	
	SchedulePane sp;
	SchemeOptions so;
	DirectControlPanel dcp;
	LogPane lo;
	JTabbedPane tabPane;
	StatsPanel stp;
	DatabaseConnection dbc;
	
	public UserInterface(Operanter op, Defaults defaults){
		this.op=op;
		this.defaults=defaults;
		
		this.setPreferredSize(new Dimension(750, 450));
		tabPane=new JTabbedPane();
		
		sp=new SchedulePane(defaults, op);
		tabPane.addTab("Schedule", sp);
		
		so =new SchemeOptions(defaults, op);
		tabPane.addTab("Operant Experiment", so);
		
		lo=new LogPane(defaults);
		tabPane.addTab("Log", lo);
		
		dcp=new DirectControlPanel(op.scheme);
		tabPane.addTab("Direct Control", dcp);
		
		stp=new StatsPanel(defaults, op.scheme, dbc);
		tabPane.addTab("Stats", stp);
	
		this.add(tabPane);
		
	}
	
	public void writeLogToTextArea(LogEvent le, int counter){
		
		lo.writeLogToTextArea(le,  counter);
		
	}
	
	public boolean shutDown(){
		boolean close=true;
		
		int n = JOptionPane.showConfirmDialog(this,"Do you really want to stop and close down this experiment?","Confirm shut down", JOptionPane.YES_NO_OPTION);
		if (n==0){
			close=true;
			//if (started){
				//scheme.stopRecording();
			//}
			//op.disconnect();
		}
		else{
			close=false;
		}
		
		return close;
	}

	
	public void update(){
		
		dcp=new DirectControlPanel(op.getScheme());
		stp=new StatsPanel(defaults, op.getScheme(), dbc);
		tabPane.remove(3);
		tabPane.insertTab("Direct Control", null, dcp, null, 3);
		tabPane.remove(4);
		tabPane.addTab("Stats", stp);
		
		
	}
		

}
