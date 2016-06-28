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



import devices.DigitalOutput;

public class DigitalOutputOptionPanel extends JPanel{

	DigitalOutput diout;
	String[] types={"Toggle", "Pulse On", "Pulse Off", "On", "Off", "Flash"};
	
	JComboBox<String> standardType=new JComboBox<String>(types);
	JComboBox<String> alternativeType=new JComboBox<String>(types);
	JSpinner standardLengthField, altLengthField, standardFlashRepsField, altFlashRepsField;
	JFormattedTextField standardOffField, altOffField;
	
	Font font=new Font("Sans-Serif", Font.PLAIN, 10);
	
	public DigitalOutputOptionPanel(DigitalOutput diout){
		this.diout=diout;
		this.setFont(font);
		this.setBorder(new TitledBorder(diout.getName()));
		NumberFormat num=NumberFormat.getNumberInstance();
		num.setMaximumFractionDigits(0);
		
		this.setLayout(new GridLayout(0,2));
		
		SpinnerModel modelOneSec = new SpinnerNumberModel(1000,0,10000,1000);
		SpinnerModel modelOne = new SpinnerNumberModel(1,0,10,1);
		SpinnerModel modelOneSec1 = new SpinnerNumberModel(1000,0,10000,1000);
		SpinnerModel modelOne1 = new SpinnerNumberModel(1,0,10,1);
		
		JLabel stlab=new JLabel("Standard response type: ");
		stlab.setFont(font);
		this.add(stlab);
		standardType.setSelectedIndex(diout.standardType);
		this.add(standardType);
		
		JLabel atlab=new JLabel("Alternative response type: ");
		this.add(atlab);
		alternativeType.setSelectedIndex(diout.alternativeType);
		this.add(alternativeType);
		
		
		JLabel sllab=new JLabel("Standard pulse length (ms): ");
		this.add(sllab);
		standardLengthField=new JSpinner(modelOneSec);
		standardLengthField.setValue(new Integer(diout.standardLength));
		setJSpinnerButtonSize(standardLengthField);
		this.add(standardLengthField);
		
		JLabel allab=new JLabel("Alternative pulse length (ms): ");
		this.add(allab);
		altLengthField=new JSpinner(modelOneSec1);
		JComponent editorAlt = altLengthField.getEditor();
		JFormattedTextField tf2 = ((JSpinner.DefaultEditor) editorAlt).getTextField();
		tf2.setColumns(3);
		altLengthField.setValue(new Integer(diout.altLength));
		setJSpinnerButtonSize(altLengthField);
		this.add(altLengthField);
		
		JLabel solab=new JLabel("Standard off length (ms): ");
		this.add(solab);
		standardOffField=new JFormattedTextField(num);
		standardOffField.setColumns(10);
		standardOffField.setValue(new Integer(diout.standardOff));
		this.add(standardOffField);
		
		JLabel aolab=new JLabel("Alternative off length (ms): ");
		this.add(aolab);
		altOffField=new JFormattedTextField(num);
		altOffField.setColumns(10);
		altOffField.setValue(new Integer(diout.altOff));
		this.add(altOffField);
		
		JLabel srlab=new JLabel("Standard number flash reps: ");
		this.add(srlab);
		standardFlashRepsField=new JSpinner(modelOne);
		JComponent editorFlash = standardFlashRepsField.getEditor();
		JFormattedTextField tf3 = ((JSpinner.DefaultEditor) editorFlash).getTextField();
		tf3.setColumns(3);
		standardFlashRepsField.setValue(new Integer(diout.standardFlashReps));
		setJSpinnerButtonSize(standardFlashRepsField);
		this.add(standardFlashRepsField);
		
		JLabel arlab=new JLabel("Alternative number flash reps: ");
		this.add(arlab);
		altFlashRepsField=new JSpinner(modelOne1);
		JComponent editorFlashAlt = altFlashRepsField.getEditor();
		JFormattedTextField tf4 = ((JSpinner.DefaultEditor) editorFlashAlt).getTextField();
		tf4.setColumns(3);
		altFlashRepsField.setValue(new Integer(diout.altFlashReps));
		setJSpinnerButtonSize(altFlashRepsField);
		this.add(altFlashRepsField);
		
		changeFont(font, this);
	}
	
	public static void changeFont (Font font, JPanel pane){
	
	    for (Component child : pane.getComponents()){
	            child.setFont(font);
	    }
	}
	
	public void updateDigitalOutput(){
		int st=standardType.getSelectedIndex();
		if (st>=0){diout.standardType=st;}
		int at=alternativeType.getSelectedIndex();
		if (at>=0){
			diout.alternativeType=at;
		}
		
		int p1=(int)((Number)standardLengthField.getValue()).intValue();
		diout.standardLength=p1;
		
		int p2=(int)((Number)altLengthField.getValue()).intValue();
		diout.altLength=p2;
		
		int p3=(int)((Number)standardOffField.getValue()).intValue();
		diout.standardOff=p3;
		
		int p4=(int)((Number)altOffField.getValue()).intValue();
		diout.altOff=p4;
		
		int p5=(int)((Number)standardFlashRepsField.getValue()).intValue();
		diout.standardFlashReps=p5;
		
		int p6=(int)((Number)altFlashRepsField.getValue()).intValue();
		diout.altFlashReps=p6;
	
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
