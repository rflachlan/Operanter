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

import devices.MotorPWMOutput;
import devices.PAUSELinker;

public class MotorPWMOptionPanel extends JPanel{

	MotorPWMOutput diout;
	
	JSpinner openField;
//	JFormattedTextField openField;
	
	Font font=new Font("Sans-Serif", Font.PLAIN, 10);
	
	public MotorPWMOptionPanel(MotorPWMOutput diout){
		this.diout=diout;
		NumberFormat num=NumberFormat.getNumberInstance();
		num.setMaximumFractionDigits(0);
		
		this.setLayout(new GridLayout(0,2));
		this.setFont(font);
		this.setBorder(new TitledBorder(diout.getName()));
		
		JLabel sllab=new JLabel("Open length (ms): ");
		this.add(sllab);
/*		openField=new JFormattedTextField(num);
		openField.setColumns(10);
		openField.setValue(new Integer(diout.pauseLength));
		this.add(openField);
		changeFont(font, this);*/
		
		SpinnerModel model = new SpinnerNumberModel(10000,0,60000,1000);
		openField=new JSpinner(model);
		JComponent editor = openField.getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		tf.setColumns(3);
		openField.setValue(new Integer(diout.pauseLength));
		setJSpinnerButtonSize(openField);
		this.add(openField);
		changeFont(font, this);
		

	}
	
	public static void changeFont (Font font, JPanel pane){
		
	    for (Component child : pane.getComponents()){
	            child.setFont(font);
	    }
	}
	
	public void updateMotorPWM(){

		int p1=(int)((Number)openField.getValue()).intValue();
		diout.pauseLength=p1;
		System.out.println("new pauseLength is: " + diout.pauseLength);
	//	diout.setLength(p1);
	
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

