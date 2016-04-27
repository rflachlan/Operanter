package op;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;

import devices.DigitalSwitch;

public class DigitalSwitchOptionPanel extends JPanel{

	DigitalSwitch diout;
	String[] types={"Toggle", "Pulse On", "Pulse Off", "On", "Off", "Flash"};
	
	JComboBox<String> standardType=new JComboBox<String>(types);
	JSpinner standardLengthField;
	
	Font font=new Font("Sans-Serif", Font.PLAIN, 10);
	
	public DigitalSwitchOptionPanel(DigitalSwitch diout){
		this.diout=diout;
		NumberFormat num=NumberFormat.getNumberInstance();
		num.setMaximumFractionDigits(0);
		
		this.setLayout(new GridLayout(0,2));
		this.setFont(font);
		this.setBorder(new TitledBorder(diout.getName()));
		
		JLabel stlab=new JLabel("Standard response type: ");
		this.add(stlab);
		standardType.setSelectedIndex(diout.standardType);
		this.add(standardType);
		
		JLabel sllab=new JLabel("Standard pulse length (ms): ");
		this.add(sllab);
		/*standardLengthField=new JFormattedTextField(num);
		standardLengthField.setColumns(10);
		standardLengthField.setValue(new Integer(diout.standardLength));
		this.add(standardLengthField);*/
		SpinnerModel model = new SpinnerNumberModel(1000,0,10000,1000);
		standardLengthField=new JSpinner(model);
		JComponent editor = standardLengthField.getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		tf.setColumns(3);
		standardLengthField.setValue(new Integer(diout.standardLength));
		setJSpinnerButtonSize(standardLengthField);
		this.add(standardLengthField);
		changeFont(font, this);
	}
	
	public static void changeFont (Font font, JPanel pane){
		
	    for (Component child : pane.getComponents()){
	            child.setFont(font);
	    }
	}
	
	public void updateDigitalSwitch(){
		int st=standardType.getSelectedIndex();
		if (st>=0){diout.standardType=st;}
		
		int p1=(int)((Number)standardLengthField.getValue()).intValue();
		diout.standardLength=p1;
	
	}
	
	public void setJSpinnerButtonSize(JSpinner spinner){
		Dimension d = spinner.getPreferredSize();
		d.width = 80;
		spinner.setPreferredSize(d);
		
		spinner.setUI(new BasicSpinnerUI(){
			protected Component createPreviousButton(){
				Component b=super.createPreviousButton();
				JPanel wrap=new JPanel(new BorderLayout());
				wrap.add(b);
				wrap.setPreferredSize(new Dimension(20, 50));
				return wrap;
			}
		});
	
	}
	
	
	
	
}
